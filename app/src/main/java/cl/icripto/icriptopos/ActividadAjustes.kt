package cl.icripto.icriptopos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible

class ActividadAjustes : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividad_ajustes)

//        GitHHub Project URL. Make it selectable by user for opening it in the browser
        val textView: TextView = findViewById(R.id.linkGH)
        textView.movementMethod = LinkMovementMethod.getInstance()

//        Default values for parameters for both BTCPay and LNBits instances
        val defaultInstance = "BTCPay"
        val defaultCurrency = "CLP"
        val defaultMerchantName = "Restaurant A"
        val defaultBtcpayServer = ""
        val defaultLnbitsServer = ""
        val defaultBtcpayStoreId = ""
        val defaultLnbitsInvoiceKey = ""
        val defaultLnbitsLnWalletId = ""
        val defaultLnbitsOnChainWalletId = ""
        val defaultTips = "no"
        val defaultBudaUserName = ""

        var currencies: Array<String>


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
        val savedBudaUserName = sharedPreferences.getString("BUDAUSERNAME", defaultBudaUserName)
        val tips : String? = sharedPreferences.getString("STATUSTIPS", defaultTips)


//        val currencyOption : Spinner = findViewById(R.id.spinner_currencies)
//        val currencyOptions : Array<String> =
//            if (savedCurrency == null) {
//                currencies
//            } else { arrayOf(savedCurrency) + currencies
//        }
//        var currency : String = savedCurrency.toString()
//        currencyOption.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, currencyOptions)
//        currencyOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//            }
//            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                currency = currencyOptions[p2]
//
//
//            }
//        }


        val resetPinButton = findViewById<Button>(R.id.delete_pin_button)
        resetPinButton.setOnClickListener {
            val sharedPreferencesPin : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
            val editor : SharedPreferences.Editor = sharedPreferencesPin.edit()
            editor.apply{
                putString("LOCALPIN", "")
            }.apply()
            Toast.makeText(this, R.string.pin_deleted, Toast.LENGTH_SHORT).show()

        }


//        Functionality of Instance selection spinner
        val instanceOption : Spinner = findViewById(R.id.spinner_instances)
        val instanceOptions : Array<String> = if (savedInstance == null) {
            arrayOf("BTCPay", "LNBits", "Buda")
        } else {
            arrayOf(savedInstance) + arrayOf("BTCPay", "LNBits", "Buda").filter{s -> s != savedInstance}
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

                        currencies = arrayOf("USD", "EUR", "AED", "ARS", "AUD", "BRL",
                            "CAD", "CHF", "CLP", "CNY", "GBP", "INR",
                            "JPY", "KRW", "MXN", "NGN", "RUB", "ZAR", "BTC")

                        val currencyOption : Spinner = findViewById(R.id.spinner_currencies)
                        val currencyOptions : Array<String> =
                            if (savedCurrency == null) {
                                currencies
                            } else { arrayOf(savedCurrency) + currencies.filter{it != savedCurrency}
                            }
                        var currency : String = savedCurrency.toString()
                        currencyOption.adapter = ArrayAdapter(baseContext, android.R.layout.simple_list_item_1, currencyOptions)
                        currencyOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(p0: AdapterView<*>?) {
                            }
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                currency = currencyOptions[p2]


                            }
                        }
//                        Set currency selector visible
                        findViewById<TextView>(R.id.currency_title).isInvisible = false
                        findViewById<FrameLayout>(R.id.frame_layout_currency).isInvisible = false

//                        Set store title visible
                        findViewById<EditText>(R.id.store_title).isInvisible = false
                        findViewById<EditText>(R.id.lightning_id).isInvisible = false


//                        Hide Lnbits text views and boxes
                        findViewById<TextView>(R.id.ln_wallet_id_title).isInvisible = true
                        findViewById<EditText>(R.id.ln_wallet_id).isInvisible = true
                        findViewById<EditText>(R.id.onchain_wallet_id_title).isInvisible = true
                        findViewById<EditText>(R.id.onchain_wallet_id).isInvisible = true


//                        Fill boxes with parameters that were loaded
                        findViewById<TextView>(R.id.server_title).setText(R.string.enter_server)
                        findViewById<EditText>(R.id.server_url).setText(savedBtcpayServer)
                        findViewById<EditText>(R.id.server_url).setHint(R.string.server_url_hint)

                        findViewById<TextView>(R.id.store_title).setText(R.string.enter_lightning_id)
                        findViewById<EditText>(R.id.lightning_id).setText(savedBtcpayStoreId)
                        findViewById<EditText>(R.id.lightning_id).setHint(R.string.lightning_id_hint)

                        findViewById<EditText>(R.id.merchant_name).setText(savedMerchantName)
                        findViewById<Switch>(R.id.tips1).isChecked = tips == "yes"


