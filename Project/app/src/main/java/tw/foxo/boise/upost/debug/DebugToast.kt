package tw.foxo.boise.upost.debug


import android.content.Context
import android.widget.Toast
import tw.foxo.boise.upost.BuildConfig

object DebugToast{
    fun makeText(context: Context,message:String,int: Int){
        if(BuildConfig.IS_DEBUG) {
            Toast.makeText(context, message, int).show()
        }
    }
}
