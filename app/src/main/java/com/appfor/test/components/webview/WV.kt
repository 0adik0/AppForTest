package com.appfor.test.components.webview

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.webkit.*
import com.appfor.test.activities.white.MainActivity
import com.appfor.test.utils.Constants.URL

class WV {
    fun setParams(activity: Activity, wov: WebView, sPrefs: SharedPreferences){
        val webSettings: WebSettings = wov.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        wov.settings.pluginState = WebSettings.PluginState.ON
        wov.settings.allowFileAccess = true

        wov.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return if (url == null || url.startsWith("http://") || url.startsWith("https://")) false else try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view.context.startActivity(intent)
                    true
                } catch (e: Exception) {
                    Log.i("TAG", "shouldOverrideUrlLoading Exception:$e")
                    true
                }
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (sPrefs.getString("last_url", null) == null){
                    with(sPrefs.edit()){
                        putString("last_url", wov.url)
                        apply()
                    }
                }
                view.evaluateJavascript("(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();") { html: String ->
                    if (html == "\"\\u003Chtml>\\u003Chead>\\u003C/head>\\u003Cbody>\\u003C/body>\\u003C/html>\"") {
                        activity.startActivity(Intent(activity, MainActivity::class.java))
                        activity.finish()
                    }
                }
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                val statusCode = errorResponse?.statusCode
                Log.d("LOG_APP", statusCode.toString())
                if (statusCode == 404) {
                    if (wov.url == "${URL}/") {
                        activity.startActivity(Intent(activity, MainActivity::class.java))
                        activity.finish()
                    }
                }
            }
        }
    }
}