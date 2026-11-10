# Inpatient PF Tracker — Android project

Android Studio project for an **offline-only** patient professional-fee tracker.

## Build and install
1. Install Android Studio and Android SDK 35 (internet required for initial build dependencies).
2. Open the `InpatientPFTracker-Android` folder in Android Studio.
3. Wait for Gradle sync; select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. Transfer the generated `app/build/outputs/apk/debug/app-debug.apk` to your Android phone and install it (Android 8.0+).

**This ZIP contains source code, not a compiled APK.** Gradle wrapper is not included; Android Studio can use its installed Gradle distribution or configure one.

## Monthly collections report (v1.1)
- Choose a reporting month from the dashboard.
- See professional fees collected during that month, based on actual payment receipt dates.
- See outstanding fees at that month-end, including earlier admissions, less payments received through month-end.
- View totals grouped by hospital and by Self Pay / individual HMO provider.
- Export a detailed monthly CSV report using Android's system file picker (compatible with Excel).
- Existing v1 patient records are preserved if the updated app is installed over the original with the same signing key. Export a backup before upgrading.
- Reports use the dates entered by the user; changing a patient fee later also changes historical month-end outstanding totals. This is not an immutable accounting ledger.

## Storage and privacy
- The application has **no INTERNET permission** and blocks non-bundled WebView resources.
- Patient data are stored in Android WebView localStorage inside the app's private data directory.
- Data do not automatically sync or move to a new phone. Uninstalling the app clears its local records.
- Export backup saves a **plain-text JSON file** using Android's file picker. This file is **not encrypted**. Store backups in an encrypted, access-controlled location, and do not share through unsecured apps.
- Import backup **replaces** existing records after confirmation; export your existing data first.
- Use patient codes instead of full names where possible. Device lock and encrypted storage are recommended.

## Limitations
- No compiled APK provided in this environment.
- No automated tests or on-device validation performed.
- No PIN/biometric lock within the app; relies on device security.
- Backup is not automatically scheduled.


## v1.2 HMO Aging Report
- Select an as-of date and review outstanding HMO balances in 0–30, 31–60, 61–90, and over-90-day buckets.
- Breakdown by HMO provider and hospital, plus patient-level detail.
- Export the aging report as an Excel-compatible CSV through the Android document picker.
- Aging starts at **admission date**, not claim submission date. Partial payments are deducted using payments received on or before the selected as-of date.
- No cloud, network permission, or internet connection is required.


## Version 1.3
Patient entry now uses separate required Surname and First name fields, displayed as SURNAME, First name. The existing offline, reports, backup/restore functions are retained. Build with Android Studio (Gradle sync, then Build > Build APK). This source package is not a compiled APK.
