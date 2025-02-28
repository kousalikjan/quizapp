package no.hvl.dat153.quizapp.presentation.quiz

import android.widget.Button
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertNotNull
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.presentation.ActivityTestWithInMemoryDatabase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizActivityTest : ActivityTestWithInMemoryDatabase() {

    private lateinit var scenario: ActivityScenario<QuizActivity>
    private lateinit var quizFragment: QuizFragment
    private lateinit var correctText0: String
    private lateinit var incorrectText0: String
    private lateinit var correctText1: String
    private lateinit var incorrectText1: String

    @Before
    fun setup() {
        initializeDatabase()
        launchActivityAndSetupVariables()
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun testCorrectAnswerIncreasesCorrectScore() {
        val buttonIds = listOf(R.id.btnOption1, R.id.btnOption2, R.id.btnOption3)

        // List all button texts for debugging
        buttonIds.forEach { buttonId ->
            onView(withId(buttonId)).check { view, _ ->
                val buttonText = (view as Button).text.toString()
                println("Button text: $buttonText")
            }
        }

        // Click the correct button
        val correctButtonId = findAnswerButton(buttonIds, isCorrect = true)
        assertNotNull(correctButtonId)

        checkScore(correctText = correctText0, incorrectText = incorrectText0)
        clickButton(correctButtonId!!)
        checkScore(correctText = correctText1, incorrectText = incorrectText0)

        clickButton(R.id.btnContinue)

        // Click the incorrect button
        val incorrectButtonId = findAnswerButton(buttonIds, isCorrect = false)
        assertNotNull(incorrectButtonId)

        clickButton(incorrectButtonId!!)
        checkScore(correctText = correctText1, incorrectText = incorrectText1)
    }

    override fun launchActivityAndSetupVariables() {
        scenario = ActivityScenario.launch(QuizActivity::class.java)
        scenario.onActivity { activity ->
            quizFragment = activity.supportFragmentManager.findFragmentById(R.id.fragment_container) as QuizFragment
            correctText0 = activity.getString(R.string.correct, "0")
            incorrectText0 = activity.getString(R.string.incorrect, "0")
            correctText1 = activity.getString(R.string.correct, "1")
            incorrectText1 = activity.getString(R.string.incorrect, "1")
        }
    }

    private fun findAnswerButton(buttonIds: List<Int>, isCorrect: Boolean): Int? {
        val correctAnswerText = quizFragment.viewModel.screenStateStream.value?.currentQuizEntry?.quizItem?.name
        assertNotNull(correctAnswerText)

        var foundButtonId: Int? = null

        buttonIds.forEach { buttonId ->
            onView(withId(buttonId)).check { view, _ ->
                val buttonText = (view as Button).text.toString()
                if ((buttonText == correctAnswerText) == isCorrect) {
                    foundButtonId = buttonId
                }
            }
        }

        return foundButtonId
    }

    private fun checkScore(correctText: String, incorrectText: String) {
        onView(withId(R.id.scoreCorrect)).check(matches(withText(correctText)))
        onView(withId(R.id.scoreIncorrect)).check(matches(withText(incorrectText)))
    }

    private fun clickButton(buttonId: Int) {
        onView(withId(buttonId)).perform(click())
    }

}