package com.example.gervaappdonuts.dao

import androidx.room.*
import com.example.gervaappdonuts.model.Usuario

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios")
    suspend fun getAll(): List<Usuario>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(usuario: Usuario): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(usuarios: List<Usuario>)

    @Delete
    suspend fun delete(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE nome = :nome LIMIT 1")
    fun buscarPorNome(nome: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE telefone = :telefone LIMIT 1")
    fun buscarPorTelefone(telefone: String): Usuario?
}