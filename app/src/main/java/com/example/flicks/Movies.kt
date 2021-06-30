package com.example.flicks

data class Movies(
    val dates:Dates,
    val page:Int,
    val results:List<Item>,
    val total_pages:Int,
    val total_results: Int
)