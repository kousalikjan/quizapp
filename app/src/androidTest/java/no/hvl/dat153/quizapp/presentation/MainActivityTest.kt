package no.hvl.dat153.quizapp.presentation

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.presentation.gallery.GalleryActivity
import no.hvl.dat153.quizapp.presentation.quiz.QuizActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest : ActivityTestWithInMemoryDatabase() {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        initializeDatabase()
        Intents.init()
        launchActivityAndSetupVariables()
    }

    @After
    fun tearDown() {
        scenario.close()
        Intents.release()
    }

    @Test
    fun testGalleryButtonStartsGalleryActivity()
    {
        onView(withId(R.id.buttonGallery)).perform(click())

        intended(hasComponent(GalleryActivity::class.java.name))
    }

    override fun launchActivityAndSetupVariables() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

}