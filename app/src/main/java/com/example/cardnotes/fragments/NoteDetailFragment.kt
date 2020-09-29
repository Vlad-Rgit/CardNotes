package com.example.cardnotes.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cardnotes.R
import com.example.cardnotes.databinding.FragmentNoteDetailLayoutBinding
import com.example.cardnotes.viewmodels.NoteDetailViewModel
import com.example.cardnotes.viewmodels.factories.NoteDetailViewModelFactory

class NoteDetailFragment: Fragment() {

    private lateinit var viewModel: NoteDetailViewModel

    override fun onAttach(context: Context) {

        super.onAttach(context)

        val args = NoteDetailFragmentArgs
            .fromBundle(requireArguments())

        viewModel = ViewModelProvider(this,
                NoteDetailViewModelFactory(args.noteId))
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

        viewModel.endEditEvent.observe(viewLifecycleOwner,
            Observer { endEdit ->

                if(endEdit) {
                    findNavController().navigate(
                        R.id.action_noteDetailFragment_to_mainMenuFragment)

                    viewModel.onEndEditEventComplete()
                }

        })

        return binding.root
    }

}