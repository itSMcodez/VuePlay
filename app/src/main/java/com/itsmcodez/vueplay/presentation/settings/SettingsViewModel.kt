package com.itsmcodez.vueplay.presentation.settings

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application
) : ViewModel() {
    private val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        application.packageManager.getPackageInfo(
            application.packageName,
            PackageManager.PackageInfoFlags.of(0)
        )
    } else {
        @Suppress("DEPRECATION")
        application.packageManager.getPackageInfo(application.packageName, 0)
    }

    private val _currentAppVersion = MutableStateFlow(packageInfo.versionName)
    val currentAppVersion = _currentAppVersion.asStateFlow()

    private val _currentAppBuildNumber = MutableStateFlow(
        PackageInfoCompat.getLongVersionCode(packageInfo)
    )
    val currentAppBuildNumber = _currentAppBuildNumber.asStateFlow()

    private val _currentSystemVersion = MutableStateFlow(Build.VERSION.RELEASE)
    val currentSystemVersion = _currentSystemVersion.asStateFlow()
}
