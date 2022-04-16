package com.thinkingdobby.tukusedbook

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.thinkingdobby.tukusedbook.data.Book
import com.thinkingdobby.tukusedbook.data.User
import com.thinkingdobby.tukusedbook.data.state_levs
import com.thinkingdobby.tukusedbook.data.state_levs_color
import kotlinx.android.synthetic.main.activity_book_detail.*
import java.lang.IllegalArgumentException

class BookDetailActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        val bundle = intent.extras
        val book = bundle!!.getParcelable<Book>("selectedBook")!!

        val pref = getSharedPreferences("profile", MODE_PRIVATE)
        val nowId = pref.getString("user_id", "temp")!!

        if (book.sold) {
            bookDetail_iv_sold.visibility = View.VISIBLE
            bookDetail_tv_sold.visibility = View.VISIBLE
        } else {
            bookDetail_iv_sold.visibility = View.INVISIBLE
            bookDetail_tv_sold.visibility = View.INVISIBLE
        }

        val storageRef = FirebaseStorage.getInstance().getReference("images/${book.book_id}")

        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.setTint(Color.WHITE)   // 추후 수정
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        storageRef.child("main").downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Glide 이용하여 이미지뷰에 로딩
                try {
                    Glide.with(this)
                        .load(task.result)
                        .placeholder(circularProgressDrawable)
                        .transform(CenterCrop())
                        .into(bookDetail_iv_main)
                } catch (e: IllegalArgumentException) {
                    Log.d("Glide Error", "from DetailActivity")
                }
            } else {
                // URL을 가져오지 못하면 토스트 메세지
                Toast.makeText(
                    this,
                    task.exception!!.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        storageRef.child("detail1").downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Glide 이용하여 이미지뷰에 로딩
                try {
                    Glide.with(this)
                        .load(task.result)
                        .placeholder(circularProgressDrawable)
                        .transform(CenterCrop())
                        .into(bookDetail_iv_detailImg1)
                } catch (e: IllegalArgumentException) {
                    Log.d("Glide Error", "from DetailActivity")
                }
            }
        }

        storageRef.child("detail2").downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Glide 이용하여 이미지뷰에 로딩
                try {
                    Glide.with(this)
                        .load(task.result)
                        .placeholder(circularProgressDrawable)
                        .transform(CenterCrop())
                        .into(bookDetail_iv_detailImg2)
                } catch (e: IllegalArgumentException) {
                    Log.d("Glide Error", "from DetailActivity")
                }
            }
        }

        storageRef.child("detail3").downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Glide 이용하여 이미지뷰에 로딩
                try {
                    Glide.with(this)
                        .load(task.result)
                        .placeholder(circularProgressDrawable)
                        .transform(CenterCrop())
                        .into(bookDetail_iv_detailImg3)
                } catch (e: IllegalArgumentException) {
                    Log.d("Glide Error", "from DetailActivity")
                }
            }
        }

        bookDetail_tv_title.text = book.title
        bookDetail_tv_publisher.text = book.publisher
        bookDetail_tv_author.text = book.author
        bookDetail_tv_pubdate.text = book.pub_date

        bookDetail_tv_stateLev.text = book.state_lev
        val color = state_levs_color[state_levs.indexOf(bookDetail_tv_stateLev.text.toString())]
        bookDetail_tv_stateLev.setTextColor(Color.parseColor(color))
        bookDetail_tv_doodle.text = book.doodle
        bookDetail_tv_damage.text = book.damage
        bookDetail_tv_stain.text = book.stain
        bookDetail_tv_detailInfo.text = book.detail_info

        Firebase.database.getReference("User").child(nowId).get().addOnSuccessListener {
            val user = it.getValue(User::class.java)!!
            bookDetail_tv_sellerNickName.text = user.name
            bookDetail_tv_sellerTel.text = user.tel
            bookDetail_tv_sellerIntro.text = user.intro
        }

        bookDetail_tv_price.text = book.price.toString()
    }
}
