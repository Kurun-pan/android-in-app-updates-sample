package com.example.in_app_updates_sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class FakeMainActivity : AppCompatActivity() {

    companion object {
        val TAG : String = FakeMainActivity::class.java.simpleName
        const val REQUEST_UPDATE_CODE = 1
    }

    private lateinit var alertDialog: AlertDialog
    private lateinit var fakeAppUpdateManager: FakeAppUpdateManager
    private lateinit var installStateUpdatedListener: InstallStateUpdatedListener

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fakeUpdateChecker()
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

        fakeAppUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->

            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED)
                    updaterDownloadCompleted(fakeAppUpdateManager)
            } else {
                // for AppUpdateType.IMMEDIATE only
                // already executing updater
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    fakeAppUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        REQUEST_UPDATE_CODE
                    )
                }
            }
        }
    }

    private fun fakeUpdateChecker() {

        fakeAppUpdateManager = FakeAppUpdateManager(this)
        fakeAppUpdateManager.setUpdateAvailable(100) // set available version code
        fakeAppUpdateManager.partiallyAllowedUpdateType = AppUpdateType.IMMEDIATE // or AppUpdateType.FLEXIBLE

        installStateUpdatedListener = InstallStateUpdatedListener { installState ->
            when (installState.installStatus()) {

                InstallStatus.CANCELED -> {
                    Log.d(TAG, "Canceled")
                }
                InstallStatus.DOWNLOADING -> {
                    Log.d(TAG, "Downloading")
                    fakeAppUpdateManager.downloadCompletes()
                }
                InstallStatus.DOWNLOADED -> {
                    Log.d(TAG, "Downloaded")
                    updaterDownloadCompleted(fakeAppUpdateManager)
                }
                InstallStatus.FAILED -> {
                    Log.d(TAG, "Failed")
                }
                InstallStatus.INSTALLING -> {
                    Log.d(TAG, "Installing")
                    fakeAppUpdateManager.installCompletes()
                }
                InstallStatus.INSTALLED -> {
                    Log.d(TAG, "Installed")
                    fakeAppUpdateManager.unregisterListener(installStateUpdatedListener)
                }
            }
        }
        fakeAppUpdateManager.registerListener(installStateUpdatedListener)

        val appUpdateInfoTask = fakeAppUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            when (appUpdateInfo.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    val updateTypes = arrayOf(AppUpdateType.FLEXIBLE, AppUpdateType.IMMEDIATE)
                    var targetType = -1

                    run loop@{
                        updateTypes.forEach { type ->
                            if (appUpdateInfo.isUpdateTypeAllowed(type)) {
                                targetType = type
                                return@loop
                            }
                        }
                    }

                    if (targetType != -1) {
                        fakeAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            targetType,
                            this,
                            REQUEST_UPDATE_CODE
                        )

                        fakeAppUpdateManager.userAcceptsUpdate()
                        fakeAppUpdateManager.downloadStarts()
                    }
                }
            }
        }
    }

    private fun updaterDownloadCompleted(manager: AppUpdateManager) {

        if (alertDialog.isShowing)
            return

        alertDialog = AlertDialog.Builder(this)
            .setTitle("Just now restart ...")
            .setMessage("An update has just been downloaded.")
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                // automatically install and restart app
                manager.completeUpdate()
            }.show()
    }
}