//                        Go Back button functionality
                        val volverButton = findViewById<Button>(R.id.go_back_button)
                        volverButton.setOnClickListener {
                            val intent = Intent(this@ActividadAjustes ,MainActivity::class.java)
                            startActivity(intent)
                        }

//                        Save button functionality
                        val guardarButton = findViewById<Button>(R.id.save_button)
                        guardarButton.setOnClickListener {
                            openMainActivitySaved(currency, instance)
                        }
                    }

                    "LNBits" -> {

                        currencies = arrayOf("USD", "EUR", "AED", "ARS", "AUD", "BRL",
                            "CAD", "CHF", "CLP", "CNY", "GBP", "INR",
                            "JPY", "KRW", "MXN", "NGN", "RUB", "ZAR", "BTC")

                        val currencyOption : Spinner = findViewById(R.id.spinner_currencies)
                        val currencyOptions : Array<String> =
                            if (savedCurrency == null) {
                                currencies
                            } else { arrayOf(savedCurrency) + currencies.filter{it != savedCurrency}
                            }
                        var currency : String = savedCurrency.toString()
                        currencyOption.adapter = ArrayAdapter(baseContext, android.R.layout.simple_list_item_1, currencyOptions)
                        currencyOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(p0: AdapterView<*>?) {
                            }
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                currency = currencyOptions[p2]


                            }
                        }

//                        Set currency selector visible
                        findViewById<TextView>(R.id.currency_title).isInvisible = false
                        findViewById<FrameLayout>(R.id.frame_layout_currency).isInvisible = false


//                        Show Lnbits text views and boxes
                        findViewById<EditText>(R.id.store_title).isInvisible = false
                        findViewById<EditText>(R.id.lightning_id).isInvisible = false
                        findViewById<TextView>(R.id.ln_wallet_id_title).isInvisible = false
                        findViewById<EditText>(R.id.ln_wallet_id).isInvisible = false
                        findViewById<EditText>(R.id.onchain_wallet_id_title).isInvisible = false
                        findViewById<EditText>(R.id.onchain_wallet_id).isInvisible = false


//                        Fill boxes with parameters that were loaded and edit instance title
                        findViewById<TextView>(R.id.server_title).setText(R.string.enter_server_lnbits)
                        findViewById<EditText>(R.id.server_url).setText(savedLnbitsServer)
                        findViewById<EditText>(R.id.server_url).setHint(R.string.server_lnbits_url_hint)


                        findViewById<TextView>(R.id.store_title).setText(R.string.enter_lnbits_api_title)
                        findViewById<EditText>(R.id.lightning_id).setText(savedLnbitsInvoiceKey)
                        findViewById<EditText>(R.id.lightning_id).setHint(R.string.enter_lnbits_api_key)

                        findViewById<TextView>(R.id.ln_wallet_id_title).setText(R.string.enter_ln_wallet_id)
                        findViewById<EditText>(R.id.ln_wallet_id).setText(savedLnbitsLnWalletId)
                        findViewById<EditText>(R.id.ln_wallet_id).setHint(R.string.ln_wallet_id_hint)

                        findViewById<TextView>(R.id.onchain_wallet_id_title).setText(R.string.enter_onchain_wallet_id)
                        findViewById<EditText>(R.id.onchain_wallet_id).setText(savedLnbitsOnChainWalletId)
                        findViewById<EditText>(R.id.onchain_wallet_id).setHint(R.string.onchain_wallet_id_hint)

                        findViewById<EditText>(R.id.merchant_name).setText(savedMerchantName)
                        findViewById<Switch>(R.id.tips1).isChecked = tips == "yes"


//                        Go Back button functionality
                        val volverButton = findViewById<Button>(R.id.go_back_button)
                        volverButton.setOnClickListener {
                            val intent = Intent(this@ActividadAjustes ,MainActivity::class.java)
                            startActivity(intent)
                        }

