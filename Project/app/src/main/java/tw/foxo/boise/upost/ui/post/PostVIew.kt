package tw.foxo.boise.upost.ui.post

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.post_view_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import tw.foxo.boise.upost.R
import tw.foxo.boise.upost.debug.DebugToast
import tw.foxo.boise.upost.eventBus.*
import tw.foxo.boise.upost.networkHandler.POSTID
import tw.foxo.boise.upost.networkjsonObj.postView.ISPOST
import tw.foxo.boise.upost.networkjsonObj.postView.PostViewFgObj
import tw.foxo.boise.upost.ui.create.CreateCommitView

class PostVIew : AppCompatActivity() , PostviewListviewFragment.OnListFragmentInteractionListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.post_view_main)
        val bundle = Bundle()
        val post_id =  intent.getStringExtra(POSTID).toInt()
        bundle.putInt(POSTID,post_id)
        post_fragment.setArguments(bundle)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        } else {
            DebugToast.makeText(this, "請勿重複註冊事件", Toast.LENGTH_SHORT)
        }
        postview_commit.setOnClickListener({
            val intent = Intent(this, CreateCommitView::class.java)
            intent.putExtra(POSTID,post_id)
            intent.putExtra(ISPOST,true)
            startActivityForResult(intent,12)
        })

    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun event(eventBusMsg: EventMessage) {
        //Toast.makeText(activity,eventBusMsg.eventName,Toast.LENGTH_SHORT).show()
        if (eventBusMsg.eventName== EVENT_CLOSEPOST){
            finish()
        }
    }
    override fun onStart() {
        super.onStart()
    }
    override fun onListFragmentInteraction(item: PostViewFgObj) {
        //To change body of created functions use File | Settings | File Templates.
    }
}
