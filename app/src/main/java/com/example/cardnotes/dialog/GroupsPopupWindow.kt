package com.example.cardnotes.dialog

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cardnotes.R
import com.example.cardnotes.adapters.GroupsAdapter
import com.example.cardnotes.databinding.GroupDialogLayoutBinding
import com.example.cardnotes.domain.GroupDomain

class GroupsPopupWindow
    (context: Context,
     parent: ViewGroup)
    : PopupWindow(context) {

    private val inflater = LayoutInflater.from(context)
    private val binding: GroupDialogLayoutBinding
    private val groupsAdapter: GroupsAdapter

    //Callbacks
    private var groupChosenCallback:
            ((group: GroupDomain) -> Unit)? = null

    private var newGroupRequestCallback:
            (() -> Unit)? = null

    init {

        binding = GroupDialogLayoutBinding.inflate(
            inflater, parent, false)

        groupsAdapter = GroupsAdapter(context)

        groupsAdapter.setGroupClickedCallback { group, position ->
            if(position == 0) {
                newGroupRequestCallback?.invoke()
            }
            else {
                groupChosenCallback?.invoke(group)
            }
        }

        binding.rvGroup.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = groupsAdapter
        }

        //Elevation property requires
        //21 min skd version
        if(Build.VERSION.SDK_INT >= 21) {
            elevation = 8f
            setBackgroundDrawable(
                context.getDrawable(R.drawable.popup_bg))
        }
        else {
            setBackgroundDrawable(context.resources
                .getDrawable(R.drawable.popup_bg_with_shadow))
        }

        contentView = binding.root
    }

    fun replaceGroups(groups: List<GroupDomain>) {
        groupsAdapter.replaceAll(groups)
    }

    //Setter for callbacks
    fun setGroupChosenCallback(callback: (group: GroupDomain) -> Unit) {
        groupChosenCallback = callback
    }

    fun setNewGroupRequestCallback(callback: () -> Unit) {
        newGroupRequestCallback = callback
    }

}