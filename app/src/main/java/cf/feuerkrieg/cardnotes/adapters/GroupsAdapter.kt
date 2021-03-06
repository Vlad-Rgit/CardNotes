package cf.feuerkrieg.cardnotes.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.databinding.GroupItemBinding
import cf.feuerkrieg.cardnotes.domain.GroupDomain
import cf.feuerkrieg.cardnotes.utils.replaceAll

class GroupsAdapter(
    private val context: Context,
    private val defaulGroupStringResId: Int)
    : RecyclerView.Adapter<GroupsAdapter.ViewHolder>() {


    private val inflater = LayoutInflater.from(context)
    private val groups = mutableListOf<GroupDomain>()

    private var groupClickedCallback:
            ((group: GroupDomain, position: Int) -> Unit)? = null

    fun replaceAll(items: List<GroupDomain>) {

        groups.replaceAll(items, this)

        groups.add(0, GroupDomain(
            groupName = context.getString(R.string.new_folder)))

        notifyItemInserted(0)

        groups.add(
            1, GroupDomain(
                groupName = context.getString(defaulGroupStringResId)
            )
        )

        notifyItemInserted(1)
    }

    fun setGroupClickedCallback(
        callback:
            (group: GroupDomain, position: Int) -> Unit
    ) {
        groupClickedCallback = callback
    }


    fun refreshGroup(groupId: Int) {
        for (i in groups.indices) {
            if (groups[i].groupId == groupId) {
                notifyItemChanged(i)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GroupItemBinding.inflate(
            inflater, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = groups[position]
        holder.performBind(group, position)
    }

    override fun getItemCount(): Int {
        return groups.size
    }


    inner class ViewHolder(
        private val binding: GroupItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                groupClickedCallback?.invoke(binding.group!!, layoutPosition)
            }
        }

        fun performBind(group: GroupDomain, position: Int) {

            if(position == 0 || position == 1) {
                binding.tvGroupName.setTypeface(
                    null, Typeface.BOLD)
            }
            else {
                binding.tvGroupName.setTypeface(
                    null, Typeface.NORMAL)
            }

            binding.group = group
        }

    }

}