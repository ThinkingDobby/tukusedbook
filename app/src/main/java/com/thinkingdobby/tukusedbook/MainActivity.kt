package com.thinkingdobby.tukusedbook

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.thinkingdobby.tukusedbook.adapter.BookAdapter
import com.thinkingdobby.tukusedbook.data.Book
import com.thinkingdobby.tukusedbook.data.departments
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val postList = arrayListOf<Book>()

    // 종료 관련
    private val FINISH_INTERVAL_TIME: Long = 2000
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 처음에 사용자의 학과, 학년으로 카테고리 분류되도록 설정
        // 카테고리 변경 시 recreate() - 안되면 finish 후 startActivity로 화면 재실행 - 인텐트로 카테고리 전달 필요요

       val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        main_rv_list.layoutManager = layoutManager
        val bookAdapter = BookAdapter(this, postList)
        main_rv_list.adapter = bookAdapter

        main_icon_profile.setOnClickListener {
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        main_et_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Do Nothing
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                bookAdapter.setCategoryChanging(true)
                bookAdapter.filter.filter(charSequence)
            }

            override fun afterTextChanged(charSequence: Editable?) {
                main_rv_list.scrollToPosition(bookAdapter.itemCount - 1)
            }
        })

        main_btn_department.setOnClickListener {
            bookAdapter.setCategoryChanging(true)
            val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            dlg.setItems(departments) { _, which ->
                main_et_department.setText(departments[which])
                bookAdapter.setDepartment(departments[which])
                main_et_search.setText("")
                bookAdapter.filter.filter("")
                main_rv_list.scrollToPosition(bookAdapter.itemCount - 1)
            }
            dlg.setTitle("학과를 선택하세요.")
            dlg.setPositiveButton("취소", null)
            dlg.show()
        }

        main_btn_grade.setOnClickListener {
            bookAdapter.setCategoryChanging(true)   // 카테고리 변경 시 플래그 true로 설정
            val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            val grades = arrayOf("전체", "1", "2", "3", "4")
            dlg.setItems(grades) { _, which ->
                main_et_grade.setText(grades[which])
                bookAdapter.setGrade(grades[which])
                main_et_search.setText("")
                bookAdapter.filter.filter("")
                main_rv_list.scrollToPosition(bookAdapter.itemCount - 1)
            }
            dlg.setTitle("학년을 선택하세요.")
            dlg.setPositiveButton("취소", null)
            dlg.show()
        }

        try {
            FirebaseDatabase.getInstance().getReference("Book")
                .orderByChild("post_date").addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        snapshot?.let { snapshot ->
                            val post = snapshot.getValue(Book::class.java)
                            post?.let {
                                if (previousChildName == null) {
                                    postList.add(it)
                                    main_rv_list.adapter?.notifyItemInserted(postList.size - 1)
                                } else {
                                    val prevIndex =
                                        postList.map { it.book_id }.indexOf(previousChildName)
                                    postList.add(prevIndex + 1, post)
                                    main_rv_list.adapter?.notifyItemInserted(prevIndex + 1)
                                }
                            }

                            bookAdapter.setDepartment(main_et_department.text.toString())
                            bookAdapter.setGrade(main_et_grade.text.toString())
                            bookAdapter.filter.filter(main_et_search.text.toString())
                        }
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        snapshot?.let { snapshot ->
                            val post = snapshot.getValue(Book::class.java)
                            post?.let {
                                val prevIndex =
                                    postList.map { it.book_id }.indexOf(previousChildName)
                                postList[prevIndex + 1] = post
                                main_rv_list.adapter?.notifyItemChanged(prevIndex + 1)
                            }
                            bookAdapter.setDepartment(main_et_department.text.toString())
                            bookAdapter.setGrade(main_et_grade.text.toString())
                            bookAdapter.filter.filter(main_et_search.text.toString())
                        }
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                        snapshot?.let {
                            val post = snapshot.getValue(Book::class.java)
                            post?.let { post ->
                                val existIndex = postList.map { it.book_id }.indexOf(post.book_id)
                                postList.removeAt(existIndex)
                                main_rv_list.adapter?.notifyItemRemoved(existIndex)
                                if (previousChildName == null) {
                                    postList.add(post)
                                    main_rv_list.adapter?.notifyItemChanged(postList.size - 1)
                                } else {
                                    val prevIndex =
                                        postList.map { it.book_id }.indexOf(previousChildName)
                                    postList.add(prevIndex + 1, post)
                                    main_rv_list.adapter?.notifyItemChanged(prevIndex + 1)
                                }
                            }
                            bookAdapter.setDepartment(main_et_department.text.toString())
                            bookAdapter.setGrade(main_et_grade.text.toString())
                            bookAdapter.filter.filter(main_et_search.text.toString())
                        }
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        snapshot?.let {
                            val post = snapshot.getValue(Book::class.java)
                            post?.let { post ->
                                val existIndex = postList.map { it.book_id }.indexOf(post.book_id)
                                postList.removeAt(existIndex)
                                main_rv_list.adapter?.notifyItemRemoved(existIndex)
                                main_rv_list.adapter?.notifyItemRangeChanged(
                                    existIndex,
                                    postList.size
                                )
                            }

                            bookAdapter.setDepartment(main_et_department.text.toString())
                            bookAdapter.setGrade(main_et_grade.text.toString())
                            bookAdapter.filter.filter(main_et_search.text.toString())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        error?.toException()?.printStackTrace()
                    }
                })

        } catch (e: Exception) {
            Log.d("Load Error", e.toString())
        }

        main_btn_floating.setOnClickListener {
            val intent = Intent(this, BookAddActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    // 화면 터치 시 키보드 내리고 검색바 포커스 해제
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view: View? = currentFocus
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        main_et_search.clearFocus()
        return super.dispatchTouchEvent(ev)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private fun startLoading() {
        val handler = Handler()
        handler.postDelayed({
            // 카테고리 변경 시 로딩 구현
        }, 1000)
    }

    // 뒤로가기 버튼 두 번 누르면 종료료
   override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime: Long = tempTime - backPressedTime
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            finish()
        } else {
            backPressedTime = tempTime
            Toast.makeText(applicationContext, "한 번 더 누르면 종료돼요.", Toast.LENGTH_SHORT).show()
        }
    }
}
