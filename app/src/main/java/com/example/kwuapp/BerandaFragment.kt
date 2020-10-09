package com.example.kwuapp


import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_beranda.*

@Suppress("DEPRECATION")
class BerandaFragment : Fragment() {

    var dataKursus: ArrayList<DataKursus> = arrayListOf()
    fun newInstance(dataKursus: ArrayList<DataKursus>): BerandaFragment?{
        val fragmentBeranda = BerandaFragment()
        val args = Bundle()
        args.putParcelableArrayList("dataKursus", dataKursus)
        fragmentBeranda.setArguments(args)
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beranda, container, false)
    }

    override fun onViewCreated(
        view: View,
        @Nullable savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        mRecyclerView1.setHasFixedSize(true)
        val layout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView1.layoutManager = GridLayoutManager(context, 2)
        val adapter = RVAdapterKursus(dataKursus)
        adapter.notifyDataSetChanged()
        mRecyclerView1.adapter = adapter
    }
}
