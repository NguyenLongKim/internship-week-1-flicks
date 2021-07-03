package com.example.flicks.models

data class PopularMovies(
    val page:Int,
    val results:List<Item>,
    val total_pages:Int,
    val total_results:Int,
)
