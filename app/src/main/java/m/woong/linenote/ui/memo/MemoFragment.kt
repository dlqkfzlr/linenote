package m.woong.linenote.ui.memo

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import kotlinx.android.synthetic.main.fragment_memo.*
import kotlinx.coroutines.launch
import m.woong.linenote.R
import m.woong.linenote.data.db.Memo
import m.woong.linenote.data.db.MemoDatabase
import m.woong.linenote.ui.Adapter.MemoAdapter
import m.woong.linenote.ui.BaseFragment
import m.woong.linenote.utils.toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/*
 * 특정 메모를 편집하거나 새로운 메모를 작성할 수 있는 MemoFragment
 */
class MemoFragment  : BaseFragment() {

    private var memo: Memo? = null
    val REQUEST_TAKE_PHOTO = 1                                  // Camera 인텐트
    val REQUEST_GET_GALLERY = 2                                 // Gallery 인텐트
    private val img_list : ArrayList<String> = ArrayList()      // 첨부이미지 파일명을 저장하는 ArrayList
    lateinit var currentPhotoPath: String                       // Camera로 촬영한 이미지 파일경로
    lateinit var currentPhotoName: String                       // Camera로 촬영한 이미지 파일명

    companion object {
        val IMAGE_DIRECTORY = "/Android/data/m.woong.linenote/files/Pictures"       // 이미지 내부저장 경로
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_memo, container, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> if (memo != null) deleteMemo() else activity!!.toast("삭제할 메모가 없습니다.")
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_memo, menu)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // HomeFragment로부터 argument를 받는 코드
        arguments?.let {
            memo = MemoFragmentArgs.fromBundle(it).memo
            edit_text_title.setText(memo?.title)
            edit_text_desc.setText(memo?.desc)

        }
        // 첨부이미지 RecyclerView
        // Memo Entity에서 해당메모의 image컬럼값을 가져와서 구분자"||"를 기준으로 배열로 나눠줌 => 이 배열을 adapter에 추가해주는 방식
        rv_pic.setHasFixedSize(true)
        rv_pic.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // 편집의 경우 해당 메모의 첨부이미지를 불러오는 코드
        launch {
            context?.let{
                if (memo == null) {

                } else {
                    val path = MemoDatabase(it).getMemoDao().getImagePath(memo!!.id)
                    val pathlist = path.split("||")
                    for (img in pathlist){
                        if (img.equals("")){
                        } else {
                            img_list.add(img)
                        }
                    }
                }
                rv_pic.adapter = MemoAdapter(img_list)
            }
        }

        // 이미지 첨부버튼
        ib_attach.setOnClickListener {
            showPictureDialog()
        }

