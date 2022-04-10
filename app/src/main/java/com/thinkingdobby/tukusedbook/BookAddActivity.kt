package com.thinkingdobby.tukusedbook

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.thinkingdobby.tukusedbook.data.Book
import kotlinx.android.synthetic.main.activity_book_add.*
import java.text.SimpleDateFormat
import java.util.*

class BookAddActivity : AppCompatActivity() {
    private var time = SimpleDateFormat("yyyy년 MM월 dd일").format(Date())
    private var bookId = "temp"
    private var like = 0
    private var sold = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_add)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        bookAdd_btn_back.setOnClickListener { finish() }

        // 기본 초기화
        bookAdd_et_pubDate.setText(time)

        // 생성 혹은 수정
        val edit = intent.getBooleanExtra("edit", false)
        if (edit) {
            // 수정인 경우 텍스트 변경
            // 수정인 경우 et에 기존 값 대입
        }

        bookAdd_btn_pubDate.setOnClickListener {
            val today = GregorianCalendar()
            val year: Int = today.get(Calendar.YEAR)
            val month: Int = today.get(Calendar.MONTH)
            val date: Int = today.get(Calendar.DATE)
            val dlg = DatePickerDialog(this, R.style.DatePickerStyle,
                { view, year, month, dayOfMonth ->
                    time = "${year}년 ${month + 1}월 ${dayOfMonth}일"
                    bookAdd_et_pubDate.setText(time)
                }, year, month, date)
            dlg.show()
        }

        bookAdd_btn_register.setOnClickListener {
            if (bookAdd_et_title.length() == 0 || bookAdd_et_publisher.length() == 0 || bookAdd_et_author.length() == 0) {
                Toast.makeText(this, "빨간색 테두리로 나타나는 항목은 반드시 입력해야 해요.", Toast.LENGTH_SHORT).show()
            } else {

                // Firebase Database Upload
                val ref = FirebaseDatabase.getInstance().getReference("Book").push()

                val pref = getSharedPreferences("profile", MODE_PRIVATE)
                val sellerId = pref.getString("user_id", "temp")
                val pageTmp = bookAdd_et_page.text.toString()
                val page = if (!pageTmp.all{it.isDigit()}) -1 else pageTmp.toInt()
                val size1Tmp = bookAdd_et_size1.text.toString()
                val size1 = if(!size1Tmp.all{it.isDigit()}) -1 else size1Tmp.toInt()
                val size2Tmp = bookAdd_et_size2.text.toString()
                val size2 = if(!size2Tmp.all{it.isDigit()}) -1 else size2Tmp.toInt()

                val book = Book(
                    if (!edit) ref.key!! else bookId,
                    sellerId!!,
                    System.currentTimeMillis().toString(),
                    bookAdd_et_title.text.toString(),
                    bookAdd_et_publisher.text.toString(),
                    bookAdd_et_author.text.toString(),
                    bookAdd_et_pubDate.text.toString(),
                    bookAdd_et_isbn.text.toString(),
                    page,
                    listOf(size1, size2),
                    bookAdd_et_department.text.toString(),
                    bookAdd_et_grade.text.toString().toInt(),
                    listOf(),
                    bookAdd_et_doodle.text.toString(),
                    bookAdd_et_damage.text.toString(),
                    bookAdd_et_stain.text.toString(),
                    bookAdd_et_stateLev.text.toString(),
                    bookAdd_et_price.text.toString().toInt(),
                    like,
                    sold
                )

                if (edit) FirebaseDatabase.getInstance().getReference("Book/$bookId").setValue(book)
                else ref.setValue(book)

                if (edit) Toast.makeText(this, "서적 정보가 등록됐어요.", Toast.LENGTH_SHORT).show()
                else Toast.makeText(this, "판매 서적이 등록됐어요.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

    }
}
