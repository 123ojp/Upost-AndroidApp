package tw.foxo.boise.upost.networkjsonObj

import org.json.JSONObject
import tw.foxo.boise.upost.networkHandler.*

import kotlin.properties.Delegates
val POST_LASTOBJ = PostFgObj(JSONObject("{\"post\":{\"author\":\"Some School\",\"author_sex\":\"female\",\"like\":10000,\"text\":\"test\",\"title\":\"No More Posts\",\"create_date\":\"2020-10-31T04:40:34Z\",\"board_name\":\"Some School Board\",\"post_id\":1,\"is_like\":false,\"commit_num\":5}}"),false)

class PostFgObj(initJson1: JSONObject,isClickAble:Boolean){

    lateinit var author: String
    lateinit var author_sex: String
    var like by Delegates.notNull<Int>()
    lateinit var post_context : String
    lateinit var title : String
    lateinit var create_date: String
    lateinit var board_name:String
    var post_id by Delegates.notNull<Int>()
    var is_like by Delegates.notNull<Boolean>()
    var commit_num by Delegates.notNull<Int>()
    lateinit var postAuthorInfo :String
    var isClickAble = isClickAble
    init {
        val initJson = initJson1.getJSONObject(POST)
        author = initJson.get(AUTHOR) as String
        author_sex = initJson.get(AUTHOR_SEX) as String
        like = initJson.get(LIKE) as Int
        post_context = initJson.get(POST_CONTEXT) as String
        title = initJson.get(TITLE) as String
        create_date = initJson.get(CREATEDATE) as String
        board_name = initJson.get(BOARDNAME) as String
        post_id = initJson.get(POSTID) as Int
        is_like = initJson.get(ISLIKE) as Boolean
        commit_num = initJson.get(COMMITNUM) as Int
        postAuthorInfo = board_name + "・" + author
    }


}
