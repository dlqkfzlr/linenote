package m.woong.linenote.ui.Adapter

import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_memo_image.view.*
import m.woong.linenote.R
import m.woong.linenote.data.db.Memo
import m.woong.linenote.ui.memo.MemoFragment.Companion.IMAGE_DIRECTORY
import java.io.File

class MemoAdapter (private val images: List<String>) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        return MemoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_memo_image, parent, false)
        )
    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {

        loadImage(holder.view.iv_attach, images[position])

        //holder.view.setOnLongClickListener {        }
    }

    fun loadImage(imageView: ImageView, imagePath: String){
        if (imagePath.contains("//")) {   // 외부이미지
            Glide.with(imageView.context)
                .load(imagePath)
                .override(400,400)
                .fitCenter()
                .error(R.drawable.ic_error)
                .into(imageView)
        } else {                // 내부저장이미지
            val file = File(
                Environment.getExternalStoragePublicDirectory(IMAGE_DIRECTORY),
                imagePath
            )
            Glide.with(imageView.context)
                .load(file)
                .override(400,400)
                .fitCenter()
                .error(R.drawable.ic_error)
                .into(imageView)

        }

    }

    class MemoViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}