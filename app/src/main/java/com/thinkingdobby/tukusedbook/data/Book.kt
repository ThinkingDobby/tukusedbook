package com.thinkingdobby.tukusedbook.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Book(
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
    var detail_img_cnt: Int,
    var doodle: String,
    var damage: String,
    var stain: String,
    var state_lev: String,
    var price: Int,
    var detail_info: String,
    var like: Int,
    var sold: Boolean,
    var like_users: MutableMap<String, Boolean>
) : Parcelable {
    constructor() : this(
        "", "", "", "", "",
        "", "", "", 0, listOf(), "", 0,
        0, "", "", "", "", 0, "", 0, false, mutableMapOf()
    )
}