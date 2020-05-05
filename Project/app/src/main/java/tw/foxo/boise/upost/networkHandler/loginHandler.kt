package tw.foxo.boise.upost.networkHandler

import android.os.Handler
import kotlinx.android.synthetic.main.fragment_share.*
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import tw.foxo.boise.upost.databaseHandler.SettingDbHelper
import kotlin.String as String1


class LoginHandler(response: Response){
    val responseText = response.body()?.string()
    var jsonRes : JSONObject? = null
    val resCode = response.code()
    var isError = false
    var token= ""
    lateinit var message : String1
    init {
        try{
            jsonRes = JSONObject(responseText)
        } catch ( e : JSONException){
            isError = true
            message = NETWORKERROR
        }
        if (resCode > 299 ) {
            isError = true
            message = "Wrong Password"
        } else {
            token = jsonRes?.getString("token").toString()
            message = "Login Success"
        }

    }



}