package cl.icripto.icriptopos

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
//import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class BudaPay : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buda_pay)
        val textView: TextView = findViewById(R.id.linkGH)
        textView.movementMethod = LinkMovementMethod.getInstance()

        val defaultPrice = 0.0
        val defaultMerchantName = ""
        val defaultBudaUserName = ""

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
        val price = sharedPreferences.getString("PRICE", defaultPrice.toString()).toString().toDouble()
        val merchantName = sharedPreferences.getString("MERCHANTNAME", defaultMerchantName).toString()
        val budaUserName = sharedPreferences.getString("BUDAUSERNAME", defaultBudaUserName).toString()

        val urlBuda = "https://www.buda.com/api/v2/pay/${budaUserName}/invoice?amount=${price}&description=cobro_${merchantName}"

        findViewById<TextView>(R.id.MontoPagoValor).text = "$${price.toInt()}"
        findViewById<TextView>(R.id.MotivoPagoValor).text = "$merchantName (vendor $budaUserName)"




        val request = Request.Builder().url(urlBuda).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful)
                    {
                        runOnUiThread {
                            Toast.makeText(
                                this@BudaPay,
                                getString(R.string.buda_username_error),
                                Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@BudaPay, MainActivity::class.java)
                            startActivity(intent)
                        }
                        throw IOException("Unexpected code $response")

                    } else {

                        val resp = response.body!!.string()
                        val invoice: String = JSONObject(resp).getString("encoded_payment_request")
                        val checkId = JSONObject(resp).getString("id")
                        val currencyCharge = JSONObject(resp).getString("currency")
                        val editor : SharedPreferences.Editor = sharedPreferences.edit()
                        editor.apply{
                            putString("LOCALCURRENCY", currencyCharge)
                        }.apply()
                        runOnUiThread {
                            findViewById<TextView>(R.id.MonedaPagoValor).text = currencyCharge
                            findViewById<ImageView>(R.id.qrcodeimage).setImageBitmap(
                                getQrCodeBitmap(invoice)
                            )

                            val copyButton = findViewById<Button>(R.id.copybutton)
                            copyButton.setOnClickListener {
                                val clipboard: ClipboardManager =
                                    getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                val clip: ClipData = ClipData.newPlainText(getString(R.string.copy_invoice_message), invoice)
                                clipboard.setPrimaryClip(clip)
                            }
                        }

                        val checkURL = "https://realtime.buda.com/sub?channel=lightninginvoices%40$checkId"
//                        val checkURL = "http://172.21.6.98:5000"
                        val requestFinish = Request.Builder().url(checkURL).build()
                        val clientFinish = OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(300, TimeUnit.SECONDS)
                            .build()
                        clientFinish.newCall(requestFinish).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                e.printStackTrace()
                            }

                            override fun onResponse(call: Call, response: Response) {
                                response.use {
                                    if (!response.isSuccessful) {
                                        throw IOException("Unexpected code $response")
                                    } else {

                                        runOnUiThread {
                                            findViewById<ImageView>(R.id.qrcodeimage).setImageResource(R.drawable.checkmark)
                                            findViewById<ProgressBar>(R.id.progressBar).isInvisible = true
                                            Toast.makeText(
                                                this@BudaPay,
                                                getString(R.string.paid_invoice_message),
                                                Toast.LENGTH_SHORT
                                            ).show()
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
                        })


                    }
                }
            }
        })

    }

    fun getQrCodeBitmap(invoice: String): Bitmap {
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