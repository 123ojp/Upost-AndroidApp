package tw.foxo.boise.upost.ui.share

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_share.*
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import tw.foxo.boise.upost.R
import tw.foxo.boise.upost.databaseHandler.SettingDbHelper
import tw.foxo.boise.upost.eventBus.EVENT_ACCOUNTRELAOD
import tw.foxo.boise.upost.eventBus.EVENT_BOARDCKICK
import tw.foxo.boise.upost.eventBus.EventMessage
import tw.foxo.boise.upost.networkHandler.*
import tw.foxo.boise.upost.ui.register.RegisterActivity
import java.io.IOException

class ShareFragment : Fragment() {

    private lateinit var shareViewModel: ShareViewModel
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == Activity.RESULT_OK ) {
            val event = EventMessage(EVENT_ACCOUNTRELAOD,1)
            EventBus.getDefault().postSticky(event)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        shareViewModel =
            ViewModelProviders.of(this).get(ShareViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_share, container, false)
        val textView: TextView = root.findViewById(R.id.text_share)
        shareViewModel.text.observe(this, Observer {
            textView.text = it
        })
        root.findViewById<Button>(R.id.login_reg_btn).setOnClickListener({
            val intent = Intent(getActivity(), RegisterActivity::class.java)
            startActivityForResult(intent,12)
        })

        root.findViewById<Button>(R.id.login_btn).setOnClickListener({
            var username = login_username.text.toString()
            var password = login_password.text.toString()
            var json = JSONObject()
            json.put(USERNAME,username)
            json.put(PASSWORD,password)
            var mHandler =  Handler(Looper.getMainLooper());

            var body: RequestBody = RequestBody.create(JSON, json.toString())
            var base_req = context?.let { it1 -> getRequests(it1).getLoginReqBase(body) }
            val okHttpClient = OkHttpClient()
            okHttpClient.newCall(base_req).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {
                    mHandler.post {
                        Toast.makeText(activity,NETWORKERROR, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val loghand = LoginHandler(response)

                    mHandler.post {
                        Toast.makeText(activity,loghand.message, Toast.LENGTH_SHORT).show()
                    }
                    if (!loghand.isError){
                        val dbHelper = context?.let { it1 -> SettingDbHelper(it1) }
                        if (dbHelper != null) {
                            dbHelper.setToken(loghand.token)
                            mHandler.post {
                            //Toast.makeText(activity,dbHelper.getToken().toString(), Toast.LENGTH_SHORT).show()

                            }
                        }
                        //return
                        val event = EventMessage(EVENT_ACCOUNTRELAOD,1)
                        EventBus.getDefault().postSticky(event)
                    }

                }

            })

        })

        return root
    }


}