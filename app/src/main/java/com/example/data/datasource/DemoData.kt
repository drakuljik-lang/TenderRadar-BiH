package com.example.data.datasource

import com.example.data.model.CompanyProfile
import com.example.data.model.NotificationItem
import com.example.data.model.Tender
import com.example.data.model.TenderDocument

object DemoData {

    val defaultCompanyProfile = CompanyProfile(
        id = "default_company_profile",
        companyName = "ABC Elektro d.o.o.",
        activity = "Elektroinstalacije i inženjering",
        city = "Zenica",
        canton = "Zeničko-dobojski (ZDK)",
        keywords = listOf("elektroinstalacije", "elektromaterijal", "rasvjeta", "električni radovi", "trafostanica"),
        cpvCategories = listOf("45310000-3", "45311000-0", "31682000-0", "31527200-8"),
        minBudgetKm = 20000.0,
        maxBudgetKm = 500000.0,
        isConfigured = true
    )

    val sampleNotifications = listOf(
        NotificationItem(
            id = "notif-1",
            title = "Novi tender odgovara vašoj firmi",
            message = "Grad Zenica je objavio tender za radove na javnoj rasvjeti. Match Score: 94%.",
            tenderId = "TND-BA-2026-001",
            timestamp = System.currentTimeMillis() - 3600_000 * 2,
            type = "MATCH_HIGH",
            isRead = false
        ),
        NotificationItem(
            id = "notif-2",
            title = "Rok ističe za 6 dana",
            message = "Kantonalna bolnica Zenica: Nabavka elektroinstalacionog materijala zatvara se uskoro.",
            tenderId = "TND-BA-2026-002",
            timestamp = System.currentTimeMillis() - 3600_000 * 8,
            type = "DEADLINE_SOON",
            isRead = false
        ),
        NotificationItem(
            id = "notif-3",
            title = "Match Score 90+",
            message = "JP Autoceste FBiH: Održavanje niskonaponske mreže i tunelske rasvjete.",
            tenderId = "TND-BA-2026-003",
            timestamp = System.currentTimeMillis() - 3600_000 * 24,
            type = "MATCH_HIGH",
            isRead = true
        ),
        NotificationItem(
            id = "notif-4",
            title = "Novi tender u vašem kantonu (ZDK)",
            message = "Objavljena rekonstrukcija elektro-mreže u industrijskoj zoni Zenica.",
            tenderId = "TND-BA-2026-008",
            timestamp = System.currentTimeMillis() - 3600_000 * 48,
            type = "NEW_TENDER",
            isRead = true
        )
    )

