package hu.zeletrik.scrumtoolkit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.zeletrik.scrumtoolkit.R
import kotlinx.android.synthetic.main.row_item.view.*

class MemberAdapter(private val items: MutableList<String>) : RecyclerView.Adapter<MemberAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addItem(name: String) {
        items.add(name)
        notifyItemInserted(items.size)
    }

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    class VH(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)) {

        fun bind(name: String) = with(itemView) {
            rowName.text = name
        }
    }
}