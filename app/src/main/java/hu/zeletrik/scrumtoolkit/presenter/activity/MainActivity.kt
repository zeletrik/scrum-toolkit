package hu.zeletrik.scrumtoolkit.presenter.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import hu.zeletrik.scrumtoolkit.R
import hu.zeletrik.scrumtoolkit.presenter.fragment.EstimateFragment
import hu.zeletrik.scrumtoolkit.presenter.fragment.OptionsFragment
import hu.zeletrik.scrumtoolkit.presenter.fragment.RetroFragment
import hu.zeletrik.scrumtoolkit.presenter.fragment.StandUpFragment
import java.util.*


class MainActivity : BaseActivity() {

    internal var prevMenuItem: MenuItem? = null
    private lateinit var viewPager: ViewPager
    private lateinit var bottomNavigationView: BottomNavigationView
    private val standUpFragment = StandUpFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        viewPager = findViewById(R.id.container)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_estimate -> viewPager.currentItem = 0
                R.id.action_timer -> viewPager.currentItem = 1
                R.id.action_retro -> viewPager.currentItem = 2
                R.id.action_settings -> viewPager.currentItem = 3
            }
            false
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (prevMenuItem != null) {
                    prevMenuItem!!.isChecked = false
                } else {
                    bottomNavigationView.menu.getItem(0).isChecked = false
                }
                bottomNavigationView.menu.getItem(position).isChecked = true
                prevMenuItem = bottomNavigationView.menu.getItem(position)

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        setupViewPager(viewPager)
    }


    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(EstimateFragment(), "Estimation")
        adapter.addFragment(standUpFragment, "Stand up")
        adapter.addFragment(RetroFragment(), "Retrospective")
        adapter.addFragment(OptionsFragment(), "Settings")
        viewPager.adapter = adapter
    }

    @Override
    override fun onResume() {
        standUpFragment.isUsersRefreshed = false
        super.onResume()
    }


    internal inner class ViewPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }
}
