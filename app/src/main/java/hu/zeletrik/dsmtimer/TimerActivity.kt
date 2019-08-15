package hu.zeletrik.dsmtimer

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.*
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hu.zeletrik.dsmtimer.util.Constants.Companion.PREFERENCE_KEY
import hu.zeletrik.dsmtimer.util.Constants.Companion.PREF_TIME_KEY
import org.apache.commons.lang3.StringUtils
import kotlin.random.Random


class TimerActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var progressInfo: TextView
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var sharedPreferences: SharedPreferences
    private var measureType = MeasureType.FREE_FORM
    private var currentUser: String = StringUtils.EMPTY
    private val overTimeHandler = Handler()
    private val values: MutableList<Pair<String, Long>> = ArrayList()
    private var members: ArrayList<String> = ArrayList()
    private var numberOfAttendees: Int = 100
    private var startTime: Long = 0
    private var overTime = 0L
    private var millisToFinish: Long = 0
    private var time: Int = 60
    private var counter: Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        progressBar = findViewById(R.id.progressBar)
        progressInfo = findViewById(R.id.txtProgress)

        if (intent.hasExtra("members")) {
            measureType = MeasureType.FIXED_LIST
            members = intent.getStringArrayListExtra("members")
            currentUser = intent.getStringExtra("firstMember")

            if (StringUtils.isNotBlank(currentUser)) {
                members.removeAt(members.indexOf(currentUser))
            } else {
                currentUser = randomize()
            }

        }
        if (intent.hasExtra("numberOfAttendees")) {
            measureType = MeasureType.FIXED_NUMBER
            numberOfAttendees = intent.getIntExtra("numberOfAttendees", 100)
        }

        sharedPreferences = getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE)
        time = sharedPreferences.getInt(PREF_TIME_KEY, 60)

        if (StringUtils.isNotBlank(currentUser)) {
            MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_DSMTimer_Dialog)
                .setCancelable(false)
                .setTitle("Let's start with")
                .setMessage(currentUser)
                .setPositiveButton(getString(R.string.start)) { _, _ ->
                    startCountDown(time)
                }.show()
        } else {
            startCountDown(time)
        }

        findViewById<Button>(R.id.nextButton).setOnClickListener { nextMember() }
        findViewById<Button>(R.id.finishButton).setOnClickListener {
            values.add(Pair(currentUser, calculateTime()))
            finishTimer()
        }
    }

    private fun nextMember() {
        if (isNextUserNeeded()) {
            if (members.isNotEmpty()) {
                currentUser = randomize()
            }

            when {
                StringUtils.isNotBlank(currentUser) ->
                    MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_DSMTimer_Dialog)
                        .setCancelable(false)
                        .setTitle("The next one is")
                        .setMessage(currentUser)
                        .setPositiveButton(getString(R.string.start)) { _, _ ->
                            stopTimer()
                            values.add(Pair(currentUser, calculateTime()))
                            startCountDown(time)
                        }.setNeutralButton("Skip") { _, _ ->
                            stopTimer()
                            members.add(currentUser)
                            nextMember()
                        }

                        .show()
                counter < numberOfAttendees -> {
                    stopTimer()
                    values.add(Pair(currentUser, calculateTime()))
                    counter++
                    startCountDown(time)
                }
                else -> finishTimer()
            }
        } else {
            finishTimer()
        }
    }

    private fun stopTimer() {
        countDownTimer.cancel()
        overTimeHandler.removeCallbacks(overTimeCounter)
    }

    private fun isNextUserNeeded(): Boolean {
        return when (measureType) {
            MeasureType.FREE_FORM -> true
            MeasureType.FIXED_LIST -> members.isNotEmpty()
            MeasureType.FIXED_NUMBER -> counter < numberOfAttendees

        }
    }

    private fun calculateTime(): Long {
        return (time * 1000 - millisToFinish + overTime).toLong()
    }

    private fun randomize(): String {
        var name = StringUtils.EMPTY
        if (members.size < 1) {
            finishTimer()
        } else {
            var index = 0
            if (members.size > 1) {
                index = Random.nextInt(0, members.size - 1)
            }

            name = members[index]
            members.removeAt(index)
        }
        return name
    }

    private fun finishTimer() {
        var totalTime = 0L
        stopTimer()
        values.add(Pair(currentUser, calculateTime()))
        values.forEach { pair: Pair<String, Long> -> totalTime += pair.second }
        val i = Intent(this, SummaryActivity::class.java)
        i.putExtra("numberOfMembers", values.size)
        i.putExtra("totalTime", totalTime.toInt())
        startActivity(i)
        finish()
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        overTimeHandler.removeCallbacks(overTimeCounter)
        super.onDestroy()
    }

    private fun startCountDown(time: Int) {
        val duration = time * 1000
        progressBar.max = duration
        progressBar.progress = duration

        progressBar.setProgressDrawableTiled(getDrawable(R.drawable.progress))
        progressInfo.setTextColor(getColor(R.color.colorPrimaryDark))

        countDownTimer = object : CountDownTimer(duration.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                millisToFinish = millisUntilFinished
                progressInfo.text = calculateTimeText(millisUntilFinished)
                progressBar.progress = millisUntilFinished.toInt()

                val secToFinish = millisUntilFinished.toInt() / 1000
                if (secToFinish < 10 && secToFinish % 2 != 0) {
                    vibrate(500)
                }
            }

            override fun onFinish() {
                vibrate(1500)
                progressBar.progress = duration
                startOvertime()
            }
        }.start()
    }

    private fun startOvertime() {
        progressBar.setProgressDrawableTiled(getDrawable(R.drawable.progress_red))
        progressBar.max = 100
        progressBar.progress = 100
        progressInfo.setTextColor(Color.RED)
        startTime = SystemClock.uptimeMillis()
        overTimeHandler.postDelayed(overTimeCounter, 0)
    }

    private fun calculateTimeText(time: Long): String {
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

    private val overTimeCounter: Runnable = object : Runnable {
        var millisecondTime: Long = 0
        var timeBuff: Long = 0

        override fun run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime
            overTime = timeBuff + millisecondTime
            vibrate(250)
            progressInfo.text = "+ ${calculateTimeText(overTime)}"
            overTimeHandler.postDelayed(this, 1000)
        }

    }

    private fun vibrate(duration: Long) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(duration)
            }
        }
    }
}