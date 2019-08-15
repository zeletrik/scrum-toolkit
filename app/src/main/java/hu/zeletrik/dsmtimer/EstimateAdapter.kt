package hu.zeletrik.dsmtimer

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import hu.zeletrik.dsmtimer.presenter.activity.EstimationCardActivity
import kotlinx.android.synthetic.main.estimate_card.view.*
import androidx.cardview.widget.CardView


class EstimateAdapter(private val items: MutableList<String>) : RecyclerView.Adapter<EstimateAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.estimate_card, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val item = itemView.findViewById<View>(R.id.estimateCard) as TextView

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(value: String) {
            item.text = value
        }

        override fun onClick(view: View) {
            val context = itemView.context
            val intent = Intent(context, EstimationCardActivity::class.java)
            intent.putExtra("value", item.text.toString())
            context.startActivity(intent)
        }
    }
}
