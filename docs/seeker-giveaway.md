# Seeker Giveaway & Entitlement Architecture

This document describes the Seeker (Solana Mobile Chapter 2 / Seeker Genesis Token) entitlement architecture in MTK Fortune Teller, distinguishing between the code prototype and the required production backend verification pipeline.

---

## Current Status: Architecture & Prototype Workstream

```
┌────────────────────────────────────────────────────────────────────────┐
│                        CURRENT PROTOTYPE FLOW                          │
│                                                                        │
│  [Connected Wallet Address / Local Verification Flag]                  │
│                        │                                               │
│                        ▼                                               │
│             [SeekerEntitlementManager]                                 │
│                        │                                               │
│                        ▼                                               │
│            Prototype Seeker Access Evaluation                          │
└────────────────────────────────────────────────────────────────────────┘

                                    VS

┌────────────────────────────────────────────────────────────────────────┐
│                      REQUIRED PRODUCTION PIPELINE                      │
│                                                                        │
│  [Mobile App] ──(1. Connect MWA)──► [Solana Wallet]                    │
│        │                                  │                            │
│        │◄──(2. Signed SIWS Challenge)─────┘                            │
│        │                                                               │
│        ├──(3. Submit Challenge + Signature)──► [Secure Verification]   │
│        │                                        [Backend Service]      │
│        │                                               │               │
│        │                                     (4. Verify SIWS Nonce &   │
│        │                                         Query On-Chain NFT    │
│        │                                         Genesis Token Holding)│
│        │                                               │               │
│        │◄──(5. Signed JWT / Entitlement Token)─────────┘               │
│        │                                                               │
│        ▼                                                               │
│  Grants Verified Production Seeker Elite Status                        │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 1. Prototype Implementation

In the current codebase:
- **`SeekerEntitlementManager`**: Prototype class that simulates evaluating whether a wallet qualifies for Seeker Elite status.
- **Current Logic**: Operates on client-side state flags or designated wallet addresses.
- **Important**: Connecting a standard Solana wallet alone does **NOT** grant production Seeker Elite status. Connecting a wallet grants up to 3 additional readings per day (5 total maximum per day).

> [!IMPORTANT]
> **Prototype Distinction**: The code prototype demonstrates the UI and state flow for Seeker owners, but client-side wallet address checking alone is **not** cryptographically sufficient for production entitlement. Production Seeker Elite entitlement remains an architectural specification and production verification workstream.

---

## 2. Production Security & Verification Requirements

In a production deployment, relying solely on client-side public key checks allows malicious clients or modified binaries to spoof ownership. To securely verify Seeker Genesis Token ownership, the production workstream requires:

### A. Sign-In With Solana (SIWS) Authentication
1. **Server Nonce Request**: The app requests a cryptographically random, time-bound nonce from the MTK backend service.
2. **SIWS Payload**: The app prompts the user's wallet via MWA to sign a standard SIWS message containing domain, address, nonce, and issue time.
3. **Backend Signature Verification**: The backend verifies the signature against the public key and validates nonce freshness to prevent replay attacks.

### B. On-Chain Genesis Token Ownership Verification
1. **Das API / Indexer Query**: Upon valid SIWS verification, the backend queries Solana Digital Asset Standard (DAS) API or RPC indexer to verify that the authenticated wallet holds an official **Solana Seeker Genesis Token / NFT**.
2. **Cryptographic JWT Issue**: If verified, the backend issues a signed, short-lived JSON Web Token (JWT) attesting to Seeker Elite entitlement.
3. **Client Verification**: `SeekerEntitlementManager` verifies the JWT signature using the backend's public key before enabling Seeker Elite access.

---

## 3. Real Seeker Hardware Testing & Next Steps

- **Devnet / Mainnet Transition**: Seeker Genesis Tokens exist on Solana Mainnet-Beta. Production verification requires querying Mainnet state even if fortune sealing executes on Devnet.
- **Physical Device Validation**: End-to-end hardware testing on physical Solana Seeker devices is required prior to production release.
