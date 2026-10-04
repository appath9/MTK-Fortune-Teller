# MTK Fortune Teller

An Android fortune-telling application inspired by Asian and Oriental divination traditions, reimagined as a modern mobile experience with interactive Solana Web3 integration.

[![Solana Devnet](https://img.shields.io/badge/Solana-Devnet-3772FF?style=flat&logo=solana)](https://solana.com)
[![Android](https://img.shields.io/badge/Android-Jetpack_Compose-3DDC84?style=flat&logo=android)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-7F52FF?style=flat&logo=kotlin)](https://kotlinlang.org)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Seeker Ready](https://img.shields.io/badge/Seeker-Ready-9945FF?style=flat&logo=solana)](https://solanamobile.com/seeker)

---

## Overview

**MTK Fortune Teller** bridges Asian and Oriental divination traditions with modern decentralized Web3 technology. The application delivers fortune readings inspired by traditional 7-day numerology concepts (*Mahabote* & *BayDin*), and enables seekers to seal their fortunes as timestamped on-chain proofs on the **Solana Devnet** using the **Solana Mobile Wallet Adapter (MWA)**.

**Built with Solana Seeker in mind**

MTK Fortune Teller is designed as a mobile-native Solana experience and is being prepared for the Solana Seeker ecosystem. A future **Seeker Elite** entitlement is planned for verified eligible Seeker owners.

The current verified access model remains **2 free readings + 3 wallet-enabled readings = 5 readings/day**. Seeker Elite is not yet a production entitlement.

---

## Why This Project

Fortune reading is part of a long tradition of Asian and Oriental divination practices. MTK Fortune Teller reimagines these traditions as a seamless modern mobile experience:

* **Cultural Heritage:** Inspired by traditional 7-day numerology concepts, featuring 64 questions and 640 fortune readings.
* **On-Chain Proof:** Sealing a fortune on Solana creates a timestamped cryptographic proof recorded on-chain.
* **Mobile-Native Solana Experience:** Built with Android Jetpack Compose and native Solana Mobile Wallet Adapter (MWA) for one-tap wallet interactions — without embedded webviews or handling users' private keys. Designed with the Solana Seeker mobile platform in mind.

---

## Key Features

* **Mystical Portal UI:** Immersive dark-themed Jetpack Compose interface with animated cosmic portals, particle celebrations, and haptic feedback.
* **English Language Interface:** Clean English language interface for questions, readings, and numerological insights.
* **Interactive 0–9 Digit Selection:** Dynamic cosmic digit wheel where seeker intuition determines the final numerology reading.
* **Solana Web3 Sealing:** Seal fortune readings as timestamped on-chain proofs on Solana Devnet via SPL Memo program transactions.
* **Oracle Offering:** Optional symbolic SOL offerings can be included in the same Devnet transaction as the fortune seal.
* **Shareable Fortune Slips:** Render visual fortune cards (`FortuneSlipCard`) and share them directly to social apps via Android system intents.
* **Sealed History:** Local Room database recording past readings, transaction signatures, and direct Solana Explorer links.

---

## Solana Web3 Integration

MTK Fortune Teller integrates Solana Web3 functionality at multiple levels:

* **Wallet Connection (MWA):** Uses `com.solana.mobilewalletadapter.clientlib` to connect seamlessly with installed Solana mobile wallets (e.g., Phantom, Solflare).
* **SPL Memo On-Chain Proof:** Encodes fortune digest, question ID, timestamp, and digit choice into an SPL Memo program instruction (`MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr`).
* **Reconciliation Engine:** Asynchronous RPC status checking ensures pending transactions are verified on Devnet even during network switches or app-backgrounding.

---

## Reading Quota Model

| Tier | Access | On-Chain Sealing |
| :--- | :--- | :--- |
| **Standard** | **2 free readings / day** | Local view & share only |
| **Web3 Prototype** | **+3 wallet-enabled readings / day** (up to 5 total) | On-Chain Devnet Sealing enabled |
| **Daily Cap** | **5 total readings / day maximum** (current verified model) | Hard-stopped at reading 6 |
| **Seeker Elite** | **Future verified entitlement** — expanded access planned for eligible verified Seeker owners | Production entitlement workstream |

### Important Quota Rules

* Connecting a wallet grants up to **3 additional readings** beyond the 2 free readings, for a **maximum total of 5 readings per day** under the current verified model.
* Attempting a 6th reading on the same calendar day is **hard-stopped** and requires waiting for the midnight local reset.
* Connecting a standard wallet alone does **NOT** grant Seeker Elite status or expanded/unlimited access.
* **Seeker Elite is a future verified entitlement**. Any expanded Seeker Elite access is planned only after production ownership verification and entitlement infrastructure are implemented.

---

## Wallet & Mobile Wallet Adapter Flow

```text
[Select Category & Question] ──► [Oracle Animation & Digit Pick]
                                          │
                                          ▼
                               [Generate Local Reading]
                                          │
                                          ▼
                             [Click "Seal on Solana"]
                                          │
                                          ▼
                           [MWA Session: Phantom / Solflare]
                                          │
                   ┌──────────────────────┴──────────────────────┐
                   ▼                                             ▼
          [Approve Transaction]                         [User Cancelled]
                   │                                             │
                   ▼                                             ▼
        [Devnet SPL Memo Proof]                         [Retain Reading Locally]
                   │
                   ▼
        [Save Record & Explorer Link]
```

Detailed documentation: [docs/web3-flow.md](docs/web3-flow.md)

---

## On-Chain Proof

Each on-chain seal produces an SPL Memo transaction on Solana Devnet containing:

```text
MTK-FORTUNE|Q:<question_id>|D:<digit>|TS:<timestamp>|HASH:<payload_hash>
```

Seekers can verify their sealed fortune on [Solana Explorer (Devnet)](https://explorer.solana.com/?cluster=devnet) directly from the application's Sealed History screen.

---

## Oracle Offering

When sealing a fortune, seekers may optionally include a symbolic SOL offering (e.g., 0.001 SOL, 0.005 SOL, or 0.01 SOL) on Solana Devnet as an act of reverence. Offerings are processed in the same atomic transaction as the SPL Memo proof instruction.

---

## Fortune Slip / Sharing

The application features a dedicated bitmap rendering helper (`FortuneSlipShareHelper`) and Compose UI component (`FortuneSlipCard`) that composes the fortune text, numerology chart, date, and project branding into an image payload ready for sharing across WhatsApp, Telegram, X (Twitter), and Instagram.

---

## Question Categories

Questions are organized into 6 core categories:

1. **Love**
2. **Career**
3. **Money**
4. **Decision**
5. **Personal Growth**
6. **General Fortune**

---

## Seeker Giveaway / Entitlement Architecture

### Current Status: Architecture & Prototype Workstream

The repository includes an architectural design and code prototype (`SeekerEntitlementManager`) for Solana Seeker device owners.

### Seeker Elite Production Roadmap & Verification Requirements

Connecting a wallet alone does **not** grant Seeker Elite entitlement. Production verification requires:

1. **Sign-In With Solana (SIWS)** challenge/response with server-side nonce verification.
2. **Backend On-Chain Query** against Solana DAS API to confirm official **Seeker Genesis Token** NFT ownership before issuing an entitlement JWT.

Detailed documentation: [docs/seeker-giveaway.md](docs/seeker-giveaway.md)

---

## Current Implementation Status

### IMPLEMENTED & VERIFIED

- [x] English language 7-day numerology fortune dataset (64 questions, 640 readings).
- [x] Interactive digit selection (0–9) and local fortune calculation engine.
- [x] Daily reading quota management (2 free + 3 wallet = 5 total max/day; hard stop on 6th) and midnight reset logic.
- [x] Solana Mobile Wallet Adapter (MWA) integration for Phantom/Solflare wallets.
- [x] Solana Devnet transaction creation with SPL Memo program proof.
- [x] MWA user cancellation handling and graceful state recovery.
- [x] Asynchronous Devnet transaction reconciliation (`reconcilePendingSeal`).
- [x] Optional SOL Oracle Offering attached to sealing transactions.
- [x] Local Room database persistence for reading history and transaction signatures.
- [x] Shareable visual Fortune Slip card rendering and system share intents.
- [x] 175 automated unit and Robolectric UI tests passing with 0 failures.

### PROTOTYPE / PRODUCTION VERIFICATION WORKSTREAM

- [ ] Production SIWS backend nonce and signature verification service.
- [ ] Server-side Solana DAS API Seeker Genesis Token ownership indexer.
- [ ] End-to-end hardware validation on physical Solana Seeker production devices.

---

## Project Architecture

```text
app/src/main/java/com/example/
├── MainActivity.kt               # Single Activity entry point
├── ui/
│   ├── FortuneViewModel.kt       # ViewModel & state management
│   ├── components/               # GlobalShell, SideMenu, FortuneSlipCard, Confetti
│   ├── screens/                  # Intro, HomePortal, SelectQuestion, Result, SealedHistory
│   └── theme/                    # Color, Type, Theme definitions
├── data/
│   ├── FortuneData.kt            # Fortune dataset
│   ├── FortuneRepository.kt      # Repository for local & sealed records
│   ├── FortunePreferences.kt     # EncryptedSharedPreferences for quotas
│   └── room/                     # Room AppDatabase, Entity, Dao
└── util/
    ├── SolanaWalletManager.kt    # MWA transaction & Devnet RPC client
    ├── SolanaWalletGatekeeper.kt # Quota & tier access control
    ├── SeekerEntitlementManager.kt # Seeker entitlement prototype logic
    └── FortuneSlipShareHelper.kt # Card bitmap rendering & sharing
```

Detailed documentation: [docs/architecture.md](docs/architecture.md)

---

## Known Limitations

* **Devnet Scope:** Configured exclusively for **Solana Devnet**.
* **Wallet App Prerequisite:** On-chain sealing requires an MWA-compatible mobile wallet (e.g., Phantom, Solflare) installed on the Android device or emulator.
* **Public RPC Endpoint:** Devnet transaction confirmation depends on public RPC node availability (`api.devnet.solana.com`).

---

## Build & Run

### Prerequisites

* Android Studio Jellyfish | 2024.1.1 or newer
* JDK 17
* Android SDK 37 (compileSdk 37, targetSdk 36)
* Android device or emulator running API 24+ (minSdk 24)

### Build Commands

```bash
# Clone public snapshot
git clone https://github.com/appath9/MTK-Fortune-Teller.git
cd MTK-Fortune-Teller

# Run full test suite
./gradlew test

# Build Debug APK
./gradlew assembleDebug
```

Output APK location: `app/build/outputs/apk/debug/app-debug.apk`

---

## Devnet Notice

This application executes Web3 transactions strictly on **Solana Devnet**. No real SOL or Mainnet assets are required or transferred. Free Devnet SOL can be obtained via public faucets (e.g., `faucet.solana.com`).

---

## Privacy / Disclaimer

MTK Fortune Teller provides fortune readings for cultural, artistic, and entertainment purposes based on traditional numerology concepts. Readings do not constitute professional financial, medical, or legal advice. Wallet addresses and transaction signatures are recorded publicly on the Solana Devnet blockchain as chosen by the user.

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
