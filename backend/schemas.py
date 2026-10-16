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
