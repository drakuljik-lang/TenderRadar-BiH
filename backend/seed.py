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
               institution="OpÄ‡ina VareÅ¡", city="VareÅ¡", canton="Zenicko-dobojski (ZDK)",
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
