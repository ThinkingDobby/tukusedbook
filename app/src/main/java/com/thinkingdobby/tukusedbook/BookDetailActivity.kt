package com.thinkingdobby.tukusedbook

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.thinkingdobby.tukusedbook.data.*
import kotlinx.android.synthetic.main.activity_book_detail.*
import kotlinx.android.synthetic.main.book_detail_dialog.*
import java.lang.IllegalArgumentException

class BookDetailActivity : AppCompatActivity() {
    private var admin = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        overridePendingTransition(0, 0)

        val bundle = intent.extras
        val book = bundle!!.getParcelable<Book>("selectedBook")!!

        val pref = getSharedPreferences("profile", MODE_PRIVATE)
        val nowId = pref.getString("user_id", "temp")!!

        val storageRef = FirebaseStorage.getInstance().getReference("images/${book.book_id}")

        if (book.seller_id == nowId) admin = true

        if (admin) {
            bookDetail_btn_edit.visibility = View.VISIBLE
            bookDetail_btn_delete.visibility = View.VISIBLE

            bookDetail_icon_like.setImageResource(R.drawable.main_icon_heart_disabled)
            bookDetail_icon_like.isClickable = false

            bookDetail_iv_line2.visibility = View.GONE

            bookDetail_cl_seller.visibility = View.GONE
            bookDetail_btn_soldFin.visibility = View.VISIBLE

            bookDetail_icon_chat.visibility = View.INVISIBLE
            bookDetail_icon_tv_chat.visibility = View.INVISIBLE
        } else {
            bookDetail_btn_edit.visibility = View.INVISIBLE
            bookDetail_btn_delete.visibility = View.INVISIBLE

            bookDetail_iv_line2.visibility = View.VISIBLE

            bookDetail_cl_seller.visibility = View.VISIBLE
            bookDetail_btn_soldFin.visibility = View.GONE

            bookDetail_icon_chat.visibility = View.VISIBLE
            bookDetail_icon_tv_chat.visibility = View.VISIBLE

            val ref = FirebaseDatabase.getInstance().getReference("Book").child(book.book_id)
            var like = book.like_users[nowId] ?: false
            if (like) bookDetail_icon_like.setImageResource(R.drawable.main_icon_heart_filled)
            else bookDetail_icon_like.setImageResource(R.drawable.main_icon_heart)

            bookDetail_icon_like.setOnClickListener {
                val checkUpdates = mutableMapOf<String, Any>()

                // 관심 항목 추가
                val userRef = FirebaseDatabase.getInstance().getReference("User").child(nowId)
                userRef.get().addOnSuccessListener {
                    val interestedUpdates = mutableMapOf<String, Any>()
                    val user = it.getValue(User::class.java)!!
                    if (book.book_id in user.interested_books) user.interested_books.remove(book.book_id)
                    else user.interested_books.add(book.book_id)

                    interestedUpdates["interested_books"] = user.interested_books

                    userRef.updateChildren(interestedUpdates)
                }

                if (like) {
                    book.like_users[nowId] = false
                    book.like--
                    checkUpdates["like_users"] = book.like_users
                    checkUpdates["like"] = book.like
                    bookDetail_icon_like.setImageResource(R.drawable.main_icon_heart)
                    like = false
                } else {
                    book.like_users[nowId] = true
                    book.like++
                    checkUpdates["like_users"] = book.like_users
                    checkUpdates["like"] = book.like
                    bookDetail_icon_like.setImageResource(R.drawable.main_icon_heart_filled)
                    like = true
                }
                bookDetail_tv_like.text = book.like.toString()
                ref.updateChildren(checkUpdates)
            }
        }

