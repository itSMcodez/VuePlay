package com.itsmcodez.vueplay

import android.app.Application
import androidx.annotation.OptIn
import androidx.media3.cast.Cast
import androidx.media3.common.util.UnstableApi
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize
import com.itsmcodez.vueplay.manager.VuePlayAnalyticsManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), SingletonImageLoader.Factory {

    @Inject
    lateinit var imageLoader: ImageLoader

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        lateinit var instance: App
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        // Initialize Cast singleton
        Cast.getSingletonInstance(this).initialize()
        // Initialize Firebase
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            if(BuildConfig.DEBUG) DebugAppCheckProviderFactory.getInstance()
            else PlayIntegrityAppCheckProviderFactory.getInstance(),
        )
        firebaseAnalytics = Firebase.analytics
        VuePlayAnalyticsManager.init(analytics = firebaseAnalytics)

        instance = this
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoader
}
