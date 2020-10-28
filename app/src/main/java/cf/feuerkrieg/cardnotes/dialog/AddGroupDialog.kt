package cf.feuerkrieg.cardnotes.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.databinding.GroupDialogLayoutBinding
import cf.feuerkrieg.cardnotes.domain.FolderDomain
import cf.feuerkrieg.cardnotes.repos.FolderRepo
import cf.feuerkrieg.cardnotes.utils.toggleHideKeyboard
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddGroupDialog
    (private val onCreateGroupCallback: (folder: FolderDomain) -> Unit)
    : DialogFragment() {


    private lateinit var positiveButton: Button
    private lateinit var negativeButton: Button

    private lateinit var binding: GroupDialogLayoutBinding
    private val groupsRepo = FolderRepo()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = GroupDialogLayoutBinding
            .inflate(layoutInflater)

        val groupDialog =
            MaterialAlertDialogBuilder(requireContext(), R.style.CardNotes_AlertDialog)
                .setTitle(requireContext().getString(R.string.create_folder))
                .setView(binding.root)
                .setPositiveButton(R.string.accept) { _, _ ->

                    lifecycleScope.launch(Dispatchers.Main) {
                        onCreateGroupCallback(
                            FolderDomain(
                                name = binding.edGroupName.text.toString()
                            )
                        )
                    }
                }
                .setNegativeButton(R.string.cancel) { _, _ -> }
                .create()

        groupDialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        groupDialog.show()

        positiveButton = groupDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        disableButton(positiveButton)

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

    override fun onResume() {
        super.onResume()
        binding.edGroupName.requestFocus()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        toggleHideKeyboard(requireContext())
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