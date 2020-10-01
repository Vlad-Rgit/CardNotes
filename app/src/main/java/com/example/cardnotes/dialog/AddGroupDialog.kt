package com.example.cardnotes.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.cardnotes.R
import com.example.cardnotes.databinding.GroupAddDialogLayoutBinding
import com.example.cardnotes.domain.GroupDomain

class AddGroupDialog
    (private val onCreateGroupCallback: (group: GroupDomain) -> Unit)
    : DialogFragment() {


    private lateinit var binding: GroupAddDialogLayoutBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = GroupAddDialogLayoutBinding
            .inflate(layoutInflater)

        return AlertDialog
            .Builder(requireContext(), R.style.CardNotes_AlertDialog)
            .setView(binding.root)
            .setPositiveButton(R.string.create_folder) { _, _ ->
                val group = GroupDomain(
                    groupName = binding.edGroupName.text.toString())

                onCreateGroupCallback(group)
            }
            .setNeutralButton(R.string.cancel) { _, _ -> }
            .create()
    }

}