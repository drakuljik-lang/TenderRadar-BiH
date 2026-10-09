# ARHITEKTURA SISTEMA (ARCHITECTURE.md)

TenderRadar BiH implementira striktnu **Clean Architecture** organizaciju sa **MVVM** obrascem.

```
UI Layer (Jetpack Compose, Material 3)
   │  Screens, Components, StateFlow observation
   ▼
ViewModel Layer (TenderViewModel)
   │  Filter orchestration, UI state aggregation
   ▼
Domain Layer (UseCases & Calculators)
   │  MatchScoreCalculator (0-100 algorithm)
   │  TenderAiSummarizer (Hallucination-free extraction)
   ▼
Repository Layer (TenderRepository interface)
   ├── DemoTenderRepository (Active: Room SQLite + Seed data)
   └── RemoteTenderRepository (Placeholder: Prepared for backend)
   ▼
Data Sources
   ├── Local: Room Database (AppDatabase, DAOs, Entities)
   └── Remote: Retrofit TenderApi (Prepared REST endpoints)
```

## Slojevi aplikacije

1. **`com.example.data.model`**:
   - `Tender`, `CompanyProfile`, `TenderMatchExplanation`, `SavedTender`, `NotificationItem`, `TenderFilter`, `AiSummaryData`.
   - Domeni su nezavisni od UI i Room biblioteka.

2. **`com.example.data.local`**:
   - Room tabele: `tenders`, `company_profile`, `saved_tenders`, `notifications`.
   - `AppDatabase` sa reaktivnim `Flow<List<T>>` povratnim tipovima.

3. **`com.example.data.remote`**:
   - `TenderApi` - Retrofit definicije za produkcijski REST servis.

4. **`com.example.data.repository`**:
   - `TenderRepository` definira ugovor za dohvat i spremanje podataka.
   - `DemoTenderRepository` automatski inicijalizuje bazu i pruža offline perzistenciju.

5. **`com.example.domain.matcher`**:
   - Ponderisani algoritam: Keywords (30%), CPV (25%), Category (15%), Location (10%), Budget (20%).
   - Generiše objašnjenja zašto tender odgovara profilu.

6. **`com.example.ui`**:
   - Komponente: `TenderCard`, `MatchScoreBadge`, `KpiCard`, `FilterBottomSheet`.
   - Ekrani: `DashboardScreen`, `FeedScreen`, `TenderDetailScreen`, `SavedScreen`, `NotificationsScreen`, `ProfileScreen`, `PremiumScreen`.
