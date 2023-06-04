package cl.icripto.icriptopos.repositories

import android.annotation.SuppressLint
import com.google.gson.Gson
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*

@SuppressLint("SetTextI18n")
suspend fun payBTCPay(amount: String, currency: String,
                      btcpayStoreId: String, btcpayApiKey: String,
                      btcpayServer: String): String {
    data class CheckoutData(
        val id: String,
        val storeId: String,
        val amount: String,
        val checkoutLink: String,
        val currency: String
    )

    try {
        val client = HttpClient(CIO)
        val response: HttpResponse = client.post {
            url {
                protocol = URLProtocol.HTTPS
                host = btcpayServer.substring(8, btcpayServer.length)
                path("api/v1/stores/$btcpayStoreId/invoices")
            }
            headers {
                append(HttpHeaders.Authorization, "token $btcpayApiKey")
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody("{\"amount\": \"$amount\", \"currency\": \"$currency\"}")
        }
        val link = Gson().fromJson(response.bodyAsText(), CheckoutData::class.java).checkoutLink
        client.close()
        return link

    } catch (error : Exception) {
        return "Error"
    }
}