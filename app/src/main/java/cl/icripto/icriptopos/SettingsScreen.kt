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

class SettingsScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_screen)

//        GitHHub Project URL. Make it selectable by user for opening it in the browser
        val textView: TextView = findViewById(R.id.linkGH)
        textView.movementMethod = LinkMovementMethod.getInstance()

//        Default values for parameters for all instances
        val defaultInstance = "BTCPay"
        val defaultCurrency = "USD"
        val defaultMerchantName = "Restaurant A"
        val defaultBtcpayServer = ""
        val defaultBtcpayApiKey = ""
        val defaultLnbitsServer = ""
        val defaultBtcpayStoreId = ""
        val defaultLnbitsInvoiceKey = ""
        val defaultLnbitsLnWalletId = ""
        val defaultLnbitsOnChainWalletId = ""
        val defaultTips = "no"
        val defaultBudaUserName = ""
        val defaultBitarooApiKey = ""
        val defaultBudaApiKey = ""
        val defaultBudaApiSecret = ""
        val defaultBinanceApiKey = ""
        val defaultBinanceApiSecret = ""

        var currencies: Array<String>


//        Loading saved parameters
        val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
        val savedInstance: String? = sharedPreferences.getString("INSTANCE", defaultInstance)
        val savedMerchantName: String? = sharedPreferences.getString("MERCHANTNAME", defaultMerchantName)
        val savedCurrency: String? = sharedPreferences.getString("LOCALCURRENCY", defaultCurrency)
        val savedBtcpayServer: String? = sharedPreferences.getString("BTCPAYSERVER", defaultBtcpayServer)
        val savedBtcpayApiKey: String? = sharedPreferences.getString("BTCPAYAPIKEY", defaultBtcpayApiKey)
        val savedLnbitsServer: String? = sharedPreferences.getString("LNBITSSERVER", defaultLnbitsServer)
        val savedBtcpayStoreId: String? = sharedPreferences.getString("BTCPAYSTORE", defaultBtcpayStoreId)
        val savedLnbitsInvoiceKey = sharedPreferences.getString("LNBITSINVOICEKEY", defaultLnbitsInvoiceKey)
        val savedLnbitsLnWalletId = sharedPreferences.getString("LNBITSLNWALLETID", defaultLnbitsLnWalletId)
        val savedLnbitsOnChainWalletId = sharedPreferences.getString("LNBITSONCHAINWALLETID", defaultLnbitsOnChainWalletId)
        val savedBudaUserName = sharedPreferences.getString("BUDAUSERNAME", defaultBudaUserName)
        val savedBitarooApiKey = sharedPreferences.getString("BITAROOAPIKEY", defaultBitarooApiKey)
        val savedBudaApiKey = sharedPreferences.getString("BUDAAPIKEY", defaultBudaApiKey)
        val savedBudaApiSecret = sharedPreferences.getString("BUDAAPISECRET", defaultBudaApiSecret)
        val savedBinanceApiKey = sharedPreferences.getString("BINANCEAPIKEY", defaultBinanceApiKey)
        val savedBinanceApiSecret = sharedPreferences.getString("BINANCEAPISECRET", defaultBinanceApiSecret)
        val tips : String? = sharedPreferences.getString("STATUSTIPS", defaultTips)

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
            arrayOf("BTCPay", "BTCPay API", "LNBits API", "Buda Link", "Buda API", "Bitaroo", "Binance API")
        } else {
            arrayOf(savedInstance) + arrayOf("BTCPay", "BTCPay API", "LNBits API", "Buda Link", "Buda API", "Bitaroo", "Binance API").filter{s -> s != savedInstance}
        }
        var instance: String

        instanceOption.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, instanceOptions)
        instanceOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
            @SuppressLint("CutPasteId")
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
                        val returnButton = findViewById<Button>(R.id.go_back_button)
                        returnButton.setOnClickListener {
                            openMainActivityNotSaved()
                        }

