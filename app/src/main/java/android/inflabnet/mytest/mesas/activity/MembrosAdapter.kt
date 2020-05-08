package android.inflabnet.mytest.mesas.activity

import android.inflabnet.mytest.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.membro_mesa.view.*

class MembrosAdapter(val toMembros: MutableList<String>, private val itemClick: (String) -> Unit) : RecyclerView.Adapter<MembrosAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.membro_mesa,parent,false)
        return ViewHolder(view,itemClick)
    }

    override fun getItemCount(): Int {
        return toMembros.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindForecast(toMembros[position])
    }

    class ViewHolder(view: View, val itemClick: (String) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bindForecast(membro: String) {
            with(membro) {
                itemView.txtMembro.text = membro
                itemView.setOnClickListener { itemClick(this) }
            }
        }
    }
}
