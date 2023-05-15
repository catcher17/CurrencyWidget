package com.catcher.currencywidget.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.IllegalFormatCodePointException

@Entity(tableName = "currencies")
data class CurrenciesTable(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val code:String,
    val name:String,
    @SerializedName("nameRu")
    val name_ru:String
)