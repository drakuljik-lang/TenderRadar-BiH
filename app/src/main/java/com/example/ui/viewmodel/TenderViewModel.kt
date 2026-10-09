package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.datasource.DemoData
import com.example.data.model.AiSummaryData
import com.example.data.model.CompanyProfile
import com.example.data.model.NotificationItem
import com.example.data.model.Tender
import com.example.data.model.TenderFilter
import com.example.data.model.TenderMatchExplanation
import com.example.data.repository.TenderRepository
import com.example.domain.ai.TenderAiSummarizer
import com.example.domain.matcher.MatchScoreCalculator
import com.example.domain.util.DeadlineHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class TenderWithMatch(
    val tender: Tender,
    val match: TenderMatchExplanation,
    val isSaved: Boolean
)

data class DashboardKpis(
    val totalCount: Int = 0,
    val matchingCount: Int = 0,
    val urgentCount: Int = 0,
    val savedCount: Int = 0
)

data class MainUiState(
    val tendersWithMatch: List<TenderWithMatch> = emptyList(),
    val filteredTenders: List<TenderWithMatch> = emptyList(),
    val companyProfile: CompanyProfile = DemoData.defaultCompanyProfile,
    val savedTenderIds: Set<String> = emptySet(),
    val notifications: List<NotificationItem> = emptyList(),
    val kpis: DashboardKpis = DashboardKpis(),
    val filter: TenderFilter = TenderFilter(),
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val errorMessage: String? = null,
    val activeTenderDetail: TenderWithMatch? = null,
    val activeAiSummary: AiSummaryData? = null,
    val isAiLoading: Boolean = false,
    val showFilterSheet: Boolean = false,
    val showProfileSheet: Boolean = false,
    val showPremiumSheet: Boolean = false
)

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Feed : Screen("feed")
    data object Saved : Screen("saved")
    data object Notifications : Screen("notifications")
    data object Profile : Screen("profile")
    data object Premium : Screen("premium")
    data object TenderDetail : Screen("tender_detail")
}

