package com.jwpyo.soundmind.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jwpyo.soundmind.view.ppg.PPGFragment
import com.jwpyo.soundmind.view.setting.SettingFragment
import com.jwpyo.soundmind.view.voice.VoiceFragment

class MainStateAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val fragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        VOICE to { VoiceFragment() },
        PPG to { PPGFragment() },
        SETTING to { SettingFragment() }
    )

    override fun createFragment(position: Int): Fragment {
        return fragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

    override fun getItemCount() = fragmentsCreators.size

    companion object {
        private const val VOICE = 0
        private const val PPG = 1
        private const val SETTING = 2
    }
}
