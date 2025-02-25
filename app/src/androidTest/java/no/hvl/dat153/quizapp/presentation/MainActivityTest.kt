package no.hvl.dat153.quizapp.presentation

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.presentation.gallery.GalleryActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val intentsTestRule = IntentsTestRule(MainActivity::class.java)

    @Test
    fun testGalleryButtonStartsGalleryActivity()
    {
        onView(withId(R.id.buttonGallery)).perform(click())

        intended(hasComponent(GalleryActivity::class.java.name))
    }

}