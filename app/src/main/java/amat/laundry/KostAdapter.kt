package amat.laundry

import amat.laundry.databinding.ItemKostBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

//class KostAdapter(
//    private val onItemClicked: (Kost) -> Unit
//) :
//    RecyclerView.Adapter<KostAdapter.ViewHolder>() {
//    private var listData = ArrayList<Kost>()
//
//    fun setData(newList: List<Kost>?) {
//        this.listData.clear()
//        notifyDataSetChanged()
//        if (newList == null) return
//        this.listData.addAll(newList)
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): ViewHolder {
//        val itemsBinding = ItemKostBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return ViewHolder(itemsBinding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val data = listData[position]
//        holder.itemView.setOnClickListener {
//            onItemClicked(data)
//        }
//        holder.bind(data)
//    }
//
//    override fun getItemCount(): Int {
//        return listData.size
//    }
//
//    class ViewHolder(private val binding: ItemKostBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(item: Kost) {
//            with(binding) {
//                textName.text = item.name
//                textAddress.text = item.address
//            }
//        }
//
//    }
//
//}