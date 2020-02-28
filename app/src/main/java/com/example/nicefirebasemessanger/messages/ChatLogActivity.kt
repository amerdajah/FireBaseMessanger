package com.example.nicefirebasemessanger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nicefirebasemessanger.R
import com.example.nicefirebasemessanger.models.ChatMessage
import com.example.nicefirebasemessanger.models.User
import com.example.nicefirebasemessanger.views.ChatFromItem
import com.example.nicefirebasemessanger.views.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()
    private lateinit var toUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        toUser = intent.getParcelableExtra("user")!!
        title = toUser.userName
        recyclerView_chat_log.adapter = adapter

        listenForMessages()

        button_send_chat_log.setOnClickListener {
            performSendMessage()
        }
    }

    private fun listenForMessages() {
        val fromId = LatestMessagesActivity.currentUser.uid
        val toId = toUser.uid
        var chatMessage: ChatMessage?
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null){
                    if (chatMessage!!.fromId == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatFromItem(chatMessage!!.message))
                    }else {
                        adapter.add(ChatToItem(chatMessage!!.message, toUser))
                    }
                }
                recyclerView_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
    }

    private fun performSendMessage() {
        val message = editText_message_chat_log.text.toString()
        editText_message_chat_log.text.clear()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser.uid
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        val chatMessage = ChatMessage(reference.key!!, message, fromId!!, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                //Toast.makeText(this, "success", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                //Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
        toReference.setValue(chatMessage)
        latestMessageRef.setValue(chatMessage)
        latestMessageToRef.setValue(chatMessage)
    }
}



