package com.example

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.example.ui.model.FortuneSlipCardData
import com.example.ui.model.FortuneSlipStatus
import com.example.util.FortuneSlipShareHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class FortuneSlipShareHelperTest {

    @Test
    fun testEnglishShareTextWithCategoryAndLocalStatus() {
        val data = FortuneSlipCardData(
            appName = "MTK Fortune Teller",
            question = "Will I succeed in my career?",
            answer = "Great success lies ahead.",
            oracleNumber = 3,
            category = "Career",
            status = FortuneSlipStatus.LOCAL
        )

        val text = FortuneSlipShareHelper.buildShareText(data)

        assertTrue(text.contains("🔮 MTK Fortune Teller"))
        assertTrue(text.contains("🏷️ Category: Career"))
        assertTrue(text.contains("❓ Question:\nWill I succeed in my career?"))
        assertTrue(text.contains("✨ Oracle № 3 🔮"))
        assertTrue(text.contains("📜 Prediction:\nGreat success lies ahead."))
        assertTrue(text.contains("Status: ✦ LOCAL READING"))
        assertFalse(text.contains("Solana Devnet"))
    }

    @Test
    fun testBurmeseShareTextWithCategoryAndLocalStatus() {
        val data = FortuneSlipCardData(
            appName = "မင်းတုန်းမင်း ၃၆ ကွက် ဗေဒင်",
            question = "စီးပွားရေး အခြေအနေ မည်သို့ရှိသနည်း။",
            answer = "စီးပွားဥစ္စာ တိုးတက်ကြီးပွားပါလိမ့်မည်။",
            oracleNumber = 5,
            category = "စီးပွားရေး",
            status = FortuneSlipStatus.LOCAL
        )

        val text = FortuneSlipShareHelper.buildShareText(data)

        assertTrue(text.contains("🔮 မင်းတုန်းမင်း ၃၆ ကွက် ဗေဒင်"))
        assertTrue(text.contains("🏷️ အမျိုးအစား: စီးပွားရေး"))
        assertTrue(text.contains("❓ မေးမြန်းခဲ့သော မေးခွန်း:\nစီးပွားရေး အခြေအနေ မည်သို့ရှိသနည်း။"))
        assertTrue(text.contains("✨ အမှတ်စဉ် № ၅ 🔮"))
        assertTrue(text.contains("📜 ဗေဒင် ဟောကိန်းအဖြေ:\nစီးပွားဥစ္စာ တိုးတက်ကြီးပွားပါလိမ့်မည်။"))
        assertTrue(text.contains("Status: ✦ ပြည်တွင်း ဟောကိန်း"))
    }

    @Test
    fun testOracleNumberZeroAndNineFormatting() {
        val dataZero = FortuneSlipCardData(
            appName = "MTK Fortune Teller",
            question = "Sample question 0",
            answer = "Sample answer 0",
            oracleNumber = 0,
            status = FortuneSlipStatus.LOCAL
        )
        val textZero = FortuneSlipShareHelper.buildShareText(dataZero)
        assertTrue(textZero.contains("Oracle № 0"))

        val dataNineBurmese = FortuneSlipCardData(
            appName = "မင်းတုန်းမင်း ၃၆ ကွက် ဗေဒင်",
            question = "Sample question 9",
            answer = "Sample answer 9",
            oracleNumber = 9,
            status = FortuneSlipStatus.LOCAL
        )
        val textNine = FortuneSlipShareHelper.buildShareText(dataNineBurmese)
        assertTrue(textNine.contains("အမှတ်စဉ် № ၉"))
    }

    @Test
    fun testCategoryPresentAndAbsent() {
        val dataWithCategory = FortuneSlipCardData(
            appName = "MTK Fortune Teller",
            question = "Question A",
            answer = "Answer A",
            oracleNumber = 1,
            category = "Love",
            status = FortuneSlipStatus.LOCAL
        )
        val textWithCategory = FortuneSlipShareHelper.buildShareText(dataWithCategory)
        assertTrue(textWithCategory.contains("Category: Love"))

        val dataWithoutCategory = FortuneSlipCardData(
            appName = "MTK Fortune Teller",
            question = "Question B",
            answer = "Answer B",
            oracleNumber = 2,
            category = null,
            status = FortuneSlipStatus.LOCAL
        )
        val textWithoutCategory = FortuneSlipShareHelper.buildShareText(dataWithoutCategory)
        assertFalse(textWithoutCategory.contains("Category:"))
    }

    @Test
    fun testPendingStatusText() {
        val dataPending = FortuneSlipCardData(
            appName = "MTK Fortune Teller",
            question = "Question Pending",
            answer = "Answer Pending",
            oracleNumber = 4,
            status = FortuneSlipStatus.PENDING
        )
        val textPending = FortuneSlipShareHelper.buildShareText(dataPending)
        assertTrue(textPending.contains("Status: ◌ ON-CHAIN VERIFICATION PENDING"))
    }

    @Test
    fun testExplorerUrlIncludedOnlyForVerifiedStatus() {
        val explorerUrl = "https://explorer.solana.com/tx/11111111111111111111111111111111?cluster=devnet"

        // Verified status + Explorer URL provided -> MUST be included
        val dataVerified = FortuneSlipCardData(
            appName = "MTK Fortune Teller",
            question = "Verified Question",
            answer = "Verified Answer",
            oracleNumber = 7,
            status = FortuneSlipStatus.VERIFIED
        )
        val textVerified = FortuneSlipShareHelper.buildShareText(dataVerified, explorerUrl)
        assertTrue(textVerified.contains("Status: 🛡️ VERIFIED ON SOLANA DEVNET"))
        assertTrue(textVerified.contains("⚡ On-Chain Devnet Proof:"))
        assertTrue(textVerified.contains(explorerUrl))

        // LOCAL status + Explorer URL provided -> MUST NOT be included
        val dataLocal = FortuneSlipCardData(
            appName = "MTK Fortune Teller",
            question = "Local Question",
            answer = "Local Answer",
            oracleNumber = 7,
            status = FortuneSlipStatus.LOCAL
        )
        val textLocal = FortuneSlipShareHelper.buildShareText(dataLocal, explorerUrl)
        assertFalse(textLocal.contains(explorerUrl))
        assertFalse(textLocal.contains("On-Chain Devnet Proof"))

        // PENDING status + Explorer URL provided -> MUST NOT be included
        val dataPending = FortuneSlipCardData(
            appName = "MTK Fortune Teller",
            question = "Pending Question",
            answer = "Pending Answer",
            oracleNumber = 7,
            status = FortuneSlipStatus.PENDING
        )
        val textPending = FortuneSlipShareHelper.buildShareText(dataPending, explorerUrl)
        assertFalse(textPending.contains(explorerUrl))
        assertFalse(textPending.contains("On-Chain Devnet Proof"))
    }

    @Test
    fun testSaveBitmapToCacheAndFilename() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        val file = FortuneSlipShareHelper.saveBitmapToCache(context, bitmap, "test_slip.png")

        assertTrue(file.exists())
        assertEquals("test_slip.png", file.name)
        assertEquals(context.cacheDir, file.parentFile)
        assertTrue(file.length() > 0)
    }

    @Test
    fun testGetUriForCacheFile() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val file = File(context.cacheDir, "fortune_slip.png")
        file.writeText("fake image data")

        val uri = FortuneSlipShareHelper.getUriForCacheFile(context, file)

        assertNotNull(uri)
        assertEquals("content", uri.scheme)
        assertTrue(uri.authority?.contains("fileprovider") == true)
        assertEquals("${context.packageName}.fileprovider", uri.authority)
    }

    @Test
    fun testBuildShareIntentStructure() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mockUri = Uri.parse("content://${context.packageName}.fileprovider/share_images/fortune_slip.png")
        val title = "Fortune Slip Reading"
        val shareText = "Sample Fortune Share Body"

        val sendIntent = FortuneSlipShareHelper.buildShareIntent(
            context = context,
            title = title,
            shareText = shareText,
            imageUri = mockUri
        )

        assertEquals(Intent.ACTION_SEND, sendIntent.action)
        assertEquals("image/png", sendIntent.type)
        assertEquals(title, sendIntent.getStringExtra(Intent.EXTRA_TITLE))
        assertEquals(title, sendIntent.getStringExtra(Intent.EXTRA_SUBJECT))
        assertEquals(shareText, sendIntent.getStringExtra(Intent.EXTRA_TEXT))
        assertEquals(mockUri, sendIntent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))

        val clipData = sendIntent.clipData
        assertNotNull(clipData)
        assertEquals(mockUri, clipData?.getItemAt(0)?.uri)

        // Verify temporary read permission flag (and NO write permission)
        val flags = sendIntent.flags
        assertTrue((flags and Intent.FLAG_GRANT_READ_URI_PERMISSION) != 0)
        assertFalse((flags and Intent.FLAG_GRANT_WRITE_URI_PERMISSION) != 0)
    }

    @Test
    fun testCreateChooserIntent() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val mockUri = Uri.parse("content://${context.packageName}.fileprovider/share_images/fortune_slip.png")
        val sendIntent = FortuneSlipShareHelper.buildShareIntent(context, "Title", "Text", mockUri)

        val chooserIntent = FortuneSlipShareHelper.createChooserIntent(sendIntent, "Chooser Title")

        assertNotNull(chooserIntent)
        assertEquals(Intent.ACTION_CHOOSER, chooserIntent.action)
        assertTrue((chooserIntent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION) != 0)
    }
}
