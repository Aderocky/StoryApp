package com.example.storyapp_ade

import com.example.storyapp_ade.api.response.ListStoryItem


object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "photoUrl + $i",
                "createdAt + $i",
                "name + $i",
                "description + $i",
                106.64356,
                -6.1335033,
            )
            items.add(story)
        }
        return items
    }
}
