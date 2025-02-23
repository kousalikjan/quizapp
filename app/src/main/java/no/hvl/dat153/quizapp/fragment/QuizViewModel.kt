package no.hvl.dat153.quizapp.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.hvl.dat153.quizapp.models.QuizEntry
import no.hvl.dat153.quizapp.usecase.GetRandomQuizEntryUseCase

class QuizViewModel(
    private val getQuizEntryUseCase: GetRandomQuizEntryUseCase = GetRandomQuizEntryUseCase()
) : ViewModel() {

    private val scoreCorrectStream = MutableStateFlow(0)
    private val scoreIncorrectStream = MutableStateFlow(0)
    private val currentQuizEntryStream = MutableStateFlow<QuizEntry?>(null)
    private val selectedNameStream = MutableStateFlow<String?>(null)
    val screenStateStream = combine(
        scoreCorrectStream,
        scoreIncorrectStream,
        currentQuizEntryStream,
        selectedNameStream,
        ScreenState::of
    ).stateIn(viewModelScope, SharingStarted.Lazily, null)

    init {
        viewModelScope.launch {
            generateNewQuizEntry()
        }
    }

    fun onButtonClick(index: Int) {
        screenStateStream.value?.currentQuizEntry?.options?.getOrNull(index)?.let { selectedName ->
            onSelectedName(selectedName)
        }
    }

    private fun onSelectedName(selectedName: String) {
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

    fun onContinueClick() {
        generateNewQuizEntry()
    }

    private fun generateNewQuizEntry() {
        viewModelScope.launch {
            getQuizEntryUseCase()?.let(::onNewQuizEntry)
        }
    }

    private fun onNewQuizEntry(quizEntry: QuizEntry) {
        currentQuizEntryStream.value = quizEntry
        selectedNameStream.value = null
    }

    data class ScreenState(
        val scoreCorrect: Int,
        val scoreIncorrect: Int,
        val currentQuizEntry: QuizEntry,
        val selectedName: String?
    ) {

        val isAnswered = selectedName != null
        val isCorrect = selectedName == currentQuizEntry.quizItem.name

        companion object {

            fun of(
                scoreCorrect: Int,
                scoreIncorrect: Int,
                currentQuizEntry: QuizEntry?,
                selectedName: String?
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
