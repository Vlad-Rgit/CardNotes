package cf.feuerkrieg.cardnotes.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.activities.MainActivity
import cf.feuerkrieg.cardnotes.adapters.FolderPickerAdapter
import cf.feuerkrieg.cardnotes.adapters.NotesAdapter
import cf.feuerkrieg.cardnotes.adapters.abstracts.BaseAdapter
import cf.feuerkrieg.cardnotes.databinding.FragmentMainMenuBinding
import cf.feuerkrieg.cardnotes.databinding.GroupDialogLayoutBinding
import cf.feuerkrieg.cardnotes.decorators.PaddingDecorator
import cf.feuerkrieg.cardnotes.dialog.AddGroupDialog
import cf.feuerkrieg.cardnotes.domain.BaseDomain
import cf.feuerkrieg.cardnotes.domain.FolderDomain
import cf.feuerkrieg.cardnotes.domain.NoteDomain
import cf.feuerkrieg.cardnotes.utils.hideKeyborad
import cf.feuerkrieg.cardnotes.utils.toggleHideKeyboard
import cf.feuerkrieg.cardnotes.viewmodels.MainMenuViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFade
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val deleteFolderId = 1
private const val renameFolderId = 2
private const val KEY_LAYOUT_TYPE = "KeyLayoutType"
private const val KEY_SCROLL_POSITION = "KeyScrollPosition"

const val KEY_CHOSEN_FOLDER = "KeyChosenFolder"


class MainMenuFragment : Fragment() {

    private lateinit var viewModel: MainMenuViewModel
    private lateinit var activity: MainActivity
    private var bindingHolder: FragmentMainMenuBinding? = null

    private val binding: FragmentMainMenuBinding
        get() = bindingHolder!!

    private lateinit var notesAdapter: NotesAdapter
    private var isNavigating = false

    private var rvNotesLastPosition = 0

    /**
     * Options menu
     */
    private lateinit var popupMenu: Menu

    /**
     * If user is typing in search field
     */
    private var isSearching = false


    var selectedItemsString = MutableLiveData<String>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
        viewModel = ViewModelProvider(this)
            .get(MainMenuViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Init note adapter for recycler view
        notesAdapter = NotesAdapter(viewModel.selectedItemsAccessor)

        savedInstanceState?.let {
            notesAdapter.layoutType = BaseAdapter.LayoutType
                .valueOf(it.getString(KEY_LAYOUT_TYPE)!!)
        }


        //If the user long presses card note
        //the selection mode is enabled
        notesAdapter.setStartSelectionListener(::startSelection)

        //If the user changes the position of the note
        //we must reflect this changes in the database
        notesAdapter.setNoteUpdatedCallback(viewModel::updateNote)


        //Navigate to NoteDetailsFragment
        //for editing clicked note
        notesAdapter.setOnNoteClickListener { note, root ->

            //Just to prevent double click on card
            if (isNavigating)
                return@setOnNoteClickListener

            isNavigating = true

            hideSearchKeyboard()

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

            val action = MainMenuFragmentDirections
                .actionMainMenuFragmentToNoteDetailFragment(note.id)

            findNavController().navigate(action, extras)
        }

        notesAdapter.setOnDropListener { from, to ->
            if (from is NoteDomain && to is FolderDomain) {
                viewModel.moveNoteToFolder(from, to)
            }
            if (from is FolderDomain && to is FolderDomain) {
                viewModel.moveFolderToFolder(from, to)
            }
            if (from is NoteDomain && to is NoteDomain) {
                AddGroupDialog { newFolder ->
                    lifecycleScope.launch {
                        newFolder.id = viewModel.addGroupImpl(newFolder)
                        viewModel.moveItemsImpl(listOf(from, to), newFolder)
                    }
                }.show(childFragmentManager, null)
            }
        }

        notesAdapter.setOnFolderClickListener { folder, root ->
            viewModel.goToFolder(folder)
        }

        notesAdapter.setOnItemMoveCallback { model, _ ->
            showFolderPicker(listOf(model))
        }

        notesAdapter.setOnItemDeleteCallback { model, root ->

            val titleId: Int
            val messageId: Int

            if (model is FolderDomain) {
                titleId = R.string.delete_folder
                messageId = R.string.do_you_want_to_delete_folder_and_all_the_notes_in_it
            } else {
                titleId = R.string.delete_note
                messageId = R.string.do_you_want_to_delete_this_note
            }

            MaterialAlertDialogBuilder(requireContext(), R.style.CardNotes_AlertDialog)
                .setTitle(titleId)
                .setMessage(messageId)
                .setPositiveButton(R.string.yes) { _, _ ->
                    viewModel.removeModel(model)
                }
                .setNegativeButton(R.string.no) { _, _ ->

                }
                .show()
        }

        //Init select items string
        selectedItemsString.value = resources.getQuantityString(
            R.plurals.selected_items, 0, 0
        )


        setFragmentResultListener(KEY_CHOSEN_FOLDER) { _, bundle ->
            val groupId = bundle.getInt("groupId")
            viewModel.setCurrentGroup(groupId)
        }
    }

