package com.github.h3lp3rs.h3lp.forum.implementation

import android.content.Context
import com.github.h3lp3rs.h3lp.database.FireDatabase
import com.github.h3lp3rs.h3lp.forum.Forum
import com.github.h3lp3rs.h3lp.forum.Path

/**
 * This forum behaves according to the ForumProtocol.md limitations (subset of Forum
 * interface) and uses the FireDatabase as an underlying data structure.
 *
 * @param path The path of the current forum pointer.
 * @param database The FireDatabase that serves as root.
 * @param context The calling context to be able to instantiate a forum
 */
class FireDBForum (
    override val path: Path,
    private val database: FireDatabase,
    private val context: Context
) :
    SimpleDBForum(database, context) {

    override fun root(): Forum {
        return FireDBForum(emptyList(), database, context)
    }

    override fun child(relativePath: Path): Forum {
        return FireDBForum(path + relativePath, database, context)
    }

    override fun parent(): Forum {
        return if (isRoot()) {
            this
        } else {
            FireDBForum(path.dropLast(1), database, context)
        }
    }
}