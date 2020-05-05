package tw.foxo.boise.upost.ui.home


import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import tw.foxo.boise.upost.R
import tw.foxo.boise.upost.networkHandler.getRequests
import tw.foxo.boise.upost.ui.home.adapter.BoardListAdapter

/**
 * A simple [Fragment] subclass.
 */
class HomeBoardList : Fragment() {
    lateinit var root:View ;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home_board_list, container, false)
        //val listView: ListView = root.findViewById(R.id.board_list_view)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_board_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val boardnet: getRequests = context?.let { getRequests(it) }
        //boardnet.getBoard()

        var array = arrayOf("Melbourne", "Vienna", "Vancouver", "Toronto", "Calgary", "Adelaide", "Perth", "Auckland", "Helsinki", "Hamburg", "Munich", "New York", "Sydney", "Paris", "Cape Town", "Barcelona", "London", "Bangkok")
        val adapter =  BoardListAdapter(activity as Activity,array)

        val listView: ListView = root.findViewById(R.id.board_list_view)
        listView.setAdapter(adapter)

    }
}