//                        Save button functionality
                        val guardarButton = findViewById<Button>(R.id.save_button)
                        guardarButton.setOnClickListener {
                            openMainActivitySaved(currency, instance)
                        }
                    }

                    "Buda" -> {

//                        Set currency selector visible
                        findViewById<TextView>(R.id.currency_title).isInvisible = true
                        findViewById<FrameLayout>(R.id.frame_layout_currency).isInvisible = true


//                        Show Lnbits text views and boxes
                        findViewById<TextView>(R.id.ln_wallet_id_title).isInvisible = true
                        findViewById<EditText>(R.id.ln_wallet_id).isInvisible = true
                        findViewById<EditText>(R.id.onchain_wallet_id_title).isInvisible = true
                        findViewById<EditText>(R.id.onchain_wallet_id).isInvisible = true
                        findViewById<EditText>(R.id.store_title).isInvisible = true
                        findViewById<EditText>(R.id.lightning_id).isInvisible = true

//                        Fill boxes with parameters that were loaded
                        findViewById<TextView>(R.id.server_title).setText(R.string.enter_buda_username)
                        findViewById<EditText>(R.id.server_url).setText(savedBudaUserName)
                        findViewById<EditText>(R.id.server_url).setHint(R.string.buda_username)

//                        Restaurant name
                        findViewById<EditText>(R.id.merchant_name).setText(savedMerchantName)
                        findViewById<Switch>(R.id.tips1).isChecked = tips == "yes"


//                        Go Back button functionality
                        val volverButton = findViewById<Button>(R.id.go_back_button)
                        volverButton.setOnClickListener {
                            val intent = Intent(this@ActividadAjustes ,MainActivity::class.java)
                            startActivity(intent)
                        }

//                        Save button functionality
                        val guardarButton = findViewById<Button>(R.id.save_button)
                        guardarButton.setOnClickListener {
                            openMainActivitySaved(savedCurrency ?: defaultCurrency, instance)
                        }



                    }
                }
            }
        }
    }

//    Save and go to main screen function
    private fun openMainActivitySaved(currency : String, instance: String) {
        val intent = Intent(this, MainActivity::class.java)
        saveData(currency, instance)
        startActivity(intent)
    }

//    Detailed saving function
    @SuppressLint("UseSwitchCompatOrMaterialCode", "CutPasteId")
    private fun saveData(currency: String, instance: String) {
        val tipsSwitch : Switch = findViewById(R.id.tips1)
        val tips: String = if (tipsSwitch.isChecked) {
            "yes"
        } else {
            "no"
        }

        when (instance) {
            "BTCPay" -> {
                val btcpayServer : String = findViewById<EditText>(R.id.server_url).text.toString()
                val storeId : String = findViewById<EditText>(R.id.lightning_id).text.toString()
                val merchantName : String = findViewById<EditText>(R.id.merchant_name).text.toString()
                val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.apply{
                    putString("LOCALCURRENCY", currency)
                }.apply()
                editor.apply{
                    putString("BTCPAYSERVER", btcpayServer)
                }.apply()
                editor.apply{
                    putString("BTCPAYSTORE", storeId)
                }.apply()
                editor.apply{
                    putString("MERCHANTNAME", merchantName)
                }.apply()
                editor.apply{
                    putString("STATUSTIPS", tips)
                }.apply()
                editor.apply{
                    putString("INSTANCE", instance)
                }.apply()

                Toast.makeText(this, R.string.data_saved, Toast.LENGTH_SHORT).show()

            }
            "LNBits" -> {
                val lnbitsServer : String = findViewById<EditText>(R.id.server_url).text.toString()
                val lnbitsInvoiceKey : String = findViewById<EditText>(R.id.lightning_id).text.toString()
                val lnbitsLnWalletId : String = findViewById<EditText>(R.id.ln_wallet_id).text.toString()
                val lnbitsOnchainWalletId : String = findViewById<EditText>(R.id.onchain_wallet_id).text.toString()
                val merchantName : String = findViewById<EditText>(R.id.merchant_name).text.toString()
                val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.apply{
                    putString("LOCALCURRENCY", currency)
                }.apply()
                editor.apply{
                    putString("LNBITSSERVER", lnbitsServer)
                }.apply()
                editor.apply{
                    putString("LNBITSINVOICEKEY", lnbitsInvoiceKey)
                }.apply()
                editor.apply{
                    putString("LNBITSLNWALLETID", lnbitsLnWalletId)
                }.apply()
                editor.apply{
                    putString("LNBITSONCHAINWALLETID", lnbitsOnchainWalletId)
                }.apply()
                editor.apply{
                    putString("MERCHANTNAME", merchantName)
                }.apply()
                editor.apply{
                    putString("STATUSTIPS", tips)
                }.apply()
                editor.apply{
                    putString("INSTANCE", instance)
                }.apply()

                Toast.makeText(this, R.string.data_saved, Toast.LENGTH_SHORT).show()


            }

            "Buda" -> {
                val budaUserName : String = findViewById<EditText>(R.id.server_url).text.toString()
                val merchantName : String = findViewById<EditText>(R.id.merchant_name).text.toString()
                val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.apply{
                    putString("LOCALCURRENCY", currency)
                }.apply()
                editor.apply{
                    putString("BUDAUSERNAME", budaUserName)
                }.apply()
                editor.apply{
                    putString("MERCHANTNAME", merchantName)
                }.apply()
                editor.apply{
                    putString("STATUSTIPS", tips)
                }.apply()
                editor.apply{
                    putString("INSTANCE", instance)
                }.apply()

                Toast.makeText(this, R.string.data_saved, Toast.LENGTH_SHORT).show()

            }

        }
    }
}