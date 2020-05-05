package tw.foxo.boise.upost.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_homeboardlistfg_list.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import tw.foxo.boise.upost.R
import tw.foxo.boise.upost.databaseHandler.SettingDbHelper
import tw.foxo.boise.upost.lang.ALLPOSTS
import tw.foxo.boise.upost.lang.BOARD_EN
import tw.foxo.boise.upost.lang.MYSCHOOL
import tw.foxo.boise.upost.lang.SCHOOLS
import tw.foxo.boise.upost.networkHandler.*
import tw.foxo.boise.upost.networkjsonObj.BaseObjList
import tw.foxo.boise.upost.networkjsonObj.BoardObj

import java.io.IOException

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [HomeBoardListFgFragment.OnListFragmentInteractionListener] interface.
 */
class HomeBoardListFgFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null
    private var board_listView  = ArrayList<BoardObj>()
    private lateinit var list_adapter :MyHomeBoardListFgRecyclerViewAdapter

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
        val view = inflater.inflate(R.layout.fragment_homeboardlistfg_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }

                adapter = MyHomeBoardListFgRecyclerViewAdapter(board_listView, listener)
                list_adapter = adapter as MyHomeBoardListFgRecyclerViewAdapter
                //MutableList
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
        var mHandler =  Handler(Looper.getMainLooper());
        var base_req = context?.let { getRequests(it).getBoardReqBase() }
        val okHttpClient = OkHttpClient()
        board_listView  = ArrayList<BoardObj>()
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
                    board_listView.add(BoardObj(JSONObject()
                        .put(BOARDID,0)
                        .put(BOARDNAME, ALLPOSTS)
                        .put(ISSCHOOLBOARD,false),true
                    ))
                    if (retj.has(B_MYSCHOOL)){
                        board_listView.add(BoardObj(JSONObject()
                            .put(BOARDID,-1)
                            .put(BOARDNAME, MYSCHOOL)
                            .put(ISSCHOOLBOARD,false),false
                            ))
                        val myschool = retj.getJSONArray(B_MYSCHOOL)
                        for (i in 0 until (myschool.length()!!)) {
                                val item = myschool.getJSONObject(i)
                                val tmpBoard = BoardObj(item,true)
                                board_listView.add(tmpBoard)

                            }
                    }
                    if (retj.has(B_BOARD)) {
                        board_listView.add(BoardObj(JSONObject()
                            .put(BOARDID,-1)
                            .put(BOARDNAME, BOARD_EN)
                            .put(ISSCHOOLBOARD,false),false
                        ))
                        val commonboard: JSONArray = retj.getJSONArray(B_BOARD)
                        for (i in 0 until (commonboard.length()!!)) {
                            val item = commonboard.getJSONObject(i)
                            val tmpBoard = BoardObj(item,true)
                            board_listView.add(tmpBoard)
                        }

                    }
                    if (retj.has(B_SCHOOL)) {
                        board_listView.add(BoardObj(JSONObject()
                            .put(BOARDID,-1)
                            .put(BOARDNAME, SCHOOLS)
                            .put(ISSCHOOLBOARD,false),false
                        ))
                        val schoolboard = retj.getJSONArray(B_SCHOOL)
                        for (i in 0 until (schoolboard.length()!!)) {
                            val item = schoolboard.getJSONObject(i)
                            val tmpBoard = BoardObj(item,true)
                            board_listView.add(tmpBoard)

                        }
                    }


                }

                mHandler.post {
                    list_adapter.mValues = board_listView
                    list_adapter.notifyDataSetChanged()


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
        fun onListFragmentInteraction(item: BoardObj?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            HomeBoardListFgFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
