package com.example.cardnotes.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cardnotes.R
import com.example.cardnotes.databinding.FragmentNoteDetailLayoutBinding
import com.example.cardnotes.dialog.AddGroupDialog
import com.example.cardnotes.domain.GroupDomain
import com.example.cardnotes.domain.NoteDomain
import com.example.cardnotes.utils.hideKeyborad
import com.example.cardnotes.utils.showKeyboard
import com.example.cardnotes.viewmodels.NoteDetailViewModel
import com.example.cardnotes.viewmodels.factories.NoteDetailViewModelFactory
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis

class NoteDetailFragment: Fragment() {

    private lateinit var binding: FragmentNoteDetailLayoutBinding
    private lateinit var viewModel: NoteDetailViewModel
    private lateinit var groupsAdapter: ArrayAdapter<GroupDomain>

    override fun onAttach(context: Context) {

        super.onAttach(context)

        val args = NoteDetailFragmentArgs
            .fromBundle(requireArguments())

        viewModel = ViewModelProvider(this,
                NoteDetailViewModelFactory(args.noteId, args.newGroupId))
            .get(NoteDetailViewModel::class.java)

        if(args.noteId == -1) {
            createAnimForNewNote()
        }
        else {
            createAnimForExistingNote()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        postponeEnterTransition()

        binding = FragmentNoteDetailLayoutBinding.inflate(
            inflater, container, false)


        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        groupsAdapter = ArrayAdapter<GroupDomain>(requireContext(),
            android.R.layout.simple_list_item_1)

        viewModel.endEditEvent.observe(viewLifecycleOwner,
            { endEdit ->

                if(endEdit) {
                    findNavController().popBackStack()

                    hideKeyborad(requireContext(), binding.root)

                    viewModel.onEndEditEventComplete()
                }

        })

        var initObserver: Observer<NoteDomain>? = null

        initObserver = Observer<NoteDomain> {
            startPostponedEnterTransition()
            if(binding.txtInputNote.text.toString() != it.value) {
                binding.txtInputNote.append(it.value)
            }
            else {
                binding.txtInputNote.setSelection(it.value.length)
            }
            viewModel.note.removeObserver(initObserver!!)
        }

        viewModel.note.observe(viewLifecycleOwner, initObserver)


        viewModel.groups.observe(viewLifecycleOwner, {
            groupsAdapter.clear()
            groupsAdapter.add(
                GroupDomain(
                groupName = requireContext()
                    .getString(R.string.new_folder)))
            groupsAdapter.addAll(it)
        })

        binding.btnChooseFolder.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setAdapter(groupsAdapter) {
                        di, i ->
                    if(i == 0) {
                        AddGroupDialog(viewModel::addGroup)
                            .show(childFragmentManager, null)
                    }
                    else {
                        val group = groupsAdapter.getItem(i)
                        viewModel.setGroup(group!!)
                    }
                }
                .show()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.txtInputNote.requestFocus()
        showKeyboard(requireContext(), binding.txtInputNote)
    }

    private fun createAnimForExistingNote() {

        sharedElementEnterTransition = MaterialContainerTransform().apply {

            drawingViewId = R.id.nav_host

            duration = resources.getInteger(
                R.integer.transition_animation).toLong()

            scrimColor = ContextCompat.getColor(requireContext(),
                android.R.color.transparent)

            setAllContainerColors(
                ContextCompat.getColor(requireContext(), android.R.color.white))
        }
    }

    private fun createAnimForNewNote() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(
                R.integer.transition_animation).toLong()
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(
                R.integer.transition_animation).toLong()
        }
    }

}