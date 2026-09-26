# Solana Web3 Sealing & Mobile Wallet Adapter Flow

This document details the implemented Web3 architecture, Mobile Wallet Adapter (MWA) flow, Solana Devnet integration, transaction sealing, reconciliation, and on-chain proof mechanisms in MTK Fortune Teller.

---

## Complete End-to-End User & Transaction Sequence

```
1. Select Day & Question Category
   └─► 2. Quota Check (SolanaWalletGatekeeper)
         ├─ Free Local Tier: 2 free readings / day
         ├─ Wallet-Connected: +3 additional readings / day (5 total max / day)
         └─ Reading 6+: Hard-stopped until midnight reset
   └─► 3. Oracle / Cosmic Animation & Digit Selection (0–9)
   └─► 4. Fortune Reading Generated (Local Authentic Dataset)
   └─► 5. Quota Committed
   └─► 6. User Clicks "Seal on Solana" (ResultScreen)
   └─► 7. Optional Oracle Offering Selection (0 / 0.001 / 0.005 / 0.01 SOL)
   └─► 8. MWA Session Triggered (SolanaWalletManager)
         ├─ Reauthorize / Authorize Wallet (e.g. Phantom, Solflare)
         ├─ Build Solana Devnet Transaction (SystemProgram Transfer + SPL Memo Instruction)
         └─ Sign & Send via MWA `transact` block
   └─► 9. Transaction Result Processing
         ├─ Success: Signature received ──► Save Record to Room DB ──► Show Explorer Link
         ├─ Cancelled: User declined ─────► Gracefully restore UI state (no crash)
         └─ Timeout / Unknown: Trigger Reconciliation RPC query
   └─► 10. Reconciliation (reconcilePendingSeal)
         └─ Query Devnet RPC for signature status ──► Update DB state accordingly
```

---

## Mobile Wallet Adapter (MWA) Integration

MTK Fortune Teller uses `com.solana.mobilewalletadapter.clientlib` to establish direct RPC communication with mobile Solana wallets (e.g., Phantom, Solflare) installed on the device.

### Association & Session Management
- **Authorization**: `MobileWalletAdapter.transact` initiates association with the wallet app.
- **Identity Claim**: Passes app identity (`identityName = "MTK Fortune Teller"`, `identityUri = "https://mtkfortuneteller.app"`).
- **Session Caching**: Caches authorization token in `EncryptedSharedPreferences` to enable seamless reauthorization on subsequent seals without repeated wallet approval prompts.

---

## On-Chain Proof Mechanism (SPL Memo)

When sealing a fortune reading on Solana Devnet, `SolanaWalletManager` constructs a transaction containing:

1. **System Program Instruction** (if an Oracle Offering was selected by the user, sending the specified SOL offering to the oracle treasury address).
2. **SPL Memo Program Instruction** (`MemoSq2gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr`):
   - **Payload Format**: `MTK-FORTUNE|Q:<question_id>|D:<digit>|TS:<timestamp>|HASH:<payload_hash>`
   - This payload creates an immutable on-chain cryptographic proof linking the user's wallet address to the specific fortune reading on Solana Devnet.

---

## Robust Error Handling & Cancellation Management

### 1. Explicit User Cancellation
If the user cancels the transaction inside the wallet prompt or presses back:
- MWA throws `UserDeclinedException`.
- The app catches the exception, updates UI state to `SealCancelled`, and retains the fortune reading locally without throwing unhandled exceptions or corrupting the local database.

### 2. Captured Signature Safety & Reconciliation (`reconcilePendingSeal`)
If a network timeout or app-switching interruption occurs after the transaction is submitted to the Solana network but before confirmation is returned to the app:
- The app records the pending signature locally.
- `reconcilePendingSeal` performs an asynchronous RPC query against Solana Devnet (`https://api.devnet.solana.com`).
- If confirmed on-chain, the record is marked as `Sealed` and the Solana Explorer link (`https://explorer.solana.com/tx/<sig>?cluster=devnet`) is generated.
- If rejected or dropped by the network, the user is given the option to re-try sealing.

---

## Oracle Offering Architecture

The **Oracle Offering** feature allows seekers to attach a small SOL offering (e.g., 0.001 SOL, 0.005 SOL, or 0.01 SOL) to their on-chain seal as an act of cosmic devotion.

- **Devnet Execution**: Offerings are transferred on Solana Devnet using Devnet SOL.
- **Instruction Composition**: The transfer instruction is combined into the same atomic transaction as the SPL Memo proof instruction. If either fails, the entire transaction rolls back.

---

## Known Limitations & Devnet Scope

- **Network Environment**: Built and configured for **Solana Devnet**.
- **Wallet Availability**: Requires an MWA-compatible mobile wallet app installed on the device (or running on Android emulator with MWA test wallet).
- **RPC Dependency**: Relies on public Solana Devnet RPC endpoints (`api.devnet.solana.com`). Network congestion on Devnet may occasionally delay transaction confirmation.
