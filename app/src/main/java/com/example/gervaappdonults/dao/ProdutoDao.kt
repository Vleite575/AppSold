package com.example.gervaappdonuts.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gervaappdonuts.model.Produto
@Dao
interface ProdutoDao {
    @Insert
    fun insert(produto: Produto)

    @Query("SELECT * FROM produtos")
    fun getAll(): List<Produto>

    @Query("SELECT * FROM produtos WHERE nome = :nome LIMIT 1")
    fun getByName(nome: String): Produto?
}