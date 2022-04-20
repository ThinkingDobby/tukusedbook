package com.thinkingdobby.tukusedbook.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.thinkingdobby.tukusedbook.BookDetailActivity
import com.thinkingdobby.tukusedbook.R
import com.thinkingdobby.tukusedbook.data.Book
import com.thinkingdobby.tukusedbook.viewHolder.BookIdViewHolder
import com.thinkingdobby.tukusedbook.viewHolder.BookViewHolder

class BookIdAdapter(val context: Context, private val dataList: MutableList<String>) : RecyclerView.Adapter<BookIdViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookIdViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.book_card, parent, false)
        return BookIdViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: BookIdViewHolder, position: Int) {
        Firebase.database.getReference("Book").child(dataList[position]).get().addOnSuccessListener {
            val book = it.getValue(Book::class.java)!!
            holder.bind(book, context)

//        리스트 각 항목 클릭
            try {
                holder.itemView.setOnClickListener {
                    val intent = Intent(context, BookDetailActivity::class.java)
                    val bundle = Bundle()
                    bundle.putParcelable("selectedBook", book)
                    intent.putExtras(bundle)
                    context.startActivity(intent)
                }
            } catch (e: Exception) {
                Log.d("infoClick", e.toString())
            }
        }
    }
}