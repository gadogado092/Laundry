package amat.kelolakost

import amat.kelolakost.data.UnitType
import amat.kelolakost.databinding.ItemUnitTypeBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class UnitTypeAdapter(
    private val onItemClicked: (UnitType) -> Unit
) :
    RecyclerView.Adapter<UnitTypeAdapter.ViewHolder>() {
    private var listData = ArrayList<UnitType>()

    fun setData(newList: List<UnitType>?) {
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
        val itemsBinding = ItemUnitTypeBinding.inflate(
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

    class ViewHolder(private val binding: ItemUnitTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UnitType) {
            with(binding) {
                textName.text = item.name
                textPriceGuarantee.text = "Uang Deposit/Jaminan ${currencyFormatterStringViewZero(item.priceGuarantee.toString())}"

            }
        }

    }

}