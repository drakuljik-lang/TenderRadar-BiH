# SPECIFIKACIJA REST API-JA (API_SPEC.md)

Specifikacija budućih REST endpointa za povezivanje TenderRadar mobilne aplikacije sa zvaničnim backend servisom:

## Base URL: `https://api.tenderradar.ba/api/v1/`

### 1. `GET /tenders`
Dohvaća paginiranu listu javnih nabavki.
- **Parametri**:
  - `page` (integer, default: 1)
  - `limit` (integer, default: 50)
  - `category` (string, opcionalno)
- **Response**: `200 OK` sa listom `Tender` objekata.

### 2. `GET /tenders/{id}`
Dohvaća detaljne informacije o pojedinačnom tenderu.
- **Response**: `200 OK` sa objektom tendera i listom dokumenata.

### 3. `GET /tenders/search`
Pretraga tendera po ključnim riječima, gradu ili CPV kodu.
- **Parametri**:
  - `q` (string, upit za pretragu)
  - `city` (string, opcionalno)
  - `cpv` (string, opcionalno)

### 4. `GET /tenders/categories`
Dohvaća listu svih aktivnih kategorija nabavki.

### 5. `POST /company/profile`
Sinhronizuje profil firme na cloud server.
- **Request Body**: JSON sa parametrima firme (naziv, djelatnost, grad, CPV, budžet).

### 6. `GET /matches`
Dohvaća personalizovane tendere sortirane po Match Score-u za prijavljenu firmu.
- **Parametri**:
  - `companyId` (string)

---
*Napomena: U trenutnom MVP-u ovi endpointi su mapirani u `TenderApi.kt` interfejsu, dok se podaci učitavaju putem `DemoTenderRepository` sloja sa Room bazom podataka.*
