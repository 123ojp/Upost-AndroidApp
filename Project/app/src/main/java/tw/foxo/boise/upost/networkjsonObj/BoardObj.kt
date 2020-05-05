package tw.foxo.boise.upost.networkjsonObj

import org.json.JSONObject
import tw.foxo.boise.upost.networkHandler.BOARDID
import tw.foxo.boise.upost.networkHandler.BOARDNAME
import tw.foxo.boise.upost.networkHandler.ISSCHOOLBOARD
import kotlin.properties.Delegates

class BoardObj(initJson: JSONObject,isClickAble:Boolean){
    var board_id by Delegates.notNull<Int>()
    var board_name: String
    var isSchoolBoard by Delegates.notNull<Boolean>()
    var isClickAble = isClickAble
    init {
        board_id = initJson.get(BOARDID) as Int
        board_name = initJson.get(BOARDNAME) as String
        isSchoolBoard = initJson.get(ISSCHOOLBOARD) as Boolean


    }

    override fun toString(): String {
        return board_name
    }


}
