package com.thinkingdobby.tukusedbook.data

import java.sql.Timestamp
import java.text.SimpleDateFormat

fun millToDate(mills: Long): String? {
    val pattern = "yyyy년 M월 d일"
    val formatter = SimpleDateFormat(pattern)
    return formatter.format(Timestamp(mills))
}