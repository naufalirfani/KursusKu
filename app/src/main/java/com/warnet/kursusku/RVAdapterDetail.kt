package com.warnet.kursusku

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.rv_detail.view.*

class RVAdapterDetail(private val context: Context?,
                      private val listKursus: DataKursus,
                      private val arrayDipelajari: ArrayList<String>,
                      private val arraySyarat: ArrayList<String>) : RecyclerView.Adapter<RVAdapterDetail.Holder>() {

    private lateinit var playerView: PlayerView
    private lateinit var simpleExoPlayer: SimpleExoPlayer

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Holder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.rv_detail, viewGroup, false)
        return Holder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val kursus = listKursus

        Glide.with(holder.itemView.context)
            .load(kursus.gambar)
//                .apply(RequestOptions().fitCenter().format(DecodeFormat.PREFER_ARGB_8888).override(Target.SIZE_ORIGINAL))
            .into(holder.view.iv_detail)
        holder.view.tv_detail_nama.text = kursus.nama
        val harga = "Rp${kursus.harga}"
        holder.view.tv_detail_harga.text = harga
        holder.view.tv_detail_hargaasli.text = "Rp49.900"
        holder.view.tv_detail_hargaasli.paintFlags = holder.view.tv_detail_hargaasli.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        val ratingValue = kursus.rating.toFloat()
        holder.view.detail_ratingbar.rating = ratingValue
        holder.view.tv_detail_rating.text = kursus.rating
        val terjual = "${kursus.pengguna} Terjual"
        holder.view.tv_detail_terjual.text = terjual
        holder.view.tv_detail_deskripsi.text = kursus.deskripsi

        holder.view.rv_dipelajari.setHasFixedSize(true)
        holder.view.rv_dipelajari.layoutManager = LinearLayoutManager(context)
        val adapter = RVADetailList(arrayDipelajari, "dipelajari")
        adapter.notifyDataSetChanged()
        holder.view.rv_dipelajari.isNestedScrollingEnabled = false
        holder.view.rv_dipelajari.adapter = adapter

        holder.view.rv_syarat.setHasFixedSize(true)
        holder.view.rv_syarat.layoutManager = LinearLayoutManager(context)
        val adapter2 = RVADetailList(arraySyarat, "syarat")
        adapter2.notifyDataSetChanged()
        holder.view.rv_syarat.isNestedScrollingEnabled = false
        holder.view.rv_syarat.adapter = adapter2

        if(kursus.video != "kosong"){
            holder.view.iv_detail.visibility = View.GONE
            initExoPlayer(holder.view.video_view, holder.view.progressBar, kursus.video)
        }
        else{
            holder.view.iv_detail.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {

        return 1
    }

    fun initExoPlayer(player: PlayerView, progressBar: ProgressBar, url: String){
        playerView = player
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context)
        playerView.player = simpleExoPlayer
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "appname"))
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(url))
        simpleExoPlayer.prepare(videoSource)
        simpleExoPlayer.playWhenReady = true

        simpleExoPlayer.addListener(object : ExoPlayer.EventListener {
            override fun onPlayerStateChanged(
                playWhenReady: Boolean,
                playbackState: Int
            ) {
                if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.visibility = View.VISIBLE
                } else {
                    progressBar.visibility = View.INVISIBLE
                }
            }
            override fun onPlayerError(error: ExoPlaybackException) {
                progressBar.visibility = View.VISIBLE
                simpleExoPlayer.stop()
                simpleExoPlayer.playWhenReady = false
            }
        })
    }

    class Holder(val view: View) : RecyclerView.ViewHolder(view)
}