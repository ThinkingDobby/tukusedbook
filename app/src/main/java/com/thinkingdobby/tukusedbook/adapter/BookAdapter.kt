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
import com.thinkingdobby.tukusedbook.BookDetailActivity
import com.thinkingdobby.tukusedbook.R
import com.thinkingdobby.tukusedbook.data.Book
import com.thinkingdobby.tukusedbook.viewHolder.BookViewHolder

class BookAdapter(val context: Context, private val dataList: ArrayList<Book>) : RecyclerView.Adapter<BookViewHolder>(), Filterable {
    private var filteredDataList: ArrayList<Book> = dataList

    private var department = "전체"
    private var grade = "전체"

    fun setDepartment(data: String) {
        department = data
    }

    fun setGrade(data: String) {
        grade = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.book_card, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredDataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(filteredDataList[position], position, context)

//        리스트 각 항목 클릭
        try {
            holder.itemView.setOnClickListener {
                val intent = Intent(context, BookDetailActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("selectedBook", filteredDataList[position])
                intent.putExtras(bundle)
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.d("infoClick", e.toString())
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val target = constraint.toString()
                filteredDataList = if (target.isEmpty()) {
                    val filteredList = ArrayList<Book>()
                    for (book in dataList) {
                        if (department == "전체" && grade == "전체") filteredList.add(book)
                        else if (department == "전체" && grade == book.grade.toString()) filteredList.add(book)
                        else if (department == book.department && grade == "전체") filteredList.add(book)
                        else if (book.department == department && book.grade.toString() == grade) filteredList.add(book)
                    }
                    filteredList
                } else {
                    val filteredList = ArrayList<Book>()
                    for (book in dataList) {
                        if (book.title.toLowerCase().contains(target.toLowerCase()) || book.ISBN.contains(target)) {
                            if (department == "전체" && grade == "전체") filteredList.add(book)
                            else if (department == "전체" && grade == book.grade.toString()) filteredList.add(book)
                            else if (department == book.department && grade == "전체") filteredList.add(book)
                            else if (book.department == department && book.grade.toString() == grade) filteredList.add(book)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredDataList
                return filterResults
            }
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                filteredDataList  = results.values as ArrayList<Book>
                notifyDataSetChanged()
            }
        }
    }
}