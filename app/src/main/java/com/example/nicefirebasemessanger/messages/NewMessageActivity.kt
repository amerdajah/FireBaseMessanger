package com.example.nicefirebasemessanger.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nicefirebasemessanger.R
import com.example.nicefirebasemessanger.models.User
import com.example.nicefirebasemessanger.views.UserItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        recycleView_newMessage.adapter = adapter
        title = "Select User"

        fetchUsers()

        adapter.setOnItemClickListener { item, view ->
            val userItem = item as UserItem
            val intent = Intent(view.context, ChatLogActivity::class.java)
            intent.putExtra("user", userItem.user)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null) adapter.add(UserItem(user))
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}


