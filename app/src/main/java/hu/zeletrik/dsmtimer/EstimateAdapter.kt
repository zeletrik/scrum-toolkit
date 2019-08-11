package hu.zeletrik.dsmtimer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.estimate_card.view.*

class EstimateAdapter(private val items: MutableList<String>) : RecyclerView.Adapter<EstimateAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addItem(value: String) {
        items.add(value)
        notifyItemInserted(items.size)
    }
    
    class VH(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.estimate_card, parent, false)) {

        fun bind(value: String) = with(itemView) {
            estimateCard.text = value
        }
    }
}