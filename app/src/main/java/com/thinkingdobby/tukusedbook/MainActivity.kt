package com.thinkingdobby.tukusedbook

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.thinkingdobby.tukusedbook.adapter.BookAdapter
import com.thinkingdobby.tukusedbook.data.Book
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val postList = mutableListOf<Book>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        main_rv_list.layoutManager = layoutManager
        main_rv_list.adapter = BookAdapter(this, postList)

        try {
            FirebaseDatabase.getInstance().reference
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
    }
}
