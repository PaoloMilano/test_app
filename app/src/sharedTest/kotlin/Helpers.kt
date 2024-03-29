import android.content.Context
import androidx.room.Room
import com.magicbluepenguin.testapplication.data.cache.AppDatabase
import com.magicbluepenguin.testapplication.data.models.ImageItem

fun getInMemoryDb(context: Context) = Room.inMemoryDatabaseBuilder(
    context,
    AppDatabase::class.java
).build()

val dummyItems = listOf(
    ImageItem("34567f", "image 1", "string in image 1", 1.01f),
    ImageItem("345dd67f", "image 2", "string in image 2", 2.02f),
    ImageItem("349dd67f", "image 3", "string in image 3", 3.03f),
    ImageItem("545dd67f", "image 4", "string in image 4", 4.04f)
)

val modifiedDummyItems = dummyItems.map { ImageItem(it._id, it.img, "new ${it.text}", it.confidence + 1) }

val testImageItem = ImageItem("345wd67f",
    "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",
    "string in image",
    4.04f)