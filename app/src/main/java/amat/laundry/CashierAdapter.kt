package amat.laundry

import amat.laundry.data.Cashier
import amat.laundry.databinding.ItemCashierBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CashierAdapter(
    private val onItemClicked: (Cashier) -> Unit
) :
    RecyclerView.Adapter<CashierAdapter.ViewHolder>() {
    private var listData = ArrayList<Cashier>()

    fun setData(newList: List<Cashier>?) {
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
        val itemsBinding = ItemCashierBinding.inflate(
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

    class ViewHolder(private val binding: ItemCashierBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cashier) {
            with(binding) {
                textName.text = item.name
                textNote.text = item.note.ifEmpty { "-" }
            }
        }

    }

}