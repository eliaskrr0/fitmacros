package com.eliaskrr.fitmacros.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@Composable
fun ProfileScreen(viewModel: ProfileViewModel, onEditClick: () -> Unit) {
    val userData by viewModel.userData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Avatar de Perfil",
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (userData.nombre.isNotBlank()) userData.nombre else "Usuario",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                val age = if (userData.fechaNacimiento.isNotBlank()) {
                    try {
                        val formatter = DateTimeFormatter.ofPattern("ddMMyyyy")
                        val birthDate = LocalDate.parse(userData.fechaNacimiento, formatter)
                        Period.between(birthDate, LocalDate.now()).years.toString()
                    } catch (e: Exception) {
                        "-"
                    }
                } else {
                    "-"
                }
                ProfileDataRow(label = "Edad", value = "$age años")
                ProfileDataRow(label = "Sexo", value = userData.sexo.ifBlank { "-" })

                Spacer(modifier = Modifier.height(16.dp))

                ProfileDataRow(label = "Altura", value = "${userData.altura.ifBlank { "-" }} cm")
                ProfileDataRow(label = "Peso", value = "${userData.peso.ifBlank { "-" }} kg")

                Spacer(modifier = Modifier.height(16.dp))

                ProfileDataRow(label = "Objetivo", value = userData.objetivo.ifBlank { "-" })

                Spacer(modifier = Modifier.height(16.dp))

                ProfileDataRow(label = "Nivel de Actividad", value = userData.activityRate.ifBlank { "-" })
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = onEditClick, modifier = Modifier.fillMaxWidth()) {
            Text("Editar Datos")
        }
    }
}

@Composable
fun ProfileDataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Text(text = value, fontSize = 16.sp)
    }
}
