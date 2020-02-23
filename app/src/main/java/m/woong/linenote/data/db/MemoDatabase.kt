package m.woong.linenote.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Memo::class],
    version = 1
)
abstract class MemoDatabase : RoomDatabase(){

    abstract fun getMemoDao() : MemoDao

    companion object {
        @Volatile private var instance : MemoDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            MemoDatabase::class.java,
            "memodatabase"
        ).build()

        /*  // 추후에 테이블 스키마를 변경할때 사용할 예비코드
       val MIGRATION_1_2 = object : Migration(1, 2) {
           override fun migrate(database: SupportSQLiteDatabase) {
               database.execSQL("ALTER TABLE Memo ADD COLUMN pic TEXT")
           }
       }
        */
    }
}