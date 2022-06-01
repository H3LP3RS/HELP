package com.github.h3lp3rs.h3lp.forum

import com.github.h3lp3rs.h3lp.model.forum.data.Forum
import com.github.h3lp3rs.h3lp.model.forum.ForumCategory.*
import com.github.h3lp3rs.h3lp.model.forum.ForumCategory.Companion.mockForum
import com.github.h3lp3rs.h3lp.model.forum.ForumCategory.Companion.root
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit.*
import kotlin.test.*

class MockForumTest {

    // Useful variables
    private lateinit var forum: Forum

    @Before
    fun setup() {
        mockForum()
        forum = root().child(GENERAL.name)
    }

    @Test
    fun newPostReturnsPost() {
        forum.newPost(AUTHOR, CONTENT,isPost = true).thenApply { p ->
            assertEquals(p.post.content, CONTENT)
            assertEquals(p.post.author, AUTHOR)
        }.orTimeout(TIMEOUT, MILLISECONDS).exceptionally { fail(TIMEOUT_FAIL_MSG) }
            .join()
    }

    @Test
    fun replyReturnsPost() {
        forum.newPost(AUTHOR, CONTENT,isPost = false).thenApply { p ->
            p.reply(AUTHOR, CONTENT).thenApply { r ->
                assertEquals(r.post.content, CONTENT)
                assertEquals(r.post.author, AUTHOR)
            }.join()
        }.orTimeout(TIMEOUT, MILLISECONDS).exceptionally { fail(TIMEOUT_FAIL_MSG) }
            .join()
    }

    @Test
    fun getNonExistentPostFails() {
        forum.getPost(emptyList()).thenApply {
            assertFalse(true) // For type checking, we cannot use fail()
        }.orTimeout(TIMEOUT, MILLISECONDS).exceptionally {
            assertTrue(true) // succeed() doesn't exist
        }.join()
    }

    @Test
    fun getWorksAfterPost() {
        forum.newPost(AUTHOR, CONTENT,isPost = true).thenApply { p1 ->
            forum.getPost(listOf(p1.post.key)).thenApply { p2 ->
                assertEquals(p1.post.author, p2.post.author)
                assertEquals(p1.post.content, p2.post.content)
            }.join()
        }.orTimeout(TIMEOUT, MILLISECONDS).exceptionally { fail(TIMEOUT_FAIL_MSG) }
            .join()
    }

    companion object {
        private const val AUTHOR = "AUTHOR"
        private const val CONTENT = "CONTENT"
        private const val TIMEOUT = 10_000L
        private const val TIMEOUT_FAIL_MSG = "Future did not complete"
    }
}