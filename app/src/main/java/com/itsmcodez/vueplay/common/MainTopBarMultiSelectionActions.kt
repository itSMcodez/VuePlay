package com.itsmcodez.vueplay.common

import androidx.annotation.DrawableRes
import com.itsmcodez.vueplay.App
import com.itsmcodez.vueplay.R

enum class MainTopBarMultiSelectionActions(
    val label: String,
    @DrawableRes val icon: Int
) {
    SHARE(
        App.instance.getString(R.string.share),
        R.drawable.rounded_share_24
    ),

    DELETE(
        App.instance.getString(R.string.delete),
        R.drawable.rounded_delete_24
    )
}