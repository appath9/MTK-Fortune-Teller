package com.example.util

object SolanaAppConfig {
    /**
     * Developer-controlled Devnet public key for Oracle offerings.
     * Must be set by the developer prior to real-device testing.
     */
    const val DEVNET_ORACLE_TREASURY = "3SmhDEaCeou33n9t7vHybi17682jDFS5HzJ8Smt3gXWq"

    /**
     * Offering amount in lamports (1,000,000 lamports = 0.001 SOL).
     */
    const val OFFERING_LAMPORTS = 1_000_000L
}
