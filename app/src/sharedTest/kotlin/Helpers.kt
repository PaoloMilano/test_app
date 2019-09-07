import android.content.Context
import androidx.room.Room
import com.magicbluepenguin.testapplication.data.cache.AppDatabase
import com.magicbluepenguin.testapplication.data.models.Item

fun getInMemoryDb(context: Context) = Room.inMemoryDatabaseBuilder(
    context,
    AppDatabase::class.java
).build()

val dummyItems = listOf(
    Item("34567f", "image 1", "string in image 1", 1.01f),
    Item("345dd67f", "image 2", "string in image 2", 2.02f),
    Item("349dd67f", "image 3", "string in image 3", 3.03f),
    Item("545dd67f", "image 4", "string in image 4", 4.04f)
)

val modifiedDummyItems = dummyItems.map { Item(it._id, it.img, "new ${it.text}", it.confidence + 1) }