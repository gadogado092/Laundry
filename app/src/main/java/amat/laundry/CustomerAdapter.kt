package amat.laundry

//class CustomerAdapter(
//    private val onItemClicked: (CustomerCreditDebit) -> Unit
//) :
//    RecyclerView.Adapter<CustomerAdapter.ViewHolder>() {
//    private var listData = ArrayList<CustomerCreditDebit>()
//
//    fun setData(newList: List<CustomerCreditDebit>?) {
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
//        fun bind(item: CustomerCreditDebit) {
//            with(binding) {
//                textName.text = item.name
//                textNumberPhone.text = item.numberPhone
//            }
//        }
//
//    }
//
//}