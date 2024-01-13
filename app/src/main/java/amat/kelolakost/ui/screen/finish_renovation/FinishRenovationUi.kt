package amat.kelolakost.ui.screen.finish_renovation

data class FinishRenovationUi(
    val kostId: String = "",
    val kostName: String = "",
    val unitId: String = "",
    val unitName: String = "",
    val unitTypeName: String = "",
    val unitStatusId: Int = 0,

    val isCash: Boolean = true,

    val finishDate: String = "",
    val noteMaintenance: String = "",
    val costMaintenance: String = "",

)
