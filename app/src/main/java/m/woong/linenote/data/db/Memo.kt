package m.woong.linenote.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/*
 * 메모장을 구성하는 DB 테이블(Entity)
 *
 *  id: PK & 인덱스
 *  title: 제목,
 *  desc: 내용,
 *  image: 이미지 파일 경로(복수의 이미지의 경우 구분자 '||'로 구분함)
 */
@Entity
data class Memo (
    val title: String,
    val desc: String,
    val image: String?
):Serializable{
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}