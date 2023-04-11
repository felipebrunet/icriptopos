package cl.icripto.icriptopos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_IcriptoPOS)
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)


//        Constants associated to the keypad and other buttons
        val input: TextView = findViewById(R.id.input)
        val buttonBotondepago: Button = findViewById(R.id.pay_button)
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
        val buttonBorrar: Button = findViewById(R.id.button_delete)
        val adjustScreenButton = findViewById<Button>(R.id.settings_button)

//        Default value for business constants
        val defaultInstance = "BTCPay"
        val defaultCurrency = "CLP"
        val defaultMerchantName = "Restaurant A"
        val defaultServer = ""
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
        val server = sharedPreferences.getString("SERVER", defaultServer).toString()
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
        findViewById<TextView>(R.id.merchant_name).text = merchantName

        if (defaultInstance == "BTCPay") {





        }

    }

    private fun addToInputText(buttonValue: String, input: TextView): String {
        return "${input.text}$buttonValue"
    }
}