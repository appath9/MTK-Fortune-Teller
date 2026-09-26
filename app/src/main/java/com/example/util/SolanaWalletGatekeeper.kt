package com.example.util

import android.net.Uri
import com.solana.mobilewalletadapter.common.util.Base58
import com.solana.publickey.SolanaPublicKey

object SolanaWalletGatekeeper {

    /**
     * Converts a raw ByteArray (public key or transaction signature) to a Base58 string.
     */
    fun encodeToBase58(bytes: ByteArray): String {
        return Base58.encode(bytes)
    }

    /**
     * Validates whether a given string is a valid 32-byte Base58 Solana public key.
     */
    fun isValidBase58PublicKey(pubKeyStr: String): Boolean {
        val clean = pubKeyStr.trim()
        if (clean.isBlank() || clean.startsWith("<") || clean.contains("YOUR_")) return false
        return try {
            val pk = SolanaPublicKey.from(clean)
            pk.bytes.size == 32
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Generates a safely encoded Solana Devnet Explorer URL for a transaction signature or account address.
     * URL parameters like `cluster=devnet` and path segments are cleanly encoded using [Uri.Builder].
     */
    fun buildExplorerUrl(
        identifier: String,
        cluster: String = "devnet",
        isTx: Boolean? = null
    ): String {
        val cleanIdentifier = identifier.trim()
        val isTxSignature = isTx ?: (cleanIdentifier.length > 64)
        val pathSegment = if (isTxSignature) "tx" else "address"

        return Uri.Builder()
            .scheme("https")
            .authority("explorer.solana.com")
            .appendPath(pathSegment)
            .appendPath(cleanIdentifier)
            .appendQueryParameter("cluster", cluster)
            .build()
            .toString()
    }
}
