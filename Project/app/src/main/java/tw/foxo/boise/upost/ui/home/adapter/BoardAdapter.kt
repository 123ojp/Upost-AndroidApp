package tw.foxo.boise.upost.ui.home.adapter

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import tw.foxo.boise.upost.R

class BoardListAdapter(private val context: Activity, private val title: Array<String>)
    : ArrayAdapter<String>(context, R.layout.fragment_home_board_list_obj, title) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.fragment_home_board_list_obj, null, true)

        val titleText = rowView.findViewById(R.id.title) as TextView

        titleText.text = title[position]


        return rowView
    }
}