class TenderViewModel(
    private val repository: TenderRepository
) : ViewModel() {

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _filter = MutableStateFlow(TenderFilter())
    val filter: StateFlow<TenderFilter> = _filter.asStateFlow()

    private val _uiState = MutableStateFlow(MainUiState(isLoading = true))
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                repository.getAllTenders(),
                repository.getCompanyProfile(),
                repository.getSavedTenderIds(),
                repository.getNotifications(),
                _filter
            ) { tenders, profile, savedIds, notifications, filterState ->
                // Ako postoje PRAVI tenderi (isDemo=false), prikaži samo njih.
                // Ako su svi DEMO, prikaži DEMO (fallback za offline).
                val hasRealTenders = tenders.any { !it.isDemo }
                val visibleTenders = if (hasRealTenders) {
                    tenders.filter { !it.isDemo }
                } else {
                    tenders
                }

                // Compute matches for all tenders
                val listWithMatches = visibleTenders.map { tender ->
                    val match = MatchScoreCalculator.calculateMatch(tender, profile)
                    val isSaved = savedIds.contains(tender.id)
                    TenderWithMatch(tender, match, isSaved)
                }.sortedByDescending { it.match.score }

                // Apply filters
                val filtered = listWithMatches.filter { item ->
                    val t = item.tender
                    val m = item.match

                    val queryMatch = filterState.query.isBlank() ||
                            t.title.contains(filterState.query, ignoreCase = true) ||
                            t.institution.contains(filterState.query, ignoreCase = true) ||
                            t.city.contains(filterState.query, ignoreCase = true) ||
                            t.cpvCode.contains(filterState.query, ignoreCase = true) ||
                            t.category.contains(filterState.query, ignoreCase = true)

                    val cityMatch = filterState.city.isBlank() || t.city.equals(filterState.city, ignoreCase = true)
                    val cantonMatch = filterState.canton.isBlank() || t.canton.contains(filterState.canton, ignoreCase = true)
                    val entityMatch = filterState.entity.isBlank() || t.entity.equals(filterState.entity, ignoreCase = true)
                    val categoryMatch = filterState.category.isBlank() || t.category.equals(filterState.category, ignoreCase = true)
                    val cpvMatch = filterState.cpv.isBlank() || t.cpvCode.contains(filterState.cpv, ignoreCase = true)

                    val minBudgetMatch = filterState.minBudget == null || t.estimatedValueKm >= filterState.minBudget
                    val maxBudgetMatch = filterState.maxBudget == null || t.estimatedValueKm <= filterState.maxBudget
                    val minScoreMatch = filterState.minMatchScore == null || m.score >= filterState.minMatchScore

                    val onlyMatchesOk = !filterState.onlyMatches || m.score >= 60
                    val onlyUrgentOk = !filterState.onlyUrgentDeadline || t.daysUntilDeadline in 0..7
                    val onlyHighValueOk = !filterState.onlyHighValue || t.estimatedValueKm >= 100000.0
                    val onlyZenicaZdkOk = !filterState.onlyZenicaZdk || (t.city.equals("Zenica", ignoreCase = true) || t.canton.contains("ZDK", ignoreCase = true))

                    queryMatch && cityMatch && cantonMatch && entityMatch && categoryMatch && cpvMatch &&
                            minBudgetMatch && maxBudgetMatch && minScoreMatch &&
                            onlyMatchesOk && onlyUrgentOk && onlyHighValueOk && onlyZenicaZdkOk
                }

                // KPIs - izvedeni iz stvarnog stanja bez fiksnih vrijednosti
                val kpis = DashboardKpis(
                    totalCount = listWithMatches.size,
                    matchingCount = listWithMatches.count { it.match.score >= 60 },
                    urgentCount = listWithMatches.count { DeadlineHelper.isUrgent(it.tender) },
                    savedCount = listWithMatches.count { it.isSaved }
                )

                MainUiState(
                    tendersWithMatch = listWithMatches,
                    filteredTenders = filtered,
                    companyProfile = profile,
                    savedTenderIds = savedIds,
                    notifications = notifications,
                    kpis = kpis,
                    filter = filterState,
                    isLoading = false,
                    isOffline = false,
                    activeTenderDetail = _uiState.value.activeTenderDetail?.let { currentDetail ->
                        listWithMatches.find { it.tender.id == currentDetail.tender.id }
                    },
                    activeAiSummary = _uiState.value.activeAiSummary,
                    isAiLoading = _uiState.value.isAiLoading,
                    showFilterSheet = _uiState.value.showFilterSheet,
                    showProfileSheet = _uiState.value.showProfileSheet,
                    showPremiumSheet = _uiState.value.showPremiumSheet
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private val navigationHistory = mutableListOf<Screen>()

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            navigationHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        return if (navigationHistory.isNotEmpty()) {
            val prev = navigationHistory.removeAt(navigationHistory.size - 1)
            _currentScreen.value = prev
            true
        } else if (_currentScreen.value != Screen.Dashboard) {
            _currentScreen.value = Screen.Dashboard
            true
        } else {
            false
        }
    }

    fun selectTender(item: TenderWithMatch) {
        navigationHistory.add(_currentScreen.value)
        _uiState.update { it.copy(activeTenderDetail = item) }
        _currentScreen.value = Screen.TenderDetail
    }

    fun toggleSave(tenderId: String) {
        viewModelScope.launch {
            repository.toggleSaveTender(tenderId)
        }
    }

    fun updateSearchQuery(query: String) {
        _filter.update { it.copy(query = query) }
    }

    fun setQuickFilter(type: String) {
        when (type) {
            "ALL" -> _filter.update {
                it.copy(
                    onlyMatches = false,
                    onlyUrgentDeadline = false,
                    onlyHighValue = false,
                    onlyZenicaZdk = false
                )
            }
            "MATCHES" -> _filter.update { it.copy(onlyMatches = !it.onlyMatches) }
            "URGENT" -> _filter.update { it.copy(onlyUrgentDeadline = !it.onlyUrgentDeadline) }
            "HIGH_VALUE" -> _filter.update { it.copy(onlyHighValue = !it.onlyHighValue) }
            "ZENICA_ZDK" -> _filter.update { it.copy(onlyZenicaZdk = !it.onlyZenicaZdk) }
        }
    }

    fun applyDetailedFilter(newFilter: TenderFilter) {
        _filter.value = newFilter
        _uiState.update { it.copy(showFilterSheet = false) }
    }

    fun resetFilters() {
        _filter.value = TenderFilter()
        _uiState.update { it.copy(showFilterSheet = false) }
    }

    fun setFilterSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showFilterSheet = visible) }
    }

    fun setProfileSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showProfileSheet = visible) }
    }

    fun setPremiumSheetVisible(visible: Boolean) {
        _uiState.update { it.copy(showPremiumSheet = visible) }
    }

    fun updateCompanyProfile(profile: CompanyProfile) {
        viewModelScope.launch {
            repository.updateCompanyProfile(profile)
            _uiState.update { it.copy(showProfileSheet = false) }
        }
    }

    fun generateAiSummary(tender: Tender) {
        _uiState.update { it.copy(isAiLoading = true) }
        viewModelScope.launch {
            // Generisanje strukturisanog sažetka bez halucinacija
            val summary = TenderAiSummarizer.generateSummary(tender)
            _uiState.update { it.copy(activeAiSummary = summary, isAiLoading = false) }
        }
    }

    fun clearAiSummary() {
        _uiState.update { it.copy(activeAiSummary = null, isAiLoading = false) }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun refreshTenders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.refreshTenders()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    class Factory(private val repository: TenderRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TenderViewModel::class.java)) {
                return TenderViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
