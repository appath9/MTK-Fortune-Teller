package com.example.model

enum class AppScreen {
    Loading,
    Intro,
    Home,
    SelectQuestion,
    AnimationPlaceholder,
    Result,
    History
}

enum class AccessTier {
    FREE,
    PAID_ACCESS,
    SEEKER_ELITE
}

enum class SealingState {
    IDLE,
    WALLET_CONNECTING,
    SIGNING_DEVNET_MEMO,
    RECONCILING,
    CONFIRMED,
    ERROR
}

data class WalkthroughStep(
    val stepNumber: Int,
    val titleMm: String,
    val titleEn: String,
    val messageMm: String,
    val messageEn: String,
    val iconEmoji: String
)

data class FortuneState(
    val currentScreen: AppScreen = AppScreen.Loading,
    val currentLang: String = "en", // Default forced to 'en' (English)
    val selectedQuestionId: String? = null,
    val pendingQuestionId: String? = null,
    val finalChoice: Int? = null, // Random choice (0-9)
    val lastIntroDate: String? = null,
    val introStep: Int = 0, // 0 to 4 (Step 1 to 5)
    val searchQuery: String = "",
    val isDrawerOpen: Boolean = false,
    val isHelpModalOpen: Boolean = false,
    val accessTier: AccessTier = AccessTier.FREE,
    val isMockSeekerElite: Boolean = false,
    val isWeb3Mode: Boolean = false,
    val freeReadingsRemaining: Int = 2,
    val prototypeReadingsRemaining: Int = 3,
    val isSubmittingTransaction: Boolean = false,
    val isReconcilingAttestation: Boolean = false,
    val attestationStatusText: String? = null,
    val attestationId: String? = null,
    val txSignature: String? = null,
    val txError: String? = null,
    val txTimestamp: Long? = null,
    val isOfferingInFlight: Boolean = false,
    val offeringStatusText: String? = null,
    val offeringId: String? = null,
    val offeringTxSignature: String? = null,
    val offeringConfirmed: Boolean = false,
    val offeringUnknownConfirmation: Boolean = false,
    val offeringError: String? = null,
    val sealingState: SealingState = SealingState.IDLE,
    val sealingError: String? = null,
    val isQuotaExhaustedDialogVisible: Boolean = false,
    val previousScreen: AppScreen? = null,
    val isResultRevealed: Boolean = false
) {
    val isSeekerElite: Boolean get() = accessTier == AccessTier.SEEKER_ELITE
}

val INTRO_STEPS = listOf(
    WalkthroughStep(
        stepNumber = 1,
        titleMm = "အဆင့် ၁",
        titleEn = "Step 1",
        messageMm = "မေးရန်အကြောင်း အမှန်တကယ်ရှိမှ မေးမြန်းပါလေ",
        messageEn = "Only ask when you genuinely have something to ask about.",
        iconEmoji = "🔮"
    ),
    WalkthroughStep(
        stepNumber = 2,
        titleMm = "အဆင့် ၂",
        titleEn = "Step 2",
        messageMm = "မေးခွန်းတစ်ခုတည်းကို အကြိမ်ကြိမ်ထပ်၍ မမေးရပါ။ မေးလျှင်လည်း တစ်ခုမျှ မှန်မည်မဟုတ်ပါ။",
        messageEn = "Do not ask the same question repeatedly. If you do, none of the answers will be correct.",
        iconEmoji = "⏳"
    ),
    WalkthroughStep(
        stepNumber = 3,
        titleMm = "အဆင့် ၃",
        titleEn = "Step 3",
        messageMm = "အမဲသား၊ ကျွဲသား မစားသူများမေးလျှင် ပို၍မှန်ပါသည်။ ထို့ကြောင့် တစ်သက်လုံးမရှောင်နိုင်တောင် မေးသောနေ့တွင် အမဲသား၊ ကျွဲသား မစားလျှင် ပို၍ကောင်းပါသည်။",
        messageEn = "The reading is said to be more accurate for people who do not eat beef or buffalo meat. Therefore, even if you cannot avoid them throughout your life, it is better not to eat beef or buffalo meat on the day you ask.",
        iconEmoji = "🌿"
    ),
    WalkthroughStep(
        stepNumber = 4,
        titleMm = "အဆင့် ၄",
        titleEn = "Step 4",
        messageMm = "မိမိသက်ဝင်ယုံကြည်နေသောဘုရားကို အမွှေးတိုင်ဖြစ်စေ၊ ဖယောင်းတိုင်ဖြစ်စေ၊ ပန်းဖြစ်စေ၊ ၎င်း (၃) မျိုးစလုံးဖြစ်စေ လှူဒါန်းရှိခိုးပြီးမှ မေးသင့်ပါသည်။",
        messageEn = "Before asking, you should make an offering and pay respects to the deity or Higher Being you sincerely believe in. You may offer incense, a candle, flowers, or all three.",
        iconEmoji = "🧘"
    ),
    WalkthroughStep(
        stepNumber = 5,
        titleMm = "အဆင့် ၅",
        titleEn = "Step 5",
        messageMm = "ရှေးဦးစွာ မေးခွန်းကို ဖွင့်ဟ၍မေးရပါမည်။ သို့တည်းမဟုတ် စိတ်တွင်းတွင် ရည်မှတ်၍ မေးရပါမည်။ မေးပြီးလျှင် ကံဇယားကွက်တွင် ထောက်ရပါမယ်။",
        messageEn = "First, ask your question out loud. Alternatively, you may ask it silently in your mind with clear intention. After asking, touch or point to the appropriate place on the fortune-telling chart.",
        iconEmoji = "✨"
    )
)
