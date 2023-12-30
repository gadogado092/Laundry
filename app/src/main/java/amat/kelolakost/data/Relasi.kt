package amat.kelolakost.data

data class UnitHome(
    val id: String,
    val name: String,
    val noteMaintenance: String,
    val kostName: String,

    val tenantName: String,
    val limitCheckOut: String,

    val unitStatusId: String,

    val unitTypeName: String,
    val priceDay: Int,
    val priceWeek: Int,
    val priceMonth: Int,
    val priceThreeMonth: Int,
    val priceSixMonth: Int,
    val priceYear: Int
)