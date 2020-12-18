package com.WarnetIT.kursusku


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_beranda.*
import java.lang.Exception

@Suppress("DEPRECATION")
class BerandaFragment : Fragment() {

    var dataKursus: ArrayList<DataKursus> = arrayListOf()

    fun newInstance(dataKursus: ArrayList<DataKursus>): BerandaFragment?{
        val fragmentBeranda = BerandaFragment()
        val args = Bundle()
        args.putParcelableArrayList("dataKursus", dataKursus)
        fragmentBeranda.arguments = args
        return fragmentBeranda
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataKursus = arguments!!.getParcelableArrayList<DataKursus>("dataKursus")!!

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        // SwipeRefreshLayout
//        mSwipeRefreshLayout =
//            rootView.findViewById<View>(R.id.swipe_container) as SwipeRefreshLayout
//        mSwipeRefreshLayout!!.setOnRefreshListener(this)
//        mSwipeRefreshLayout!!.setColorSchemeResources(
//            R.color.colorPrimary,
//            R.color.colorPrimary,
//            R.color.colorPrimary,
//            R.color.colorPrimary
//        )
//
//        mSwipeRefreshLayout!!.post {
//            mSwipeRefreshLayout!!.isRefreshing = true
//            loadKursus()
//        }

        return inflater.inflate(R.layout.fragment_beranda, container, false)
    }

    override fun onViewCreated(
        view: View,
        @Nullable savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        mRecyclerView1.setHasFixedSize(true)
        mRecyclerView1.layoutManager = GridLayoutManager(context, 2)
        val adapter = RVAdapterKursus(activity, dataKursus)
        adapter.notifyDataSetChanged()
        mRecyclerView1.adapter = adapter

        mRecyclerView1.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                //show button when not on top
                val visibility = if ((mRecyclerView1.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() != 0){
                    View.VISIBLE
                }
                else {
                    View.INVISIBLE
                }
                btn_back_to_top.visibility = visibility



                if(btn_back_to_top.visibility == View.VISIBLE){
                    val handler = Handler()
                    handler.postDelayed(Runnable { // Do something after 5s = 5000ms
                        try {
                            btn_back_to_top.visibility = View.INVISIBLE
                        }
                        catch (e:Exception){
                        }
                    }, 4000)
                }

                //hide layout when scroll down
                if (dy > 0){

                    val id = resources.getIdentifier(
                        "com.WarnetIT.kursusku:drawable/ic_arrow_downward_white_24dp",
                        null,
                        null
                    )
                    btn_back_to_top.setCompoundDrawablesWithIntrinsicBounds(id,0,0,0)
                    //smooth scroll
                    val smoothScroller: RecyclerView.SmoothScroller = object : LinearSmoothScroller(activity) {
                        override fun getVerticalSnapPreference(): Int {
                            return SNAP_TO_END
                        }
                    }

                    btn_back_to_top.setOnClickListener{
                        smoothScroller.targetPosition = dataKursus.size
                        (mRecyclerView1.layoutManager as GridLayoutManager).startSmoothScroll(smoothScroller)
                        btn_back_to_top.visibility = View.INVISIBLE
                    }
                }
                else if(dy < 0){
                    val id = resources.getIdentifier(
                        "com.example.kwuapp:drawable/ic_arrow_upward_white_24dp",
                        null,
                        null)
                    btn_back_to_top.setCompoundDrawablesWithIntrinsicBounds(id,0,0,0)
                    //smooth scroll
                    val smoothScroller: RecyclerView.SmoothScroller = object : LinearSmoothScroller(activity) {
                        override fun getVerticalSnapPreference(): Int {
                            return SNAP_TO_START
                        }
                    }

                    btn_back_to_top.setOnClickListener{
                        smoothScroller.targetPosition = 0
                        (mRecyclerView1.layoutManager as GridLayoutManager).startSmoothScroll(smoothScroller)
                        btn_back_to_top.visibility = View.INVISIBLE
                    }
                }
            }
        })
    }
}