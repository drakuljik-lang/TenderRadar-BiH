# ═══════════════════════════════════════════════════════════
# TenderRadar BiH — Backend setup (PowerShell)
# ═══════════════════════════════════════════════════════════

Write-Host "Kreiram fajlove..." -ForegroundColor Cyan

# ── requirements.txt ─────────────────────────────────────
@'
fastapi>=0.115.0
uvicorn[standard]>=0.32.0
sqlalchemy>=2.0.36
pydantic>=2.10.0
python-dotenv>=1.0.1
'@ | Set-Content -Encoding UTF8 requirements.txt

# ── database.py ──────────────────────────────────────────
@'
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base

SQLALCHEMY_DATABASE_URL = "sqlite:///./tenderradar.db"

engine = create_engine(
    SQLALCHEMY_DATABASE_URL,
    connect_args={"check_same_thread": False},
)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
'@ | Set-Content -Encoding UTF8 database.py

# ── models.py ────────────────────────────────────────────
@'
from sqlalchemy import Column, Integer, String, Float, DateTime, ForeignKey, Text, JSON
from sqlalchemy.orm import relationship
from datetime import datetime
from database import Base


class Company(Base):
    __tablename__ = "companies"
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    jib = Column(String, nullable=True)
    industry = Column(String, nullable=False)
    city = Column(String, nullable=True)
    canton = Column(String, nullable=True)
    cpv_codes = Column(JSON, default=list)
    keywords = Column(JSON, default=list)
    min_value = Column(Float, default=0)
    max_value = Column(Float, default=10000000)
    created_at = Column(DateTime, default=datetime.utcnow)


class Tender(Base):
    __tablename__ = "tenders"
    id = Column(Integer, primary_key=True, index=True)
    tender_number = Column(String, unique=True, index=True)
    title = Column(String, nullable=False)
    institution = Column(String)
    city = Column(String)
    canton = Column(String)
    entity = Column(String)
    description = Column(Text)
    estimated_value_km = Column(Float, nullable=True)
    deadline = Column(DateTime, nullable=True)
    published_date = Column(DateTime, default=datetime.utcnow)
    category = Column(String)
    cpv_code = Column(String, index=True)
    cpv_description = Column(String)
    source_url = Column(String)
    status = Column(String, default="Aktivan")


class Match(Base):
    __tablename__ = "matches"
    id = Column(Integer, primary_key=True, index=True)
    tender_id = Column(Integer, ForeignKey("tenders.id"))
    company_id = Column(Integer, ForeignKey("companies.id"))
    score = Column(Integer)
    level = Column(String)
    reasons = Column(JSON, default=list)
    breakdown = Column(JSON, default=dict)
    created_at = Column(DateTime, default=datetime.utcnow)
'@ | Set-Content -Encoding UTF8 models.py

# ── matcher.py ───────────────────────────────────────────
@'
def calculate_match(tender, company) -> dict:
    reasons = []
    cpv_pts = 0
    kw_pts = 0
    loc_pts = 0
    budget_pts = 0

    if tender.cpv_code and company.cpv_codes:
        t_prefix = (tender.cpv_code or "")[:4]
        matched = False
        for c in company.cpv_codes:
            if c and c[:4] == t_prefix:
                cpv_pts = 40
                reasons.append("CPV se poklapa: " + tender.cpv_code + " (40 b)")
                matched = True
                break
        if not matched:
            cpv_pts = 10
            reasons.append("Djelimicno CPV poklapanje (10 b)")
    else:
        reasons.append("CPV kod nije dostupan")

    if tender.title and company.keywords:
        text = (tender.title + " " + (tender.description or "")).lower()
        hits = [kw for kw in company.keywords if kw.lower() in text]
        if hits:
            ratio = min(len(hits) / len(company.keywords), 1.0)
            kw_pts = int(30 * ratio)
            reasons.append("Kljucne rijeci: " + ", ".join(hits) + " (" + str(kw_pts) + " b)")
        else:
            reasons.append("Nema poklapanja kljucnih rijeci")
    else:
        reasons.append("Nema kljucnih rijeci u profilu")

    if tender.city and company.city:
        if tender.city.lower() == company.city.lower():
            loc_pts = 15
            reasons.append("Isti grad: " + tender.city + " (15 b)")
        elif tender.canton and company.canton and tender.canton == company.canton:
            loc_pts = 8
            reasons.append("Isti kanton: " + tender.canton + " (8 b)")
        else:
            reasons.append("Razlicita lokacija")
    else:
        reasons.append("Lokacija nije dostupna")

    if tender.estimated_value_km is not None:
        if company.min_value <= tender.estimated_value_km <= company.max_value:
            budget_pts = 15
            reasons.append("Vrijednost u rasponu firme (15 b)")
        else:
            reasons.append("Vrijednost van raspona firme")
    else:
        reasons.append("Vrijednost nije dostupna")

    total = cpv_pts + kw_pts + loc_pts + budget_pts
    if total >= 75:
        level = "VISOKA PODUDARNOST"
    elif total >= 50:
        level = "SREDNJA PODUDARNOST"
    elif total >= 25:
        level = "MOGUCA PODUDARNOST"
    else:
        level = "NISKA PODUDARNOST"

    return {
        "score": total,
        "level": level,
        "reasons": reasons,
        "breakdown": {
            "cpv": cpv_pts,
            "keywords": kw_pts,
            "location": loc_pts,
            "budget": budget_pts,
        },
    }
