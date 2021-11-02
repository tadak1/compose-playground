package com.example.composeplayground.utilities

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController

class SystemStatusBarController constructor(
    private val activity: Activity,
) {
    fun showStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.show(WindowInsets.Type.statusBars())
            activity.window.insetsController?.show(WindowInsets.Type.navigationBars())
            activity.window.insetsController?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_DEFAULT
        } else {
            activity.actionBar?.show()
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.window.insetsController?.hide(WindowInsets.Type.statusBars())
            activity.window.insetsController?.hide(WindowInsets.Type.navigationBars())
            activity.window.insetsController?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            activity.actionBar?.hide()
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }
}