package com.ingrammicro.helpme.ui.heroes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.appbar.MaterialToolbar
import com.ingrammicro.helpme.R
import com.ingrammicro.helpme.databinding.FragmentHeroBinding
import com.ingrammicro.helpme.utils.SessionManager
import com.ingrammicro.imdelivery.SharedViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

class HeroFragment : Fragment() {

    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: HeroViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var binding: FragmentHeroBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHeroBinding.inflate(inflater, container, false)
        binding.profileBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topAppBar = requireActivity().findViewById(R.id.topAppBar)
        topAppBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        topAppBar.visibility = View.VISIBLE
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(context)
        viewModel = ViewModelProviders.of(this).get(HeroViewModel::class.java)
        sharedViewModel.superHero.observe(viewLifecycleOwner, Observer { hero ->
            var name = hero.phone
            var email = "No email found"
            if (hero.name != "") {
                name = hero.name
            }
            if (hero.email != "") {
                email = hero.email
            }
            binding.include.userFullName.text = name
            binding.include.userEmailId.text = email
            binding.include.helpedPoints.text = hero.helpExtended.toString()
            Glide.with(this)
                .load("")
                .placeholder(hero.profilePic)
                .timeout(60000)
                .centerCrop()
                .into(binding.include.profileImage)
            binding.include.verifiedIcon.visibility = View.GONE
            binding.include.profileContainer.visibility = View.VISIBLE

            binding.description.setText(hero.description)
            binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
//                super.onReady(youTubePlayer)
                    val videoId = hero.videoId
                    youTubePlayer.cueVideo(videoId, 0f)
                }
            })

            binding.contactButton.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_DIAL
                intent.data = Uri.parse("tel: ${hero.phone}")
                startActivity(intent)
            }
        })

        lifecycle.addObserver(binding.youtubePlayerView)


//        youTubePlayer

//        binding.youtubePlayerView.getYouTubePlayerWhenReady(youTubePlayer -> {
//            // do stuff with it
//            youTubePlayer.cueVideo(String videoId, float startTime)
//        })

//        viewModel.fetchProfile(args.userId)
        //viewModel.fetchProfile("65e722614aafa9dffbcfe4f2aea43dd8")
        //viewModel.fetchMyHelps("65e722614aafa9dffbcfe4f2aea43dd8")
//        binding.include.profileContainer.visibility = View.INVISIBLE
    }
}