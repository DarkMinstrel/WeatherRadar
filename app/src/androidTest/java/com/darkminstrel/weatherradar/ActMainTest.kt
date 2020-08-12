package com.darkminstrel.weatherradar

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import com.darkminstrel.weatherradar.ui.act_main.ActMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class ActMainTest {

    @get:Rule
    val activityTestRule = ActivityTestRule(ActMain::class.java)

    @Before
    fun init() {}

    @Test
    fun testLoading() {
        assertOr({
            onView(withId(R.id.error)).checkVisible()
        },{
            onView(withId(R.id.progress)).checkVisible()
            onView(withId(R.id.error)).checkNotVisible()
            onView(withId(R.id.iv_radar)).checkNotVisible()

            onView(withId(R.id.progress)).waitUntilNotVisible(10000)

            assertOr({onView(withId(R.id.iv_radar)).checkVisible()} , {onView(withId(R.id.error)).checkVisible()})
        })
    }
}