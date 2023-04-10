package cl.icripto.icriptopos

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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

        val defaultMoneda = "CLP"
        val defaultMerchantName = "Restaurant A"
        val defaultBtcpayServer = ""
        val defaultBtcpayStoreId = ""
        val defaultTips = "no"
        val defaultPin = ""
        val defaultLnbitsServer = ""
        val invoiceKeyLnbits = ""



    }
}