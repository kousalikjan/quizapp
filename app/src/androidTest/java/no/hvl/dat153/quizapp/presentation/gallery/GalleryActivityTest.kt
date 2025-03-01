package no.hvl.dat153.quizapp.presentation.gallery

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.presentation.ActivityTestWithInMemoryDatabase
import no.hvl.dat153.quizapp.presentation.addentry.AddEntryActivity
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GalleryActivityTest : ActivityTestWithInMemoryDatabase() {

    private lateinit var scenario: ActivityScenario<GalleryActivity>
    private lateinit var recyclerView: RecyclerView

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
    fun testRemoveEntry() {
        var itemCount = recyclerView.adapter?.itemCount ?: 0
        assertEquals(3, itemCount)

        onView(withId(R.id.recyclerView))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    clickChildButtonWithId(R.id.buttonDelete)
                )
            )
        Thread.sleep(500) // Find a better way to wait for the RecyclerView to update

        itemCount = recyclerView.adapter?.itemCount ?: 0
        assertEquals(2, itemCount)
    }

    @Test
    fun testAddEntry() {
        val testImageUri = Uri.parse("android.resource://no.hvl.dat153.quizapp/${R.drawable.test_image}")
        val resultData = Intent().apply {
            data = testImageUri
        }
        Intents.intending(hasAction(Intent.ACTION_PICK))
            .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, resultData))

        var itemCount = recyclerView.adapter?.itemCount ?: 0
        assertEquals(3, itemCount)

        onView(withId(R.id.buttonAddEntry))
            .perform(click())

        intended(hasComponent(AddEntryActivity::class.java.name))

        // Write a text in the EditText
        onView(withId(R.id.addEntryEditText))
            .perform(click())
            .perform(ViewActions.typeText("New entry"))

        onView(withId(R.id.addEntryButtonAdd))
            .perform(click())

        intended(hasAction(Intent.ACTION_PICK))

        onView(withId(R.id.addEntryButtonSave))
            .perform(click())

        Thread.sleep(500) // Find a better way to wait for the RecyclerView to update

        itemCount = recyclerView.adapter?.itemCount ?: 0
        assertEquals(4, itemCount)
    }

    override fun launchActivityAndSetupVariables() {
        scenario = ActivityScenario.launch(GalleryActivity::class.java)
        scenario.onActivity { activity ->
            recyclerView = activity.findViewById(R.id.recyclerView)
        }
    }

    // Inspired from https://stackoverflow.com/questions/28476507/using-espresso-to-click-view-inside-recyclerview-item
    private fun clickChildButtonWithId(buttonDelete: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints() = allOf(
                isDescendantOfA(isAssignableFrom(RecyclerView::class.java)),
                isAssignableFrom(Button::class.java)
            )

            override fun getDescription() = "Click on a child button with a specified id."

            override fun perform(uiController: UiController?, view: View?) {
                val v = view?.findViewById<Button>(buttonDelete)
                v?.performClick()
            }
        }
    }
}
