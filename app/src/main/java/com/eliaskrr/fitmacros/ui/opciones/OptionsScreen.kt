package com.eliaskrr.fitmacros.ui.opciones

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.eliaskrr.fitmacros.ui.navigation.AppScreen
import com.eliaskrr.fitmacros.ui.theme.BackgroundCard

private data class Option(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun OptionsScreen(navController: NavController) {
    val options = listOf(
        Option(
            title = "Gestionar Perfil",
            icon = Icons.Default.AccountCircle,
            onClick = { navController.navigate(AppScreen.Profile.route) }
        ),
        Option(
            title = "Notificaciones y Recordatorios",
            icon = Icons.Default.Notifications,
            onClick = { navController.navigate(AppScreen.Notifications.route) }
        ),
        Option(
            title = "Acerca de",
            icon = Icons.Default.Info,
            onClick = { navController.navigate(AppScreen.About.route) }
        ),
        Option(
            title = "Exportar Datos",
            icon = Icons.Default.UploadFile,
            onClick = { navController.navigate(AppScreen.Export.route) }
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(options) { option ->
            OptionItem(option = option)
        }
    }
}

@Composable
private fun OptionItem(option: Option) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = option.onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.title,
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = option.title, modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null
            )
        }
    }
}