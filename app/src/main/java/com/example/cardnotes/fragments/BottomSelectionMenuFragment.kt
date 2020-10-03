package com.example.cardnotes.fragments

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cardnotes.R
import com.example.cardnotes.databinding.BottomSelectionMenuFragmentBinding
import com.example.cardnotes.domain.GroupDomain
import com.example.cardnotes.domain.NoteDomain
import com.example.cardnotes.utils.getMaxHeight
import com.example.cardnotes.viewmodels.MainMenuViewModel
import java.lang.reflect.Array


class BottomSelectionMenuFragment: Fragment() {


    private lateinit var binding: BottomSelectionMenuFragmentBinding

    //Late initialization because we can retrieve the height of
    //the fragment host only when it first appeared
    private lateinit var revealAnimator: ObjectAnimator

    private lateinit var viewModel: MainMenuViewModel

    private lateinit var groupsAdapter: ArrayAdapter<GroupDomain>

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

        //Prevent the overlapped views to be clicked
        binding.fragmentHost.setOnTouchListener { _, _ ->
            true
        }

        groupsAdapter = ArrayAdapter<GroupDomain>(requireContext(),
            android.R.layout.simple_list_item_1)

        viewModel.groups.observe(viewLifecycleOwner, Observer {
            groupsAdapter.clear()
            groupsAdapter.add(
                GroupDomain(groupName = "New folder"))
            groupsAdapter.addAll(it)
            groupsAdapter.notifyDataSetChanged()
        })

        binding.btnMove.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setAdapter(groupsAdapter) { di, i ->
                    val group = groupsAdapter.getItem(i)!!
                    viewModel.moveSelectedNotes(group)
                }
                .show()
        }

        return binding.root
    }

    fun show() {

        binding.fragmentHost.post {

            if(!::revealAnimator.isInitialized) {
                initAnimator()
            }

            revealAnimator.cancel()
            revealAnimator.start()
        }


    }

    fun hide() {

        binding.fragmentHost.post {

            if(!::revealAnimator.isInitialized) {
                initAnimator()
            }

            revealAnimator.cancel()
            revealAnimator.reverse()
        }
    }

    private fun initAnimator() {
        revealAnimator = ObjectAnimator.ofFloat(binding.fragmentHost, View.TRANSLATION_Y,

            binding.fragmentHost.height.toFloat(), 0f).apply {

            duration = requireContext().resources
                .getInteger(R.integer.bottom_selection_menu_duration).toLong()

            doOnStart {
                binding.fragmentHost.visibility = View.VISIBLE
            }

            doOnEnd {
                if(binding.fragmentHost.translationY > 0f) {
                    binding.fragmentHost.visibility = View.INVISIBLE
                }
            }
        }
    }

}