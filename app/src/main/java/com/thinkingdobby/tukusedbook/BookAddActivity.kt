package com.thinkingdobby.tukusedbook

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.thinkingdobby.tukusedbook.data.*
import kotlinx.android.synthetic.main.activity_book_add.*
import java.text.SimpleDateFormat
import java.util.*

class BookAddActivity : AppCompatActivity() {
    private var basicUri =
        "android.resource://com.thinkingdobby.tukusedbook/drawable/bookadd_iv_basic"
    private var uriPhoto: Uri? =
        Uri.parse(basicUri)
    private var detailUriPhotoTemp1: Uri? =
        Uri.parse(basicUri)
    private var detailUriPhotoTemp2: Uri? =
        Uri.parse(basicUri)
    private var detailUriPhotoTemp3: Uri? =
        Uri.parse(basicUri)

    private var time = SimpleDateFormat("yyyy년 MM월 dd일").format(Date())
    private var bookId = "temp"
    private var imgCnt = 3
    private var like = 0
    private var sold = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_add)


        bookAdd_iv_main.setOnClickListener { loadImage(0) }
        bookAdd_tv_mainImgAdd.setOnClickListener { loadImage(0) }

        bookAdd_iv_detailImgTemp1.setOnClickListener {
            loadImage(1)
        }
        bookAdd_iv_detailImgTemp2.setOnClickListener {
            loadImage(2)
        }
        bookAdd_iv_detailImgTemp3.setOnClickListener {
            loadImage(3)
        }
        bookAdd_btn_removeImgTemp1.setOnClickListener {
            detailUriPhotoTemp1 = Uri.parse(basicUri)
            bookAdd_iv_detailImgTemp1.setImageURI(detailUriPhotoTemp1)
            bookAdd_btn_removeImgTemp1.visibility = View.INVISIBLE
            bookAdd_tv_detailImgTemp1.visibility = View.VISIBLE
        }
        bookAdd_btn_removeImgTemp2.setOnClickListener {
            detailUriPhotoTemp2 = Uri.parse(basicUri)
            bookAdd_iv_detailImgTemp2.setImageURI(detailUriPhotoTemp2)
            bookAdd_btn_removeImgTemp2.visibility = View.INVISIBLE
            bookAdd_tv_detailImgTemp2.visibility = View.VISIBLE
        }
        bookAdd_btn_removeImgTemp3.setOnClickListener {
            detailUriPhotoTemp3 = Uri.parse(basicUri)
            bookAdd_iv_detailImgTemp3.setImageURI(detailUriPhotoTemp3)
            bookAdd_btn_removeImgTemp3.visibility = View.INVISIBLE
            bookAdd_tv_detailImgTemp3.visibility = View.VISIBLE
        }


        bookAdd_btn_back.setOnClickListener { finish() }

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

        bookAdd_et_detailInfo.addTextChangedListener(object : TextWatcher {
            var prev = ""

            override fun afterTextChanged(p0: Editable?) {
                if (bookAdd_et_detailInfo.lineCount >= 7) {
                    bookAdd_et_detailInfo.setText(prev)
                    bookAdd_et_detailInfo.setSelection(bookAdd_et_detailInfo.length())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                prev = p0.toString()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        // 선택 버튼
        bookAdd_btn_department.setOnClickListener {
            val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            dlg.setItems(departments) { _, which ->
                bookAdd_et_department.setText(departments[which])
            }
            dlg.setTitle("학과를 선택하세요.")
            dlg.show()
        }

        bookAdd_btn_grade.setOnClickListener {
            val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            val grades = arrayOf("1", "2", "3", "4")
            dlg.setItems(grades) { _, which ->
                bookAdd_et_grade.setText(grades[which])
            }
            dlg.setTitle("학년을 선택하세요.")
            dlg.show()
        }

        bookAdd_btn_doodle.setOnClickListener {
            val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            dlg.setItems(doodles) { _, which ->
                bookAdd_et_doodle.setText(doodles[which])
            }
            dlg.setTitle("낙서 여부를 선택하세요.")
            dlg.show()
        }

        bookAdd_btn_damage.setOnClickListener {
            val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            dlg.setItems(damages) { _, which ->
                bookAdd_et_damage.setText(damages[which])
            }
            dlg.setTitle("손상 여부를 선택하세요.")
            dlg.show()
        }

        bookAdd_btn_stain.setOnClickListener {
            val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            dlg.setItems(stains) { _, which ->
                bookAdd_et_stain.setText(stains[which])
            }
            dlg.setTitle("얼룩 여부를 선택하세요.")
            dlg.show()
        }

        bookAdd_btn_stateLev.setOnClickListener {
            val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            dlg.setItems(state_levs) { _, which ->
                bookAdd_et_stateLev.setText(state_levs[which])
                bookAdd_et_stateLev.setTextColor(Color.parseColor(state_levs_color[which]))
            }
            dlg.setTitle("서적 등급을 선택하세요.")
            dlg.show()
        }

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
                    imgCnt,
                    bookAdd_et_doodle.text.toString(),
                    bookAdd_et_damage.text.toString(),
                    bookAdd_et_stain.text.toString(),
                    bookAdd_et_stateLev.text.toString(),
                    bookAdd_et_price.text.toString().toInt(),
                    bookAdd_et_detailInfo.text.toString(),
                    like,
                    sold,
                    mutableMapOf()
                )

                if (edit) FirebaseDatabase.getInstance().getReference("Book/$bookId").setValue(book)
                else ref.setValue(book)

                imageUpload(book.book_id, "main", edit)
                imageUpload(book.book_id, "detail1", edit)
                imageUpload(book.book_id, "detail2", edit)
                imageUpload(book.book_id, "detail3", edit)
            }

        }
    }

    private fun loadImage(type: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Load Picture"), type)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                uriPhoto = data?.data
                bookAdd_iv_main.setImageURI(uriPhoto)
            }
        } else if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                detailUriPhotoTemp1 = data?.data
                bookAdd_iv_detailImgTemp1.setImageURI(detailUriPhotoTemp1)
                bookAdd_btn_removeImgTemp1.visibility = View.VISIBLE
                bookAdd_tv_detailImgTemp1.visibility = View.INVISIBLE
            }
        } else if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                detailUriPhotoTemp2 = data?.data
                bookAdd_iv_detailImgTemp2.setImageURI(detailUriPhotoTemp2)
                bookAdd_btn_removeImgTemp2.visibility = View.VISIBLE
                bookAdd_tv_detailImgTemp2.visibility = View.INVISIBLE
            }
        } else if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                detailUriPhotoTemp3 = data?.data
                bookAdd_iv_detailImgTemp3.setImageURI(detailUriPhotoTemp3)
                bookAdd_btn_removeImgTemp3.visibility = View.VISIBLE
                bookAdd_tv_detailImgTemp3.visibility = View.INVISIBLE
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
        val target = when (type) {
            "main" -> uriPhoto
            "detail1" -> detailUriPhotoTemp1
            "detail2" -> detailUriPhotoTemp2
            else -> detailUriPhotoTemp3
        }

        storageRef.putFile(target!!).addOnSuccessListener {
            if (type == "detail3") {
                if (edit) Toast.makeText(this, "서적 정보가 변경됐어요.", Toast.LENGTH_SHORT).show()
                else Toast.makeText(this, "판매 서적이 등록됐어요.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}
