package com.thinkingdobby.tukusedbook.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    val id: String,
    val name: String,
    val tell: String,
    val department: String,
    val grade: Int,
    val intro: String = ""
) : Parcelable