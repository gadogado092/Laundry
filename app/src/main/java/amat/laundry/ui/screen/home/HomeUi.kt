package amat.laundry.ui.screen.home

data class HomeUi(
    val currentDate: String = "",
    val startDateMonth: String = "",
    val endDateMonth: String = "",

    val totalTransactionToday: String = "",
    val totalTransactionMonth: String = "",

    val totalCashFlowToday: String = "",
    val totalCashFlowMonth: String = ""
)

data class HomeList(
    val listToday: List<HomeItem> = mutableListOf(),
    val listMonth: List<HomeItem> = mutableListOf(),
    val listCashFlowToday: List<HomeItem> = mutableListOf(),
    val listCashFlowMonth: List<HomeItem> = mutableListOf()
)

data class HomeItem(
    val categoryName: String,
    val categoryUnit: String,
    val totalQty: String = "0",
    val totalPrice: String = "0",
)