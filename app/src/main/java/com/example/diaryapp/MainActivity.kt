package com.example.diaryapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved theme (Light/Dark)
        ThemeManager.applySavedTheme(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ---------------------------
        // TOOLBAR & CENTERED TITLE
        // ---------------------------
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Set centered title TextView
        val titleView = findViewById<TextView>(R.id.toolbarTitle)
        titleView.text = "DiaryApp"

        // ---------------------------
        // TABLAYOUT + VIEWPAGER2 SETUP
        // ---------------------------
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        viewPager.adapter = PageAdapter(this)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Date"
                1 -> "Entry"
                else -> "Diary"
            }
        }.attach()
    }

    // ---------------------------
    // TOP RIGHT MENU (DARK MODE)
    // ---------------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            // Toggle Light ↔ Dark Theme
            R.id.action_toggle_theme -> {
                ThemeManager.toggleTheme(this)
                recreate()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
