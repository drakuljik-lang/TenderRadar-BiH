# TENDERRADAR BiH v0.1 MVP

> **"Pronađi tender koji odgovara tvojoj firmi."**  
> Brand: **ZENICA DIGITAL**  
> Package: `ba.zenicadigital.tenderradar`

Profesionalna native Android aplikacija za tržište Bosne i Hercegovine koja privrednim društvima i obrtima omogućava brzo pronalaženje, pametno filtriranje i automatsku procjenu podudarnosti javnih nabavki (tendera) objavljenih na zvaničnim portalima.

---

## 🚀 Ključne funkcionalnosti

1. **Dashboard & KPI pregled**:
   - Prikaz ključnih poslovnih metrika: *Novi tenderi*, *Moji matching tenderi*, *Rokovi < 7 dana*, *Spremljeni tenderi*.
   - Direktan uvid u tendere sa visokom podudarnošću.

2. **Match Score algoritam (0–100%)**:
   - **Ključne riječi (30%)**: Detekcija riječi iz profila firme u opisu i specifikaciji.
   - **CPV klasifikacija (25%)**: Usklađenost jedinstvenog rječnika javnih nabavki.
   - **Kategorija djelatnosti (15%)**: Provjera registrovane privredne grane.
   - **Lokacija (10%)**: Grad, kanton (ZDK, KS, TK...) i entitet.
   - **Raspon vrijednosti (20%)**: Provjera budžetskog kapaciteta firme.
   - Transparentno objašnjenje zašto je tender dobio određeni score (npr. *✓ Elektroinstalacije, ✓ ZDK, ✓ Odgovarajući CPV*).

3. **Napredni filteri i brza pretraga**:
   - Filteri po gradovima, kantonima, procijenjenoj vrijednosti, kategorijama i minimalnom match score-u.
   - Brzi filteri: *"Samo moji"*, *"Rok < 7 dana"*, *"> 100.000 KM"*, *"Zenica / ZDK"*.

4. **Profil firme i prilagođavanje algoritma**:
   - Konfiguracija parametara kompanije: djelatnost, ključne riječi, CPV oznake, min/max budžet.

5. **AI Sažetak tenderske dokumentacije**:
   - Strukturisano izdvajanje 6 ključnih elemenata bez halucinacija (Šta se nabavlja, Ko može aplicirati, Proc. vrijednost, Rok, Dokumenti, Upozorenja i rizici).

6. **Lokalna perzistencija & Offline podrška (Room Database)**:
   - Pohranjivanje tendera, spremljenih zabilješki, notifikacija i profila u SQLite bazu.
   - Potpuna funkcionalnost u offline režimu rada.

7. **B2B Premium model**:
   - Struktura paketa: FREE, PRO, BUSINESS.
   - Usklađeno sa Google Play pravilima: B2B aktivacija putem poslovnog računa/fakture (bez klijentskog Google Play Billing-a).

---

## 🛠 Tehnologije

- **Jezik**: Kotlin 2.2
- **UI Framework**: Jetpack Compose & Material Design 3 (B2B Deep Navy & Cyan paleta)
- **Arhitektura**: Clean Architecture + MVVM + Repository Pattern
- **Lokalna baza**: Room 2.7 (KSP Symbol Processing)
- **Mreža & API**: Retrofit 2 + Moshi (pripremljeno za backend integraciju)
- **Testiranje**: JUnit 4, Robolectric
