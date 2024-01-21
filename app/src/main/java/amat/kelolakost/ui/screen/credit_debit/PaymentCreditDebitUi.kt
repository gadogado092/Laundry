package amat.kelolakost.ui.screen.credit_debit

data class PaymentCreditDebitUi(
    val isFullPayment: Boolean = true,
    val isCash: Boolean = true,

    val downPayment: String = "0",
    val remaining: String = "0",

    val totalPayment: String = "0",

    val createAt: String = "",
    val dueDate: String = "",
    val oldDueDate: String = "",

    val creditDebitId: String = "",
    val creditDebitName: String = "",
    val note: String = "",
    val remainingDebt: Int = 0,
    val status: Int = -1
)
