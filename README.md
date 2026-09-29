# Card Scanner

A credit card scanner app built on one modus operandi, the same one we'll use
for every consumer app we build:

> **The backend scans every day. Modules carry the facts. The app just displays it.**

## How it works

1. **Backend scan (daily).** `.github/workflows/daily-scan.yml` runs every morning
   (~8:05 AM ET). It executes `backend/scan_offers.py`, which fetches each card's
   offer page listed in `backend/sources.yaml`, extracts bonus/offer snippets, and
   writes them to `configs/offers.json`. Changes are committed automatically.
2. **One module per bank/provider.** `banks/<bank>/` holds that provider's card
   facts as Kotlin (`<Bank>Catalog.kt`) — annual fee, welcome bonus, earn rates,
   perks, transfer partners, BIN prefixes. Nothing is hardcoded in the app UI;
   `app/` rolls every bank module into one catalog in `BankCatalogs.kt` and
   displays it. This is the compile-and-roll step.
3. **The app is a thin display client.** On launch it shows the compiled catalog
   instantly, then silently refreshes the daily offer snippets from the backend
   feed (`CONFIG_BASE_URL` in `app/build.gradle.kts`). The camera scan runs
   on-device (CameraX + ML Kit OCR): the photo never leaves the phone, and only
   the BIN + last 4 digits are kept in memory. The full card number is never
   stored or transmitted.

## Project layout

```
card-scanner/
  app/            Android app (Compose). Scan screen, catalog, card detail.
                  Rolls the bank modules into one catalog (BankCatalogs.kt).
  core/           Pure logic: CardMatcher (Luhn, OCR parsing, BIN match),
                  config data models. Unit-tested in CI.
  banks/          One module per provider: td, rbc, cibc, bmo, amex.
                  Each <Bank>Catalog.kt carries that bank's card facts.
  configs/        offers.json — rewritten daily by the backend scan.
  backend/        scan_offers.py + sources.yaml — the daily scan job.
  .github/workflows/
    build.yml       CI: unit tests + debug APK artifact (card-scanner-debug)
    daily-scan.yml  Cron: run the scan, commit updated offers
```

## Status

- v0.2.0 module layout. App id `com.lemon.cardscanner` is **provisional**, not final.
- 5 seeded cards (RBC Avion VI, TD First Class Travel VI, CIBC Aventura VI,
  BMO Ascend WE, Amex Cobalt). Facts verified 2026-09-28; `binPrefixes` are empty
  until verified from real card scans. Annual fees left blank where unconfirmed.
- Not yet tested on a real device. Debug APK builds via GitHub Actions.

## Adding a card

1. Add its facts to its bank's module: `banks/<bank>/.../<Bank>Catalog.kt`
   (new bank = new `:banks/<bank>` module, listed in `settings.gradle.kts` and
   rolled into `app/BankCatalogs.kt`).
2. Add its offer page to `backend/sources.yaml`.
3. Push — CI builds the APK, and the next daily scan picks up its offers.
