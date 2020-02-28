package com.example.nicefirebasemessanger.views

import com.example.nicefirebasemessanger.R
import com.example.nicefirebasemessanger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.row_new_message_user.view.*

class UserItem(val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_username_user_row.text = user.userName
        Picasso.get().load(user.profileImageUri).into(viewHolder.itemView.imageView_user_row)
    }

    override fun getLayout(): Int {
        return R.layout.row_new_message_user
    }
}