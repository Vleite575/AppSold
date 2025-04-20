package com.example.gervaappdonuts.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gervaappdonuts.model.ResumoVenda
import com.example.gervaappdonuts.model.Venda

@Dao
interface VendaDao {
    @Insert fun insert(venda: Venda)
    @Query("SELECT * FROM vendas") fun getAll(): List<Venda>
    @Query("DELETE FROM vendas WHERE id = :id") fun deleteById(id: Int)
    @Query("SELECT * FROM vendas WHERE pago = 0")
    fun getVendasEmAberto(): List<Venda>

    @Query("SELECT cliente, SUM(valor) as total FROM vendas GROUP BY cliente")
    fun getResumoPorCliente(): List<ResumoVenda>

    // Marcar como paga
    @Query("UPDATE vendas SET pago = 1 WHERE cliente = :cliente")
    fun marcarComoPaga(cliente: String)

    // Listar vendas abertas por cliente
    @Query("SELECT * FROM vendas WHERE cliente = :cliente AND pago = 0")
    suspend fun getVendasAbertasPorCliente(cliente: String): List<Venda>

    // Para obter o resumo apenas de vendas em aberto
    @Query("SELECT cliente, SUM(valor) as total FROM vendas WHERE pago = 0 GROUP BY cliente")
    fun getResumoVendasAbertas(): List<ResumoVenda>

    @Query("SELECT * FROM vendas WHERE cliente LIKE '%' || :cliente || '%'")
    fun getHistoricoCompletoPorCliente(cliente: String): List<Venda>

    @Query("""
    SELECT SUM(valor) FROM vendas 
    WHERE pago = 1 AND strftime('%m-%Y', data) = strftime('%m-%Y', 'now')
""")
    fun getLucroDoMes(): Double?

    @Query("SELECT * FROM vendas WHERE pago = 1")
    fun getVendasPagas(): List<Venda>

    @Query("SELECT * FROM vendas")
    fun getTodasAsVendas(): List<Venda>
}