//                        Save button functionality
                        val saveButton = findViewById<Button>(R.id.save_button)
                        saveButton.setOnClickListener {
                            openMainActivitySaved(currency, instance)

                        }
                    }

                    "BTCPay API" -> {

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

//                        Set BTCPay API key Visible
                        findViewById<TextView>(R.id.ln_wallet_id_title).isInvisible = false
                        findViewById<EditText>(R.id.ln_wallet_id).isInvisible = false

//                        Hide Lnbits text views and boxes
                        findViewById<EditText>(R.id.onchain_wallet_id_title).isInvisible = true
                        findViewById<EditText>(R.id.onchain_wallet_id).isInvisible = true


//                        Fill boxes with parameters that were loaded
                        findViewById<TextView>(R.id.server_title).setText(R.string.enter_server)
                        findViewById<EditText>(R.id.server_url).setText(savedBtcpayServer)
                        findViewById<EditText>(R.id.server_url).setHint(R.string.server_url_hint)

                        findViewById<TextView>(R.id.store_title).setText(R.string.enter_lightning_id)
                        findViewById<EditText>(R.id.lightning_id).setText(savedBtcpayStoreId)
                        findViewById<EditText>(R.id.lightning_id).setHint(R.string.lightning_id_hint)


                        findViewById<TextView>(R.id.ln_wallet_id_title).setText(R.string.enter_btcpay_api_key)
                        findViewById<EditText>(R.id.ln_wallet_id).setText(savedBtcpayApiKey)
                        findViewById<EditText>(R.id.ln_wallet_id).setHint(R.string.btcpay_api_key_hint)

                        findViewById<EditText>(R.id.merchant_name).setText(savedMerchantName)
                        findViewById<Switch>(R.id.tips1).isChecked = tips == "yes"


//                        Go Back button functionality
                        val returnButton = findViewById<Button>(R.id.go_back_button)
                        returnButton.setOnClickListener {
                            openMainActivityNotSaved()
                        }

//                        Save button functionality
                        val saveButton = findViewById<Button>(R.id.save_button)
                        saveButton.setOnClickListener {
                            openMainActivitySaved(currency, instance)
                        }
                    }


                    "LNBits API" -> {

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
                        val returnButton = findViewById<Button>(R.id.go_back_button)
                        returnButton.setOnClickListener {
                            openMainActivityNotSaved()
                        }

//                        Save button functionality
                        val saveButton = findViewById<Button>(R.id.save_button)
                        saveButton.setOnClickListener {
                            openMainActivitySaved(currency, instance)
                        }
                    }

                    "Buda Link" -> {

                        currencies = arrayOf("ARS", "CLP", "COP", "PEN")

                        val currencyOption : Spinner = findViewById(R.id.spinner_currencies)
                        val currencyOptions : Array<String> =
                            if (currencies.contains(savedCurrency) && savedCurrency != null) {
                                arrayOf(savedCurrency) + currencies.filter{it != savedCurrency}
                            } else {
                                currencies
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


//                        Hide Lnbits text views and boxes
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
                        val returnButton = findViewById<Button>(R.id.go_back_button)
                        returnButton.setOnClickListener {
                            openMainActivityNotSaved()
                        }

//                        Save button functionality
                        val saveButton = findViewById<Button>(R.id.save_button)
                        saveButton.setOnClickListener {
                            openMainActivitySaved(currency, instance)
                        }
                    }

                    "Buda API" -> {

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
                        findViewById<TextView>(R.id.server_title).setText(R.string.enter_buda_api_key)
                        findViewById<EditText>(R.id.server_url).setText(savedBudaApiKey)
                        findViewById<EditText>(R.id.server_url).setHint(R.string.buda_api_key)

                        findViewById<TextView>(R.id.store_title).setText(R.string.enter_buda_api_secret)
                        findViewById<EditText>(R.id.lightning_id).setText(savedBudaApiSecret)
                        findViewById<EditText>(R.id.lightning_id).setHint(R.string.buda_api_secret)

                        findViewById<EditText>(R.id.merchant_name).setText(savedMerchantName)
                        findViewById<Switch>(R.id.tips1).isChecked = tips == "yes"


//                        Go Back button functionality
                        val returnButton = findViewById<Button>(R.id.go_back_button)
                        returnButton.setOnClickListener {
                            openMainActivityNotSaved()
                        }

//                        Save button functionality
                        val saveButton = findViewById<Button>(R.id.save_button)
                        saveButton.setOnClickListener {
                            openMainActivitySaved(currency, instance)
                        }
                    }

                    "Binance API" -> {

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
                        findViewById<TextView>(R.id.server_title).setText(R.string.enter_binance_api_key)
                        findViewById<EditText>(R.id.server_url).setText(savedBinanceApiKey)
                        findViewById<EditText>(R.id.server_url).setHint(R.string.binance_api_key)

                        findViewById<TextView>(R.id.store_title).setText(R.string.enter_binance_api_secret)
                        findViewById<EditText>(R.id.lightning_id).setText(savedBinanceApiSecret)
                        findViewById<EditText>(R.id.lightning_id).setHint(R.string.binance_api_secret)

                        findViewById<EditText>(R.id.merchant_name).setText(savedMerchantName)
                        findViewById<Switch>(R.id.tips1).isChecked = tips == "yes"


//                        Go Back button functionality
                        val returnButton = findViewById<Button>(R.id.go_back_button)
                        returnButton.setOnClickListener {
                            openMainActivityNotSaved()
                        }

//                        Save button functionality
                        val saveButton = findViewById<Button>(R.id.save_button)
                        saveButton.setOnClickListener {
                            openMainActivitySaved(currency, instance)
                        }
                    }


                    "Bitaroo" -> {

                        currencies = arrayOf("USD", "EUR", "AED", "ARS", "AUD", "BRL",
                            "CAD", "CHF", "CLP", "CNY", "GBP", "INR",
                            "JPY", "KRW", "MXN", "NGN", "RUB", "ZAR", "BTC")

                        val currencyOption : Spinner = findViewById(R.id.spinner_currencies)
                        val currencyOptions : Array<String> =
                            if (currencies.contains(savedCurrency) && savedCurrency != null) {
                                arrayOf(savedCurrency) + currencies.filter{it != savedCurrency}
                            } else {
                                currencies
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


//                        Hide Lnbits text views and boxes
                        findViewById<TextView>(R.id.ln_wallet_id_title).isInvisible = true
                        findViewById<EditText>(R.id.ln_wallet_id).isInvisible = true
                        findViewById<EditText>(R.id.onchain_wallet_id_title).isInvisible = true
                        findViewById<EditText>(R.id.onchain_wallet_id).isInvisible = true
                        findViewById<EditText>(R.id.store_title).isInvisible = true
                        findViewById<EditText>(R.id.lightning_id).isInvisible = true

//                        Fill boxes with parameters that were loaded
                        findViewById<TextView>(R.id.server_title).setText(R.string.enter_bitaroo_apikey)
                        findViewById<EditText>(R.id.server_url).setText(savedBitarooApiKey)
                        findViewById<EditText>(R.id.server_url).setHint(R.string.bitaroo_apikey)

//                        Restaurant name
                        findViewById<EditText>(R.id.merchant_name).setText(savedMerchantName)
                        findViewById<Switch>(R.id.tips1).isChecked = tips == "yes"


//                        Go Back button functionality
                        val returnButton = findViewById<Button>(R.id.go_back_button)
                        returnButton.setOnClickListener {
                            openMainActivityNotSaved()
                        }

//                        Save button functionality
                        val saveButton = findViewById<Button>(R.id.save_button)
                        saveButton.setOnClickListener {
                            openMainActivitySaved(currency, instance)
                        }
                    }


                }
            }
        }
    }

