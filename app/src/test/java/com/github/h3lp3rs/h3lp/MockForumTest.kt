package com.github.h3lp3rs.h3lp

import com.github.h3lp3rs.h3lp.forum.MockForum
import org.junit.Test

class MockForumTest {

    @Test
    fun myFunnyTest() {
        val forum = MockForum(null, "", null)
        forum.child("CARDIOLOGY").newPost("Gentil Alexis", "Salut!").thenApply {
            it.reply("Dark Alexis", "Bouh!")
            it.reply("Darker Alexis", "Oullaaa!")
            println(forum)
        }.join()
    }
}