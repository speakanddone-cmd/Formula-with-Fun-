package com.jbs.formulawithfun

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: FormulaAdapter
    private lateinit var tts: TextToSpeech
    private lateinit var masterList: List<Formula>          // All formulas sorted alphabetically by title
    private lateinit var formulaListForStandard: List<Formula> // Raw list loaded for chosen standard (unsorted copy)
    private lateinit var prefs: android.content.SharedPreferences

    // Track which formula (position) is speaking
    private var ttsSpeakingIndex: Int? = null

    // ---- Standard-wise formulas for Maths ----
    private val formulasByStandard = QuizData.formulasByStandard

    companion object {
        private const val RECORD_AUDIO_PERMISSION_CODE = 1001
        private const val SPEECH_REQUEST_CODE = 2001
    }

    /**
     * Normalize user input and stored titles for forgiving matching:
     * - lowercase
     * - remove ALL whitespace
     * - convert common superscript characters into ^N form
     */
    private fun normalizeFormulaString(input: String): String {
        return input
            .lowercase(Locale.getDefault())
            .replace("\\s+".toRegex(), "")           // Remove ALL spaces
            .replace("²", "^2")
            .replace("³", "^3")
            .replace("⁴", "^4")
            .replace("⁵", "^5")
            .replace("⁶", "^6")
            .replace("⁷", "^7")
            .replace("⁸", "^8")
            .replace("⁹", "^9")
            .replace("⁰", "^0")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Ask for Mic permission (first-time)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO_PERMISSION_CODE
            )
        }

        // Show standard selection only if not already chosen
        // Show standard selection if not chosen yet
        prefs = getSharedPreferences("formula_fun_prefs", Context.MODE_PRIVATE)
        if (!prefs.contains("user_standard")) {
            startActivity(Intent(this, StandardSelectionActivity::class.java))
            finish()
            return
        } else {
            // If standard already chosen, normally we want launcher to go to OptionsActivity.
            // But do NOT redirect if this activity was explicitly launched by OptionsActivity itself.
            val skipRedirect = intent?.getBooleanExtra("skipRedirect", false) ?: false
            if (!skipRedirect) {
                // launched from launcher / fresh start: go to OptionsActivity
                startActivity(Intent(this, OptionsActivity::class.java))
                finish()
                return
            }
            // else: skipRedirect == true -> continue in MainActivity (user intentionally asked to open it)
        }



        setContentView(R.layout.activity_main)

        // ---- Load formulas for the selected standard ----
        val standard = prefs.getInt("user_standard", 8)
        val favoriteTitles = getFavoriteTitles()

        // Keep an unsorted copy (to re-build masterList when favorites toggle)
        formulaListForStandard = (formulasByStandard[standard] ?: emptyList()).map {
            it.copy(isFavorite = favoriteTitles.contains(it.title))
        }

        // Build masterList sorted alphabetically by title (case-insensitive)
        masterList = formulaListForStandard
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })

        // Ensure 'isFavorite' flag is set according to saved favorites
        val favSet = getFavoriteTitles()
        masterList = masterList.map { it.copy(isFavorite = favSet.contains(it.title)) }

        // ---- Setup RecyclerView ----
        val recyclerView = findViewById<RecyclerView>(R.id.rvSuggestions)
        adapter = FormulaAdapter(
            masterList, // start with full, sorted master list visible by default
            { formula, marked -> toggleFavorite(formula) },
            { formula, position -> onSpeakClicked(formula, position) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // ---- Setup TTS with progress listener for ON/OFF feedback ----
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.ENGLISH
                tts.setSpeechRate(0.7f)
                tts.setOnUtteranceProgressListener(object :
                    android.speech.tts.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        runOnUiThread {
                            ttsSpeakingIndex = null
                            adapter.setSpeakingIndex(null)
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        runOnUiThread {
                            ttsSpeakingIndex = null
                            adapter.setSpeakingIndex(null)
                        }
                    }
                })
            }
        }

        // ---- Search Logic (title-only prefix search, normalized) ----
        val etSearch = findViewById<EditText>(R.id.etFormulaSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val rawQuery = s?.toString() ?: ""
                val normalizedQuery = normalizeFormulaString(rawQuery)

                val results: List<Formula> = if (normalizedQuery.isEmpty()) {
                    // show full master list when query empty
                    masterList
                } else {
                    // *** CHANGED: use prefix match (startsWith) on normalized title ONLY ***
                    masterList.filter {
                        normalizeFormulaString(it.title).startsWith(normalizedQuery)
                    }
                }

                adapter.updateList(results)
            }
        })

        // ---- Show Favorites Button ----
        val btnFavorites = findViewById<Button>(R.id.btnFavorites)
        btnFavorites.setOnClickListener {
            // show only favorites, keep them alphabetical
            val favs = masterList.filter { it.isFavorite }
            adapter.updateList(favs)
        }

        val btnQuiz = findViewById<Button>(R.id.btnQuiz)
        btnQuiz.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        // ---- Voice Input (Google SpeechRecognizer, ONLINE mic) ----
        val tilFormulaSearch = findViewById<TextInputLayout>(R.id.tilFormulaSearch)
        tilFormulaSearch.setEndIconOnClickListener {
            startSpeechToText()
        }
    }

    // --- Google SpeechRecognizer: Launch speech input
    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak the formula name...")
        }
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition not supported.", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Handle speech result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results?.getOrNull(0) ?: ""
            val etSearch = findViewById<EditText>(R.id.etFormulaSearch)
            etSearch.setText(spokenText)
        }
    }

    // --- Favorites helpers ---
    private fun getFavoriteTitles(): MutableSet<String> =
        prefs.getStringSet("favorite_formulas", mutableSetOf()) ?: mutableSetOf()

    private fun saveFavoriteTitles(titles: Set<String>) {
        prefs.edit().putStringSet("favorite_formulas", titles).apply()
    }

    /**
     * Toggle favorite state in backing lists and refresh the UI.
     * Keeps alphabetical ordering and updates masterList's isFavorite flags.
     */
    private fun toggleFavorite(formula: Formula) {
        val favoriteTitles = getFavoriteTitles()
        val isAdding = !formula.isFavorite
        if (isAdding) {
            favoriteTitles.add(formula.title)
        } else {
            favoriteTitles.remove(formula.title)
        }
        saveFavoriteTitles(favoriteTitles)

        // Rebuild masterList from base standard list and apply favorite flags, keep alphabetical order
        masterList = formulaListForStandard
            .map { it.copy(isFavorite = favoriteTitles.contains(it.title)) }
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })

        // Respect current search query:
        val etSearch = findViewById<EditText>(R.id.etFormulaSearch)
        val normalizedQuery = normalizeFormulaString(etSearch.text.toString().trim())

        val currentList = if (normalizedQuery.isEmpty()) {
            masterList
        } else {
            // *** CHANGED: use prefix match (startsWith) on normalized title ONLY ***
            masterList.filter { normalizeFormulaString(it.title).startsWith(normalizedQuery) }
        }

        adapter.updateList(currentList)
    }

    // SPEAKER BUTTON HANDLER: instantly updates ON/OFF state and speaks!
    private fun onSpeakClicked(formula: Formula, position: Int) {
        if (ttsSpeakingIndex == position) {
            tts.stop()
            ttsSpeakingIndex = null
            adapter.setSpeakingIndex(null)
            return
        }
        if (ttsSpeakingIndex != null) {
            tts.stop()
        }
        adapter.setSpeakingIndex(position)
        ttsSpeakingIndex = position
        tts.speak("${formula.title}: ${formula.explanation}",
            TextToSpeech.QUEUE_FLUSH,
            null,
            "FORMULA_SPEAK"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
    }
}
