package com.appfor.test.activities.splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.appfor.test.R
import com.appfor.test.activities.webview.WebViewActivity
import com.appfor.test.components.appsflyer.AppsflyerUtils
import com.appfor.test.components.settings.ScreenUtils
import com.appfor.test.components.url.ParamUtils
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashMap

class SplashActivity : AppCompatActivity() {
    private val model: SplashViewModel by lazy { ViewModelProvider(this)[SplashViewModel::class.java] }
    //
    private lateinit var sPrefs: SharedPreferences
    private lateinit var job: Job
    private lateinit var observer: Observer<Boolean>
    private lateinit var tm: TelephonyManager
    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        tm = applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val masterKey = MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        sPrefs = EncryptedSharedPreferences.create(this, "setting", masterKey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
        ScreenUtils().setFull(window)
        if(sPrefs.getInt("join_install", 0) != 1){
            val edit = sPrefs.edit()
            if(sPrefs.getString("install_id", null) == null) edit.putString("install_id", UUID.randomUUID().toString()).apply() // Проверка на инсталл ид
       /*     CoroutineScope(Dispatchers.IO).launch {
                Repository().sendInstall(sPrefs, tm, applicationContext.packageName)
                withContext(coroutineContext){
                    with(edit){
                        putInt("join_install", 1)
                        apply()
                    }
                }
            } */
        }
        if(isInternetConnection(applicationContext)){
            if(sPrefs.getString("last_url", null) != null) {
                startActivity(Intent(this, WebViewActivity::class.java).putExtra("organic", false))
                finish()
            } else {
              /*  if (model.statusInstall.toString() == "Organic") {
                    startActivity(Intent(this, WebViewActivity::class.java).putExtra("organic", true))
                } */
                model.initViewModel()
                observer = observer()
                model.finished.observe(this, observer)
            }
        } else {
            startActivity(Intent(this, WebViewActivity::class.java).putExtra("organic", true))
            finish()
        }

    }
    private fun observer() = Observer<Boolean> {
        if(it){
            job = CoroutineScope(Dispatchers.IO).launch {
                val url = createURL(model.campaign.value!!)
                with(sPrefs.edit()){
                    putString("last_url", url)
                    apply()
                }
                withContext(coroutineContext){
                    startActivity(Intent(applicationContext, WebViewActivity::class.java)
                        .putExtra("url", url)
                        .putExtra("organic", false))
                    finish()
                    Log.i("APP_CHECK", "check: ${model.campaign.value} + ${model.campaign_id.value} + ${model.adset_id.value} + $url")
                }
            }
        } else {
            startActivity(Intent(applicationContext, WebViewActivity::class.java).putExtra("organic", true))
            finish()
        }
    }

    private suspend fun createURL(sub: HashMap<Int, String>) : String = withContext(Dispatchers.IO){
        applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return@withContext ParamUtils().replace_param(
            sub,
            AppsflyerUtils().getAppsflyerId(applicationContext)
        )
    }
    private fun isInternetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true
            else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true
            else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return true
        }
        return false
    }
    override fun onStop() {
        super.onStop()
        if(model.finished.hasActiveObservers()) model.finished.removeObserver(observer)
    }
}