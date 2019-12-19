package com.example.sep6

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.sep6.Toml.checkTrailingSlash
import kotlinx.android.synthetic.main.activity_main.*
import shadow.com.google.gson.Gson
import timber.log.Timber
import java.lang.IllegalStateException

class MainActivity : AppCompatActivity() {
    private lateinit var transferServer : String
    private lateinit var depositUrl : String
    private lateinit var withDrawData : WithdrawData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        val textView = findViewById<TextView>(R.id.body)
        findViewById<View>(R.id.fetch).setOnClickListener {
            AsyncTask.execute {
                val toml = Toml.createForDomain(getAnchorDomain())
                transferServer = checkTrailingSlash(toml.getString("TRANSFER_SERVER"))
                val info = Toml.getTransferInfo(transferServer)
                runOnUiThread {
                    textView.text = info
                }
            }
        }

        getDepositAddress.setOnClickListener {
            AsyncTask.execute {
                val depositData = Toml.getDepositUrl(transferServer, getAsset(), getStellarAddress(), "garlic_74@hotmail.com")

                runOnUiThread {
                    val deposit = Gson().fromJson(depositData, DepositData::class.java)

                    if (deposit.url == null) {
                        Toast.makeText(this, "SEP24 not supported", Toast.LENGTH_SHORT).show()
                        textView.text = depositData
                        val depositSap6 = Gson().fromJson(depositData, DepositSEP6::class.java)
                        depositSap6.asset = getAsset()
                        startActivity(SEP6Activity.newIntent(it.context, depositSap6))

                    } else {
                        depositUrl = checkTrailingSlash(deposit.url)
                        depositUrl = deposit.url
                        textView.text = deposit.url

                        startActivity(WebViewActivity.newIntent(it.context, depositUrl))
                    }
                }
            }
        }

        withdraw.setOnClickListener {
            AsyncTask.execute {
                withDrawData = Toml.getWithdrawInfo(transferServer, getAsset(), getStellarAddress(), getDestinationAddress())
                runOnUiThread {
                    textView.text = withDrawData.toString()
                }
            }
        }

        startWithdraw.setOnClickListener {
            AsyncTask.execute {
                when(withDrawData.type) {
                    "non_interactive_customer_info_needed" -> {

                    }
                    "interactive_customer_info_needed" -> {
                        startActivity(WebViewActivity.newIntent(it.context, withDrawData.url))
                    } else -> {
                        throw IllegalStateException("not valid type:${withDrawData.type}")
                    }
                }
            }
        }

        clear.setOnClickListener {
            textView.text = null
        }
    }

    private fun getAnchorDomain() : String {
        return findViewById<EditText>(R.id.address).text.toString()
    }

    private fun getStellarAddress() : String {
        return findViewById<EditText>(R.id.stellarAddress).text.toString()
    }

    private fun getDestinationAddress(): String {
        return findViewById<EditText>(R.id.destAddress).text.toString()
    }

    private fun getAsset() : String {
        return findViewById<EditText>(R.id.asset).text.toString()
    }
}
