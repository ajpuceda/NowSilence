package com.nowsilence.app.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nowsilence.app.presentation.theme.NowSilenceTheme

enum class UsagePlan(val label: String, val description: String) {
    PERSONAL("Particular", "Bienestar y reducción de estrés. Filtra para protegerte del ruido digital."),
    ENTERPRISE("Empresarial", "Productividad. Prioriza lo relevante e integra tus herramientas de trabajo.")
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    var selectedPlan by remember { mutableStateOf(UsagePlan.PERSONAL) }
    var apiKey by remember { mutableStateOf("") }
    var isApiKeyVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(top = 16.dp)
        )

        PlanSelectorSection(
            selected = selectedPlan,
            onSelect = { selectedPlan = it }
        )

        AdvancedSection(
            apiKey = apiKey,
            onApiKeyChange = { apiKey = it },
            isApiKeyVisible = isApiKeyVisible,
            onToggleVisibility = { isApiKeyVisible = !isApiKeyVisible }
        )

        Text(
            text = "NowSilence v0.1.0 · Toda la IA funciona en tu dispositivo. Ningún dato sale de tu teléfono.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Composable
private fun PlanSelectorSection(
    selected: UsagePlan,
    onSelect: (UsagePlan) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Plan de uso",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            UsagePlan.entries.forEachIndexed { index, plan ->
                SegmentedButton(
                    selected = selected == plan,
                    onClick = { onSelect(plan) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = UsagePlan.entries.size
                    ),
                    icon = {
                        Icon(
                            imageVector = if (plan == UsagePlan.PERSONAL) Icons.Filled.Person
                            else Icons.Filled.Business,
                            contentDescription = plan.label,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    label = { Text(plan.label) }
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = selected.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Composable
private fun AdvancedSection(
    apiKey: String,
    onApiKeyChange: (String) -> Unit,
    isApiKeyVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Avanzado",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = apiKey,
            onValueChange = onApiKeyChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("API Key externa (opcional)") },
            placeholder = { Text("sk-...") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            visualTransformation = if (isApiKeyVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (isApiKeyVisible) Icons.Filled.VisibilityOff
                        else Icons.Filled.Visibility,
                        contentDescription = if (isApiKeyVisible) "Ocultar API Key" else "Mostrar API Key"
                    )
                }
            },
            supportingText = {
                Text(
                    text = "Para usuarios avanzados: conecta servicios externos de inferencia. La IA local sigue funcionando sin esto.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    NowSilenceTheme {
        SettingsScreen()
    }
}
