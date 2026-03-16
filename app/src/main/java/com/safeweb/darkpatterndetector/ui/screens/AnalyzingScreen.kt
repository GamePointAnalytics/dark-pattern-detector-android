package com.safeweb.darkpatterndetector.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.safeweb.darkpatterndetector.ui.theme.SafeGreen

@Composable
fun AnalyzingScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            color = SafeGreen,
            strokeWidth = 6.dp
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Analyzing Screenshot",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Gemini Nano is scanning for deceptive patterns...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Progress indicators
        AnalysisStep(text = "Loading on-device model", isCompleted = true)
        AnalysisStep(text = "Scanning visual elements", isCompleted = true)
        AnalysisStep(text = "Identifying dark patterns", isCompleted = false)
    }
}

@Composable
private fun AnalysisStep(text: String, isCompleted: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    if (isCompleted) SafeGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isCompleted) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isCompleted) FontWeight.Medium else FontWeight.Normal
        )
    }
}
