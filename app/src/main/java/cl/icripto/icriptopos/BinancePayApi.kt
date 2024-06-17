package cl.icripto.icriptopos

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import cl.icripto.icriptopos.models.BinanceCheckoutData
import cl.icripto.icriptopos.models.BinanceResponseObject
import cl.icripto.icriptopos.models.PriceObject
import cl.icripto.icriptopos.repositories.Hmac256
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*
import io.ktor.util.Identity.encode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.ByteString.Companion.decodeHex
import okio.ByteString.Companion.encode
import java.util.*
import kotlin.collections.set

class BinancePayApi : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_binance_pay_api)
        val textView: TextView = findViewById(R.id.linkGH)
        textView.movementMethod = LinkMovementMethod.getInstance()

        val defaultamountFiat = "150"
        val defaultCurrency = "CLP"
        val defaultMerchantName = ""
        val defaultBinanceApiKey = ""
        val defaultBinanceApiSecret = ""
        val timeToExpire: Long = 80000


        val sharedPreferences: SharedPreferences =
            getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
        val amountFiat = sharedPreferences.getString("PRICE", defaultamountFiat)
        val merchantName = sharedPreferences.getString("MERCHANTNAME", defaultMerchantName).toString()
        val binanceApiKey = sharedPreferences.getString("BINANCEAPIKEY", defaultBinanceApiKey).toString()
        val binanceApiSecret = sharedPreferences.getString("BINANCEAPISECRET", defaultBinanceApiSecret).toString()
        val currency = sharedPreferences.getString("LOCALCURRENCY", defaultCurrency).toString()
        val urlBinance = "https://api.binance.com"
        val pathBinance = "/sapi/v1/capital/deposit/address"
        val urlBinanceCheck = "https://api.binance.com"
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
            priceClient.close()
            val btcValueDecimal = String.format(Locale.ENGLISH, "%.8f", btcValue)
//            val btcValueDecimal = "9e-05"
            val coin = "BTC"
            val network = "LIGHTNING"
            val nonce = (System.currentTimeMillis()).toString()
//            val nonce = 1718589204214
            val data = "coin=BTC&network=LIGHTNING&amount=$btcValueDecimal&timestamp=$nonce"
//            val data = "coin=BTC&network=LIGHTNING&amount=$btcValueDecimal&timestamp=$nonce"
//            val data = "hola"


            val signature = Hmac256.digest(data, binanceApiSecret)

            Log.d("lalala", "data es: ${data}")
            Log.d("lalala", "firma es: ${signature}")

//            Log.d("lalala", "firma es: ${signature},\nnonce es: ${nonce}, monto es: ${btcValueDecimal}")
            val binanceClient = HttpClient(CIO)

            val responseBinanceGet: HttpResponse = binanceClient.get(urlBinance + pathBinance) {
                header("X-MBX-APIKEY", binanceApiKey)
//                header("Content-Type", "application/json")
                url{
                    parameters.append("coin", coin)
                    parameters.append("network", network)
                    parameters.append("amount", btcValueDecimal)
                    parameters.append("timestamp", nonce)
                    parameters.append("signature", signature)
                }
            }


            Log.d("lalala", "message, ${responseBinanceGet.status}")
            Log.d("lalala", "message, ${responseBinanceGet.request.url}")
            Log.d("lalala", "message, ${responseBinanceGet.bodyAsText()}")
            Log.d("lalala", "invoice es: ${Gson().fromJson(responseBinanceGet.bodyAsText(), BinanceResponseObject::class.java).address}")


            if (responseBinanceGet.status.toString() != "202") {
                binanceClient.close()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@BinancePayApi,
                        getString(R.string.binance_username_error),
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@BinancePayApi, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            } else {
                Log.d("lalala", "pasamos!")

                val binanceInvoice = Gson().fromJson(responseBinanceGet.bodyAsText(), BinanceCheckoutData::class.java).invoice.address
                binanceClient.close()



                withContext(Dispatchers.Main) {
                    findViewById<ImageView>(R.id.qrcodeimage).setImageBitmap(
                        getQrCodeBitmap(binanceInvoice)
                    )


                    timer.start()
                    val copyButton = findViewById<Button>(R.id.copybutton)
                    copyButton.setOnClickListener {
                        val clipboard: ClipboardManager =
                            getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clip: ClipData = ClipData.newPlainText(getString(R.string.copy_invoice_message), binanceInvoice)
                        clipboard.setPrimaryClip(clip)
                    }
                }







//
//
//                val pathBinanceCheck = "/sapi/v1/capital/deposit/hisrec"
//                val nonce2 = (System.currentTimeMillis()).toString()
//                val mensaje2 = "GET $pathBinanceCheck $nonce2"
//                val signature2 = Hmac256.digest(mensaje2, binanceApiSecret)
////
//                val binanceClient2 = HttpClient(CIO) {
//                    install(HttpTimeout) {
//                        requestTimeoutMillis = timeToExpire
//                    }
//                }
//
//                try {
//                    val responseBinanceGet2: HttpResponse = binanceClient2.get("$urlBinanceCheck$pathBinanceCheck") {
//                        header("X-SBTC-APIKEY", binanceApiKey)
//                        header("X-SBTC-NONCE", nonce2)
//                        header("X-SBTC-SIGNATURE", signature2)
//                        header("Content-Type", "application/json")
//                    }
//
//
//                    if (responseBinanceGet2.status.toString() != "200 OK") {
//                        binanceClient2.close()
//                        finishAffinity()
//                    } else {
//                        binanceClient2.close()
//                        withContext(Dispatchers.Main) {
//                            findViewById<ImageView>(R.id.qrcodeimage).setImageResource(R.drawable.checkmark)
//                            findViewById<ProgressBar>(R.id.progressBar).isInvisible = true
//                            val copyButton = findViewById<Button>(R.id.copybutton)
//                            copyButton.text = getString(R.string.go_back_text)
//                            Toast.makeText(
//                                this@BinancePayApi,
//                                getString(R.string.paid_invoice_message),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            timer.cancel()
//                            copyButton.setOnClickListener {
//                                val intent = Intent(baseContext, MainActivity::class.java)
//                                startActivity(intent)
//                                finishAffinity()
//                            }
//                        }
//                    }
//                } catch (e: HttpRequestTimeoutException) {
//                    e.printStackTrace()
//                    binanceClient2.close()
//                    withContext(Dispatchers.Main) {
//                        findViewById<ImageView>(R.id.qrcodeimage).setImageResource(R.drawable.xmark)
//                        findViewById<ProgressBar>(R.id.progressBar).isInvisible = true
//                        val copyButton = findViewById<Button>(R.id.copybutton)
//                        copyButton.text = getString(R.string.go_back_text)
//                        copyButton.setOnClickListener {
//                            val intent = Intent(baseContext, MainActivity::class.java)
//                            startActivity(intent)
//                            finishAffinity()
//                        }
//                    }
//                }

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