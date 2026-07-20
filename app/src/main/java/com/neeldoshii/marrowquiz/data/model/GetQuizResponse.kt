package com.neeldoshii.marrowquiz.data.model

import com.google.gson.annotations.SerializedName

data class GetQuizResponse(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("question")
    val question: String? = null,
    @SerializedName("options")
    val options: List<String>? = null,
    @SerializedName("correctOptionIndex")
    val correctOptionIndex: Int? = null,
)
