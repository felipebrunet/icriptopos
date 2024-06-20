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
import app.cash.lninvoice.PaymentRequest
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
import kotlinx.coroutines.*
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
        val timeToExpire: Long = 240000


        val sharedPreferences: SharedPreferences =
            getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
        val amountFiat = sharedPreferences.getString("PRICE", defaultamountFiat)
        val merchantName = sharedPreferences.getString("MERCHANTNAME", defaultMerchantName).toString()
        val binanceApiKey = sharedPreferences.getString("BINANCEAPIKEY", defaultBinanceApiKey).toString()
        val binanceApiSecret = sharedPreferences.getString("BINANCEAPISECRET", defaultBinanceApiSecret).toString()
        val currency = sharedPreferences.getString("LOCALCURRENCY", defaultCurrency).toString()
        val urlBinance = "https://api.binance.com"
        val pathBinance = "/sapi/v1/capital/deposit/address"
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
        findViewById<TextView>(R.id.MotivoPagoValor).text = merchantName

        val priceClient = HttpClient(CIO)
        val corr1 = CoroutineScope(Dispatchers.IO)
        corr1.launch {
            val responsePrice: HttpResponse = priceClient.get(btcPriceUrl)
            val btcValue = Gson().fromJson(responsePrice.bodyAsText(), PriceObject::class.java).result
            priceClient.close()
            val btcValueDecimal = String.format(Locale.ENGLISH, "%.8f", btcValue)
            val coin = "BTC"
            val network = "LIGHTNING"
            val nonce = (System.currentTimeMillis()).toString()
            val data = "coin=BTC&network=LIGHTNING&amount=$btcValueDecimal&timestamp=$nonce"
            val signature = Hmac256.digest(data, binanceApiSecret)


            val binanceClient = HttpClient(CIO)
            val responseBinanceGet: HttpResponse = binanceClient.get(urlBinance + pathBinance) {
                header("X-MBX-APIKEY", binanceApiKey)
                url{
                    parameters.append("coin", coin)
                    parameters.append("network", network)
                    parameters.append("amount", btcValueDecimal)
                    parameters.append("timestamp", nonce)
                    parameters.append("signature", signature)
                }
            }

            val failCode: Int = Gson().fromJson(responseBinanceGet.bodyAsText(), BinanceResponseObject::class.java).code ?: 0

            if (responseBinanceGet.status.toString() != "200 ") {
                if (failCode == -1100) {
                    binanceClient.close()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@BinancePayApi,
                            getString(R.string.binance_invalid_amount),
                            Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@BinancePayApi, MainActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                    }
                }
                else {
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
                }
            } else {


                val binanceInvoice = Gson().fromJson(responseBinanceGet.bodyAsText(), BinanceResponseObject::class.java).address
                val paymentHash = PaymentRequest.parseUnsafe(binanceInvoice).paymentHash
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

                val pathBinanceCheck = "/sapi/v1/capital/deposit/hisrec"
                val binanceClient2 = HttpClient(CIO)

                for (idx in 0 until (timeToExpire*0.8/2000).toInt()) {


                    val nonce2 = (System.currentTimeMillis()).toString()
                    val data2 = "timestamp=$nonce2"
                    val signature2 = Hmac256.digest(data2, binanceApiSecret)

                    val responseBinanceGet2: HttpResponse = binanceClient2.get(urlBinance + pathBinanceCheck) {
                        header("X-MBX-APIKEY", binanceApiKey)
                        url{
                            parameters.append("timestamp", nonce2)
                            parameters.append("signature", signature2)
                        }
                    }
                    val binanceResponse = responseBinanceGet2.bodyAsText()
                    delay(1000)

                    if (binanceResponse.contains(paymentHash, ignoreCase = true)){
                        binanceClient2.close()
                        withContext(Dispatchers.Main) {
                            findViewById<ImageView>(R.id.qrcodeimage).setImageResource(R.drawable.checkmark)
                            findViewById<ProgressBar>(R.id.progressBar).isInvisible = true
                            val copyButton = findViewById<Button>(R.id.copybutton)
                            copyButton.text = getString(R.string.go_back_text)
                            Toast.makeText(
                                this@BinancePayApi,
                                getString(R.string.paid_invoice_message),
                                Toast.LENGTH_SHORT
                            ).show()
                            timer.cancel()
                            copyButton.setOnClickListener {
                                val intent = Intent(baseContext, MainActivity::class.java)
                                startActivity(intent)
                                finishAffinity()
                            }
                        }
                    }
                    delay(1000)
                }



                binanceClient2.close()
                withContext(Dispatchers.Main) {
                    findViewById<ImageView>(R.id.qrcodeimage).setImageResource(R.drawable.xmark)
                    findViewById<ProgressBar>(R.id.progressBar).isInvisible = true
                    val copyButton = findViewById<Button>(R.id.copybutton)
                    copyButton.text = getString(R.string.go_back_text)
                    timer.cancel()
                    copyButton.setOnClickListener {
                        val intent = Intent(baseContext, MainActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
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