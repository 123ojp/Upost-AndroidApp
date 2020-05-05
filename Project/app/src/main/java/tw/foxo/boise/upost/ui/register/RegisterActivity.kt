package tw.foxo.boise.upost.ui.register

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_share.*
import okhttp3.*
import org.json.JSONObject
import tw.foxo.boise.upost.R
import tw.foxo.boise.upost.databaseHandler.SettingDbHelper
import tw.foxo.boise.upost.networkHandler.*
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == Activity.RESULT_OK ) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        reg_hascofcode.setOnClickListener({
            val goIntent: Intent = Intent(this@RegisterActivity,ConfirmActivity::class.java);
            startActivityForResult(goIntent,12)
        })
        reg_register.setOnClickListener({
            var username = reg_username.text.toString()
            var email = reg_email.text.toString()
            var json = JSONObject()
            json.put(USERNAME,username)
            json.put(EMAIL,email)
            var mHandler =  Handler(Looper.getMainLooper());

            var body: RequestBody = RequestBody.create(JSON, json.toString())
            var base_req = this?.let { it1 -> getRequests(it1).getRegReqBase(body) }
            val okHttpClient = OkHttpClient()
            okHttpClient.newCall(base_req).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    mHandler.post {
                        Toast.makeText(this@RegisterActivity, NETWORKERROR, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val getHand = GetHandler(response)
                    val retj = getHand.jsonRes

                    mHandler.post {
                        if (retj != null) {
                            Toast.makeText(this@RegisterActivity,retj.getString(MSG), Toast.LENGTH_SHORT).show()
                        }
                        if(response.code() == 200){
                            val goIntent: Intent = Intent(this@RegisterActivity,ConfirmActivity::class.java);
                            startActivityForResult(goIntent,12)
                        }
                    }

                }

            })
        })
    }
}
