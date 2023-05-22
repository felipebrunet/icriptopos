package cl.icripto.icriptopos.models

data class InvoiceData(
    val amount: Long?,
    val balance: Int?,
    val completelink: String?,
    val completelinktext: String?,
    val description: String?,
    val id: String?,
    val lnbitswallet: String?,
    val onchainaddress: String?,
    val onchainwallet: String?,
    val paid: Boolean?,
    val payment_hash: String?,
    val payment_request: String?,
    val time: Int?,
    val time_elapsed: Boolean?,
    val time_left: Double?,
    val timestamp: Int?,
    val user: String?,
    val webhook: String?,
    val detail : String?
)