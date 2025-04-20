package com.example.gervaappdonuts

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gervaappdonuts.database.AppDatabase
import com.example.gervaappdonuts.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CadastroUsuarioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_usuario)

        // Agora é seguro acessar a ActionBar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val nomeEditText = findViewById<EditText>(R.id.editNome)
        val telefoneEditText = findViewById<EditText>(R.id.editTelefone)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarUsuario)

        val db = AppDatabase.getDatabase(this)

        btnSalvar.setOnClickListener {
            val nome = nomeEditText.text.toString()
            val telefone = telefoneEditText.text.toString()

            if (nome.isNotEmpty() && telefone.isNotEmpty()) {
                val usuario = Usuario(nome = nome, telefone = telefone)

                CoroutineScope(Dispatchers.IO).launch {
                    db.usuarioDao().insert(usuario)
                    runOnUiThread {
                        Toast.makeText(this@CadastroUsuarioActivity, "Usuário salvo!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}