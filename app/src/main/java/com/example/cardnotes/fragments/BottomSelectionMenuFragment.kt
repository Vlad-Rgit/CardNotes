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
import androidx.lifecycle.lifecycleScope
import com.example.cardnotes.R
import com.example.cardnotes.databinding.BottomSelectionMenuFragmentBinding
import com.example.cardnotes.dialog.AddGroupDialog
import com.example.cardnotes.domain.GroupDomain
import com.example.cardnotes.domain.NoteDomain
import com.example.cardnotes.utils.styleDialogButton
import com.example.cardnotes.viewmodels.MainMenuViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Array


class BottomSelectionMenuFragment: Fragment() {


    private lateinit var binding: BottomSelectionMenuFragmentBinding


    private lateinit var viewModel: MainMenuViewModel

    private lateinit var groupsAdapter: ArrayAdapter<GroupDomain>


    private var onEndSelectionCallback: Runnable? = null

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


        binding.btnDelete.setOnClickListener {

            val res = requireContext().resources
            val count = viewModel.selectedNotes.size

            val title = res.getString(R.string.delete_notes)
            val message = res.getQuantityString(
                R.plurals.delete_notes, count, count)

            val dialog = AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(res.getString(R.string.no)) { _, _ -> }
                .setPositiveButton(res.getString(R.string.yes)) {
                    _, _ ->

                    viewModel.removeSelectedNotes()
                    onEndSelectionCallback?.run()
                }
                .show()

            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            styleDialogButton(negativeButton)
            styleDialogButton(positiveButton)
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
                GroupDomain(groupName = requireContext()
                    .getString(R.string.new_folder)))

            groupsAdapter.addAll(it)
            groupsAdapter.notifyDataSetChanged()
        })

        binding.btnMove.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setAdapter(groupsAdapter) { di, i ->

                    if(i == 0) {
                        AddGroupDialog {
                            lifecycleScope.launch(Dispatchers.Main) {
                                it.groupId = viewModel.addGroupImpl(it)
                                viewModel.moveSelectedNotes(it)
                                onEndSelectionCallback?.run()
                            }
                        }.show(childFragmentManager, null)
                    }
                    else {
                        val group = groupsAdapter.getItem(i)!!
                        viewModel.moveSelectedNotes(group)
                        onEndSelectionCallback?.run()
                    }
                }
                .show()
        }

        return binding.root
    }

    fun setOnEndSelectionCallback(callback: Runnable) {
        onEndSelectionCallback = callback
    }

}