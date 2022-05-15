package com.github.h3lp3rs.h3lp.forum

import android.os.Build
import androidx.annotation.RequiresApi
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.categoriesMap
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors.toList

/**
 * This implementation mocks a synchronous offline forum.
 * NOTE: This forum behaves according to the ForumProtocol.md limitations (subset of Forum
 * interface)
 * @param parent The wrapped forum we want to cacheThe parent forum, nullable if root
 * @param childPath The path of the child sub-forum or post
 * @param post The post associated to this path, if present
 */
class MockForum(
    private val parent: MockForum?,
    private val childPath: String,
    private var post: ForumPost?
) : Forum {
    // Path calculated by appending the parent path with the new relative child path
    override val path: Path =
        if (parent == null) {
            emptyList()
        } else parent.path + childPath

    // Syntactic sugar
    private val isRoot = parent == null

    // A list of all children categories or posts
    private val children: MutableList<MockForum> =
        if(isRoot) {
            val list = mutableListOf<MockForum>()
            for(e in categoriesMap) {
                list.add(MockForum(this, e.key, null))
            }
            list
        } else mutableListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun newPost(author: String, content: String): CompletableFuture<ForumPost> {
        // If it is a post, we consider this to be a reply
        post?.let {
            require(!isRoot) // Cannot be root (cf limitations)

            val replyData = ForumPostData(
                author,
                content,
                ZonedDateTime.now(),
                path.joinToString(separator = "/"), // Post pointer key
                "", // Empty since stored locally
                categoriesMap[path.first()]!! // Must be a predefined category (cf limitations)
            )
            post = ForumPost(this, it.post, it.replies + replyData)

            return CompletableFuture.completedFuture(post)
        }

        // If this is a category, we consider this a new post
        val postPointer = MockForum(this, children.size.toString(), null)
        children.add(postPointer)

        val postData = ForumPostData(
            author,
            content,
            ZonedDateTime.now(),
            postPointer.path.joinToString(separator = "/"),
            "", // Empty since stored locally
            categoriesMap[path.first()]!! // Must be a predefined category (cf limitations)
        )
        postPointer.post = ForumPost(postPointer, postData, emptyList())

        return CompletableFuture.completedFuture(postPointer.post)
    }

    override fun getPost(relativePath: Path): CompletableFuture<ForumPost> {
        TODO("Not yet implemented")
    }

    override fun getAll(): CompletableFuture<List<CategoryPosts>> {
        TODO("Not yet implemented")
    }

    override fun listenToAll(action: (ForumPostData) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun root(): Forum {
        return if(isRoot) this
        else parent!!.root()
    }

    override fun child(relativePath: Path): Forum {
        // Sanitize input path
        val path = relativePath.stream().filter {
            it.isNotEmpty()
        }.collect(toList())

        if (path.isEmpty()) return this
        else {
            val first = path.first()
            for (child in children) {
                if(child.childPath == first) {
                    return child.child(path.takeLast(path.size - 1))
                }
            }
            throw Error()
        }
    }

    override fun parent(): Forum {
        if(isRoot) throw Error()
        else return parent!!
    }

    override fun toString(): String {
        return explore(0)
    }

    private fun explore(depth: Int): String {
        val prefix = "\t".repeat(depth)
        val builder = StringBuilder()

        val path = path.joinToString(separator = "/")
        builder.append("$prefix Full path: $path\n")

        val isPost = post != null
        builder.append("$prefix Is post: $isPost\n")

        // It is a post => Stop exploring
        post?.let {
            val postData = it.post
            builder.append("$prefix \t$postData\n")

            val replies = it.replies
            for(r in replies) {
                builder.append("$prefix \t\t$r\n")
            }

            return builder.toString()
        }

        // It is not a post => Explore children
        for(child in children) {
            builder.append(child.explore(depth + 1))
        }

        return builder.toString()
    }
}