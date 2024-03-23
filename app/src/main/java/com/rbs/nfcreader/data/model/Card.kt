package com.rbs.nfcreader.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Card(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "serial_number")
    var serialNumber: String? = null,

    @ColumnInfo(name = "message")
    var message: String? = null
) : Parcelable