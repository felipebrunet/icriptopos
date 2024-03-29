package cl.icripto.icriptopos

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.method.LinkMovementMethod
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import cl.icripto.icriptopos.models.BitarooCheckoutData
import cl.icripto.icriptopos.models.PriceObject
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import java.util.*

class BitarooPay : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitaroo_pay)

        val textView: TextView = findViewById(R.id.linkGH)
        textView.movementMethod = LinkMovementMethod.getInstance()

        val defaultamountFiat = "1.0"
        val defaultCurrency = "AUD"
        val defaultMerchantName = ""
        val defaultBitarooApiKey = ""
        val timeToExpire: Long = 180000

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
        val amountFiat = sharedPreferences.getString("PRICE", defaultamountFiat)
        val merchantName = sharedPreferences.getString("MERCHANTNAME", defaultMerchantName).toString()
        val apiKeyBitaroo = "Bearer ${sharedPreferences.getString("BITAROOAPIKEY", defaultBitarooApiKey).toString()}"
        val currency = sharedPreferences.getString("LOCALCURRENCY", defaultCurrency).toString()
        val urlBitaroo = "https://api.bitaroo.com.au"
        val pathBitaroo = "v1/payments/ln-invoice"
        val btcPriceUrl = "https://api.yadio.io/convert/$amountFiat/$currency/BTC"
        findViewById<TextView>(R.id.MonedaPagoValor).text = currency
        findViewById<TextView>(R.id.MontoPagoValor).text = String.format(Locale.ENGLISH, "%.2f", amountFiat!!.toDouble())
        findViewById<TextView>(R.id.MotivoPagoValor).text = merchantName

        val priceClient = HttpClient(CIO)
        val bitarooClient = HttpClient(CIO)

        val timer = object: CountDownTimer(timeToExpire, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                findViewById<TextView>(R.id.TextoInstruccion).text = getString(R.string.instr_para_pagar) + " " + (millisUntilFinished/1000).toString() + " s"
            }

            override fun onFinish() {
                bitarooClient.close()
                findViewById<ImageView>(R.id.qrcodeimage).setImageResource(R.drawable.xmark)
                findViewById<ProgressBar>(R.id.progressBar).isInvisible = true
                val copyButton = findViewById<Button>(R.id.copybutton)
                copyButton.text = getString(R.string.go_back_text)
                copyButton.setOnClickListener {
                    val intent = Intent(baseContext, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }
        }



        val corr1 = CoroutineScope(Dispatchers.IO)
        corr1.launch {
            val responsePrice: HttpResponse = priceClient.get(btcPriceUrl)
            val btcValue = Gson().fromJson(responsePrice.bodyAsText(), PriceObject::class.java).result
            val btcValueDecimal = String.format(Locale.ENGLISH, "%.8f", btcValue )
            priceClient.close()


            val bitarooPost: HttpResponse = bitarooClient.post{
                url {
                    protocol = URLProtocol.HTTPS
                    host = urlBitaroo.substring(8, urlBitaroo.length)
                    path(pathBitaroo)
                }
                headers {
                    append(HttpHeaders.Authorization, apiKeyBitaroo)
                    append(HttpHeaders.ContentType, "application/json")
                }
                setBody("{\"amount\": \"$btcValueDecimal\"}")
            }

            if (bitarooPost.status.toString() != "200 OK") {
                bitarooClient.close()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@BitarooPay,
                        getString(R.string.bitaroo_username_error),
                        Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@BitarooPay, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            } else {
                val bitarooInvoice = Gson().fromJson(bitarooPost.bodyAsText(), BitarooCheckoutData::class.java).invoice

                withContext(Dispatchers.Main) {
                    findViewById<ImageView>(R.id.qrcodeimage).setImageBitmap(
                        getQrCodeBitmap(bitarooInvoice)
                    )


                    timer.start()
                    val copyButton = findViewById<Button>(R.id.copybutton)
                    copyButton.setOnClickListener {
                        val clipboard: ClipboardManager =
                            getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val clip: ClipData = ClipData.newPlainText(getString(R.string.copy_invoice_message), bitarooInvoice)
                        clipboard.setPrimaryClip(clip)
                    }
                }

                do {
                    delay(1000)
                    val bitarooGet: HttpResponse = bitarooClient.get{
                        url {
                            protocol = URLProtocol.HTTPS
                            host = urlBitaroo.substring(8, urlBitaroo.length)
                            path(pathBitaroo)
                        }
                        headers {
                            append(HttpHeaders.Authorization, apiKeyBitaroo)
                            append(HttpHeaders.ContentType, "application/json")
                        }
                    }



                } while (bitarooGet.bodyAsText() != "null")

                bitarooClient.close()

                withContext(Dispatchers.Main) {
                    findViewById<ImageView>(R.id.qrcodeimage).setImageResource(R.drawable.checkmark)
                    findViewById<ProgressBar>(R.id.progressBar).isInvisible = true
                    val copyButton = findViewById<Button>(R.id.copybutton)
                    copyButton.text = getString(R.string.go_back_text)
                    Toast.makeText(
                        this@BitarooPay,
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