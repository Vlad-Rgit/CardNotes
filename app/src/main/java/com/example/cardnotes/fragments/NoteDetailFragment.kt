package com.example.cardnotes.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cardnotes.R
import com.example.cardnotes.databinding.FragmentNoteDetailLayoutBinding
import com.example.cardnotes.dialog.AddGroupDialog
import com.example.cardnotes.domain.GroupDomain
import com.example.cardnotes.viewmodels.NoteDetailViewModel
import com.example.cardnotes.viewmodels.factories.NoteDetailViewModelFactory

class NoteDetailFragment: Fragment() {

    private lateinit var viewModel: NoteDetailViewModel
    private lateinit var groupsAdapter: ArrayAdapter<GroupDomain>

    override fun onAttach(context: Context) {

        super.onAttach(context)

        val args = NoteDetailFragmentArgs
            .fromBundle(requireArguments())

        viewModel = ViewModelProvider(this,
                NoteDetailViewModelFactory(args.noteId, args.newGroupId))
            .get(NoteDetailViewModel::class.java)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = FragmentNoteDetailLayoutBinding.inflate(
            inflater, container, false)


        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        groupsAdapter = ArrayAdapter<GroupDomain>(requireContext(),
            android.R.layout.simple_list_item_1)

        viewModel.endEditEvent.observe(viewLifecycleOwner,
            { endEdit ->

                if(endEdit) {
                    findNavController().navigate(
                        R.id.action_noteDetailFragment_to_mainMenuFragment)

                    viewModel.onEndEditEventComplete()
                }

        })

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

}