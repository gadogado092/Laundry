package amat.laundry

import amat.laundry.data.entity.PriceDuration
import amat.laundry.databinding.ItemPriceDurationBinding
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