package com.warnet.kursusku


import android.content.Intent
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
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_beranda.*
import java.lang.Exception
import kotlin.random.Random

@Suppress("DEPRECATION")
class BerandaFragment : Fragment() {

    private var dataKursus: ArrayList<DataKursus> = arrayListOf()
    private var listRandom: MutableList<Int> = mutableListOf()

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

        callSlider()

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
                        "com.warnet.kursusku:drawable/ic_arrow_downward_white_24dp",
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
                        "com.warnet.kursusku:drawable/ic_arrow_upward_white_24dp",
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

    private fun callSlider(){
//        val sliderView: SliderView? = view?.findViewById(R.id.imageSlider)
//        sliderView?.setSliderAdapter(SliderAdapter(context, dataSlider))
//        sliderView?.setIndicatorAnimation(IndicatorAnimationType.FILL)
//        sliderView?.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
//        sliderView?.isAutoCycle = true
//        sliderView?.startAutoCycle()

        val imageList = ArrayList<SlideModel>()

        for (i in dataKursus.indices){
            val angka = Random.nextInt(0, dataKursus.size)
            if(listRandom.contains(angka))
                continue
            listRandom.add(angka)
            if (listRandom.size == 7)
                break
        }

        listRandom.forEach{
            imageList.add(SlideModel(dataKursus.get(it).gambar, dataKursus.get(it).nama))
        }

        val imageSlider = view?.findViewById<ImageSlider>(R.id.image_slider)
        imageSlider?.setImageList(imageList, ScaleTypes.CENTER_CROP)
        imageSlider?.setItemClickListener(
            object: ItemClickListener {
                override fun onItemSelected(position: Int) {
                    val dilihat = dataKursus.get(position).dilihat.plus(1)
                    val db2 = FirebaseFirestore.getInstance()
                    db2.collection("kursus").document(dataKursus.get(position).nama)
                        .update("dilihat", dilihat)
                        .addOnSuccessListener { result2 ->
                        }
                        .addOnFailureListener { exception ->
                        }
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("kursus", dataKursus.get(position))
                    context?.startActivity(intent)
                }

            }
        )
    }
}
