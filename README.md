# Card Scanner

A credit card scanner app built on one modus operandi, the same one we'll use
for every consumer app we build:

> **The backend scans every day. Configs carry the facts. The app just displays it.**

## How it works

1. **Backend scan (daily).** `.github/workflows/daily-scan.yml` runs every morning
   (~8:05 AM ET). It executes `backend/scan_offers.py`, which fetches each card's
   offer page listed in `backend/sources.yaml`, extracts bonus/offer snippets, and
   writes them to `configs/offers.json` (stamping each card's `lastScanned`).
   Changes are committed automatically.
2. **Configs are the product.** `configs/cards/*.json` hold every fact the app
   shows — annual fee, welcome bonus, earn rates, perks, transfer partners, BIN
   prefixes. Nothing is hardcoded in the app; add a card by adding a JSON file
   and listing it in `configs/index.json` + `backend/sources.yaml`.
3. **The app is a thin display client.** On launch it shows the bundled configs
   instantly, then silently refreshes from the backend feed
   (`CONFIG_BASE_URL` in `app/build.gradle.kts`). The camera scan runs on-device
   (CameraX + ML Kit OCR): the photo never leaves the phone, and only the BIN +
   last 4 digits are kept in memory. The full card number is never stored or
   transmitted.

## Project layout

```
card-scanner/
  app/            Android app (Compose). Scan screen, catalog, card detail.
  core/           Pure logic: CardMatcher (Luhn, OCR parsing, BIN match),
                  config data models. Unit-tested in CI.
  configs/        THE product data. cards/*.json, index.json, offers.json
                  (rewritten daily by the backend scan).
  backend/        scan_offers.py + sources.yaml — the daily scan job.
  .github/workflows/
    build.yml       CI: unit tests + debug APK artifact (card-scanner-debug)
    daily-scan.yml  Cron: run the scan, commit updated configs
```

## Status

- v0.1.0 scaffold. App id `com.lemon.cardscanner` is **provisional**, not final.
- 5 seeded card configs (RBC Avion VI, TD First Class Travel VI, CIBC Aventura VI,
  BMO Ascend WE, Amex Cobalt). Facts verified 2026-09-28; `binPrefixes` are empty
  until verified from real card scans. Annual fees left blank where unconfirmed.
- Not yet tested on a real device. Debug APK builds via GitHub Actions.

## Adding a card

1. Create `configs/cards/<card-id>.json` (copy an existing one).
2. Add the id to `configs/index.json`.
3. Add its offer page to `backend/sources.yaml`.
4. Push — the next daily scan picks it up, and the app displays it.
