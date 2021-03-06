package m.woong.linenote.data.db

import androidx.room.*

/*
 * MemoDao를 통해 메모장의 내용을 CRUD 가능
 */
@Dao
interface MemoDao {

    @Insert
    suspend fun addMemo(memo: Memo)

    @Query("SELECT * FROM memo ORDER BY id DESC")
    suspend fun getAllMemos() : List<Memo>

    @Query("SELECT image FROM memo WHERE id = :idx LIMIT 1")
    suspend fun getImagePath(idx : Int) : String

    @Insert
    suspend fun addMultipleMemos(vararg memo: Memo)

    @Update
    suspend fun updateMemo(memo: Memo)

    @Delete
    suspend fun deleteMemo(memo: Memo)

}