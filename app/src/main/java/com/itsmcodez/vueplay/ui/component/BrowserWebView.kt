package com.itsmcodez.vueplay.ui.component

import android.content.Intent
import android.graphics.Bitmap
import android.os.Message
import android.view.View
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

data class WebViewDownloadIntent(
    val url: String,
    val userAgent: String,
    val contentDisposition: String?,
    val fileName: String,
    val mimeType: String,
    val contentLength: Long
)

@Composable
fun BrowserWebView(
    modifier: Modifier = Modifier,
    url: String,
    onDownloadTriggered: (intent: WebViewDownloadIntent) -> Unit,
    onProgressChanged: (progress: Int) -> Unit,
    onReceivedError: (error: WebResourceError, request: WebResourceRequest) -> Unit,
    onPageStarted: (url: String) -> Unit,
    onPageFinished: (url: String) -> Unit,
    onShowCustomView: (viewCallback: WebChromeClient.CustomViewCallback?, view: View?) -> Unit = { _, _ -> },
    onHideCustomView: () -> Unit = {},
    onWebViewCreated: (view: WebView) -> Unit,
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = android.widget.FrameLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        onPageFinished(url ?: "")
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        onPageStarted(url ?: "")
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        onReceivedError(error ?: return, request ?: return)
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url.toString()
                        if (url.startsWith("http://") || url.startsWith("https://")) return false
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Ignore
                        }
                        return true
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        onProgressChanged(newProgress)
                    }

                    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                        onShowCustomView(callback, view)
                    }

                    override fun onHideCustomView() {
                        onHideCustomView()
                    }

                    // Prevent navigate away from WebView
                    override fun onCreateWindow(
                        view: WebView?,
                        isDialog: Boolean,
                        isUserGesture: Boolean,
                        resultMsg: Message?
                    ): Boolean {
                        return false;
                    }
                }

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                // Prevent navigate away from WebView
                settings.javaScriptCanOpenWindowsAutomatically = false
                settings.setSupportMultipleWindows(false)
                settings.mediaPlaybackRequiresUserGesture = false

                setDownloadListener { downloadUrl, userAgent, contentDisposition, mimeType, contentLength ->
                    settings.userAgentString = userAgent
                    val extractedFileName = URLUtil.guessFileName(
                        downloadUrl,
                        contentDisposition,
                        mimeType
                    )
                    val formattedFilename = try {
                        URLDecoder.decode(extractedFileName, StandardCharsets.UTF_8.name())
                    } catch (e: Exception) { extractedFileName }
                    val intentData = WebViewDownloadIntent(
                        url = downloadUrl,
                        userAgent = userAgent,
                        contentDisposition = contentDisposition,
                        fileName = formattedFilename,
                        mimeType = mimeType,
                        contentLength = contentLength
                    )
                    onDownloadTriggered(intentData)
                }
            }
        },
        update = { webView ->
            if (webView.url != url) {
                webView.loadUrl(url)
            }
            onWebViewCreated(webView)
        }
    )
}
