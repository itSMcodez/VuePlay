package com.itsmcodez.vueplay.constant

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.itsmcodez.vueplay.App
import com.itsmcodez.vueplay.R

object PreferenceKeys {

    // View Type
    val VIEW_TYPE_KEY = stringPreferencesKey("view_type")
    enum class ViewType {
        GRID,
        LIST
    }

    // Sort Order
    val VIDEOS_SORT_ORDER_KEY = stringPreferencesKey("videos_sort_order")
    enum class SortOrder(val label: String) {
        ASCENDING(App.instance.getString(R.string.sort_order_ascending)),
        DESCENDING(App.instance.getString(R.string.sort_order_descending))
    }

    // Sort Option
    val VIDEOS_SORT_OPTION_KEY = stringPreferencesKey("videos_sort_option")
    enum class SortOption(val label: String) {
        NAME(App.instance.getString(R.string.sort_option_name)),
        DATE_ADDED(App.instance.getString(R.string.sort_option_date_added)),
        DURATION(App.instance.getString(R.string.sort_option_duration))
    }

    // General
    val FIRST_TIME_VIDEO_PLAYBACK = booleanPreferencesKey("first_time_video_playback")
    val ENABLE_AUTO_RESUME_VIDEOS = booleanPreferencesKey("enable_auto_resume_videos")
    val ENABLE_HANDLE_AUDIO_FOCUS = booleanPreferencesKey("enable_handle_audio_focus")
    val ENABLE_AUTO_PAUSE_ON_HEADSET_CONNECT = booleanPreferencesKey("enable_auto_pause_on_headset_connect")

}