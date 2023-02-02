package com.appfor.test.components.appsflyer

import android.content.Context
import com.appsflyer.AppsFlyerLib
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppsflyerUtils {
    suspend fun getAppsflyerId(context: Context) : String = withContext(Dispatchers.IO){
        return@withContext AppsFlyerLib.getInstance().getAppsFlyerUID(context).toString()
    }
}