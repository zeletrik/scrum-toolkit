package hu.zeletrik.scrumtoolkit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hu.zeletrik.scrumtoolkit.R
import hu.zeletrik.scrumtoolkit.domain.RetroItem

class RetroAdapter(private val items: MutableList<RetroItem>) :
    RecyclerView.Adapter<RetroAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.retro_card, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val timeItem = itemView.findViewById<View>(R.id.retroTime) as TextView
        private val textItem = itemView.findViewById<View>(R.id.retroItemText) as TextView

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(value: RetroItem) {
            timeItem.text = value.time
            textItem.text = value.text
        }

        override fun onClick(view: View) {

        }
    }
}