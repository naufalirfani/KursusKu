package com.example.kwuapp


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_beranda.*
import kotlinx.android.synthetic.main.fragment_kategori.*

class KategoriFragment : Fragment() {

    var dataKursus: ArrayList<DataKursus> = arrayListOf()
    var dataKursus2: ArrayList<DataKursus> = arrayListOf()
    lateinit var kategori: String
    lateinit var kategori2: String
    val tempat = "main"

    fun newInstance(dataKursus: ArrayList<DataKursus>,
                    dataKursus2: ArrayList<DataKursus>,
                    kategori: String,
                    kategori2: String): KategoriFragment?{
        val fragmentKategori= KategoriFragment()
        val args = Bundle()
        args.putParcelableArrayList("dataKursus", dataKursus)
        args.putParcelableArrayList("dataKursus2", dataKursus2)
        args.putString("kategori", kategori)
        args.putString("kategori2", kategori2)
        fragmentKategori.setArguments(args)
        return fragmentKategori
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataKursus = arguments!!.getParcelableArrayList<DataKursus>("dataKursus")!!
        dataKursus2 = arguments!!.getParcelableArrayList<DataKursus>("dataKursus2")!!
        kategori = arguments!!.getString("kategori")!!
        kategori2 = arguments!!.getString("kategori2")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kategori, container, false)
    }

    override fun onViewCreated(
        view: View,
        @Nullable savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        tv_kategori.text = kategori
        tv_kategori2.text = kategori2

        mRecyclerView2.setHasFixedSize(true)
        val layout = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView2.layoutManager = layout
        val adapter = RVAdapterKursus(activity, dataKursus, tempat)
        adapter.notifyDataSetChanged()
        mRecyclerView2.adapter = adapter

        mRecyclerView3.setHasFixedSize(true)
        val layout2 = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView3.layoutManager = layout2
        val adapter2 = RVAdapterKursus(activity, dataKursus2, tempat)
        adapter.notifyDataSetChanged()
        mRecyclerView3.adapter = adapter2

        iv_bisnis.setOnClickListener {
            val intent = Intent(context, KategoriActivity::class.java)
            intent.putExtra("kategori", getString(R.string.bisnis))
            startActivity(intent)

        }
        iv_desain.setOnClickListener {
            val intent = Intent(context, KategoriActivity::class.java)
            intent.putExtra("kategori", getString(R.string.desain))
            startActivity(intent)

        }
        iv_finansial.setOnClickListener {
            val intent = Intent(context, KategoriActivity::class.java)
            intent.putExtra("kategori", getString(R.string.finasial))
            startActivity(intent)

        }
        iv_fotografi.setOnClickListener {
            val intent = Intent(context, KategoriActivity::class.java)
            intent.putExtra("kategori", getString(R.string.fotografi))
            startActivity(intent)

        }
        iv_kantor.setOnClickListener {
            val intent = Intent(context, KategoriActivity::class.java)
            intent.putExtra("kategori", getString(R.string.kantor))
            startActivity(intent)

        }
        iv_pendidikan.setOnClickListener {
            val intent = Intent(context, KategoriActivity::class.java)
            intent.putExtra("kategori", getString(R.string.pendidikan))
            startActivity(intent)

        }
        iv_penegmbangan.setOnClickListener {
            val intent = Intent(context, KategoriActivity::class.java)
            intent.putExtra("kategori", getString(R.string.pengembangan))
            startActivity(intent)

        }
    }
}
