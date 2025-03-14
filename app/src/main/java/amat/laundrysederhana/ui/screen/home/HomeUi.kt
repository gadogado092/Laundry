package amat.laundrysederhana.ui.screen.home

data class HomeUi(
    val currentDate: String = "",
    val startDateMonth: String = "",
    val endDateMonth: String = ""
)

data class HomeList(
    val listToday: List<HomeItem> = mutableListOf(),
    val listMonth: List<HomeItem> = mutableListOf()
)

data class HomeItem(
    val categoryName: String,
    val categoryUnit: String,
    val totalQty: String = "0",
    val totalPrice: String = "0",
)