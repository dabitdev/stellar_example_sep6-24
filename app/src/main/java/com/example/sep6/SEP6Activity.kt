package com.example.sep6

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class SEP6Activity : AppCompatActivity() {
    companion object {
        private const val INTENT_SEP6_DATA = "INTENT_SEP6_DATA"
        fun newIntent(context: Context, deposit:DepositSEP6) : Intent {
            val intent = Intent(context, SEP6Activity::class.java)
            intent.putExtra(INTENT_SEP6_DATA, deposit)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sep6)

        val imageView = findViewById<ImageView>(R.id.qrCodeView)
        val text = findViewById<TextView>(R.id.depositInfo)

        Timber.plant(Timber.DebugTree())
        intent?.let {
            it.getSerializableExtra(INTENT_SEP6_DATA)?.let { data ->
                val info = data as DepositSEP6
                generateQRCode(info.how, imageView, 300)
                text.text = "Please deposit a min amount of ${info.min_amount} ${info.asset} " +
                        "to the above address. The fees are calculated in the following manner: " +
                        "fix fee ${info.fee_fixed} ${info.asset} and fee percent of ${info.fee_percent}. Max amount is ${info.max_amount} ${info.asset}."

                supportActionBar?.let { bar ->
                    bar.title = "Deposit ${info.asset} (SEP6)"
                }
            }
        }
    }

    private fun generateQRCode(data: String, imageView: ImageView, size: Int) {
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, size, size)
        imageView.setImageBitmap(bitmap)
    }
}
