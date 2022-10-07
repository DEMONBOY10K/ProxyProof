package com.mact.proxyproof

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener


class SliderActivity : AppCompatActivity() {
    private var viewPager: ViewPager? = null
    private var intromanager: com.mact.proxyproof.Intromanager? = null
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private lateinit var dots: Array<TextView?>
    var next: Button? = null
    var skip: Button? = null
    private var dotsLayout: LinearLayout? = null
    private lateinit var layouts: IntArray
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Material)
        setContentView(R.layout.activity_slider)

        // Importing the intromanager class which will check if this is the first time the application has been launchedd.
        intromanager = Intromanager(this)
        if (!intromanager!!.Check()) {
            intromanager!!.setFirst(false)
            val i = Intent(this@SliderActivity, Home::class.java)
            startActivity(i)
            finish()
        }
        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        setContentView(R.layout.activity_slider)

        //Objects
        viewPager = findViewById<View>(R.id.view_pager) as ViewPager
        dotsLayout = findViewById<View>(R.id.layoutDots) as LinearLayout
        skip = findViewById<View>(R.id.btn_skip) as Button
        next = findViewById<View>(R.id.btn_next) as Button
        layouts = intArrayOf(R.layout.slider_1, R.layout.slider_2, R.layout.slider_3)
        addBottomDots(0)
        changeStatusBarColor()
        viewPagerAdapter = ViewPagerAdapter()
        viewPager!!.adapter = viewPagerAdapter
        viewPager!!.addOnPageChangeListener(viewListener)

        //For the next and previous buttons
        skip!!.setOnClickListener { view: View? ->
            val i = Intent(this@SliderActivity, Home::class.java)
            startActivity(i)
            finish()
        }
        next!!.setOnClickListener { view: View? ->
            val current = getItem(+1)
            if (current < layouts.size) {
                viewPager!!.currentItem = current
            } else {
                val i = Intent(this@SliderActivity, Home::class.java)
                startActivity(i)
                finish()
            }
        }
    }

    //Giving the dots functionality
    private fun addBottomDots(position: Int) {
        dots = arrayOfNulls(layouts.size)
        val colorActive = resources.getIntArray(R.array.dot_active)
        val colorInactive = resources.getIntArray(R.array.dot_inactive)
        dotsLayout!!.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = Html.fromHtml("&#8226;")
            dots[i]!!.textSize = 35f
            dots[i]!!.setTextColor(colorInactive[position])
            dotsLayout!!.addView(dots[i])
        }
        if (dots.size > 0) dots[position]!!.setTextColor(colorActive[position])
    }

    private fun getItem(i: Int): Int {
        return viewPager!!.currentItem + i
    }

    var viewListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            addBottomDots(position)
            if (position == layouts.size - 1) {
                next!!.text = "PROCEED"
                skip!!.visibility = View.GONE
            } else {
                next!!.text = "NEXT"
                skip!!.visibility = View.VISIBLE
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    //PagerAdapter class which will inflate our sliders in our ViewPager
    inner class ViewPagerAdapter : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null
        override fun instantiateItem(myContainer: ViewGroup, mPosition: Int): Any {
            layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val v = layoutInflater!!.inflate(layouts[mPosition], myContainer, false)
            myContainer.addView(v)
            return v
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(mView: View, mObject: Any): Boolean {
            return mView === mObject
        }

        override fun destroyItem(mContainer: ViewGroup, mPosition: Int, mObject: Any) {
            val v = mObject as View
            mContainer.removeView(v)
        }
    }
}