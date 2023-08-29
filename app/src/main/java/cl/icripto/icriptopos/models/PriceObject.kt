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

data class BudaCheckoutData(
    val invoice: BudaResponseObject
)
data class BudaResponseObject (
    val id: String,
    val encoded_payment_request: String,
    val currency: String,
    val memo: String,
    val amount: Int,
    val expiration_time: Int,
    val state: String,
    val price: String?

)