package com.example.gervaappdonuts.util

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.provider.ContactsContract
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.gervaappdonuts.database.AppDatabase
import com.example.gervaappdonuts.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.preference.PreferenceManager

object ImportadorDeContatos {

    const val CODIGO_PERMISSAO_CONTATO = 100

    // Função para verificar se os contatos já foram importados
    private fun verificarSeJaImportou(context: Context): Boolean {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getBoolean("contatos_importados", false)
    }

    // Função para marcar os contatos como importados
    private fun marcarContatosComoImportados(context: Context) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putBoolean("contatos_importados", true)
        editor.apply()
    }

    // Função para verificar permissão e importar os contatos
    fun verificarPermissaoEImportar(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CODIGO_PERMISSAO_CONTATO
            )
        } else {
            // Verifica se os contatos já foram importados antes de tentar importar
            if (!verificarSeJaImportou(activity)) {
                importarContatos(activity)
            } else {
                Toast.makeText(activity, "Contatos já importados anteriormente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Função para tratar o resultado da permissão
    fun tratarResultadoPermissao(
        activity: Activity,
        requestCode: Int,
        grantResults: IntArray
    ) {
        if (requestCode == CODIGO_PERMISSAO_CONTATO &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // Verifica se os contatos já foram importados antes de tentar importar
            if (!verificarSeJaImportou(activity)) {
                importarContatos(activity)
            } else {
                Toast.makeText(activity, "Contatos já importados anteriormente", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(activity, "Permissão negada para ler contatos", Toast.LENGTH_SHORT).show()
        }
    }

    // Função para importar os contatos
    fun importarContatos(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val contatos = carregarContatos(context)
            val usuarios = contatos.map { (nome, telefone) ->
                Usuario(nome = nome, telefone = telefone)
            }

            val dao = AppDatabase.getDatabase(context).usuarioDao()

            var inseridos = 0
            usuarios.forEach {
                val resultado = dao.insert(it)
                if (resultado != -1L) inseridos++
            }

            // Marca os contatos como importados
            marcarContatosComoImportados(context)

            launch(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "$inseridos contato(s) importado(s) com sucesso!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Função para carregar os contatos
    fun carregarContatos(context: Context): List<Pair<String, String>> {
        val lista = mutableListOf<Pair<String, String>>()
        val contentResolver: ContentResolver = context.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )

        cursor?.use {
            val nomeIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numeroIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val nome = it.getString(nomeIndex)
                val numeroBruto = it.getString(numeroIndex).replace("[^\\d]".toRegex(), "")

                val numeroFormatado = when {
                    numeroBruto.startsWith("55") && numeroBruto.length > 11 -> numeroBruto.substring(2)
                    numeroBruto.length > 11 -> numeroBruto.takeLast(11)
                    else -> numeroBruto
                }

                if (numeroFormatado.length in 10..11) {
                    lista.add(Pair(nome, numeroFormatado))
                }
            }
        }

        return lista
    }
}