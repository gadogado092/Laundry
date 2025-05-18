package amat.laundry.ui.screen.home

data class HomeUi(
    val currentDate: String = "",
    val tomorrowDate: String = "",
    val startDateMonth: String = "",
    val endDateMonth: String = "",

    val totalTransactionToday: String = "",
    val totalTransactionMonth: String = "",

    val totalCashFlowToday: String = "",
    val totalCashFlowMonth: String = "",

    val totalBalance: String = "0",
    val totalCashInDay: String = "0",
    val totalCashInMonth: String = "0",
    val totalCashOutDay: String = "0",
    val totalCashOutMonth: String = "0",

    val readyToPickup: String = "0",
    val deadline: String = "0",
    val late: String = "0",
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