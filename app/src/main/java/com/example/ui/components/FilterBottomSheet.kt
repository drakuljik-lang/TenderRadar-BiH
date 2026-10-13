package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.TenderFilter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    currentFilter: TenderFilter,
    onApplyFilter: (TenderFilter) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var city by remember(currentFilter) { mutableStateOf(currentFilter.city) }
    var canton by remember(currentFilter) { mutableStateOf(currentFilter.canton) }
    var entity by remember(currentFilter) { mutableStateOf(currentFilter.entity) }
    var category by remember(currentFilter) { mutableStateOf(currentFilter.category) }
    var cpv by remember(currentFilter) { mutableStateOf(currentFilter.cpv) }
    var minBudget by remember(currentFilter) { mutableStateOf(currentFilter.minBudget?.toLong()?.toString() ?: "") }
    var maxBudget by remember(currentFilter) { mutableStateOf(currentFilter.maxBudget?.toLong()?.toString() ?: "") }
    var minScore by remember(currentFilter) { mutableStateOf(currentFilter.minMatchScore?.toFloat() ?: 0f) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.testTag("filter_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filteri javnih nabavki",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedButton(
                    onClick = onReset,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Poništi", fontSize = 12.sp)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Gradovi (quick select)
            Text("Grad:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            val popularCities = listOf("Zenica", "Sarajevo", "Tuzla", "Mostar", "Bihać", "Doboj", "Kakanj")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                popularCities.forEach { c ->
                    FilterChip(
                        selected = city.equals(c, ignoreCase = true),
                        onClick = { city = if (city.equals(c, ignoreCase = true)) "" else c },
                        label = { Text(c, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Kantoni
            Text("Kanton / Regija:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            val cantons = listOf("ZDK", "Kanton Sarajevo", "Tuzlanski", "USK", "HNK")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                cantons.forEach { k ->
                    FilterChip(
                        selected = canton.contains(k, ignoreCase = true),
                        onClick = { canton = if (canton.contains(k, ignoreCase = true)) "" else k },
                        label = { Text(k, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Kategorija
            Text("Kategorija nabavke:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            val categories = listOf("Elektroinstalacije", "Građevina", "IT", "Medicinska oprema", "Kancelarijski materijal", "Zaštita", "Transport", "Održavanje", "Hrana", "Javne usluge")
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = category.equals(cat, ignoreCase = true),
                        onClick = { category = if (category.equals(cat, ignoreCase = true)) "" else cat },
                        label = { Text(cat, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Minimalni Match Score Slider
            Text(
                text = "Minimalni Match Score: ${minScore.toInt()}%",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Slider(
                value = minScore,
                onValueChange = { minScore = it },
                valueRange = 0f..100f,
                steps = 9
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Raspon vrijednosti (KM)
            Text("Raspon procijenjene vrijednosti (KM):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = minBudget,
                    onValueChange = { minBudget = it },
                    label = { Text("Min. KM") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = maxBudget,
                    onValueChange = { maxBudget = it },
                    label = { Text("Maks. KM") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val updated = currentFilter.copy(
                        city = city,
                        canton = canton,
                        entity = entity,
                        category = category,
                        cpv = cpv,
                        minBudget = minBudget.toDoubleOrNull(),
                        maxBudget = maxBudget.toDoubleOrNull(),
                        minMatchScore = if (minScore > 0f) minScore.toInt() else null
                    )
                    onApplyFilter(updated)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apply_filters_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("PRIMIJENI FILTERE", fontWeight = FontWeight.Bold)
            }
        }
    }
}
