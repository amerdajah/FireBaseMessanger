package com.example.nicefirebasemessanger.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(val uid: String, val userName: String, val profileImageUri: String): Parcelable {
    constructor(): this("", "", "")
}