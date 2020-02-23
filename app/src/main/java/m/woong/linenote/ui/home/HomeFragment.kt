package m.woong.linenote.ui.home

import android.os.Bundle
import android.view.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch
import m.woong.linenote.R
import m.woong.linenote.data.db.MemoDatabase
import m.woong.linenote.ui.Adapter.HomeAdapter
import m.woong.linenote.ui.BaseFragment

class HomeFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // RecyclerView 세팅
        recycler_view_notes.setHasFixedSize(true)
        recycler_view_notes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // Coroutine으로 모든 Memo를 조회함
        launch {
            context?.let{
                val notes = MemoDatabase(it).getMemoDao().getAllMemos()
                recycler_view_notes.adapter =
                    HomeAdapter(notes)
            }
        }

        // 새로운 메모 작성버튼
        button_add.setOnClickListener {
            val action = HomeFragmentDirections.actionMemo()
            Navigation.findNavController(it).navigate(action)
        }
    }


}
