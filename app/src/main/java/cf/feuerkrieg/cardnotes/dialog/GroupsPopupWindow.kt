package cf.feuerkrieg.cardnotes.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.adapters.GroupsAdapter
import cf.feuerkrieg.cardnotes.databinding.GroupPopupLayoutBinding
import cf.feuerkrieg.cardnotes.domain.GroupDomain

class GroupsPopupWindow
    (context: Context,
     parent: ViewGroup,
     defaultGroupStringResId: Int)
    : PopupWindow(context) {

    private val inflater = LayoutInflater.from(context)
    private val binding: GroupPopupLayoutBinding
    private val groupsAdapter: GroupsAdapter

    //Callbacks
    private var groupChosenCallback:
            ((group: GroupDomain) -> Unit)? = null

    private var newGroupRequestCallback:
            (() -> Unit)? = null

    init {


        binding = GroupPopupLayoutBinding.inflate(
            inflater, parent, false)

        groupsAdapter = GroupsAdapter(context, defaultGroupStringResId)

        groupsAdapter.setGroupClickedCallback { group, position ->
            //If new group clicked
            if(position == 0) {
                newGroupRequestCallback?.invoke()
            }
            //If All groups clicked
            else if (position == 1) {
                groupChosenCallback?.invoke(
                    GroupDomain(
                        groupId = -1,
                        groupName = ""))
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

        val background = ContextCompat.getDrawable(context,
            R.drawable.popup_bg_with_shadow)

        isOutsideTouchable = true

        setBackgroundDrawable(background)

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

    fun refreshGroup(groupId: Int) {
        groupsAdapter.refreshGroup(groupId)
    }
}