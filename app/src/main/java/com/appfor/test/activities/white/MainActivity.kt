package com.appfor.test.activities.white

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import com.appfor.test.R
import com.appfor.test.components.settings.ScreenUtils
import com.appfor.test.utils.Constants

class MainActivity : AppCompatActivity() {

    private lateinit var wv: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_webview)
        ScreenUtils().setFull(window)
        wv = findViewById(R.id.webview)

        loadWVURl(wv)
        wv.loadUrl(Constants.URL_WHITE)
        Log.d("LOG_APP", Constants.URL_WHITE)

            wv.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event?.action === MotionEvent.ACTION_UP && wv.canGoBack()) {
                        wv.goBack()
                        return true
                    } else {
                        return false
                    }
                }
            })
    }
    fun loadWVURl(wov: WebView) {
        val webSettings: WebSettings = wov.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        wov.settings.pluginState = WebSettings.PluginState.ON
        wov.settings.allowFileAccess = true
    }
}