package com.github.h3lp3rs.h3lp

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.github.h3lp3rs.h3lp.forum.Forum
import com.github.h3lp3rs.h3lp.forum.ForumCategory.*
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.mockForum
import com.github.h3lp3rs.h3lp.forum.ForumCategory.Companion.root
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse

// MockForumTest is an android test since it requires an application context to use the storages
class MockForumTest : H3lpAppTest(){
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
        forum.newPost(AUTHOR, CONTENT,isPost = true).thenApply { p ->
            assertEquals(p.post.content, CONTENT)
            assertEquals(p.post.author, AUTHOR)
        }.join()
    }

    @Test
    fun replyReturnsPost() {
        forum.newPost(AUTHOR, CONTENT,isPost = false).thenApply { p ->
            p.reply(AUTHOR, CONTENT).thenApply { r ->
                assertEquals(r.post.content, CONTENT)
                assertEquals(r.post.author, AUTHOR)
            }.join()
        }.join()
    }

    @Test
    fun getWorksAfterPost() {
        forum.newPost(AUTHOR, CONTENT,isPost = true).thenApply { p1 ->
            forum.getPost(listOf(p1.post.key)).thenApply { p2 ->
                assertEquals(p1.post.author, p2.post.author)
                assertEquals(p1.post.content, p2.post.content)
            }.join()
        }.join()
    }

    // TODO: Check listeners (way out of time), so their mocking stays risky and bug prone.
    // TODO: Will do in the next sprint, the other functions have same the behaviour than fire forum

    companion object {
        private const val AUTHOR = "AUTHOR"
        private const val CONTENT = "CONTENT"
    }
}