package tw.foxo.boise.upost.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import tw.foxo.boise.upost.R
import tw.foxo.boise.upost.eventBus.EVENT_BOARDCKICK
import tw.foxo.boise.upost.eventBus.EventMessage
import tw.foxo.boise.upost.lang.NOMORE_POST
import tw.foxo.boise.upost.lang.WAITING_LOADING
import tw.foxo.boise.upost.networkHandler.GetHandler
import tw.foxo.boise.upost.networkHandler.NETWORKERROR
import tw.foxo.boise.upost.networkHandler.P_POST
import tw.foxo.boise.upost.networkHandler.getRequests
import tw.foxo.boise.upost.networkjsonObj.POST_LASTOBJ
import tw.foxo.boise.upost.networkjsonObj.PostFgObj
import tw.foxo.boise.upost.ui.home.HomeViewPostHotFragment.OnListFragmentInteractionListener


import java.io.IOException

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [HomeViewPostNewFragment.OnListFragmentInteractionListener] interface.
 */
class HomeViewPostNewFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1
    private var board_id = 0
    private var ref_flag = 0
    private var listener: OnListFragmentInteractionListener? = null
    private var postList  = ArrayList<PostFgObj>()
    private lateinit var hotPostAdapter: MyHomeViewPostHotRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_homeviewpostnew_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyHomeViewPostHotRecyclerViewAdapter(postList, listener)
                hotPostAdapter = adapter as MyHomeViewPostHotRecyclerViewAdapter

            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    override fun onStart() {
        super.onStart()
        if (ref_flag == 0){
            updatePostList()
            ref_flag = 1
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        } else {
            //Toast.makeText(activity, "請勿重複註冊事件", Toast.LENGTH_SHORT).show()
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun event(eventBusMsg: EventMessage) {
        //Toast.makeText(activity,eventBusMsg.eventName,Toast.LENGTH_SHORT).show()
        if (eventBusMsg.eventName == EVENT_BOARDCKICK) {
            board_id = eventBusMsg.eventData as Int
            updatePostList()
        }
    }
    fun updatePostList() {
         var mHandler =  Handler(Looper.getMainLooper());
         var base_req = context?.let { getRequests(it).getNewPostReqBase(board_id) }
         val okHttpClient = OkHttpClient()
        postList  = ArrayList<PostFgObj>()
        val waitingLoad = POST_LASTOBJ
        waitingLoad.title = WAITING_LOADING
        postList.add(waitingLoad)

        hotPostAdapter.mValues = postList
        hotPostAdapter.notifyDataSetChanged()


         okHttpClient.newCall(base_req).enqueue(object : Callback {

             override fun onFailure(call: Call, e: IOException) {
                 mHandler.post {
                     Toast.makeText(activity, NETWORKERROR, Toast.LENGTH_SHORT).show()
                 }
             }

             override fun onResponse(call: Call, response: Response) {
                 val getHand = GetHandler(response)
                 val retj = getHand.jsonRes
                 postList  = ArrayList<PostFgObj>()

                 if (retj != null) {
                     if (retj.has(P_POST)) {

                         val postlist = retj.getJSONArray(P_POST)
                         for (i in 0 until (postlist.length()!!)) {
                             val item = postlist.getJSONObject(i)
                             val tmpPost = PostFgObj(item,true)
                             postList.add(tmpPost)

                         }
                     }

                 }
                 val footer = POST_LASTOBJ
                 footer.title = NOMORE_POST
                 postList.add(footer)

                 mHandler.post {
                     hotPostAdapter.mValues = postList
                     hotPostAdapter.notifyDataSetChanged()


                     //Toast.makeText(activity,getHand.message, Toast.LENGTH_SHORT).show()
                 }


             }

         })

    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */


    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            HomeViewPostNewFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
