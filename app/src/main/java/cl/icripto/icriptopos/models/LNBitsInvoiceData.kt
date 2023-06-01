package cl.icripto.icriptopos.models

data class LNBitsInvoiceData(
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
    val time: Int?,
    val timestamp: Int?,
    val user: String?,
    val webhook: String?,
    val detail : String?
)