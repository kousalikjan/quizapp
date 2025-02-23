package no.hvl.dat153.quizapp.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import no.hvl.dat153.quizapp.R

class QuizFragment : Fragment(R.layout.fragment_quiz) {

    private lateinit var scoreCorrect: TextView
    private lateinit var scoreIncorrect: TextView
    private lateinit var imageView: ImageView
    private lateinit var returnButton: ImageButton
    private lateinit var optionsButtons: List<Button>
    private lateinit var continueButton: Button

    private lateinit var viewModel: QuizViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[QuizViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scoreCorrect = view.findViewById(R.id.scoreCorrect)
        scoreIncorrect = view.findViewById(R.id.scoreIncorrect)
        imageView = view.findViewById(R.id.imageView)
        returnButton = view.findViewById(R.id.btnReturn)
        optionsButtons = listOf(
            view.findViewById(R.id.btnOption1),
            view.findViewById(R.id.btnOption2),
            view.findViewById(R.id.btnOption3)
        )
        continueButton = view.findViewById(R.id.btnContinue)

        setupOnClickListeners()
        observeScreenState()
    }

    private fun setupOnClickListeners() {
        returnButton.setOnClickListener { activity?.finish() }
        optionsButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                viewModel.onButtonClick(index)
            }
        }
        continueButton.setOnClickListener { viewModel.onContinueClick() }
    }

    private fun observeScreenState() {
        lifecycleScope.launch {
            viewModel.screenStateStream
                .filterNotNull()
                .collect(::applyState)
        }
    }

    private fun applyState(screenState: QuizViewModel.ScreenState) {
        val activity = activity ?: return

        scoreCorrect.text = getString(R.string.correct, screenState.scoreCorrect.toString())
        scoreIncorrect.text = getString(R.string.incorrect, screenState.scoreIncorrect.toString())
        imageView.setImageURI(screenState.currentQuizEntry.quizItem.getUri())
        optionsButtons.forEachIndexed { index, button ->
            val option = screenState.currentQuizEntry.options.getOrNull(index)
            option?.let { button.text = option }
        }
        screenState.selectedName?.let { selectedName ->
            val correctName = screenState.currentQuizEntry.quizItem.name
            val correctButton = optionsButtons.find { it.text == correctName }

            correctButton?.setBackgroundColor(activity.getColor(R.color.green))

            if (!screenState.isCorrect) {
                val selectedButton = optionsButtons.find { it.text == selectedName }
                selectedButton?.setBackgroundColor(activity.getColor(R.color.red))
            }
        } ?: run {
            optionsButtons.forEach { button -> button.setBackgroundColor(activity.getColor(R.color.dark_gray)) }
        }
        continueButton.visibility = if (screenState.isAnswered) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }
}
