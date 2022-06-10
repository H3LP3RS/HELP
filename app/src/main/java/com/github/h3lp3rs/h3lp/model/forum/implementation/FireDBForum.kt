package com.github.h3lp3rs.h3lp.model.forum.implementation

import android.content.Context
import com.github.h3lp3rs.h3lp.model.database.FireDatabase
import com.github.h3lp3rs.h3lp.model.forum.data.Path

/**
 * This forum behaves according to the ForumProtocol.md limitations (subset of Forum
 * interface) and uses the FireDatabase as an underlying data structure.
 *
 * @param path The path of the current forum pointer.
 * @database The FireDatabase that serves as root.
 * @param context The calling context to be able to instantiate a forum
 */
class FireDBForum(
    path: Path,
    database: FireDatabase,
    private val context: Context
) : SimpleDBForum(path, database, context)
