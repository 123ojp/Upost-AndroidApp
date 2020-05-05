package tw.foxo.boise.upost.ui.post

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.fragment_homeviewpostnew.view.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.postview_fg_banner.view.*
import kotlinx.android.synthetic.main.postview_fg_comment.view.*
import kotlinx.android.synthetic.main.postview_fg_post.view.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import org.greenrobot.eventbus.EventBus
import tw.foxo.boise.upost.R
import tw.foxo.boise.upost.debug.DebugToast
import tw.foxo.boise.upost.eventBus.EVENT_CLOSEPOST
import tw.foxo.boise.upost.eventBus.EVENT_EDITCOMMIIT
import tw.foxo.boise.upost.eventBus.EVENT_EDITPOST
import tw.foxo.boise.upost.eventBus.EventMessage
import tw.foxo.boise.upost.lang.*
import tw.foxo.boise.upost.networkHandler.*
import tw.foxo.boise.upost.networkjsonObj.postView.*
import tw.foxo.boise.upost.ui.create.CreateCommitView
import tw.foxo.boise.upost.ui.create.CreatePostView


import tw.foxo.boise.upost.ui.post.PostviewListviewFragment.OnListFragmentInteractionListener
import java.io.IOException

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyPostviewListviewRecyclerViewAdapter(
    var mValues: List<PostViewFgObj>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyPostviewListviewRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as PostViewFgObj
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TYPE_COMMIT){
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.postview_fg_comment, parent, false)
            return CommitViewHolder(view)
        } else if (viewType == TYPE_POST){
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.postview_fg_post, parent, false)
            return PostViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.postview_fg_banner, parent, false)
            return BannerViewHolder(view)
        }
    }
    override fun getItemViewType(position: Int): Int {
        val item = mValues[position]
        return item.type
        //return super.getItemViewType(position)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        when (holder) {
            is CommitViewHolder -> {
                val commentObj = item.data as PostViewCommit
                holder.mAuthorText.text = commentObj.author
                holder.mCDateText.text = commentObj.create_date
                holder.mContentText.text = commentObj.commit_context
                if (commentObj.author_sex == BOY) {
                    holder.mAuthIcon.backgroundTintList = ColorStateList.valueOf(
                        Color.parseColor(
                            BOY_ICON
                        )
                    )
                } else if (commentObj.author_sex == GIRL) {
                    holder.mAuthIcon.backgroundTintList = ColorStateList.valueOf(
                        Color.parseColor(
                            GIRL_ICON
                        )
                    )
                } else {
                    holder.mAuthIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(
                        OTHER_ICON
                    ))

                }
                if (commentObj.is_del){
                    holder.mLikeBtn.visibility  = View.GONE
                    holder.mContentText.visibility  = View.GONE
                } else {
                    holder.mLikeBtn.text = commentObj.like.toString()
                    var likeicon_fullid = R.drawable.ic_favorite_border_black_24dp
                    if (commentObj.is_like) {
                        likeicon_fullid = R.drawable.ic_favorite_black_24dp
                        holder.mLikeBtn.setCompoundDrawablesWithIntrinsicBounds(likeicon_fullid,0,0,0)
                    }
                    if (commentObj.is_Author){
                        holder.mLikeBtn.setCompoundDrawablesWithIntrinsicBounds(likeicon_fullid,0,R.drawable.ic_more_vert_black_24dp,0)

                        holder.mLikeBtn.setOnClickListener({
                            val popupMenu: PopupMenu = PopupMenu(holder.mView.context,holder.mLikeBtn)
                            popupMenu.menuInflater.inflate(R.menu.postview_comment_option,popupMenu.menu)
                            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                                when(item.itemId) {
                                    R.id.postview_commit_like -> {
                                        likeComment(commentObj,holder)
                                        Toast.makeText(holder.mView.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                                    }
                                    R.id.postview_commit_delete -> {
                                        delCommentDialog(commentObj,holder)
                                        Toast.makeText(holder.mView.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                                    }
                                    R.id.postview_commit_edit -> {
                                        editComment(commentObj,holder)
                                        Toast.makeText(holder.mView.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                                    }
                                }
                                true
                            })
                            popupMenu.show()

                        })

                    } else {
                        //if not author
                        holder.mLikeBtn.setOnClickListener({
                            likeComment(commentObj,holder)
                        })
                    }
                }
            }
            is PostViewHolder -> {
                val postObj = item.data as PostViewPost
                holder.mAuthorText.text = postObj.author
                holder.mTitle.text = postObj.title
                holder.mCDateText.text = postObj.create_date
                holder.mContentText.text = postObj.post_context
                holder.mCommentNum.text = "Commits: "+postObj.commit_num.toString()
                if (postObj.author_sex == BOY) {
                    holder.mAuthIcon.backgroundTintList = ColorStateList.valueOf(
                        Color.parseColor(
                            BOY_ICON
                        )
                    )
                } else if (postObj.author_sex == GIRL) {
                    holder.mAuthIcon.backgroundTintList = ColorStateList.valueOf(
                        Color.parseColor(
                            GIRL_ICON
                        )
                    )
                } else {
                    holder.mAuthIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(
                        OTHER_ICON
                    ))

                }

                holder.mLikeBtn.text = postObj.like.toString()
                var likeicon_fullid = R.drawable.ic_favorite_border_black_24dp
                if (postObj.is_like) {
                    likeicon_fullid = R.drawable.ic_favorite_black_24dp
                    holder.mLikeBtn.setCompoundDrawablesWithIntrinsicBounds(likeicon_fullid,0,0,0)
                }
                holder.mLikeBtn.setOnClickListener({
                    likePost(postObj,holder)
                })
                if (postObj.is_Author){
                    holder.mAuthorEdit.setOnClickListener({
                        val popupMenu: PopupMenu = PopupMenu(holder.mView.context,holder.mAuthorEdit)
                        popupMenu.menuInflater.inflate(R.menu.postview_post_author_option,popupMenu.menu)
                        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                            when(item.itemId) {
                                R.id.postview_post_edit -> {
                                    editPost(postObj,holder)
                                    DebugToast.makeText(holder.mView.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT)
                                }
                                R.id.postview_post_delete -> {
                                    delPostDialog(postObj,holder)
                                    DebugToast.makeText(holder.mView.context, "You Clicked : " + item.title, Toast.LENGTH_SHORT)
                                }
                            }
                            true
                        })
                        popupMenu.show()

                    })

                } else {
                    //if not author
                    holder.mAuthorEdit.visibility = View.GONE

                }

            }
            is BannerViewHolder -> {
                val bannerObj = item.data as PostViewBanner
                holder.mFooterText.text = bannerObj.title
            }
        }


        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size
    inner class CommitViewHolder(override val mView: View) : MyPostviewListviewRecyclerViewAdapter.ViewHolder(mView) {
        val mAuthorText:TextView = mView.comment_authorinfo
        val mCDateText:TextView = mView.comment_cdate
        val mAuthIcon = mView.comment_autIcon
        val mLikeBtn =  mView.comment_like
        val mContentText = mView.comment_content
        override fun toString(): String {
            return super.toString()
        }
    }
    inner class PostViewHolder(override val mView: View) : MyPostviewListviewRecyclerViewAdapter.ViewHolder(mView) {
        val mAuthorText:TextView = mView.post_authorinfo
        val mCDateText:TextView = mView.post_cdate
        val mAuthIcon = mView.post_autIcon
        val mLikeBtn =  mView.post_like
        val mContentText = mView.post_content
        val mTitle = mView.post_title
        val mAuthorEdit = mView.post_author_edit
        val mCommentNum = mView.postlist_post_commitnum
        override fun toString(): String {
            return super.toString()
        }
    }
    inner class BannerViewHolder(override val mView: View) : MyPostviewListviewRecyclerViewAdapter.ViewHolder(mView) {
        val mFooterText: TextView = mView.postview_footer_text
        override fun toString(): String {
            return super.toString()
        }
    }
    open inner class ViewHolder(open val mView: View) : RecyclerView.ViewHolder(mView) {


        override fun toString(): String {
            return super.toString()
        }
    }
    fun delPostDialog(post:PostViewPost,holder: PostViewHolder){
        val builder = AlertDialog.Builder(holder.mView.context)
        builder.setTitle(DELETE_CONFIREM)
        builder.setMessage(DELETE_CONFIREM_MSG)
        builder.setPositiveButton(YES,{ dialog, whichButton ->
            delPost(post,holder)
        })

        builder.setNegativeButton(NO, { dialog, whichButton ->

        })
        builder.create().show()


    }
    fun delPost(post:PostViewPost,holder: PostViewHolder){



        var mHandler =  Handler(Looper.getMainLooper())
        var base_req =  holder.mView.context.let { getRequests(it).getDelPostBase(post.post_id) }
        val okHttpClient = OkHttpClient()


        okHttpClient.newCall(base_req).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                mHandler.post {
                    Toast.makeText(holder.mView.context, NETWORKERROR, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val getHand = GetHandler(response)
                val retj = getHand.jsonRes
                val message = retj?.getString(MSG)

                mHandler.post {
                    if (message != null){
                        if (getHand.isError){
                            Toast.makeText(holder.mView.context, message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(holder.mView.context, DELETE_SUCC, Toast.LENGTH_SHORT).show()

                            DebugToast.makeText(holder.mView.context, message, Toast.LENGTH_SHORT)
                        }
                        val event = EventMessage(EVENT_CLOSEPOST,0)
                        EventBus.getDefault().postSticky(event)
                    }

                }

            }

        })
    }
    fun delCommentDialog(comment:PostViewCommit,holder: CommitViewHolder){
        val builder = AlertDialog.Builder(holder.mView.context)
        builder.setTitle(DELETE_CONFIREM)
        builder.setMessage(DELETE_CONFIREM_MSG)
        builder.setPositiveButton(YES,{ dialog, whichButton ->
            delComment(comment,holder)
        })

        builder.setNegativeButton(NO, { dialog, whichButton ->

        })
        builder.create().show()


    }
    fun delComment(commit:PostViewCommit,holder: CommitViewHolder){


        holder.mAuthorText.text = COMMENT_DEL
        holder.mAuthIcon.backgroundTintList = ColorStateList.valueOf(Color.parseColor(
            OTHER_ICON
        ))
        holder.mCDateText.text = ""
        holder.mLikeBtn.visibility  = View.GONE
        holder.mContentText.visibility  = View.GONE

        var mHandler =  Handler(Looper.getMainLooper())
        var base_req =  holder.mView.context.let { getRequests(it).getDelCommentBase(commit.commit_id) }
        val okHttpClient = OkHttpClient()


        okHttpClient.newCall(base_req).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                mHandler.post {
                    Toast.makeText(holder.mView.context, NETWORKERROR, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val getHand = GetHandler(response)
                val retj = getHand.jsonRes
                val message = retj?.getString(MSG)

                mHandler.post {
                    if (message != null){
                        if (getHand.isError){
                            Toast.makeText(holder.mView.context, message, Toast.LENGTH_SHORT).show()
                        } else {
                            DebugToast.makeText(holder.mView.context, message, Toast.LENGTH_SHORT)
                        }
                    }

                }

            }

        })
    }
    fun likePost(post:PostViewPost,holder: PostViewHolder){

        var mHandler =  Handler(Looper.getMainLooper())
        var base_req =  holder.mView.context.let { getRequests(it).getPostLikeorUnlikeBase(post.is_like,post.post_id) }
        val okHttpClient = OkHttpClient()

        var like_option_id = 0

        if (post.is_like){
            post.is_like = false
            post.like -= 1

            holder.mLikeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_black_24dp,0,0,0)

        } else {
            post.like += 1
            post.is_like = true

            holder.mLikeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_black_24dp,0,0,0)

        }
        holder.mLikeBtn.text = post.like.toString()

        okHttpClient.newCall(base_req).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                mHandler.post {
                    Toast.makeText(holder.mView.context, NETWORKERROR, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val getHand = GetHandler(response)
                val retj = getHand.jsonRes
                val message = retj?.getString(MSG)

                mHandler.post {
                    if (message != null){
                        if (getHand.isError){
                            Toast.makeText(holder.mView.context, message, Toast.LENGTH_SHORT).show()
                        } else {
                            DebugToast.makeText(holder.mView.context, message, Toast.LENGTH_SHORT)
                        }
                    }

                }

            }

        })
    }
    fun editPost(post:PostViewPost,holder: PostViewHolder){
        val context = holder.mView.context
        val intent = Intent(context, CreatePostView::class.java)
        intent.putExtra(POSTID,post.post_id)
        intent.putExtra(TITLE,post.title)
        intent.putExtra(POST_CONTEXT,post.post_context)
        intent.putExtra(ISPOST,true)
        startActivity(context,intent, Bundle())
        //EventBus.getDefault().postSticky(event)

    }
    fun editComment(commit:PostViewCommit,holder: CommitViewHolder){
        val context = holder.mView.context
        val intent = Intent(context, CreateCommitView::class.java)
        intent.putExtra(COMMITID,commit.commit_id)
        intent.putExtra(COMMIT_CONTEXT,commit.commit_context)
        intent.putExtra(ISPOST,false)
        startActivity(context,intent, Bundle())
        //EventBus.getDefault().postSticky(event)

    }
    fun likeComment(commit:PostViewCommit,holder: CommitViewHolder){

        var mHandler =  Handler(Looper.getMainLooper())
        var base_req =  holder.mView.context.let { getRequests(it).getLikeorUnlikeBase(commit.is_like,commit.commit_id) }
        val okHttpClient = OkHttpClient()

        var like_option_id = 0
        if (commit.is_Author){
            like_option_id = R.drawable.ic_more_vert_black_24dp
        }
        if (commit.is_like){
            commit.is_like = false
            commit.like -= 1

            holder.mLikeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_border_black_24dp,0,like_option_id,0)

        } else {
            commit.like += 1
            commit.is_like = true

            holder.mLikeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite_black_24dp,0,like_option_id,0)

        }
        holder.mLikeBtn.text = commit.like.toString()

        okHttpClient.newCall(base_req).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                mHandler.post {
                    Toast.makeText(holder.mView.context, NETWORKERROR, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val getHand = GetHandler(response)
                val retj = getHand.jsonRes
                val message = retj?.getString(MSG)

                mHandler.post {
                    if (message != null){
                        if (getHand.isError){
                            Toast.makeText(holder.mView.context, message, Toast.LENGTH_SHORT).show()
                        } else {
                            DebugToast.makeText(holder.mView.context, message, Toast.LENGTH_SHORT)
                        }
                    }

                }

            }

        })
    }
}
