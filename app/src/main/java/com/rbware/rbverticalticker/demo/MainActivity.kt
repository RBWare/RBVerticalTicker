package com.rbware.rbverticalticker.demo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rbware.rbverticalticker.VerticalTicker
import com.rbware.rbverticalticker.rememberVerticalTickerState

private val sampleHeadlines = listOf(
    "RBVerticalTicker is live",
    "Swipe-free, auto-advancing headlines",
    "Built with Jetpack Compose",
    "Drop it into any screen",
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoTheme {
                DemoScreen()
            }
        }
    }
}

@Composable
private fun DemoTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}

@Composable
private fun DemoScreen() {
    Scaffold { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "RBVerticalTicker demo",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "Advance the ticker below with the buttons.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
                )
                val tickerState = rememberVerticalTickerState(sampleHeadlines)
                VerticalTicker(state = tickerState, visibleCount = 3, topFadeAlpha = 0.15f)
                Row(
                    modifier = Modifier.padding(top = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(onClick = { tickerState.showNext() }) {
                        Text("Next")
                    }
                    OutlinedButton(onClick = { tickerState.showNext("Breaking: manual entry injected!") }) {
                        Text("Inject alert")
                    }
                }
                val context = LocalContext.current
                OutlinedButton(
                    onClick = { context.startActivity(Intent(context, XmlDemoActivity::class.java)) },
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    Text("XML/Java demo")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DemoScreenPreview() {
    DemoTheme {
        DemoScreen()
    }
}
