package no.hvl.dat153.quizapp.presentation.quiz

import android.content.Context
import android.widget.Button
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import no.hvl.dat153.quizapp.R
import no.hvl.dat153.quizapp.data.room.AppDatabase
import no.hvl.dat153.quizapp.domain.models.GalleryEntry
import no.hvl.dat153.quizapp.domain.models.QuizEntry
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizActivityTest {

    private lateinit var scenario: ActivityScenario<QuizActivity>

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        AppDatabase.initializeInMemory(context)

        val dao = AppDatabase.db.galleryEntryDao()

        runBlocking {
            dao.insert(GalleryEntry(name = "Option 1", uriString = "file://someuri"))
            dao.insert(GalleryEntry(name = "Option 2", uriString = "file://someuri"))
            dao.insert(GalleryEntry(name = "Option 3", uriString = "file://someuri"))
        }

        scenario = ActivityScenario.launch(QuizActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    @Test
    fun testCorrectAnswerIncreasesCorrectScore() {
        // Find the button with the "Correct" value
        val buttonIds = listOf(R.id.btnOption1, R.id.btnOption2, R.id.btnOption3)


    }

}