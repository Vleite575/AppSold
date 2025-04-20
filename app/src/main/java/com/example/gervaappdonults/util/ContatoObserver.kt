package com.example.gervaappdonuts.util

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import com.example.gervaappdonuts.database.AppDatabase
import com.example.gervaappdonuts.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContatoObserver(private val context: Context) : ContentObserver(Handler(Looper.getMainLooper())) {

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)

        CoroutineScope(Dispatchers.IO).launch {
            val novosContatos = ImportadorDeContatos.carregarContatos(context)
            val dao = AppDatabase.getDatabase(context).usuarioDao()

            novosContatos.forEach { (nome, telefone) ->
                dao.insert(Usuario(nome = nome, telefone = telefone))
            }
        }
    }

    fun registrar() {
        context.contentResolver.registerContentObserver(
            ContactsContract.Contacts.CONTENT_URI,
            true,
            this
        )
    }

    fun remover() {
        context.contentResolver.unregisterContentObserver(this)
    }
}