package amat.laundrysederhana.ui.screen.bill

import amat.laundrysederhana.data.DetailTransaction

data class BillUi(
    val businessName: String = "",
    val businessNumberPhone: String = "",
    val businessAddress: String = "",
    val noteTransaction: String = "",
    val footerNote: String = "",

    val printerName:String = "",
    val printerAddress:String = "",

    val dateTimeTransaction: String = "",
    val customerName: String = "",
    val invoiceCode: String = "",
    val isFullPayment: Boolean = false,

    val listDetailTransaction: List<DetailTransaction> = mutableListOf(),

    val totalPrice: String = "0",
)