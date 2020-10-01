package com.example.cardnotes.fragments

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.cardnotes.R
import com.example.cardnotes.adapters.NotesAdapter
import com.example.cardnotes.databinding.FragmentMainMenuBinding
import com.example.cardnotes.decorators.PaddingDecorator
import com.example.cardnotes.dialog.GroupsPopupWindow
import com.example.cardnotes.utils.ItemTouchHelperCallback
import com.example.cardnotes.viewmodels.MainMenuViewModel


class MainMenuFragment: Fragment() {

    private lateinit var viewModel: MainMenuViewModel
    private lateinit var activity: AppCompatActivity
    private lateinit var binding: FragmentMainMenuBinding

    /**
     * Popup for choosing group or creating
     * new group
     */
    private lateinit var groupsPopupWindow: GroupsPopupWindow


    /**
     * If user is typing in search field
     */
    private var isSearching = false


    /**
     * Opacity animation for selection host at the top of
     * the screen
     */
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
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainMenuBinding.inflate(
            inflater, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        //Init group groups popup window
        groupsPopupWindow = GroupsPopupWindow(
            requireContext(), binding.mainMenuHost
        )

        viewModel.groups.observe(viewLifecycleOwner,
            Observer { groups ->
                groupsPopupWindow.replaceGroups(groups)
            })

        //Init note adapter for recycler view
        val notesAdapter = NotesAdapter(
            viewLifecycleOwner,
            requireContext(), R.layout.note_item
        )

        //If the user long presses card note
        //the selection mode is enabled
        notesAdapter.setStartSelectionListener(::startSelection)

        //If the user changes the position of the note
        //we must reflect this changes in the database
        notesAdapter.setNoteUpdatedCallback(viewModel::updateNote)

        //Navigate to NoteDetailsFragment
        //for editing clicked note
        notesAdapter.setOnNoteClickCallback {

            val action = MainMenuFragmentDirections
                .actionMainMenuFragmentToNoteDetailFragment(it.noteId)

            findNavController().navigate(action)
        }


        //Init notes recycler view
        binding.rvNotes.apply {

            setHasFixedSize(true)

            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )

            addItemDecoration(
                PaddingDecorator(
                    16, 16, 16, 16
                )
            )

            ItemTouchHelper(ItemTouchHelperCallback())
                .attachToRecyclerView(this)

            adapter = notesAdapter
        }

        //Attach observer to notes collection
        //And reflect any changes within the adapter
        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            notesAdapter.replaceAll(notes)
            notesAdapter.sortByPosition()
        }

        //End selection of the notes and
        //remove all the selected notes
        binding.btnAcceptEdit.setOnClickListener {
            endSelection()
            val removed = notesAdapter.getSelectedNotes()
            notesAdapter.disableSelection()
            viewModel.removeNotes(removed)
        }

        //End selection but not remove the selected notes
        binding.btnEndEdit.setOnClickListener {
            endSelection()
            notesAdapter.disableSelection()
        }


        //Attach listener to the search text field
        //Filter listener each time the user types a character
        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrBlank() && isSearching) {
                    clearSearch()
                } else {
                    //if it is first character
                    //show the close icon
                    //instead of search icon
                    if (!isSearching)
                        startSearch()

                    viewModel.searchQuery = s.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        //Navigate to NoteDetailsFragment to add the note
        binding.btnAddNote.setOnClickListener {

            val action = MainMenuFragmentDirections
                .actionMainMenuFragmentToNoteDetailFragment()

            findNavController().navigate(action)
        }

        binding.btnFolder.setOnClickListener {

            if(groupsPopupWindow.isShowing) {
                groupsPopupWindow.dismiss()
                hideDim()
            }
            else {
                showDim()
                groupsPopupWindow.showAsDropDown(binding.btnFolder)
            }
        }

        binding.dim.setOnClickListener {
            groupsPopupWindow.dismiss()
            hideDim()
        }


        return binding.root
    }


    /**
     * Show the selection host
     */
    private fun startSelection() {
        if(binding.selectionHost.alpha == 0f) {
            opacityAnimator.start()
        }
    }

    /**
     * Hide the selection host
     */
    private fun endSelection() {
        opacityAnimator.reverse()
    }

    fun setEnableForAllViews(parent: View, enable: Boolean) {
        parent.isEnabled = enable
        if(parent is ViewGroup) {
            for(child in parent.children) {
                setEnableForAllViews(child, enable)
            }
        }
    }

    private fun showDim() {
        binding.dim.visibility = View.VISIBLE
    }

    private fun hideDim() {
        binding.dim.visibility = View.GONE
    }

    /**
     * Show close icon instead of search icon
     * in the search text field
     */
    private fun startSearch() {

        isSearching = true

        binding.txtSearchLayout.endIconDrawable =
            requireContext().resources.getDrawable(R.drawable.baseline_close_black_24)

        binding.txtSearchLayout.setEndIconOnClickListener {
            clearSearch()
        }
    }

    /**
     * Show search icon instead of close icon
     * in the text field and clear this text field
     */
    private fun clearSearch() {
        isSearching = false
        binding.edSearch.setText("")
        binding.txtSearchLayout.endIconDrawable =
            requireContext().resources.getDrawable(R.drawable.baseline_search_black_24)
    }


}