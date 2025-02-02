package no.hvl.dat153.quizapp.usecase

import no.hvl.dat153.quizapp.models.GalleryEntry
import no.hvl.dat153.quizapp.models.QuizEntry
import no.hvl.dat153.quizapp.repositories.GalleryEntryRepository

class GetRandomQuizEntryUseCase(
    private val quizRepository: GalleryEntryRepository = GalleryEntryRepository
) {

    operator fun invoke(): QuizEntry? {
        val quizItem = quizRepository.entries.randomOrNull() ?: return null
        val incorrectOptions = quizRepository.entries.map { it.name }.shuffled().take(NUMBER_OF_INCORRECT_OPTIONS)
        val options = incorrectOptions + quizItem.name
        return QuizEntry(quizItem, options.shuffled())
    }

    companion object {

        private const val NUMBER_OF_INCORRECT_OPTIONS = 2
    }
}
