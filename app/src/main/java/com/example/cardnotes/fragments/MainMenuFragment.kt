package com.example.cardnotes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cardnotes.databinding.FragmentMainMenuBinding

class MainMenuFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMainMenuBinding.inflate(
            inflater, container, false
        )

        return binding.root
    }

}