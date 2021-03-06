package tw.foxo.boise.upost.networkHandler



import android.os.Handler
import kotlinx.android.synthetic.main.fragment_share.*
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import tw.foxo.boise.upost.databaseHandler.SettingDbHelper
import tw.foxo.boise.upost.lang.PLZ_LOGIN
import kotlin.String as String1


class GetHandler(response: Response){
    val responseText = response.body()?.string()
    var jsonRes : JSONObject? = null
    val resCode = response.code()
    var isError = false
    lateinit var message : String1
    init {
        try{
            jsonRes = JSONObject(responseText)
        } catch ( e : JSONException){
            isError = true
            message = NETWORKERROR
            jsonRes = JSONObject()
        }
        if (resCode > 299 ) {
            isError = true
            message = "Bad"
            if (resCode == 401){
                message = PLZ_LOGIN
            }

        } else {
            message = "Success"
        }
        if (!(jsonRes?.has(MSG))!!){
            jsonRes!!.put(MSG,message)

        }

    }



}