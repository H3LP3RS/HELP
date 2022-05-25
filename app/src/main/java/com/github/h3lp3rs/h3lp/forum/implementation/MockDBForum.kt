package com.github.h3lp3rs.h3lp.forum.implementation

import android.content.Context
import com.github.h3lp3rs.h3lp.database.MockDatabase
import com.github.h3lp3rs.h3lp.forum.Forum
import com.github.h3lp3rs.h3lp.forum.Path

/**
 * This forum behaves according to the ForumProtocol.md limitations (subset of Forum
 * interface) and uses the MockDatabase as an underlying data structure.
 *
 * @param path The path of the current forum pointer.
 * @param database The MockDatabase that serves as root.
 * @param context The calling context to be able to instantiate a forum (even if this is a mock forum,
 * it requires a context to instantiate the SimpleDBForum
 */
class MockDBForum(override val path: Path, private val database: MockDatabase, private val context: Context) :
    SimpleDBForum(database, context) {

    override fun root(): Forum {
        return MockDBForum(emptyList(), database, context)
    }

    override fun child(relativePath: Path): Forum {
        return MockDBForum(path + relativePath, database, context)
    }

    override fun parent(): Forum {
        return if (isRoot()) {
            this
        } else {
            MockDBForum(path.dropLast(1), database, context)
        }
    }
}