//    Save and go to main screen function
    private fun openMainActivitySaved(currency : String, instance: String) {
        saveData(currency, instance)
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        finishAffinity()
    }

    private fun openMainActivityNotSaved() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        finishAffinity()
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

            "BTCPay API" -> {
                val btcpayServer : String = findViewById<EditText>(R.id.server_url).text.toString()
                val storeId : String = findViewById<EditText>(R.id.lightning_id).text.toString()
                val btcpayApiKey : String = findViewById<EditText>(R.id.ln_wallet_id).text.toString()
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
                    putString("BTCPAYAPIKEY", btcpayApiKey)
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

            "LNBits API" -> {
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

            "Buda Link" -> {
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

//                Toast.makeText(this, "${R.string.data_saved} ${budaUserName.isNotEmpty()}", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, R.string.data_saved, Toast.LENGTH_SHORT).show()

            }

            "Buda API" -> {
                val budaApiKey : String = findViewById<EditText>(R.id.server_url).text.toString()
                val budaApiSecret : String = findViewById<EditText>(R.id.lightning_id).text.toString()
                val merchantName : String = findViewById<EditText>(R.id.merchant_name).text.toString()
                val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.apply{
                    putString("LOCALCURRENCY", currency)
                }.apply()
                editor.apply{
                    putString("BUDAAPIKEY", budaApiKey)
                }.apply()
                editor.apply{
                    putString("BUDAAPISECRET", budaApiSecret)
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

//                Toast.makeText(this, "${R.string.data_saved} ${budaUserName.isNotEmpty()}", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, R.string.data_saved, Toast.LENGTH_SHORT).show()

            }


            "Binance API" -> {
                val binanceApiKey : String = findViewById<EditText>(R.id.server_url).text.toString()
                val binanceApiSecret : String = findViewById<EditText>(R.id.lightning_id).text.toString()
                val merchantName : String = findViewById<EditText>(R.id.merchant_name).text.toString()
                val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.apply{
                    putString("LOCALCURRENCY", currency)
                }.apply()
                editor.apply{
                    putString("BINANCEAPIKEY", binanceApiKey)
                }.apply()
                editor.apply{
                    putString("BINANCEAPISECRET", binanceApiSecret)
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

//                Toast.makeText(this, "${R.string.data_saved} ${budaUserName.isNotEmpty()}", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, R.string.data_saved, Toast.LENGTH_SHORT).show()

            }




            "Bitaroo" -> {
                val bitarooApiKey : String = findViewById<EditText>(R.id.server_url).text.toString()
                val merchantName : String = findViewById<EditText>(R.id.merchant_name).text.toString()
                val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
                val editor : SharedPreferences.Editor = sharedPreferences.edit()
                editor.apply{
                    putString("LOCALCURRENCY", currency)
                }.apply()
                editor.apply{
                    putString("BITAROOAPIKEY", bitarooApiKey)
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

//                Toast.makeText(this, "${R.string.data_saved} ${budaUserName.isNotEmpty()}", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, R.string.data_saved, Toast.LENGTH_SHORT).show()

            }

        }
    }
}