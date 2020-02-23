package m.woong.linenote.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Memo::class],
    version = 1,
    exportSchema = false
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
            //) .addMigrations(MIGRATION_1_2).build()   // 추후 스키마 변경시 주석 해제 후 사용


        /*   // 추후에 테이블 스키마를 변경할때 사용할 예비코드 (상단의 version을 2로 바꿔주고 사용할 것)
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Memo ADD COLUMN pic TEXT")
            }
        }
         */

    }
}