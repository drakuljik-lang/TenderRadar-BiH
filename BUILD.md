# UPUTSTVO ZA BUILD (BUILD.md)

## Zahtjevi okruženja
- JDK 17 ili JDK 21
- Android SDK (compileSdk 36, targetSdk 36, minSdk 24)
- Gradle 8.13+

---

## 1. Pokretanje testova
Za pokretanje svih jediničnih testova i Robolectric testova:
```bash
gradle :app:testDebugUnitTest
```

---

## 2. Generisanje Debug APK-a
Za kompajliranje i kreiranje instalacionog Debug APK paketa:
```bash
gradle :app:assembleDebug
```
Generisani APK se nalazi na lokaciji:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 3. Generisanje Release APK-a
Za kreiranje optimizovanog Release APK paketa:
```bash
gradle :app:assembleRelease
```
Generisani APK se nalazi na lokaciji:
`app/build/outputs/apk/release/app-release-unsigned.apk` (ili potpisan sa upload ključem)

---

## 4. Generisanje Android App Bundle (.aab) za Google Play
Za distribuciju putem Google Play Developer Console:
```bash
gradle :app:bundleRelease
```
Generisani bundle paket se nalazi na lokaciji:
`app/build/outputs/bundle/release/app-release.aab`
