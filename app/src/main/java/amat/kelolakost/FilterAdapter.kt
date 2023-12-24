package amat.kelolakost

import amat.kelolakost.data.entity.FilterEntity
import amat.kelolakost.databinding.ItemFilterBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class FilterAdapter(
    private val selected: FilterEntity,
    private val onItemClicked: (FilterEntity) -> Unit
) :
    RecyclerView.Adapter<FilterAdapter.ViewHolder>() {
    private var listData = ArrayList<FilterEntity>()

    fun setData(newList: List<FilterEntity>?) {
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
        val itemsBinding = ItemFilterBinding.inflate(
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
        holder.bind(data, selected)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    class ViewHolder(private val binding: ItemFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FilterEntity, selected: FilterEntity) {
            with(binding) {
                textTitle.text = item.title
                if (item.value == selected.value) {
                    textTitle.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.teal_200
                        )
                    )
                } else {
                    textTitle.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.black_font
                        )
                    )
                }
            }
        }

    }

}