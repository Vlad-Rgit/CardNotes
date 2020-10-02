package com.example.cardnotes.fragments

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cardnotes.databinding.BottomSelectionMenuFragmentBinding
import com.example.cardnotes.viewmodels.MainMenuViewModel

class BottomSelectionMenuFragment: Fragment() {


    private lateinit var binding: BottomSelectionMenuFragmentBinding
    private lateinit var heightAnimator: ValueAnimator

    private lateinit var viewModel: MainMenuViewModel

    override fun onAttach(context: Context) {

        super.onAttach(context)

        viewModel = ViewModelProvider(requireParentFragment())
            .get(MainMenuViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = BottomSelectionMenuFragmentBinding
            .inflate(inflater, container, false)

        binding.fragmentHost.visibility = View.INVISIBLE
        binding.btnDelete.setOnClickListener {
            viewModel.removeSelectedNotes()
        }

        return binding.root
    }


    fun show() {

        if(::heightAnimator.isInitialized) {
            heightAnimator.cancel()
        }

        binding.fragmentHost.post {

            heightAnimator = ValueAnimator.ofInt(0, binding.fragmentHost.height)
                .apply {

                    addUpdateListener {
                        val animatedValue = it.animatedValue as Int
                        val layoutParams = binding.fragmentHost.layoutParams
                        layoutParams.height = animatedValue
                        binding.fragmentHost.layoutParams = layoutParams
                        binding.fragmentHost.postInvalidate()
                    }

                    doOnStart {
                        binding.fragmentHost.visibility = View.VISIBLE
                    }
                }

            Log.d("BottomAnimation", "Animation Starts")

            heightAnimator.start()
        }

    }

    fun hide() {
        if(::heightAnimator.isInitialized) {
            heightAnimator.cancel()
        }


        binding.fragmentHost.post {

            heightAnimator = ValueAnimator.ofInt(binding.fragmentHost.height, 0)
                .apply {

                    addUpdateListener {
                        val animatedValue = it.animatedValue as Int
                        val layoutParams = binding.fragmentHost.layoutParams
                        layoutParams.height = animatedValue
                        binding.fragmentHost.layoutParams = layoutParams
                        binding.fragmentHost.postInvalidate()
                    }

                    doOnEnd {
                        binding.fragmentHost.visibility = View.GONE
                    }

                }

            Log.d("BottomAnimation", "Animation Starts")

            heightAnimator.start()
        }
    }

}