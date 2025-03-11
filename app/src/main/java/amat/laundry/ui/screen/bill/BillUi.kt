package amat.laundry.ui.screen.bill

import amat.laundry.data.DetailTransaction

data class BillUi(
    val businessName: String = "",
    val businessNumberPhone: String = "",
    val businessAddress: String = "",
    val footerNote: String = "",

    val dateTimeTransaction: String = "",
    val customerName: String = "",
    val invoiceCode: String = "",

    val listDetailTransaction: MutableList<DetailTransaction> = mutableListOf(),

    val totalPrice: String = "0",
)