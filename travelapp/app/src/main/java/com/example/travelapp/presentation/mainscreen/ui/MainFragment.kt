package com.example.travelapp.presentation.mainscreen.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.travelapp.presentation.mainscreen.viewmodels.MainFragmentViewModel

class MainFragment: Fragment() {

    private lateinit var viewModel: MainFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)
            .get(MainFragmentViewModel::class.java)
        viewModel.loadCities()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    private fun initRecyclerView() {

    }
}