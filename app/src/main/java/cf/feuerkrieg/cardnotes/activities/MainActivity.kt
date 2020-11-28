package cf.feuerkrieg.cardnotes.activities

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import cf.feuerkrieg.cardnotes.R
import cf.feuerkrieg.cardnotes.utils.LocaleHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }



    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        LocaleHelper.overrideLocaleFromAppContext(this)
    }
}