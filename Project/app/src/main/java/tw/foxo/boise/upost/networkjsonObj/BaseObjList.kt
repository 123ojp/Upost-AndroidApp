package tw.foxo.boise.upost.networkjsonObj

import org.json.JSONObject
import tw.foxo.boise.upost.networkHandler.BOARDID
import tw.foxo.boise.upost.networkHandler.BOARDNAME
import tw.foxo.boise.upost.networkHandler.ISSCHOOLBOARD

class BaseObjList {
    val boardObjBaseList = ArrayList<BoardObj>()

    init {
        var json = JSONObject()
        json.put(BOARDID,0)
        json.put(BOARDNAME,"NULL")
        json.put(ISSCHOOLBOARD,false)
        var initObj = BoardObj(json,false)
        boardObjBaseList.add(initObj)
    }



}