        // 메모 저장버튼
        button_save.setOnClickListener { view ->

            val memoTitle = edit_text_title.text.toString().trim()
            val memoDesc = edit_text_desc.text.toString().trim()

            if (memoTitle.isEmpty()) {
                edit_text_title.error = "제목을 입력해주세요"
                edit_text_title.requestFocus()
                return@setOnClickListener
            }

            if (memoDesc.isEmpty()) {
                edit_text_desc.error = "내용을 입력해주세요"
                edit_text_desc.requestFocus()
                return@setOnClickListener
            }

            var pathlist = ""
            if (img_list.size!=0){
                for (img in img_list){
                    pathlist = pathlist + img + "||"
                }
            } else {
            }

            launch {

                context?.let {
                    val mMemo = Memo(memoTitle, memoDesc, pathlist)

                    if (memo == null) {
                        MemoDatabase(it).getMemoDao().addMemo(mMemo)
                        it.toast("메모가 저장되었습니다.")
                    } else {
                        mMemo.id = memo!!.id
                        MemoDatabase(it).getMemoDao().updateMemo(mMemo)
                        it.toast("메모를 수정했습니다.")
                    }

                    // softKeyBoard를 숨기는 코드
                    view.context.hideKeyboard(view)

                    val action = MemoFragmentDirections.actionSaveMemo()
                    Navigation.findNavController(view).navigate(action)
                }
            }

        }

    }

    // softKeyBoard를 숨기는 Method
    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // 해당 메모를 삭제하는 Method
    private fun deleteMemo() {
        AlertDialog.Builder(context).apply {
            setTitle("작성 중인 메모를 삭제하시겠습니까?")
            setMessage("삭제하면 다시 복구할 수 없습니다.")
            setPositiveButton("예") { _, _ ->
                launch {
                    MemoDatabase(context).getMemoDao().deleteMemo(memo!!)
                    val action =
                        MemoFragmentDirections.actionSaveMemo()
                    Navigation.findNavController(view!!).navigate(action)
                }
            }
            setNegativeButton("아니오") { _, _ ->

            }
        }.create().show()
    }

    // 이미지를 첨부하는 방법선택 Dialog를 띄워주는 Method
    private fun showPictureDialog() {
        val pictureDialog = androidx.appcompat.app.AlertDialog.Builder(context!!)
        val pictureDialogItems = arrayOf("카메라", "갤러리", "외부이미지 url")
        pictureDialog.setTitle("이미지 첨부방식 선택")
                    .setItems(pictureDialogItems
                    ) { dialog, which ->
                        when (which) {
                            0 -> takePictureFromCamera()
                            1 -> getImageFromGallery()
                            2 -> showEditTextDialog()
                        }
                    }
                    .show()
    }


    // 방법1)) 직접 카메라로 촬영해서 이미지 가져옴
    private fun takePictureFromCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(context!!.packageManager)?.also {
                // 사진이 저장될 파일을 미리 생성
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) { // 파일생성시 에러발생
                    null
                }
                // 파일이 성공적으로 생성될때만 작업을 계속 진행함
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(context!!, context!!.packageName, it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    // 방법2)) 갤러리에서 이미지 가져옴
    private fun getImageFromGallery(){
        Intent(Intent.ACTION_PICK).also { getGalleryIntent ->
            getGalleryIntent.setType("image/*")
            startActivityForResult(getGalleryIntent, REQUEST_GET_GALLERY)
        }
    }


    // 방법3)) 외부 url로 이미지 가져옴
    // 외부 url을 입력하기 위한 Dialog
    private fun showEditTextDialog(){
        val etDialog = androidx.appcompat.app.AlertDialog.Builder(context!!)
        val urlInput = EditText(context!!)
        etDialog.setTitle("외부이미지 url을 입력해주세요")
                .setView(urlInput)
                .setPositiveButton("입력") { dialogInterface, i ->
                    val imageUrl = urlInput.text.toString()
                    showUrlImageDialog(imageUrl)
                }
                .setNegativeButton("취소") { dialogInterface, i ->

                }
                .show()
    }

    // 외부url 이미지를 확인하기 위한 Dialog
    private fun showUrlImageDialog(imageUrl : String?){
        val urlImageView = ImageView(context!!)
        var error = false
        Glide.with(this)
            .load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: com.bumptech.glide.request.target.Target<Drawable>?, p3: Boolean): Boolean {
                    error = true
                    return false
                }
                override fun onResourceReady(p0: Drawable?, p1: Any?, p2: com.bumptech.glide.request.target.Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                    return false
                }
            })
            .override(480,480)
            .fitCenter()
            .error(R.drawable.ic_error)
            .into(urlImageView)
        val imageDialog = androidx.appcompat.app.AlertDialog.Builder(context!!)
        imageDialog.setTitle("해당 이미지가 맞습니까?")
            .setMessage("")
            .setView(urlImageView)
            .setPositiveButton("완료") { dialogInterface, i ->
                if (error){
                    context!!.toast("잘못된 이미지 주소입니다.")
                } else {
                    // 첨부이미지 RecyclerView를 구성하는 배열에 추가할 것
                    img_list.add(imageUrl!!)
                    rv_pic.adapter!!.notifyDataSetChanged()
                    Log.d("이미지배열", "$img_list")
                }
            }
            .setNegativeButton("다시찍기") { dialogInterface, i ->
                showEditTextDialog()
            }
            .show()
    }

    /*
     * 내부저장소에 Camera 또는 Gallery 이미지를 저장하는 Method
     */
    // 방법1에서 Intent 시작 전 File을 만들어주는 작업
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // 이미지 File 생성 (파일명 예시: JPEG_20200221_082547_1486047547.jpg)
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // 내부 저장경로
            currentPhotoPath = absolutePath
            val name = absolutePath.split("/")
            currentPhotoName = name[name.size-1]
        }
    }

    // 방법2 실행 후 갤러리에서 가져온 이미지를 앱전용 폴더에 저장함
    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val imageDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // 해당 디렉토리가 없으면 생성해줌
        if (!imageDirectory.exists())
        {
            imageDirectory.mkdirs()
        }

        try
        {
            Log.d("기본저장경로",imageDirectory.toString())
            val f = File(imageDirectory, ((Calendar.getInstance()
                .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile( context!!,
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("최종저장경로", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }


    // 이미지 Callback Method
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when(requestCode){
                REQUEST_TAKE_PHOTO -> {
                    // 첨부이미지 RecyclerView를 구성하는 배열에 추가할 것
                    img_list.add(currentPhotoName)
                    rv_pic.adapter!!.notifyDataSetChanged()
                    Log.d("이미지배열", "$img_list")
                }
                REQUEST_GET_GALLERY -> {
                    if (data != null){
                        val contentURI = data!!.data
                        try {
                            val bitmap = MediaStore.Images.Media.getBitmap( context!!.contentResolver, contentURI)
                            Log.d("경로", contentURI.toString())
                            val path = saveImage(bitmap)
                            // 첨부이미지 RecyclerView를 구성하는 배열에 추가할 것
                            val pathlist = path.split("/")
                            val name = pathlist[pathlist.size-1]
                            img_list.add(name)
                            rv_pic.adapter!!.notifyDataSetChanged()
                            Log.d("이미지배열", "$img_list")
                        }catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }


}