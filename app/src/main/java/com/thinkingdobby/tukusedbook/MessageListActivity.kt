package com.thinkingdobby.tukusedbook

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.thinkingdobby.tukusedbook.data.ChatModel
import com.thinkingdobby.tukusedbook.data.User
import kotlinx.android.synthetic.main.activity_message_list.*
import java.util.*
import java.util.Collections.reverseOrder
import kotlin.collections.ArrayList

class MessageListActivity : AppCompatActivity() {

    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private var mode = "buy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        mode = intent.getStringExtra("mode") ?: "buy"

        messageList_rv_list.layoutManager = LinearLayoutManager(this)
        messageList_rv_list.adapter = RecyclerViewAdapter()
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        private val chatModel = ArrayList<ChatModel>()
        private var nowId: String? = null
        private val destinationUsers: ArrayList<String> = arrayListOf()

        init {
            val pref = getSharedPreferences("profile", MODE_PRIVATE)
            nowId = pref.getString("user_id", "temp")!!

            val value = mode == "buy"
            fireDatabase.child("Chat").orderByChild("users/$nowId").equalTo(value)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        chatModel.clear()
                        for (data in snapshot.children) {
                            chatModel.add(data.getValue<ChatModel>()!!)
                            println(data)
                        }
                        notifyDataSetChanged()
                    }
                })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

            return CustomViewHolder(
                LayoutInflater.from(this@MessageListActivity).inflate(R.layout.item_chat, parent, false)
            )
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView_title: TextView = itemView.findViewById(R.id.chat_textview_title)
            val textView_lastMessage: TextView =
                itemView.findViewById(R.id.chat_item_textview_lastmessage)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            var destinationUid: String? = null
            //채팅방에 있는 유저 모두 체크
            for (user in chatModel[position].users.keys) {
                if (!user.equals(nowId)) {
                    destinationUid = user
                    destinationUsers.add(destinationUid)
                }
            }
            fireDatabase.child("User").child("$destinationUid")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val friend = snapshot.getValue<User>()
                        holder.textView_title.text = friend?.name
                    }
                })
            //메세지 내림차순 정렬 후 마지막 메세지의 키값을 가져옴
            val commentMap = TreeMap<String, ChatModel.Comment>(reverseOrder())
            commentMap.putAll(chatModel[position].comments)
            val lastMessageKey = commentMap.keys.toTypedArray()[0]
            holder.textView_lastMessage.text = chatModel[position].comments[lastMessageKey]?.message

            //채팅창 선책 시 이동
            holder.itemView.setOnClickListener {
                val intent = Intent(this@MessageListActivity, MessageActivity::class.java)
                intent.putExtra("destinationUid", destinationUsers[position])
                intent.putExtra("mode", mode)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return chatModel.size
        }
    }
}
