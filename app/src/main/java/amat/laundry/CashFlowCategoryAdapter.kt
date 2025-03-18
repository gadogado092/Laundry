package amat.laundry

import amat.laundry.data.CashFlowCategory
import amat.laundry.databinding.ItemCashFlowCategoryBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CashFlowCategoryAdapter(
    private val onItemClicked: (CashFlowCategory) -> Unit
) :
    RecyclerView.Adapter<CashFlowCategoryAdapter.ViewHolder>() {
    private var listData = ArrayList<CashFlowCategory>()

    fun setData(newList: List<CashFlowCategory>?) {
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
        val itemsBinding = ItemCashFlowCategoryBinding.inflate(
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

    class ViewHolder(private val binding: ItemCashFlowCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CashFlowCategory) {
            with(binding) {
                textName.text = item.name
            }
        }

    }

}