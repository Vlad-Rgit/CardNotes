package com.example.cardnotes.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cardnotes.R
import com.example.cardnotes.databinding.FragmentNoteDetailLayoutBinding
import com.example.cardnotes.dialog.AddGroupDialog
import com.example.cardnotes.dialog.GroupsPopupWindow
import com.example.cardnotes.domain.NoteDomain
import com.example.cardnotes.utils.hideKeyborad
import com.example.cardnotes.utils.showKeyboard
import com.example.cardnotes.viewmodels.NoteDetailViewModel
import com.example.cardnotes.viewmodels.factories.NoteDetailViewModelFactory
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis

class NoteDetailFragment: Fragment() {


    companion object {
        val KEY_CHOSEN_FOLDER = "KeyChosenFolder"
    }

    private lateinit var binding: FragmentNoteDetailLayoutBinding
    private lateinit var viewModel: NoteDetailViewModel
    private lateinit var groupsPopupWindow: GroupsPopupWindow

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

        groupsPopupWindow = GroupsPopupWindow(requireContext(),
            binding.root as ViewGroup, R.string.no_folder)

        groupsPopupWindow.setGroupChosenCallback {
            if(it.groupId == -1) {
                viewModel.note.value?.groupId = null
                viewModel.folderNote.value = null
            }
            else {
                viewModel.note.value?.groupId = it.groupId
                viewModel.folderNote.value = it
            }

            hideGroupsPopup()
        }

        groupsPopupWindow.setNewGroupRequestCallback {

            hideGroupsPopup()

            val dialog = AddGroupDialog {
                viewModel.addGroup(it)
            }

            dialog.show(childFragmentManager, null)
        }

        viewModel.endEditEvent.observe(viewLifecycleOwner,
            { endEdit ->

                if(endEdit) {

                    if(viewModel.note.value!!.groupId == null)
                        setFragmentResult(KEY_CHOSEN_FOLDER,
                            bundleOf("groupId" to -1))
                    else
                        setFragmentResult(KEY_CHOSEN_FOLDER,
                            bundleOf("groupId" to viewModel.note.value!!.groupId))

                    findNavController().navigateUp()

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
            groupsPopupWindow.replaceGroups(it)
        })

        binding.btnChooseFolder.setOnClickListener {
            hideKeyborad(requireContext(), binding.root)
            binding.overlay.visibility = View.VISIBLE
            groupsPopupWindow.showAsDropDown(binding.btnChooseFolder)
        }

        return binding.root
    }

    private fun hideGroupsPopup() {
        groupsPopupWindow.dismiss()
        binding.overlay.visibility = View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("NoteDetailFragment", "View destroyed")
    }

}