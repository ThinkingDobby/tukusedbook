package com.thinkingdobby.tukusedbook

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.thinkingdobby.tukusedbook.data.ChatModel
import com.thinkingdobby.tukusedbook.data.User
import kotlinx.android.synthetic.main.activity_message.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MessageActivity : AppCompatActivity() {

    private val ref = FirebaseDatabase.getInstance().reference

    // 채팅방 id
    private var chatRoomUid: String? = null
    // 사용자 id
    private var uid = "temp"
    // 상대 id
    private var destinationUid = "temp"

    private var mode = "buy"

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM/dd hh:mm")
        val curTime = dateFormat.format(Date(time)).toString()

        val pref = getSharedPreferences("profile", MODE_PRIVATE)
        uid = pref.getString("user_id", "temp")!!
        // 리스트에서 넘어오면서 상대 id 인텐트로 받아와야
        destinationUid = intent.getStringExtra("destinationUid") ?: "temp"
        mode = intent.getStringExtra("mode") ?: "buy"

        message_btn_send.setOnClickListener {
            Log.d("클릭 시 dest", destinationUid)
            val chatModel = ChatModel()
            chatModel.users.put(uid, true)
            chatModel.users.put(destinationUid!!, false)

            val comment = ChatModel.Comment(uid, message_et_input.text.toString(), curTime)
            if (chatRoomUid == null) {
                message_btn_send.isEnabled = false
                ref.child("Chat").push().setValue(chatModel).addOnSuccessListener {
                    //채팅방 생성
                    checkChatRoom()
                    //메세지 보내기
                    Handler().postDelayed({
                        println(chatRoomUid)
                        ref.child("Chat").child(chatRoomUid.toString())
                            .child("comments").push().setValue(comment)
                        message_et_input.text = null
                    }, 1000L)
                    Log.d("chatUidNull dest", destinationUid)
                }
            } else {
                ref.child("Chat").child(chatRoomUid.toString()).child("comments")
                    .push().setValue(comment)
                message_et_input.text = null
                Log.d("chatUidNotNull dest", destinationUid)
            }
        }
        checkChatRoom()
    }

    private fun checkChatRoom() {
        val value = mode == "buy"
        ref.child("Chat").orderByChild("users/$uid").equalTo(value)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children) {
                        println(item)
                        val chatModel = item.getValue<ChatModel>()
                        if (chatModel?.users!!.containsKey(destinationUid)) {
                            chatRoomUid = item.key
                            message_btn_send.isEnabled = true
                            message_rv_list?.layoutManager = LinearLayoutManager(this@MessageActivity)
                            message_rv_list?.adapter = RecyclerViewAdapter()
                        }
                    }
                }
            })
    }

    inner class RecyclerViewAdapter :
        RecyclerView.Adapter<RecyclerViewAdapter.MessageViewHolder>() {

        private val comments = ArrayList<ChatModel.Comment>()
        private var friend: User? = null

        init {
            ref.child("User").child(destinationUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        friend = snapshot.getValue<User>()
                        message_tv_oppo.text = friend?.name
                        getMessageList()
                    }
                })
        }

        fun getMessageList() {
            ref.child("Chat").child(chatRoomUid.toString()).child("comments")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        comments.clear()
                        for (data in snapshot.children) {
                            val item = data.getValue<ChatModel.Comment>()
                            comments.add(item!!)
                            println(comments)
                        }
                        notifyDataSetChanged()
                        //메세지를 보낼 시 화면을 맨 밑으로 내림
                        message_rv_list?.scrollToPosition(comments.size - 1)
                    }
                })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)

            return MessageViewHolder(view)
        }

        @SuppressLint("RtlHardcoded")
        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            holder.textView_message.textSize = 20F
            holder.textView_message.text = comments[position].message
            holder.textView_time.text = comments[position].time
            if (comments[position].uid.equals(uid)) { // 본인 채팅
                holder.textView_message.setBackgroundResource(R.drawable.rightbubble)
                holder.textView_name.visibility = View.INVISIBLE
                holder.layout_main.gravity = Gravity.RIGHT
            } else { // 상대방 채팅
                holder.textView_name.text = friend?.name
                holder.textView_name.visibility = View.VISIBLE
                holder.textView_message.setBackgroundResource(R.drawable.leftbubble)
                holder.layout_main.gravity = Gravity.LEFT
            }
        }

        inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView_message: TextView = view.findViewById(R.id.messageItem_textView_message)
            val textView_name: TextView = view.findViewById(R.id.messageItem_textview_name)
            val layout_main: LinearLayout = view.findViewById(R.id.messageItem_linearlayout_main)
            val textView_time: TextView = view.findViewById(R.id.messageItem_textView_time)
        }

        override fun getItemCount(): Int {
            return comments.size
        }
    }
}