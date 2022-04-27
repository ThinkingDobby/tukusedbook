package com.thinkingdobby.tukusedbook

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
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

        message_btn_back.setOnClickListener { finish() }

        message_et_input.addTextChangedListener(object : TextWatcher {
            var prev = ""

            override fun afterTextChanged(p0: Editable?) {
                if (message_et_input.lineCount >= 4) {
                    message_et_input.setText(prev)
                    message_et_input.setSelection(message_et_input.length())
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                prev = p0.toString()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

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
            message_et_input.requestFocus()
        }
        checkChatRoom()
        message_btn_send.setOnTouchListener { p0, p1 ->
            if (p1 != null) {
                if (p1.action == MotionEvent.ACTION_UP) {
                    message_et_input.requestFocus()
                }
            }
            false
        }


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
                            message_rv_list?.layoutManager =
                                LinearLayoutManager(this@MessageActivity)
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
                        message_tv_subject.text = friend?.name
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
            message_tv_subject.text = friend?.name + "님과의 대화"
            holder.tv_message.text = comments[position].message
            holder.tv_time.text = comments[position].time
            if (comments[position].uid.equals(uid)) { // 본인 채팅
                holder.tv_message.setBackgroundResource(R.color.leftbubble)
                holder.layout_main.gravity = Gravity.RIGHT
                holder.ll_time.gravity = Gravity.LEFT
                holder.left_arrow.visibility = View.GONE
                holder.right_arrow.visibility = View.VISIBLE
            } else { // 상대방 채팅
                holder.tv_message.setBackgroundResource(R.color.rightbubble)
                holder.layout_main.gravity = Gravity.LEFT
                holder.ll_time.gravity = Gravity.RIGHT
                holder.left_arrow.visibility = View.VISIBLE
                holder.right_arrow.visibility = View.GONE
            }
        }

        inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tv_message: TextView = view.findViewById(R.id.messageItem_tv_message)
            val layout_main: LinearLayout = view.findViewById(R.id.messageItem_linearlayout_main)
            val tv_time: TextView = view.findViewById(R.id.messageItem_tv_time)
            val ll_time: LinearLayout = view.findViewById(R.id.messageItme_ll_time)
            val right_arrow: ImageView = view.findViewById(R.id.messageItem_iv_rightarrow)
            val left_arrow: ImageView = view.findViewById(R.id.messageItem_iv_leftarrow)
        }

        override fun getItemCount(): Int {
            return comments.size
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val view: View? = currentFocus
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x + -150, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }

        message_et_input.clearFocus()
        return super.dispatchTouchEvent(ev)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}