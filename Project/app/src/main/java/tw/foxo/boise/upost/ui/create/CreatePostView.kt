package tw.foxo.boise.upost.ui.create

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_create_commit_view.*
import kotlinx.android.synthetic.main.activity_create_post_view.*
import kotlinx.android.synthetic.main.app_bar_main.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import tw.foxo.boise.upost.R
import tw.foxo.boise.upost.debug.DebugToast
import tw.foxo.boise.upost.lang.*
import tw.foxo.boise.upost.networkHandler.*
import tw.foxo.boise.upost.networkjsonObj.BoardObj
import tw.foxo.boise.upost.networkjsonObj.postView.ISPOST
import java.io.IOException

class CreatePostView : AppCompatActivity() {
    var postToBoardID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post_view)
        val ispost = intent.getBooleanExtra(ISPOST,true)
        if (ispost){
            val org_title =  intent.getStringExtra(TITLE)
            val org_content = intent.getStringExtra(POST_CONTEXT)
            val post_id = intent.getIntExtra(POSTID,0)
            create_post_title.setText(org_title)
            create_post_text.setText(org_content)
            create_post_spinner.visibility = View.GONE
            create_post_btn.setOnClickListener({
                val post_content_str = create_post_text.text.toString()
                val post_title= create_post_title.text.toString()
                if (post_title != "" && post_content_str != "") {
                    editPost(post_id, post_title ,post_content_str)
                } else{
                    Toast.makeText(this@CreatePostView, NO_EMPTY, Toast.LENGTH_SHORT).show()
                }
            })

        } else {
            updataBoardSpinner()
            create_post_btn.setOnClickListener({
                val post_content_str = create_post_text.text.toString()
                val post_title= create_post_title.text.toString()
                if (post_title != "" && post_content_str != "") {
                    postnewPost(postToBoardID, post_title ,post_content_str)
                } else{
                    Toast.makeText(this@CreatePostView, NO_EMPTY, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    fun editPost(post_id:Int,post_title:String, post_content:String){

        var json = JSONObject()
        json.put(TITLE,post_title)
        json.put(POST_CONTEXT,post_content)
        var mHandler =  Handler(Looper.getMainLooper());
        var body: RequestBody = RequestBody.create(JSON, json.toString())
        var base_req = this@CreatePostView?.let { it1 -> getRequests(it1).getEditPostReqBase(post_id,body) }
        val okHttpClient = OkHttpClient()
        okHttpClient.newCall(base_req).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                mHandler.post {
                    Toast.makeText(this@CreatePostView, NETWORKERROR, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val getHand = GetHandler(response)
                val retj = getHand.jsonRes
                val message = retj?.getString(MSG)

                mHandler.post {
                    if (message != null){
                        if (getHand.isError){
                            Toast.makeText(this@CreatePostView, message, Toast.LENGTH_SHORT).show()
                        } else {
                            DebugToast.makeText(this@CreatePostView, message, Toast.LENGTH_SHORT)
                            finish()
                        }
                    }

                }

            }

        })
    }
    fun postnewPost(board_id:Int,post_title:String, post_content:String){

        var json = JSONObject()
        json.put(TITLE,post_title)
        json.put(POST_CONTEXT,post_content)
        var mHandler =  Handler(Looper.getMainLooper());

        var body: RequestBody = RequestBody.create(JSON, json.toString())
        var base_req = this@CreatePostView?.let { it1 -> getRequests(it1).getPostPostReqBase(board_id,body) }
        val okHttpClient = OkHttpClient()
        okHttpClient.newCall(base_req).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                mHandler.post {
                    Toast.makeText(this@CreatePostView, NETWORKERROR, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val getHand = GetHandler(response)
                val retj = getHand.jsonRes
                val message = retj?.getString(MSG)

                mHandler.post {
                    if (message != null){
                        if (getHand.isError){
                            Toast.makeText(this@CreatePostView, message, Toast.LENGTH_SHORT).show()
                        } else {
                            DebugToast.makeText(this@CreatePostView, message, Toast.LENGTH_SHORT)
                            finish()
                        }
                    }

                }

            }

        })
    }
    fun updataBoardSpinner(){
        var mHandler =  Handler(Looper.getMainLooper());
        var base_req = this@CreatePostView?.let { getRequests(it).getBoardReqBase() }
        val okHttpClient = OkHttpClient()
        var board_listView  = ArrayList<BoardObj>()
        okHttpClient.newCall(base_req).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                mHandler.post {
                    Toast.makeText(this@CreatePostView, NETWORKERROR, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val getHand = GetHandler(response)
                val retj = getHand.jsonRes
                if (retj != null) {
                    if (retj.has(B_MYSCHOOL)) {
                        val myschool = retj.getJSONArray(B_MYSCHOOL)
                        for (i in 0 until (myschool.length()!!)) {
                            val item = myschool.getJSONObject(i)
                            val tmpBoard = BoardObj(item, true)
                            board_listView.add(tmpBoard)

                        }
                    }
                    if (retj.has(B_BOARD)) {
                        val commonboard: JSONArray = retj.getJSONArray(B_BOARD)
                        for (i in 0 until (commonboard.length()!!)) {
                            val item = commonboard.getJSONObject(i)
                            val tmpBoard = BoardObj(item, true)
                            board_listView.add(tmpBoard)
                        }

                    }
                }
                val adapter:ArrayAdapter<BoardObj> = ArrayAdapter(this@CreatePostView, android.R.layout.simple_spinner_dropdown_item, board_listView)
                mHandler.post {
                    create_post_spinner.adapter = adapter
                    create_post_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                            postToBoardID = board_listView[pos].board_id
                            DebugToast.makeText(this@CreatePostView, "Now Board Select" + postToBoardID.toString(), Toast.LENGTH_SHORT)

                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {}
                    }
                }


            }

        })
    }
}
