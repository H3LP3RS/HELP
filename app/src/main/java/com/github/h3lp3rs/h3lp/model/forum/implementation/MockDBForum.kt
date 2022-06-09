package com.github.h3lp3rs.h3lp.model.forum.implementation

import com.github.h3lp3rs.h3lp.model.database.MockDatabase
import com.github.h3lp3rs.h3lp.model.forum.data.Forum
import com.github.h3lp3rs.h3lp.model.forum.data.Path

/**
 * This forum behaves according to the ForumProtocol.md limitations (subset of Forum
 * interface) and uses the MockDatabase as an underlying data structure.
 *
 * @param path The path of the current forum pointer.
 * @database The MockDatabase that serves as root.
 */
class MockDBForum(path: Path, database: MockDatabase) : SimpleDBForum(path, database)