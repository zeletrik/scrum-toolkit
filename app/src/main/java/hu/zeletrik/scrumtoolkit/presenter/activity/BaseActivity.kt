package hu.zeletrik.scrumtoolkit.presenter.activity

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.zeletrik.scrumtoolkit.R
import hu.zeletrik.scrumtoolkit.domain.OrientationMode
import hu.zeletrik.scrumtoolkit.domain.ThemeMode
import hu.zeletrik.scrumtoolkit.util.Constants
import hu.zeletrik.scrumtoolkit.util.Constants.Companion.PREF_CURRENT_ORIENTATION_KEY
import hu.zeletrik.scrumtoolkit.util.Constants.Companion.PREF_CURRENT_THEME_KEY
import org.apache.commons.lang3.StringUtils


open class BaseActivity : AppCompatActivity() {

    private var currentTheme: String = StringUtils.EMPTY
    private var rotationSettings: String = StringUtils.EMPTY
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPref = getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)

        currentTheme = sharedPref.getString(PREF_CURRENT_THEME_KEY, StringUtils.EMPTY)!!

        setAppTheme(currentTheme)

        rotationSettings = sharedPref.getString(PREF_CURRENT_ORIENTATION_KEY, StringUtils.EMPTY)!!

        setOrientation(rotationSettings)

    }

    override fun onResume() {
        super.onResume()
        val theme = sharedPref.getString(PREF_CURRENT_THEME_KEY, StringUtils.EMPTY)
        val rotation = sharedPref.getString(PREF_CURRENT_ORIENTATION_KEY, StringUtils.EMPTY)
        if ((currentTheme != theme) || (rotation != rotationSettings))
            recreate()
    }

    private fun setAppTheme(currentTheme: String) {
        when (currentTheme) {
            ThemeMode.OLED.name -> setTheme(R.style.Theme_App_Oled)
            ThemeMode.DARK.name -> setTheme(R.style.Theme_App_Dark)
            else -> setTheme(R.style.Theme_App_Light)
        }
    }


    private fun setOrientation(current: String) {
        when (current) {
            OrientationMode.PORTRAIT.name
            -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            OrientationMode.LANDSCAPE.name
            -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            else -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER
        }
    }
}
