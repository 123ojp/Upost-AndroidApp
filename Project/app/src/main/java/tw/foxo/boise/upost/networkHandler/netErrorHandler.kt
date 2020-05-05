package tw.foxo.boise.upost.networkHandler

import android.widget.Toast
import okhttp3.Response

class netErrorHandler(response: Response){
    init {
        if (!response.isSuccessful) {
            //Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show()
        }
    }


}
