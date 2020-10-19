package com.example.cardnotes.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.cardnotes.R
import com.example.cardnotes.databinding.GroupAddDialogLayoutBinding
import com.example.cardnotes.domain.GroupDomain
import com.example.cardnotes.repos.GroupsRepo
import com.example.cardnotes.utils.styleDialogButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddGroupDialog
    (private val onCreateGroupCallback: (group: GroupDomain) -> Unit)
    : DialogFragment() {


    private lateinit var positiveButton: Button
    private lateinit var negativeButton: Button

    private lateinit var binding: GroupAddDialogLayoutBinding
    private val groupsRepo = GroupsRepo()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = GroupAddDialogLayoutBinding
            .inflate(layoutInflater)

        val groupDialog = AlertDialog
            .Builder(requireContext(), R.style.CardNotes_AlertDialog)
            .setTitle(requireContext().getString(R.string.create_folder))
            .setView(binding.root)
            .setPositiveButton(R.string.create_folder) { _, _ ->

                lifecycleScope.launch(Dispatchers.Main) {
                    onCreateGroupCallback(
                        GroupDomain(
                            groupName = binding.edGroupName.text.toString())
                    )
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .show()

        groupDialog.window!!.setBackgroundDrawableResource(R.drawable.dialog_bg)

        positiveButton = groupDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        disableButton(positiveButton)

        negativeButton = groupDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

        styleDialogButton(negativeButton)

        binding.edGroupName.addTextChangedListener {

            lifecycleScope.launch(Dispatchers.Main) {

                if (it.isNullOrBlank()) {
                    setError(R.string.folder_name_must_not_be_empty)
                }
                else if(groupsRepo.isExist(it.toString())) {
                    setError(R.string
                        .folder_with_the_same_name_already_exists)
                }
                else {
                    removeError()
                }
            }

        }

        return groupDialog
    }




    private fun setError(resId: Int) {
        binding.txtGroupNameLayout.error =
            requireContext().getString(resId)
        disableButton(positiveButton)
    }

    private fun removeError() {
        binding.txtGroupNameLayout.error = null
        enableButton(positiveButton)
    }

    private fun enableButton(btn: Button) {
        btn.isEnabled = true
        btn.setTextColor(ContextCompat.getColor(
            requireContext(), R.color.colorPrimary))
    }

    private fun disableButton(btn: Button) {
        btn.isEnabled = false
        btn.setTextColor(ContextCompat.getColor(
            requireContext(), android.R.color.darker_gray
        ))
    }



}