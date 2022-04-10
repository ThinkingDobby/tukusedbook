package com.thinkingdobby.tukusedbook.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.thinkingdobby.tukusedbook.R
import com.thinkingdobby.tukusedbook.data.Book
import com.thinkingdobby.tukusedbook.viewHolder.BookViewHolder

class BookAdapter(val context: Context, private val dataList: MutableList<Book>) : RecyclerView.Adapter<BookViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.book_card, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(dataList[position], position, context)
//        리스트 각 항목 클릭
//        try {
//            holder.itemView.pet_btn_info.setOnClickListener {
//                val intent = Intent(context, BookDetailActivity::class.java)
//                val bundle = Bundle()
//                bundle.putParcelable("selectedBook", dataList[position])
//                intent.putExtras(bundle)
//                context.startActivity(intent)
//            }
//        } catch (e: Exception) {
//            Log.d("infoClick", e.toString())
//        }
    }
}