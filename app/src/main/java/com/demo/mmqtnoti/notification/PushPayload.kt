package com.demo.mmqtnoti.notification

import com.google.gson.annotations.SerializedName

data class PushPayload(
        @SerializedName("title")
        val title: String,

        @SerializedName("type")
        val type: String,

        @SerializedName("content")
        val content: String,

        @SerializedName("created_date")
        val createdDate: Long,

        @SerializedName("expiredDate")
        val expiredDate: Long)