package com.example.in_app_updates_sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.OnSuccessListener
import dagger.android.AndroidInjection
import java.util.concurrent.Executor
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG : String = MainActivity::class.java.simpleName
        const val REQUEST_UPDATE_CODE = 1
    }

    lateinit var installStateUpdatedListener: InstallStateUpdatedListener

    @Inject
    lateinit var appUpdateManager: AppUpdateManager

    @Inject
    lateinit var playServiceExecutor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_main)

        updateChecker()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == REQUEST_UPDATE_CODE) {
            if (resultCode != RESULT_OK) {
                // If the update is cancelled or fails, you can request to start the update again.
                Log.e(TAG, "Update flow failed! Result code: $resultCode")
            }
        }
    }

    override fun onResume() {

        super.onResume()

        appUpdateManager.appUpdateInfo.addOnSuccessListener(playServiceExecutor, OnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED)
                    updaterDownloadCompleted()
            } else {
                // for AppUpdateType.IMMEDIATE only
                // already executing updater
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        REQUEST_UPDATE_CODE
                    )
                }
            }
        })
    }

    private fun updateChecker() {

        //appUpdateManager = AppUpdateManagerFactory.create(this)
        installStateUpdatedListener = InstallStateUpdatedListener { installState ->
            when (installState.installStatus()) {
                InstallStatus.DOWNLOADED -> {
                    Log.d(TAG, "Downloaded")
                    updaterDownloadCompleted()
                }
                InstallStatus.INSTALLED -> {
                    Log.d(TAG, "Installed")
                    appUpdateManager.unregisterListener(installStateUpdatedListener)
                }
                else -> {
                    Log.d(TAG, "installStatus = " + installState.installStatus())
                }
            }
        }
        appUpdateManager.registerListener(installStateUpdatedListener)

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener(playServiceExecutor, OnSuccessListener { appUpdateInfo ->
            when (appUpdateInfo.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    val updateTypes = arrayOf(AppUpdateType.FLEXIBLE, AppUpdateType.IMMEDIATE)
                    run loop@{
                        updateTypes.forEach { type ->
                            if (appUpdateInfo.isUpdateTypeAllowed(type)) {
                                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, type, this, REQUEST_UPDATE_CODE)
                                return@loop
                            }
                        }
                    }
                }
                else -> {
                    Log.d(TAG, "updateAvailability = " + appUpdateInfo.updateAvailability())
                }
            }
        })
    }

    private fun updaterDownloadCompleted() {

        Snackbar.make(
            findViewById(R.id.activity_main_layout),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { appUpdateManager.completeUpdate() }
            show()
        }
    }
}