'@ | Set-Content -Encoding UTF8 matcher.py

# ── schemas.py ───────────────────────────────────────────
@'
from pydantic import BaseModel
from datetime import datetime
from typing import Optional, List, Dict


class CompanyCreate(BaseModel):
    name: str
    jib: Optional[str] = None
    industry: str
    city: Optional[str] = None
    canton: Optional[str] = None
    cpv_codes: List[str] = []
    keywords: List[str] = []
    min_value: float = 0
    max_value: float = 10000000


class CompanyOut(CompanyCreate):
    id: int
    class Config:
        from_attributes = True


class TenderOut(BaseModel):
    id: int
    tender_number: Optional[str] = None
    title: str
    institution: Optional[str] = None
    city: Optional[str] = None
    canton: Optional[str] = None
    entity: Optional[str] = None
    description: Optional[str] = None
    estimated_value_km: Optional[float] = None
    deadline: Optional[datetime] = None
    published_date: Optional[datetime] = None
    category: Optional[str] = None
    cpv_code: Optional[str] = None
    cpv_description: Optional[str] = None
    source_url: Optional[str] = None
    status: str = "Aktivan"
    class Config:
        from_attributes = True


class MatchOut(BaseModel):
    tender_id: int
    company_id: int
    tender_title: str
    company_name: str
    score: int
    level: str
    reasons: List[str]
    breakdown: Dict[str, int]
'@ | Set-Content -Encoding UTF8 schemas.py

# ── main.py ──────────────────────────────────────────────
@'
from fastapi import FastAPI, Depends, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from typing import List

from database import Base, engine, get_db
from models import Tender, Company
from schemas import CompanyCreate, CompanyOut, TenderOut, MatchOut
from matcher import calculate_match

Base.metadata.create_all(bind=engine)
app = FastAPI(title="TenderRadar BiH API", version="0.1.0")
app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_credentials=True, allow_methods=["*"], allow_headers=["*"])


@app.get("/")
def root():
    return {"status": "ok", "app": "TenderRadar BiH API", "version": "0.1.0"}


@app.get("/tenders", response_model=List[TenderOut])
def list_tenders(db: Session = Depends(get_db)):
    return db.query(Tender).all()


@app.get("/tenders/{tender_id}", response_model=TenderOut)
def get_tender(tender_id: int, db: Session = Depends(get_db)):
    t = db.query(Tender).get(tender_id)
    if not t:
        raise HTTPException(status_code=404, detail="Tender nije pronadjen")
    return t


@app.post("/companies", response_model=CompanyOut)
def create_company(payload: CompanyCreate, db: Session = Depends(get_db)):
    c = Company(**payload.model_dump())
    db.add(c); db.commit(); db.refresh(c)
    return c


@app.get("/companies/{company_id}", response_model=CompanyOut)
def get_company(company_id: int, db: Session = Depends(get_db)):
    c = db.query(Company).get(company_id)
    if not c:
        raise HTTPException(status_code=404, detail="Firma nije pronadjena")
    return c


@app.get("/match/{tender_id}/{company_id}", response_model=MatchOut)
def match(tender_id: int, company_id: int, db: Session = Depends(get_db)):
    t = db.query(Tender).get(tender_id)
    c = db.query(Company).get(company_id)
    if not t or not c:
        raise HTTPException(status_code=404, detail="Nije pronadjeno")
    r = calculate_match(t, c)
    return {"tender_id": tender_id, "company_id": company_id, "tender_title": t.title, "company_name": c.name, **r}


@app.get("/feed/{company_id}")
def feed(company_id: int, min_score: int = 0, db: Session = Depends(get_db)):
    c = db.query(Company).get(company_id)
    if not c:
        raise HTTPException(status_code=404, detail="Firma nije pronadjena")
    items = []
    for t in db.query(Tender).all():
        r = calculate_match(t, c)
        if r["score"] >= min_score:
            items.append({"tender_id": t.id, "tender_title": t.title, **r})
    items.sort(key=lambda x: x["score"], reverse=True)
    return {"company_id": company_id, "count": len(items), "items": items}
'@ | Set-Content -Encoding UTF8 main.py

# ── seed.py ──────────────────────────────────────────────
@'
from datetime import datetime
from database import SessionLocal, Base, engine
from models import Tender, Company

