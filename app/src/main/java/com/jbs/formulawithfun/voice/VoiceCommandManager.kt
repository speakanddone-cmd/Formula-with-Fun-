package com.jbs.formulawithfun.voice

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.result.ActivityResultLauncher
import java.util.*

class VoiceCommandManager(
    private val activity: Activity,
    val speechLauncher: ActivityResultLauncher<Intent>,
    private var tts: TextToSpeech? = null,
    private var language: Locale = Locale.getDefault()
) {

    private var lastOnDone: (() -> Unit)? = null

    init {
        tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) { lastOnDone?.invoke() }
            override fun onError(utteranceId: String?) { lastOnDone?.invoke() }
        })
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        lastOnDone = onDone
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ttsID")
    }

    fun listen(prompt: String? = null) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
        if (prompt != null) intent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
        speechLauncher.launch(intent)
    }

    fun setLanguage(locale: Locale) {
        language = locale
        tts?.language = locale
    }

    fun setTts(tts: TextToSpeech) {
        this.tts = tts
        tts.language = language
        tts.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) { lastOnDone?.invoke() }
            override fun onError(utteranceId: String?) { lastOnDone?.invoke() }
        })
    }

    // --- New: Pause, Resume, Stop ---

    fun pauseTts() {
        // No direct pause in Android TTS; stop is closest option
        tts?.stop()
    }

    fun resumeTts(text: String, onDone: (() -> Unit)? = null) {
        // Resumes by re-speaking the last text (pass text to this method from MainActivity)
        speak(text, onDone)
    }

    fun stopAll() {
        tts?.stop()
        // No direct "stop" for speechRecognizer - it only listens once per launch.
        // If you add custom SpeechRecognizer logic, handle stop there.
    }
}
