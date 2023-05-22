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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.*

@SuppressLint("SetTextI18n")
fun getBtcPrice(currency : String, amount : Double,
                lnWalletId : String, onChainWalletId : String,
                merchantName : String, lnbitsServer : String,
                invoiceKey : String,
                context: Context) {
    var satsAmount: Long
//    val amount100x = amount * 100
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
//            val priceBTC = 1 / responseBody!!.rate
            val btcAmount = (responseBody!!.result)
            satsAmount = (btcAmount * 100000000).toLong()
//            val satsSentence = "Amount in sats is $satsAmount sats, and in bitcoin is $btcAmount"
//            Toast.makeText(context, satsSentence, Toast.LENGTH_LONG).show()


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
                payment_hash = null,
                payment_request = null,
                time = 30,
                time_elapsed = null,
                time_left = null,
                timestamp = null,
                user = null,
                webhook = webHook,
                detail = null
            )

//            Log.d("acoaco", "amount is ${invoiceData.amount.toString()}")
//            Log.d("acoaco", "lnbitswallet is ${invoiceData.lnbitswallet.toString()}")
//            Log.d("acoaco", "onchainwallet is ${invoiceData.onchainwallet.toString()}")
//            Log.d("acoaco", invoiceData.webhook.toString())
//            Log.d("acoaco", invoiceData.completelink.toString())
//            Log.d("acoaco", invoiceData.completelinktext.toString())
//            Log.d("acoaco", invoiceData.description.toString())

            Log.d("acoacoaco", "lnbitsserver is $lnbitsServer")
            Log.d("acoacoaco", "invoice key is $invoiceKey")
            Log.d("acoacoaco", "invoice data is $invoiceData")

            apiService.getInvoice(lnbitsServer, invoiceKey, invoiceData) {
                if (it?.id != null) {
//                    Toast.makeText(context, "Ok, el id es ${it.id}", Toast.LENGTH_LONG).show()

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