package com.jwpyo.soundmind.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jwpyo.soundmind.view.history.HistoryFragment
import com.jwpyo.soundmind.view.record.RecordFragment
import com.jwpyo.soundmind.view.setting.SettingFragment

class MainStateAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val fragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        RECORD to { RecordFragment() },
        HISTORY to { HistoryFragment() },
        SETTING to { SettingFragment() }
    )

    override fun createFragment(position: Int): Fragment {
        return fragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

    override fun getItemCount() = fragmentsCreators.size

    companion object {
        private const val RECORD = 0
        private const val HISTORY = 1
        private const val SETTING = 2
    }
}
