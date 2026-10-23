package com.nowsilence.app.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nowsilence.app.presentation.theme.NowSilenceTheme

data class DigestItem(
    val appName: String,
    val title: String,
    val time: String
)

data class DigestCategory(
    val name: String,
    val icon: ImageVector,
    val summary: String,
    val items: List<DigestItem>
)

private val sampleCategories = listOf(
    DigestCategory(
        name = "Mensajes Urgentes",
        icon = Icons.Filled.Notifications,
        summary = "2 notificaciones priorizadas por la IA",
        items = listOf(
            DigestItem("WhatsApp", "Ana: ¿Pudiste revisar el documento?", "09:41"),
            DigestItem("Slack", "Deploy en producción completado", "10:15")
        )
    ),
    DigestCategory(
        name = "Social",
        icon = Icons.Filled.Favorite,
        summary = "14 notificaciones agrupadas silenciosamente",
        items = listOf(
            DigestItem("Instagram", "3 nuevos me gusta en tu foto", "11:02"),
            DigestItem("TikTok", "maria_te ha mencionado", "11:45"),
            DigestItem("X", "5 nuevas interacciones", "12:30")
        )
    ),
    DigestCategory(
        name = "Promociones",
        icon = Icons.Filled.ShoppingCart,
        summary = "23 notificaciones bloqueadas",
        items = listOf(
            DigestItem("Amazon", "¡Oferta flash! 40% en electrónicos", "08:20"),
            DigestItem("Booking", "Precios bajaron en tu destino", "13:10")
        )
    ),
    DigestCategory(
        name = "Marketing",
        icon = Icons.Filled.Campaign,
        summary = "9 notificaciones bloqueadas",
        items = listOf(
            DigestItem("Spotify", "Nueva playlist para ti", "07:55"),
            DigestItem("Netflix", "Nuevos episodios disponibles", "16:22")
        )
    )
)

@Composable
fun DigestScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Resumen del día",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleCategories) { category ->
                ExpandableCategoryCard(category = category)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ExpandableCategoryCard(category: DigestCategory) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = { isExpanded = !isExpanded }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = category.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Colapsar" else "Expandir",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(if (isExpanded) 180f else 0f),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    category.items.forEach { item ->
                        DigestItemRow(item = item)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun DigestItemRow(item: DigestItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.appName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = item.time,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DigestScreenPreview() {
    NowSilenceTheme {
        DigestScreen()
    }
}
