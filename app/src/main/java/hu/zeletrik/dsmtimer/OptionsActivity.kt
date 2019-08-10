package hu.zeletrik.dsmtimer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.commons.lang3.StringUtils

class OptionsActivity : AppCompatActivity() {

    private lateinit var chipGroup: ChipGroup
    private lateinit var startTimerFab: FloatingActionButton
    private lateinit var sharedPreferences: SharedPreferences
    private var firstMember: String = StringUtils.EMPTY
    private val members = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        chipGroup = findViewById(R.id.chipGroup)
        startTimerFab = findViewById(R.id.startTimerFab)

        sharedPreferences = getSharedPreferences(Constants.PREFERENCE_KEY, Context.MODE_PRIVATE)

        if (sharedPreferences.contains(Constants.PREF_ATTENDEE_LIST_KEY)) {
            val json = sharedPreferences.getString(Constants.PREF_ATTENDEE_LIST_KEY, StringUtils.EMPTY)
            val saved = getList(json!!)
            saved.forEach { member ->  addChipToGroup(member) }
        }
        chipGroup.isSingleSelection = true

        chipGroup.setOnCheckedChangeListener { _, i ->
            firstMember = getSelectedText(chipGroup, i)
        }

        startTimerFab.setOnClickListener {
            chipGroup.forEach { view ->
                members.add((view as Chip).text.toString()) }

            val i = Intent(this, TimerActivity::class.java)
            i.putExtra("members", members)
            i.putExtra("firstMember", firstMember)
            startActivity(i)
        }
    }

    private fun addChipToGroup(person: String) {
        val chip = Chip(this)
        chip.text = person
        chip.isCloseIconEnabled = true
        chip.setChipIconTintResource(R.color.colorPrimaryDark)
        chip.isClickable = true
        chip.isCheckable = true
        chip.height = 12
        chip.textSize = 24F
        chipGroup.addView(chip as View)
        chip.setOnCloseIconClickListener { chipGroup.removeView(chip as View) }
    }

    private fun getSelectedText(chipGroup: ChipGroup, id: Int): String {
        val mySelection = chipGroup.findViewById<Chip>(id)
        return mySelection?.text?.toString() ?: StringUtils.EMPTY
    }

        private fun getList(jsonArray: String): ArrayList<String> {
        val typeOfT = TypeToken.getParameterized(ArrayList::class.java, String::class.java).type
        return Gson().fromJson(jsonArray, typeOfT)
    }
}