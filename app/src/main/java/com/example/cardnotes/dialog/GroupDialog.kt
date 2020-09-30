package com.example.cardnotes.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cardnotes.R
import com.example.cardnotes.databinding.GroupDialogLayoutBinding
import com.example.cardnotes.domain.GroupDomain
import com.example.cardnotes.viewmodels.GroupDialogViewModel

class GroupDialog: DialogFragment() {


    private lateinit var viewModel: GroupDialogViewModel
    private lateinit var binding: GroupDialogLayoutBinding


    private val groupListListener = object : DialogInterface.OnClickListener {
        override fun onClick(dialog: DialogInterface?, which: Int) {

        }

    }

    override fun onAttach(context: Context) {

        super.onAttach(context)

        viewModel = ViewModelProvider(this)
            .get(GroupDialogViewModel::class.java)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = GroupDialogLayoutBinding.inflate(
            layoutInflater, null, false)


        val groupsAdapter = ArrayAdapter<GroupDomain>(
            requireContext(), R.layout.group_item)

        viewModel.groups.observe(this,
            Observer {
                val groups = mutableListOf<GroupDomain>(
                GroupDomain(groupName = "New group"))
                groups.addAll(it)
                groupsAdapter.clear()
                groupsAdapter.addAll(groups) })

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setAdapter(groupsAdapter, groupListListener)
            .create()
    }




}