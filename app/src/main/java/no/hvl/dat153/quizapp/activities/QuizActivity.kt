package no.hvl.dat153.quizapp.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.models.GalleryEntry
import no.hvl.dat153.quizapp.models.QuizEntry
import no.hvl.dat153.quizapp.usecase.GetRandomQuizEntryUseCase

class QuizActivity(
    private val getQuizEntryUseCase: GetRandomQuizEntryUseCase = GetRandomQuizEntryUseCase()
) : AppCompatActivity() {

    private lateinit var scoreCorrect: TextView
    private lateinit var scoreIncorrect: TextView
    private lateinit var imageView: ImageView
    private lateinit var returnButton: ImageButton
    private lateinit var optionsButtons: List<Button>
    private lateinit var continueButton: Button

    private val scoreCorrectStream = MutableStateFlow(0)
    private val scoreIncorrectStream = MutableStateFlow(0)
    private val currentQuizEntryStream = MutableStateFlow<QuizEntry?>(null)
    private val selectedNameStream = MutableStateFlow<GalleryEntry.Name?>(null)
    private val screenStateStream = combine(
        scoreCorrectStream,
        scoreIncorrectStream,
        currentQuizEntryStream,
        selectedNameStream,
        ScreenState::of
    ).stateIn(lifecycleScope, SharingStarted.Lazily, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        scoreCorrect = findViewById(R.id.scoreCorrect)
        scoreIncorrect = findViewById(R.id.scoreIncorrect)
        imageView = findViewById(R.id.imageView)
        returnButton = findViewById(R.id.btnReturn)
        optionsButtons = listOf(
            findViewById(R.id.btnOption1),
            findViewById(R.id.btnOption2),
            findViewById(R.id.btnOption3)
        )
        continueButton = findViewById(R.id.btnContinue)

        setupOnClickListeners()
        observeScreenState()

        generateNewQuizEntry()
    }

    private fun setupOnClickListeners() {
        returnButton.setOnClickListener { finish() }
        optionsButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                screenStateStream.value?.currentQuizEntry?.options?.getOrNull(index)?.let { selectedName ->
                    onSelectedName(selectedName)
                }
            }
        }
        continueButton.setOnClickListener { onContinueClick() }
    }

    private fun observeScreenState() {
        lifecycleScope.launch {
            screenStateStream
                .filterNotNull()
                .collect(::applyState)
        }
    }

    private fun applyState(screenState: ScreenState) {
        scoreCorrect.text = getString(R.string.correct, screenState.scoreCorrect.toString())
        scoreIncorrect.text = getString(R.string.incorrect, screenState.scoreIncorrect.toString())
        imageView.setImageBitmap(screenState.currentQuizEntry.quizItem.image)
        optionsButtons.forEachIndexed { index, button ->
            val option = screenState.currentQuizEntry.options.getOrNull(index)
            option?.let { button.text = option.value }
        }
        screenState.selectedName?.let { selectedName ->
            val correctName = screenState.currentQuizEntry.quizItem.name
            val correctButton = optionsButtons.find { it.text == correctName.value }

            correctButton?.setBackgroundColor(getColor(R.color.green))

            if (!screenState.isCorrect) {
                val selectedButton = optionsButtons.find { it.text == selectedName.value }
                selectedButton?.setBackgroundColor(getColor(R.color.red))
            }
        } ?: run {
            optionsButtons.forEach { button -> button.setBackgroundColor(getColor(R.color.dark_gray)) }
        }
        continueButton.visibility = if (screenState.isAnswered) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    private fun onSelectedName(selectedName: GalleryEntry.Name) {
        if (screenStateStream.value?.isAnswered == true) {
            return
        }

        selectedNameStream.value = selectedName
        increaseScore()
    }

    private fun increaseScore() {
        when (screenStateStream.value?.isCorrect) {
            true -> increaseCorrect()
            false -> increaseIncorrect()
            null -> Unit
        }
    }

    private fun increaseCorrect() {
        scoreCorrectStream.update { it + 1 }
    }

    private fun increaseIncorrect() {
        scoreIncorrectStream.update { it + 1 }
    }

    private fun onContinueClick() {
        generateNewQuizEntry()
    }

    private fun generateNewQuizEntry() {
        getQuizEntryUseCase()?.let(::onNewQuizEntry)
    }

    private fun onNewQuizEntry(quizEntry: QuizEntry) {
        currentQuizEntryStream.value = quizEntry
        selectedNameStream.value = null
    }

    private data class ScreenState(
        val scoreCorrect: Int,
        val scoreIncorrect: Int,
        val currentQuizEntry: QuizEntry,
        val selectedName: GalleryEntry.Name?
    ) {

        val isAnswered = selectedName != null
        val isCorrect = selectedName == currentQuizEntry.quizItem.name

        companion object {

            fun of(
                scoreCorrect: Int,
                scoreIncorrect: Int,
                currentQuizEntry: QuizEntry?,
                selectedName: GalleryEntry.Name?
            ): ScreenState? {
                return currentQuizEntry?.let { quizEntry ->
                    ScreenState(
                        scoreCorrect,
                        scoreIncorrect,
                        quizEntry,
                        selectedName
                    )
                }
            }
        }
    }
}
