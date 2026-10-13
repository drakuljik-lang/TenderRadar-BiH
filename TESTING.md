# STRATEGIJA I POKRIVENOST TESTIRANJA (TESTING.md)

## 1. Unit Testovi algoritma Match Score (`MatchScoreCalculatorTest.kt`)

Pokriveni testni scenariji:
- `testFullMatch()`: Verifikacija maksimalne podudarnosti (Score >= 80, status VISOKA PODUDARNOST) kada se poklapaju sve stavke (ključne riječi, CPV, lokacija, kategorija, raspon budžeta).
- `testNoMatch()`: Verifikacija niskog rezultata (Score < 40, status NISKA PODUDARNOST) kada tender nema dodirnih tačaka sa profilom firme.
- `testKeywordMatch()`: Izolovani test pondera ključnih riječi (max 30 bodova).
- `testCpvMatch()`: Izolovani test CPV koda (tačno 25 bodova).
- `testLocationMatch()`: Izolovani test lokalnog tendera (grad Zenica = 10 bodova).
- `testCategoryMatch()`: Izolovani test kategorije djelatnosti (15 bodova).

## 2. Robolectric & Compose Testovi (`ExampleRobolectricTest.kt`)
- Provjera učitavanja resursa i stringova aplikacije na izolovanom JVM okruženju bez potrebe za fizičkim emulatorom.

## Pokretanje testova
```bash
gradle :app:testDebugUnitTest
```
