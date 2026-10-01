# Security Policy

## Supported Versions

Security updates and fixes are provided for the latest version of **MTK Fortune Teller** available on the `main` branch.

| Version / Branch | Supported |
| :--- | :--- |
| `main` (Latest) | Yes |
| Pre-release / Older snapshots | No |

## Reporting a Vulnerability

If you discover a potential security vulnerability in MTK Fortune Teller, please report it responsibly and privately.

### Preferred Reporting Channels:
1. **GitHub Private Vulnerability Reporting (Preferred):**  
   Use the **"Report a vulnerability"** button under the [Security tab](../../security/advisories/new) of this repository to open a private disclosure draft.
2. **Security Contact Email:**  
   If you cannot access GitHub's reporting tool, email: **nmyint1999@gmail.com**

Please **do not** publicly disclose the vulnerability through GitHub Issues, pull requests, or social media before the report has been reviewed and addressed.

### What to Include in Your Report:
- A clear description of the vulnerability.
- Exact steps to reproduce or proof-of-concept details.
- The affected component (e.g., MWA handling, SPL Memo attestation pipeline, access-control logic).
- Potential impact and threat model.
- Relevant non-sensitive logs or screenshots.

> **CRITICAL:** Do NOT include private keys, seed phrases, or sensitive credentials in your report.

## Response Commitment

- **Initial Acknowledgement:** Within 48–72 hours of report receipt.
- **Triage & Remediation:** If validated, remediation steps and timeline will be communicated through the private advisory channel.

## Audit & Threat Scope

Security reports are particularly relevant to:
- Solana Mobile Wallet Adapter (MWA 2.2.0) session handling.
- Wallet authorization and transaction signing lifecycles.
- On-chain transaction construction and SPL Memo attestation reconciliation.
- Reading quota accounting and daily access gating.
- Local data persistence and application network configuration (strict HTTPS enforcement).

## Prototype and Devnet Notice

MTK Fortune Teller is currently a hackathon prototype operating exclusively on **Solana Devnet**. It is not configured for, nor intended to handle, real Mainnet financial assets in its current release. Production-grade security hardening and Mainnet deployment are scheduled for future milestone releases.