    val demoTenders: List<Tender> = listOf(
        Tender(
            id = "TND-BA-2026-001",
            title = "Nabavka i ugradnja pametne LED javne rasvjete na području Grada Zenica",
            institution = "Gradska uprava Zenica",
            city = "Zenica",
            canton = "Zeničko-dobojski (ZDK)",
            entity = "Federacija BiH",
            estimatedValueKm = 185000.0,
            publishedDate = "02.10.2026.",
            deadlineDate = "18.10.2026.",
            daysUntilDeadline = 10,
            category = "Elektroinstalacije",
            cpvCode = "45316110-9",
            cpvDescription = "Postavljanje vanjske rasvjetne opreme",
            description = "Predmet javne nabavke je izvođenje radova na demontaži postojeće dotrajale rasvjete i ugradnji novih energetski efikasnih LED svjetiljki sa sistemom centralnog upravljanja na glavnim saobraćajnicama u Zenici.",
            documents = listOf(
                TenderDocument("doc-1", "Tenderska dokumentacija - Otvoreni postupak", "Tenderska dokumentacija", "PDF", "2.4 MB", "https://ejn.gov.ba/demo/doc1.pdf"),
                TenderDocument("doc-2", "Troškovnik i tehnička specifikacija rasvjete", "Tehnička specifikacija", "DOCX", "840 KB", "https://ejn.gov.ba/demo/doc2.docx"),
                TenderDocument("doc-3", "Obrazac za cijenu ponude Aneks 3", "Obrazac", "PDF", "310 KB", "https://ejn.gov.ba/demo/doc3.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        ),
        Tender(
            id = "TND-BA-2026-002",
            title = "Nabavka elektroinstalacionog materijala i potrošnih elemenata za održavanje",
            institution = "JU Kantonalna bolnica Zenica",
            city = "Zenica",
            canton = "Zeničko-dobojski (ZDK)",
            entity = "Federacija BiH",
            estimatedValueKm = 64000.0,
            publishedDate = "05.10.2026.",
            deadlineDate = "14.10.2026.",
            daysUntilDeadline = 6,
            category = "Elektroinstalacije",
            cpvCode = "31682000-0",
            cpvDescription = "Potrošni elektromaterijal",
            description = "Sukcesivna isporuka kablova, prekidača, osigurača, razvodnih ormara i pratećeg elektromaterijala za potrebe redovnog održavanja elektroinstalacija u bolničkim traktovima.",
            documents = listOf(
                TenderDocument("doc-4", "Poziv za dostavljanje ponuda i specifikacija", "Tenderska dokumentacija", "PDF", "1.8 MB", "https://ejn.gov.ba/demo/doc4.pdf"),
                TenderDocument("doc-5", "Nacrt okvirnog sporazuma", "Obrazac", "PDF", "450 KB", "https://ejn.gov.ba/demo/doc5.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        ),
        Tender(
            id = "TND-BA-2026-003",
            title = "Redovno i vanredno održavanje niskonaponskih elektroinstalacija i sistema tunelske rasvjete",
            institution = "JP Autoceste FBiH d.o.o. Mostar",
            city = "Zenica",
            canton = "Zeničko-dobojski (ZDK)",
            entity = "Federacija BiH",
            estimatedValueKm = 340000.0,
            publishedDate = "29.09.2026.",
            deadlineDate = "22.10.2026.",
            daysUntilDeadline = 14,
            category = "Elektroinstalacije",
            cpvCode = "45311000-0",
            cpvDescription = "Elektroinstalacijski radovi",
            description = "Održavanje elektroenergetske infrastrukture na dionici Koridora Vc Vranduk - Zenica jug, uključujući kontrolu trafostanica, UPS sistema i agregata za pomoćno napajanje.",
            documents = listOf(
                TenderDocument("doc-6", "Tenderska dokumentacija Vranduk-Zenica", "Tenderska dokumentacija", "PDF", "3.2 MB", "https://ejn.gov.ba/demo/doc6.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        ),
        Tender(
            id = "TND-BA-2026-004",
            title = "Rekonstrukcija i energetska efikasnost upravne zgrade ZDK",
            institution = "Vlada Zeničko-dobojskog kantona",
            city = "Zenica",
            canton = "Zeničko-dobojski (ZDK)",
            entity = "Federacija BiH",
            estimatedValueKm = 520000.0,
            publishedDate = "01.10.2026.",
            deadlineDate = "30.10.2026.",
            daysUntilDeadline = 22,
            category = "Građevina",
            cpvCode = "45210000-2",
            cpvDescription = "Građevinski radovi na zgradama",
            description = "Građevinsko-zanatski radovi, termoizolaciona fasada, zamjena stolarije, krovopokrivački radovi te prateća rekonstrukcija grijanja.",
            documents = listOf(
                TenderDocument("doc-7", "Glavni izvedbeni projekat", "Tehnička specifikacija", "PDF", "5.1 MB", "https://ejn.gov.ba/demo/doc7.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        ),
        Tender(
            id = "TND-BA-2026-005",
            title = "Nadogradnja mrežne serverske infrastrukture i firewall sistema",
            institution = "Klinički centar Univerziteta u Sarajevu (KCUS)",
            city = "Sarajevo",
            canton = "Kanton Sarajevo (KS)",
            entity = "Federacija BiH",
            estimatedValueKm = 210000.0,
            publishedDate = "04.10.2026.",
            deadlineDate = "19.10.2026.",
            daysUntilDeadline = 11,
            category = "IT",
            cpvCode = "48820000-2",
            cpvDescription = "Poslužitelji (serveri)",
            description = "Isporuka dva serverska ormara sa redundantnim napajanjem, enterprise switch opreme i licenca za sigurnosni nadzor bolničke baze podataka.",
            documents = listOf(
                TenderDocument("doc-8", "IT Specifikacija servera i sigurnosti", "Tehnička specifikacija", "PDF", "1.4 MB", "https://ejn.gov.ba/demo/doc8.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        ),
        Tender(
            id = "TND-BA-2026-006",
            title = "Nabavka digitalnog stacionarnog RTG aparata za radiološku dijagnostiku",
            institution = "JU Dom zdravlja Zenica",
            city = "Zenica",
            canton = "Zeničko-dobojski (ZDK)",
            entity = "Federacija BiH",
            estimatedValueKm = 295000.0,
            publishedDate = "28.09.2026.",
            deadlineDate = "15.10.2026.",
            daysUntilDeadline = 7,
            category = "Medicinska oprema",
            cpvCode = "33111000-1",
            cpvDescription = "Rendgenski uređaji",
            description = "Nabavka, montaža, puštanje u rad i obuka medicinskog osoblja za rad na savremenom digitalnom rentgen aparatu sa ravnim detektorom visoke rezolucije.",
            documents = listOf(
                TenderDocument("doc-9", "Tehnička specifikacija RTG", "Tehnička specifikacija", "PDF", "2.1 MB", "https://ejn.gov.ba/demo/doc9.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        ),
        Tender(
            id = "TND-BA-2026-007",
            title = "Godišnja nabavka kancelarijskog materijala i tonera za potrebe kantonalnih organa",
            institution = "Ministarstvo za pravosuđe i upravu ZDK",
            city = "Zenica",
            canton = "Zeničko-dobojski (ZDK)",
            entity = "Federacija BiH",
            estimatedValueKm = 48000.0,
            publishedDate = "03.10.2026.",
            deadlineDate = "12.10.2026.",
            daysUntilDeadline = 4,
            category = "Kancelarijski materijal",
            cpvCode = "30192700-8",
            cpvDescription = "Kancelarijski materijal",
            description = "Papir za printanje A4 i A3 formata, registratori, arhivske kutije, hemijske olovke i originalni toneri za laserske printere HP i Canon.",
            documents = listOf(
                TenderDocument("doc-10", "Spisak kancelarijskih artikala", "Obrazac", "PDF", "620 KB", "https://ejn.gov.ba/demo/doc10.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        ),
        Tender(
            id = "TND-BA-2026-008",
            title = "Sanacija trafostanice TS 10/0.4 kV i zamjena visokonaponskih prekidača",
            institution = "RMU Zenica d.o.o. Zenica",
            city = "Zenica",
            canton = "Zeničko-dobojski (ZDK)",
            entity = "Federacija BiH",
            estimatedValueKm = 115000.0,
            publishedDate = "06.10.2026.",
            deadlineDate = "23.10.2026.",
            daysUntilDeadline = 15,
            category = "Elektroinstalacije",
            cpvCode = "31214000-9",
            cpvDescription = "Rasklopni uređaji",
            description = "Ispitivanje, servis i djelimična zamjena vakuumskih sklopki u pogonskoj trafostanici jame Raspotočje uz izdavanje atestne dokumentacije.",
            documents = listOf(
                TenderDocument("doc-11", "Projekat sanacije TS", "Tehnička specifikacija", "PDF", "1.9 MB", "https://ejn.gov.ba/demo/doc11.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        ),
        Tender(
            id = "TND-BA-2026-009",
            title = "Usluge fizičkog i tehničkog obezbjeđenja objekata i imovine",
            institution = "Univerzitet u Tuzli",
            city = "Tuzla",
            canton = "Tuzlanski (TK)",
            entity = "Federacija BiH",
            estimatedValueKm = 190000.0,
            publishedDate = "25.09.2026.",
            deadlineDate = "11.10.2026.",
            daysUntilDeadline = 3,
            category = "Zaštita",
            cpvCode = "79713000-5",
            cpvDescription = "Usluge čuvara",
            description = "24-satno fizičko osiguranje fakultetskih zgrada i kampusa Univerziteta u Tuzli uz video-nadzor i interventne patrole.",
            documents = listOf(
                TenderDocument("doc-12", "Plan obezbjeđenja kampusa", "Tenderska dokumentacija", "PDF", "980 KB", "https://ejn.gov.ba/demo/doc12.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        ),
        Tender(
            id = "TND-BA-2026-010",
            title = "Nabavka dva nova specijalizovana teretna vozila za komunalni otpad",
            institution = "JP Komrad d.o.o. Bihać",
            city = "Bihać",
            canton = "Unsko-sanski (USK)",
            entity = "Federacija BiH",
            estimatedValueKm = 430000.0,
            publishedDate = "02.10.2026.",
            deadlineDate = "28.10.2026.",
            daysUntilDeadline = 20,
            category = "Transport",
            cpvCode = "34144510-6",
            cpvDescription = "Vozila za odvoz smeća",
            description = "Isporuka novih autosmećara zapremine nadogradnje 16 m3 sa hidrauličnim podizačem za kontejnere i EURO 6 dizel motorom.",
            documents = listOf(
                TenderDocument("doc-13", "Katalog specifikacija vozila", "Tehnička specifikacija", "PDF", "2.8 MB", "https://ejn.gov.ba/demo/doc13.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        ),
        Tender(
            id = "TND-BA-2026-011",
            title = "Godišnje preventivno i korektivno održavanje liftova i vertikalnog transporta",
            institution = "Služba za zajedničke poslove organa i tijela FBiH",
            city = "Sarajevo",
            canton = "Kanton Sarajevo (KS)",
            entity = "Federacija BiH",
            estimatedValueKm = 72000.0,
            publishedDate = "30.09.2026.",
            deadlineDate = "16.10.2026.",
            daysUntilDeadline = 8,
            category = "Održavanje",
            cpvCode = "50750000-7",
            cpvDescription = "Usluge održavanja dizala",
            description = "Mjesečni pregled, servisiranje, zamjena habajućih dijelova i hitne intervencije na 18 putničkih liftova u zgradama Vlade FBiH.",
            documents = listOf(
                TenderDocument("doc-14", "Spisak lokacija liftova", "Obrazac", "PDF", "750 KB", "https://ejn.gov.ba/demo/doc14.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        ),
        Tender(
            id = "TND-BA-2026-012",
            title = "Sukcesivna nabavka prehrambenih artikala i mliječnih proizvoda za dječije vrtiće",
            institution = "JU Predškolski odgoj i obrazovanje Zenica",
            city = "Zenica",
            canton = "Zeničko-dobojski (ZDK)",
            entity = "Federacija BiH",
            estimatedValueKm = 88000.0,
            publishedDate = "04.10.2026.",
            deadlineDate = "20.10.2026.",
            daysUntilDeadline = 12,
            category = "Hrana",
            cpvCode = "15800000-6",
            cpvDescription = "Razni prehrambeni proizvodi",
            description = "Dnevna isporuka svježeg mlijeka, mliječnih prerađevina, hljeba, brašna i osnovnih namirnica za potrebe centralne kuhinje predškolskih ustanova.",
            documents = listOf(
                TenderDocument("doc-15", "Sanitarni uslovi i jelovnik", "Tenderska dokumentacija", "PDF", "1.1 MB", "https://ejn.gov.ba/demo/doc15.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        ),
        Tender(
            id = "TND-BA-2026-013",
            title = "Usluge pranja, peglanja i dezinfekcije bolničkog veša",
            institution = "Opća bolnica 'Prim. dr. Abdulah Nakaš' Sarajevo",
            city = "Sarajevo",
            canton = "Kanton Sarajevo (KS)",
            entity = "Federacija BiH",
            estimatedValueKm = 160000.0,
            publishedDate = "05.10.2026.",
            deadlineDate = "27.10.2026.",
            daysUntilDeadline = 19,
            category = "Javne usluge",
            cpvCode = "98311000-3",
            cpvDescription = "Usluge prikupljanja i pranja rublja",
            description = "Pružanje usluga mašinskog pranja kontaminiranog i standardnog rublja po ISO standardima dezinfekcije u zdravstvenim ustanovama.",
            documents = listOf(
                TenderDocument("doc-16", "Protokol higijene rublja", "Tehnička specifikacija", "PDF", "920 KB", "https://ejn.gov.ba/demo/doc16.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        ),
        Tender(
            id = "TND-BA-2026-014",
            title = "Izrada projektno-tehničke dokumentacije za rekonstrukciju mosta na rijeci Bosni",
            institution = "Grad Doboj",
            city = "Doboj",
            canton = "Regija Doboj",
            entity = "Republika Srpska",
            estimatedValueKm = 95000.0,
            publishedDate = "01.10.2026.",
            deadlineDate = "17.10.2026.",
            daysUntilDeadline = 9,
            category = "Građevina",
            cpvCode = "71322000-1",
            cpvDescription = "Usluge tehničkog projektiranja građevina",
            description = "Geomehanička ispitivanja, statički proračun i izrada glavnog projekta sanacije kolovozne konstrukcije i stubova mosta.",
            documents = listOf(
                TenderDocument("doc-17", "Projektni zadatak mosta", "Tehnička specifikacija", "PDF", "3.6 MB", "https://ejn.gov.ba/demo/doc17.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        ),
        Tender(
            id = "TND-BA-2026-015",
            title = "Nabavka i isporuka softverskog rješenja za elektronsko upravljanje dokumentima (DMS)",
            institution = "Gradska uprava Grada Mostara",
            city = "Mostar",
            canton = "Hercegovačko-neretvanski (HNK)",
            entity = "Federacija BiH",
            estimatedValueKm = 175000.0,
            publishedDate = "03.10.2026.",
            deadlineDate = "25.10.2026.",
            daysUntilDeadline = 17,
            category = "IT",
            cpvCode = "48311000-1",
            cpvDescription = "Paket programske podrške za upravljanje dokumentima",
            description = "Implementacija sistema protokola, elektronskog potpisa, arhiviranja i praćenja rješavanja upravnih predmeta.",
            documents = listOf(
                TenderDocument("doc-18", "Funkcionalna specifikacija DMS-a", "Tehnička specifikacija", "PDF", "2.0 MB", "https://ejn.gov.ba/demo/doc18.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        ),
        Tender(
            id = "TND-BA-2026-016",
            title = "Izvođenje radova na elektroinstalacijama jake i slabe struje u novoj sportskoj dvorani",
            institution = "Općina Kakanj",
            city = "Kakanj",
            canton = "Zeničko-dobojski (ZDK)",
            entity = "Federacija BiH",
            estimatedValueKm = 245000.0,
            publishedDate = "05.10.2026.",
            deadlineDate = "26.10.2026.",
            daysUntilDeadline = 18,
            category = "Elektroinstalacije",
            cpvCode = "45311000-0",
            cpvDescription = "Elektroinstalacijski radovi",
            description = "Kompletno ožičenje, rasvjeta terena u LED tehnologiji sa DALI regulacijom, panik rasvjeta, ozvučenje i sistem vatrodojave.",
            documents = listOf(
                TenderDocument("doc-19", "Troškovnik elektroinstalacija Kakanj", "Tehnička specifikacija", "PDF", "2.7 MB", "https://ejn.gov.ba/demo/doc19.pdf")
            ),
            isDemo = true,
            status = "Aktivan"
        )
    )
}
