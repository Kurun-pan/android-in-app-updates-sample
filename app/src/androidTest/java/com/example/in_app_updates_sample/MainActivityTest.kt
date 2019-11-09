package com.example.in_app_updates_sample

import android.app.Activity
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.in_app_updates_sample.di.TestApplication
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.android.controller.ComponentController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
//@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var fakeAppUpdateManager: FakeAppUpdateManager
    private lateinit var mainActivityController: ActivityController<MainActivity>
    private lateinit var mainActivity: MainActivity

    @Before
    fun setup() {

        mainActivityController = Robolectric.buildActivity(MainActivity::class.java)
        mainActivity = Mockito.spy(mainActivityController.get())
        fakeAppUpdateManager = ApplicationProvider.getApplicationContext<TestApplication>().fakeAppUpdateManager
        replaceComponentInActivityController(mainActivityController, this.mainActivity)
    }

    private fun replaceComponentInActivityController(activityController: ActivityController<*>, activity: Activity) {

        val componentField = ComponentController::class.java.getDeclaredField("component")
        componentField.isAccessible = true
        componentField.set(activityController, activity)
    }

    @Test
    fun test_FlexibleUpdateComplete() {

        fakeAppUpdateManager.partiallyAllowedUpdateType = AppUpdateType.FLEXIBLE
        fakeAppUpdateManager.setUpdateAvailable(2)

        ActivityScenario.launch(MainActivity::class.java)

        assertTrue(fakeAppUpdateManager.isConfirmationDialogVisible)

        fakeAppUpdateManager.userAcceptsUpdate()
        fakeAppUpdateManager.downloadStarts()
        fakeAppUpdateManager.downloadCompletes()

        assertTrue(fakeAppUpdateManager.isInstallSplashScreenVisible)
        fakeAppUpdateManager.installCompletes()
    }
}
