package amat.laundry.ui.screen.bill

import amat.laundry.data.DetailTransaction

data class BillUi(
    val businessName: String = "",
    val businessNumberPhone: String = "",
    val businessAddress: String = "",
    val noteTransaction: String = "",
    val footerNote: String = "",

    val printerName: String = "",
    val printerAddress: String = "",
    val printerCharacterSize: Int = 32,
    val sizeLinePrinter: Int = 42,

    val dateTimeTransaction: String = "",
    val customerName: String = "",
    val cashierName: String = "",
    val invoiceCode: String = "",
    val estimationReadyToPickup: String = "",
    val isFullPayment: Boolean = false,
    val laundryStatusName: String = "",
    val laundryStatusId: Int = -1,

    val listDetailTransaction: List<DetailTransaction> = mutableListOf(),

    val totalPrice: String = "0",
)