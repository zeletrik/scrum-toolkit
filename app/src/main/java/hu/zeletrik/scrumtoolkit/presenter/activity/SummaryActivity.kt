package hu.zeletrik.scrumtoolkit.presenter.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import hu.zeletrik.scrumtoolkit.R
import hu.zeletrik.scrumtoolkit.util.Constants.Companion.PREFERENCE_KEY
import hu.zeletrik.scrumtoolkit.util.Constants.Companion.PREF_TIME_RECORD_KEY

class SummaryActivity : BaseActivity() {

    private lateinit var numberOfMembersText: TextView
    private lateinit var totalTimeText: TextView
    private lateinit var averageTimeText: TextView
    private lateinit var newRecordText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        numberOfMembersText = findViewById(R.id.noOfMembers)
        totalTimeText = findViewById(R.id.allTime)
        averageTimeText = findViewById(R.id.average)
        newRecordText = findViewById(R.id.newRecord)

        val sharedPreferences = getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
        val currentRecord = if (sharedPreferences.contains(PREF_TIME_RECORD_KEY))
            sharedPreferences.getInt(PREF_TIME_RECORD_KEY, 100) else 100


        val numberOfMembers = intent.getIntExtra("numberOfMembers", 0)
        val totalTimeMilSec = intent.getIntExtra("totalTime", 0)

        val averageTime = totalTimeMilSec / 1000 / numberOfMembers
        val newRecord = averageTime < currentRecord

        numberOfMembersText.text = numberOfMembers.toString()
        totalTimeText.text = calculateTimeText(totalTimeMilSec)


        if (currentRecord == 100) {
            averageTimeText.text = "$averageTime sec"
        } else {
            averageTimeText.text = "$averageTime sec [Last record: $currentRecord sec]"
        }

        if (newRecord) {
            newRecordText.visibility = View.VISIBLE
            sharedPreferences
                .edit()
                .putInt(PREF_TIME_RECORD_KEY, averageTime)
                .apply()
        } else {
            newRecordText.visibility = View.INVISIBLE

        }

    }

    private fun calculateTimeText(time: Int): String {
        val progress = time / 1000
        val p1 = progress % 60
        val p2 = progress / 60
        val p3 = p2 % 60

        var p3String = p3.toString()
        var p1String = p1.toString()

        if (p3 < 10) {
            p3String = "0$p3"
        }
        if (p1 < 10) {
            p1String = "0$p1"
        }

        return "$p3String:$p1String"
    }
}