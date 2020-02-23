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
import m.woong.linenote.utils.toast

class HomeFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recycler_view_notes.setHasFixedSize(true)
        recycler_view_notes.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


        launch {
            context?.let{
                val notes = MemoDatabase(it).getMemoDao().getAllMemos()
                recycler_view_notes.adapter =
                    HomeAdapter(notes)
            }
        }


        button_add.setOnClickListener {

            val action = HomeFragmentDirections.actionMemo()
            Navigation.findNavController(it).navigate(action)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.select -> context?.toast("선택해주세요")
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home, menu)
    }

}
