package cl.icripto.icriptopos

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.*
import androidx.core.view.isInvisible
import org.w3c.dom.Text

class ActividadAjustes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividad_ajustes)

//        Github Project URL. Make it selectable by user for opening it in the browser
        val textView: TextView = findViewById(R.id.linkGH)
        textView.movementMethod = LinkMovementMethod.getInstance()


//        Default values for parameters for both BTCPay and LNBits instances
        val defaultInstance = "BTCPay"
        val defaultCurrency = "CLP"
        val defaultMerchantName = "Restaurant A"
        val defaultBtcpayServer = "https://btcpay.icripto.cl"
        val defaultLnbitsServer = "https://lnbits.icripto.cl"
        val defaultBtcpayStoreId = ""
        val defaultLnbitsInvoiceKey = ""
        val defaultLnbitsLnWalletId = ""
        val defaultLnbitsOnChainWalletId = ""
        val defaultTips = "no"

//        Loading saved parameters
        val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
        val savedInstance: String? = sharedPreferences.getString("INSTANCE", defaultInstance)
        val savedMerchantName: String? = sharedPreferences.getString("MERCHANTNAME", defaultMerchantName)
        val savedCurrency: String? = sharedPreferences.getString("LOCALCURRENCY", defaultCurrency)
        val savedBtcpayServer: String? = sharedPreferences.getString("BTCPAYSERVER", defaultBtcpayServer)
        val savedLnbitsServer: String? = sharedPreferences.getString("LNBITSSERVER", defaultLnbitsServer)
        val savedBtcpayStoreId: String? = sharedPreferences.getString("BTCPAYSTORE", defaultBtcpayStoreId)
        val savedLnbitsInvoiceKey = sharedPreferences.getString("LNBITSINVOICEKEY", defaultLnbitsInvoiceKey)
        val savedLnbitsLnWalletId = sharedPreferences.getString("LNBITSLNWALLETID", defaultLnbitsLnWalletId)
        val savedLnbitsOnChainWalletId = sharedPreferences.getString("LNBITSONCHAINWALLETID", defaultLnbitsOnChainWalletId)
        val tips : String? = sharedPreferences.getString("STATUSTIPS", defaultTips)

//        Functionality of Currency selection spinner
        val currencyOption : Spinner = findViewById(R.id.spinner_currencies)
        val currencyOptions : Array<String>
        if (savedCurrency == null) {
            currencyOptions = arrayOf("USD", "EUR", "AED", "ARS", "AUD", "BRL", "CAD", "CHF", "CLP", "CNY", "GBP", "INR", "JPY", "KRW", "MXN", "NGN", "RUB", "ZAR", "BTC")
        } else {
            currencyOptions = arrayOf(savedCurrency) + arrayOf("USD", "EUR", "AED", "ARS", "AUD", "BRL", "CAD", "CHF", "CLP", "CNY", "GBP", "INR", "JPY", "KRW", "MXN", "NGN", "RUB", "ZAR", "BTC").filter{s -> s != savedCurrency}
        }
        var currency : String = savedCurrency.toString()
        currencyOption.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, currencyOptions)
        currencyOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                currency = currencyOptions[p2]
            }
        }


//        Functionality of Instance selection spinner
        val instanceOption : Spinner = findViewById(R.id.spinner_instances)
        val instanceOptions : Array<String>
        if (savedInstance == null) {
            instanceOptions = arrayOf("BTCPay", "LNBits")
        } else {
            instanceOptions = arrayOf(savedInstance) + arrayOf("BTCPay", "LNBits").filter{s -> s != savedInstance}
        }
        var instance: String

        instanceOption.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, instanceOptions)
        instanceOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                instance = instanceOptions[p2]

//                Switch behavior and settings activity depending on the instance selected by user
                when (instance) {
                    "BTCPay" -> {

//                        Hide Lnbits textviews and boxes
                        findViewById<TextView>(R.id.ln_wallet_id_title).isInvisible = true
                        findViewById<EditText>(R.id.ln_wallet_id).isInvisible = true
                        findViewById<EditText>(R.id.onchain_wallet_id_title).isInvisible = true
                        findViewById<EditText>(R.id.onchain_wallet_id).isInvisible = true

                        findViewById<TextView>(R.id.server_title).setText(R.string.enter_server)
                        findViewById<EditText>(R.id.server_url).setText(savedBtcpayServer)
                        findViewById<EditText>(R.id.server_url).setHint(R.string.server_url_hint)

//                        Fill boxes with parameters that were loaded
                        findViewById<EditText>(R.id.server_url).setText(savedBtcpayServer)
                        findViewById<EditText>(R.id.lightning_id).setText(savedBtcpayStoreId)
                        findViewById<EditText>(R.id.merchant_name).setText(savedMerchantName)
                        findViewById<Switch>(R.id.tips1).isChecked = tips == "yes"





//                        Go Back button functionality
                        val volverButton = findViewById<Button>(R.id.go_back_button)
                        volverButton.setOnClickListener {
                            val intent = Intent(this@ActividadAjustes ,MainActivity::class.java)
                            startActivity(intent)
                        }


                    }
                    "LNBits" -> {

                        findViewById<TextView>(R.id.ln_wallet_id_title).isInvisible = false
                        findViewById<EditText>(R.id.ln_wallet_id).isInvisible = false
                        findViewById<EditText>(R.id.onchain_wallet_id_title).isInvisible = false
                        findViewById<EditText>(R.id.onchain_wallet_id).isInvisible = false

                        findViewById<TextView>(R.id.server_title).setText(R.string.enter_server_lnbits)
                        findViewById<EditText>(R.id.server_url).setText(savedLnbitsServer)
                        findViewById<EditText>(R.id.server_url).hint = "Clearnet Only"





                    }

                }

            }
        }





    }

//    private fun openMainActivitySaved(moneda : String) {
//        val intent = Intent(this, MainActivity::class.java)
//        saveData(moneda)
//        startActivity(intent)
//    }
//
//    private fun saveData(moneda: String) {
//        val tipsSwitch : Switch = findViewById(R.id.tips1)
//        val tips: String = if (tipsSwitch.isChecked) {
//            "yes"
//        } else {
//            "no"
//        }
//        val nombreLocal : String = findViewById<EditText>(R.id.NLocal).text.toString()
//        val nombreServidor : String = findViewById<EditText>(R.id.URLServicio).text.toString()
//        val nombreIdTienda : String = findViewById<EditText>(R.id.IDTienda).text.toString()
//        val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
//        val editor : SharedPreferences.Editor = sharedPreferences.edit()
//        editor.apply{
//            putString("STATUSTIPS", tips)
//        }.apply()
//        editor.apply{
//            putString("LOCALNOMBRE", nombreLocal)
//        }.apply()
//        editor.apply{
//            putString("LOCALMONEDA", moneda)
//        }.apply()
//        editor.apply{
//            putString("LOCALSERVER", nombreServidor)
//        }.apply()
//        editor.apply{
//            putString("LOCALID", nombreIdTienda)
//        }.apply()
//
//        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show()
//    }
//
}