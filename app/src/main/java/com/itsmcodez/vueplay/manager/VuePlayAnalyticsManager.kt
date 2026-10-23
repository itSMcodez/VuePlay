package com.itsmcodez.vueplay.manager

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent

object VuePlayAnalyticsManager {
    private var analytics: FirebaseAnalytics? = null
    fun init(analytics: FirebaseAnalytics) {
        VuePlayAnalyticsManager.analytics = analytics
    }

    fun logClickEvent(event: ClickEvent) {
        analytics?.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
            param(FirebaseAnalytics.Param.ITEM_ID, event.id)
            param(FirebaseAnalytics.Param.ITEM_NAME, event.itemName)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, event.contentType)
        }
    }
}

enum class ClickEvent(
    val id: String,
    val itemName: String,
    val contentType: String
) {
    FEATURED_MOVIES_TV_SHOWS_CAROUSEL(
        "featured_movies_tv_shows_carousel",
        "Featured Movies & TV Shows Carousel",
        "Carousel"
    ),
    MOVIES_TV_SHOWS_GENRE_TAB_ROW(
        "movies_tv_shows_genre_tab_row",
        "Movies & TV Shows Genre Tab Row",
        "Genre Tab Row"
    ),
    MOVIES_TV_SHOWS_NAVIGABLE_SECTION_HEADER(
        "movies_tv_shows_navigable_section_header",
        "Movies & TV Shows Navigable Section Header",
        "Navigable Section Header"
    ),
    DOWNLOAD_MENU_ITEM(
        "download_menu_item",
        "Download Menu Item",
        "Menu Item"
    ),
    REPORT_MENU_ITEM(
        "report_menu_item",
        "Report Menu Item",
        "Menu Item"
    ),
    MOVIE_TV_SHOW_ITEM(
        "movie_tv_show_item",
        "Movie & TV Show Item",
        "Item"
    ),
    MOVIES_TV_SHOWS_SEARCH(
        "movies_tv_shows_search",
        "Movies & TV Shows Search",
        "Search"
    ),
    SEARCH_HISTORY_ITEM(
        "search_history_item",
        "Search History Item",
        "Item"
    ),
    LOCAL_NAV_BAR_ITEM(
        "local_nav_bar_item",
        "Local Nav Bar Item",
        "Item"
    ),
    SETTINGS_NAV_BAR_ITEM(
        "settings_nav_bar_item",
        "Settings Nav Bar Item",
        "Item"
    ),
    LOCAL_VIDEO_ITEM(
        "local_video_item",
        "Local Video Item",
        "Item"
    ),
    LOCAL_VIDEO_FOLDER_ITEM(
        "local_video_folder_item",
        "Local Video Folder Item",
        "Item"
    ),
    WATCH_BUTTON(
        "watch_button",
        "Watch Button",
        "Button"
    ),
    DOWNLOAD_BUTTON(
        "download_button",
        "Download Button",
        "Button"
    ),
    REPORT_BUTTON(
        "report_button",
        "Report Button",
        "Button"
    )
}
