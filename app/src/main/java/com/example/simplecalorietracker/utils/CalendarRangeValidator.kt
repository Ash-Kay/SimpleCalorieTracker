package com.example.simplecalorietracker.utils

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints

class CalendarRangeValidator(private val minDate: Long, private val maxDate: Long) :
    CalendarConstraints.DateValidator {


    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("not implemented")
    }

    override fun describeContents(): Int {
        TODO("not implemented")
    }

    override fun isValid(date: Long): Boolean {
        return !(minDate > date || maxDate < date)

    }

    companion object CREATOR : Parcelable.Creator<CalendarRangeValidator> {
        override fun createFromParcel(parcel: Parcel): CalendarRangeValidator {
            return CalendarRangeValidator(parcel)
        }

        override fun newArray(size: Int): Array<CalendarRangeValidator?> {
            return arrayOfNulls(size)
        }
    }

}