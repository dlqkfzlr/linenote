package m.woong.linenote.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Memo (
    val title: String,
    val desc: String,
    val image: String?
):Serializable{
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}