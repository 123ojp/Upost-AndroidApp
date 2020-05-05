package tw.foxo.boise.upost.ui.home

import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tw.foxo.boise.upost.R


import tw.foxo.boise.upost.ui.home.HomeBoardListFgFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.fragment_homeboardlistfg.view.*
import tw.foxo.boise.upost.networkjsonObj.BoardObj

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyHomeBoardListFgRecyclerViewAdapter(
    var mValues: List<BoardObj>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyHomeBoardListFgRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as BoardObj
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_homeboardlistfg, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        if (!item.isClickAble){
            holder.mContentView.setTypeface(null, Typeface.BOLD);
        }
        holder.mContentView.text = item.board_name

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {

        val mContentView: TextView = mView.board_name

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
