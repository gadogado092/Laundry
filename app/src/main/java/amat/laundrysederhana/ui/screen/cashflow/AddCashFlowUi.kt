package amat.laundrysederhana.ui.screen.cashflow

data class AddCashFlowUi(
    val cashFlowId: String = "",
    val cashFlowCategoryId: String = "",
    val cashFlowCategoryName: String = "",
    val createAt: String = "",
    val note: String = "",
    val nominal: String = "",
    val qty: String = "",
    val unit: String = ""
)
