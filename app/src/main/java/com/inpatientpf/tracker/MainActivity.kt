package com.inpatientpf.tracker

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.widget.Toast
import org.json.JSONObject

class MainActivity : Activity() {
    private lateinit var web: WebView
    private var backupPayload: String? = null
    private var reportPayload: String? = null
    private val csvCode = 103
    private val createCode = 101
    private val openCode = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        setContentView(web)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.allowFileAccess = true
        web.settings.allowContentAccess = false
        web.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean = !request.url.toString().startsWith("file:///android_asset/")
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                val u = request.url.toString()
                return if (!u.startsWith("file:///android_asset/")) WebResourceResponse("text/plain", "UTF-8", 403, "Blocked", emptyMap(), java.io.ByteArrayInputStream(ByteArray(0))) else null
            }
        }
        web.addJavascriptInterface(BackupBridge(), "AndroidBackup")
        web.addJavascriptInterface(ReportBridge(), "AndroidReports")
        web.loadUrl("file:///android_asset/index.html")
    }

    inner class BackupBridge {
        @JavascriptInterface fun exportData(json: String) {
            runOnUiThread {
                backupPayload = json
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                    putExtra(Intent.EXTRA_TITLE, "inpatient-pf-backup-${java.time.LocalDate.now()}.json")
                }
                startActivityForResult(intent, createCode)
            }
        }
        @JavascriptInterface fun importData() {
            runOnUiThread {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                }
                startActivityForResult(intent, openCode)
            }
        }
    }

    inner class ReportBridge {
        @JavascriptInterface fun exportCsv(filename: String, csv: String) {
            runOnUiThread {
                reportPayload = csv
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/csv"
                    putExtra(Intent.EXTRA_TITLE, filename)
                }
                startActivityForResult(intent, csvCode)
            }
        }
    }

    @Deprecated("Deprecated in Android")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        val uri: Uri = data?.data ?: return
        try {
            if (requestCode == createCode) {
                val json = backupPayload ?: return
                contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray(Charsets.UTF_8)) }
                    ?: error("Cannot open backup destination")
                backupPayload = null
                Toast.makeText(this, "Backup saved", Toast.LENGTH_LONG).show()
            } else if (requestCode == csvCode) {
                val csv = reportPayload ?: return
                contentResolver.openOutputStream(uri)?.use { it.write(csv.toByteArray(Charsets.UTF_8)) }
                    ?: error("Cannot open report destination")
                reportPayload = null
                Toast.makeText(this, "Monthly report saved", Toast.LENGTH_LONG).show()
            } else if (requestCode == openCode) {
                val json = contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                    ?: error("Cannot read backup")
                if (json.length > 15_000_000) error("Backup file too large")
                web.evaluateJavascript("window.restoreNativeBackup(" + JSONObject.quote(json) + ")", null)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Backup operation failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    override fun onBackPressed() {
        if (web.canGoBack()) web.goBack() else super.onBackPressed()
    }
}
