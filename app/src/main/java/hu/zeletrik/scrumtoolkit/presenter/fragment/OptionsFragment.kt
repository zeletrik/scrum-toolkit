package hu.zeletrik.scrumtoolkit.presenter.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import hu.zeletrik.scrumtoolkit.R
import hu.zeletrik.scrumtoolkit.domain.MeasureType
import hu.zeletrik.scrumtoolkit.domain.OrientationMode
import hu.zeletrik.scrumtoolkit.domain.ThemeMode
import hu.zeletrik.scrumtoolkit.presenter.activity.AttendeeSettingsActivity
import hu.zeletrik.scrumtoolkit.util.Constants
import hu.zeletrik.scrumtoolkit.util.Constants.Companion.PREF_CURRENT_ORIENTATION_KEY
import hu.zeletrik.scrumtoolkit.util.Constants.Companion.PREF_CURRENT_THEME_KEY
import org.apache.commons.lang3.StringUtils



class OptionsFragment : Fragment() {

    private lateinit var rootView: View


    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var currentOrientation: TextView
    private lateinit var currentTheme: TextView
    private lateinit var currentAttendeesSetting: TextView
    private var useList: Boolean = false
    private var useFixNumber: Boolean = false

    private var time: Int = 60

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.options_fragment, container, false)
        val simpleSeekBar = rootView.findViewById<SeekBar>(R.id.baseTimeSeekBar)
        val baseTimeText = rootView.findViewById<TextView>(R.id.baseTimeValue)
        val modeButton = rootView.findViewById<Button>(R.id.setModeButton)
        currentOrientation = rootView.findViewById(R.id.currentOrientation)
        currentTheme = rootView.findViewById(R.id.currentTheme)
        currentAttendeesSetting = rootView.findViewById(R.id.currentAttendeesSetting)

        simpleSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progressChangedValue = 0

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                progressChangedValue = progress
                baseTimeText.text = String.format(getString(R.string.base_time_value), progressChangedValue)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                time = progressChangedValue
                saveValues()
            }
        })


        //region Orientation card view
        val orientationExpanded = rootView.findViewById<ConstraintLayout>(R.id.orientationExpandableView)
        val orientationArrow = rootView.findViewById<Button>(R.id.orientationArrowBtn)
        val orientationSettingsView = rootView.findViewById<CardView>(R.id.orientationCardView)

        orientationArrow.setOnClickListener {
            if (orientationExpanded.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(orientationSettingsView, AutoTransition())
                orientationExpanded.visibility = View.VISIBLE
                orientationArrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_up)
            } else {
                TransitionManager.beginDelayedTransition(orientationSettingsView, AutoTransition())
                orientationExpanded.visibility = View.GONE
                orientationArrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_down)
            }
        }
        //endregion

        //region Theme card view
        val themeExpanded = rootView.findViewById<ConstraintLayout>(R.id.themeExpandableView)
        val themeArrow = rootView.findViewById<Button>(R.id.themeArrowBtn)
        val themeSettingsView = rootView.findViewById<CardView>(R.id.themeCardView)

        themeArrow.setOnClickListener {
            if (themeExpanded.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(themeSettingsView, AutoTransition())
                themeExpanded.visibility = View.VISIBLE
                themeArrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_up)
            } else {
                TransitionManager.beginDelayedTransition(themeSettingsView, AutoTransition())
                themeExpanded.visibility = View.GONE
                themeArrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_down)
            }
        }
        //endregion

        //region Dsm timer card view
        val dsmTimerExpanded = rootView.findViewById<ConstraintLayout>(R.id.dsmTimerExpandableView)
        val dsmTimerArrow = rootView.findViewById<Button>(R.id.dsmTimerArrowBtn)
        val dsmTimerSettingsView = rootView.findViewById<CardView>(R.id.dsmTimerCardView)

        dsmTimerArrow.setOnClickListener {
            if (dsmTimerExpanded.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(dsmTimerSettingsView, AutoTransition())
                dsmTimerExpanded.visibility = View.VISIBLE
                dsmTimerArrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_up)
            } else {
                TransitionManager.beginDelayedTransition(dsmTimerSettingsView, AutoTransition())
                dsmTimerExpanded.visibility = View.GONE
                dsmTimerArrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_down)
            }
        }
        //endregion

        //region Attendees card view
        val attendeesExpanded = rootView.findViewById<ConstraintLayout>(R.id.attendeesExpandableView)
        val attendeesArrow = rootView.findViewById<Button>(R.id.attendeesArrowBtn)
        val attendeesSettingsView = rootView.findViewById<CardView>(R.id.attendeesCardView)

        attendeesArrow.setOnClickListener {
            if (attendeesExpanded.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(attendeesSettingsView, AutoTransition())
                attendeesExpanded.visibility = View.VISIBLE
                attendeesArrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_up)
            } else {
                TransitionManager.beginDelayedTransition(attendeesSettingsView, AutoTransition())
                attendeesExpanded.visibility = View.GONE
                attendeesArrow.setBackgroundResource(R.drawable.ic_keyboard_arrow_down)
            }
        }
        //endregion


        //region SharedPrefs
        sharedPreferences = activity!!.getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)
        getValues()
        simpleSeekBar.progress = time
        //endregion

        val themeRadioGroup = rootView.findViewById<RadioGroup>(R.id.themeRadio)
        themeRadioGroup.clearCheck()
        themeRadioGroup
            .setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radioLightTheme -> {
                        val current = sharedPreferences.getString(PREF_CURRENT_THEME_KEY, StringUtils.EMPTY)
                        if (current != ThemeMode.LIGHT.name) {
                            sharedPreferences.edit().putString(PREF_CURRENT_THEME_KEY, ThemeMode.LIGHT.name).apply()
                            activity!!.recreate()
                        }
                    }
                    R.id.radioDarkTheme -> {
                        val current = sharedPreferences.getString(PREF_CURRENT_THEME_KEY, StringUtils.EMPTY)
                        if (current != ThemeMode.DARK.name) {
                            sharedPreferences.edit().putString(PREF_CURRENT_THEME_KEY, ThemeMode.DARK.name).apply()
                            activity!!.recreate()
                        }
                    }
                    R.id.radioOledTheme -> {
                        val current = sharedPreferences.getString(PREF_CURRENT_THEME_KEY, StringUtils.EMPTY)
                        if (current != ThemeMode.OLED.name) {
                            sharedPreferences.edit().putString(PREF_CURRENT_THEME_KEY, ThemeMode.OLED.name).apply()
                            activity!!.recreate()
                        }
                    }
                }
            }


        val rotateRadioGroup = rootView.findViewById<RadioGroup>(R.id.rotateRadio)
        rotateRadioGroup.clearCheck()
        rotateRadioGroup
            .setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radioPortrait -> {
                        val current = sharedPreferences.getString(PREF_CURRENT_ORIENTATION_KEY, StringUtils.EMPTY)
                        if (current != OrientationMode.PORTRAIT.name) {
                            sharedPreferences.edit().putString(PREF_CURRENT_ORIENTATION_KEY, OrientationMode.PORTRAIT.name).apply()
                            activity!!.recreate()
                        }
                    }
                    R.id.radioLandscape -> {
                        val current = sharedPreferences.getString(PREF_CURRENT_ORIENTATION_KEY, StringUtils.EMPTY)
                        if (current != OrientationMode.LANDSCAPE.name) {
                            sharedPreferences.edit().putString(PREF_CURRENT_ORIENTATION_KEY, OrientationMode.LANDSCAPE.name).apply()
                            activity!!.recreate()
                        }
                    }
                    R.id.radioFixAuto -> {
                        val current = sharedPreferences.getString(PREF_CURRENT_ORIENTATION_KEY, StringUtils.EMPTY)
                        if (current != OrientationMode.DEVICE_DEFAULT.name) {
                            sharedPreferences.edit().putString(PREF_CURRENT_ORIENTATION_KEY, OrientationMode.DEVICE_DEFAULT.name).apply()
                            activity!!.recreate()
                        }
                    }
                }
            }


        val modeRadioGroup = rootView.findViewById<RadioGroup>(R.id.attendeeSettingsRadio)
        modeRadioGroup.clearCheck()
        modeRadioGroup
            .setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.radioFixNumAttendee -> {
                        modeButton.visibility = View.VISIBLE
                        modeButton.text = "Set number"
                        useFixNumber = true
                        useList = false
                        currentAttendeesSetting.text= MeasureType.FIXED_NUMBER.text
                        saveValues()

                    }
                    R.id.radioFreeAttendeeList -> {
                        modeButton.visibility = View.GONE
                        useFixNumber = false
                        useList = false
                        currentAttendeesSetting.text = MeasureType.FREE_FORM.text
                        saveValues()
                    }
                    R.id.radioAttendeeList -> {
                        modeButton.visibility = View.VISIBLE
                        modeButton.text = "Configure list"
                        useFixNumber = false
                        useList = true
                        currentAttendeesSetting.text = MeasureType.FIXED_LIST.text
                        saveValues()
                    }
                }
            }

        rootView.findViewById<RadioButton>(R.id.radioFixNumAttendee).isChecked = useFixNumber
        rootView.findViewById<RadioButton>(R.id.radioAttendeeList).isChecked = useList
        rootView.findViewById<RadioButton>(R.id.radioFreeAttendeeList).isChecked = !useFixNumber && !useList


        when (sharedPreferences.getString(PREF_CURRENT_THEME_KEY, StringUtils.EMPTY)) {
            ThemeMode.DARK.name -> {
                rootView.findViewById<RadioButton>(R.id.radioDarkTheme).isChecked = true
                currentTheme.text = ThemeMode.DARK.text
            }
            ThemeMode.OLED.name -> {
                rootView.findViewById<RadioButton>(R.id.radioOledTheme).isChecked = true
                currentTheme.text = ThemeMode.OLED.text
            }
            else -> {
                rootView.findViewById<RadioButton>(R.id.radioLightTheme).isChecked = true
                currentTheme.text = ThemeMode.LIGHT.text
            }
        }

        when (sharedPreferences.getString(PREF_CURRENT_ORIENTATION_KEY, StringUtils.EMPTY)) {
            OrientationMode.PORTRAIT.name -> {
                rootView.findViewById<RadioButton>(R.id.radioPortrait).isChecked = true
                currentOrientation.text = OrientationMode.PORTRAIT.text

            }
            OrientationMode.LANDSCAPE.name -> {
                rootView.findViewById<RadioButton>(R.id.radioLandscape).isChecked = true
                currentOrientation.text = OrientationMode.LANDSCAPE.text
            }
            else -> {
                rootView.findViewById<RadioButton>(R.id.radioFixAuto).isChecked = true
                currentOrientation.text = OrientationMode.DEVICE_DEFAULT.text
            }
        }


        modeButton.setOnClickListener {
            val i = Intent(activity, AttendeeSettingsActivity::class.java)

            val mode = when {
                useList -> MeasureType.FIXED_LIST
                useFixNumber -> MeasureType.FIXED_NUMBER
                else -> MeasureType.FREE_FORM
            }

            i.putExtra("mode", mode.name)
            startActivity(i)
        }

        return rootView
    }


    fun saveValues() {
        val editor = sharedPreferences.edit()
        editor.putBoolean(Constants.PREF_USE_LIST_KEY, useList)
        editor.putBoolean(Constants.PREF_USE_FIX_NUM_KEY, useFixNumber)
        editor.putInt(Constants.PREF_TIME_KEY, time)
        editor.apply()
    }

    private fun getValues() {
        if (sharedPreferences.contains(Constants.PREF_USE_LIST_KEY)) {
            useList = sharedPreferences.getBoolean(Constants.PREF_USE_LIST_KEY, false)
        }
        if (sharedPreferences.contains(Constants.PREF_USE_FIX_NUM_KEY)) {
            useFixNumber = sharedPreferences.getBoolean(Constants.PREF_USE_FIX_NUM_KEY, false)
        }
        if (sharedPreferences.contains(Constants.PREF_TIME_KEY)) {
            time = sharedPreferences.getInt(Constants.PREF_TIME_KEY, 60)
        }
    }

}