package com.example.gervaappdonuts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gervaappdonuts.model.Usuario

class UsuarioAdapter(private var lista: List<Usuario>) :
    RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeText: TextView = itemView.findViewById(R.id.txtNome)
        val telefoneText: TextView = itemView.findViewById(R.id.txtTelefone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = lista[position]
        holder.nomeText.text = usuario.nome
        holder.telefoneText.text = formatarTelefone(usuario.telefone)
    }

    override fun getItemCount() = lista.size

    fun updateList(newList: List<Usuario>) {
        lista = newList
        notifyDataSetChanged()
    }

    private fun formatarTelefone(numero: String): String {
        val digitos = numero.filter { it.isDigit() }

        return when {
            digitos.length == 11 ->
                "(${digitos.substring(0, 2)}) ${digitos.substring(2, 7)}-${digitos.substring(7)}"
            digitos.length == 10 ->
                "(${digitos.substring(0, 2)}) ${digitos.substring(2, 6)}-${digitos.substring(6)}"
            else -> numero
        }
    }
}