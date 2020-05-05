package tw.foxo.boise.upost.networkjsonObj.postView

import org.json.JSONObject
import tw.foxo.boise.upost.lang.COMMENT_DEL
import tw.foxo.boise.upost.networkHandler.*
import kotlin.properties.Delegates

class PostViewCommit(initJson: JSONObject) {
    lateinit var author: String
    lateinit var author_sex: String
    var like by Delegates.notNull<Int>()
    lateinit var commit_context : String
    lateinit var create_date: String
    var commit_id by Delegates.notNull<Int>()
    var is_like by Delegates.notNull<Boolean>()
    var is_del by Delegates.notNull<Boolean>()
    var is_Author by Delegates.notNull<Boolean>()
    init {
        is_del = initJson.get(ISDEL) as Boolean
        if (!is_del) {
            author = initJson.get(AUTHOR) as String
            author_sex = initJson.get(AUTHOR_SEX) as String
            like = initJson.get(LIKE) as Int
            commit_context = initJson.get(POST_CONTEXT) as String
            create_date = initJson.get(CREATEDATE) as String
            commit_id = initJson.get(COMMITID) as Int
            is_like = initJson.get(ISLIKE) as Boolean
            is_Author = initJson.get(ISAUTHOR) as Boolean
        } else {
            author = COMMENT_DEL
            author_sex = ""
            like = 0
            commit_context = ""
            create_date = ""
            commit_id = 0
            is_like = false
            is_Author = false
        }
    }
}