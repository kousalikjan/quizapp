package no.hvl.dat153.quizapp.presentation.quiz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import no.hvl.dat153.quizapp.R

class QuizActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, QuizFragment())
                .commit()
        }
    }
}
