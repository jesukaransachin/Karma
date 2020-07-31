package com.ingrammicro.helpme.ui.heroes

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.data.EventObserver
import com.ingrammicro.helpme.data.model.Hero
import com.ingrammicro.helpme.data.model.SuperHero
import com.ingrammicro.helpme.databinding.FragmentHeroesDialogBinding
import com.ingrammicro.helpme.databinding.FragmentHeroesDialogItemBinding
import com.ingrammicro.imdelivery.SharedViewModel

class HeroesDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: HeroesViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var itemAdapter: ItemAdapter
    private lateinit var binding: FragmentHeroesDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_heroes_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHeroesDialogBinding.bind(view)
        with(binding.list) {
            layoutManager = LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL, false
            )
            itemAdapter = ItemAdapter()
            adapter = itemAdapter
        }

        binding.setClickListener {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet: FrameLayout? =
                dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
        return dialog
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        subscribeToChanges()
    }

    fun subscribeToChanges() {

        viewModel.fetchHeroes.observe(viewLifecycleOwner, EventObserver { resource ->
            viewModel.parseResponse(resource)
        })

        viewModel.notifyAdapter.observe(viewLifecycleOwner, Observer {
            itemAdapter.notifyDataSetChanged()
        })

        viewModel.moveToProfile.observe(viewLifecycleOwner, EventObserver { hero ->
            sharedViewModel.superHero.value = hero
            findNavController().navigate(R.id.navigate_to_hero)
        })
    }

    private inner class ViewHolder internal constructor(
        private val binding: FragmentHeroesDialogItemBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        init {
            binding.setClickListener {
                binding.item?.let { hero ->
                    viewModel.navigateToProfile(hero)
                }
            }
        }

        fun bind(hero: SuperHero) {
            binding.apply {
                //    "https://i.imgur.com/H981AN7.jpg"
                Glide.with(imageView)
                    .load("")
                    .placeholder(hero.profilePic)
//                    .error(R.drawable.ic_placeholder_hero)
                    .into(imageView)
                item = hero
                executePendingBindings()
            }
            if (hero.isSpecial) {
                binding.imgbtnStar.visibility = View.VISIBLE
            } else {
                binding.imgbtnStar.visibility = View.INVISIBLE
            }
        }
    }

    private inner class ItemAdapter internal constructor() :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                FragmentHeroesDialogItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            holder.bind(item)
        }

        override fun getItemCount(): Int = viewModel.superHeroes.size

        private fun getItem(position: Int): SuperHero = viewModel.superHeroes[position]
    }
}