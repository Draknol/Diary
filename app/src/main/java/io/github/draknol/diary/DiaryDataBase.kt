package io.github.draknol.diary

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Entity
@Serializable
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val date: String
)

@Dao
interface DiaryDao {
    @Query(value = "SELECT * FROM Entry ORDER BY date DESC")
    fun getAllDesc(): Flow<List<Entry>>

    @Query(value = "SELECT * FROM Entry ORDER BY date ASC")
    fun getAllAsc(): Flow<List<Entry>>

    @Insert
    fun insert(entry: Entry): Long

    @Query(value = "DELETE FROM Entry")
    fun deleteAllEntries()
}

@Database(entities = [Entry::class], version = 1)
abstract class DiaryDataBase : RoomDatabase() {
    abstract fun DiaryDao(): DiaryDao
    companion object {
        private val roomDataBaseCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(context = Dispatchers.IO).launch {
                    val dao: DiaryDao = Instance!!.DiaryDao()
                    dao.deleteAllEntries()
                    dao.insert(Entry(
                        title = "Stuff I did the day before yesterday",
                        content = "I did stuff",
                        date = LocalDate.now().plusDays(-2).toString())
                    )
                    dao.insert(Entry(
                        title = "Stuff I did yesterday",
                        content = "I did stuff",
                        date = LocalDate.now().plusDays(-1).toString())
                    )
                    dao.insert(Entry(
                        title = "Stuff I did today",
                        content = "I did stuff",
                        date = LocalDate.now().toString())
                    )
                }
            }
        }

        @Volatile
        private var Instance: DiaryDataBase? = null
        fun getDataBase(context: Context): DiaryDataBase {
            return Instance ?: synchronized(lock = this) {
                Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = DiaryDataBase::class.java,
                    name = "diary_database"
                ).addCallback(callback = roomDataBaseCallback).build().also { Instance = it }
            }
        }
    }
}