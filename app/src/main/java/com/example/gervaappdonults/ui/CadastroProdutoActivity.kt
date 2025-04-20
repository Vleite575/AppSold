package com.example.gervaappdonuts

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gervaappdonuts.database.AppDatabase
import com.example.gervaappdonuts.model.Produto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CadastroProdutoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_produto)

        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val nomeProduto = findViewById<EditText>(R.id.editNomeProduto)
        val precoProduto = findViewById<EditText>(R.id.editPrecoProduto)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarProduto)

        val db = AppDatabase.getDatabase(this)

        btnSalvar.setOnClickListener {
            val nome = nomeProduto.text.toString()
            val preco = precoProduto.text.toString().toDoubleOrNull()

            if (nome.isNotEmpty() && preco != null) {
                val produto = Produto(nome = nome, preco = preco)

                CoroutineScope(Dispatchers.IO).launch {
                    db.produtoDao().insert(produto)
                    runOnUiThread {
                        Toast.makeText(this@CadastroProdutoActivity, "Produto salvo!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}