package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompanyProfile
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.NavyDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    currentProfile: CompanyProfile,
    onSaveProfile: (CompanyProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    var companyName by remember(currentProfile) { mutableStateOf(currentProfile.companyName) }
    var activity by remember(currentProfile) { mutableStateOf(currentProfile.activity) }
    var city by remember(currentProfile) { mutableStateOf(currentProfile.city) }
    var canton by remember(currentProfile) { mutableStateOf(currentProfile.canton) }
    var keywordsText by remember(currentProfile) { mutableStateOf(currentProfile.keywords.joinToString(", ")) }
    var cpvText by remember(currentProfile) { mutableStateOf(currentProfile.cpvCategories.joinToString(", ")) }
    var minBudget by remember(currentProfile) { mutableStateOf(currentProfile.minBudgetKm.toLong().toString()) }
    var maxBudget by remember(currentProfile) { mutableStateOf(currentProfile.maxBudgetKm.toLong().toString()) }
    var savedSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "PROFIL VAŠE FIRME",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanAccent,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Prilagodi TenderRadar algoritam",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Uneseni podaci određuju Match Score za svaki tender (ključne riječi, CPV, lokacija, raspon vrijednosti).",
                        fontSize = 12.sp,
                        color = androidx.compose.ui.graphics.Color(0xFF94A3B8)
                    )
                }
            }
        }

        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "OSNOVNI PODACI O PRAVNOM LICU",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = { Text("Naziv firme") },
                        placeholder = { Text("npr. ABC Elektro d.o.o.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_company_name"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = activity,
                        onValueChange = { activity = it },
                        label = { Text("Glavna djelatnost") },
                        placeholder = { Text("npr. Elektroinstalacije i inženjering") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_activity"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { city = it },
                            label = { Text("Grad") },
                            placeholder = { Text("Zenica") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_city"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = canton,
                            onValueChange = { canton = it },
                            label = { Text("Kanton") },
                            placeholder = { Text("ZDK") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_canton"),
                            singleLine = true
                        )
                    }
                }
            }
        }

        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PARAMETRI MATCHING ALGORITMA",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = keywordsText,
                        onValueChange = { keywordsText = it },
                        label = { Text("Ključne riječi (odvojene zarezom)") },
                        placeholder = { Text("elektroinstalacije, elektromaterijal, rasvjeta...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_keywords"),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = cpvText,
                        onValueChange = { cpvText = it },
                        label = { Text("CPV kodovi djelatnosti (odvojeni zarezom)") },
                        placeholder = { Text("45310000, 31682000, 31527200...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_cpv"),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Željeni raspon vrijednosti tendera (KM):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = minBudget,
                            onValueChange = { minBudget = it },
                            label = { Text("Min. vrijednost (KM)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_min_budget"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = maxBudget,
                            onValueChange = { maxBudget = it },
                            label = { Text("Maks. vrijednost (KM)") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_max_budget"),
                            singleLine = true
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    val kws = keywordsText.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    val cpvs = cpvText.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    val minVal = minBudget.toDoubleOrNull() ?: 10000.0
                    val maxVal = maxBudget.toDoubleOrNull() ?: 500000.0

                    val updated = currentProfile.copy(
                        companyName = companyName.ifBlank { "ABC Elektro d.o.o." },
                        activity = activity.ifBlank { "Elektroinstalacije" },
                        city = city.ifBlank { "Zenica" },
                        canton = canton.ifBlank { "ZDK" },
                        keywords = if (kws.isNotEmpty()) kws else listOf("elektroinstalacije"),
                        cpvCategories = if (cpvs.isNotEmpty()) cpvs else listOf("45310000"),
                        minBudgetKm = minVal,
                        maxBudgetKm = maxVal,
                        isConfigured = true
                    )
                    onSaveProfile(updated)
                    savedSuccess = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("save_profile_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "SAČUVAJ PROFIL I PRERAČUNAJ MATCHEVE", fontWeight = FontWeight.Bold)
            }

            if (savedSuccess) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✓ Profil je uspješno spremljen u lokalnu bazu podataka!",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}
