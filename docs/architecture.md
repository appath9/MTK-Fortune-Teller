# Project Architecture — MTK Fortune Teller

This document provides a technical overview of the MTK Fortune Teller Android application architecture, data flow, state management, and Web3 integration layer for hackathon judges and developers.

---

## Architecture Overview

MTK Fortune Teller follows modern Android development practices using **Jetpack Compose** for UI, **StateFlow / ViewModel** for reactive state management, **Room** for local database persistence, **EncryptedSharedPreferences** for secure storage, and **Solana Mobile Wallet Adapter (MWA)** for Web3 on-chain transactions on Solana Devnet.

```
┌────────────────────────────────────────────────────────────────────────┐
│                              UI LAYER                                  │
│  [MainActivity] -> [GlobalShell] -> Jetpack Compose Screen Hierarchy  │
│  IntroScreen | HomePortalScreen | SelectQuestionScreen | ResultScreen  │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ StateFlow / Events
┌───────────────────────────────────▼────────────────────────────────────┐
│                           VIEWMODEL LAYER                              │
│                          [FortuneViewModel]                            │
│    UI State Management | Daily Quota Rules | Wallet Session State      │
└─────────┬─────────────────────────┬──────────────────────────┬─────────┘
          │                         │                          │
┌─────────▼──────────┐    ┌─────────▼──────────┐    ┌──────────▼─────────┐
│     DATA LAYER     │    │   SECURITY / PREFS  │    │     WEB3 LAYER     │
│ [FortuneRepository]│    │[FortunePreferences]│    │[SolanaWalletManager]│
│ [FortuneRecordDao] │    │EncryptedShared     │    │[WalletGatekeeper]  │
│ [AppDatabase]      │    │Preferences         │    │[SeekerEntitlement] │
└────────────────────┘    └────────────────────┘    └────────────────────┘
```

---

## Component Breakdown

### 1. UI Layer (`com.example.ui`)
- **`MainActivity.kt`**: Single Activity entry point. Sets up Material3 theme (`Theme.kt`), window insets, and embeds `GlobalShell`.
- **`GlobalShell.kt`**: Root container providing top bar, side drawer navigation (`SideMenuDrawer`), modal dialogs (`HelpAboutModal`), confetti animations (`ConfettiCelebrationCanvas`), and main screen navigation based on `FortuneViewModel` state.
- **Screens (`com.example.ui.screens`)**:
  - `IntroScreen`: Welcome screen with mystical visuals and entry portal.
  - `HomePortalScreen`: Central portal displaying day-of-week selection, quota gauge, and wallet status.
  - `SelectQuestionScreen`: Categorized question selector (Love, Wealth, Career, Health, General) with search.
  - `LoadingScreen` / `AnimationPlaceholderScreen`: Mystical cosmic animation during fortune calculation and 0–9 digit selection.
  - `ResultScreen`: Displays the fortune reading, numerology chart, "Seal on Solana" CTA button, Oracle Offering options, and shareable Fortune Slip card (`FortuneSlipCard`).
  - `SealedHistoryScreen`: List of past local and on-chain sealed readings with direct links to Solana Explorer.

### 2. ViewModel & State Management (`com.example.ui.FortuneViewModel`)
- Exposes `FortuneUiState` as an immutable `StateFlow`.
- Handles user interactions: day selection, question selection, digit pick, quota deduction, wallet connection, transaction signing, and fortune sharing.
- Guarantees thread safety and atomic state updates during MWA wallet interactions and network reconciliation.

### 3. Data & Persistence Layer (`com.example.data`)
- **`FortuneData.kt`**: Dataset containing 64 questions and 640 authentic readings in English based on traditional 7-day numerology (Mahabote & BayDin).
- **`FortuneRepository.kt`**: Orchestrates local fortune retrieval and history management.
- **`AppDatabase.kt` & `FortuneRecordEntity.kt`**: Room database persisting fortune readings, sealing signatures, block timestamps, and on-chain explorer links.
- **`FortunePreferences.kt`**: Secure storage using AndroidX `EncryptedSharedPreferences` for daily quota counts, last reset date, and cached wallet address.

### 4. Web3 & Gatekeeper Layer (`com.example.util`)
- **`SolanaWalletManager.kt`**: Wraps Solana Mobile Wallet Adapter (MWA) client library. Handles wallet authorization, reauthorization, transaction building with SystemProgram and SPL Memo program instructions, signing, and Devnet RPC submission.
- **`SolanaWalletGatekeeper.kt`**: Evaluates daily reading quota tiers and access permission based on wallet connection state.
- **`SeekerEntitlementManager.kt`**: Prototype implementation for Solana Seeker device / Genesis Token entitlement evaluation (production verification workstream).

---

## Daily Quota & Access Model

The access and quota model strictly enforces daily limits:

| Tier / State | Condition | Daily Reading Limit | On-Chain Sealing |
| :--- | :--- | :--- | :--- |
| **Free Local Tier** | Default (No wallet connected) | **2 free readings / day** | Local view & share only |
| **Wallet-Gated Tier** | Connected Solana Wallet (MWA) | **3 additional readings / day** | On-Chain Devnet Sealing enabled |
| **Daily Maximum Cap** | All users | **5 total readings / day maximum** | Hard-stopped at reading 6 |

### Quota Rules Enforced
1. **Unconnected User**: Allowed **2 free readings per day**.
2. **Wallet-Connected User**: Unlocks up to **3 additional readings per day**, bringing the maximum allowable daily total to **5 readings per day**.
3. **Hard Stop**: Attempting a 6th reading on the same day is hard-stopped by `SolanaWalletGatekeeper` until midnight local reset.
4. **Seeker Elite**: Connecting a standard wallet alone does **not** grant Seeker Elite access. Seeker Elite remains a prototype / production verification workstream requiring SIWS backend verification.

Daily quotas automatically reset at midnight local time (`FortunePreferences`).

---

## Local Calculation vs. Web3 Sealing Separation

1. **Local Fortune Engine**: Fortune readings are generated on-device using traditional 7-day numerology calculations. This guarantees zero latency, offline availability, and privacy.
2. **On-Chain Web3 Sealing**: After generating a reading, the user can optionally "Seal on Solana". This creates an immutable Devnet transaction containing an SPL Memo with the cryptographic digest of the fortune, question ID, and timestamp, permanently recording the reading on the Solana blockchain.
