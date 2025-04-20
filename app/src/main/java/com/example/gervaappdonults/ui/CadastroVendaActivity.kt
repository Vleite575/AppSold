package com.example.gervaappdonuts

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.gervaappdonuts.database.AppDatabase
import com.example.gervaappdonuts.model.Venda
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activity responsável pelo cadastro de novas vendas.
 * Permite selecionar cliente, produto e quantidade, calculando o total automaticamente.
 */
class CadastroVendaActivity : AppCompatActivity() {

    // Declaração das Views
    private lateinit var autoCompleteCliente: AutoCompleteTextView
    private lateinit var spinnerProduto: Spinner
    private lateinit var spinnerQuantidade: Spinner
    private lateinit var tvTotal: TextView
    private lateinit var btnSalvar: Button

    // Variáveis para cálculo do total
    private var totalVenda = 0.0
    private var precoProduto = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_venda)

        // Configura a Toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Botão de voltar

        // Inicializa as Views
        initViews()

        // Carrega dados assincronamente
        carregarDados()
    }

    /**
     * Inicializa todas as Views do layout
     */
    private fun initViews() {
        autoCompleteCliente = findViewById(R.id.autoCliente)
        spinnerProduto = findViewById(R.id.spinnerProduto)
        spinnerQuantidade = findViewById(R.id.spinnerQuantidade)
        tvTotal = findViewById(R.id.tvTotal)
        btnSalvar = findViewById(R.id.btnSalvarVenda)

        // Configura listeners
        configurarListeners()
    }

    /**
     * Configura os listeners para eventos de seleção
     */
    private fun configurarListeners() {
        // Listener para quando um produto é selecionado
        spinnerProduto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                atualizarPrecoProduto()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Listener para quando a quantidade é alterada
        spinnerQuantidade.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                atualizarTotal()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Listener para o botão de salvar
        btnSalvar.setOnClickListener { salvarVenda() }
    }

    /**
     * Carrega dados de clientes e produtos do banco de dados
     */
    private fun carregarDados() {
        val db = AppDatabase.getDatabase(this)

        CoroutineScope(Dispatchers.IO).launch {
            val usuarios = db.usuarioDao().getAll()
            val produtos = db.produtoDao().getAll()

            runOnUiThread {
                // Preenche o AutoCompleteTextView com nomes de clientes
                configurarAutoCompleteClientes(usuarios.map { it.nome })

                // Preenche o Spinner de produtos
                configurarSpinnerProdutos(produtos.map { it.nome })

                // Preenche o Spinner de quantidades (1-10)
                configurarSpinnerQuantidades()
            }
        }
    }

    /**
     * Configura o AutoCompleteTextView para sugestão de clientes
     */
    private fun configurarAutoCompleteClientes(clientes: List<String>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            clientes
        )
        autoCompleteCliente.setAdapter(adapter)
    }

    /**
     * Configura o Spinner de produtos
     */
    private fun configurarSpinnerProdutos(produtos: List<String>) {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            produtos
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerProduto.adapter = adapter
    }

    /**
     * Configura o Spinner de quantidades (1 a 10)
     */
    private fun configurarSpinnerQuantidades() {
        val quantidades = List(10) { (it + 1).toString() }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            quantidades
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerQuantidade.adapter = adapter
    }

    /**
     * Atualiza o preço do produto selecionado
     */
    private fun atualizarPrecoProduto() {
        val produtoNome = spinnerProduto.selectedItem?.toString() ?: return
        val db = AppDatabase.getDatabase(this)

        CoroutineScope(Dispatchers.IO).launch {
            val produto = db.produtoDao().getByName(produtoNome)
            produto?.let {
                precoProduto = it.preco
                runOnUiThread { atualizarTotal() }
            }
        }
    }

    /**
     * Calcula e atualiza o total da venda
     */
    private fun atualizarTotal() {
        val quantidade = spinnerQuantidade.selectedItem?.toString()?.toIntOrNull() ?: 1
        totalVenda = precoProduto * quantidade
        tvTotal.text = "Total: R$ ${"%.2f".format(totalVenda)}"
    }

    /**
     * Valida e salva a venda no banco de dados
     */
    private fun salvarVenda() {
        val cliente = autoCompleteCliente.text.toString().trim()
        val produto = spinnerProduto.selectedItem?.toString() ?: ""
        val quantidade = spinnerQuantidade.selectedItem?.toString() ?: "1"

        // Validações
        when {
            cliente.isEmpty() -> {
                Toast.makeText(this, "Informe o cliente", Toast.LENGTH_SHORT).show()
                return
            }
            produto.isEmpty() -> {
                Toast.makeText(this, "Selecione um produto", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Cria objeto Venda
        val venda = Venda(
            cliente = cliente,
            produto = "$produto (Qtd: $quantidade)",
            valor = totalVenda,
            data = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            pago = false
        )

        // Salva no banco de dados
        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getDatabase(this@CadastroVendaActivity).vendaDao().insert(venda)
            runOnUiThread {
                Toast.makeText(this@CadastroVendaActivity, "Venda salva com sucesso!", Toast.LENGTH_SHORT).show()
                finish() // Fecha a activity após salvar
            }
        }
    }

    /**
     * Trata o clique no botão de voltar da Toolbar
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}