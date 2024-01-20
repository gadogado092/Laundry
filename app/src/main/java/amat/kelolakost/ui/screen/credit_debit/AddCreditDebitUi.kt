package amat.kelolakost.ui.screen.credit_debit

data class AddCreditDebitUi(
    val customerCreditDebitId: String = "",
    val customerCreditDebitName: String = "",
    val nominal: String = "",
    val note: String = "",
    val createAt: String = "",
    val dueDate: String = "",
    val isCredit: Boolean = true,
    val isCash: Boolean = true
)
