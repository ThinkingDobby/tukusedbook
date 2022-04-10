package com.thinkingdobby.tukusedbook.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Parcelize
class Book (
    var book_id: String,
    var seller_id: String,
    var post_date: String,
    var title: String,
    var publisher: String,
    var author: String,
    var pub_date: String,
    var ISBN: String,
    var page: Int,
    var size: List<Int>,
    var department: String,
    var grade: Int,
    var detail_img: List<String>,
    var doodle: String,
    var damage: String,
    var stain: String,
    var state_lev: String,
    var price: Int,
    var like: Int,
    var sold: Boolean
): Parcelable {
    constructor(): this("", "", "", "", "",
        "", "", "", 0, listOf(), "", 0,
        listOf(), "", "", "", "", 0, 0, false)
}