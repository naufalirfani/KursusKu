package com.example.kwuapp


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import kotlinx.android.synthetic.main.fragment_beranda.*

/**
 * A simple [Fragment] subclass.
 */
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
        tv_test.text = dataKursus[0].nama
    }
}
