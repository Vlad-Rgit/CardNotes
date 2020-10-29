package cf.feuerkrieg.cardnotes.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.databinding.BottomSelectionMenuFragmentBinding
import cf.feuerkrieg.cardnotes.dialog.AddGroupDialog
import cf.feuerkrieg.cardnotes.domain.FolderDomain
import cf.feuerkrieg.cardnotes.viewmodels.MainMenuViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BottomSelectionMenuFragment: Fragment() {


    private lateinit var binding: BottomSelectionMenuFragmentBinding


    private lateinit var viewModel: MainMenuViewModel

    private lateinit var groupsAdapter: ArrayAdapter<FolderDomain>


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
            val count = viewModel.selectedItems.size

            val title = res.getString(R.string.delete_notes)
            val message = res.getQuantityString(
                R.plurals.delete_notes, count, count)

            MaterialAlertDialogBuilder(requireContext(), R.style.CardNotes_AlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(res.getString(R.string.no)) { _, _ -> }
                .setPositiveButton(res.getString(R.string.yes)) {
                    _, _ ->

                    viewModel.removeSelectedNotes()
                    onEndSelectionCallback?.run()
                }
                .show()

        }

        groupsAdapter = ArrayAdapter<FolderDomain>(
            requireContext(),
            android.R.layout.simple_list_item_1
        )

        viewModel.folders.observe(viewLifecycleOwner, {
            groupsAdapter.clear()

            groupsAdapter.add(
                FolderDomain(
                    name = requireContext()
                        .getString(R.string.new_folder)
                )
            )

            groupsAdapter.addAll(it)
            groupsAdapter.notifyDataSetChanged()
        })

        binding.btnMove.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setAdapter(groupsAdapter) { di, i ->

                    if(i == 0) {
                        AddGroupDialog {
                            lifecycleScope.launch(Dispatchers.Main) {
                                it.id = viewModel.addGroupImpl(it)
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