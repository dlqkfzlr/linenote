package m.woong.linenote.ui.Adapter

import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_home_memo.view.*
import m.woong.linenote.R
import m.woong.linenote.data.db.Memo
import m.woong.linenote.ui.home.HomeFragmentDirections
import m.woong.linenote.ui.memo.MemoFragment.Companion.IMAGE_DIRECTORY
import java.io.File

/*
 * 전체 메모 조회시,
 * RecyclerView에 부착되는 Adapter
 */
class HomeAdapter (private val memos: List<Memo>) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_memo, parent, false)
        )
    }

    override fun getItemCount() = memos.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.view.tv_title.text = memos[position].title
        holder.view.tv_note.text = memos[position].desc
        val thumbnail = memos[position].image
        Log.d("memo 데이터", memos[position].toString())
        if (thumbnail.equals("")){  // 첨부 이미지가 없는 메모의 경우, ImageView를 사라지게함.
            holder.view.iv_thumbnail.visibility = View.GONE
        } else {                            // 첨부 이미지가 존재하는 메모의 경우, 첫 이미지를 썸네일로 채택함.
            val first = thumbnail!!.split("||")
            holder.view.iv_thumbnail.visibility = View.VISIBLE
            loadImage(holder.view.iv_thumbnail, first[0])
        }

        holder.view.setOnClickListener {
            val action = HomeFragmentDirections.actionMemo()
            action.memo = memos[position]
            Navigation.findNavController(it).navigate(action)
        }

    }

    /*
     * 크게 두가지로 나눠서 Image를 로드함
     * 1) 문자열'//'가 포함될 경우 외부이미지url이므로 해당 url경로 그대로 이미지를 불러옴
     * 2) 그외의 경우 내부이미지로 간주하여 내부저장소경로에서 이미지 File을 불러옴
     */
    fun loadImage(imageView: ImageView, imagePath: String){
        if (imagePath.contains("//")) {   // 외부이미지
            Log.d("썸네일", "외부이미지")
            Glide.with(imageView.context)
                .load(imagePath)
                .override(400,400)
                .centerCrop()
                .error(R.drawable.ic_error)
                .into(imageView)
        } else {                // 내부저장이미지
            Log.d("썸네일", "내부이미지")
            val file = File(
                Environment.getExternalStoragePublicDirectory(IMAGE_DIRECTORY),
                imagePath
            )
            Glide.with(imageView.context)
                .load(file)
                .override(400,400)
                .centerCrop()
                .error(R.drawable.ic_error)
                .into(imageView)

        }

    }



    class HomeViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}