    fun onBackPressed() {
        if (notesAdapter.isSelectionMode)
            endSelection()
        else
            viewModel.goBackFolder()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        isNavigating = false

        postponeEnterTransition()


        bindingHolder = FragmentMainMenuBinding.inflate(
            inflater, container, false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.ui = this

        notesAdapter.lifecycleOwner = viewLifecycleOwner

        //Init options menu
        popupMenu = binding.mainToolbar.menu

        initMenuListeners()

        //Init notes recycler view
        binding.rvNotes.apply {
            notesAdapter.recyclerView = this

            setHasFixedSize(true)

            val sidePadding = resources.getDimension(R.dimen.side_padding).toInt()

            addItemDecoration(
                PaddingDecorator(
                    sidePadding, sidePadding, sidePadding, sidePadding
                )
            )

            /*     ItemTouchHelper(ItemTouchHelperCallback())
                     .attachToRecyclerView(this)*/

            applyLayoutType(notesAdapter.layoutType)

            doOnPreDraw {
                startPostponedEnterTransition()
            }

            savedInstanceState?.let {
                rvNotesLastPosition = it.getInt(KEY_SCROLL_POSITION)
                this.scrollToPosition(rvNotesLastPosition)
            }

            /*if (rvNotesLastPosition > 0) {
                binding.mainMenuHost.transitionToEnd()
            }*/

        }


        //Change select items string when user checks some note
        viewModel.selectedNotesAmount.observe(viewLifecycleOwner, { quantity ->
            if (quantity > 0) {
                selectedItemsString.value = resources
                    .getQuantityString(R.plurals.selected_items, quantity, quantity)
                binding.btnDelete.isEnabled = true
                binding.btnMove.isEnabled = true
            } else {
                selectedItemsString.value = resources
                    .getString(R.string.select_items)
                binding.btnDelete.isEnabled = false
                binding.btnMove.isEnabled = false
            }
        })

        //Attach observer to notes collection
        //And reflect any changes within the adapter
        viewModel.notes.observe(viewLifecycleOwner, { notes ->
            notesAdapter.setNotes(notes)
        })

        viewModel.folders.observe(viewLifecycleOwner, { folders ->
            notesAdapter.setFolders(folders)
        })

        viewModel.currentGroup.observe(viewLifecycleOwner) {
            if (it.isDefaultFolder) {
                binding.mainToolbar.setTitle(R.string.app_name)
                binding.mainToolbar.navigationIcon = null
                binding.mainToolbar.setNavigationOnClickListener {

                }
            } else {
                binding.mainToolbar.title = it.name
                binding.mainToolbar.setNavigationIcon(R.drawable.back)
                binding.mainToolbar.setNavigationOnClickListener {
                    viewModel.goBackFolder()
                }
            }

            updatePopupMenu(it.id)
        }

        binding.btnSelectAll.apply {

            toggledColor = ContextCompat.getColor(
                requireContext(), R.color.colorAccent
            )

            normalColor = ContextCompat.getColor(
                requireContext(), R.color.primaryTextColor
            )

            setOnClickListener {
                notesAdapter.setIsSelectedForAllItems(isChecked)
            }
        }
/*
        binding.txtSearchLayout.setEndIconOnClickListener {
            clearSearch()
        }*/


        //End selection but not remove the selected notes
        binding.btnEndEdit.setOnClickListener {
            if (notesAdapter.isSelectionMode) {
                endSelection()
            }
        }


        //Navigate to NoteDetailsFragment to add the note
        binding.btnAddNote.setOnClickListener {

            hideSearchKeyboard()

            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                duration = resources.getInteger(
                    R.integer.transition_animation
                ).toLong()
            }

            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                duration = resources.getInteger(
                    R.integer.transition_animation
                ).toLong()
            }


            val action = MainMenuFragmentDirections
                .actionMainMenuFragmentToNoteDetailFragment(
                    newGroupId = viewModel.currentGroup.value!!.id
                )

            findNavController().navigate(action)
        }


