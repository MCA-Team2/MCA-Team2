package com.jwpyo.soundmind.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jwpyo.soundmind.R
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

@BindingAdapter(
    value = ["pagerAdapter", "pageLimit", "userInputEnabled"],
    requireAll = false
)
fun bindPagerAdapter(
    view: ViewPager2,
    adapter: FragmentStateAdapter?,
    pageLimit: Int,
    userInputEnabled: Boolean?,
) {
    view.adapter = adapter
    view.offscreenPageLimit = pageLimit
    view.isUserInputEnabled = userInputEnabled ?: true
}

@BindingAdapter("bindNavigation")
fun bindNavigation(view: ViewPager2, navigationView: BottomNavigationView) {
    navigationView.setOnNavigationItemSelectedListener { item ->
        val checked = item.setChecked(true)
        when (checked.itemId) {
            R.id.nav_record -> {
                view.setCurrentItem(0, false)
                return@setOnNavigationItemSelectedListener true
            }
            R.id.nav_history -> {
                view.setCurrentItem(1, false)
                return@setOnNavigationItemSelectedListener true
            }
            R.id.nav_setting -> {
                view.setCurrentItem(2, false)
                return@setOnNavigationItemSelectedListener true
            }
        }
        return@setOnNavigationItemSelectedListener false
    }
}

@BindingAdapter("ldt_text")
fun bindingLDTText(view: TextView, ldt: LocalDateTime?) {
    view.text =
        if (ldt == null) "invalid time"
        else ldt.format(DateTimeFormatter.ofPattern("MM/dd HH:mm:ss"))
}

@BindingAdapter("date_text")
fun bindingLDTText(view: TextView, dt: LocalDate?) {
    view.text =
        if (dt == null) "invalid time"
        else dt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
}


