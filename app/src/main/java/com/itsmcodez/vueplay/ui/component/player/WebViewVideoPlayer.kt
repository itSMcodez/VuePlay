package com.itsmcodez.vueplay.ui.component.player

import android.content.Intent
import android.graphics.Bitmap
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.itsmcodez.vueplay.ui.component.BrowserWebView

@Composable
fun WebViewVideoPlayer(
    modifier: Modifier = Modifier,
    videoUrl: String,
    allowedHosts: Set<String>,
    sandbox: Boolean,
    onProgressChanged: (Int) -> Unit,
    onReceivedError: (WebResourceError, WebResourceRequest) -> Unit,
    onPageStarted: (String) -> Unit,
    onPageFinished: (String) -> Unit,
    onShowCustomView: (WebChromeClient.CustomViewCallback?, View?) -> Unit = { _, _ -> },
    onHideCustomView: () -> Unit = {},
    onWebViewCreated: (WebView) -> Unit
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                * {
                    box-sizing: border-box;                
                }
                
                html, body {
                    margin: 0;
                    padding: 0;
                    width: 100%;
                    height: 100%;
                    background: black;
                }

                iframe {
                    width: 100%;
                    height: 100%;
                    border: none;
                    display: block;
                }
            </style>
        </head>

        <body>
            <iframe
                src="$videoUrl"
                allowfullscreen allow="autoplay; fullscreen; picture-in-picture" title="Media Player"
                ${ if (sandbox) "sandbox=\"allow-forms allow-pointer-lock allow-same-origin allow-scripts allow-presentation\"" else ""}>
            </iframe>
        </body>
        </html>
    """.trimIndent()

    // Destroy WebView state to prevent backstack buildup
    DisposableEffect(Unit) {
        onDispose {
            webView?.let { view ->
                view.stopLoading()
                view.onPause()
                (view.parent as? ViewGroup)?.removeView(view)
                view.destroy()
            }
            webView = null
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = android.widget.FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
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

                    // Prevents application crashes if the media engine runs out of memory or drops frames
                    override fun onRenderProcessGone(
                        view: WebView?,
                        detail: RenderProcessGoneDetail?
                    ): Boolean {
                        return true // Tell the system we handled the exception
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url ?: return true
                        val host = url.host ?: return true
                        val urlString = url.toString()

                        val isMainFrame = request.isForMainFrame

                        // Dynamically check against the passed allowedHosts parameter
                        val isAllowedDomain = allowedHosts.any { allowed ->
                            host == allowed || host.endsWith(".$allowed")
                        }

                        // Block malicious redirects inside sub-frames/iframes
                        if (!isMainFrame && !isAllowedDomain) {
                            return true
                        }

                        // Check main-frame navigations
                        if (urlString.startsWith("http://") || urlString.startsWith("https://")) {
                            return !isAllowedDomain
                        }

                        // Handle deep link intents safely
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, url)
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
            }
        },
        update = { view ->
            view.loadDataWithBaseURL("https://vueplay.cc/", html, "text/html", "utf-8", null)
            webView = view
            onWebViewCreated(view)
        }
    )
}