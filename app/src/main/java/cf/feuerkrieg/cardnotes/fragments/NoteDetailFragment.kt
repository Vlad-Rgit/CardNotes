package cf.feuerkrieg.cardnotes.fragments

import android.content.Context
import android.os.Bundle
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
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.databinding.FragmentNoteDetailLayoutBinding
import cf.feuerkrieg.cardnotes.dialog.AddGroupDialog
import cf.feuerkrieg.cardnotes.dialog.GroupsPopupWindow
import cf.feuerkrieg.cardnotes.domain.NoteDomain
import cf.feuerkrieg.cardnotes.utils.hideKeyborad
import cf.feuerkrieg.cardnotes.utils.showKeyboard
import cf.feuerkrieg.cardnotes.viewmodels.NoteDetailViewModel
import cf.feuerkrieg.cardnotes.viewmodels.factories.NoteDetailViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis

class NoteDetailFragment: Fragment() {


    companion object {
        const val KEY_CHOSEN_FOLDER = "KeyChosenFolder"
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

                if (endEdit) {
                    navigateUp()
                    viewModel.onEndEditEventComplete()
                }

            })

        var initObserver: Observer<NoteDomain>? = null

        initObserver = Observer<NoteDomain> {
            startPostponedEnterTransition()
            if (binding.txtInputNote.text.toString() != it.value) {
                binding.txtInputNote.append(it.value)
            } else {
                binding.txtInputNote.setSelection(it.value.length)
            }
            viewModel.note.removeObserver(initObserver!!)
        }

        binding.btnClose.setOnClickListener {
            if (viewModel.hasChanges()) {
                MaterialAlertDialogBuilder(requireContext(), R.style.CardNotes_AlertDialog)
                    .setTitle(R.string.close_edit)
                    .setMessage(R.string.do_you_want_to_close_and_decline_all_changes)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        navigateUp()
                    }
                    .setNegativeButton(R.string.no) { _, _ -> }
                    .show()
            } else {
                navigateUp()
            }
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

    private fun navigateUp() {
        setGroupResult()
        hideKeyborad(requireContext(), binding.root)
        findNavController().navigateUp()
    }

    private fun setGroupResult() {
        if (viewModel.note.value!!.groupId == null)
            setFragmentResult(
                KEY_CHOSEN_FOLDER,
                bundleOf("groupId" to -1)
            )
        else
            setFragmentResult(
                KEY_CHOSEN_FOLDER,
                bundleOf("groupId" to viewModel.note.value!!.groupId)
            )
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

}