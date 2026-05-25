package com.example.jeva.domain.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    var balance: Double = 1500000.0,
    val transactions: Map<String, TransactionModel> = emptyMap()
) {
    // Constructor vacío requerido por Firebase
    constructor() : this("", "", "", 1500000.0, emptyMap())
}

@IgnoreExtraProperties
data class TransactionModel(
    val id: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    @field:JvmField val isExpense: Boolean = true
) {
    // Constructor vacío requerido por Firebase
    constructor() : this("", "", 0.0, "", true)
}