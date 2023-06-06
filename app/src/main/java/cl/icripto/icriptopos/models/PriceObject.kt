package cl.icripto.icriptopos.models

data class PriceObject(
    val rate: Double,
    val request: Request,
    val result: Double,
    val timestamp: Long
)

data class Request(
    val amount: Double,
    val from: String,
    val to: String
)

data class BitarooCheckoutData(
    val qr: String,
    val amount: String,
    val invoice: String
)