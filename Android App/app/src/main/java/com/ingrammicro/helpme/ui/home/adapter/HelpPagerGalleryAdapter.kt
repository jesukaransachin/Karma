package com.ingrammicro.helpme.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.databinding.HelpPagerGallaryItemBinding
import com.ingrammicro.helpme.ui.home.HelpViewModel

class HelpPagerGalleryAdapter(private var viewModel: HelpViewModel) :
    RecyclerView.Adapter<HelpPagerGalleryAdapter.GalleryViewHolder>() {

    private var photos = arrayListOf<String>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GalleryViewHolder {
        return GalleryViewHolder(
            HelpPagerGallaryItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = photos.size

    fun getItem(position: Int): String = photos[position]

    fun addItems(items: ArrayList<String>) {
        if (!items.isNullOrEmpty()) {
//        photos.addAll(items)
            photos = items
            notifyDataSetChanged()
        }
    }

    inner class GalleryViewHolder(private val binding: HelpPagerGallaryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener {
                viewModel.navigateToGallery(binding.childPosition)
            }
        }

        fun bind(photo: String, position: Int) {
            binding.apply {
                //    "https://i.imgur.com/H981AN7.jpg"
                childPosition = position
                Glide.with(imageView)
                    .load(photo)
                    .placeholder(R.drawable.ic_placeholder_small)
//                    .error(R.drawable.ic_placeholder_category)
                    .into(imageView)
                executePendingBindings()
            }
        }
    }
}