package com.thinkingdobby.tukusedbook

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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

        main_et_search.addTextChangedListener (object: TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //Do Nothing
            }

            override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
                bookAdapter.filter.filter(charSequence)
            }

            override fun afterTextChanged(charSequence: Editable?) {
                //Do Nothing
            }
        })

        main_btn_department.setOnClickListener {
            val dlg = AlertDialog.Builder(this, R.style.AlertDialogStyle)
            dlg.setItems(departments) { _, which ->
                main_et_department.setText(departments[which])
                bookAdapter.setDepartment(departments[which])
                main_et_search.setText("")
                bookAdapter.filter.filter("")
                main_rv_list.scrollToPosition(bookAdapter.itemCount - 1)
            }
            dlg.setTitle("학과를 선택하세요.")
            dlg.show()
        }

        main_btn_grade.setOnClickListener {
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
//                            항목 없을 때
//                            if (postList.size != 0) findPet_tv_empty.visibility = View.INVISIBLE
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
                                main_rv_list.adapter?.notifyItemRangeChanged(existIndex, postList.size)
                            }
//                            항목 없을 때
//                            if (postList.size != 0) findPet_tv_empty.visibility = View.INVISIBLE
//                            else findPet_tv_empty.visibility = View.VISIBLE
                            bookAdapter.setDepartment(main_et_department.text.toString())
                            bookAdapter.setGrade(main_et_grade.text.toString())
                            bookAdapter.filter.filter(main_et_search.text.toString())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        error?.toException()?.printStackTrace()
                    }
                })

//            항목 없을 때
//            if (postList.size == 0) findPet_tv_empty.visibility = View.VISIBLE
//            else findPet_tv_empty.visibility = View.INVISIBLE
        } catch (e: Exception) {
            Log.d("Load Error", e.toString())
        }

        main_btn_floating.setOnClickListener {
            val intent = Intent(this, BookAddActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}
