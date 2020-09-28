package com.example.cardnotes.fragments

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.cardnotes.R
import com.example.cardnotes.adapters.NotesAdapter
import com.example.cardnotes.databinding.FragmentMainMenuBinding
import com.example.cardnotes.decorators.PaddingDecorator
import com.example.cardnotes.utils.ItemTouchHelperCallback
import com.example.cardnotes.viewmodels.MainMenuViewModel

class MainMenuFragment: Fragment() {

    private lateinit var viewModel: MainMenuViewModel
    private lateinit var activity: AppCompatActivity
    private lateinit var binding: FragmentMainMenuBinding

    private val opacityAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        addUpdateListener {
            val animatedValue = it.animatedValue as Float
            binding.selectionHost.alpha = animatedValue
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        activity = requireActivity() as AppCompatActivity

        viewModel = ViewModelProvider(this)
            .get(MainMenuViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        binding = FragmentMainMenuBinding.inflate(
            inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        val notesAdapter = NotesAdapter(viewLifecycleOwner,
            requireContext(), R.layout.note_item)

        notesAdapter.setStartSelectionListener(::startSelection)

        binding.rvNotes.apply {

            setHasFixedSize(true)

            layoutManager = StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL)

            addItemDecoration(PaddingDecorator(
                16,16,16,16))

            ItemTouchHelper(ItemTouchHelperCallback())
                .attachToRecyclerView(this)

            adapter = notesAdapter
        }

        viewModel.notes.observe(viewLifecycleOwner) {
            notesAdapter.replaceAll(it)
        }

        binding.btnAcceptEdit.setOnClickListener {
            endSelection()
            notesAdapter.acceptEdit()
        }

        binding.btnEndEdit.setOnClickListener {
            endSelection()
            notesAdapter.cancelEdit()
        }

        binding.btnAddNote.setOnClickListener {

            val action = MainMenuFragmentDirections
                .actionMainMenuFragmentToNoteDetailFragment()

            findNavController().navigate(action)
        }

        return binding.root
    }


    private fun startSelection() {
        if(binding.selectionHost.alpha == 0f) {
            opacityAnimator.start()
        }
    }

    private fun endSelection() {
        opacityAnimator.reverse()
    }


}