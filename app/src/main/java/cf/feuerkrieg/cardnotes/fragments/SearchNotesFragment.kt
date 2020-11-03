package cf.feuerkrieg.cardnotes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.adapters.SearchAdapter
import cf.feuerkrieg.cardnotes.adapters.abstracts.BaseAdapter
import cf.feuerkrieg.cardnotes.databinding.FragmentSearchNotesBinding
import cf.feuerkrieg.cardnotes.decorators.PaddingDecorator
import cf.feuerkrieg.cardnotes.viewmodels.SearchViewModel
import com.google.android.material.transition.MaterialElevationScale

class SearchNotesFragment : Fragment() {

    private lateinit var binding: FragmentSearchNotesBinding
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)
            .get(SearchViewModel::class.java)

        searchAdapter = SearchAdapter()

        searchAdapter.setOnNoteClickListener { note, root ->

            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(
                    R.integer.transition_animation
                ).toLong()
            }

            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(
                    R.integer.transition_animation
                ).toLong()
            }

            val extras = FragmentNavigatorExtras(
                root to resources.getString(R.string.note_detail_transition_name)
            )


            val action = SearchNotesFragmentDirections
                .actionSearchNotesFragmentToNoteDetailFragment(note.id)

            findNavController().navigate(action, extras)
        }

        searchAdapter.setOnFolderClickListener { folder, root ->
            setFragmentResult(
                KEY_CHOSEN_FOLDER,
                bundleOf("groupId" to folder.id)
            )
            findNavController().navigateUp()
        }

        enterTransition = MaterialElevationScale(true)
        exitTransition = MaterialElevationScale(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        postponeEnterTransition()

        binding = FragmentSearchNotesBinding.inflate(
            inflater, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.rvSearch.apply {

            adapter = searchAdapter

            val sidePadding = resources
                .getDimension(R.dimen.side_padding)
                .toInt()

            addItemDecoration(
                PaddingDecorator(
                    sidePadding,
                    sidePadding,
                    sidePadding,
                    sidePadding
                )
            )
        }

        applyLayoutType()

        viewModel.folders.observe(viewLifecycleOwner) {
            searchAdapter.setFolders(it)
        }

        viewModel.notes.observe(viewLifecycleOwner) {
            searchAdapter.setNotes(it)
        }

        binding.txtSearchLayout.setStartIconOnClickListener {
            findNavController().navigateUp()
        }

        binding.rvSearch.doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun applyLayoutType() {
        when (searchAdapter.layoutType) {
            BaseAdapter.LayoutType.List -> {
                binding.rvSearch.layoutManager = LinearLayoutManager(requireContext())
            }
            BaseAdapter.LayoutType.Grid -> {
                binding.rvSearch.layoutManager = StaggeredGridLayoutManager(
                    2,
                    StaggeredGridLayoutManager.VERTICAL
                )
            }
        }
    }

}