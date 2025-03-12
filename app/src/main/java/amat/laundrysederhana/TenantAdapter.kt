package amat.laundrysederhana

//class TenantAdapter(
//    private val onItemClicked: (Tenant) -> Unit
//) :
//    RecyclerView.Adapter<TenantAdapter.ViewHolder>() {
//    private var listData = ArrayList<Tenant>()
//
//    fun setData(newList: List<Tenant>?) {
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
//        val itemsBinding = ItemTenantBinding.inflate(
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
//    class ViewHolder(private val binding: ItemTenantBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(item: Tenant) {
//            with(binding) {
//                textName.text = item.name
//                textNumberPhone.text = item.numberPhone
//            }
//        }
//
//    }
//
//}