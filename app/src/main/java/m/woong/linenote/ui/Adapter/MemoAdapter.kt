package m.woong.linenote.ui.Adapter

/*
 * Glide 라이브러리 사용
 * An image loading and caching library for Android focused on smooth scrolling
 * https://github.com/bumptech/glide
 */

import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_memo_image.view.*
import m.woong.linenote.R
import m.woong.linenote.ui.memo.MemoFragment.Companion.IMAGE_DIRECTORY
import java.io.File

/*
 * 메모 작성 및 편집 시,
 * 첨부이미지 RecyclerView에 부착되는 Adapter
 */
class MemoAdapter (private val images: ArrayList<String>) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>(){

    private val TAG = "MemoAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        return MemoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_memo_image, parent, false)
        )
    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {

        // 해당 메모의 첨부이미지를 Glide를 통해 화면에 보여줌
        loadImage(holder.view.iv_attach, images[position])

        // 해당 뷰를 롱클릭시 삭제가능한 Dialog를 띄워줌
        holder.view.setOnLongClickListener {
            deleteImage(position, it)
            return@setOnLongClickListener true
        }

    }

    private fun loadImage(imageView: ImageView, imagePath: String){
        if (imagePath.contains("//")) {   // 외부이미지
            Glide.with(imageView.context)
                .load(imagePath)
                .override(600,600)
                .centerCrop()
                .error(R.drawable.ic_error)
                .into(imageView)
        } else {                                // 내부저장이미지
            val file = File(
                Environment.getExternalStoragePublicDirectory(IMAGE_DIRECTORY),
                imagePath
            )
            Glide.with(imageView.context)
                .load(file)
                .override(600,600)
                .centerCrop()
                .error(R.drawable.ic_error)
                .into(imageView)

        }

    }

    class MemoViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    // 해당 첨부이미지를 삭제하는 Method
    private fun deleteImage(pos: Int, view: View){
        val deleteDialog = androidx.appcompat.app.AlertDialog.Builder(view.context)
        deleteDialog.setTitle("해당 이미지 삭제하시겠습니까?")
                    .setPositiveButton("예") { dialogInterface, i ->
                        images.removeAt(pos)
                        Log.d(TAG, images.toString())
                        this.notifyDataSetChanged()
                    }
                    .setNegativeButton("아니오") { dialogInterface, i ->
                    }
                    .show()

    }

}