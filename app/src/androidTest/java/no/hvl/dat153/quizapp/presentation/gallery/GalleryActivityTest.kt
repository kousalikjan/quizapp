package no.hvl.dat153.quizapp.presentation.gallery

import android.content.Context
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.data.room.AppDatabase
import no.hvl.dat153.quizapp.domain.models.GalleryEntry
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GalleryActivityTest {

    private lateinit var scenario: ActivityScenario<GalleryActivity>
    private lateinit var recyclerView: RecyclerView

    @Before
    fun setup() {
        initializeDatabase()
        Intents.init()
        launchActivityAndSetupVariables()
    }

    private fun initializeDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        AppDatabase.initializeInMemory(context)

        val dao = AppDatabase.db.galleryEntryDao()

        runBlocking {
            dao.insert(GalleryEntry(name = "Option 1", uriString = "file://someuri"))
            dao.insert(GalleryEntry(name = "Option 2", uriString = "file://someuri"))
            dao.insert(GalleryEntry(name = "Option 3", uriString = "file://someuri"))
        }
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
        Thread.sleep(500) // TODO: Find a better way to wait for the recycler view to update

        itemCount = recyclerView.adapter?.itemCount ?: 0
        assertEquals(2, itemCount)
    }


    private fun launchActivityAndSetupVariables() {
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