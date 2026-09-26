package com.example

import com.example.util.SolanaWalletGatekeeper
import com.solana.publickey.SolanaPublicKey
import com.solana.transaction.AccountMeta
import com.solana.transaction.Message
import com.solana.transaction.Transaction
import com.solana.transaction.TransactionInstruction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SolanaWalletGatekeeperTest {

    @Test
    fun testBase58Encoding() {
        val testBytes = byteArrayOf(0, 1, 2, 3, 4, 5)
        val base58Str = SolanaWalletGatekeeper.encodeToBase58(testBytes)
        assertTrue(base58Str.isNotEmpty())
    }

    @Test
    fun testInspectMessageWithFalseVsTrueWritable() {
        val userPublicKey = SolanaPublicKey(ByteArray(32) { 1 })
        val memoProgramId = SolanaPublicKey.from("MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr")

        // 1) FALSE WRITABLE (The bug)
        val accountMetaFalse = AccountMeta(userPublicKey, isSigner = true, isWritable = false)
        val instructionFalse = TransactionInstruction(
            programId = memoProgramId,
            accounts = listOf(accountMetaFalse),
            data = "test".toByteArray()
        )
        val msgFalse = Message.Builder()
            .addInstruction(instructionFalse)
            .setRecentBlockhash("11111111111111111111111111111111")
            .build()

        println("=== FALSE WRITABLE (BUG) ===")
        println("signatureCount: ${msgFalse.signatureCount}")
        println("readOnlyAccounts: ${msgFalse.readOnlyAccounts}")
        println("readOnlyNonSigners: ${msgFalse.readOnlyNonSigners}")
        println("accounts: ${msgFalse.accounts.map { SolanaWalletGatekeeper.encodeToBase58(it.bytes) }}")

        // 2) TRUE WRITABLE (The fix)
        val accountMetaTrue = AccountMeta(userPublicKey, isSigner = true, isWritable = true)
        val instructionTrue = TransactionInstruction(
            programId = memoProgramId,
            accounts = listOf(accountMetaTrue),
            data = "test".toByteArray()
        )
        val msgTrue = Message.Builder()
            .addInstruction(instructionTrue)
            .setRecentBlockhash("11111111111111111111111111111111")
            .build()

        println("=== TRUE WRITABLE (FIX) ===")
        println("signatureCount: ${msgTrue.signatureCount}")
        println("readOnlyAccounts: ${msgTrue.readOnlyAccounts}")
        println("readOnlyNonSigners: ${msgTrue.readOnlyNonSigners}")
        println("accounts: ${msgTrue.accounts.map { SolanaWalletGatekeeper.encodeToBase58(it.bytes) }}")

        // Verify bounds & structure
        val serializedTx = Transaction(msgTrue).serialize()
        val deserializedTx = Transaction.from(serializedTx)
        val msg = deserializedTx.message
        val accountKeysSize = msg.accounts.size

        println("=== SERIALIZED DIAGNOSTICS ===")
        println("Serialized Tx Length: ${serializedTx.size} bytes")
        println("AccountKeys Count: $accountKeysSize")
        msg.accounts.forEachIndexed { idx, key ->
            println("AccountKey[$idx]: ${SolanaWalletGatekeeper.encodeToBase58(key.bytes)}")
        }
        msg.instructions.forEachIndexed { iIdx, ix ->
            println("Instruction[$iIdx] programIdIndex: ${ix.programIdIndex} (valid: ${ix.programIdIndex.toInt() < accountKeysSize})")
            ix.accountIndices.forEachIndexed { aIdx, accIdx ->
                println("Instruction[$iIdx] accountIndex[$aIdx]: $accIdx (valid: ${accIdx.toInt() < accountKeysSize})")
                assertTrue("instruction account index < accounts.size", accIdx.toInt() < accountKeysSize)
            }
            assertTrue("programIdIndex < accounts.size", ix.programIdIndex.toInt() < accountKeysSize)
        }
    }

    @Test
    fun testBuildExplorerUrlForAddress() {
        val address = "7xKXtg2CW87d97TXJSDp32847"
        val url = SolanaWalletGatekeeper.buildExplorerUrl(address)
        assertEquals("https://explorer.solana.com/address/7xKXtg2CW87d97TXJSDp32847?cluster=devnet", url)
    }

    @Test
    fun testBuildExplorerUrlForTransactionSignature() {
        val txSignature = "5eykt4UsFv8P8NJdTREpY1vzqKqZKvdpKuc147dw2N9d1111111111111111111111111111111111111111111111111"
        val url = SolanaWalletGatekeeper.buildExplorerUrl(txSignature)
        assertEquals("https://explorer.solana.com/tx/$txSignature?cluster=devnet", url)
    }
}
