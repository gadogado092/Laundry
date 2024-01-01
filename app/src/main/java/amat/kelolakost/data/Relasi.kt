package amat.kelolakost.data

data class UnitHome(
    val id: String,
    val name: String,
    val noteMaintenance: String,
    val unitStatusId: Int,

    val kostName: String,

    val tenantName: String,
    val limitCheckOut: String,

    val unitTypeName: String,
    val priceDay: Int,
    val priceWeek: Int,
    val priceMonth: Int,
    val priceThreeMonth: Int,
    val priceSixMonth: Int,
    val priceYear: Int
)

data class TenantHome(
    val id: String,
    val name: String,
    val numberPhone: String,
    val limitCheckOut: String,

    val unitId: String,
    val unitName: String,
    val kostName: String,
)