        binding.btnDelete.setOnClickListener {

            val res = requireContext().resources
            val count = viewModel.selectedItems.size

            val title = res.getString(R.string.delete_notes)
            val message = res.getQuantityString(
                R.plurals.delete_items, count, count
            )

            MaterialAlertDialogBuilder(requireContext(), R.style.CardNotes_AlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(res.getString(R.string.no)) { _, _ -> }
                .setPositiveButton(res.getString(R.string.yes)) { _, _ ->

                    viewModel.removeSelectedNotes()
                    endSelection()
                }
                .show()

        }

        val groupsAdapter = ArrayAdapter<FolderDomain>(
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
            showFolderPicker(viewModel.selectedItems)
        }

        return binding.root
    }

    private fun initMenuListeners() {

        binding.mainToolbar.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.menu_change_layout -> changeLayoutType()
                R.id.menu_new_first -> notesAdapter.sortByDate(true)
                R.id.menu_old_first -> notesAdapter.sortByDate(false)
                R.id.menu_settings -> {

                    exitTransition = MaterialElevationScale(false)
                    reenterTransition = MaterialElevationScale(true)

                    findNavController()
                        .navigate(R.id.action_mainMenuFragment_to_preferencesFragment)
                }
                R.id.menu_search -> {

                    exitTransition = MaterialElevationScale(false)
                    reenterTransition = MaterialElevationScale(true)

                    findNavController().navigate(
                        R.id.action_mainMenuFragment_to_searchNotesFragment
                    )
                }
                deleteFolderId -> {
                    MaterialAlertDialogBuilder(requireContext(), R.style.CardNotes_AlertDialog)
                        .setTitle(R.string.delete_folder)
                        .setMessage(R.string.do_you_want_to_delete_folder_and_all_the_notes_in_it)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            viewModel.removeCurrentGroup()
                        }
                        .setNegativeButton(R.string.no) { _, _ -> }
                        .show()
                }

