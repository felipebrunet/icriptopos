package cl.icripto.icriptopos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import cl.icripto.icriptopos.repositories.payBTCPay
import cl.icripto.icriptopos.repositories.payLNBits
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_IcriptoPOS)
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)


//        Constants associated to the keypad and other buttons

        val input: TextView = findViewById(R.id.input)
        val buttonPay: Button = findViewById(R.id.pay_button)
        val button1: Button = findViewById(R.id.button_1)
        val button2: Button = findViewById(R.id.button_2)
        val button3: Button = findViewById(R.id.button_3)
        val button4: Button = findViewById(R.id.button_4)
        val button5: Button = findViewById(R.id.button_5)
        val button6: Button = findViewById(R.id.button_6)
        val button7: Button = findViewById(R.id.button_7)
        val button8: Button = findViewById(R.id.button_8)
        val button9: Button = findViewById(R.id.button_9)
        val button0: Button = findViewById(R.id.button_0)
        val buttonDot: Button = findViewById(R.id.button_dot)
        val buttonDelete: Button = findViewById(R.id.button_delete)
        val adjustScreenButton = findViewById<Button>(R.id.settings_button)

//        Default value for business constants

        val defaultInstance = "BTCPay"
        val defaultCurrency = "CLP"
        val defaultMerchantName = ""
        val defaultBtcpayServer = ""
        val defaultBtcpayApiKey = ""
        val defaultLnbitsServer = ""
        val defaultBudaUserName = ""
        val defaultBudaApiKey = ""
        val defaultBudaApiSecret = ""
        val defaultBinanceApiKey = ""
        val defaultBinanceApiSecret = ""
        val defaultBitarooApiKey = ""
        val defaultBtcpayStoreId = ""
        val defaultLnbitsInvoiceKey = ""
        val defaultLnbitsLnWalletId = ""
        val defaultLnbitsOnChainWalletId = ""
        val defaultPin = ""
        val defaultTips = "no"
        val initMessage: String = getString(R.string.init_val_screen)

//        Load current User parameters

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
        val instance = sharedPreferences.getString("INSTANCE", defaultInstance).toString()
        val currency = sharedPreferences.getString("LOCALCURRENCY", defaultCurrency).toString()
        val merchantName = sharedPreferences.getString("MERCHANTNAME", defaultMerchantName).toString()
        val btcpayServer = sharedPreferences.getString("BTCPAYSERVER", defaultBtcpayServer).toString()
        val lnbitsServer = sharedPreferences.getString("LNBITSSERVER", defaultLnbitsServer).toString()
        val budaUserName = sharedPreferences.getString("BUDAUSERNAME", defaultBudaUserName).toString()
        val budaApiKey = sharedPreferences.getString("BUDAAPIKEY", defaultBudaApiKey).toString()
        val budaApiSecret = sharedPreferences.getString("BUDAAPISECRET", defaultBudaApiSecret).toString()
        val binanceApiKey = sharedPreferences.getString("BINANCEAPIKEY", defaultBinanceApiKey).toString()
        val binanceApiSecret = sharedPreferences.getString("BINANCEAPISECRET", defaultBinanceApiSecret).toString()
        val bitarooApiKey = sharedPreferences.getString("BITAROOAPIKEY", defaultBitarooApiKey).toString()
        val btcpayStoreId = sharedPreferences.getString("BTCPAYSTORE", defaultBtcpayStoreId).toString()
        val btcpayApiKey = sharedPreferences.getString("BTCPAYAPIKEY", defaultBtcpayApiKey).toString()
        val lnbitsInvoiceKey = sharedPreferences.getString("LNBITSINVOICEKEY", defaultLnbitsInvoiceKey).toString()
        val lnbitsLnWalletId = sharedPreferences.getString("LNBITSLNWALLETID", defaultLnbitsLnWalletId).toString()
        val lnbitsOnChainWalletId = sharedPreferences.getString("LNBITSONCHAINWALLETID", defaultLnbitsOnChainWalletId).toString()
        val tips = sharedPreferences.getString("STATUSTIPS", defaultTips).toString()
        val pin = sharedPreferences.getString("LOCALPIN", defaultPin).toString()


