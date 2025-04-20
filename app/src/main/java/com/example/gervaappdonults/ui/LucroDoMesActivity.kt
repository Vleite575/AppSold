package com.example.gervaappdonuts.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Color

class LucroDoMesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VendaAdapter
    private lateinit var txtTotal: TextView
    private lateinit var txtRecebido: TextView
    private lateinit var txtAReceber: TextView
    private lateinit var btnInicio: Button
    private lateinit var btnFim: Button

    private var dataInicio: String? = null
    private var dataFim: String? = null
    private val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lucro_do_mes)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recyclerLucro)
        recyclerView.layoutManager = LinearLayoutManager(this)
        txtTotal = findViewById(R.id.txtTotalVendas)
        txtRecebido = findViewById(R.id.txtTotalRecebido)
        txtAReceber = findViewById(R.id.txtTotalAReceber)
        btnInicio = findViewById(R.id.btnDataInicio)
        btnFim = findViewById(R.id.btnDataFim)

        adapter = VendaAdapter(mutableListOf()) {}
        recyclerView.adapter = adapter

        btnInicio.setOnClickListener {
            mostrarDatePicker { data ->
                dataInicio = data
                btnInicio.text = "Início: $data"
                carregarDados()
            }
        }

        btnFim.setOnClickListener {
            mostrarDatePicker { data ->
                dataFim = data
                btnFim.text = "Fim: $data"
                carregarDados()
            }
        }

        carregarDados()
    }

    private fun carregarDados() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getDatabase(this@LucroDoMesActivity).vendaDao()
            val vendas = dao.getTodasAsVendas()

            val filtradas = vendas.filter { venda ->
                val dataVenda = try {
                    formatoData.parse(venda.data)
                } catch (e: Exception) { null }

                val inicio = dataInicio?.let { formatoData.parse(it) }
                val fim = dataFim?.let { formatoData.parse(it) }

                val depoisDoInicio = (inicio?.let {
                    val dataVendaDepois = dataVenda?.after(it) ?: false
                    val dataVendaIgual = dataVenda == it
                    dataVendaDepois || dataVendaIgual
                } ?: true)

                val antesDoFim = (fim?.let {
                    val dataVendaAntes = dataVenda?.before(it) ?: false
                    val dataVendaIgual = dataVenda == it
                    dataVendaAntes || dataVendaIgual
                } ?: true)

                depoisDoInicio && antesDoFim
            }

            val total = filtradas.sumOf { it.valor }
            val totalRecebido = filtradas.filter { it.pago }.sumOf { it.valor }
            val totalAReceber = filtradas.filter { !it.pago }.sumOf { it.valor }

            withContext(Dispatchers.Main) {
                txtTotal.text = "Total: R$ %.2f".format(total)
                txtRecebido.text = "Recebido: R$ %.2f".format(totalRecebido)
                txtAReceber.text = "A Receber: R$ %.2f".format(totalAReceber)
                adapter.updateList(filtradas)
            }
        }
    }

    private fun mostrarDatePicker(callback: (String) -> Unit) {
        val calendario = Calendar.getInstance()
        val ano = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            R.style.CustomDatePickerDialogTheme,
            { _, year, month, dayOfMonth ->
                val dataFormatada = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                callback(dataFormatada) // 👉 Isso estava faltando!
            },
            ano, mes, dia
        )

        datePicker.show()

        // Espera o dialog estar pronto para acessar os botões
        datePicker.window?.decorView?.post {
            datePicker.getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(Color.WHITE)
            datePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(Color.WHITE)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}