package hu.zeletrik.scrumtoolkit.presenter.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.zeletrik.scrumtoolkit.R
import hu.zeletrik.scrumtoolkit.domain.ThemeMode
import hu.zeletrik.scrumtoolkit.util.Constants
import hu.zeletrik.scrumtoolkit.util.Constants.Companion.PREF_CURRENT_THEME_KEY
import org.apache.commons.lang3.StringUtils

open class BaseActivity : AppCompatActivity() {

    private var currentTheme: String = StringUtils.EMPTY
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)

        currentTheme = sharedPref.getString(PREF_CURRENT_THEME_KEY, StringUtils.EMPTY)!!

        setAppTheme(currentTheme)
    }

    override fun onResume() {
        super.onResume()
        val theme = sharedPref.getString(PREF_CURRENT_THEME_KEY, StringUtils.EMPTY)
        if (currentTheme != theme)
            recreate()
    }

    private fun setAppTheme(currentTheme: String) {
        when (currentTheme) {
            ThemeMode.OLED.name -> setTheme(R.style.Theme_App_Oled)
            ThemeMode.DARK.name -> setTheme(R.style.Theme_App_Dark)
            else -> setTheme(R.style.Theme_App_Light)
        }
    }
}