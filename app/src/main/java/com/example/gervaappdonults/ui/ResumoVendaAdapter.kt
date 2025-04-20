package com.example.gervaappdonuts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gervaappdonuts.model.ResumoVenda
import android.widget.Button

class ResumoVendaAdapter(
    private var lista: List<ResumoVenda>,
    private val onPagarClick: (ResumoVenda) -> Unit,
    private val onDetalhesClick: (ResumoVenda) -> Unit
) : RecyclerView.Adapter<ResumoVendaAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome = view.findViewById<TextView>(R.id.txtCliente)
        val total = view.findViewById<TextView>(R.id.txtTotal)
        val btnPagar = view.findViewById<Button>(R.id.btnPagar)
        val btnDetalhes = view.findViewById<Button>(R.id.btnDetalhes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resumo_venda, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.nome.text = item.cliente
        holder.total.text = "R$ %.2f".format(item.total)

        holder.btnPagar.setOnClickListener { onPagarClick(item) }
        holder.btnDetalhes.setOnClickListener { onDetalhesClick(item) }
    }

    fun updateList(novaLista: List<ResumoVenda>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}