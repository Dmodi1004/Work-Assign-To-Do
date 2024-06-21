package com.example.assignto_do.models

import java.util.UUID

data class Works(
    val id: String = UUID.randomUUID().toString(),
    val workId: String? = null,
    val workTitle: String? = null,
    val workDesc: String? = null,
    val bossId: String? = null,
    val workPriority: String? = null,
    val workLastDate: String? = null,
    val workStatus: String? = null,
    var expanded: Boolean = false
)
