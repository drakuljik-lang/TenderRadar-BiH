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
