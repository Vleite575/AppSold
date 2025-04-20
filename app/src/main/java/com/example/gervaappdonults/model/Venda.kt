package com.example.gervaappdonuts.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vendas")
data class Venda(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var cliente: String,
    var produto: String,
    var valor: Double,
    var data: String,
    var pago: Boolean = false // TIPO BOOLEAN, NÃO STRING
)