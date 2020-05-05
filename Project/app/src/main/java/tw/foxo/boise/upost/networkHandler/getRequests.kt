package tw.foxo.boise.upost.networkHandler

import android.content.Context
import android.widget.TextView
import okhttp3.*
import tw.foxo.boise.upost.MainActivity
import tw.foxo.boise.upost.R
import tw.foxo.boise.upost.databaseHandler.SettingDbHelper
import tw.foxo.boise.upost.ui.home.HomeBoardList
import java.io.IOException


class getRequests(context:Context) {
    val client = OkHttpClient()
    val dbHelper =  SettingDbHelper(context)

    val payload = "test payload"

    val okHttpClient = OkHttpClient()
    val baseRequest = Request.Builder()
    fun isLogin(builder: Request.Builder): Request.Builder {
        var token = dbHelper.getToken()
        if (token != null)
            builder.addHeader("Authorization", "Token " + token)
        return builder
    }
    fun getLoginReqBase(requestbody :RequestBody): Request? {
        var request = baseRequest
            .url(LOGIN_URL)
            .post(requestbody)
            .build()
        return request
    }
    fun getPostPostReqBase(board_id: Int,requestbody :RequestBody): Request? {
        val url = BOARDURL + board_id.toString() + "/" + C_COMMINT_END
        var builder:Request.Builder = baseRequest.url(url)
        builder = isLogin(builder)
        val request =builder.post(requestbody).build()
        return request
    }
    fun getEditPostReqBase(post_id: Int,requestbody :RequestBody): Request? {
        val url = POST_LIST_URL + post_id.toString() + "/" + EDIT
        var builder:Request.Builder = baseRequest.url(url)
        builder = isLogin(builder)
        val request =builder.post(requestbody).build()
        return request
    }
    fun getPostCommitReqBase(post_id: Int,requestbody :RequestBody): Request? {
        val url = POST_LIST_URL + post_id.toString() + "/" + C_COMMINT_END
        var builder:Request.Builder = baseRequest.url(url)
        builder = isLogin(builder)
        val request =builder.post(requestbody).build()
        return request
    }
    fun getEditCommitReqBase(commit_id: Int,requestbody :RequestBody): Request? {
        val url = COMMENT_HEAD + commit_id.toString() + "/" + EDIT
        var builder:Request.Builder = baseRequest.url(url)
        builder = isLogin(builder)
        val request =builder.post(requestbody).build()
        return request
    }
    fun getRegReqBase(requestbody :RequestBody): Request? {
        var request = baseRequest
            .url(REG_URL)
            .post(requestbody)
            .build()
        return request
    }
    fun getRegCreateBase(requestbody :RequestBody): Request? {
        var request = baseRequest
            .url(CREATE_URL)
            .post(requestbody)
            .build()
        return request
    }
    fun getUserInfoBase():Request? {
        var builder = baseRequest
            .url(USER_INFO_URL)
        builder = isLogin(builder)
        var request = builder.build()
        return request
    }
    fun getBoardReqBase(): Request? {
        var builder = baseRequest
            .url(BOARDURL)
        builder = isLogin(builder)
        var request = builder.build()
        return request
    }
    fun getHotPostReqBase(board_id:Int): Request? {
        var builder:Request.Builder
        if (board_id==0) {
            builder = baseRequest
                .url(POST_LIST_URL)
        } else {
            builder = baseRequest
                .url(BOARDURL+board_id.toString()+"/")
        }
        builder = isLogin(builder)
        var request = builder.build()
        return request
    }
    fun getNewPostReqBase(board_id:Int): Request? {

        var builder:Request.Builder
        if (board_id==0) {
            builder = baseRequest
                .url(POST_LIST_URL+SORT_BY_DATE)
        } else {
            builder = baseRequest
                .url(BOARDURL+board_id.toString()+"/"+SORT_BY_DATE)
        }
        builder = isLogin(builder)
        var request = builder.build()
        return request
    }
    fun getAPostReqBase(post_id:Int): Request? {

        var builder:Request.Builder

        builder = baseRequest
            .url(POST_LIST_URL + post_id.toString() + "/")

        builder = isLogin(builder)
        var request = builder.build()
        return request
    }
    fun getPostLikeorUnlikeBase(islike : Boolean,post_id:Int): Request? {

        var builder:Request.Builder
        var like_end = ""
        if (islike){
            like_end= E_UNLIKE
        } else {
            like_end = E_LIKE
        }
        builder = baseRequest
            .url(POST_LIST_URL + post_id.toString() + "/" + like_end)

        builder = isLogin(builder)
        var request = builder.build()
        return request
    }
    fun getLikeorUnlikeBase(islike : Boolean,commit_id:Int): Request? {

        var builder:Request.Builder
        var like_end = ""
        if (islike){
             like_end= E_UNLIKE
        } else {
             like_end = E_LIKE
        }
        builder = baseRequest
            .url(COMMENT_HEAD + commit_id.toString() + "/" + like_end)

        builder = isLogin(builder)
        var request = builder.build()
        return request
    }
    fun getDelPostBase(post_id:Int): Request? {

        var builder:Request.Builder

        builder = baseRequest
            .url(POST_LIST_URL + post_id.toString() + "/" + DEL)

        builder = isLogin(builder)
        var request = builder.build()
        return request
    }
    fun getDelCommentBase(commit_id:Int): Request? {

        var builder:Request.Builder

        builder = baseRequest
            .url(COMMENT_HEAD + commit_id.toString() + "/" + DEL)

        builder = isLogin(builder)
        var request = builder.build()
        return request
    }
    fun getBoard() {
        var request = baseRequest
        .url(BOARDURL)
        .build()
        var retString : String = ""
        okHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                retString = ""
            }

            override fun onResponse(call: Call, response: Response) {
                retString = response.body().toString();

            }

        })
    }
}
