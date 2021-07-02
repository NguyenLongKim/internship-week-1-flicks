package com.example.flicks.listeners

import com.example.flicks.models.Item

interface ItemClickListener {
    fun onClick(item: Item)
}