package com.example.storyapp_ade.view.welcome

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.storyapp_ade.R
import com.example.storyapp_ade.di.EspressoIdlingResource
import com.example.storyapp_ade.view.login.LoginActivity
import com.example.storyapp_ade.view.main.MainActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@MediumTest
class WelcomeActivityTest {

    private val dummyEmail = "aderockysaputra@gmail.com"
    private val dummyPass = "10032003"

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(WelcomeActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun fromWelcomeUntilLoginandLogout() {

        //check if login button is displayed
        onView(withId(R.id.login_Button)).check(matches(isDisplayed()))

        //click login button (WelcomeActivity)
        onView(withId(R.id.login_Button)).perform(click())

        //check if already in LoginActivity
        Intents.intended(hasComponent(LoginActivity::class.java.name))

        //fill the form
        onView(withId(R.id.emailEditText)).perform(typeText(dummyEmail), closeSoftKeyboard())
        onView(withId(R.id.passwordEditText)).perform(typeText(dummyPass), closeSoftKeyboard())

        //click login button (LoginActivity)
        onView(withId(R.id.loginButton)).perform(click())

        //check if already in MainActivity
        Intents.intended(hasComponent(MainActivity::class.java.name))

        //check if recyclerview is displayed
        onView(withId(R.id.rv_hero)).check(matches(isDisplayed()))

        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)

        onView(withId(R.id.btn_logout)).perform(click())

        Intents.intended(hasComponent(WelcomeActivity::class.java.name))
    }
}