                renameFolderId -> {

                    val groupBinding = GroupDialogLayoutBinding.inflate(layoutInflater)

                    val dialog =
                        MaterialAlertDialogBuilder(requireContext(), R.style.CardNotes_AlertDialog)
                            .setTitle(R.string.rename_folder)
                            .setView(groupBinding.root)
                            .setPositiveButton(R.string.accept) { _, _ ->
                                lifecycleScope.launch(Dispatchers.Main) {
                                    viewModel.updateCurrentGroup(groupBinding.edGroupName.text.toString())
                                }
                            }
                            .setNegativeButton(R.string.cancel) { _, _ -> }
                            .setOnDismissListener {
                                toggleHideKeyboard(requireContext())
                            }
                            .show()

                    val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

                    groupBinding.edGroupName.requestFocus()
                    groupBinding.edGroupName.append(viewModel.currentGroup.value!!.name)

                    groupBinding.edGroupName.addTextChangedListener {
                        if (it.isNullOrBlank()) {
                            groupBinding.txtGroupNameLayout.error =
                                resources.getString(R.string.folder_name_must_not_be_empty)
                            positiveButton.isEnabled = false
                        } else {
                            lifecycleScope.launch(Dispatchers.Main) {
                                if (viewModel.isGroupNameExists(it.toString())) {
                                    groupBinding.txtGroupNameLayout.error =
                                        resources.getString(R.string.folder_with_the_same_name_already_exists)
                                    positiveButton.isEnabled = false
                                } else {
                                    groupBinding.txtGroupNameLayout.error = null
                                    positiveButton.isEnabled = true
                                }
                            }
                        }
                    }

                    dialog.show()
                }
            }

            true
        }
    }


    /**
     * Init options menu
     */
    /*private fun createPopupMenu(): PopupMenu {

         val popupMenu = MenuInflater.

         popupMenu.inflate(R.menu.main_menu)

         popupMenu.setOnMenuItemClickListener {
             when(it.itemId) {
                 deleteFolderId -> {
                     MaterialAlertDialogBuilder(requireContext(), R.style.CardNotes_AlertDialog)
                         .setTitle(R.string.delete_folder)
                         .setMessage(
                             R.string.do_you_want_to_delete_folder_and_all_the_notes_in_it
                         )
                         .setPositiveButton(R.string.yes) { _, _ ->
                             viewModel.removeCurrentGroup()
                             viewModel.setAllGroups()
                             updatePopupMenu(-1)
                         }
                         .setNegativeButton(R.string.no) { _, _ -> }
                         .show()
                 }
                 R.id.menu_change_layout -> {
                     changeLayoutType()
                 }
                 R.id.menu_new_first -> {
                     notesAdapter.sortByDate(isDescending = true)
                     binding.rvNotes.scrollToPosition(0)
                 }
                 R.id.menu_old_first -> {
                     notesAdapter.sortByDate()
                     binding.rvNotes.scrollToPosition(0)
                 }
                 R.id.menu_settings -> {
                     val action = MainMenuFragmentDirections
                         .actionMainMenuFragmentToPreferencesFragment()

                     exitTransition = MaterialFadeThrough().apply {
                         duration = resources.getInteger(
                             R.integer.transition_animation
                         ).toLong()
                     }

                     findNavController().navigate(action)
                 }
                 renameFolderId -> {

                     val groupBinding = GroupDialogLayoutBinding.inflate(
                         layoutInflater
                     )

                     val dialog =
                         MaterialAlertDialogBuilder(requireContext(), R.style.CardNotes_AlertDialog)
                             .setTitle(R.string.rename_folder)
                             .setView(groupBinding.root)
                             .setPositiveButton(R.string.accept) { _, _ ->
                                 lifecycleScope.launch(Dispatchers.Main) {
                                     viewModel.updateCurrentGroup(groupBinding.edGroupName.text.toString())
                                     groupsPopupWindow.refreshGroup(viewModel.currentGroup.value!!.groupId)
                                 }
                             }
                             .setNegativeButton(R.string.cancel) { _, _ -> }
                             .setOnDismissListener {
                                 toggleHideKeyboard(requireContext())
                             }
                             .create()

                     dialog.window!!.setSoftInputMode(
                         WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                     )

                     dialog.show()

                     val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                     positiveButton.isEnabled = false

                     groupBinding.edGroupName.requestFocus()
                     groupBinding.edGroupName.append(viewModel.currentGroup.value!!.groupName)

                     groupBinding.edGroupName.addTextChangedListener {
                         if (it.isNullOrBlank()) {
                             groupBinding.txtGroupNameLayout.error =
                                 resources.getString(R.string.folder_name_must_not_be_empty)
                             positiveButton.isEnabled = false
                         } else {
                             lifecycleScope.launch(Dispatchers.Main) {
                                 if (viewModel.isGroupNameExists(it.toString())) {
                                     groupBinding.txtGroupNameLayout.error =
                                         resources.getString(R.string.folder_with_the_same_name_already_exists)
                                     positiveButton.isEnabled = false
                                 } else {
                                     groupBinding.txtGroupNameLayout.error = null
                                     positiveButton.isEnabled = true
                                 }
                             }
                         }
                     }
                 }
             }

             false
         }

         return popupMenu
     }*/


    private fun applyLayoutType(layoutType: BaseAdapter.LayoutType) {
        val item = popupMenu.findItem(R.id.menu_change_layout)
        when (layoutType) {
            BaseAdapter.LayoutType.Grid -> {
                notesAdapter.layoutType = BaseAdapter.LayoutType.Grid
                binding.rvNotes.layoutManager = StaggeredGridLayoutManager(
                    2, StaggeredGridLayoutManager.VERTICAL
                )
                binding.rvNotes.adapter = notesAdapter
                // item.setTitle(R.string.list_view)
            }
            BaseAdapter.LayoutType.List -> {
                notesAdapter.layoutType = BaseAdapter.LayoutType.List
                binding.rvNotes.layoutManager = LinearLayoutManager(requireContext())
                binding.rvNotes.adapter = notesAdapter
                //  item.setTitle(R.string.grid_view)
            }
        }
    }

    private fun changeLayoutType() {
        if (notesAdapter.layoutType == BaseAdapter.LayoutType.Grid) {
            applyLayoutType(BaseAdapter.LayoutType.List)
        } else {
            applyLayoutType(BaseAdapter.LayoutType.Grid)
        }
    }


    private fun updatePopupMenu(groupId: Int) {
        if (groupId == -1) {
            popupMenu.removeItem(deleteFolderId)
            popupMenu.removeItem(renameFolderId)
        } else {

            if (popupMenu.findItem(deleteFolderId) == null)
                popupMenu.add(0, deleteFolderId, 0, R.string.delete_folder)

            if (popupMenu.findItem(renameFolderId) == null)
                popupMenu.add(
                    0,
                    renameFolderId,
                    0,
                    resources.getString(R.string.rename_folder)
                )
        }
    }

    /**
     * Show the selection host
     */
    private fun startSelection() {

        if (binding.selectionToolbar.visibility == View.GONE) {


            TransitionManager.beginDelayedTransition(
                binding.mainMenuHost,
                buildSelectionModeTransition()
            )


            binding.selectionToolbar.visibility = View.VISIBLE
            binding.mainToolbar.visibility = View.GONE

            binding.bottomSelectionMenuHost.let {
                val params = it.layoutParams
                params.height = FrameLayout.LayoutParams.WRAP_CONTENT
                it.layoutParams = params
            }

            binding.btnAddNote.visibility = View.GONE

            binding.bottomSelectionMenuHost.measure(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

            binding.rvNotes.setPadding(0, 0, 0, binding.bottomSelectionMenuHost.measuredHeight)
        }
    }

    /**
     * Hide the selection host
     */
    private fun endSelection() {

        notesAdapter.endSelectionMode()
        notesAdapter.setIsSelectedForAllItems(false)
        binding.btnSelectAll.isChecked = false

        TransitionManager.beginDelayedTransition(
            binding.mainMenuHost,
            buildSelectionModeTransition()
        )

        binding.mainToolbar.visibility = View.VISIBLE
        binding.selectionToolbar.visibility = View.GONE

        binding.bottomSelectionMenuHost.let {
            val params = it.layoutParams
            params.height = 0
            it.layoutParams = params
        }

        binding.btnAddNote.visibility = View.VISIBLE

        binding.rvNotes.setPadding(0, 0, 0, 0)
    }

    private fun buildSelectionModeTransition(): TransitionSet {
        return TransitionSet().apply {
            addTransition(MaterialFade().apply {
                addTarget(binding.mainToolbar)
                addTarget(binding.selectionToolbar)
                addTarget(binding.btnAddNote)
            })
            addTransition(ChangeBounds())
        }
    }


    private fun showPopup() {
        /* hideSearchKeyboard()
         showOverlay()
         groupsPopupWindow.showAsDropDown(binding.btnFolder)*/
    }

    private fun hideSearchKeyboard() {
        hideKeyborad(requireContext(), requireView().rootView)
    }


    /**
     * Show close icon instead of search icon
     * in the search text field
     */
    private fun startSearch() {

        isSearching = true

        /*inding.txtSearchLayout.endIconDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_close_black_24)*/
    }

    /**
     * Show search icon instead of close icon
     * in the text field and clear this text field
     */
    private fun clearSearch() {

        /*if (!binding.edSearch.text.isNullOrBlank()) {
            binding.edSearch.setText("")
        }

        isSearching = false

        binding.txtSearchLayout.endIconDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.baseline_search_black_24)

        binding.edSearch.clearFocus()*/
        hideSearchKeyboard()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_LAYOUT_TYPE, notesAdapter.layoutType.name)
    }

    private fun showFolderPicker(movedItems: Collection<BaseDomain>) {

        fun showSnackbar(dest: FolderDomain) {

            val message: String

            if (movedItems.size == 1) {
                message = getString(
                    R.string.item_moved,
                    movedItems.first().name,
                    dest.name
                )
            } else {
                message = ""
            }

            Snackbar.make(binding.mainMenuHost, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) {
                    viewModel.moveItems(movedItems, viewModel.currentGroup.value!!)
                }
                .show()
        }


        val bottomDialog = BottomSheetDialog(requireContext())

        val pickerAdapter = FolderPickerAdapter().apply {
            setOnNewFolderRequestCallback {
                AddGroupDialog {
                    lifecycleScope.launch(Dispatchers.Main) {
                        it.id = viewModel.addGroupImpl(it)
                        viewModel.moveItemsImpl(movedItems, it)
                        bottomDialog.dismiss()
                        showSnackbar(it)
                    }
                }.show(childFragmentManager, null)
            }
            setOnFolderPickedCallback {
                viewModel.moveItems(movedItems, it)
                bottomDialog.dismiss()
                showSnackbar(it)
            }
        }


        viewModel.allFolders.observe(viewLifecycleOwner) {
            pickerAdapter.setItems(it.filter { item ->
                !movedItems.contains(item)
            })
        }


        val rvFolderPicker = (layoutInflater.inflate(
            R.layout.bottom_folder_picker,
            null
        ) as RecyclerView).apply {
            setHasFixedSize(true)
            isNestedScrollingEnabled = true
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = pickerAdapter
            val sidePadding = resources.getDimension(R.dimen.side_padding).toInt()
            addItemDecoration(
                PaddingDecorator(
                    sidePadding, sidePadding, sidePadding, sidePadding
                )
            )
        }

        bottomDialog.apply {
            setContentView(rvFolderPicker)
            show()
        }
    }


    private fun getRvNotesPosition(): Int {

        val manager = binding.rvNotes.layoutManager

        return if (manager is LinearLayoutManager) {
            var position = manager.findFirstVisibleItemPosition()
            if (position == RecyclerView.NO_POSITION) {
                position = 0
            }
            position
        }
        else if(manager is StaggeredGridLayoutManager) {
            val positions = manager.findFirstVisibleItemPositions(null)
            if(positions[0] == RecyclerView.NO_POSITION) {
                positions[0] = 0
            }
            positions[0]
        }
        else {
            throw IllegalStateException()
        }
    }

    override fun onPause() {
        super.onPause()
        rvNotesLastPosition = getRvNotesPosition()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //Remove all cached data and listeners from adapter
        binding.rvNotes.adapter = null
        //Ensure that there is no references to the old view
        bindingHolder = null
    }


}