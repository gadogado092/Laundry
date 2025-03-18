package amat.laundry

import amat.laundry.data.Category
import amat.laundry.databinding.ItemTenantBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val onItemClicked: (Category) -> Unit
) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    private var listData = ArrayList<Category>()

    fun setData(newList: List<Category>?) {
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
        val itemsBinding = ItemTenantBinding.inflate(
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

    class ViewHolder(private val binding: ItemTenantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category) {
            with(binding) {
                textName.text = item.name
                textNumberPhone.text = item.unit
            }
        }

    }

}