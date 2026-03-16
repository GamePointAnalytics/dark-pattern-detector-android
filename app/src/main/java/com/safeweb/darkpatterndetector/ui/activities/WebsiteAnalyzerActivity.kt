package com.safeweb.darkpatterndetector.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.safeweb.darkpatterndetector.AnalysisViewModel
import com.safeweb.darkpatterndetector.R
import com.safeweb.darkpatterndetector.ui.screens.ResultsScreen

/**
 * Activity that loads a URL in a WebView and extracts its text for analysis.
 */
class WebsiteAnalyzerActivity : ComponentActivity() {

    private val viewModel: AnalysisViewModel by viewModels()
    private var targetUrl by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle URL from intent (e.g., from Share menu)
        handleIntent(intent)

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when (val state = uiState) {
                        is AnalysisViewModel.UiState.Results -> {
                            ResultsScreen(
                                result = state.result,
                                screenshotBitmap = state.screenshotBitmap,
                                onDismiss = { 
                                    viewModel.reset()
                                    finish() 
                                }
                            )
                        }
                        is AnalysisViewModel.UiState.Analyzing -> {
                            WebsiteProgressScreen(url = targetUrl, isLoading = true)
                        }
                        is AnalysisViewModel.UiState.Error -> {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(24.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                            ) {
                                Text(state.message, color = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { finish() }) {
                                    Text(stringResource(R.string.back_button))
                                }
                            }
                        }
                        else -> {
                            WebsiteProgressScreen(url = targetUrl, isLoading = false)
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!sharedText.isNullOrBlank() && (sharedText.startsWith("http://") || sharedText.startsWith("https://"))) {
                targetUrl = sharedText
                extractTextFromUrl(targetUrl)
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun extractTextFromUrl(url: String) {
        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        
        // Inject an interface to receive the extracted text back to Android/Kotlin
        webView.addJavascriptInterface(WebAppInterface { extractedText ->
            // Use the ViewModel to analyze, which handles session-level AI availability
            viewModel.analyzeWebsite(this, extractedText)
        }, "AndroidTextExtractor")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Execute JS to grab all visible text from the body and pass it to our interface
                webView.evaluateJavascript(
                    "(function() { AndroidTextExtractor.processDOM(document.body.innerText); })();",
                    null
                )
            }
        }
        
        // Load the given URL
        webView.loadUrl(url)
    }
}

/**
 * Interface injected into the WebView.
 */
class WebAppInterface(private val onTextExtracted: (String) -> Unit) {
    @JavascriptInterface
    @Suppress("unused")
    fun processDOM(text: String) {
        onTextExtracted(text)
    }
}

@Composable
fun WebsiteProgressScreen(url: String, isLoading: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.analyzing_website))
            Spacer(modifier = Modifier.height(8.dp))
            Text(url, style = MaterialTheme.typography.bodySmall)
        } else {
            Text(stringResource(R.string.no_url_waiting))
        }
    }
}
