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
