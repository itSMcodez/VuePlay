package com.itsmcodez.vueplay.util

import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

object TimeFormatter {
    fun formatTimeMilliseconds(milliseconds: Long, locale: Locale): String {
        val formattedTime = milliseconds
            .milliseconds
            .toComponents { days, hours, minutes, seconds, _ ->
                if (days > 0) {
                    String.format(
                        locale,
                        "%02d:%02d:%02d:%02d",
                        days,
                        hours,
                        minutes,
                        seconds
                    )
                } else if (hours > 0) {
                    String.format(
                        locale,
                        "%02d:%02d:%02d",
                        hours,
                        minutes,
                        seconds
                    )
                } else {
                    String.format(
                        locale,
                        "%02d:%02d",
                        minutes,
                        seconds
                    )
                }
            }
        return formattedTime
    }
}