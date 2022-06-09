package com.github.h3lp3rs.h3lp.forum

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.github.h3lp3rs.h3lp.model.forum.ForumCategory.Companion.mockForum
import com.github.h3lp3rs.h3lp.model.forum.ForumCategory.Companion.root
import com.github.h3lp3rs.h3lp.model.forum.ForumCategory.GENERAL
import com.github.h3lp3rs.h3lp.model.forum.data.Forum
import org.junit.Assert.assertFalse
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit.MILLISECONDS


// MockForumTest is an android test since it requires an application context to use the storages
class MockForumTest {
    // IMPORTANT NOTE: the current gradle version is compatible at most with Java 8 but the method
    // orTimeout on CompletableFuture was added in Java 9, there is thus unfortunately no way to use
    // it here

    // Useful variables
    private lateinit var forum: Forum

    @Before
    fun setup() {
        mockForum(getApplicationContext())
        forum = root(getApplicationContext()).child(GENERAL.name)
    }

    @Test
    fun newPostReturnsPost() {
        forum.newPost(AUTHOR, CONTENT, isPost = true).thenApply { p ->
            assertEquals(p.post.content, CONTENT)
            assertEquals(p.post.author, AUTHOR)
        }.orTimeout(TIMEOUT, MILLISECONDS).exceptionally { fail(TIMEOUT_FAIL_MSG) }
            .join()
    }

    @Test
    fun replyReturnsPost() {
        forum.newPost(AUTHOR, CONTENT, isPost = false).thenApply { p ->
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
        forum.newPost(AUTHOR, CONTENT, isPost = true).thenApply { p1 ->
            forum.getPost(listOf(p1.post.key)).thenApply { p2 ->
                assertEquals(p1.post.author, p2.post.author)
                assertEquals(p1.post.content, p2.post.content)
            }.join()
        }.orTimeout(TIMEOUT, MILLISECONDS).exceptionally { fail(TIMEOUT_FAIL_MSG) }
            .join()
    }

    @Test
    fun listenerWorksWhenPostsBefore() {
        var counter = 0
        forum.newPost(AUTHOR, CONTENT, isPost = true).orTimeout(TIMEOUT, MILLISECONDS)
            .exceptionally { fail(TIMEOUT_FAIL_MSG) }.join()
        forum.newPost(AUTHOR, CONTENT, isPost = true).orTimeout(TIMEOUT, MILLISECONDS)
            .exceptionally { fail(TIMEOUT_FAIL_MSG) }.join()
        forum.listenToAll { counter++ }
        assertEquals(2, counter)
    }

    @Test
    fun listenerWorksWhenPostsAfter() {
        var counter = 0
        forum.listenToAll { counter++ }
        forum.newPost(AUTHOR, CONTENT, isPost = true).orTimeout(TIMEOUT, MILLISECONDS)
            .exceptionally { fail(TIMEOUT_FAIL_MSG) }.join()
        assertEquals(1, counter)
    }

    @Test
    fun listenToReplyWorks() {
        var counter = 0
        val p = forum.newPost(AUTHOR, CONTENT, isPost = true).orTimeout(TIMEOUT, MILLISECONDS)
            .exceptionally { fail(TIMEOUT_FAIL_MSG) }.join()
        p.listen {
            assertEquals(AUTHOR, it.author)
            assertEquals(CONTENT, it.content)
            counter++
        }
        p.reply(AUTHOR, CONTENT)
        assertEquals(1, counter)
    }

    companion object {
        private const val AUTHOR = "AUTHOR"
        private const val CONTENT = "CONTENT"
        private const val TIMEOUT = 10_000L
        private const val TIMEOUT_FAIL_MSG = "Future did not complete"
    }
}