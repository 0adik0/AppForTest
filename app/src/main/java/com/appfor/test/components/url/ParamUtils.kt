package com.appfor.test.components.url

import com.appfor.test.components.url.URLConstants.APPSFLYER_ID
import com.appfor.test.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ParamUtils {
    suspend fun replace_param(sub: HashMap<Int, String>, appsflyer_id: String) : String = withContext(Dispatchers.IO){
        var url = generateUrl()
        with(url){
            if(contains(APPSFLYER_ID)) url = url.replace(APPSFLYER_ID, appsflyer_id)
        }
        return@withContext url
    }
    private fun generateUrl() = "${Constants.URL}?sub_id_1=${Constants.APPSFLYER}&sub_id_2=$APPSFLYER_ID"
}