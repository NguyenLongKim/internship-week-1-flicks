package com.example.flicks.models


data class Trailers(
    val id: Int,
    val quicktime: List<Any>,
    val youtube: List<Youtube>
)


data class Youtube(
    val name: String,
    val size: String,
    val source: String,
    val type: String
)