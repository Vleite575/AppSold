// model/Pedido.kt
package com.example.gervaappdonuts.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "pedidos")
data class Pedido(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val data: Date,
    val valor: Double,
    val tamanho: String
)
