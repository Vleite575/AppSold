package com.example.gervaappdonuts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gervaappdonuts.model.Venda

class VendaAdapter(
    private var lista: MutableList<Venda>,
    private val onPagarClick: (Venda) -> Unit
) : RecyclerView.Adapter<VendaAdapter.VendaViewHolder>() {

    inner class VendaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cliente: TextView = view.findViewById(R.id.txtCliente)
        val produto: TextView = view.findViewById(R.id.txtProduto)
        val valor: TextView = view.findViewById(R.id.txtValor)
        val data: TextView = view.findViewById(R.id.txtData)
        val btnPagar: Button = view.findViewById(R.id.btnPagar)
        val txtStatusPago: TextView = view.findViewById(R.id.txtStatusPago) // novo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_venda, parent, false)
        return VendaViewHolder(view)
    }

    override fun onBindViewHolder(holder: VendaViewHolder, position: Int) {
        val venda = lista[position]
        holder.cliente.text = venda.cliente
        holder.produto.text = venda.produto
        holder.valor.text = "R$ %.2f".format(venda.valor)
        holder.data.text = venda.data

        if (venda.pago) {
            holder.btnPagar.visibility = View.GONE
            holder.txtStatusPago.visibility = View.VISIBLE
        } else {
            holder.btnPagar.visibility = View.VISIBLE
            holder.txtStatusPago.visibility = View.GONE
            holder.btnPagar.setOnClickListener { onPagarClick(venda) }
        }
    }

    override fun getItemCount(): Int = lista.size

    fun updateList(novaLista: List<Venda>) {
        lista = novaLista.toMutableList()
        notifyDataSetChanged()
    }
}