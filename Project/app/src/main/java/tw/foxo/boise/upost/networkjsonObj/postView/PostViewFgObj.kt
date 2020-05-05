package tw.foxo.boise.upost.networkjsonObj.postView

import org.json.JSONObject
import kotlin.properties.Delegates

val TYPE_BANNER = 0
val TYPE_POST = 1
val TYPE_COMMIT = 2
val ISPOST = "ispost"
class PostViewFgObj( initData:Any, type1:Int) {
    lateinit var data:Any ;
    var type by Delegates.notNull<Int>();
    init {
        type = type1
        if (type == TYPE_BANNER){
            data = PostViewBanner(initData as String)
        } else if (type == TYPE_POST) {
            data = PostViewPost(initData as JSONObject)
        } else if (type == TYPE_COMMIT) {
            data = PostViewCommit(initData as JSONObject)
        }
    }

}