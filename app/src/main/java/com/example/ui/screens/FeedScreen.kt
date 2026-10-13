package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DemoBanner
import com.example.ui.components.QuickFilterRow
import com.example.ui.components.TenderCard
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.TenderWithMatch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    uiState: MainUiState,
    onSearchChange: (String) -> Unit,
    onQuickFilter: (String) -> Unit,
    onOpenFilterSheet: () -> Unit,
    onSelectTender: (TenderWithMatch) -> Unit,
    onToggleSave: (String) -> Unit,
    onResetFilters: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeFilterCount = listOf(
        uiState.filter.city.isNotBlank(),
        uiState.filter.canton.isNotBlank(),
        uiState.filter.category.isNotBlank(),
        uiState.filter.minBudget != null,
        uiState.filter.maxBudget != null,
        uiState.filter.minMatchScore != null,
        uiState.filter.onlyMatches,
        uiState.filter.onlyUrgentDeadline,
        uiState.filter.onlyHighValue,
        uiState.filter.onlyZenicaZdk
    ).count { it }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("feed_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.filter.query,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("global_search_input"),
                    placeholder = {
                        Text("Pretraži tendere, grad, CPV...", fontSize = 13.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Pretraga",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (uiState.filter.query.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Očisti",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                BadgedBox(
                    badge = {
                        if (activeFilterCount > 0) {
                            Badge { Text("$activeFilterCount") }
                        }
                    }
                ) {
                    IconButton(
                        onClick = onOpenFilterSheet,
                        modifier = Modifier.testTag("open_filters_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filteri",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val currentQuick = when {
                uiState.filter.onlyMatches -> "MATCHES"
                uiState.filter.onlyUrgentDeadline -> "URGENT"
                uiState.filter.onlyHighValue -> "HIGH_VALUE"
                uiState.filter.onlyZenicaZdk -> "ZENICA_ZDK"
                else -> "ALL"
            }

            QuickFilterRow(
                selectedFilter = currentQuick,
                onFilterSelected = onQuickFilter
            )
        }

        if (uiState.isLoading && uiState.filteredTenders.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = onRefresh,
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.filteredTenders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Nema rezultata",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Povuci nadole da osvježiš tendere ili poništi filtere.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedButton(onClick = onResetFilters) {
                                    Text("Poništi filtere")
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Pronađeno: ${uiState.filteredTenders.size} tendera",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Povuci nadole za osvježenje",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                if (uiState.showDemoBanner) DemoBanner()
                            }
                        }

                        items(uiState.filteredTenders, key = { it.tender.id }) { item ->
                            TenderCard(
                                item = item,
                                onDetailsClick = { onSelectTender(item) },
                                onSaveToggle = { onToggleSave(item.tender.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
