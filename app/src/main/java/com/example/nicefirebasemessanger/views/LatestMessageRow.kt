@file:Suppress("DEPRECATION")

package com.example.nicefirebasemessanger.views


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.nicefirebasemessanger.messages.LatestMessagesActivity
import com.example.nicefirebasemessanger.models.ChatMessage
import com.example.nicefirebasemessanger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.row_latest_meassages.view.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import com.example.nicefirebasemessanger.R
import com.example.nicefirebasemessanger.messages.LatestMessagesActivity.Companion.builder
import com.example.nicefirebasemessanger.messages.LatestMessagesActivity.Companion.channelId
import com.example.nicefirebasemessanger.messages.LatestMessagesActivity.Companion.description
import com.example.nicefirebasemessanger.messages.LatestMessagesActivity.Companion.notificationChannel
import com.example.nicefirebasemessanger.messages.LatestMessagesActivity.Companion.notificationManager
import de.hdodenhof.circleimageview.CircleImageView


class LatestMessageRow(private val chatMessage: ChatMessage, private val context: Context): Item<ViewHolder>(){

    lateinit var partnerUser: User
    var bitmap: Bitmap? = null



    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_message_latest_messages_row.text = chatMessage.message

        val partnerId = if (chatMessage.fromId == FirebaseAuth.getInstance().uid){ chatMessage.toId} else{ chatMessage.fromId}
        val ref = FirebaseDatabase.getInstance().getReference("/users/$partnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                partnerUser = p0.getValue(User::class.java)?: return
                viewHolder.itemView.textView_userName_latest_messages_row.text = partnerUser.userName
                Picasso.get().load(partnerUser.profileImageUri).into(viewHolder.itemView.imageView_latest_message_row)
                if (imageView2Bitmap(viewHolder.itemView.imageView_latest_message_row))
                    createNotification()
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
    private fun imageView2Bitmap(view: CircleImageView?): Boolean {
        if (view != null) {
            bitmap = (view.drawable as BitmapDrawable).bitmap
        }
        if (bitmap != null){
            return true
        }
        return false
    }

    private fun createNotification() {
        val intent = Intent(context, LatestMessagesActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(context, channelId)
                .setContentTitle("test")
                .setContentText(chatMessage.message)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
        }else {
            builder = Notification.Builder(context)
                .setContentTitle("test")
                .setContentText(chatMessage.message)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234, builder.build())
    }

    override fun getLayout(): Int {
        return R.layout.row_latest_meassages
    }
}
