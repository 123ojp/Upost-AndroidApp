package tw.foxo.boise.upost.networkjsonObj.postView

import org.json.JSONObject
import tw.foxo.boise.upost.networkHandler.*
import kotlin.properties.Delegates

class PostViewPost (initJson: JSONObject) {
    lateinit var author: String
    lateinit var author_sex: String
    var like by Delegates.notNull<Int>()
    lateinit var post_context : String
    lateinit var title : String
    lateinit var create_date: String
    lateinit var board_name:String
    var post_id by Delegates.notNull<Int>()
    var is_like by Delegates.notNull<Boolean>()
    var is_Author by Delegates.notNull<Boolean>()
    var commit_num by Delegates.notNull<Int>()
    lateinit var postAuthorInfo :String
    init {
        author = initJson.get(AUTHOR) as String
        author_sex = initJson.get(AUTHOR_SEX) as String
        like = initJson.get(LIKE) as Int
        post_context = initJson.get(POST_CONTEXT) as String
        title = initJson.get(TITLE) as String
        create_date = initJson.get(CREATEDATE) as String
        board_name = initJson.get(BOARDNAME) as String
        post_id = initJson.get(POSTID) as Int
        is_like = initJson.get(ISLIKE) as Boolean
        is_Author = initJson.get(ISAUTHOR) as Boolean
        commit_num = initJson.get(COMMITNUM) as Int
        postAuthorInfo = board_name + "・" + author
    }
}