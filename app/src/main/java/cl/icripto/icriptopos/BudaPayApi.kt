package cl.icripto.icriptopos

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.method.LinkMovementMethod
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import cl.icripto.icriptopos.models.BudaCheckoutData
import cl.icripto.icriptopos.models.PriceObject
import cl.icripto.icriptopos.repositories.Hmac
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
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
        val timeToExpire: Long = 60000


        val sharedPreferences: SharedPreferences =
            getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
        val amountFiat = sharedPreferences.getString("PRICE", defaultamountFiat)
        val merchantName = sharedPreferences.getString("MERCHANTNAME", defaultMerchantName).toString()
        val budaApiKey = sharedPreferences.getString("BUDAAPIKEY", defaultBudaApiKey).toString()
        val budaApiSecret = sharedPreferences.getString("BUDAAPISECRET", defaultBudaApiSecret).toString()
        val currency = sharedPreferences.getString("LOCALCURRENCY", defaultCurrency).toString()
        val urlBuda = "https://www.buda.com"
        val pathBuda = "/api/v2/lightning_network_invoices"
        val urlBudaCheck = "https://realtime.buda.com"
        val btcPriceUrl = "https://api.yadio.io/convert/$amountFiat/$currency/BTC"
        val timer = object: CountDownTimer(timeToExpire, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                findViewById<TextView>(R.id.TextoInstruccion).text = getString(R.string.instr_para_pagar) + " " + (millisUntilFinished/1000).toString() + " s"
            }

            override fun onFinish() {
            }
        }


        findViewById<TextView>(R.id.MonedaPagoValor).text = currency
        findViewById<TextView>(R.id.MontoPagoValor).text = String.format(Locale.ENGLISH, "%.2f", amountFiat!!.toDouble() )
//        findViewById<TextView>(R.id.MontoPagoValor).text = "$${amountFiat}"
        findViewById<TextView>(R.id.MotivoPagoValor).text = merchantName

        val priceClient = HttpClient(CIO)
        val corr1 = CoroutineScope(Dispatchers.IO)
        corr1.launch {
            val responsePrice: HttpResponse = priceClient.get(btcPriceUrl)
            val btcValue = Gson().fromJson(responsePrice.bodyAsText(), PriceObject::class.java).result
            val satsValue = (btcValue*100000000).toInt()
            priceClient.close()
//            val btcValueDecimal = String.format(Locale.ENGLISH, "%.8f", btcValue)
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

            if (responseBudaPost.status.toString() != "201 Created") {
                budaClient.close()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@BudaPayApi,
                        getString(R.string.buda_username_error),
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@BudaPayApi, MainActivity::class.java)
                    startActivity(intent)
                }
            } else {

                val budaInvoice = Gson().fromJson(responseBudaPost.bodyAsText(), BudaCheckoutData::class.java).invoice.encoded_payment_request
                val checkId = Gson().fromJson(responseBudaPost.bodyAsText(), BudaCheckoutData::class.java).invoice.id
                budaClient.close()



                withContext(Dispatchers.Main) {
                    findViewById<ImageView>(R.id.qrcodeimage).setImageBitmap(
                        getQrCodeBitmap(budaInvoice)
                    )


                    timer.start()
                    val copyButton = findViewById<Button>(R.id.copybutton)
                    copyButton.setOnClickListener {
                        val clipboard: ClipboardManager =
                            getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clip: ClipData = ClipData.newPlainText(getString(R.string.copy_invoice_message), budaInvoice)
                        clipboard.setPrimaryClip(clip)
                    }
                }

                val pathBudaCheck = "/sub?channel=lightninginvoices%40$checkId"
                val nonce2 = (System.currentTimeMillis()*1000).toString()
                val mensaje2 = "GET $pathBudaCheck $nonce2"
                val signature2 = Hmac.digest(mensaje2, budaApiSecret)
//
                val budaClient2 = HttpClient(CIO) {
                    install(HttpTimeout) {
                        requestTimeoutMillis = timeToExpire
                    }
                }



                try {
                    val responseBudaGet: HttpResponse = budaClient2.get("$urlBudaCheck$pathBudaCheck") {
                        header("X-SBTC-APIKEY", budaApiKey)
                        header("X-SBTC-NONCE", nonce2)
                        header("X-SBTC-SIGNATURE", signature2)
                        header("Content-Type", "application/json")
                    }

                    if (responseBudaGet.status.toString() != "200 OK") {
                        budaClient2.close()
                        finish()
                    } else {
                        budaClient2.close()
                        withContext(Dispatchers.Main) {
                            findViewById<ImageView>(R.id.qrcodeimage).setImageResource(R.drawable.checkmark)
                            findViewById<ProgressBar>(R.id.progressBar).isInvisible = true
                            val copyButton = findViewById<Button>(R.id.copybutton)
                            copyButton.text = getString(R.string.go_back_text)
                            Toast.makeText(
                                this@BudaPayApi,
                                getString(R.string.paid_invoice_message),
                                Toast.LENGTH_SHORT
                            ).show()
                            timer.cancel()
                            copyButton.setOnClickListener {
                                val intent = Intent(baseContext, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                } catch (e: HttpRequestTimeoutException) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        findViewById<ImageView>(R.id.qrcodeimage).setImageResource(R.drawable.xmark)
                        findViewById<ProgressBar>(R.id.progressBar).isInvisible = true
                        val copyButton = findViewById<Button>(R.id.copybutton)
                        copyButton.text = getString(R.string.go_back_text)
                        copyButton.setOnClickListener {
                            val intent = Intent(baseContext, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }

            }
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