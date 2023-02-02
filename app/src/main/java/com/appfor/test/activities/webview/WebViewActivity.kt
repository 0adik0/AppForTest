package com.appfor.test.activities.webview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.appfor.test.R
import com.appfor.test.components.settings.ScreenUtils

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        ScreenUtils().setFull(window)
    }
}