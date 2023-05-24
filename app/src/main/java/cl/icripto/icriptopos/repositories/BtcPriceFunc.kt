package cl.icripto.icriptopos.repositories

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import cl.icripto.icriptopos.apis.BtcPriceInterface
import cl.icripto.icriptopos.models.InvoiceData
import cl.icripto.icriptopos.models.PriceObject
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@SuppressLint("SetTextI18n")
fun getBtcPrice(currency : String, amount : Double,
                lnWalletId : String, onChainWalletId : String,
                merchantName : String, lnbitsServer : String,
                invoiceKey : String,
                context: Context) {
    var satsAmount: Long
    val webHook = ""
    val completeLink = ""
    val callbackMessage = ""
    val limitForOnchain = 10000
    var onChainIdTemp = ""
    val btcPriceUrl = "https://api.yadio.io/convert/${amount}/$currency/"


    val retrofitBuilder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(btcPriceUrl)
        .build()
        .create(BtcPriceInterface::class.java)
    val retrofitData = retrofitBuilder.getData()
    retrofitData.enqueue(object : Callback<PriceObject?> {
        override fun onFailure(call: Call<PriceObject?>, t: Throwable) {
            Log.d("MainActivity", "OnFailure: " + t.message)
        }

        @SuppressLint("SetTextI18n")
        override fun onResponse(call: Call<PriceObject?>, response: Response<PriceObject?>) {
            val responseBody = response.body()
            val btcAmount = (responseBody!!.result)
            satsAmount = (btcAmount * 100000000).toLong()


            val apiService = RestApiService()


            if (satsAmount > limitForOnchain ) {
                onChainIdTemp = onChainWalletId
            }

            val invoiceData = InvoiceData(
                amount = satsAmount,
                balance = null,
                completelink = completeLink,
                completelinktext = callbackMessage,
                description = merchantName,
                id = null,
                lnbitswallet = lnWalletId,
                onchainwallet = onChainIdTemp,
                onchainaddress = null,
                paid = null,
                time = 30,
                timestamp = null,
                user = null,
                webhook = webHook,
                detail = null
            )


            apiService.getInvoice(lnbitsServer, invoiceKey, invoiceData) {
                if (it?.id != null) {
                    startActivity(
                        context,
                        Intent.parseUri("$lnbitsServer${it.id}", 0)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                        null
                    )

                } else {
                    Toast.makeText(context, "Error registering new user", Toast.LENGTH_LONG).show()
                }
            }

        }

    })

}