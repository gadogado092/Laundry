package amat.laundry

//import amat.laundry.databinding.ItemUnitBinding
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import amat.laundry.data.UnitAdapter as UnitAdapterData

//class UnitAdapter(
//    private val onItemClicked: (UnitAdapterData) -> Unit
//) :
//    RecyclerView.Adapter<UnitAdapter.ViewHolder>() {
//    private var listData = ArrayList<UnitAdapterData>()
//
//    fun setData(newList: List<UnitAdapterData>?) {
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
//        val itemsBinding = ItemUnitBinding.inflate(
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
//    class ViewHolder(private val binding: ItemUnitBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(item: UnitAdapterData) {
//            with(binding) {
//                textName.text = item.name
//                textUnitTypeName.text = item.unitTypeName
//            }
//        }
//
//    }
//
//}