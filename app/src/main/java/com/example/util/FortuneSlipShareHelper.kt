package com.example.util

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.ui.model.FortuneSlipCardData
import com.example.ui.model.FortuneSlipStatus
import java.io.File
import java.io.FileOutputStream

/**
 * Isolated presentation/share utility for rendering and sharing Fortune Slip cards.
 *
 * Handles share text construction, bitmap caching, FileProvider URI generation,
 * and ACTION_SEND intent creation using existing app FileProvider configuration.
 */
object FortuneSlipShareHelper {

    const val DEFAULT_FILENAME = "fortune_slip.png"

    /**
     * Determines whether the card data contains Burmese characters or app identity.
     */
    fun isBurmeseData(data: FortuneSlipCardData): Boolean {
        return data.appName.contains("မင်းတုန်းမင်း") ||
                data.question.any { it.code in 0x1000..0x109F } ||
                data.answer.any { it.code in 0x1000..0x109F }
    }

    /**
     * Builds a localized share title.
     */
    fun buildShareTitle(data: FortuneSlipCardData): String {
        return if (isBurmeseData(data)) "ဗေဒင် ဟောကိန်း မျှဝေရန်" else "Fortune Slip Reading"
    }

    /**
     * Builds formatted share text from [FortuneSlipCardData].
     *
     * Explorer URL is included ONLY when status is [FortuneSlipStatus.VERIFIED]
     * and a valid explorerUrl string is provided.
     */
    fun buildShareText(
        data: FortuneSlipCardData,
        explorerUrl: String? = null
    ): String {
        val isBurmese = isBurmeseData(data)
        val burmeseDigits = listOf("၀", "၁", "၂", "၃", "၄", "၅", "၆", "၇", "၈", "၉")

        val oracleDisplay = data.oracleNumber?.let { num ->
            if (isBurmese) burmeseDigits.getOrElse(num) { num.toString() } else num.toString()
        }

        val statusText = when (data.status) {
            FortuneSlipStatus.LOCAL -> if (isBurmese) "✦ ပြည်တွင်း ဟောကိန်း" else "✦ LOCAL READING"
            FortuneSlipStatus.VERIFIED -> if (isBurmese) "🛡️ SOLANA DEVNET အတည်ပြုပြီး" else "🛡️ VERIFIED ON SOLANA DEVNET"
            FortuneSlipStatus.PENDING -> if (isBurmese) "◌ SOLANA အတည်ပြုချက် စောင့်ဆိုင်းဆဲ" else "◌ ON-CHAIN VERIFICATION PENDING"
        }

        return buildString {
            appendLine("🔮 ${data.appName}")
            if (!data.category.isNullOrBlank()) {
                if (isBurmese) {
                    appendLine("🏷️ အမျိုးအစား: ${data.category}")
                } else {
                    appendLine("🏷️ Category: ${data.category}")
                }
            }
            appendLine()
            if (isBurmese) {
                appendLine("❓ မေးမြန်းခဲ့သော မေးခွန်း:")
                appendLine(data.question)
                if (oracleDisplay != null) {
                    appendLine("✨ အမှတ်စဉ် № $oracleDisplay 🔮")
                }
                appendLine("📜 ဗေဒင် ဟောကိန်းအဖြေ:")
                appendLine(data.answer)
            } else {
                appendLine("❓ Question:")
                appendLine(data.question)
                if (oracleDisplay != null) {
                    appendLine("✨ Oracle № $oracleDisplay 🔮")
                }
                appendLine("📜 Prediction:")
                appendLine(data.answer)
            }

            appendLine()
            appendLine("Status: $statusText")

            // Strict rule: Only include Explorer URL if status is strictly VERIFIED
            if (data.status == FortuneSlipStatus.VERIFIED && !explorerUrl.isNullOrBlank()) {
                appendLine()
                if (isBurmese) {
                    appendLine("⚡ Solana Devnet အတည်ပြုချက် သက်သေ:")
                } else {
                    appendLine("⚡ On-Chain Devnet Proof:")
                }
                append(explorerUrl)
            }
        }.trimEnd()
    }

    /**
     * Saves an Android [Bitmap] to a PNG file inside the app cache directory.
     */
    fun saveBitmapToCache(
        context: Context,
        bitmap: Bitmap,
        fileName: String = DEFAULT_FILENAME
    ): File {
        val cacheFile = File(context.cacheDir, fileName)
        FileOutputStream(cacheFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return cacheFile
    }

    /**
     * Resolves content URI using existing FileProvider authority (${packageName}.fileprovider).
     */
    fun getUriForCacheFile(context: Context, file: File): Uri {
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }

    /**
     * Constructs an Android ACTION_SEND intent with image/png, EXTRA_STREAM, ClipData, and temporary read permission.
     */
    fun buildShareIntent(
        context: Context,
        title: String,
        shareText: String,
        imageUri: Uri
    ): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/png"
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_STREAM, imageUri)
            clipData = ClipData.newRawUri("fortune_slip", imageUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    /**
     * Wraps the send intent in an Android Intent.createChooser.
     */
    fun createChooserIntent(sendIntent: Intent, chooserTitle: String): Intent {
        return Intent.createChooser(sendIntent, chooserTitle).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    /**
     * Helper to perform full share flow with an already-rendered Bitmap.
     */
    fun shareFortuneSlip(
        context: Context,
        data: FortuneSlipCardData,
        bitmap: Bitmap,
        explorerUrl: String? = null,
        fileName: String = DEFAULT_FILENAME
    ) {
        val shareText = buildShareText(data, explorerUrl)
        val title = buildShareTitle(data)
        val file = saveBitmapToCache(context, bitmap, fileName)
        val uri = getUriForCacheFile(context, file)
        val sendIntent = buildShareIntent(context, title, shareText, uri)
        val chooserIntent = createChooserIntent(sendIntent, title)
        context.startActivity(chooserIntent)
    }
}
