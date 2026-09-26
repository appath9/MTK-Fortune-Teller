package com.example.util

import com.example.model.AccessTier
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class VerificationErrorCode {
    CANCELLED,
    SIWS_CANCELLED,
    INVALID_SIGNATURE,
    INVALID_NONCE,
    EXPIRED_NONCE,
    REUSED_NONCE,
    SGT_ABSENT,
    MAINNET_RPC_UNAVAILABLE,
    BACKEND_UNAVAILABLE,
    VERIFICATION_FAILED
}

data class SiwsPayload(
    val domain: String = "fortuneteller.solana.app",
    val address: String,
    val statement: String = "Sign in to verify Seeker ownership.",
    val uri: String = "https://fortuneteller.solana.app",
    val version: String = "1",
    val chainId: String = "solana:mainnet",
    val nonce: String,
    val issuedAt: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date()),
    val expirationTime: String? = null
)

data class EntitlementResult(
    val isVerified: Boolean,
    val accessTier: AccessTier,
    val walletAddress: String? = null,
    val sgtMintAddress: String? = null,
    val errorCode: VerificationErrorCode? = null,
    val errorMessage: String? = null
)

object SeekerEntitlementManager {

    /**
     * Official Seeker Genesis Token (SGT) Group / Mint Address on Solana Mainnet.
     */
    const val SGT_COLLECTION_MINT_MAINNET = "46625xA3VNG23sVQ9Rh6iZX3M38y16QU2yJwJ3P135fU"

    /**
     * Solana Mainnet RPC Endpoint for Seeker Token-2022 verification.
     */
    const val MAINNET_RPC_URL = "https://api.mainnet-beta.solana.com"

    /**
     * Simulates or executes fetching a single-use nonce from the secure backend.
     * Since no backend server is present in the repository, this returns null
     * to signal that production verification cannot proceed without a server.
     */
    suspend fun requestNonceFromBackend(): String? {
        // Production architecture: HTTP GET /api/v1/siws/nonce -> returns short-lived single-use nonce
        return null
    }

    /**
     * Validates SIWS Payload and Nonce conditions.
     */
    fun validateNonce(
        nonce: String?,
        expectedNonce: String?,
        isNonceUsed: Boolean,
        isExpired: Boolean = false
    ): VerificationErrorCode? {
        if (nonce.isNullOrBlank()) return VerificationErrorCode.INVALID_NONCE
        if (expectedNonce != null && nonce != expectedNonce) return VerificationErrorCode.INVALID_NONCE
        if (isNonceUsed) return VerificationErrorCode.REUSED_NONCE
        if (isExpired) return VerificationErrorCode.EXPIRED_NONCE
        return null
    }

    /**
     * Validates SIWS Payload structure.
     */
    fun validateSiwsPayload(payload: SiwsPayload): VerificationErrorCode? {
        if (payload.address.isBlank()) return VerificationErrorCode.INVALID_SIGNATURE
        if (payload.chainId != "solana:mainnet") return VerificationErrorCode.VERIFICATION_FAILED
        if (payload.nonce.isBlank()) return VerificationErrorCode.INVALID_NONCE
        return null
    }

    /**
     * Production SIWS + SGT Verification Pipeline.
     * Enforces that both SIWS signature verification and Solana Mainnet Token-2022 SGT check succeed.
     *
     * In the absence of a backend verification server, this function safely falls back to AccessTier.FREE.
     */
    suspend fun verifySiwsAndSgtWithBackend(
        payload: SiwsPayload,
        signatureBase58: String?,
        expectedNonce: String? = null,
        isNonceUsed: Boolean = false,
        isNonceExpired: Boolean = false,
        hasSgtOnMainnet: Boolean = false
    ): EntitlementResult {
        // 1. Check Nonce
        val nonceError = validateNonce(payload.nonce, expectedNonce, isNonceUsed, isNonceExpired)
        if (nonceError != null) {
            return EntitlementResult(
                isVerified = false,
                accessTier = AccessTier.FREE,
                errorCode = nonceError,
                errorMessage = "SIWS Nonce validation failed: $nonceError"
            )
        }

        // 2. Check Payload
        val payloadError = validateSiwsPayload(payload)
        if (payloadError != null) {
            return EntitlementResult(
                isVerified = false,
                accessTier = AccessTier.FREE,
                errorCode = payloadError,
                errorMessage = "SIWS Payload validation failed: $payloadError"
            )
        }

        // 3. Check Signature
        if (signatureBase58.isNullOrBlank()) {
            return EntitlementResult(
                isVerified = false,
                accessTier = AccessTier.FREE,
                errorCode = VerificationErrorCode.INVALID_SIGNATURE,
                errorMessage = "SIWS Signature missing or invalid."
            )
        }

        // 4. Verify SGT Ownership on Mainnet
        if (!hasSgtOnMainnet) {
            return EntitlementResult(
                isVerified = false,
                accessTier = AccessTier.FREE,
                errorCode = VerificationErrorCode.SGT_ABSENT,
                errorMessage = "No genuine Seeker Genesis Token found for wallet ${payload.address} on Solana Mainnet."
            )
        }

        // 5. Backend Server Requirement Check
        // Production verification MUST be verified by a backend server that verifies the SIWS signature & SGT on Mainnet.
        return EntitlementResult(
            isVerified = false,
            accessTier = AccessTier.FREE,
            errorCode = VerificationErrorCode.BACKEND_UNAVAILABLE,
            errorMessage = "Production Seeker Elite entitlement remains pending backend verification service availability."
        )
    }
}
