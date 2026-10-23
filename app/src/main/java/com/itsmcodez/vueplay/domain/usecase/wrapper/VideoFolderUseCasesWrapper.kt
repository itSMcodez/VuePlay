package com.itsmcodez.vueplay.domain.usecase.wrapper

import com.itsmcodez.vueplay.domain.usecase.GetVideoFolders
import com.itsmcodez.vueplay.domain.usecase.GetVideoFoldersAsync
import javax.inject.Inject

data class VideoFolderUseCasesWrapper @Inject constructor(
    val getVideoFolders: GetVideoFolders,
    val getVideoFoldersAsync: GetVideoFoldersAsync
)