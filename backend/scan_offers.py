#!/usr/bin/env python3
"""Daily backend scan: refresh the card-offer configs.

Modus operandi: THIS job does the scanning. The app just displays configs/.
Reads backend/sources.yaml, fetches each card's offer page, extracts
bonus-related snippets, writes configs/offers.json, and stamps each card
config's lastScanned. Committing is left to the caller (the daily-scan workflow).
"""
import hashlib
import json
import re
import sys
from datetime import datetime, timezone
from html import unescape
from pathlib import Path
from urllib.request import Request, urlopen

ROOT = Path(__file__).resolve().parent.parent
SOURCES = ROOT / "backend" / "sources.yaml"
CARDS_DIR = ROOT / "configs" / "cards"
OFFERS_JSON = ROOT / "configs" / "offers.json"

DEFAULT_PATTERNS = [
    r"welcome bonus.{0,160}",
    r"earn up to.{0,160}",
    r"bonus.{0,40}points.{0,120}",
    r"annual fee.{0,80}(waived|rebate|free)",
    r"first.year.{0,80}(free|rebate|waived)",
    r"limited.time offer.{0,160}",
    r"lounge.{0,80}(complimentary|free|visits)",
]

TITLE_RE = re.compile(r"<title[^>]*>(.*?)</title>", re.IGNORECASE | re.DOTALL)
TAG_RE = re.compile(r"<[^>]+>")
WS_RE = re.compile(r"\s+")


def fetch(url: str) -> str:
    req = Request(
        url,
        headers={
            "User-Agent": (
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
                "(KHTML, like Gecko) Chrome/126.0 Safari/537.36"
            ),
            "Accept-Language": "en-CA,en;q=0.9",
        },
    )
    with urlopen(req, timeout=25) as resp:
        raw = resp.read().decode("utf-8", errors="replace")
    return raw


def clean_text(html: str) -> str:
    text = TAG_RE.sub(" ", html)
    text = unescape(text)
    return WS_RE.sub(" ", text).strip()


def extract_snippets(text: str, patterns) -> list[str]:
    found: list[str] = []
    seen: set[str] = set()
    for pat in patterns:
        for m in re.finditer(pat, text, re.IGNORECASE | re.DOTALL):
            snippet = WS_RE.sub(" ", m.group(0)).strip()
            key = hashlib.md5(snippet.lower().encode()).hexdigest()
            if len(snippet) > 25 and key not in seen:
                seen.add(key)
                found.append(snippet[:280])
            if len(found) >= 8:
                break
        if len(found) >= 8:
            break
    return found[:8]


def main() -> int:
    try:
        import yaml
    except ImportError:
        print("PyYAML missing: pip install -r backend/requirements.txt", file=sys.stderr)
        return 2
    with open(SOURCES) as f:
        sources = yaml.safe_load(f)["sources"]

    now = datetime.now(timezone.utc)
    stamped = now.strftime("%Y-%m-%d")
    out: dict = {"generatedAt": now.isoformat(timespec="seconds"), "cards": {}}

    for src in sources:
        card_id = src["card_id"]
        url = src["offer_url"]
        patterns = src.get("patterns") or DEFAULT_PATTERNS
        try:
            html = fetch(url)
            title_m = TITLE_RE.search(html)
            title = WS_RE.sub(" ", unescape(title_m.group(1)).strip())[:140] if title_m else ""
            snippets = extract_snippets(clean_text(html), patterns)
            status = f"OK ({len(snippets)} snippets)"
        except Exception as e:  # noqa: BLE001 - scan must never die on one source
            title, snippets = "", []
            status = f"FETCH FAILED: {type(e).__name__}"
        out["cards"][card_id] = {
            "cardId": card_id,
            "fetchedAt": now.isoformat(timespec="seconds"),
            "pageTitle": title,
            "snippets": snippets,
            "url": url,
        }
        cfg = CARDS_DIR / f"{card_id}.json"
        if cfg.exists():
            data = json.loads(cfg.read_text())
            data["lastScanned"] = stamped
            cfg.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n")
        print(f"{card_id}: {status}")

    OFFERS_JSON.write_text(json.dumps(out, indent=2, ensure_ascii=False) + "\n")
    print(f"Wrote {OFFERS_JSON}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
