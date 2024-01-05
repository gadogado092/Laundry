package amat.kelolakost.ui.screen.check_in

import amat.kelolakost.currencyFormatterStringViewZero
import amat.kelolakost.databinding.ItemPriceDurationBinding
import amat.kelolakost.ui.screen.unit.PriceDuration
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PriceDurationAdapter(
    private val onItemClicked: (PriceDuration) -> Unit
) :
    RecyclerView.Adapter<PriceDurationAdapter.ViewHolder>() {
    private var listData = ArrayList<PriceDuration>()

    fun setData(newList: List<PriceDuration>?) {
        this.listData.clear()
        notifyDataSetChanged()
        if (newList == null) return
        this.listData.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemsBinding = ItemPriceDurationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemsBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listData[position]
        holder.itemView.setOnClickListener {
            onItemClicked(data)
        }
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    class ViewHolder(private val binding: ItemPriceDurationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PriceDuration) {
            with(binding) {
                textPriceDuration.text =
                    "${currencyFormatterStringViewZero(item.price)}/${item.duration}"
            }
        }

    }

}