@file:Suppress("DEPRECATION")

package com.example.nicefirebasemessanger.messages

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.nicefirebasemessanger.R
import com.example.nicefirebasemessanger.models.ChatMessage
import com.example.nicefirebasemessanger.models.User
import com.example.nicefirebasemessanger.registerlogin.RegisterActivity
import com.example.nicefirebasemessanger.views.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_lateset_messages.*

class LatestMessagesActivity : AppCompatActivity() {



    companion object{
        lateinit var currentUser: User
        lateinit var partnerUser: User
        val latestMessagesMap = HashMap<String, ChatMessage>()
        lateinit var notificationManager: NotificationManager
        lateinit var notificationChannel: NotificationChannel
        lateinit var builder: Notification.Builder
        val channelId = "com.example.nicefirebasemessanger.messages"
        val description = "Notification Test"
    }
    private val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lateset_messages)

        recyclerView_latest_messages.adapter = adapter
        recyclerView_latest_messages.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        verifyUserIsLoggedIn()

        adapter.setOnItemClickListener { item, view ->
            val row = item as LatestMessageRow
            val intent = Intent(view.context, ChatLogActivity::class.java)
            intent.putExtra("user", row.partnerUser)
            startActivity(intent)
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        button_notofocation.setOnClickListener {
            //createNotification()
        }
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }else {
            fetchCurrentUser()
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/${uid}")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)!!
                listenForLatestMessages()
            }
            override fun onCancelled(p0: DatabaseError) {}
        })
    }

    private fun refreshRecyclerView(){
        adapter.clear()
        latestMessagesMap.values.forEach{
            adapter.add(LatestMessageRow(it, this))
            adapter.notifyDataSetChanged()
        }
    }

    private fun listenForLatestMessages() {
        val ref = FirebaseDatabase.getInstance().getReference("latest-messages/${currentUser.uid}")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerView()
                //createNotification(chatMessage)
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerView()
                //createNotification(chatMessage)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getPartnerUser(chatMessage: ChatMessage): User? {
        val partnerId = if (chatMessage.fromId == FirebaseAuth.getInstance().uid){ chatMessage.toId} else{ chatMessage.fromId}
        val ref = FirebaseDatabase.getInstance().getReference("/users/$partnerId")
        val usersArrayList = HashMap<String, User>()
        var i = 0
        var success: Boolean = false
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                i++
                val user: User? = p0.getValue(User::class.java)?: return
                usersArrayList["user$i"] = user!!
                success = true
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
        return null
    }
    private fun createNotification(chatMessage: ChatMessage) {
        val intent = Intent(this, LatestMessagesActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, channelId)
                .setContentTitle("test")
                .setContentText(chatMessage.message)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_foreground))
                .setContentIntent(pendingIntent)
        }else {
            builder = Notification.Builder(this)
                .setContentTitle("test")
                .setContentText(chatMessage.message)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_foreground))
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())
    }


}


