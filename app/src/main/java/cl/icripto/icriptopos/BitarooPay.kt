package cl.icripto.icriptopos

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
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
import io.ktor.client.request.headers
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import java.util.*

class BitarooPay : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitaroo_pay)

        val currency = "CLP"
        val amountFiat = "3"
        val urlBitaroo = "https://api.bitaroo.com.au"
        val pathBitaroo = "v1/payments/ln-invoice"
        val apiKeyBitaroo = "Bearer yeQXq39oQLaHYTLP.yyWP__BJS1SVB3HMXsgcxGNn7UQXS3n9WhNfTFrw"
        val btcPriceUrl = "https://api.yadio.io/convert/$amountFiat/$currency/BTC"
        findViewById<TextView>(R.id.MonedaPagoValor).text = currency
        findViewById<TextView>(R.id.MontoPagoValor).text = amountFiat

        val priceClient = HttpClient(CIO)
        val corr1 = CoroutineScope(Dispatchers.IO)
        corr1.launch {
            val responsePrice: HttpResponse = priceClient.get(btcPriceUrl)
            val btcValue = Gson().fromJson(responsePrice.bodyAsText(), PriceObject::class.java).result
            val btcValueDecimal = String.format(Locale.ENGLISH, "%.8f", btcValue )
            val bitarooClient = HttpClient(CIO)
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
            val bitarooInvoice = Gson().fromJson(bitarooPost.bodyAsText(), BitarooCheckoutData::class.java).invoice

            withContext(Dispatchers.Main) {
                findViewById<ImageView>(R.id.qrcodeimage).setImageBitmap(
                    getQrCodeBitmap(bitarooInvoice)
                )
                val copyButton = findViewById<Button>(R.id.copybutton)
                copyButton.setOnClickListener {
                    val clipboard: ClipboardManager =
                        getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip: ClipData = ClipData.newPlainText(getString(R.string.copy_invoice_message), bitarooInvoice)
                    clipboard.setPrimaryClip(clip)
                }
            }

            do {
                delay(3000)
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

            withContext(Dispatchers.Main) {
                findViewById<ImageView>(R.id.qrcodeimage).setImageResource(R.drawable.checkmark)
                findViewById<ProgressBar>(R.id.progressBar).isInvisible = true
                Toast.makeText(baseContext, "Eureka, invoice pagado", Toast.LENGTH_SHORT).show()
                val copyButton = findViewById<Button>(R.id.copybutton)
                copyButton.text = getString(R.string.go_back_text)
                copyButton.setOnClickListener {
                    val intent = Intent(baseContext, MainActivity::class.java)
                    startActivity(intent)
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