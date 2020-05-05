package tw.foxo.boise.upost.ui.register

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_confirm.*
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.*
import org.json.JSONObject
import tw.foxo.boise.upost.R
import tw.foxo.boise.upost.databaseHandler.SettingDbHelper
import tw.foxo.boise.upost.networkHandler.*
import java.io.IOException

class ConfirmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)
        confir_register.setOnClickListener({
            var token = confir_token.text.toString()
            var passwd = confir_passwd.text.toString()
            var check_sex = confir_sexragro.getCheckedRadioButtonId()
            if (check_sex<0 || token == "" || passwd == ""){
                Toast.makeText(this@ConfirmActivity, "Please Check all input", Toast.LENGTH_SHORT).show()
            } else {
                var sexselect = findViewById<RadioButton>(check_sex)
                var sex= sexselect.text.toString()

                var json = JSONObject()
                json.put(TOKEN, token)
                json.put(PASSWORD, passwd)
                json.put(SEX,sex)
                var mHandler = Handler(Looper.getMainLooper());

                var body: RequestBody = RequestBody.create(JSON, json.toString())
                var base_req = this?.let { it1 -> getRequests(it1).getRegCreateBase(body) }
                val okHttpClient = OkHttpClient()
                okHttpClient.newCall(base_req).enqueue(object : Callback {

                    override fun onFailure(call: Call, e: IOException) {
                        mHandler.post {
                            Toast.makeText(this@ConfirmActivity, NETWORKERROR, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val getHand = GetHandler(response)
                        val retj = getHand.jsonRes

                        mHandler.post {
                            if (retj != null && retj.has(MSG)) {
                                Toast.makeText(this@ConfirmActivity, retj.getString(MSG), Toast.LENGTH_SHORT)
                                    .show()
                            }
                            if (response.code() == 200) {

                                val resIntel: Intent = Intent()
                                val dbHelper = SettingDbHelper(this@ConfirmActivity)
                                if (retj != null) {
                                    dbHelper.setToken(retj.getString(TOKEN))
                                }
                                if (retj != null) {
                                    Toast.makeText(this@ConfirmActivity, "Welcome "+retj.getString(
                                        USERNAME), Toast.LENGTH_SHORT)
                                        .show()
                                }
                                setResult(Activity.RESULT_OK,resIntel)
                                finish()
                            }
                        }

                    }

                })
            }
        })

    }

}
