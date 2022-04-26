package com.thinkingdobby.tukusedbook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.thinkingdobby.tukusedbook.adapter.BookIdAdapter
import kotlinx.android.synthetic.main.activity_interested.*

class InterestedActivity : AppCompatActivity() {

    private val postList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interested)

        val pref = getSharedPreferences("profile", MODE_PRIVATE)
        val id = pref.getString("user_id", "temp")!!

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        interested_rv_list.layoutManager = layoutManager
        val bookAdapter = BookIdAdapter(this, postList)
        interested_rv_list.adapter = bookAdapter

        try {
            FirebaseDatabase.getInstance().getReference("User/$id/interested_books").addChildEventListener(object :
                ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot?.let { snapshot ->
                        val post = snapshot.getValue(String::class.java)
                        post?.let {
                            if (previousChildName == null) {
                                postList.add(it)
                                interested_rv_list.adapter?.notifyItemInserted(postList.size - 1)
                            } else {
                                val prevIndex =
                                    postList.map { it }.indexOf(previousChildName)
                                postList.add(prevIndex + 1, post)
                                interested_rv_list.adapter?.notifyItemInserted(prevIndex + 1)
                            }
                        }

                        if (postList.size != 0) interested_cl_empty.visibility = View.INVISIBLE
                    }
                }

                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    snapshot?.let { snapshot ->
                        val post = snapshot.getValue(String::class.java)
                        post?.let {
                            val prevIndex =
                                postList.map { it }.indexOf(previousChildName)
                            postList[prevIndex + 1] = post
                            interested_rv_list.adapter?.notifyItemChanged(prevIndex + 1)
                        }
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    snapshot?.let {
                        val post = snapshot.getValue(String::class.java)
                        post?.let { post ->
                            val existIndex = postList.map { it }.indexOf(post)
                            postList.removeAt(existIndex)
                            interested_rv_list.adapter?.notifyItemRemoved(existIndex)
                            if (previousChildName == null) {
                                postList.add(post)
                                interested_rv_list.adapter?.notifyItemChanged(postList.size - 1)
                            } else {
                                val prevIndex =
                                    postList.map { it }.indexOf(previousChildName)
                                postList.add(prevIndex + 1, post)
                                interested_rv_list.adapter?.notifyItemChanged(prevIndex + 1)
                            }
                        }
                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    snapshot?.let {
                        val post = snapshot.getValue(String::class.java)
                        post?.let { post ->
                            val existIndex = postList.map { it }.indexOf(post)
                            postList.removeAt(existIndex)
                            interested_rv_list.adapter?.notifyItemRemoved(existIndex)
                            interested_rv_list.adapter?.notifyItemRangeChanged(
                                existIndex,
                                postList.size
                            )
                        }

//                       항목 없을 때
                        if (postList.size != 0) interested_cl_empty.visibility = View.INVISIBLE
                        else interested_cl_empty.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    error?.toException()?.printStackTrace()
                }
            })

//           항목 없을 때
            if (postList.size != 0) interested_cl_empty.visibility = View.INVISIBLE
            else interested_cl_empty.visibility = View.VISIBLE
        } catch (e: Exception) {
            Log.d("Load Error", e.toString())
        }

        interested_btn_back.setOnClickListener {
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}
