package com.ingrammicro.helpme.ui.home.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.databinding.PhotoGalleryItemBinding

class PhotoGalleryAdapter() : RecyclerView.Adapter<PhotoGalleryAdapter.ViewHolder>() {

    private val TAG = PhotoGalleryAdapter::class.java.simpleName

    private var photos = arrayListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            PhotoGalleryItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder")
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        Log.d(TAG, "onViewAttachedToWindow")
        holder.onViewAttached()
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        Log.d(TAG, "onViewDetachedFromWindow")
        holder.onViewDetached()
    }

    override fun getItemCount(): Int = photos.size

    fun getItem(position: Int): String = photos[position]

    fun addItems(items: ArrayList<String>) {
        if (!items.isNullOrEmpty()) {
//            photos.addAll(items)
            photos = items
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(private val binding: PhotoGalleryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var exoPlayer: SimpleExoPlayer

        fun bind(mediaName: String) {
            binding.apply {
                //    "https://i.imgur.com/H981AN7.jpg"
                item = mediaName
                val extension: String = mediaName.substring(mediaName.lastIndexOf("."))
                if (extension == ".mp4") {
                    imageView.visibility = View.GONE
                    playerView.visibility = View.VISIBLE
//                    videoView.setVideoURI(Uri.parse(mediaName))
//                    videoView.setVideoPath(mediaName)

//                    initializePlayer(videoSample)
//                    videoView.start()
                } else {
                    playerView.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    Glide.with(imageView)
                        .load(item)
                        .placeholder(R.drawable.ic_placeholder_big)
//                        .error(R.drawable.ic_placeholder_category)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
                        .into(imageView)
                }
                executePendingBindings()
            }
        }

        fun initializePlayer() {

            val videoSample =
                "https://developers.google.com/training/images/tacoma_narrows.mp4"

            binding.item?.let { url ->
                binding.apply {
                    exoPlayer = SimpleExoPlayer.Builder(binding.root.context).build()
                    binding.playerView.player = exoPlayer

                    val uri = Uri.parse(url)

                    val dataSourceFactory: DataSource.Factory =
                        DefaultDataSourceFactory(binding.root.context, "exoplayer-codelab")
                    val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(uri)
                    exoPlayer.prepare(mediaSource)
                    exoPlayer.playWhenReady = true
                }
            }
        }

        private fun releasePlayer() {
            exoPlayer.release()
        }

        fun onViewAttached() {
            binding.apply {
                if (playerView.visibility == View.VISIBLE) {
                    initializePlayer()
                }
            }
        }

        fun onViewDetached() {
            binding.apply {
                if (playerView.visibility == View.VISIBLE) {
                    releasePlayer()
                }
            }
        }
    }
}