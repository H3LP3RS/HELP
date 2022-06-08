package com.github.h3lp3rs.h3lp.model.forum.implementation

import com.github.h3lp3rs.h3lp.model.database.FireDatabase
import com.github.h3lp3rs.h3lp.model.forum.data.Forum
import com.github.h3lp3rs.h3lp.model.forum.data.Path

/**
 * This forum behaves according to the ForumProtocol.md limitations (subset of Forum
 * interface) and uses the FireDatabase as an underlying data structure.
 *
 * @param path The path of the current forum pointer.
 * @database The FireDatabase that serves as root.
 */
class FireDBForum(path: Path, database: FireDatabase) : SimpleDBForum(path, database)
