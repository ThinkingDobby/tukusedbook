package com.thinkingdobby.tukusedbook.viewHolder

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.storage.FirebaseStorage
import com.thinkingdobby.tukusedbook.data.Book
import com.thinkingdobby.tukusedbook.data.state_levs
import com.thinkingdobby.tukusedbook.data.state_levs_color
import kotlinx.android.synthetic.main.book_card.view.*
import java.lang.IllegalArgumentException

class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val book_tv_title = itemView.book_tv_title
    val book_tv_publisher = itemView.book_tv_publisher
    val book_tv_stateLev = itemView.book_tv_stateLev
    val book_tv_price = itemView.book_tv_price
    val book_tv_like = itemView.book_tv_like

    val book_iv_book = itemView.book_iv_book
    val book_iv_line = itemView.book_iv_line

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun bind(book: Book, position: Int, context: Context) {
        book_tv_title.text = book.title
        book_tv_publisher.text = book.publisher
        book_tv_stateLev.text = book.state_lev
        book_tv_price.text = book.price.toString()
        book_tv_like.text = book.like.toString()

        if (position == 0) book_iv_line.visibility = View.INVISIBLE

        val color = state_levs_color[state_levs.indexOf(book_tv_stateLev.text.toString())]
        book_tv_stateLev.setTextColor(Color.parseColor(color))

//        이미지 로드
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.setTint(Color.WHITE)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

//        메인 이미지 로드
        val storageRef = FirebaseStorage.getInstance().getReference("images/${book.book_id}").child("main")

        storageRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Glide 이용하여 이미지뷰에 로딩
                try {
                    // 추후 progress bar와 visibility 사용하는 방식으로 변경할 것
                    Glide.with(context)
                        .load(task.result)
                        .placeholder(circularProgressDrawable)
                        .transform(CenterCrop())
                        .into(book_iv_book)
                } catch (e: IllegalArgumentException) {
                    Log.d("Glide Error", "from PetViewHolder")
                }
            } else {
                // URL을 가져오지 못한 경우
                Log.d("Image Load Error", "URL 불러오지 못함")
            }
        }
    }
}