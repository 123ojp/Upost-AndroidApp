package tw.foxo.boise.upost.ui.home

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_home_post_list_footer.view.*
import tw.foxo.boise.upost.R


import tw.foxo.boise.upost.ui.home.HomeViewPostHotFragment.OnListFragmentInteractionListener



import kotlinx.android.synthetic.main.fragment_homeviewposthot.view.*
import tw.foxo.boise.upost.networkHandler.*
import tw.foxo.boise.upost.networkjsonObj.PostFgObj

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyHomeViewPostHotRecyclerViewAdapter(
    var mValues: List<PostFgObj>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyHomeViewPostHotRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as PostFgObj
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }
    val TYPE_FOOTER = 1 //說明是帶有Footer的

    val TYPE_NORMAL = 2
    override fun getItemViewType(position: Int): Int {
        if (position == itemCount -1){
            return TYPE_FOOTER;
        }
        //return TYPE_NORMAL
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TYPE_FOOTER){
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_home_post_list_footer, parent, false)
            return FooterViewHolder(view)
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_homeviewposthot, parent, false)
        return NormalViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        when (holder) {
            is FooterViewHolder -> {
                holder.mFooterText.text = item.title
            }
            is NormalViewHolder -> {

                holder.mPostAutInfo.text = item.postAuthorInfo
                holder.mPostCreDate.text = item.create_date
                holder.mPostTitle.text = item.title
                holder.mPostContent.text = item.post_context
                holder.mPostLikeNum.text = item.like.toString()
                holder.mPostCommtDisplay.text = "Commits: "+item.commit_num.toString()
                //holder.mContentView.text = item.content
                if (item.is_like){
                    holder.mPostIsLike.setImageResource(R.drawable.ic_favorite_black_24dp)
                } else {
                    holder.mPostIsLike.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                }
                if (item.author_sex == BOY) {
                    holder.mAuthIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(BOY_ICON))
                } else if (item.author_sex == GIRL){
                    holder.mAuthIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(GIRL_ICON))

                } else {
                    holder.mAuthIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(OTHER_ICON))

                }
                with(holder.mView) {
                    tag = item
                    setOnClickListener(mOnClickListener)
                }
            }
        }
    }

    override fun getItemCount(): Int = mValues.size
    inner class FooterViewHolder(override val mView: View) : ViewHolder(mView) {
        val mFooterText:TextView = mView.board_p_footer_text
        override fun toString(): String {
            return super.toString()
        }
    }
    inner class NormalViewHolder(override val mView: View) : ViewHolder(mView) {
        val mPostAutInfo: TextView = mView.postlist_authorinfo
        val mPostCreDate: TextView = mView.postlist_cdate
        val mPostTitle : TextView = mView.postlist_title
        val mPostContent: TextView = mView.postlist_content
        val mAuthIcon = mView.postlist_autIcon
        val mPostIsLike = mView.postlist_islike
        val mPostLikeNum = mView.postlist_likes
        val mPostCommtDisplay = mView.postlist_commit
        override fun toString(): String {
            return super.toString()
        }
    }
    open inner class ViewHolder(open val mView: View) : RecyclerView.ViewHolder(mView) {


        override fun toString(): String {
            return super.toString()
        }
    }
}
