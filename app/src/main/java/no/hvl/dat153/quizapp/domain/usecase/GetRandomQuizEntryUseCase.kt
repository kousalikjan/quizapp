package no.hvl.dat153.quizapp.domain.usecase

import no.hvl.dat153.quizapp.domain.models.QuizEntry
import no.hvl.dat153.quizapp.domain.repositories.GalleryEntryRepository

class GetRandomQuizEntryUseCase(
    private val quizRepository: GalleryEntryRepository = GalleryEntryRepository
) {

    suspend operator fun invoke(): QuizEntry? {
        val entries = quizRepository.getAll()
        val quizItem = entries.randomOrNull() ?: return null
        val correctOption = quizItem.name
        val incorrectOptions = entries
            .map { it.name }
            .filter { it != correctOption }
            .shuffled()
            .take(NUMBER_OF_INCORRECT_OPTIONS)
        val options = incorrectOptions + correctOption
        return QuizEntry(quizItem, options.shuffled())
    }

    companion object {

        private const val NUMBER_OF_INCORRECT_OPTIONS = 2
    }
}
