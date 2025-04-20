// dao/PedidoDao.kt
package com.example.gervaappdonuts.dao

import androidx.room.*
import com.example.gervaappdonuts.model.Pedido

@Dao
interface PedidoDao {
    @Query("SELECT * FROM pedidos")
    suspend fun getAll(): List<Pedido>

    @Query("SELECT * FROM pedidos WHERE usuarioId = :userId")
    suspend fun getByUsuario(userId: Int): List<Pedido>

    @Insert
    suspend fun insert(pedido: Pedido)

    @Delete
    suspend fun delete(pedido: Pedido)
}
