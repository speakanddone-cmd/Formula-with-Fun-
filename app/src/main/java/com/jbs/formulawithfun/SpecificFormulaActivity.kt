package com.jbs.formulawithfun

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class SpecificFormulaActivity : AppCompatActivity() {

    companion object {
        private const val REQ_SPEECH_FORMULA = 4001
        private const val REQ_SPEECH_COMMAND = 4002
        private const val REQ_RECORD_AUDIO = 4003

        // Two distinct utterance IDs:
        private const val UTTERANCE_ID_FORMULA = "SPEC_FORMULA_TTS"
        private const val UTTERANCE_ID_PROMPT = "SPEC_PROMPT_TTS"
    }

    private lateinit var etSpecificSearch: EditText
    private lateinit var btnSpecificMic: ImageButton
    private lateinit var btnSearchManual: Button
    private lateinit var tvStatus: TextView
    private lateinit var tvFormulaTitle: TextView
    private lateinit var tvFormulaExplanation: TextView
    private lateinit var btnRepeat: Button
    private lateinit var btnNext: Button
    private lateinit var btnExit: Button

    private lateinit var tts: TextToSpeech
    private var currentFormula: Formula? = null

    private lateinit var masterList: List<Formula>
    private val formulasByStandard = QuizData.formulasByStandard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specific_formula)

        // View bindings
        etSpecificSearch = findViewById(R.id.etSpecificSearch)
        btnSpecificMic = findViewById(R.id.btnSpecificMic)
        btnSearchManual = findViewById(R.id.btnSearchManual)
        tvStatus = findViewById(R.id.tvStatus)
        tvFormulaTitle = findViewById(R.id.tvFormulaTitle)
        tvFormulaExplanation = findViewById(R.id.tvFormulaExplanation)
        btnRepeat = findViewById(R.id.btnRepeat)
        btnNext = findViewById(R.id.btnNext)
        btnExit = findViewById(R.id.btnExit)

        // Build the master list for selected standard (same logic as MainActivity)
        val prefs = getSharedPreferences("formula_fun_prefs", Context.MODE_PRIVATE)
        if (!prefs.contains("user_standard")) {
            startActivity(Intent(this, StandardSelectionActivity::class.java))
            finish()
            return
        }
        val standard = prefs.getInt("user_standard", 8)
        val favoriteTitles = prefs.getStringSet("favorite_formulas", mutableSetOf()) ?: mutableSetOf()
        masterList = (formulasByStandard[standard] ?: emptyList()).map {
            it.copy(isFavorite = favoriteTitles.contains(it.title))
        }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title })

        // Initialize TTS with distinct handling for formula vs prompt utterances
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.ENGLISH
                tts.setSpeechRate(0.8f)
                tts.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        runOnUiThread { tvStatus.text = "Speaking..." }
                    }

                    override fun onDone(utteranceId: String?) {
                        // Distinguish between formula spoken and prompt spoken
                        runOnUiThread {
                            when (utteranceId) {
                                UTTERANCE_ID_FORMULA -> {
                                    // After speaking the formula, speak the prompt words aloud,
                                    // then when prompt is done we will open the mic in the UTTERANCE_ID_PROMPT branch.
                                    tvStatus.text = "Speaking prompt: Repeat, Next or Exit..."
                                    if (::tts.isInitialized) {
                                        // speak prompt; do not start recognizer here
                                        tts.speak("Repeat. Next. Or Exit.", TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID_PROMPT)
                                    }
                                }

                                UTTERANCE_ID_PROMPT -> {
                                    // After the prompt has been spoken, start listening for command
                                    tvStatus.text = "Listening for command..."
                                    startSpeechToTextForCommand()
                                }

                                else -> {
                                    // Unknown utterance id — fallback to listening for command
                                    tvStatus.text = "Listening for command..."
                                    startSpeechToTextForCommand()
                                }
                            }
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        runOnUiThread {
                            tvStatus.text = "TTS error"
                        }
                    }
                })
            } else {
                tvStatus.text = "TTS initialization failed"
            }
        }

        // Manual search button
        btnSearchManual.setOnClickListener {
            val q = etSpecificSearch.text?.toString() ?: ""
            if (q.isNotBlank()) performSearchAndSpeak(q)
            else Toast.makeText(this, "Type a formula name or use the mic", Toast.LENGTH_SHORT).show()
        }

        // Mic button for listening for formula name
        btnSpecificMic.setOnClickListener {
            startSpeechToTextForFormula()
        }

        // Keyboard "search" action on EditText
        etSpecificSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val q = etSpecificSearch.text?.toString() ?: ""
                if (q.isNotBlank()) performSearchAndSpeak(q)
                true
            } else false
        }

        // Manual action buttons
        btnRepeat.setOnClickListener { repeatCurrentFormula() }
        btnNext.setOnClickListener { startSpeechToTextForFormula() } // listen for next formula
        btnExit.setOnClickListener { exitToOptions() }

        // initial UI
        tvStatus.text = "Enter or speak a formula name"
    }

    private fun performSearchAndSpeak(rawQuery: String) {
        val normalizedQuery = SearchUtils.normalizeFormulaString(rawQuery)
        if (normalizedQuery.isBlank()) {
            tvStatus.text = "Enter a formula name"
            return
        }

        // Prefix search first (title only)
        val prefixMatches = masterList.filter {
            SearchUtils.normalizeFormulaString(it.title).startsWith(normalizedQuery)
        }

        val finalMatch = when {
            prefixMatches.isNotEmpty() -> prefixMatches.first()
            else -> {
                // fallback to contains on title
                val containsMatches = masterList.filter {
                    SearchUtils.normalizeFormulaString(it.title).contains(normalizedQuery)
                }
                containsMatches.firstOrNull()
            }
        }

        if (finalMatch == null) {
            currentFormula = null
            tvFormulaTitle.text = ""
            tvFormulaExplanation.text = ""
            tvStatus.text = "No match found. Try again."
        } else {
            currentFormula = finalMatch
            tvFormulaTitle.text = finalMatch.title
            tvFormulaExplanation.text = finalMatch.explanation
            // speak the formula (this will trigger UTTERANCE_ID_FORMULA)
            speakCurrentFormula()
        }
    }

    private fun speakCurrentFormula() {
        val formula = currentFormula
        if (formula == null) {
            tvStatus.text = "No formula selected"
            return
        }
        // Stop any running TTS
        if (::tts.isInitialized) {
            tts.stop()
        }
        tvStatus.text = "Speaking..."
        val textToSpeak = "${formula.title}. ${formula.explanation}"
        tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID_FORMULA)
    }

    // Called when "Repeat" action is requested
    private fun repeatCurrentFormula() {
        if (currentFormula == null) {
            tvStatus.text = "Nothing to repeat. Speak or type a formula first."
            return
        }
        speakCurrentFormula()
    }

    // Start speech recognizer for formula name
    private fun startSpeechToTextForFormula() {
        if (!hasRecordAudioPermission()) {
            requestRecordAudioPermission()
            return
        }

        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak formula name")
            }
            tvStatus.text = "Listening for formula..."
            startActivityForResult(intent, REQ_SPEECH_FORMULA)
        } catch (e: Exception) {
            tvStatus.text = "Speech recognition not supported"
        }
    }

    // Start speech recognizer for command (repeat / next / exit)
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

    // Handle recognizer results
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK || data == null) {
            // User cancelled or no result
            if (requestCode == REQ_SPEECH_COMMAND) {
                tvStatus.text = "No command recognized. Try again or tap a button."
            } else if (requestCode == REQ_SPEECH_FORMULA) {
                tvStatus.text = "No speech result. Try again."
            }
            return
        }

        val matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        val spoken = if (!matches.isNullOrEmpty()) matches[0] else ""

        when (requestCode) {
            REQ_SPEECH_FORMULA -> {
                // Put recognized text into EditText and perform search
                if (spoken.isNotBlank()) {
                    etSpecificSearch.setText(spoken)
                    performSearchAndSpeak(spoken)
                } else {
                    tvStatus.text = "Could not understand. Try again."
                }
            }
            REQ_SPEECH_COMMAND -> {
                if (spoken.isNotBlank()) {
                    handleCommandSpeech(spoken)
                } else {
                    tvStatus.text = "Could not understand command. Try again."
                }
            }
        }
    }

    private fun handleCommandSpeech(command: String) {
        val lower = command.lowercase(Locale.getDefault())
        tvStatus.text = "Heard: $command"

        // Map common keywords
        when {
            lower.contains("repeat") || lower.contains("again") || lower.contains("replay") -> {
                repeatCurrentFormula()
            }
            lower.contains("next") || lower.contains("another") || lower.contains("new") || lower.contains("search") -> {
                // Listen for next formula name
                startSpeechToTextForFormula()
            }
            lower.contains("exit") || lower.contains("home") || lower.contains("quit") || lower.contains("back") -> {
                exitToOptions()
            }
            else -> {
                // not recognized
                tvStatus.text = "Command not recognized. Say Repeat, Next or Exit."
                // give one more attempt by speaking the prompt again:
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

    // handle permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_RECORD_AUDIO) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted. Tap mic to speak.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Microphone permission required for speech.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun exitToOptions() {
        // stop TTS and go to OptionsActivity
        if (::tts.isInitialized) {
            tts.stop()
        }
        val intent = Intent(this, OptionsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
    }
}
