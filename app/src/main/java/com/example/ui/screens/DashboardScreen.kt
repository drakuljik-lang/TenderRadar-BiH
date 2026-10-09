package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DemoBanner
import com.example.ui.components.KpiCard
import com.example.ui.components.OfflineBanner
import com.example.ui.components.TenderCard
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.TenderWithMatch

@Composable
fun DashboardScreen(
    uiState: MainUiState,
    onNavigate: (Screen) -> Unit,
    onSelectTender: (TenderWithMatch) -> Unit,
    onToggleSave: (String) -> Unit,
    onOpenFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. BRAND HEADER
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_header_card"),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f, fill = false).padding(end = 8.dp)
                        ) {
                            Surface(
                                color = CyanAccent.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "ZENICA DIGITAL",
                                    color = CyanAccent,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Text(
                                text = uiState.companyProfile.companyName,
                                color = Color(0xFFE2E8F0),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "TENDERRADAR BiH",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Javne nabavke koje odgovaraju tvojoj firmi.",
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Profil: ${uiState.companyProfile.city} • ${uiState.companyProfile.activity}",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f).padding(end = 6.dp)
                        )

                        OutlinedButton(
                            onClick = { onNavigate(Screen.Profile) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "PROFIL",
                                color = CyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. DEMO BANNER & OFFLINE BANNER
        if (uiState.showDemoBanner) {
            item {
                DemoBanner()
            }
        }

        if (uiState.isOffline) {
            item {
                OfflineBanner()
            }
        }

        // 3. VELIKI KPI CARDS (2x2 Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "KLJUČNI POKAZATELJI",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Novi tenderi",
                        value = "${uiState.kpis.totalCount}",
                        subtitle = "ukupno u bazi",
                        icon = Icons.Default.Description,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Feed) }
                    )

                    KpiCard(
                        title = "Moji matching",
                        value = "${uiState.kpis.matchingCount}",
                        subtitle = "odgovaraju firmi",
                        icon = Icons.Default.CheckCircle,
                        containerColor = Color(0xFFE0F2FE),
                        contentColor = Color(0xFF0369A1),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Feed) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiCard(
                        title = "Rokovi uskoro",
                        value = "${uiState.kpis.urgentCount}",
                        subtitle = "rokova < 7 dana",
                        icon = Icons.Default.AccessTime,
                        containerColor = Color(0xFFFEF3C7),
                        contentColor = Color(0xFFB45309),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Feed) }
                    )

                    KpiCard(
                        title = "Spremljeni",
                        value = "${uiState.kpis.savedCount}",
                        subtitle = "u praćenju",
                        icon = Icons.Default.Bookmark,
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = Color(0xFF334155),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(Screen.Saved) }
                    )
                }
            }
        }

        // 4. TOP MATCHING TENDERI SECTION
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VISOKA PODUDARNOST ZA VAŠU FIRMU",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(
                    onClick = onOpenFilter,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Filteri",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Top 4 matching tenders
        val topMatches = uiState.tendersWithMatch.take(4)
        items(topMatches, key = { it.tender.id }) { item ->
            TenderCard(
                item = item,
                onDetailsClick = { onSelectTender(item) },
                onSaveToggle = { onToggleSave(item.tender.id) }
            )
        }

        // 5. VIEW ALL TENDERS ACTION
        item {
            Button(
                onClick = { onNavigate(Screen.Feed) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("view_all_tenders_button"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("PREGLEDAJ SVE TENDERE (${uiState.tendersWithMatch.size})")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