//        Transition to Settings activity, including the setup of the secret pin

        adjustScreenButton.setOnClickListener {
            val builder =
                AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))//(this)
            if (pin == "") {
                builder.setTitle(getString(R.string.create_pin))
                val inputPin = EditText(ContextThemeWrapper(this, R.style.AlertInputCustom))
                builder.setView(inputPin)
                builder.setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                    val sharedPreferencesPin: SharedPreferences =
                        getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPreferencesPin.edit()
                    editor.apply {
                        putString("LOCALPIN", inputPin.text.toString())
                    }.apply()
                    if (inputPin.text.isNotEmpty()) {
                        Toast.makeText(this, getString(R.string.saved_pin), Toast.LENGTH_SHORT).show()
                    }
                    val intent = Intent(this, SettingsScreen::class.java)
                    startActivity(intent)

                }
                builder.show()
            } else {
                builder.setTitle(getString(R.string.enter_pin))
                val inputPin = EditText(ContextThemeWrapper(this, R.style.AlertInputCustom))
                builder.setView(inputPin)
                builder.setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()

                    if (inputPin.text.toString() == pin) {
                        Toast.makeText(this, getString(R.string.granted_message), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, SettingsScreen::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, getString(R.string.denied_message), Toast.LENGTH_SHORT).show()
                    }

                }
                builder.show()
            }
        }

//        Setting merchant name and currency in the main activity

        findViewById<TextView>(R.id.currency).text = currency
        findViewById<TextView>(R.id.merchant_title).text = merchantName

        if (!checkSettings(instance, btcpayServer, btcpayStoreId,
                btcpayApiKey, lnbitsServer, lnbitsInvoiceKey,
                lnbitsLnWalletId, budaUserName, budaApiKey, budaApiSecret, binanceApiKey, binanceApiSecret, bitarooApiKey)) {
            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0F)
            input.text = addToInputText(initMessage, input)
        }

        else {

            input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30.0F)

            buttonDelete.setOnClickListener {
                input.text = ""
                input.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
            button1.setOnClickListener {
                input.text = addToInputText("1", input)
            }
            button2.setOnClickListener {
                input.text = addToInputText("2", input)
            }
            button3.setOnClickListener {
                input.text = addToInputText("3", input)
            }
            button4.setOnClickListener {
                input.text = addToInputText("4", input)
            }
            button5.setOnClickListener {
                input.text = addToInputText("5", input)
            }
            button6.setOnClickListener {
                input.text = addToInputText("6", input)
            }
            button7.setOnClickListener {
                input.text = addToInputText("7", input)
            }
            button8.setOnClickListener {
                input.text = addToInputText("8", input)
            }
            button9.setOnClickListener {
                input.text = addToInputText("9", input)
            }
            button0.setOnClickListener {
                if (input.text.isEmpty()) {
                    // Show Error Message
                    input.text = addToInputText("", input)
                }
                else {
                    input.text = addToInputText("0", input)
                }
            }
            buttonDot.setOnClickListener {
                if (input.text.isEmpty()) {
                    input.text = addToInputText("0.", input)
                } else {
                    input.text = addToInputText(".", input)
                }
            }

//        Setting the function of the "Pay" button
            buttonPay.setOnClickListener{
                if (input.text.isNotEmpty()) {
                    try {
                        val amount: Double = input.text.toString().toDouble()
                        if (amount > 0) {

                            if (tips == "yes") {
                                payWithTip(instance, btcpayServer, btcpayStoreId, btcpayApiKey,
                                    lnbitsServer, lnbitsInvoiceKey, lnbitsLnWalletId,
                                    lnbitsOnChainWalletId, amount, merchantName, currency)
                            } else {
                                goPayment(instance, btcpayServer, btcpayStoreId, btcpayApiKey,
                                    lnbitsServer, lnbitsInvoiceKey, lnbitsLnWalletId,
                                    lnbitsOnChainWalletId, amount, merchantName, currency)
                            }
                        }
                    } catch (e: Exception) {
                        input.text = "Error"
                        input.setTextColor(ContextCompat.getColor(this, R.color.red))
                    }

                }
            }

        }

    }




