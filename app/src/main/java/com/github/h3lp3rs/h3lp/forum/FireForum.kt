import android.os.Build
import androidx.annotation.RequiresApi
import com.github.h3lp3rs.h3lp.database.Databases
import com.github.h3lp3rs.h3lp.database.Databases.Companion.databaseOf
import com.github.h3lp3rs.h3lp.database.Databases.FORUM
import com.github.h3lp3rs.h3lp.forum.*
import com.github.h3lp3rs.h3lp.forum.data.ForumPostData
import java.time.ZonedDateTime
import java.util.concurrent.CompletableFuture

class FireForum(override val path: Path) : Forum {
    private val rootForum = databaseOf(FORUM)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun newPost(author: String, content: String): CompletableFuture<ForumPost> {
        val forumPostData = ForumPostData(author, content, ZonedDateTime.now(), emptyList())
        val forumPost = ForumPost(this, forumPostData)
        rootForum.addToObjectsListConcurrently(pathToKey(path), ForumPost::class.java, forumPost)
        //TODO : not incredible but allows us to work only with futures
        return CompletableFuture.completedFuture(forumPost)
    }

    override fun getPost(relativePath: Path): CompletableFuture<ForumPost> {
        val fullPath = pathToKey(path + relativePath)
        return rootForum.getObject(fullPath, ForumPost::class.java)
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

    /**
     * Returns the corresponding string (in Firebase, strings separated by a "/" represent a path,
     * as in a filesystem with "files" or children in the case of a database)
     */
    private fun pathToKey(path: Path): String {
        return path.joinToString(separator = "/")
    }
}
