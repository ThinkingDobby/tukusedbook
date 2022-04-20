package com.thinkingdobby.tukusedbook.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (
    val id: String,
    val name: String,
    val tel: String,
    val department: String,
    val grade: Int,
    val intro: String,
    val my_books: MutableList<String>,
    val interested_books: MutableList<String>
) : Parcelable {
    constructor() : this("", "", "", "", 1, "", mutableListOf(), mutableListOf())
}