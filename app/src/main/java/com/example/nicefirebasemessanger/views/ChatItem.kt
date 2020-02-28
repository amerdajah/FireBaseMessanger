package com.example.nicefirebasemessanger.views

import com.example.nicefirebasemessanger.R
import com.example.nicefirebasemessanger.messages.LatestMessagesActivity
import com.example.nicefirebasemessanger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.row_chat_from.view.*
import kotlinx.android.synthetic.main.row_chat_to.view.*

class ChatFromItem(private val message: String): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text = message
        Picasso.get().load(LatestMessagesActivity.currentUser.profileImageUri).into(viewHolder.itemView.imageView_chat_from_row)
    }

    override fun getLayout(): Int {
        return R.layout.row_chat_from
    }
}
class ChatToItem(private val message: String, user: User): Item<ViewHolder>(){

    private val uri = user.profileImageUri
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = message
        Picasso.get().load(uri).into(viewHolder.itemView.imageView_chat_to_row)
    }

    override fun getLayout(): Int {
        return R.layout.row_chat_to
    }
}