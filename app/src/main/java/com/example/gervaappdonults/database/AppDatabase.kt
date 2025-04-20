package com.example.gervaappdonuts.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gervaappdonuts.dao.*
import com.example.gervaappdonuts.model.*
import com.example.gervaappdonuts.util.Converters

@Database(
    entities = [Usuario::class, Pedido::class, Produto::class, Venda::class],
    version = 7
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun produtoDao(): ProdutoDao
    abstract fun vendaDao(): VendaDao // <- ESSA LINHA É O QUE FALTAVA

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "your_db"
                )
                    .fallbackToDestructiveMigration() // <-- isso aqui
                    .build().also { instance = it }
            }
    }
}