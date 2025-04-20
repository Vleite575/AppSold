package com.example.gervaappdonuts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gervaappdonuts.databinding.ActivityMainBinding
import com.example.gervaappdonuts.ui.*
import com.example.gervaappdonuts.util.ContatoObserver
import com.example.gervaappdonuts.util.ImportadorDeContatos
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.gervaappdonuts.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var observer: ContatoObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verificarPermissaoContato()

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_cadastrar_venda -> {
                    startActivity(Intent(this, CadastroVendaActivity::class.java))
                    true
                }
                R.id.menu_usuarios_produtos -> {
                    mostrarDialogoCadastro()
                    true
                }
                R.id.menu_vendas_abertas -> {
                    startActivity(Intent(this, ListaVendasActivity::class.java))
                    true
                }
                R.id.menu_mais -> {
                    mostrarDialogoMais()
                    true
                }
                else -> false
            }
        }
    }

    private fun verificarPermissaoContato() {
        val permission = Manifest.permission.READ_CONTACTS

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            iniciarObservacaoDeContatos()
            importarSomenteSeNecessario()
        } else if (!jaSolicitouPermissaoAntes()) {
            marcarPermissaoComoSolicitada()
            ActivityCompat.requestPermissions(this, arrayOf(permission), 100)
        }
    }

    private fun iniciarObservacaoDeContatos() {
        observer = ContatoObserver(this)
        observer.registrar()
    }

    private fun importarSomenteSeNecessario() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getDatabase(this@MainActivity).usuarioDao()
            val existe = dao.getAll().isNotEmpty()

            if (!existe) {
                ImportadorDeContatos.importarContatos(this@MainActivity)
            }
        }
    }

    private fun jaSolicitouPermissaoAntes(): Boolean {
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        return prefs.getBoolean("permissao_contato_solicitada", false)
    }

    private fun marcarPermissaoComoSolicitada() {
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("permissao_contato_solicitada", true).apply()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            iniciarObservacaoDeContatos()
            importarSomenteSeNecessario()
        } else {
            Toast.makeText(this, "Permissão negada para ler contatos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::observer.isInitialized) {
            observer.remover()
        }
    }

    private fun mostrarDialogoCadastro() {
        val opcoes = arrayOf("Cadastrar Usuário", "Cadastrar Produto")

        MaterialAlertDialogBuilder(this)
            .setTitle("Escolha uma opção")
            .setItems(opcoes) { _, which ->
                when (which) {
                    0 -> startActivity(Intent(this, CadastroUsuarioActivity::class.java))
                    1 -> startActivity(Intent(this, CadastroProdutoActivity::class.java))
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoMais() {
        val opcoes = arrayOf("Lucro do Mês", "Histórico de Compras")

        MaterialAlertDialogBuilder(this)
            .setTitle("Escolha uma opção")
            .setItems(opcoes) { _, which ->
                when (which) {
                    0 -> startActivity(Intent(this, LucroDoMesActivity::class.java))
                    1 -> startActivity(Intent(this, HistoricoComprasActivity::class.java))
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}