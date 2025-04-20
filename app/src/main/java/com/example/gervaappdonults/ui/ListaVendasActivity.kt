package com.example.gervaappdonuts

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gervaappdonuts.database.AppDatabase
import com.example.gervaappdonuts.ui.DetalhesVendaActivity
import kotlinx.coroutines.*

class ListaVendasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ResumoVendaAdapter
    private lateinit var db: AppDatabase
    private lateinit var txtSemCompras: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_vendas)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db = AppDatabase.getDatabase(this)
        recyclerView = findViewById(R.id.recyclerVendas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        txtSemCompras = findViewById(R.id.txtSemCompras)

        adapter = ResumoVendaAdapter(
            listOf(),
            onPagarClick = { resumo ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.vendaDao().marcarComoPaga(resumo.cliente)
                    carregarVendas()
                }
            },
            onDetalhesClick = { resumo ->
                val intent = Intent(this@ListaVendasActivity, DetalhesVendaActivity::class.java)
                intent.putExtra("cliente_nome", resumo.cliente)
                startActivityForResult(intent, 1) // Aqui usamos startActivityForResult
            }
        )

        recyclerView.adapter = adapter
        carregarVendas()
    }

    private fun carregarVendas() {
        CoroutineScope(Dispatchers.IO).launch {
            val lista = db.vendaDao().getResumoVendasAbertas()
            withContext(Dispatchers.Main) {
                adapter.updateList(lista)
                txtSemCompras.visibility = if (lista.isEmpty()) {
                    TextView.VISIBLE
                } else {
                    TextView.GONE
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            carregarVendas()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}