package cf.feuerkrieg.cardnotes.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


fun hideKeyborad(context: Context, view: View) {
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun showKeyboard(context: Context, view: View) {
    val imm = context.getSystemService(
        Activity.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun toggleHideKeyboard(context: Context) {

    val imm = context
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
}