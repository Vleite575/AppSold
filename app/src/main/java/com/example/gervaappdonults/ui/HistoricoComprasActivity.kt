// HistoricoComprasActivity.kt (adaptado com getHistoricoCompletoPorCliente)
package com.example.gervaappdonuts.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gervaappdonuts.R
import com.example.gervaappdonuts.VendaAdapter
import com.example.gervaappdonuts.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoricoComprasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VendaAdapter
    private lateinit var edtCliente: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico_compras)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recyclerHistorico)
        recyclerView.layoutManager = LinearLayoutManager(this)
        edtCliente = findViewById(R.id.edtFiltroCliente)

        adapter = VendaAdapter(mutableListOf()) { /* vazio, já que não vai pagar aqui */ }
        recyclerView.adapter = adapter

        edtCliente.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val cliente = s.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    val vendas = AppDatabase.getDatabase(this@HistoricoComprasActivity)
                        .vendaDao().getHistoricoCompletoPorCliente(cliente)
                    withContext(Dispatchers.Main) {
                        adapter.updateList(vendas)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
