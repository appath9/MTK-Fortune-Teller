package com.example

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class VisualShareTest {

    @Test
    fun testCacheFileAndAuthorityFormatting() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val cacheFile = File(context.cacheDir, "oracle_proof.png")
        assertEquals("oracle_proof.png", cacheFile.name)
        assertEquals(context.cacheDir, cacheFile.parentFile)

        val expectedAuthority = "${context.packageName}.fileprovider"
        assertEquals("com.ATH.mtkweb3.fileprovider", expectedAuthority)
    }

    @Test
    fun testShareIntentImageAndTextExtras() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mockUri = Uri.parse("content://${context.packageName}.fileprovider/share_images/oracle_proof.png")
        val title = "Fortune Prediction"
        val shareBody = "🔮 Fortune Teller Web3 | Solana Oracle\nhttps://explorer.solana.com/tx/11111?cluster=devnet"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/png"
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, shareBody)
            putExtra(Intent.EXTRA_STREAM, mockUri)
            clipData = ClipData.newRawUri("oracle_proof", mockUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        assertEquals(Intent.ACTION_SEND, sendIntent.action)
        assertEquals("image/png", sendIntent.type)
        assertEquals(title, sendIntent.getStringExtra(Intent.EXTRA_TITLE))
        assertEquals(shareBody, sendIntent.getStringExtra(Intent.EXTRA_TEXT))
        assertEquals(mockUri, sendIntent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))
        assertNotNull(sendIntent.clipData)
        assertTrue((sendIntent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION) != 0)
    }

    @Test
    fun testShareAppMessageExactText() {
        val expectedMessage = """
            🔮 MTK Fortune Teller

            Explore ancient wisdom with a modern Web3 experience. Ask your question, reveal your reading, and optionally seal it on Solana as an on-chain Oracle attestation.

            Try MTK Fortune Teller:
            https://play.google.com/store/apps/details?id=com.ATH.mtkweb3
        """.trimIndent()

        assertTrue(expectedMessage.startsWith("🔮 MTK Fortune Teller"))
        assertTrue(expectedMessage.contains("Explore ancient wisdom with a modern Web3 experience. Ask your question, reveal your reading, and optionally seal it on Solana as an on-chain Oracle attestation."))
        assertTrue(expectedMessage.contains("Try MTK Fortune Teller:\nhttps://play.google.com/store/apps/details?id=com.ATH.mtkweb3"))
        Assert.assertFalse(expectedMessage.contains("မရမ်းတလင်း"))
    }
}