        bookDetail_btn_back.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        bookDetail_btn_edit.setOnClickListener {
            val intent = Intent(this, BookAddActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("selectedBook", book)
            intent.putExtras(bundle)
            intent.putExtra("edit", true)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        bookDetail_btn_delete.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference("Book").child(book.book_id)
            var sRef = FirebaseStorage.getInstance().getReference("images").child(book.book_id)

            val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            val dlgXml = View.inflate(this, R.layout.basic_dialog, null)
            val tv = dlgXml.findViewById<TextView>(R.id.basicDialog_tv_basic)
            builder.setView(dlgXml)

            // 내가 등록한 책 항목 제거
            val userRef = FirebaseDatabase.getInstance().getReference("User").child(nowId)
            userRef.get().addOnSuccessListener {
                val myBooksUpdates = mutableMapOf<String, Any>()
                val user = it.getValue(User::class.java)!!
                if (book.book_id in user.my_books) user.my_books.remove(book.book_id)

                myBooksUpdates["my_books"] = user.my_books

                userRef.updateChildren(myBooksUpdates)
            }

            // 임시 디자인
            if (book.seller_id == nowId) {
                tv.text = "삭제한 글은 복구할 수 없습니다.\n\n글을 삭제할까요?"

                builder.setPositiveButton("아니오") { _, which ->
                }

                builder.setNegativeButton("예") { _, which ->
                    ref.removeValue()
                    // Firebase Storage - 폴더 내부 내용 없어야 폴더 삭제 가능
                    sRef.child("main").delete()
                    sRef.child("detail1").delete()
                    sRef.child("detail2").delete()
                    sRef.child("detail3").delete()
                    sRef.delete()
                    Toast.makeText(this, "삭제되었어요.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                builder.create().show()
            }
        }

        var sold = book.sold

        if (sold) {
            bookDetail_iv_sold.visibility = View.VISIBLE
            bookDetail_tv_sold.visibility = View.VISIBLE
            bookDetail_btn_soldFin.text = "판매 확정 취소"
        } else {
            bookDetail_iv_sold.visibility = View.INVISIBLE
            bookDetail_tv_sold.visibility = View.INVISIBLE
            bookDetail_btn_soldFin.text = "판매 확정"
        }

        bookDetail_btn_soldFin.setOnClickListener {
            val builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)

            val dlgXml = View.inflate(this, R.layout.basic_dialog, null)
            val tv = dlgXml.findViewById<TextView>(R.id.basicDialog_tv_basic)
            builder.setView(dlgXml)

            if (sold) {
                tv.text = "판매 확정을 취소할까요?"

                builder.setPositiveButton("아니오") { _, which ->
                }

                builder.setNegativeButton("예") { _, which ->
                    sold = false
                    bookDetail_iv_sold.visibility = View.INVISIBLE
                    bookDetail_tv_sold.visibility = View.INVISIBLE
                    bookDetail_btn_soldFin.text = "판매 확정"

                    FirebaseDatabase.getInstance().getReference("Book/${book.book_id}/sold").setValue(sold)
                    Toast.makeText(this, "판매 확정이 취소되었어요.", Toast.LENGTH_SHORT).show()
                }
                builder.show()
            } else {
                tv.text = "판매 확정 시 다른 사람들은 이 글을 볼 수 없어요.\n\n판매 확정 하시겠어요?"

                builder.setPositiveButton("아니오") { _, which ->
                }

                builder.setNegativeButton("예") { _, which ->
                    sold = true
                    bookDetail_iv_sold.visibility = View.VISIBLE
                    bookDetail_tv_sold.visibility = View.VISIBLE
                    bookDetail_btn_soldFin.text = "판매 확정 취소"

                    FirebaseDatabase.getInstance().getReference("Book/${book.book_id}/sold").setValue(sold)
                    Toast.makeText(this, "판매가 확정되었어요.", Toast.LENGTH_SHORT).show()
                }
                builder.show()
            }
        }

        bookDetail_tv_moreInfo.setOnClickListener {
            val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            val dlgXml = View.inflate(this, R.layout.book_detail_dialog, null)
            dlg.setPositiveButton("닫기") { _, which ->
            }
            val date = dlgXml.findViewById<TextView>(R.id.bookDetailDialog_tv_date)
            date.text = millToDate(book.post_date.toLong())
            val isbn = dlgXml.findViewById<TextView>(R.id.bookDetailDialog_tv_isbn)
            isbn.text = book.ISBN
            val page = dlgXml.findViewById<TextView>(R.id.bookDetailDialog_tv_page)
            page.text = "${book.page} 쪽"
            val size = dlgXml.findViewById<TextView>(R.id.bookDetailDialog_tv_size)
            size.text = "${book.size[0]} x ${book.size[1]} (mm)"
            dlg.setView(dlgXml)
            dlg.show()
        }

        bookDetail_tv_guide.setOnClickListener {
            val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            val dlgXml = View.inflate(this, R.layout.state_guide, null)
            dlg.setPositiveButton("닫기") { _, which ->
            }
            dlg.setView(dlgXml)
            dlg.show()
        }


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

        bookDetail_tv_like.text = book.like.toString()

        Firebase.database.getReference("User").child(book.seller_id).get().addOnSuccessListener {
            val user = it.getValue(User::class.java)!!
            bookDetail_tv_sellerNickName.text = user.name
            bookDetail_tv_sellerTel.text = user.tel
            bookDetail_tv_sellerIntro.text = user.intro
        }

        bookDetail_tv_price.text = book.price.toString()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}
