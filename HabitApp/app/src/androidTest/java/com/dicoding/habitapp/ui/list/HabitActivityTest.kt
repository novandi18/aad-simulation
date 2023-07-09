package com.dicoding.habitapp.ui.list

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.dicoding.habitapp.R
import com.dicoding.habitapp.ui.add.AddHabitActivity

//TODO 16 : Write UI test to validate when user tap Add Habit (+), the AddHabitActivity displayed
@RunWith(AndroidJUnit4ClassRunner::class)
class HabitActivityTest {
    @JvmField
    @Rule
    val activityRule = ActivityScenarioRule(HabitListActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    fun showAddHabitActivityWhenUserTapFab() {
        onView(withId(R.id.fab)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(AddHabitActivity::class.java.name))
    }
}