//    More useful functions
    private fun addToInputText(buttonValue: String, input: TextView): String {
        return "${input.text}$buttonValue"
    }

    private fun checkSettings(currentInstance: String, btcpayServer: String,
                              btcpayStoreId: String, btcpayApiKey: String, lnbitsServer: String, lnbitsInvoiceKey: String,
                              lnbitsLnWalletId: String, budaUserName: String, budaApiKey: String, budaApiSecret: String,
                              binanceApiKey: String, binanceApiSecret: String, bitarooApiKey: String): Boolean {
        return when (currentInstance) {
            "BTCPay" -> !(btcpayServer == "" || btcpayStoreId == "")
            "BTCPay API" -> !(btcpayServer == "" || btcpayStoreId == "" || btcpayApiKey == "")
            "LNBits API" -> !(lnbitsServer == "" || lnbitsInvoiceKey == "" || lnbitsLnWalletId == "")
            "Buda Link" -> budaUserName.isNotEmpty()
            "Buda API" -> !(budaApiKey == "" || budaApiSecret == "")
            "Binance API" -> !(binanceApiKey == "" || binanceApiSecret == "")
            "Bitaroo" -> bitarooApiKey.isNotEmpty()
            else -> false
        }
    }

    private fun payWithTip(instance: String, btcpayServer: String, btcpayStoreId: String, btcpayApiKey: String,
                           lnbitsServer: String, lnbitsInvoiceKey: String, lnbitsLnWalletId: String,
                           lnbitsOnChainWalletId: String, amount: Double, merchantName: String,
                           currency: String) {
        val noTip = R.string.no_tip
        var tipValue: Double
        val tipString = getString(noTip)
        val tipMessage = R.string.tip_message
        val items = arrayOf(tipString, "5%", "10%", "15%", "20%")
        val builder = AlertDialog.Builder(this)
        builder.setTitle(tipMessage)
        builder.setItems(items) { _, which->
            tipValue = when (items[which]) {
                "5%" -> 1.05
                "10%" -> 1.1
                "15%" -> 1.15
                "20%" -> 1.2
                else -> {
                    1.0
                }
            }
            goPayment(instance, btcpayServer, btcpayStoreId, btcpayApiKey,
                lnbitsServer, lnbitsInvoiceKey, lnbitsLnWalletId,
                lnbitsOnChainWalletId, amount*tipValue, merchantName, currency)
        }
        builder.show()
    }

    private fun goPayment(instance: String, btcpayServer: String, btcpayStoreId: String, btcpayApiKey: String,
                          lnbitsServer: String, lnbitsInvoiceKey: String, lnbitsLnWalletId: String,
                          lnbitsOnChainWalletId: String, amount: Double, merchantName: String,
                          currency: String) {
        Toast.makeText(this, getString(R.string.loading_message), Toast.LENGTH_SHORT).show()

        if (instance == "BTCPay") {
            val urlBtcpay = "${btcpayServer}/api/v1/invoices?storeId=${btcpayStoreId}&price=${amount}&checkoutDesc=${merchantName}&currency=${currency}"
            startActivity(Intent.parseUri(urlBtcpay, 0))
        }

        if (instance == "BTCPay API") {
            val corr1 = CoroutineScope(Dispatchers.IO)
            corr1.launch {
                val link = payBTCPay(
                    amount.toString(), currency,
                    btcpayStoreId, btcpayApiKey, btcpayServer
                )
                withContext(Dispatchers.Main) {
                    if (link != "Error") {
                        startActivity(Intent.parseUri(link, 0), null)
                    } else {
                        Toast.makeText(baseContext, "Error API BTCPay", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        if (instance == "LNBits API") {
            payLNBits(currency, amount, lnbitsLnWalletId,
                lnbitsOnChainWalletId, merchantName,
                "$lnbitsServer/satspay/", lnbitsInvoiceKey,
                baseContext)
        }
        if (instance == "Buda Link") {
            val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.apply{
                putString("PRICE", amount.toString())
            }.apply()
            val intent = Intent(this, BudaPay::class.java)
            startActivity(intent)
        }

        if (instance == "Buda API") {
            val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.apply{
                putString("PRICE", amount.toString())
            }.apply()
            val intent = Intent(this, BudaPayApi::class.java)
            startActivity(intent)
        }

//////////////////////////////////////////////////
//        New Binance API integration

        if (instance == "Binance API") {
            val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.apply{
                putString("PRICE", amount.toString())
            }.apply()
            val intent = Intent(this, BinancePayApi::class.java)
            startActivity(intent)
        }

//////////////////////////////////////////////////

        if (instance == "Bitaroo") {
            val sharedPreferences : SharedPreferences = getSharedPreferences("sharedPres", Context.MODE_PRIVATE)
            val editor : SharedPreferences.Editor = sharedPreferences.edit()
            editor.apply{
                putString("PRICE", amount.toString())
            }.apply()
            val intent = Intent(this, BitarooPay::class.java)
            startActivity(intent)
        }



    }
}