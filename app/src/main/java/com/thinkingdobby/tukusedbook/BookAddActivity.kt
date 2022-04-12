package com.thinkingdobby.tukusedbook

import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.thinkingdobby.tukusedbook.data.Book
import kotlinx.android.synthetic.main.activity_book_add.*
import java.text.SimpleDateFormat
import java.util.*

class BookAddActivity : AppCompatActivity() {
    private var pickImageFromAlbum = 0
    private var uriPhoto: Uri? =
        Uri.parse("android.resource://com.thinkingdobby.tukusedbook/drawable/bookadd_iv_basic")

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

        bookAdd_iv_main.setOnClickListener { loadImage() }
        bookAdd_tv_mainImgAdd.setOnClickListener { loadImage() }
        bookAdd_btn_back.setOnClickListener { finish() }

        bookAdd_v_bot.setOnTouchListener { view, motionEvent -> true }

        // EditText 관리
        bookAdd_et_title.addTextChangedListener(object : TextWatcher {
            var prev = ""

            override fun afterTextChanged(p0: Editable?) {
                if (bookAdd_et_title.text.toString() != "") {
                    bookAdd_et_title.setBackgroundResource(R.drawable.createprofile_et_essential_satisfied)
                } else {
                    bookAdd_et_title.setBackgroundResource(R.drawable.createprofile_et_essential)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        bookAdd_et_publisher.addTextChangedListener(object : TextWatcher {
            var prev = ""

            override fun afterTextChanged(p0: Editable?) {
                if (bookAdd_et_publisher.text.toString() != "") {
                    bookAdd_et_publisher.setBackgroundResource(R.drawable.createprofile_et_essential_satisfied)
                } else {
                    bookAdd_et_publisher.setBackgroundResource(R.drawable.createprofile_et_essential)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        bookAdd_et_author.addTextChangedListener(object : TextWatcher {
            var prev = ""

            override fun afterTextChanged(p0: Editable?) {
                if (bookAdd_et_author.text.toString() != "") {
                    bookAdd_et_author.setBackgroundResource(R.drawable.createprofile_et_essential_satisfied)
                } else {
                    bookAdd_et_author.setBackgroundResource(R.drawable.createprofile_et_essential)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

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
            val dlg = DatePickerDialog(
                this, R.style.DatePickerStyle,
                { view, year, month, dayOfMonth ->
                    time = "${year}년 ${month + 1}월 ${dayOfMonth}일"
                    bookAdd_et_pubDate.setText(time)
                }, year, month, date
            )
            dlg.show()
        }

        bookAdd_btn_register.setOnClickListener {
            if (bookAdd_et_title.length() == 0 || bookAdd_et_publisher.length() == 0 || bookAdd_et_author.length() == 0) {
                Toast.makeText(this, "빨간색 테두리로 나타나는 항목은 반드시 입력해야 해요.", Toast.LENGTH_SHORT).show()
            } else {
                val progDlg = ProgressDialog(this, R.style.ProgressBarStyle)
                progDlg.setMessage("잠시만 기다려주세요...")
                progDlg.show()

                // Firebase Database Upload
                val ref = FirebaseDatabase.getInstance().getReference("Book").push()

                val pref = getSharedPreferences("profile", MODE_PRIVATE)
                val sellerId = pref.getString("user_id", "temp")
                val pageTmp = bookAdd_et_page.text.toString()
                val page = if (!pageTmp.all { it.isDigit() }) -1 else pageTmp.toInt()
                val size1Tmp = bookAdd_et_size1.text.toString()
                val size1 = if (!size1Tmp.all { it.isDigit() }) -1 else size1Tmp.toInt()
                val size2Tmp = bookAdd_et_size2.text.toString()
                val size2 = if (!size2Tmp.all { it.isDigit() }) -1 else size2Tmp.toInt()

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

                imageUpload(book.book_id, "main", edit)
            }

        }
    }

    private fun loadImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Load Picture"), pickImageFromAlbum)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickImageFromAlbum) {
            if (resultCode == Activity.RESULT_OK) {
                uriPhoto = data?.data
                bookAdd_iv_main.setImageURI(uriPhoto)
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view: View? = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) && view is EditText && !view.javaClass
                .name.startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x: Float = ev.rawX + view.getLeft() - scrcoords[0]
            val y: Float = ev.rawY + view.getTop() - scrcoords[1]
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) (this.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager).hideSoftInputFromWindow(
                this.window.decorView.applicationWindowToken, 0
            )
        }
        return super.dispatchTouchEvent(ev)
    }

    // type은 main, sub1, sub2, ... 중 어느 이미지인지
    private fun imageUpload(id: String, type: String, edit: Boolean) {
        val storageRef = FirebaseStorage.getInstance().getReference("images/$id").child(type)
        storageRef.putFile(uriPhoto!!).addOnSuccessListener {
            if (edit) Toast.makeText(this, "서적 정보가 변경됐어요.", Toast.LENGTH_SHORT).show()
            else Toast.makeText(this, "판매 서적이 등록됐어요.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
