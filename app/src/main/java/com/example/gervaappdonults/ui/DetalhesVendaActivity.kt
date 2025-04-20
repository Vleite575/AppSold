package com.example.gervaappdonuts.ui

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gervaappdonuts.R
import com.example.gervaappdonuts.VendaAdapter
import com.example.gervaappdonuts.database.AppDatabase
import com.example.gervaappdonuts.util.ScreenshotUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetalhesVendaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VendaAdapter
    private lateinit var db: AppDatabase
    private lateinit var cliente: String
    private lateinit var layoutPrint: LinearLayout
    private lateinit var btnCompartilhar: Button
    private lateinit var btnPagarTudo: Button
    private var numeroWhatsApp: String = "" // Número extraído

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_venda)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        cliente = intent.getStringExtra("cliente_nome") ?: return
        db = AppDatabase.getDatabase(this)

        recyclerView = findViewById(R.id.recyclerDetalhesVenda)
        recyclerView.layoutManager = LinearLayoutManager(this)
        layoutPrint = findViewById(R.id.layoutDetalhes)
        btnCompartilhar = findViewById(R.id.btnCompartilhar)
        btnPagarTudo = findViewById(R.id.btnPagarTudo)

        adapter = VendaAdapter(
            mutableListOf(),
            onPagarClick = { }
        )

        recyclerView.adapter = adapter

        carregarVendas()

        btnCompartilhar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val vendas = db.vendaDao().getVendasAbertasPorCliente(cliente)
                val total = vendas.sumOf { it.valor }

                // Formatar lista de produtos e datas
                val listaItens = vendas.joinToString("\n") {
                    "- ${it.produto} (${it.data})"
                }

                val usuario = db.usuarioDao().buscarPorNome(cliente)
                numeroWhatsApp = usuario?.telefone?.replace("[^\\d]".toRegex(), "") ?: ""

                val mensagem = buildString {
                    appendLine("Olá, $cliente! Segue o resumo da sua compra:")
                    appendLine()
                    vendas.forEach {
                        appendLine("- ${it.produto} (${it.data}) - R$ %.2f".format(it.valor))
                    }
                    appendLine()
                    appendLine("Total: R$ %.2f".format(total))
                    appendLine("Pix (CPF): 13419423926")
                    appendLine("Nome: Maicon Gervasoni")
                    appendLine("*MANDAR O COMPROVANTE!*")
                }

                withContext(Dispatchers.Main) {
                    ScreenshotUtil.compartilharViaWhatsAppComNumero(
                        context = this@DetalhesVendaActivity,
                        numero = numeroWhatsApp,
                        mensagem = mensagem
                    )
                }
            }
        }

        btnPagarTudo.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.vendaDao().marcarComoPaga(cliente)
                withContext(Dispatchers.Main) {
                    setResult(RESULT_OK) // avisa que houve alteração
                    finish() // fecha a tela e volta para a anterior
                }
            }
        }
    }

    private fun carregarVendas() {
        CoroutineScope(Dispatchers.IO).launch {
            val vendas = db.vendaDao().getVendasAbertasPorCliente(cliente)
            withContext(Dispatchers.Main) {
                adapter.updateList(vendas)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}