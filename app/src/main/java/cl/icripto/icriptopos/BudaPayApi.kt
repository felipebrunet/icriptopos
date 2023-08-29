package cl.icripto.icriptopos

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import cl.icripto.icriptopos.models.BudaCheckoutData
import cl.icripto.icriptopos.models.PriceObject
import cl.icripto.icriptopos.repositories.Hmac
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*
import kotlin.collections.hashMapOf
import kotlin.collections.set


class BudaPayApi : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buda_pay_api)

        val textView: TextView = findViewById(R.id.linkGH)
        textView.movementMethod = LinkMovementMethod.getInstance()

        val defaultamountFiat = "150"
        val defaultCurrency = "CLP"
        val defaultMerchantName = ""
        val defaultBudaApiKey = ""
        val defaultBudaApiSecret = ""


        val sharedPreferences: SharedPreferences =
            getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
        val amountFiat = sharedPreferences.getString("PRICE", defaultamountFiat)
        val merchantName = sharedPreferences.getString("MERCHANTNAME", defaultMerchantName).toString()
        val budaApiKey = sharedPreferences.getString("BUDAAPIKEY", defaultBudaApiKey).toString()
        val budaApiSecret = sharedPreferences.getString("BUDAAPISECRET", defaultBudaApiSecret).toString()
        val currency = sharedPreferences.getString("LOCALCURRENCY", defaultCurrency).toString()
        val urlBuda = "https://www.buda.com"
        val urlBudaCheck = "https://realtime.buda.com/sub?channel=lightninginvoices%40"
        val pathBuda = "/api/v2/lightning_network_invoices"
        val btcPriceUrl = "https://api.yadio.io/convert/$amountFiat/$currency/BTC"


        findViewById<TextView>(R.id.MonedaPagoValor).text = currency
        findViewById<TextView>(R.id.MontoPagoValor).text = "$${amountFiat}"
        findViewById<TextView>(R.id.MotivoPagoValor).text = merchantName


        val priceClient = HttpClient(CIO)
        val corr1 = CoroutineScope(Dispatchers.IO)
        corr1.launch {
            val responsePrice: HttpResponse = priceClient.get(btcPriceUrl)
            val btcValue = Gson().fromJson(responsePrice.bodyAsText(), PriceObject::class.java).result
            val satsValue = (btcValue*100000000).toInt()
            val btcValueDecimal = String.format(Locale.ENGLISH, "%.8f", btcValue)
            Log.d("acoacoaco", "satsvalue es $satsValue")
            val nonce = (System.currentTimeMillis()*1000).toString()
            val data = "{\"amount_satoshis\": \"$satsValue\", \"currency\": \"BTC\", \"memo\": \"Cobro $merchantName\"}"
            val encodedData = Base64.getEncoder().encodeToString(data.toByteArray())
            val mensaje = "POST $pathBuda $encodedData $nonce"
            val signature = Hmac.digest(mensaje, budaApiSecret)

            val budaClient = HttpClient(CIO)
            val responseBudaPost: HttpResponse = budaClient.post(urlBuda + pathBuda) {
                header("X-SBTC-APIKEY", budaApiKey)
                header("X-SBTC-NONCE", nonce)
                header("X-SBTC-SIGNATURE", signature)
                header("Content-Type", "application/json")
                setBody(data)
            }
//            Log.d("acoacoaco", "Respuesta es ${responseBudaPost.bodyAsText()}")
            val budaInvoice = Gson().fromJson(responseBudaPost.bodyAsText(), BudaCheckoutData::class.java).invoice.encoded_payment_request
            val checkId = Gson().fromJson(responseBudaPost.bodyAsText(), BudaCheckoutData::class.java).invoice.id
//            Log.d("acoacoaco", "Invoice es $budaInvoice")


            withContext(Dispatchers.Main) {
                findViewById<ImageView>(R.id.qrcodeimage).setImageBitmap(
                    getQrCodeBitmap(budaInvoice)
                )
                val copyButton = findViewById<Button>(R.id.copybutton)
                copyButton.setOnClickListener {
                    val clipboard: ClipboardManager =
                        getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText(getString(R.string.copy_invoice_message), budaInvoice)
                    clipboard.setPrimaryClip(clip)
                }
            }

            val checkURL = "$urlBudaCheck$checkId"







        }
    }

    private fun getQrCodeBitmap(invoice: String): Bitmap {
        val size = 640 //pixels
        hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(invoice, BarcodeFormat.QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }

}