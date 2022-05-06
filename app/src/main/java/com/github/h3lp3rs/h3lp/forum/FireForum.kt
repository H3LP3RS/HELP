package com.github.h3lp3rs.h3lp.forum

import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.database.Databases.FORUM
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import java.util.concurrent.CompletableFuture

class FireForum(override val path: Path): Forum {
    private val root = databaseOf(FORUM)

    override fun newPost(author: String, content: String): CompletableFuture<ForumPost> {
        TODO("Not yet implemented")
    }

    override fun getPost(relativePath: Path): CompletableFuture<ForumPost> {
        val fullPath = path + relativePath
        TODO("Not yet implemented")
    }

    override fun getAll(): CompletableFuture<List<CategoryPosts>> {
        TODO("Not yet implemented")
    }

    override fun listenToAll(action: (ForumPostData) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun root(): Forum {
        return FireForum(emptyList())
    }

    override fun category(category: ForumCategory): Forum {
        // The categories are the first sub-level of the forum, thus they lie after root (the
        // empty list)
        return FireForum(listOf(category.name))
    }

    override fun child(relativePath: Path): Forum {
        return FireForum(path + relativePath)
    }
}