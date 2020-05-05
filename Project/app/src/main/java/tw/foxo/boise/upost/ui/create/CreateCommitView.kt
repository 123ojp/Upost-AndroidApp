package tw.foxo.boise.upost.ui.create

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_commit_view.*
import kotlinx.android.synthetic.main.fragment_homeviewpostnew.*
import kotlinx.android.synthetic.main.fragment_share.*
import kotlinx.android.synthetic.main.postview_fg_comment.*
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import tw.foxo.boise.upost.R
import tw.foxo.boise.upost.databaseHandler.SettingDbHelper
import tw.foxo.boise.upost.debug.DebugToast
import tw.foxo.boise.upost.eventBus.*
import tw.foxo.boise.upost.lang.NO_EMPTY
import tw.foxo.boise.upost.networkHandler.*
import tw.foxo.boise.upost.networkjsonObj.postView.ISPOST
import java.io.IOException

class CreateCommitView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_commit_view)
        val ispost = intent.getBooleanExtra(ISPOST,true)
        if (ispost){
            val post_id = intent.getIntExtra(POSTID,0)

            create_comment_btn.setOnClickListener({
                if (create_comment_text.text.toString() != "") {
                    val commit_content_str = create_comment_text.text.toString()
                    postnewComment(post_id, commit_content_str)
                } else{
                    Toast.makeText(this@CreateCommitView, NO_EMPTY, Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            val commit_id = intent.getIntExtra(COMMITID,0)
            val orgial_commit = intent.getStringExtra(COMMIT_CONTEXT)
            create_comment_text.setText(orgial_commit)
            create_comment_btn.setOnClickListener({
                if (create_comment_text.text.toString() != "") {
                    val commit_content_str = create_comment_text.text.toString()
                    editComment(commit_id, commit_content_str)
                } else{
                    Toast.makeText(this@CreateCommitView, NO_EMPTY, Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    fun editComment(commit_id:Int,commit_content:String){
        var json = JSONObject()
        json.put(COMMIT_CONTEXT,commit_content)
        var mHandler =  Handler(Looper.getMainLooper());

        var body: RequestBody = RequestBody.create(JSON, json.toString())
        var base_req = this@CreateCommitView?.let { it1 -> getRequests(it1).getEditCommitReqBase(commit_id,body) }
        val okHttpClient = OkHttpClient()
        okHttpClient.newCall(base_req).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                mHandler.post {
                    Toast.makeText(this@CreateCommitView, NETWORKERROR, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val getHand = GetHandler(response)
                val retj = getHand.jsonRes
                val message = retj?.getString(MSG)

                mHandler.post {
                    if (message != null){
                        if (getHand.isError){
                            Toast.makeText(this@CreateCommitView, message, Toast.LENGTH_SHORT).show()
                        } else {
                            DebugToast.makeText(this@CreateCommitView, message, Toast.LENGTH_SHORT)
                            finish()
                        }
                    }

                }

            }

        })
    }
    fun postnewComment(post_id:Int,commit_content:String){
        var json = JSONObject()
        json.put(COMMIT_CONTEXT,commit_content)
        var mHandler =  Handler(Looper.getMainLooper());

        var body: RequestBody = RequestBody.create(JSON, json.toString())
        var base_req = this@CreateCommitView?.let { it1 -> getRequests(it1).getPostCommitReqBase(post_id,body) }
        val okHttpClient = OkHttpClient()
        okHttpClient.newCall(base_req).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                mHandler.post {
                    Toast.makeText(this@CreateCommitView, NETWORKERROR, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val getHand = GetHandler(response)
                val retj = getHand.jsonRes
                val message = retj?.getString(MSG)

                mHandler.post {
                    if (message != null){
                        if (getHand.isError){
                            Toast.makeText(this@CreateCommitView, message, Toast.LENGTH_SHORT).show()
                        } else {
                            DebugToast.makeText(this@CreateCommitView, message, Toast.LENGTH_SHORT)
                            finish()
                        }
                    }

                }

            }

        })
    }
}
