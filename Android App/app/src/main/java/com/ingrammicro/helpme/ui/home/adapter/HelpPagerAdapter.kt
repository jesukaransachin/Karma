package com.ingrammicro.helpme.ui.home.adapter

import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.model.Help
import com.ingrammicro.helpme.databinding.HelpPagerItemBinding
import com.ingrammicro.helpme.ui.home.HelpViewModel
import com.ingrammicro.helpme.utils.AppUtils.shareImage

class HelpPagerAdapter(private var viewModel: HelpViewModel) :
    RecyclerView.Adapter<HelpPagerAdapter.PagerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PagerViewHolder {
        return PagerViewHolder(
            HelpPagerItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = viewModel.helpList.size

    fun getItem(position: Int): Help = viewModel.helpList[position]

    inner class PagerViewHolder(val binding: HelpPagerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var helpGalleryAdapter: HelpPagerGalleryAdapter

        init {
            binding.setClickListener {
                binding.item?.let { help ->
                    binding.apply {
                        viewModel.toggleDetails()
                    }
                }
            }

            binding.helpDetails.setClickListener {
                viewModel.onHelp(binding.itemPosition)
            }

            binding.helpDetails.setShareClickListener { view ->
                binding.helpDetails.buttonLayout.visibility = View.GONE
                shareImage(view.context, binding.root)
                binding.helpDetails.buttonLayout.visibility = View.VISIBLE
            }

            binding.apply {
                helpDetails.recyclerView.apply {
                    layoutManager = LinearLayoutManager(
                        binding.root.context,
                        LinearLayoutManager.HORIZONTAL, false
                    )
//                    isNestedScrollingEnabled = false
//                    ViewCompat.setNestedScrollingEnabled(this, false)
                    helpGalleryAdapter = HelpPagerGalleryAdapter(viewModel)
                    adapter = helpGalleryAdapter
//                    setRecycledViewPool(viewPool)
                }

                description.movementMethod = ScrollingMovementMethod()
            }
        }

        fun bind(help: Help, position: Int) {
            binding.apply {
                item = help
                itemPosition = position
                if (viewModel.mExpanded) {
                    expandable.expand(false)
                    description.visibility = View.VISIBLE
                } else {
                    description.visibility = View.GONE
                    expandable.collapse(false)
                }
                helpGalleryAdapter.addItems(help.photos)
                description.movementMethod = ScrollingMovementMethod()
                Glide.with(imageView)
                    .load(help.category.url)
                    .placeholder(R.drawable.ic_placeholder_category)
//                    .error(R.drawable.ic_placeholder_category)
                    .into(imageView)
                executePendingBindings()
            }
        }
    }
}