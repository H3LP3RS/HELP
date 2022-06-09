package com.github.h3lp3rs.h3lp.forum

import androidx.test.core.app.ApplicationProvider
import com.github.h3lp3rs.h3lp.model.database.Databases.*
import com.github.h3lp3rs.h3lp.model.database.Databases.Companion.setDatabase
import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.forum.implementation.CachedForum
import com.github.h3lp3rs.h3lp.model.forum.ForumCategory
import com.github.h3lp3rs.h3lp.model.forum.data.Forum
import com.github.h3lp3rs.h3lp.view.signin.SignInActivity.Companion.globalContext
import com.github.h3lp3rs.h3lp.model.storage.Storages.Companion.resetStorage
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * This class tests the implementation of the CachedForum
 * @see CachedForum
 * @warning The following test functions use join() on futures.
 * Although not recommended usually, here our futures are based
 * on mock implementations that immediately return.
 */
class CacheForumTest {

    // Useful variables
    private lateinit var rawForum: Forum
    private lateinit var cachedForum: Forum

    @Before
    fun setup() {
        globalContext = ApplicationProvider.getApplicationContext()
        resetStorage()
        setDatabase(PREFERENCES, MockDatabase())
        ForumCategory.mockForum()
        rawForum = ForumCategory.root().child(CATEGORY.name)
        cachedForum = CachedForum(rawForum)
    }

    private fun resetOnlineForum() {
        ForumCategory.mockForum()
        rawForum = ForumCategory.root().child(CATEGORY.name)
        cachedForum = CachedForum(rawForum)
    }

    @Test
    fun newPostIsAddedToCache() {
        // Add a post
        val postOnline = cachedForum.newPost(AUTHOR, CONTENT, isPost = true).join()

        // Reset the forum, acts as a clear of the data
        resetOnlineForum()

        // Fetch the post through the cache
        val postCached = cachedForum.getPost(postOnline.post.key).join()

        assertEquals(CONTENT, postCached.post.content)
        assertEquals(AUTHOR, postCached.post.author)
    }

    @Test
    fun repliesAreAddedToCache() {
        // Add a post
        val postOnline = cachedForum.newPost(AUTHOR, CONTENT, isPost = true).join()

        // Add two replies
        postOnline.reply(AUTHOR, CONTENT).join()
        postOnline.reply(AUTHOR, CONTENT).join()

        // Reset the forum, acts as a clear of the data
        resetOnlineForum()

        // Fetch the post through the cache
        val postCached = cachedForum.getPost(postOnline.post.key).join()

        assertEquals(2, postCached.replies.size)
    }

    @Test
    fun postAndGetDoesNotDuplicate() {
        // Add a post
        val postOnline = cachedForum.newPost(AUTHOR, CONTENT, isPost = true).join()

        // Fetch it again
        cachedForum.getPost(postOnline.post.key).join()

        // Reset the forum, acts as a clear of the data
        resetOnlineForum()

        // Fetch the post through the cache
        val all = cachedForum.getAll().join()

        assertEquals(1, all.size)
        assertEquals(1, all[0].second.size)
    }

    @Test
    fun getAddsToCache() {
        // Add an external post
        val postOnline = rawForum.newPost(AUTHOR, CONTENT, isPost = true).join()

        // Fetch it through the cache
        cachedForum.getPost(postOnline.post.key).join()

        // Reset the forum, acts as a clear of the data
        resetOnlineForum()

        // Fetch the post through the cache
        val cachedPost = cachedForum.getPost(postOnline.post.key).join()

        assertEquals(CONTENT, cachedPost.post.content)
        assertEquals(AUTHOR, cachedPost.post.author)
    }

    @Test
    fun getAllAddsToCache() {
        // Add an external post and reply
        val postOnline = rawForum.newPost(AUTHOR, CONTENT, isPost = true).join()
        postOnline.reply(AUTHOR, CONTENT).join()

        // Fetch through cache the post
        cachedForum.getAll().join()

        // Reset the forum, acts as a clear of the data
        resetOnlineForum()

        // Fetch the post through the cache
        val all = cachedForum.getAll().join()

        assertEquals(1, all.size)
        assertEquals(1, all[0].second.size)
        assertEquals(1, all[0].second[0].replies.size)
    }

    @Test
    fun getAllFromDifferentCategoriesAddsToCache() {
        // Add external posts and two replies
        val rawRoot = rawForum.root()
        for (category in ForumCategory.values()) {
            val post = rawRoot.child(category.name).newPost(AUTHOR, CONTENT, isPost = true).join()
            post.reply(AUTHOR, CONTENT).join()
            post.reply(AUTHOR, CONTENT).join()
        }

        cachedForum.root().getAll().join()

        // Reset the forum, acts as a clear of the data
        resetOnlineForum()

        // Fetch the post through the cache
        val all = cachedForum.root().getAll().join()

        assertEquals(ForumCategory.values().size, all.size)
        for (c in all) {
            assertEquals(1, c.second.size)
            assertEquals(2, c.second[0].replies.size)
        }
    }

    @Test
    fun listenerAddsToCache() {
        var counter = 0
        cachedForum.root().listenToAll { counter++ }

        // Add an external post
        val post = rawForum.newPost(AUTHOR, CONTENT, isPost = true).join()

        // Reset the forum, acts as a clear of the data
        resetOnlineForum()

        val cachedPost = cachedForum.getPost(post.post.key).join()

        assertEquals(AUTHOR, cachedPost.post.author)
        assertEquals(CONTENT, cachedPost.post.content)
        assertEquals(1, counter)
    }

    @Test
    fun replyListenerAddsToCache() {
        var counter = 0
        cachedForum.root().listenToAll { counter++ }

        // Add an external post
        val post = rawForum.newPost(AUTHOR, CONTENT, isPost = true).join()
        post.reply(AUTHOR, CONTENT).join()

        // Reset the forum, acts as a clear of the data
        resetOnlineForum()

        val all = cachedForum.getAll().join()

        assertEquals(1, all.size)
        assertEquals(1, all[0].second.size)
        assertEquals(1, all[0].second[0].replies.size)
        assertEquals(2, counter)
    }

    companion object {
        private const val AUTHOR = "AUTHOR"
        private const val CONTENT = "CONTENT"
        private val CATEGORY = ForumCategory.GENERAL
    }
}