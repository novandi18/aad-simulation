package com.dicoding.courseschedule.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.notification.DailyReminder
import com.dicoding.courseschedule.util.NightMode

class SettingsFragment : PreferenceFragmentCompat(), OnPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        //TODO 10 : Update theme based on value in ListPreference
        val listPreference = findPreference<ListPreference>(resources.getString(R.string.pref_key_dark))
        listPreference?.onPreferenceChangeListener = this

        //TODO 11 : Schedule and cancel notification in DailyReminder based on SwitchPreference
        val switchPreference = findPreference<SwitchPreference>(resources.getString(R.string.pref_key_notify))
        switchPreference?.onPreferenceChangeListener = this
    }

    private fun updateTheme(nightMode: Int): Boolean {
        AppCompatDelegate.setDefaultNightMode(nightMode)
        requireActivity().recreate()
        return true
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        when (preference.key) {
            resources.getString(R.string.pref_key_dark) -> {
                val listAuto = resources.getString(R.string.pref_dark_auto)
                val listDark = resources.getString(R.string.pref_dark_on)
                val listLight = resources.getString(R.string.pref_dark_off)

                when (newValue.toString()) {
                    listAuto -> updateTheme(NightMode.AUTO.value)
                    listDark -> updateTheme(NightMode.ON.value)
                    listLight -> updateTheme(NightMode.OFF.value)
                }
            }
            resources.getString(R.string.pref_key_notify) -> {
                val isEnabled = newValue as Boolean
                if (isEnabled) DailyReminder().setDailyReminder(requireContext()) else DailyReminder().cancelAlarm(requireContext())
            }
        }

        return true
    }
}