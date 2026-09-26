# Testing & Verification Guide — MTK Fortune Teller

This document details the test suite, test coverage, execution commands, and verification results for MTK Fortune Teller.

---

## Test Suite Summary

The project includes an extensive suite of automated unit, integration, state management, and Robolectric UI tests.

### Current Automated Verification Result

```text
Build Status: SUCCESSFUL
Total Unit Tests: 175
Passed: 175
Failed: 0
Skipped: 0
Target Commit: 50be709d26e3031b28d490ddacba479be50545dc
```

---

## Key Test Classes & Categories

| Test Class | Category | Test Objectives |
| :--- | :--- | :--- |
| **`DailyQuotaAndAccessTest`** | Quota & Access | Verifies Free Tier (2), Wallet Tier (+3 additional, 5 max total), hard stop on reading 6, and midnight reset logic in `FortunePreferences`. |
| **`SolanaWalletGatekeeperTest`** | Access Control | Verifies tier gating, wallet connection elevation (+3 readings), and daily cap permission checks. |
| **`SeekerEntitlementTest`** | Entitlement | Validates prototype Seeker tier detection, quota overrides, and tier fallback behaviors. |
| **`QuestionCategoryTest`** | Data Lookup | Verifies category mapping (Love, Wealth, Career, Health, General) and English question/reading retrieval. |
| **`QuestionConfirmationTest`** | UI Flow | Verifies question selection confirmation dialogs and navigation payload passing. |
| **`OracleOfferingTest`** | Web3 Offerings | Validates SOL offering selection, amount parsing, and instruction assembly. |
| **`FortuneSlipCardDataTest`** | Presentation | Validates `FortuneSlipCardData` formatting, numerology chart layout, and text string composition. |
| **`FortuneSlipCardUiTest`** | UI Component | Verifies Compose rendering of the shareable Fortune Slip card component. |
| **`FortuneSlipShareHelperTest`** | Media / Intent | Validates bitmap creation, canvas drawing, and Android Intent creation for social sharing. |
| **`VisualShareTest`** | Visual Rendering | Tests visual card rendering and share payload formatting. |
| **`SealingHardeningLevel2Test`** | MWA Resilience | Validates MWA session handling, token reauthorization, and exception recovery. |
| **`ReconciliationRaceRegressionTest`** | Async / Web3 | Tests race condition resilience during asynchronous RPC transaction reconciliation. |
| **`Phase3RuntimeAndTimeoutTest`** | Network Resilience | Validates network timeout handling, user cancellation recovery, and fallback state transitions. |
| **`Path1AndPath2BackFixTest`** | Navigation | Ensures backstack integrity and proper screen pop behavior during navigation flows. |
| **`FreeSealCtaRegressionTest`** | UI State | Verifies CTA button state consistency across free vs. wallet-connected sealing modes. |
| **`GreetingScreenshotTest`** | Robolectric UI | Executes Robolectric UI rendering test for greeting screen layout. |
| **`ExampleRobolectricTest`** | UI Framework | Verifies Robolectric framework compatibility and context initialization. |
| **`ExampleUnitTest`** | Base Framework | Basic framework sanity verification. |

---

## Running Automated Tests

To execute the full test suite locally:

```bash
# Run all unit and Robolectric tests
./gradlew test

# Run tests for a specific package or class
./gradlew testDebugUnitTest --tests "com.example.DailyQuotaAndAccessTest"
```

---

## Physical Device & Manual Verification Protocol

In addition to automated tests, manual testing protocols are established for physical device verification:

1. **Wallet Connection & MWA Protocol**:
   - Install Phantom or Solflare wallet on Android test device.
   - Switch wallet network to **Solana Devnet**.
   - Trigger "Connect Wallet" from `HomePortalScreen`. Verify MWA authorization prompt appears and successfully returns wallet address.
2. **Fortune Reading & Sealing Flow**:
   - Select day, category, question, and digit (0–9).
   - Verify fortune reading generates instantly.
   - Click "Seal on Solana". Choose optional Oracle Offering (0.001 SOL).
   - Approve MWA transaction prompt in Phantom/Solflare.
   - Verify success dialog displays transaction signature and valid Solana Explorer Devnet link (`https://explorer.solana.com/tx/<sig>?cluster=devnet`).
3. **User Cancellation Test**:
   - In the wallet prompt, press "Cancel" or "Reject".
   - Verify the app returns smoothly to `ResultScreen` with a clear cancellation message and no app freeze or crash.
4. **Daily Quota Verification**:
   - Perform 2 free readings without wallet -> verify prompt to connect wallet.
   - Connect wallet -> perform 3 additional readings -> verify 5 total readings achieved.
   - Attempt 6th reading -> verify hard stop dialog.
