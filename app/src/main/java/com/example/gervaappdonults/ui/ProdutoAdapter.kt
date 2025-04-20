package com.example.gervaappdonuts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gervaappdonuts.model.Produto

class ProdutoAdapter(private var lista: List<Produto>) :
    RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    inner class ProdutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.txtNomeProduto)
        val preco: TextView = itemView.findViewById(R.id.txtPrecoProduto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produto, parent, false)
        return ProdutoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = lista[position]
        holder.nome.text = produto.nome
        holder.preco.text = "R$ %.2f".format(produto.preco)
    }

    override fun getItemCount(): Int = lista.size

    fun updateList(novaLista: List<Produto>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}