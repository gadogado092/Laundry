package amat.kelolakost.ui.screen.unit

data class UnitUi(
    val id: String = "",
    val name: String = "",
    val note: String = "",
    val kostId: String = "",
    val kostName: String = "",
    val unitTypeId: String = "",
    val unitTypeName: String = "",
    val noteMaintenance: String = "",
    val unitStatusId: Int = 2,
    val tenantId: String = "",
    val isDelete: Boolean = false
)