package com.jbs.formulawithfun

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class MarkedFormulasActivity : AppCompatActivity() {

    companion object {
        private const val REQ_SPEECH_COMMAND = 5001
        private const val REQ_RECORD_AUDIO = 5002

        // Two utterance ids: one for spoken formula, one for the prompt
        private const val UTTERANCE_ID_FORMULA = "MARKED_FORMULA_TTS"
        private const val UTTERANCE_ID_PROMPT = "MARKED_PROMPT_TTS"
    }

    private lateinit var adapter: FormulaAdapter
    private lateinit var tts: TextToSpeech

    private lateinit var rvMarkedFormulas: RecyclerView
    private lateinit var tvStatus: TextView
    private lateinit var btnRepeat: Button
    private lateinit var btnNext: Button
    private lateinit var btnExit: Button
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var btnBackToOptions: Button

    private val formulasByStandard = QuizData.formulasByStandard

    private var markedList = mutableListOf<Formula>()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("formula_fun_prefs", Context.MODE_PRIVATE)
        if (!prefs.contains("user_standard")) {
            startActivity(Intent(this, StandardSelectionActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_marked_formulas)

        // View bindings
        rvMarkedFormulas = findViewById(R.id.rvMarkedFormulas)
        tvStatus = findViewById(R.id.tvStatus)
        btnRepeat = findViewById(R.id.btnRepeat)
        btnNext = findViewById(R.id.btnNext)
        btnExit = findViewById(R.id.btnExit)
        layoutEmpty = findViewById(R.id.layoutEmpty)
        btnBackToOptions = findViewById(R.id.btnBackToOptions)

        // Prepare marked list from preferences
        val standard = prefs.getInt("user_standard", 8)
        val favoriteTitles = prefs.getStringSet("favorite_formulas", mutableSetOf()) ?: mutableSetOf()
        val baseList = (formulasByStandard[standard] ?: emptyList()).map {
            it.copy(isFavorite = favoriteTitles.contains(it.title))
        }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })

        markedList = baseList.filter { it.isFavorite }.toMutableList()

        if (markedList.isEmpty()) {
            showEmptyState()
            btnBackToOptions.setOnClickListener { exitToOptions() }
            return
        } else {
            hideEmptyState()
        }

        // Setup RecyclerView and adapter
        adapter = FormulaAdapter(
            markedList,
            { formula, _ -> toggleFavoriteAndRefresh(formula) },
            { formula, position -> onItemSpeakClicked(formula, position) }
        )

        rvMarkedFormulas.layoutManager = LinearLayoutManager(this)
        rvMarkedFormulas.adapter = adapter

        // TTS init with distinct handling for formula vs prompt
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.ENGLISH
                tts.setSpeechRate(0.85f)
                tts.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        runOnUiThread { tvStatus.text = "Speaking..." }
                    }

                    override fun onDone(utteranceId: String?) {
                        runOnUiThread {
                            when (utteranceId) {
                                UTTERANCE_ID_FORMULA -> {
                                    // After a formula is spoken, speak the prompt words out loud
                                    tvStatus.text = "Speaking prompt: Repeat, Next or Exit..."
                                    if (::tts.isInitialized) {
                                        tts.speak("Repeat. Next. Or Exit.", TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID_PROMPT)
                                    }
                                }
                                UTTERANCE_ID_PROMPT -> {
                                    // After the prompt finished speaking, open mic to capture command
                                    tvStatus.text = "Listening for command..."
                                    startSpeechToTextForCommand()
                                }
                                else -> {
                                    tvStatus.text = "Listening for command..."
                                    startSpeechToTextForCommand()
                                }
                            }
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        runOnUiThread { tvStatus.text = "TTS error" }
                    }
                })
            } else {
                tvStatus.text = "TTS initialization failed"
            }
        }

        // Buttons
        btnRepeat.setOnClickListener { repeatCurrent() }
        btnNext.setOnClickListener { goNext() }
        btnExit.setOnClickListener { exitToOptions() }
        btnBackToOptions.setOnClickListener { exitToOptions() }

        // Start speaking first marked formula
        currentIndex = 0
        speakFormulaAtIndex(currentIndex)
    }

    private fun showEmptyState() {
        layoutEmpty.visibility = android.view.View.VISIBLE
        rvMarkedFormulas.visibility = android.view.View.GONE
        tvStatus.text = ""
    }

    private fun hideEmptyState() {
        layoutEmpty.visibility = android.view.View.GONE
        rvMarkedFormulas.visibility = android.view.View.VISIBLE
    }

    private fun toggleFavoriteAndRefresh(formula: Formula) {
        val prefs = getSharedPreferences("formula_fun_prefs", Context.MODE_PRIVATE)
        val favSet = prefs.getStringSet("favorite_formulas", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        if (favSet.contains(formula.title)) {
            favSet.remove(formula.title)
        } else {
            favSet.add(formula.title)
        }
        prefs.edit().putStringSet("favorite_formulas", favSet).apply()

        // Rebuild marked list (alphabetical)
        val standard = prefs.getInt("user_standard", 8)
        val baseList = (formulasByStandard[standard] ?: emptyList()).map {
            it.copy(isFavorite = favSet.contains(it.title))
        }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })

        markedList = baseList.filter { it.isFavorite }.toMutableList()
        adapter.updateList(markedList)

        if (markedList.isEmpty()) {
            // If user removed last favorite, show empty screen
            showEmptyState()
        } else {
            // adjust currentIndex if out of bounds
            if (currentIndex >= markedList.size) currentIndex = markedList.size - 1
            // clear speaking highlight until next speak
            adapter.setSpeakingIndex(null)
        }
    }

    // When user taps the speaker icon on an item (if adapter supports it)
    private fun onItemSpeakClicked(formula: Formula, position: Int) {
        // stop any playing, speak the selected item
        currentIndex = position
        speakFormulaAtIndex(currentIndex)
    }

    private fun speakFormulaAtIndex(index: Int) {
        if (markedList.isEmpty()) return
        if (index < 0 || index >= markedList.size) return

        currentIndex = index
        val formula = markedList[currentIndex]

        // highlight in adapter and scroll
        adapter.setSpeakingIndex(currentIndex)
        rvMarkedFormulas.scrollToPosition(currentIndex)

        // stop any previous TTS and speak formula (will trigger UTTERANCE_ID_FORMULA)
        if (::tts.isInitialized) tts.stop()
        val text = "${formula.title}. ${formula.explanation}"
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID_FORMULA)
    }

    private fun repeatCurrent() {
        if (markedList.isEmpty()) {
            Toast.makeText(this, "No formulas to repeat", Toast.LENGTH_SHORT).show()
            return
        }
        speakFormulaAtIndex(currentIndex)
    }

    private fun goNext() {
        if (markedList.isEmpty()) {
            Toast.makeText(this, "No formulas", Toast.LENGTH_SHORT).show()
            return
        }
        if (currentIndex < markedList.size - 1) {
            currentIndex++
            speakFormulaAtIndex(currentIndex)
        } else {
            // End reached - speak prompt to repeat or exit
            if (::tts.isInitialized) {
                tts.stop()
                tts.speak("End of marked formulas. Repeat or Exit.", TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID_PROMPT)
            }
        }
    }

    private fun exitToOptions() {
        if (::tts.isInitialized) tts.stop()
        // If OptionsActivity is already in back stack, just finish to go back
        if (!isTaskRoot) {
            finish()
        } else {
            val intent = Intent(this, OptionsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    // Start listening only for a short command (repeat/next/exit)
    private fun startSpeechToTextForCommand() {
        if (!hasRecordAudioPermission()) {
            requestRecordAudioPermission()
            return
        }
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Say: Repeat, Next or Exit")
            }
            tvStatus.text = "Listening for command..."
            startActivityForResult(intent, REQ_SPEECH_COMMAND)
        } catch (e: Exception) {
            tvStatus.text = "Speech recognition not supported"
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_SPEECH_COMMAND) {
            if (resultCode != Activity.RESULT_OK || data == null) {
                tvStatus.text = "No command recognized. Use buttons or try again."
                return
            }
            val matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spoken = if (!matches.isNullOrEmpty()) matches[0] else ""
            if (spoken.isNotBlank()) {
                handleCommandSpeech(spoken)
            } else {
                tvStatus.text = "Could not understand. Use buttons or try again."
            }
        }
    }

    private fun handleCommandSpeech(command: String) {
        val lower = command.lowercase(Locale.getDefault())
        tvStatus.text = "Heard: $command"

        when {
            lower.contains("repeat") || lower.contains("again") || lower.contains("replay") -> {
                repeatCurrent()
            }
            lower.contains("next") || lower.contains("another") || lower.contains("new") -> {
                goNext()
            }
            lower.contains("exit") || lower.contains("home") || lower.contains("quit") || lower.contains("back") -> {
                exitToOptions()
            }
            else -> {
                // ask again once by speaking prompt again
                tvStatus.text = "Command not recognized. Say Repeat, Next or Exit (or tap a button)."
                if (::tts.isInitialized) {
                    tts.speak("Sorry, please say Repeat, Next or Exit.", TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID_PROMPT)
                }
            }
        }
    }

    private fun hasRecordAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQ_RECORD_AUDIO)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_RECORD_AUDIO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted. Tap Next/Repeat or wait after speaking for command.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Microphone permission is required for voice commands.", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
    }
}
