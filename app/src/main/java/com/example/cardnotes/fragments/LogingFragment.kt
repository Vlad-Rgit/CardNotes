package com.example.cardnotes.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

private const val LOGGING_FRAGMENT = "LoggingFragment"

open class LoggingFragment: Fragment() {

    init {
        Log.d(LOGGING_FRAGMENT, "onInit")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(LOGGING_FRAGMENT, "onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOGGING_FRAGMENT, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(LOGGING_FRAGMENT, "onCreateView")
        return null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(LOGGING_FRAGMENT, "onActivityCreated")
    }

    override fun onStart() {
        super.onStart()
        Log.d(LOGGING_FRAGMENT, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOGGING_FRAGMENT, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(LOGGING_FRAGMENT, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(LOGGING_FRAGMENT, "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(LOGGING_FRAGMENT, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(LOGGING_FRAGMENT, "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(LOGGING_FRAGMENT, "onDetach")
    }

}