package com.zhakki.quizapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Data structure for Category Lookup from https://opentdb.com/api_category.php
 */
data class CategoryResponse(
    @SerializedName("trivia_categories")
    val categories: List<Category>
)

data class Category(
    val id: Int,
    val name: String
)
