package com.github.h3lp3rs.h3lp.forum.implementation

import com.github.h3lp3rs.h3lp.database.FireDatabase
import com.github.h3lp3rs.h3lp.forum.Forum
import com.github.h3lp3rs.h3lp.forum.Path

/**
 * This forum behaves according to the ForumProtocol.md limitations (subset of Forum
 * interface) and uses the FireDatabase as an underlying data structure.
 *
 * @param path The path of the current forum pointer.
 * @database The FireDatabase that serves as root.
 */
class FireDBForum (
    override val path: Path,
    private val database: FireDatabase
) :
    SimpleDBForum(database) {

    override fun root(): Forum {
        return FireDBForum(emptyList(), database)
    }

    override fun child(relativePath: Path): Forum {
        return FireDBForum(path + relativePath, database)
    }

    override fun parent(): Forum {
        return if (isRoot()) {
            this
        } else {
            FireDBForum(path.dropLast(1), database)
        }
    }
}