package tw.foxo.boise.upost.ui.post

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
import tw.foxo.boise.upost.R
import tw.foxo.boise.upost.lang.*
import tw.foxo.boise.upost.networkHandler.*
import tw.foxo.boise.upost.networkjsonObj.POST_LASTOBJ

import tw.foxo.boise.upost.networkjsonObj.postView.*
import java.io.IOException


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [PostviewListviewFragment.OnListFragmentInteractionListener] interface.
 */
class PostviewListviewFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null
    private var post_id = 0
    private var postList =  ArrayList<PostViewFgObj>()
    private lateinit var postAdapter: MyPostviewListviewRecyclerViewAdapter
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
        val view = inflater.inflate(R.layout.fragment_postviewlistview_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyPostviewListviewRecyclerViewAdapter(postList, listener)
                postAdapter = adapter as MyPostviewListviewRecyclerViewAdapter
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
        post_id = arguments?.getInt(POSTID) ?:0
        loadPost()
    }
    fun loadPost(){
        var mHandler =  Handler(Looper.getMainLooper());
        var base_req = context?.let { getRequests(it).getAPostReqBase(post_id) }
        val okHttpClient = OkHttpClient()
        postList  = ArrayList<PostViewFgObj>()
        val waitingLoad = PostViewFgObj(WAITING_LOADING, TYPE_BANNER)
        postList.add(waitingLoad)
        postAdapter.mValues = postList
        postAdapter.notifyDataSetChanged()

        postList  = ArrayList<PostViewFgObj>()
        okHttpClient.newCall(base_req).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                mHandler.post {
                    Toast.makeText(activity, NETWORKERROR, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val getHand = GetHandler(response)
                val retj = getHand.jsonRes
                if (retj != null) {
                    if (retj.has(POST)) {
                        val postInfo = retj.getJSONObject(POST)
                        val tmpobj = PostViewFgObj(postInfo, TYPE_POST)
                        postList.add(tmpobj)
                    }
                    if (retj.has(COMMIT_TOP)) {
                        val postlist = retj.getJSONArray(COMMIT_TOP)
                        if (postlist.length()!! != 0){

                            val topCommitTitle = PostViewFgObj(TOPCOMMITTITLE, TYPE_BANNER)
                            postList.add(topCommitTitle)
                        }
                        for (i in 0 until (postlist.length()!!)) {
                            val item = postlist.getJSONObject(i)
                            val tmpCommit = PostViewFgObj(item, TYPE_COMMIT)
                            postList.add(tmpCommit)

                        }
                    }
                    if (retj.has(COMMIT)) {
                        val postlist = retj.getJSONArray(COMMIT)
                        if (postlist.length()!! != 0){
                            val NewcommitTitle = PostViewFgObj(NEWCOMMITTITLE, TYPE_BANNER)
                            postList.add(NewcommitTitle)
                        } else {
                            val noCommit = PostViewFgObj(NOCOMMIT, TYPE_BANNER)
                            postList.add(noCommit)
                        }
                        for (i in 0 until (postlist.length()!!)) {
                            val item = postlist.getJSONObject(i)
                            val tmpCommit = PostViewFgObj(item, TYPE_COMMIT)
                            postList.add(tmpCommit)
                        }
                        if(postlist.length()!! != 0){
                            val noMoreCommit = PostViewFgObj(NOMORE_COMMIT, TYPE_BANNER)
                            postList.add(noMoreCommit)
                        }
                    }
                }
                mHandler.post {
                    postAdapter.mValues = postList
                    postAdapter.notifyDataSetChanged()
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
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: PostViewFgObj)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            PostviewListviewFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
