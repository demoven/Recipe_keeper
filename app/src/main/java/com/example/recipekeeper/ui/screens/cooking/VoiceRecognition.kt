package com.example.recipekeeper.ui.screens.cooking

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.runtime.MutableState

class VoiceRecognition(
    private val context: Context,
    private val isListeningRef: MutableState<Boolean>
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var speechRecognizerIntent: Intent? = null
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    private var lastSuivantDetectionTime = 0L
    private var lastRetourDetectionTime = 0L
    private val detectionCooldownMs = 1000L

    var onSuivant: (() -> Unit)? = null
    var onRetour: (() -> Unit)? = null
    var onIngredient: (() -> Unit)? = null
    var onFermer: (() -> Unit)? = null
    var shouldShowIngredients: (() -> Boolean)? = null

    fun initialize() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        }

        val recognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                if (isListeningRef.value) {
                    val delay = if (error == SpeechRecognizer.ERROR_NO_MATCH) 50L else 200L
                    handler.postDelayed({
                        speechRecognizer?.startListening(speechRecognizerIntent)
                    }, delay)
                }
            }

            override fun onResults(results: Bundle?) {
                val data = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (data != null && data.isNotEmpty()) {
                    data.forEach { result -> checkAndActivateKeywords(result) }
                }
                if (isListeningRef.value) {
                    handler.postDelayed({
                        speechRecognizer?.startListening(speechRecognizerIntent)
                    }, 50)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val data = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (data != null && data.isNotEmpty()) {
                    data.forEach { partialText ->
                        if (partialText.isNotEmpty()) {
                            checkAndActivateKeywords(partialText)
                        }
                    }
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        speechRecognizer?.setRecognitionListener(recognitionListener)
    }

    fun startListening() {
        speechRecognizer?.startListening(speechRecognizerIntent)
        isListeningRef.value = true
    }

    fun destroy() {
        isListeningRef.value = false
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    private fun checkAndActivateKeywords(textToCheck: String) {
        if (textToCheck.isEmpty()) return

        val lowerText = textToCheck.lowercase().trim()
        val currentTime = System.currentTimeMillis()

        val suivantDetected = lowerText.contains("suivant")
        val terminerDetected = lowerText.contains("terminer") || lowerText.contains("terminé")

        if (suivantDetected || terminerDetected) {
            val timeSinceLastDetection = currentTime - lastSuivantDetectionTime
            if (timeSinceLastDetection >= detectionCooldownMs) {
                lastSuivantDetectionTime = currentTime
                onSuivant?.invoke()
            }
        }

        val retourDetected = lowerText.contains("retour")
        if (retourDetected) {
            val timeSinceLastDetection = currentTime - lastRetourDetectionTime
            if (timeSinceLastDetection >= detectionCooldownMs) {
                lastRetourDetectionTime = currentTime
                onRetour?.invoke()
            }
        }

        val ingredientDetected = lowerText.contains("ingrédient") || lowerText.contains("ingredient")
        if (ingredientDetected && shouldShowIngredients?.invoke() == false) {
            onIngredient?.invoke()
        }

        val fermerDetected = lowerText.contains("fermer") || lowerText.contains("fermé")
        if (fermerDetected && shouldShowIngredients?.invoke() == true) {
            onFermer?.invoke()
        }
    }
}

