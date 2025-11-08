package com.eliaskrr.fitmacros.ui.opciones

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.DayOfWeek
import java.time.LocalTime

enum class ReminderType {
    BREAKFAST, LUNCH, DINNER, WEIGH_IN
}

class NotificationsViewModel : ViewModel() {

    var showTimePicker by mutableStateOf(false)
        private set

    var showDayPicker by mutableStateOf(false)
        private set

    var editingReminder by mutableStateOf<ReminderType?>(null)
        private set

    var breakfastTime by mutableStateOf(LocalTime.of(9, 0))
        private set

    var lunchTime by mutableStateOf(LocalTime.of(14, 0))
        private set

    var dinnerTime by mutableStateOf(LocalTime.of(21, 0))
        private set

    var weighInTime by mutableStateOf(LocalTime.of(9, 0))
        private set

    var weighInDay by mutableStateOf(DayOfWeek.SATURDAY)
        private set

    fun onTimePickerRequested(reminderType: ReminderType) {
        editingReminder = reminderType
        showTimePicker = true
    }

    fun onTimePickerDismissed() {
        showTimePicker = false
        editingReminder = null
    }

    fun onDayPickerRequested() {
        showDayPicker = true
    }

    fun onDayPickerDismissed() {
        showDayPicker = false
    }

    fun onDaySelected(day: DayOfWeek) {
        weighInDay = day
    }

    fun onTimeSelected(time: LocalTime) {
        when (editingReminder) {
            ReminderType.BREAKFAST -> breakfastTime = time
            ReminderType.LUNCH -> lunchTime = time
            ReminderType.DINNER -> dinnerTime = time
            ReminderType.WEIGH_IN -> weighInTime = time
            null -> {}
        }
    }
}