Base.metadata.create_all(bind=engine)
db = SessionLocal()

if db.query(Company).count() == 0:
    db.add(Company(
        name="Zenica Elektro Inzenjering d.o.o.",
        jib="4201234567890",
        industry="Elektroinstalacije i odrzavanje",
        city="Zenica",
        canton="Zenicko-dobojski (ZDK)",
        cpv_codes=["45316110", "45310000", "31500000", "50700000"],
        keywords=["rasvjeta", "elektroinstalacije", "LED", "rekonstrukcija"],
        min_value=10000, max_value=300000,
    ))

if db.query(Tender).count() == 0:
    db.add_all([
        Tender(tender_number="TND-BA-2026-001",
               title="Nabavka i ugradnja pametne LED javne rasvjete u Gradu Zenica",
               institution="Grad Zenica", city="Zenica", canton="Zenicko-dobojski (ZDK)",
               entity="Federacija BiH", description="LED rasvjetna tijela.",
               estimated_value_km=245000.0, deadline=datetime(2026, 10, 28, 12, 0),
               published_date=datetime(2026, 10, 12), category="Elektroinstalacije",
               cpv_code="45316110-9", cpv_description="Vanjska rasvjeta",
               source_url="https://open.ejn.gov.ba/ProcurementNotices/1"),
        Tender(tender_number="TND-BA-2026-002",
               title="Rekonstrukcija trafostanice Zenica Jug",
               institution="JP Elektroprivreda BiH", city="Zenica",
               canton="Zenicko-dobojski (ZDK)", entity="Federacija BiH",
               description="Trafostanica 10/0.4 kV.", estimated_value_km=185000.0,
               deadline=datetime(2026, 11, 5, 12, 0), published_date=datetime(2026, 10, 10),
               category="Elektroinstalacije", cpv_code="45310000-3",
               cpv_description="Elektricne instalacije",
               source_url="https://open.ejn.gov.ba/ProcurementNotices/2"),
        Tender(tender_number="TND-BA-2026-003",
               title="Nabavka agregata za KCUS Sarajevo",
               institution="KCUS Sarajevo", city="Sarajevo", canton="Kantonalni (KS)",
               entity="Federacija BiH", description="Dizel agregat 250 kVA.",
               estimated_value_km=95000.0, deadline=datetime(2026, 11, 12, 12, 0),
               published_date=datetime(2026, 10, 11), category="Elektro oprema",
               cpv_code="31120000-9", cpv_description="Generatori",
               source_url="https://open.ejn.gov.ba/ProcurementNotices/3"),
        Tender(tender_number="TND-BA-2026-004",
               title="Izgradnja kruznog toka u Varesu",
               institution="Općina Vareš", city="Vareš", canton="Zenicko-dobojski (ZDK)",
               entity="Federacija BiH", description="Kruzni tok.",
               estimated_value_km=413747.35, deadline=datetime(2026, 11, 20, 12, 0),
               published_date=datetime(2026, 10, 13), category="Gradevina",
               cpv_code="45233120-4", cpv_description="Putevi",
               source_url="https://open.ejn.gov.ba/ProcurementNotices/4"),
        Tender(tender_number="TND-BA-2026-005",
               title="Laboratorijska oprema za UKC Tuzla",
               institution="UKC Tuzla", city="Tuzla", canton="Tuzlanski (TK)",
               entity="Federacija BiH", description="Medicinska oprema.",
               estimated_value_km=520000.0, deadline=datetime(2026, 11, 18, 12, 0),
               published_date=datetime(2026, 10, 14), category="Medicinska oprema",
               cpv_code="33100000-1", cpv_description="Medicinski uredjaji",
               source_url="https://open.ejn.gov.ba/ProcurementNotices/5"),
    ])

db.commit()
print("Seed OK. Kompanije: " + str(db.query(Company).count()) + ", Tenderi: " + str(db.query(Tender).count()))
db.close()
'@ | Set-Content -Encoding UTF8 seed.py

Write-Host ""
Write-Host "Fajlovi kreirani:" -ForegroundColor Green
Get-ChildItem *.py, *.txt | Select-Object Name, Length

Write-Host ""
Write-Host "Instaliram pakete..." -ForegroundColor Cyan
python -m pip install -r requirements.txt

Write-Host ""
Write-Host "Seed baze..." -ForegroundColor Cyan
python seed.py

Write-Host ""
Write-Host "═══════════════════════════════════════════════" -ForegroundColor Green
Write-Host "SVE SPREMNO" -ForegroundColor Green
Write-Host "═══════════════════════════════════════════════" -ForegroundColor Green
Write-Host ""
Write-Host "Pokreni server:" -ForegroundColor Yellow
Write-Host "  python -m uvicorn main:app --host 127.0.0.1 --port 8001"
Write-Host ""