package com.thinkingdobby.tukusedbook.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Book (
    val book_id: String,
    val seller_id: String,
    val post_date: String,
    val title: String,
    val publisher: String,
    val author: String,
    val pub_date: String,
    val ISBN: String,
    val page: Int,
    val size: String,
    val department: String,
    val grade: Int,
    val detail_img: Array<String>,
    val doodle: String,
    val damage: String,
    val stain: String,
    val state_lev: String,
    val price: Int,
    val like: Int,
    val sold: Boolean
): Parcelable {
    constructor(): this("", "", "", "", "",
        "", "", "", 0, "", "", 0,
        arrayOf(), "", "", "", "", 0, 0, false)
}