package com.example.sep6

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber
import android.webkit.WebViewClient

class WebViewActivity : AppCompatActivity() {
    companion object {
        private const val INTENT_ARGUMENT_URL = "INTENT_ARGUMENT_URL"
        fun newIntent(context: Context, url:String) : Intent {
            val intent = Intent(context,WebViewActivity::class.java)
            intent.putExtra(INTENT_ARGUMENT_URL, url)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        Timber.plant(Timber.DebugTree())
        supportActionBar?.let {
            it.title = "SEP24"
        }
        val webView = findViewById<WebView>(R.id.webView)
        webView.webViewClient = WebViewClient()
        intent?.let {
            it.getStringExtra(INTENT_ARGUMENT_URL)?.let { url ->
                webView.loadUrl(url)
            }
        }
    }
}
