package com.itsmcodez.vueplay.presentation.streaming.download

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itsmcodez.vueplay.R
import com.itsmcodez.vueplay.ui.component.BrowserWebView
import com.itsmcodez.vueplay.ui.component.WebViewDownloadIntent
import com.itsmcodez.vueplay.ui.component.vueui.VueButton
import com.itsmcodez.vueplay.util.FileUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieAndTvSeriesDownloadScreen(
    modifier: Modifier = Modifier,
    viewModel: MovieAndTvSeriesDownloadViewModel,
    onShowSnackbar: (msg: String, action: String?, result: (SnackbarResult) -> Unit) -> Unit,
    onNavigateToDownloadsScreen: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var activeIntent by remember { mutableStateOf<WebViewDownloadIntent?>(null) }
    var webView by remember { mutableStateOf<android.webkit.WebView?>(null) }
    var showCustomErrorPage by remember { mutableStateOf(false) }
    val isTvSeries by viewModel.isTvSeries
    val tmdbId by viewModel.tmdbId
    val season by viewModel.season
    val episode by viewModel.episode
    val downloadUrl = remember(isTvSeries, tmdbId, season, episode) {
        getDownloadUrl(
            tmdbId = tmdbId,
            season = season,
            episode = episode,
            isTvSeries = isTvSeries
        )
    }

    if (activeIntent != null) {
        val fileIntent = activeIntent!!
        val sizeInMb = FileUtil.formatSizeMb(
            LocalLocale.current.platformLocale,
            fileIntent.contentLength.toFloat()
        )

        AlertDialog(
            onDismissRequest = { activeIntent = null },
            title = { Text(stringResource(R.string.title_menu_item_download)) },
            text = {
                Column {
                    Text(stringResource(R.string.msg_dialog_download))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Name: ${fileIntent.fileName}", style = MaterialTheme.typography.bodySmall)
                    Text("Size: $sizeInMb", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.startDownload(
                            fileIntent.url,
                            fileIntent.fileName,
                            webView?.settings?.userAgentString
                        )
                        activeIntent = null
                        onShowSnackbar(
                            context.getString(R.string.msg_download_started),
                            context.getString(R.string.view)
                        ) { result ->
                            when (result) {
                                SnackbarResult.ActionPerformed -> {
                                    onNavigateToDownloadsScreen()
                                }
                                SnackbarResult.Dismissed -> Unit
                            }
                        }
                    }
                ) { Text(text = stringResource(R.string.title_menu_item_download)) }
            },
            dismissButton = {
                TextButton(onClick = { activeIntent = null }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    BackHandler(webView?.canGoBack() == true) { webView?.goBack() }

    TopAppBar(
        title = { Text(text = stringResource(R.string.title_menu_item_download)) },
        navigationIcon = {
            IconButton(onClick = {
                if (webView?.canGoBack() == true) webView?.goBack()
                else onBack()
            }) {
                Icon(
                    painter = painterResource(R.drawable.rounded_arrow_back_24),
                    contentDescription = null
                )
            }
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(
                WindowInsets(
                    top = TopAppBarDefaults.TopAppBarExpandedHeight
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            var showProgress by remember { mutableStateOf(false) }
            var progress by remember { mutableFloatStateOf(0f) }

            if(showProgress)
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    progress = { progress }
                )

            if(showCustomErrorPage)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.msg_refresh_failed),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)
                    )
                    VueButton(onClick = { webView?.reload() }) {
                        Text(text = stringResource(R.string.refresh))
                    }
                }

            if(showCustomErrorPage.not())
                BrowserWebView(
                url = downloadUrl,
                modifier = Modifier.weight(1f),
                onWebViewCreated = { view -> webView = view },
                onDownloadTriggered = { activeIntent = it },
                onProgressChanged = { newProgress -> progress = newProgress / 100f },
                onReceivedError = { error, request ->
                    if(request.isForMainFrame) {
                        showCustomErrorPage = true
                        showProgress = false
                        webView?.stopLoading()
                    }
                },
                onPageStarted = {
                    showProgress = true
                    showCustomErrorPage = false
                },
                onPageFinished = { showProgress = false }
            )
        }
    }
}

fun getDownloadUrl(tmdbId: String, season: Int = -1, episode: Int = -1, isTvSeries: Boolean): String {
    val hasSeries = season > -1 && episode > -1
    return if(isTvSeries && hasSeries) {
        "https://vidvault.ru/tv/${tmdbId}/${season}/${episode}"
    } else {
        "https://vidvault.ru/movie/${tmdbId}"
    }
}
