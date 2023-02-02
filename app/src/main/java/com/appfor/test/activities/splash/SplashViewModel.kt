package com.appfor.test.activities.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.appfor.test.components.url.DeeplinkChecker
import com.appfor.test.utils.Constants
import com.onesignal.OneSignal
import kotlinx.coroutines.*
import kotlin.coroutines.resume
import android.util.Log


class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private var af_campaign: String? = null
    //
    private var af_campaign_id: String? = null
    //
    val campaign_id: MutableLiveData<String?> = MutableLiveData()
    val adset_id: MutableLiveData<String> = MutableLiveData()

    //
    val finished: MutableLiveData<Boolean> = MutableLiveData()
    val campaign: MutableLiveData<HashMap<Int, String>> = MutableLiveData()

    fun initViewModel() {
        viewModelScope.launch {
            initialOneSignalNotification()
            getCampaign()
            when {
                af_campaign != null -> {
                    saveData(af_campaign!!)
                    finished.value = true
                }
            }
            finished.value = false
        }
    }

    private suspend fun saveData(_campaign: String) {
        campaign.value = DeeplinkChecker().getSubs(_campaign)
        campaign_id.value = af_campaign_id
    }


    private fun initialOneSignalNotification() {

        OneSignal.initWithContext(getApplication())
        OneSignal.setAppId(Constants.ONE_SIGNAL_ID)

    }

    private suspend fun getCampaign() = suspendCancellableCoroutine<Unit> {

        if (af_campaign != null) {
            it.resume(Unit)
            return@suspendCancellableCoroutine
        }
        val conversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(conversionData: MutableMap<String, Any>) {
                it.resume(Unit)
                if (conversionData["campaign"] != null)
                    af_campaign = conversionData["campaign"].toString()
                Log.d("LOG_APP", af_campaign.toString())
            }

            override fun onAppOpenAttribution(attributionData: MutableMap<String, String>?) {
                it.resume(Unit)
            }

            override fun onAttributionFailure(errorMessage: String?) {
                it.resume(Unit)
            }

            override fun onConversionDataFail(errorMessage: String?) {
                it.resume(Unit)
            }
        }
        AppsFlyerLib.getInstance()
            .init(Constants.APPSFLYER, conversionListener, getApplication())
        AppsFlyerLib.getInstance().start(getApplication())

    }
}