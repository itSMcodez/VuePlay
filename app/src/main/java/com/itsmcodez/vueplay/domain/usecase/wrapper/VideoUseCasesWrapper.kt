package com.itsmcodez.vueplay.domain.usecase.wrapper

import com.itsmcodez.vueplay.domain.usecase.DeleteResumableVideo
import com.itsmcodez.vueplay.domain.usecase.DeleteVideos
import com.itsmcodez.vueplay.domain.usecase.GetAllResumableVideos
import com.itsmcodez.vueplay.domain.usecase.GetResumableVideo
import com.itsmcodez.vueplay.domain.usecase.GetVideos
import com.itsmcodez.vueplay.domain.usecase.GetVideosAsync
import com.itsmcodez.vueplay.domain.usecase.InsertResumableVideo
import com.itsmcodez.vueplay.domain.usecase.LaunchVideos
import com.itsmcodez.vueplay.domain.usecase.RenameVideo
import com.itsmcodez.vueplay.domain.usecase.ShareVideos
import com.itsmcodez.vueplay.domain.usecase.UpdateResumableVideo
import javax.inject.Inject

data class VideoUseCasesWrapper @Inject constructor(
    val launchVideos: LaunchVideos,
    val getVideos: GetVideos,
    val getVideosAsync: GetVideosAsync,
    val getAllResumableVideos: GetAllResumableVideos,
    val renameVideo: RenameVideo,
    val shareVideos: ShareVideos,
    val deleteVideos: DeleteVideos,
    val getResumableVideo: GetResumableVideo,
    val insertResumableVideo: InsertResumableVideo,
    val updateResumableVideo: UpdateResumableVideo,
    val deleteResumableVideo: DeleteResumableVideo
)
