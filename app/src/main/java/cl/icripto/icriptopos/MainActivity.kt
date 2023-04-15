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

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
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
        val defaultLnbitsServer = ""
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
        val btcpayServer= sharedPreferences.getString("BTCPAYSERVER", defaultBtcpayServer).toString()
        val lnbitsServer= sharedPreferences.getString("LNBITSSERVER", defaultLnbitsServer).toString()
        val btcpayStoreId = sharedPreferences.getString("BTCPAYSTORE", defaultBtcpayStoreId).toString()
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
                    Toast.makeText(this, getString(R.string.saved_pin), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ActividadAjustes::class.java)
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
                        val intent = Intent(this, ActividadAjustes::class.java)
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

        if (!checkSettings(instance, btcpayServer, btcpayStoreId, lnbitsServer, lnbitsInvoiceKey, lnbitsLnWalletId)) {
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
                        val price: Double = input.text.toString().toDouble()
                        if (price > 0) {

                            if (tips == "yes") {
                                payWithTip(instance, btcpayServer, btcpayStoreId,
                                    lnbitsServer, lnbitsInvoiceKey, lnbitsLnWalletId,
                                    lnbitsOnChainWalletId, price, merchantName, currency)
                            } else {
                                goPayment(instance, btcpayServer, btcpayStoreId,
                                    lnbitsServer, lnbitsInvoiceKey, lnbitsLnWalletId,
                                    lnbitsOnChainWalletId, price, merchantName, currency)
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
                              btcpayStoreId: String, lnbitsServer: String, lnbitsInvoiceKey: String,
                              lnbitsLnWalletId: String): Boolean {
        return when (currentInstance) {
            "BTCPay" -> !(btcpayServer == "" || btcpayStoreId == "")
            "LNBits" -> !(lnbitsServer == "" || lnbitsInvoiceKey == "" || lnbitsLnWalletId == "")
            else -> false
        }
    }

    private fun payWithTip(instance: String, btcpayServer: String, btcpayStoreId: String,
                           lnbitsServer: String, lnbitsInvoiceKey: String, lnbitsLnWalletId: String,
                           lnbitsOnChainWalletId: String, price: Double, merchantName: String, currency: String) {
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
            goPayment(instance, btcpayServer, btcpayStoreId,
                lnbitsServer, lnbitsInvoiceKey, lnbitsLnWalletId,
                lnbitsOnChainWalletId, price*tipValue, merchantName, currency)
        }
        builder.show()
    }

    private fun goPayment(instance: String, btcpayServer: String, btcpayStoreId: String,
                          lnbitsServer: String, lnbitsInvoiceKey: String, lnbitsLnWalletId: String,
                          lnbitsOnChainWalletId: String, price: Double, merchantName: String, currency: String) {
        if (instance == "BTCPay") {
            val urlIcripto = "${btcpayServer}/api/v1/invoices?storeId=${btcpayStoreId}&price=${price}&checkoutDesc=${merchantName}&currency=${currency}"
            startActivity(Intent.parseUri(urlIcripto, 0))
        } else {




            val urlIcripto = "${btcpayServer}/api/v1/invoices?storeId=${btcpayStoreId}&price=${price}&checkoutDesc=${merchantName}&currency=${currency}"
            startActivity(Intent.parseUri(urlIcripto, 0))
        }

    }

}