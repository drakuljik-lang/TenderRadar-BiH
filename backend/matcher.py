"""
Match Score — 100 bodova:
  CPV              40
  Kljucne rijeci   30
  Lokacija         15
  Budzet           15

Rjesava padeze u bosanskom: "rasvjeta" matcha "rasvjete", "rasvjetna" itd.
"""


def _root_match(keyword: str, text: str, min_root_len: int = 5) -> bool:
    """
    Provjerava da li se keyword pojavljuje u tekstu — bilo kao cijela rijec
    ili kao korijen (prvih N znakova) da bi se uhvatili padezi.
    """
    kw = keyword.lower().strip()
    if not kw:
        return False

    # 1. Direktan match
    if kw in text:
        return True

    # 2. Root match — skini zadnja 1-3 slova, provjeri
    for trim in (1, 2, 3):
        root = kw[:-trim] if trim < len(kw) else ""
        if len(root) >= min_root_len and root in text:
            return True

    return False


def calculate_match(tender, company) -> dict:
    reasons = []
    cpv_pts = 0
    kw_pts = 0
    loc_pts = 0
    budget_pts = 0

    # ── 1. CPV podudaranje (max 40) ────────────────────────────
    if tender.cpv_code and company.cpv_codes:
        t_prefix = (tender.cpv_code or "")[:4]
        matched = False
        for c in company.cpv_codes:
            if c and c[:4] == t_prefix:
                cpv_pts = 40
                reasons.append(f"CPV se poklapa: {tender.cpv_code} (40 b)")
                matched = True
                break
        if not matched:
            cpv_pts = 10
            reasons.append("Djelimicno CPV poklapanje (10 b)")
    else:
        reasons.append("CPV kod nije dostupan")

    # ── 2. Kljucne rijeci (max 30) — s root matchingom ─────────
    if tender.title and company.keywords:
        text = (tender.title + " " + (tender.description or "")).lower()
        hits = [kw for kw in company.keywords if _root_match(kw, text)]
        if hits:
            ratio = min(len(hits) / len(company.keywords), 1.0)
            kw_pts = int(30 * ratio)
            reasons.append(f"Kljucne rijeci: {', '.join(hits)} ({kw_pts} b)")
        else:
            reasons.append("Nema poklapanja kljucnih rijeci")
    else:
        reasons.append("Nema kljucnih rijeci u profilu")

    # ── 3. Lokacija (max 15) ───────────────────────────────────
    if tender.city and company.city:
        if tender.city.lower() == company.city.lower():
            loc_pts = 15
            reasons.append(f"Isti grad: {tender.city} (15 b)")
        elif tender.canton and company.canton and tender.canton == company.canton:
            loc_pts = 8
            reasons.append(f"Isti kanton: {tender.canton} (8 b)")
        else:
            reasons.append(f"Razlicita lokacija ({tender.city} vs {company.city})")
    else:
        reasons.append("Lokacija nije dostupna")

    # ── 4. Budzet (max 15) ─────────────────────────────────────
    if tender.estimated_value_km is not None:
        if company.min_value <= tender.estimated_value_km <= company.max_value:
            budget_pts = 15
            reasons.append(f"Vrijednost {tender.estimated_value_km:,.0f} KM u rasponu (15 b)")
        else:
            reasons.append(
                f"Vrijednost {tender.estimated_value_km:,.0f} KM van raspona "
                f"({company.min_value:,.0f}-{company.max_value:,.0f})"
            )
    else:
        reasons.append("Vrijednost nije dostupna")

    # ── Ukupno ─────────────────────────────────────────────────
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