package com.github.h3lp3rs.h3lp.forum

import com.github.h3lp3rs.h3lp.R
import com.github.h3lp3rs.h3lp.forum.ForumCategory.*
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.post_forum_row.view.*
import java.util.concurrent.CompletableFuture

/**
 * A forum post containing all the data.
 * The class has as attribute the instance of the forum that directly points on it
 * @param forum Forum at the path of the post
 * @param post Data of the post
 * @param replies Replies to this post
 */
class ForumPost(
    val forum : Forum,
    val post : ForumPostData,
    val replies : List<ForumPostData>,
) : Item<ViewHolder>() {

    /**
     * Adds a reply to the post
     * @param author The author of the reply
     * @param content The content of the reply
     * @return This post in the form of a future
     * WARNING: the returned post doesn't necessarily contain the reply since we have no way of
     * knowing when the database actually got the reply
     */
    fun reply(author : String, content : String) : CompletableFuture<ForumPost> {
        forum.child(post.repliesKey).newPost(author, content)
        return refresh()
    }

    /**
     * Refreshes the post if possible
     * @return this This post updated with potential replies from others
     * in the form of a future
     */
    private fun refresh() : CompletableFuture<ForumPost> {
        return forum.child(post.key).getPost(emptyList())
    }

    /**
     * Listens to this post and executes the lambda with the new data
     * as parameter when a change occurs (ie: reply added)
     * @param action The action taken when a change occurs
     */
    fun listen(action : (ForumPostData) -> Unit) {
        forum.child(post.key).listenToAll(action)
    }

    override fun bind(viewHolder : ViewHolder, position : Int) {
        viewHolder.itemView.question_post.text = post.content
        viewHolder.itemView.image_post.setImageResource(getImage())
        viewHolder.itemView.authpost_author.text = post.author
    }

    override fun getLayout() : Int {
        return R.layout.post_forum_row
    }

    private fun getImage() : Int {
        return when (forum.path[0]) {
            GENERAL.name -> R.drawable.ic_generalist
            CARDIOLOGY.name -> R.drawable.ic_cardiology
            TRAUMATOLOGY.name -> R.drawable.ic_traumatology
            PEDIATRY.name -> R.drawable.ic_pediatric
            NEUROLOGY.name -> R.drawable.ic_neurology
            GYNECOLOGY.name -> R.drawable.ic_gynecology
            else -> {
                R.drawable.ic_generalist
            }
        }
    }
}
