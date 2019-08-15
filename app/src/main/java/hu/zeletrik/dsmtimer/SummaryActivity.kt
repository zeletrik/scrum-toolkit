package hu.zeletrik.dsmtimer

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import hu.zeletrik.dsmtimer.util.Constants.Companion.PREFERENCE_KEY
import hu.zeletrik.dsmtimer.util.Constants.Companion.PREF_TIME_RECORD_KEY

class SummaryActivity : AppCompatActivity() {

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
        val totalTimeSec = intent.getIntExtra("totalTime", 0) / 1000

        val averageTime = totalTimeSec / numberOfMembers
        val newRecord = averageTime < currentRecord

        numberOfMembersText.text = numberOfMembers.toString()
        totalTimeText.text = "$totalTimeSec sec"


        if (currentRecord == 100) {
            averageTimeText.text = "$averageTime sec"
        } else {
            averageTimeText.text = "$averageTime sec [$currentRecord sec]"
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
}