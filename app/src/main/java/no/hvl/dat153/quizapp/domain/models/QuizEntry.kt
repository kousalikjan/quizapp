package no.hvl.dat153.quizapp.domain.models

data class QuizEntry(
    val quizItem: GalleryEntry,
    val options: List<String>,
)
