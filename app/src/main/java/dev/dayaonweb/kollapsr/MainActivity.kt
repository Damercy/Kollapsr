package dev.dayaonweb.kollapsr

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private var sumPositionWithOffset = INITIAL_SUM_PAGE_OFFSET
    private lateinit var pager: ViewPager2
    private var pagerNonFullScreenHeight: Int = -1
    private var pagerFullScreenHeight: Int = -1
    var onTransitionCompleteListener: BottomNavListener? = null
    private lateinit var bottomNav: BottomNavigationView


    private val pagerCb = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            Log.d(
                TAG,
                "initViews: padnav=${bottomNav.paddingBottom} padpage=${pager.paddingBottom} navheight=${bottomNav.height} pageHeight=${pager.height} initheight=$pagerNonFullScreenHeight"
            )
            bottomNav.setPadding(0, 0, 0, 0)
            val translatedValue = (1.0f - positionOffset) * BOTTOM_NAV_BAR_HEIGHT
            if (isSwipingToLeft(position, positionOffset) && position == 0) {
                // Going from home fragment to camera fragment
                bottomNav.animate()
                    .translationY(translatedValue + FLING_AMOUNT)
                    .start()
                onTransitionCompleteListener?.onTransitionComplete(translatedValue == BOTTOM_NAV_BAR_HEIGHT * 1.0f)
                if (translatedValue == BOTTOM_NAV_BAR_HEIGHT * 1.0f)
                    bottomNav.isVisible = false

            } else if (!isSwipingToLeft(position, positionOffset) && position == 0) {
                // Going from camera fragment to home fragment
                bottomNav.isVisible = true
                bottomNav.animate()
                    .translationY(translatedValue)
                    .start()
                onTransitionCompleteListener?.onTransitionComplete(false)
            }
            sumPositionWithOffset = position + positionOffset
        }

        override fun onPageSelected(position: Int) {
            when (position) {
                1 -> checkNavItem(R.id.pay)
                2 -> checkNavItem(R.id.search)
                3 -> checkNavItem(R.id.history)
                4 -> checkNavItem(R.id.profile)
            }
        }

        override fun onPageScrollStateChanged(state: Int) = Unit
    }

    private fun isSwipingToLeft(position: Int, positionOffset: Float) =
        position + positionOffset < sumPositionWithOffset


    fun checkNavItem(menuId: Int) {
        bottomNav.menu.findItem(menuId).isChecked = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initPager(savedInstanceState)
    }


    private fun initPager(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            pager.apply {
                adapter = PagerAdapter(this@MainActivity)
                currentItem = 1
                registerOnPageChangeCallback(pagerCb)
            }
        }
    }


    private fun initViews() {
        pager = findViewById(R.id.pager)
        bottomNav = findViewById(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.pay -> pager.currentItem = 1
            R.id.search -> pager.currentItem = 2
            R.id.history -> pager.currentItem = 3
            R.id.profile -> pager.currentItem = 4
        }
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        pager.unregisterOnPageChangeCallback(pagerCb)
    }


    companion object {
        private const val INITIAL_SUM_PAGE_OFFSET = 0.0F
        private const val FLING_AMOUNT =
            50 // Increase value to hide bottom nav bar on lower amount of swipe & vice versa
        private val BOTTOM_NAV_BAR_HEIGHT = "56".toInt().toPx // As per material spec
    }
}