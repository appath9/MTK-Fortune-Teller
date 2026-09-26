package com.example.data

data class FortuneAnswer(
    val en: String,
    val mm: String
)

data class FortuneQuestion(
    val id: String,
    val en: String,
    val mm: String,
    val answers: Map<String, FortuneAnswer>
)

object FortuneDataProvider {
    // Standard Myanmar digit to Western digit and vice versa conversion for search matching
    fun normalizeSearch(text: String): String {
        return text
            .replace('၀', '0')
            .replace('၁', '1')
            .replace('၂', '2')
            .replace('၃', '3')
            .replace('၄', '4')
            .replace('၅', '5')
            .replace('၆', '6')
            .replace('၇', '7')
            .replace('၈', '8')
            .replace('၉', '9')
            .lowercase()
            .trim()
    }

    val fortuneData: List<FortuneQuestion> = listOf(
        FortuneQuestion(
            id = "Q1",
            en = "Will this couple have children?",
            mm = "ဇနီးမောင်နှံ၌ သားသမီးရ ကိန်းနှင့်ပတ်သက်သောအဟော။",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "They will have a good son who will do good for the world.",
                    mm = "ဤလောကကိုအကျိုးပြုမည့် သားကောင်းရတနာတစ်ဦး ရရှိပေလိမ့်မည်"
                ),
                "1" to FortuneAnswer(
                    en = "It is uncertain whether this couple will have children.",
                    mm = "ဇနီးမောင်နှံ၌သားသမီးရကိန်းမှာမသေချာပါ"
                ),
                "2" to FortuneAnswer(
                    en = "They will have a gifted son who becomes an outstanding person.",
                    mm = "အလွန်ထူးခြားသောပုဂ္ဂိုလ်ဖြစ်မည့် ပါရမီရှင်သားတစ်ဦး ရရှိပေလိမ့်မည်"
                ),
                "3" to FortuneAnswer(
                    en = "They will have a boy.",
                    mm = "ယောက်ျားလေးတစ်ဦးရရှိပေလိမ့်မည်"
                ),
                "4" to FortuneAnswer(
                    en = "They will hear pregnancy news from a woman, and soon after, they will have a child.",
                    mm = "မိန်းမတစ်ဦးထံမှ သားသမီးရသောသတင်းကြားပြီး မကြာခင်၌ သားသမီးရလိမ့်မည်"
                ),
                "5" to FortuneAnswer(
                    en = "They will definitely have children.",
                    mm = "သားသမီးရရန်သေချာပါတယ်"
                ),
                "6" to FortuneAnswer(
                    en = "A gifted soul will be reborn into this couple.",
                    mm = "ပါရမီရှင်တစ်ဦးသည် ဤဇနီးမောင်နှံ၌  ဝင်စားလိမ့်မည်"
                ),
                "7" to FortuneAnswer(
                    en = "They will have an extraordinary child, and after that, they will become wealthy.",
                    mm = "အလွန်ထူးခြားသော သားသမီးကို ရပေလိမ့်မည်။ သားသမီးရပြီးနောက် ဤဇနီးမောင်နှံ ချမ်းသာလိမ့်မည်။"
                ),
                "8" to FortuneAnswer(
                    en = "They will have good and virtuous children.",
                    mm = "သားကောင်းသမီးကောင်းများ ထွန်းကားပေလိမ့်မည်"
                ),
                "9" to FortuneAnswer(
                    en = "They will have children who bring benefits to their parents.",
                    mm = "မိဘကို အကျိုးပေးသော သားသမီးရရှိလိမ့်မည်"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q2",
            en = "Will this baby be a boy or a girl?",
            mm = "ဤပဋိသန္ဓေမှ ယောက်ျားလေးမွေးမည်လော မိန်းကလေးမွေးမည်လော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "It will be a boy with an easy birth – an extraordinary child.",
                    mm = "ယောက်ျားလေးဖြစ်ပါလိမ့်မည်။ မွေးဖွားရ လွယ်ကူပေလိမ့်မည်။ လွန်စွာထူးခြားသော ကလေးဖြစ်ပါသည်။"
                ),
                "1" to FortuneAnswer(
                    en = "It is too early to tell the gender, but I can already say the child will be extraordinary and gifted.",
                    mm = "ယောက်ျားလေးမွေးမည် မိန်းကလေးမွေးမည်ကိုဟောရန် အချိန်စောပါသေးသည်။ သို့ရာတွင် မွေးဖွားမည့်ကလေးမှာ အလွန်ထူးခြားသောပါရမီရှင်ဖြစ်ကြောင်း အခုကပင် ဟောလိုက်ပါသည်"
                ),
                "2" to FortuneAnswer(
                    en = "It will definitely be a boy.",
                    mm = "ယောက်ျားလေး မွေးမှာသေချာပါတယ်"
                ),
                "3" to FortuneAnswer(
                    en = "It will surely be a boy.",
                    mm = "ယောကျာ်းမွေးမည်မှာသေချာပါသည်"
                ),
                "4" to FortuneAnswer(
                    en = "It will surely be a boy, and he will be very mischievous.",
                    mm = "ယောင်္ကျားလေးမွေးမည်မှာသေချာပါသည်။ ထိုယောက်ျားလေးမှာ လွန်စွာဆိုးပေလိမ့်မည်။"
                ),
                "5" to FortuneAnswer(
                    en = "They will have a truly manly boy.",
                    mm = "ယောက်ျားပီသသော ယောက်ျား မျိုးမွေးဖွားပါလိမ့်မည်"
                ),
                "6" to FortuneAnswer(
                    en = "It is certain to be a boy.",
                    mm = "ယောက်ျားလေးဖြစ်မှာသေချာပါတယ်"
                ),
                "7" to FortuneAnswer(
                    en = "It will be a girl.",
                    mm = "မိန်းကလေးဖြစ်ပါလိမ့်မည်"
                ),
                "8" to FortuneAnswer(
                    en = "A boy who is an extraordinary being visiting this earth.",
                    mm = "ဤကမ္ဘာသို့ အလည်အပတ်ရောက်ရှိလာသော ထူးခြားသည့်ပုဂ္ဂိုလ်ယောင်္ကျားလေးတဦးဖြစ်ပါတယ်။"
                ),
                "9" to FortuneAnswer(
                    en = "It will be a boy.",
                    mm = "ယောက်ျားလေးဖြစ်ပါသည်"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q3",
            en = "Will this birth be a normal delivery or a C-section?",
            mm = "ဤကိုယ်ဝန်ကိုမွေးသည့်အခါ ရိုးရိုးမွေးရမည်လော ခွဲစိတ်၍မွေးရမည်လော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "No surgery is needed.",
                    mm = "ခွဲစိတ်မွေးရန်မလိုပါ"
                ),
                "1" to FortuneAnswer(
                    en = "A C-section will be needed.",
                    mm = "ခွဲစိပ်၍ မွေးရပါမည်"
                ),
                "2" to FortuneAnswer(
                    en = "A C-section will be needed.",
                    mm = "ခွဲစိပ်၍မွေးရပါမယ်"
                ),
                "3" to FortuneAnswer(
                    en = "It seems a C-section will be needed, but in the end, it will be a natural birth.",
                    mm = "ခွဲစိပ်ရမည်ဟု ထင်ရပါသည်၊၊ အမှန်တကယ်မွေးသောအခါ မခွဲစိတ်ပဲ မွေးရပေလိမ့်မည်"
                ),
                "4" to FortuneAnswer(
                    en = "It will be a C-section delivery.",
                    mm = "ခွဲစိတ်၍မွေးရပေလိမ့်မည်"
                ),
                "5" to FortuneAnswer(
                    en = "The birth will happen without surgery.",
                    mm = "မခွဲမစိတ်ပဲမွေးရပေလိမ့်မည်"
                ),
                "6" to FortuneAnswer(
                    en = "It will be a natural birth. On the delivery day, place a yellow flower in a pot beside the mother's bed.",
                    mm = "မခွဲစိတ်ပဲ မီးဖွားရပေလိမ့်မည်။ မွေးမည့်နေ့တွင် ပန်း၀ါ(၁)ပွင့်ကို မိခင်လောင်း၏ အိပ်ရာဘေးတွင် ပန်းအိုးဖြင့်ထိုးထားပါ"
                ),
                "7" to FortuneAnswer(
                    en = "It will not be a C-section.",
                    mm = "ခွဲစိပ်၍၍မွေးရမည်မဟုတ်ပါ"
                ),
                "8" to FortuneAnswer(
                    en = "The birth will happen without surgery.",
                    mm = "ခွဲစိတ်ခြင်းမပြုရပဲ မွေးပေလိမ့်မည်"
                ),
                "9" to FortuneAnswer(
                    en = "A C-section will be needed, but it will be an easy one.",
                    mm = "ခွဲစိတ်၍မွေးဖွားရပေလိမ့်မည်။ သို့သော်လွယ်လွယ်ကူကူ မွေးပါလိမ့်မည်"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q4",
            en = "Is this child a reincarnation?",
            mm = "မွေးဖွားလာမည့်ကလေးသည် လူဝင်စားဟုတ်တုံလော  မဟုတ်တုံလောဆိုသည်ကိုဟောသော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "No, it is not the reincarnation of the person you have in mind.",
                    mm = "လူဝင်စားဟုမပြောနိုင်ပါ။ သင်ထင်နေသောသူ ပြန်ဝင်စားခြင်းမဟုတ်ပါ"
                ),
                "1" to FortuneAnswer(
                    en = "It is a reincarnation, from a delta region.",
                    mm = "မွေးလာသောကလေးမှာလူဝင်စားဖြစ်ပါသည်။ မြစ်ဝကျွန်းပေါ် အရပ်ဒေသမှဖြစ်ပေလိမ့်မည်"
                ),
                "2" to FortuneAnswer(
                    en = "It is a reincarnation, coming from a wealthy life.",
                    mm = "လူဝင်စားဖြစ်ပါတယ်။ ချမ်းသာသောဘဝမှရောက်ရှိလာသူဖြစ်ပါသည်"
                ),
                "3" to FortuneAnswer(
                    en = "It is the reincarnation of a successful and rich person.",
                    mm = "လူဝင်စားတစ်ဦးဖြစ်ပါတယ်။ အောင်မြင်ချမ်းသာသောလူတစ်ဦး ပြန်လည်ဝင်စားခြင်းဖြစ်ပါတယ်"
                ),
                "4" to FortuneAnswer(
                    en = "It is a reincarnation.",
                    mm = "လူဝင်စားတစ်ဦးဖြစ်ပါသည်"
                ),
                "5" to FortuneAnswer(
                    en = "It is the reincarnation of a very wealthy person.",
                    mm = "လူဝင်စားဖြစ်ပါသည်။ လွန်စွာမှချမ်းသာသူတစ်ဦး ဝင်စားခြင်းဖြစ်ပါသည်"
                ),
                "6" to FortuneAnswer(
                    en = "It cannot be said to be a reincarnation.",
                    mm = "လူဝင်စားဟုမပြောနိုင်ပါ"
                ),
                "7" to FortuneAnswer(
                    en = "It is a reincarnation.",
                    mm = "လူဝင်စားတစ်ဦးဖြစ်ပါတယ်"
                ),
                "8" to FortuneAnswer(
                    en = "It is not a reincarnation.",
                    mm = "လူဝင်စားဟုမပြောနိုင်ပါ"
                ),
                "9" to FortuneAnswer(
                    en = "It is not a reincarnation as you think.",
                    mm = "သင်ထင်သကဲ့သို့ လူ၀င်စားမဟုတ်ပါ"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q5",
            en = "Will this child bring benefits to the parents?",
            mm = "မွေးဖွားလာသောကလေးသည် မိဘကိုအကျိုးပေးမည်လော/ မပေးမည်လောနှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "This child will not be beneficial to the parents.",
                    mm = "မိဘကိုအကျိုးပြုမည်မဟုတ်ပါ"
                ),
                "1" to FortuneAnswer(
                    en = "This child will be very beneficial to the parents.",
                    mm = "မိဘကိုလွန်စွာအကျိုးပေးမည့် သားသမီးဖြစ်ပေလိမ့်မည်"
                ),
                "2" to FortuneAnswer(
                    en = "The parents will benefit greatly.",
                    mm = "မိဘကိုကောင်းစွာ အကျိုးပေးပေလိမ့်မည်"
                ),
                "3" to FortuneAnswer(
                    en = "Not very beneficial – the child will go their own way when grown up.",
                    mm = "မိဘကို အကျိုးပေးလိမ့်မည်မဟုတ်ပါ။ အရွယ်ရောက်သည်နှင့်သူ့လမ်းသူသွားလိမ့်မည်။"
                ),
                "4" to FortuneAnswer(
                    en = "The parents will benefit greatly and will also gain much honor because of this child.",
                    mm = "မိဘကိုလွန်စွာအကျိုးပေးပေလိမ့်မည်။ ထို့အပြင်မိဘမှာ သူ့ကြောင့် များစွာ ဂုဏ်ရှိပေလိမ့်မည်"
                ),
                "5" to FortuneAnswer(
                    en = "This child will be very good to the parents.",
                    mm = "မိဘကိုကောင်းစွာအကျိုးပေးမည့်ကလေးဖြစ်သည်"
                ),
                "6" to FortuneAnswer(
                    en = "Not only will the child benefit the parents, but will also take good care of them.",
                    mm = "မိဘကိုကောင်းစွာ အကျိုးပေးသည့်အပြင် ကောင်းစွာလည်း စောင့်ရှောက်ပေးနေမည်"
                ),
                "7" to FortuneAnswer(
                    en = "This child will be beneficial to the parents.",
                    mm = "မိဘကိုအကျိုးပေးမည့်သားသမီးဖြစ်ပါသည်"
                ),
                "8" to FortuneAnswer(
                    en = "No other child will benefit the parents as much as this one.",
                    mm = "ဤကလေးလောက် မိဘကို အကျိုးပေးမည့် ကလေးရှိမည်မဟုတ်ပါ"
                ),
                "9" to FortuneAnswer(
                    en = "Not beneficial – the child will choose their own path when grown up.",
                    mm = "မိဘကိုအကျိုးပေးလိမ့်မည်မဟုတ်ပါ။ အရွယ်ရောက်သည်နှင့် သူ့လမ်းသူသွားပါလိမ့်မည်"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q6",
            en = "Will this child excel in education?",
            mm = "ထိုပဋိသန္ဓေမှ မွေးဖွားလာသည့်ကလေးသည် ပညာသင်ကြားရာ၌ ထူးချွန်မှုရှိမည် /မရှိမည်နှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "The parents will need to supervise this child's education closely.",
                    mm = "ဤကလေးသည် ပညာရေး၌ထူးချွန်ရန်မိဘကများစွာ ကြပ်မတ်ရပေလိမ့်မည်"
                ),
                "1" to FortuneAnswer(
                    en = "This child will face some difficulties in education.",
                    mm = "ဤကလေးသည် ပညာသင်ကြားရေး၌ အနည်းငယ် အခက်ခဲတွေ့ပေလိမ့်မည်"
                ),
                "2" to FortuneAnswer(
                    en = "The parents will need to put a lot of effort into guiding this child's education.",
                    mm = "ဤကလေးမှာ ပညာရေးအတွက်မိဘကများစွာထိန်းကျောင်းပေးရပေလိမ့်မည်"
                ),
                "3" to FortuneAnswer(
                    en = "This child will excel in education.",
                    mm = "ဤကလေးသည်ပညာရေး၌ ထူးချွန်ပေးလိမ့်မည်"
                ),
                "4" to FortuneAnswer(
                    en = "This child will excel greatly in education.",
                    mm = "ဤကလေးသည် ပညာရေး၌ များစွာထူးချွန်လိမ့်မည်"
                ),
                "5" to FortuneAnswer(
                    en = "The parents will need to put a lot of effort into guiding this child's education.",
                    mm = "ဤကလေးသည် ပညာသင်ကြားရာ၌ မိဘကများစွာ ထိန်းကျောင်းပေးရပေလိမ့်မည်"
                ),
                "6" to FortuneAnswer(
                    en = "The parents will need to watch over this child's education closely.",
                    mm = "ဤကလေးသည်ပညာသင်ကြားရေး၌ ထူးချွန်ရန် မိဘကများစွာ ကြည့်ရှုရပေလိမ့်မည်"
                ),
                "7" to FortuneAnswer(
                    en = "This child will surely excel in education.",
                    mm = "ဤကလေးသည် ပညာသင်ကြားရေး၌ ထူးချွန်သောကလေး မုချဖြစ်လိမ့်မည်"
                ),
                "8" to FortuneAnswer(
                    en = "This child will excel in education.",
                    mm = "ဤကလေးသည်ပညာရေး၌ ထူးချွန်မည့် ကလေးဖြစ်သည်/"
                ),
                "9" to FortuneAnswer(
                    en = "This child will surely excel in education.",
                    mm = "ဤကလေးသည်ပညာရေး၌ မုချထူးချွန်ပေလိမ့်မည်"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q7",
            en = "What about the current illness?",
            mm = "ရောဂါဖြစ်ပွားခြင်းနှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "It is time for the illness to end. After recovery, your finances will surely improve.",
                    mm = "ရောဂါပျောက်ကင်းချိန်တန်ပါပြီ။ ထိုရောဂါပျောက်ကင်းပြီးနောက် သင်လည်း စီးပွားရေး မုချကောင်းသွားပါလိမ့်မည်။"
                ),
                "1" to FortuneAnswer(
                    en = "This illness is just minor and will be cured soon.",
                    mm = "အခုဖြစ်ပွားနေသောရောဂါမှာ အသေးအဖွဲမျှသာဖြစ်ပြီး မကြာခင်ပျောက်ကင်းသွားလိမ့်မည်"
                ),
                "2" to FortuneAnswer(
                    en = "This illness will require serious treatment, but it will eventually be cured.",
                    mm = "ယခုဖြစ်ပွားသောရောဂါမှာ အကြိတ်အနယ် ကုသရပေလိမ့်မည်။ သို့ရာတွင်ပျောက်ကင်းချမ်းသာ သွားပေလိမ့်မည်"
                ),
                "3" to FortuneAnswer(
                    en = "This illness will be cured soon.",
                    mm = "ဖြစ်ပွားနေသောရောဂါမှာ မကြာခင်ပျောက်ကင်းသွားပါလိမ့်မည်"
                ),
                "4" to FortuneAnswer(
                    en = "This illness may cost a lot of money or exhaust the patient, but it will be cured.",
                    mm = "ဖြစ်ပွားနေသောရောဂါမှာ ငွေကုန်ချင်ကုန်မည် (သို့မဟုတ်) လူပင်ပန်းလျှင်ပင်ပန်းမည်။ သို့သော် ပျောက်ကင်းပါလိမ့်မယ်"
                ),
                "5" to FortuneAnswer(
                    en = "This illness will be cured soon – or try going on a trip.",
                    mm = "ယခုဖြစ်ပွားနေသောရောဂါမှာ မကြာခင် ပျောက်ပါလိမ့်မည် (သို့မဟုတ်) ခရီးတစ်ခုသွားလိုက်လျှင် ပျောက်သွားပါလိမ့်မည်"
                ),
                "6" to FortuneAnswer(
                    en = "There is nothing to worry about – it will be cured soon.",
                    mm = "ယခုဖြစ်ပွားနေသောရောဂါမှာ စိုးရိမ်စ၇ာမရှိပါ ။ မကြာခင် ပျောက်ကင်းသွားပါလိမ့်မည်"
                ),
                "7" to FortuneAnswer(
                    en = "This illness will be cured by a brown-skinned healer or doctor.",
                    mm = "ယခုဖြစ်ပွားနေသောရောဂါမှာ အသားညိုညိုဆေးဆရာ (သို့) ဆရာဝန်နှင့်ကုလျှင် ပျောက်ကင်းပေလိမ့်မည်"
                ),
                "8" to FortuneAnswer(
                    en = "Feed a black cat, offer a yellow flower to a shrine, and donate a walking stick to a monk. If you do these, the illness will be cured.",
                    mm = "ယခုဖြစ်ပွားနေသောရောဂါမှာ ကြောင်နက်ကိုအစာကြွေးလိုက်ပါ။ ပန်းဝါတပွင့်ဘုရားကိုလှူလိုက်ပါ။ တောင်ဝှေးတချောင်း ဘုန်းကြီးတစ်ပါးကိုလှူပါ။ ဤသို့ပြုလုပ်လျှင်ပျောက်ကင်းသွားပါလိမ့်မည်။"
                ),
                "9" to FortuneAnswer(
                    en = "This illness will be cured soon. If you hear news of a friend's wedding, it will heal even faster.",
                    mm = "ယခုဖြစ်ပွားနေတဲ့ရောဂါမှာ မကြာခင်ပျောက်ကင်းသွားပေလိမ့်မည်။ အသိမိတ်ဆွေတစ်ဦး မင်္ဂလာဆောင်တဲ့သတင်းကြားရလျှင် ထိုထက်မြန်မြန်ပျောက်ကင်းသွားပေလိမ့်မည်။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q8",
            en = "Are the current medications and diets helping to cure the illness?",
            mm = "ယခုစားနေသော ဆေးဝါးဓာတ်စာတို့သည် ရောဂါပျောက်ကင်းရန်  အထောက်ကူပြုပါ၏လော / မပြုပါ၏လောနှင့် ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "You will see improvement right after taking these medications and diets.",
                    mm = "ဤဆေးဝါးဓာတ်စာများ မှီဝဲပြီးသည်နှင့်တပြိုင်နက် သိသာပေလိမ့်မည်"
                ),
                "1" to FortuneAnswer(
                    en = "The current medications and diets are very helpful for curing the illness.",
                    mm = "ယခုမှီဝဲနေသော ဆေးဝါးဓာတ်စာတို့မှာ ရောဂါပျောက်ကင်းသွားရန် များစွာ အထောက်အကူပြုပါသည်။"
                ),
                "2" to FortuneAnswer(
                    en = "The current medications and diets are very helpful for curing the illness.",
                    mm = "ယခုမှီဝဲနေသော ဆေးဝါးဓာတ်စာတို့မှာ ရောဂါပျောက်ကင်းသွားရန် များစွာ အထောက်အကူပြုပါသည်။"
                ),
                "3" to FortuneAnswer(
                    en = "It is better to buy the medications and diets from a faraway place – the illness will be cured.",
                    mm = "ဆေးဝါးဓာတ်စာတို့ကို ရပ်ဝေးမှ ဝယ်ယူမှီဝဲလျှင် ပို၍ကောင်းပါသည်။ ရောဂါလည်းပျောက်ပါမည်။"
                ),
                "4" to FortuneAnswer(
                    en = "The cure will come faster if the medications and diets are bought by a young man or boy.",
                    mm = "ဆေးဝါးဓာတ်စာတို့ကို လူငယ်တစ်ဦးအား ဝယ်ခိုင်းပြီးမှမှီဝဲလျှင် ပို၍အပျောက်မြန်ပေလိမ့်မည်။"
                ),
                "5" to FortuneAnswer(
                    en = "The cure will come faster if the medications and diets are bought by a dark-skinned person.",
                    mm = "အသားမဲမဲလူတယောက်က ဝယ်ခိုင်းသော ဆေးဝါးဓာတ်စာများဖြစ်ပါက ရောဂါပျောက်ရန် ပို၍မြန်ပေလိမ့်မည်။"
                ),
                "6" to FortuneAnswer(
                    en = "The current medications and diets are quite helpful for curing the illness.",
                    mm = "ယခုမှီဝဲနေသောဆေးဝါးဓာတ်စာတို့သည် ရောဂါပျောက်ကင်းရန် များစွာထောက်ကူပြုပါတယ်"
                ),
                "7" to FortuneAnswer(
                    en = "If a newlywed person buys the medications and diets, the illness will be cured instantly.",
                    mm = "မင်္ဂလာဆောင်ပြီးစ လူတစ်ဦးအားဝယ်ခိုင်းသောဆေးဝါးဓာတ်စာများဖြစ်ပါက ရောဂါ ချက်ချင်းပျောက်ကင်းသွားပေလိမ့်မည်။"
                ),
                "8" to FortuneAnswer(
                    en = "These medications and diets will not only cure the illness but will also bring you wealth afterward.",
                    mm = "ဤဆေးဝါးဓာတ်စာများသည် ရောဂါပျောက်ကင်းရုံမျှမက ဓါတ်ရိုက်သကဲ့သို့ဖြစ်ပြီး ချမ်းသာသွားလိမ့်မည်။"
                ),
                "9" to FortuneAnswer(
                    en = "These medications and diets will work like magic medicine.",
                    mm = "ဤဆေးဝါးဓာတ်စာများသည် နတ်ဆေး နတ်၀ါးများကဲ့သို့အစွမ်းပြပါလိမ့်မယ်။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q9",
            en = "Is the upcoming major surgery safe and will it succeed?",
            mm = "အရေးကြီးသော ခွဲစိတ်ကုသမှု ပြုလုပ်ရန် ရှိပါသည်။ အန္တရာယ်ကင်းခြင်းရှိ၏ /မရှိ၏နှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "There is nothing to worry about – modern medicine has advanced enough for this surgery. Offer two candles to a female deity.",
                    mm = "ခွဲစိတ်ကုသမှုမှာ စိုးရိမ်စရာမရှိပါ။ ကမ္ဘာကြီးမှာတိုးတက်နေပြီဖြစ်၍ ဤခွဲစိတ်ကုသမှုမှာ ထမင်းစားရေသောက်ဖြစ်ပါသည်။ ဖယောင်းတိုင်နှစ်တိုင်ထွန်း၍ မယ်တော်တပါးပါးကို တိုင်တည်လိုက်ပါ။"
                ),
                "1" to FortuneAnswer(
                    en = "The surgery is safe and will succeed. No danger at all. Offer a glass of water mixed from two ponds to the shrine.",
                    mm = "ခွဲစိတ်ကုသမှုမှာအန္တရာယ်မရှိပါ။ အောင်မြင်ပါလိမ့်မည်။ အန္တရာယ်လည်းကင်းပါသည်။ ရေကန်နှစ်ကန်မှရေကို ဖန်ခွက်ထဲထည့်၍ မြတ်စွာဘုရားကိုကပ်လှူပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "This is an important surgery, so it is crucial to find a skilled surgeon. Donate four cups of milk.",
                    mm = "ခွဲစိတ်ကုသမှုမှာ အရေးကြီးသောကြောင့် ကျွမ်းကျင်သောဆရာဝန်ကို အတိအကျလေ့လာရန် အရေးကြီးပါသည်။ နွားနို့ (၄)ခွက် လှူလိုက်ပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "The surgery will go smoothly and succeed. There is no danger. Do a good deed, such as releasing birds, before the surgery.",
                    mm = "ခွဲစိတ်ကုသမှုမှာ ချောမောစွာ အောင်မြင်ပါသည်။ အန္တရာယ်မရှိပါ။ မပြုလုပ်မီ၌ ငှက်လွှတ်ပေးသော ကုသိုလ်ကို ပြုလုပ်ပေးပါ။"
                ),
                "4" to FortuneAnswer(
                    en = "The surgery is important. There is no danger, but the surgeon will need to put in great effort. Offer eight incense sticks to the shrine.",
                    mm = "ခွဲစိတ်ကုသမှုမှာအရေးကြီးပါသည်။ အန္တရာယ်မရှိဟုဆိုသော်လည်း ခွဲစိတ်ဆရာဝန်အဖို့ ကြိုးပမ်းရမည့်ကိစ်စဖြစ်ပါတယ်။ အမွှေးတိုင် (၈) တိုင် ဘုရားလှူပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "This is an important surgery. Place a knife upright in front of the shrine – it will succeed.",
                    mm = "ခွဲစိတ်ကုသမှုမှာရေးကြီးပါသည်။ ဘုရားစင်ရှေ့မှာ ဓားတချောင်းထောင်းထားပါ။ အောင်မြင်ပါလိမ့်မည်။"
                ),
                "6" to FortuneAnswer(
                    en = "The surgery will be a smooth success. The surgeon is very skilled.",
                    mm = "ခွဲစိတ်ကုသမှုမှာ ချောမွေ့စွာအောင်မြင်ပါလိမ့်မည်။ ခွဲစိတ်မည့်ဆရာဝန်မှာ အလွန် ကျွမ်းကျင်ပါသည်။"
                ),
                "7" to FortuneAnswer(
                    en = "The surgery will be a smooth success. No worries. The surgeon is heaven-sent. Nothing to fear.",
                    mm = "ခွဲစိတ်ကုသမှုမှာ ချောမွေ့စွာအောင်မြင်သွားပေလိမ့်မည်။ စိုးရိမ်စရာမရှိပါ။ ခွဲစိတ်ဆရာဝန်မှာ ကောင်းကင်မှ လွှတ်လိုက်သောသူဖြစ်ပါသည်။ ဘာမှပူစရာမလိုပါ။"
                ),
                "8" to FortuneAnswer(
                    en = "There is nothing to worry about – the surgery will succeed.",
                    mm = "ခွဲစိတ်ကုသမှုမှာစိုးရိမ်စရာမရှိပါ။ အောင်မြင်ပါလိမ့်မည်။"
                ),
                "9" to FortuneAnswer(
                    en = "There is absolutely nothing to worry about. Offer two candles to a female deity.",
                    mm = "ခွဲစိတ်ကုသမှုမှာ လုံးဝစိုးရိမ်စရာမလိုပါ။ မယ်တော်များကို ဖယောင်းဝတိုင် (၂)တိုင်ထွန်း၍ တိုင်တည်ပါ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q10",
            en = "Can the debts be settled soon?",
            mm = "သူတစ်ပါး၏ ကြွေးမြီ၊ အခြားသောကြွေးမြီ များရှိနေပါသည်။ အမြန်ဆုံးဆပ်နိုင်ခြင်းရှိ /မရှိနှင့်ဆိုင်သောအဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Not only will the debt be settled, but you may even be able to buy another house.",
                    mm = "ကြွေးကြေရုံမဟုတ်ပါဘူး။ အိမ်တလုံးထပ်ပြီး ဝယ်နိုင်ဦးမှာပါ"
                ),
                "1" to FortuneAnswer(
                    en = "Don't worry about the debt – just put in a little effort, and it will be settled.",
                    mm = "အကြွေးအတွက်မပူပါနဲ့။ နည်းနည်းလေးကြိုးစားပြီးတော့ ဆပ်ရမှာပါ၊၊ ကြေသွားမှာပါ"
                ),
                "2" to FortuneAnswer(
                    en = "No matter how much debt is left, don't worry – it will be settled in its own way.",
                    mm = "ကြွေးက ဘယ်လောက်များများကျန်သေးလို့လည်း။ မပူပါနဲ့။ သူ့နည်းသူ့ဟန်နဲ့ ကျေသွားမှာပါ"
                ),
                "3" to FortuneAnswer(
                    en = "Focus on repaying the debt, but don't worry – it will be settled.",
                    mm = "ကြွေးဆပ်မယ်လို့ အာရုံပြုထားပါ။ ဒီအတွက်တော့ မပူပါနဲ့။ ကြေသွားမှာပါ"
                ),
                "4" to FortuneAnswer(
                    en = "As a gesture of goodwill, invite a dark-skinned child from the street into your home and give them a meal. Then a way to settle the debts will appear.",
                    mm = "ဓာတ်ရိုက်တဲ့အနေနဲ့ လမ်းထဲက အသားမည်းမည်းကလေးတစ်ယောက်ကို အိမ်မှာခေါ်ပြီး ထမင်းတခါလောက် ကျွေးလိုက်စမ်းပါ။ သူ.အကြွေးတွေကြေဖို့ နည်းလမ်းပေါ်လာမှာပါ"
                ),
                "5" to FortuneAnswer(
                    en = "This is just a difficult time. You don't need to repay the debts yourself – heaven will take care of it.",
                    mm = "ဒီအချိန်လေး အကျပ်အတည်း တွေ့နေတာပါ။ သင့်ရဲ့အကြွေးတွေကို သင်ကိုယ်တိုင် ဆပ်ဖို့မလိုပါဘူး။ ကောင်းကင်ဘုံက ဆပ်ပေးပါလိမ့်မည်"
                ),
                "6" to FortuneAnswer(
                    en = "Why worry about the debt? It will all be settled after a nearby woman gives birth.",
                    mm = "သင့်ရဲ့အကြွေးတွေအတွက် ဘာဖြစ်နေတာလဲ။ အိမ်နားကမိန်းမတစ်ယောက် ကလေးမျက်နှာမြင်တဲ့ နမိတ်ပေါ်ပြီးရင် သင့်ရဲ့အကြွေးတွေ တပြားမကျန် ကျေသွားမှာပါ"
                ),
                "7" to FortuneAnswer(
                    en = "Don't worry about the debt – you will be able to settle it soon.",
                    mm = "သင့်အကြွေးတွေအတွက် မပူပါနဲ့။ မကြာခင် ဆပ်နိုင်မှာပါ"
                ),
                "8" to FortuneAnswer(
                    en = "Stay happy and don't think about the debts. They will be settled. Even if not, nothing bad will happen.",
                    mm = "ပျော်ပျော်နေစမ်းပါ။ ကြွေးတွေကိုခေါင်းထဲမထည့်ပါနဲ့။ ကြေသွားမှာပါ ။ မကြေလည်း ဘာမှမဖြစ်ပါဘူး"
                ),
                "9" to FortuneAnswer(
                    en = "Do you want to know about the debt? Don't even ask – just consider it already settled.",
                    mm = "ကြွေးအကြောင်း သိချင်လို့လား။ မမေးပါနဲ့။ ကြေပြီးလို့သာမှတ်လိုက်ပါတော့"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q11",
            en = "Will I win or lose the lawsuit?",
            mm = "တရားရုံးတွင် တရားတပေါင် ရင်ဆိုင်နေသည်။ ထိုတရားတပေါင်၌ မိမိနိုင်ခြင်း / ရှုံးခြင်းနှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "If you lose this case, the law would make no sense. Whatever happens, you will win.",
                    mm = "ဒီအမှုမှာသင်ကရှုံးသွားရင် တရားဥပဒေဆိုတာ အဓိပ္ပါယ်မရှိတော့ဘူး။ ဘာဘဲဖြစ်ဖြစ် သင်နိုင်မှာပါ"
                ),
                "1" to FortuneAnswer(
                    en = "Fill three club-shaped cups with water and offer them to the shrine. (Use club-cups, not glass cups.) The case will end within three days or three weeks – and you will win.",
                    mm = "ကလပ်သုံးလုံးမှာ ရေအပြည့်ထည့်ပြီး  ဘုရားတင်လိုက်ပါ ။ ကလပ်နော် ဖန်ခွက်မဟုတ်ဘူး။ 3 ရက် ဒါမှမဟုတ် 3ပတ်အတွင်း အမှုက ပြီးသွားမှာပါ။ ကိုယ်ပဲနိုင်မှာပါ"
                ),
                "2" to FortuneAnswer(
                    en = "Even if you lose, nothing bad will happen – but you will win.",
                    mm = "အမှုဖြစ်နေတယ်ဆိုတာကလည်း ပြောသာပြောရမှာပါ။ ဒီအမှုကလည်း ရှုံးသွားရင် ဘာမှဖြစ်မှာမဟုတ်ဘူး။ ဒါပေမဲ့လည်း နိုင်မှာပါ"
                ),
                "3" to FortuneAnswer(
                    en = "You will win the case, but one of your witnesses will betray you.",
                    mm = "သင်ရင်ဆိုင်နေရတဲ့အမှုဟာ နိုင်တော့နိုင်မှာပါ။ ဒါပေမယ့် သင့်ရဲ့သက်သေတစ်ယောက်က သစ္စာဖေါက်သွားလိမ့်မယ်"
                ),
                "4" to FortuneAnswer(
                    en = "Your side will surely win. Bring a child to court with you – you will win for sure.",
                    mm = "ဒီအမှုဟာ ကိုယ့်ဘက်က မုချနိုင်မှာပါ ။ တရားရုံးကို ကလေးတယောက်ခေါ်သွားပါ။ သင် မုချနိုင်ပါလိမ့်မယ်"
                ),
                "5" to FortuneAnswer(
                    en = "A young woman will bring you good news about winning the case – and you will definitely win.",
                    mm = "ဒီအမှုနိုင်မယ်ဆိုတဲ့ သတင်းကောင်းတစ်ခုကို မိန်းလေးတစ်ယောက် လာပြောလိမ့်မယ်။ နိုင်လည်းနိုင်မှာသေချာပါတယ်"
                ),
                "6" to FortuneAnswer(
                    en = "You will surely win. But the evidence may be rechecked – still, it will be fine.",
                    mm = "နိုင်မှာသေချာပါတယ်။ ဒါပေမယ့် သက်သေတွေကိုပြန်စစ်ဦးမှာလား။ မဆိုးပါဘူး"
                ),
                "7" to FortuneAnswer(
                    en = "You will surely win. You have hired a lawyer, haven't you? That lawyer is very capable.",
                    mm = "နိုင်မှသေချာပါတယ်။ သင် ရှေ့နေငှားထားတယ်မဟုတ်လား။ အဲဒီရှေ့နေဟာ အလွန်ကို တော်တဲ့လူပဲ"
                ),
                "8" to FortuneAnswer(
                    en = "The other side will withdraw the case. No need to worry. And even if they don't, you will win.",
                    mm = "ဒီအမှုကို တဖက်ကပြန်ပြီး ရုပ်သိမ်းသွားလိမ့်မည်။ ပူစရာမလိုပါဘူး။ မရုပ်သိမ်းလည်း ကိုယ်က နိုင်မှာပါ"
                ),
                "9" to FortuneAnswer(
                    en = "Donate a long walking stick to an old monk – then you will surely win.",
                    mm = "ဘုန်းကြီးအိုတစ်ပါးကို တောင်ဝှေးရှည်ကြီးတချောင်း လှူလိုက်ပါ။ ကိုယ်ကနိုင်မှာသေချာပါတယ်"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q12",
            en = "Will I lose any of my property?",
            mm = "သင်ပိုင်သော ပစ္စည်းကို စွန့်လွှတ်ရခြင်း၊ ဆုံးရှုံးရခြင်း ရှိမရှိနှင့်ဆိုင်သောအဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "No loss is seen. Consult someone who is far away – they will give you good advice. Just follow it.",
                    mm = "ဆုံးရှုံးဖို့လည်းမမြင်ပါဘူး။ ရပ်ဝေးရောက်နေတဲ့သူနဲ့ တိုင်ပင်လိုက်ပါ။ သူကသင့်ကိုအကြံဉာဏ်ပေးပါလိမ့်မယ်။ အဲဒီအတိုင်းလုပ်လိုက်ပါ။"
                ),
                "1" to FortuneAnswer(
                    en = "You won't lose even a single penny.",
                    mm = "ပွန်းနေတဲ့ ခြူးပြားလေးတစ်ပြားတောင် ဆုံးရှုံးစရာမရှိပါဘူး။"
                ),
                "2" to FortuneAnswer(
                    en = "No loss at all – only wealth will come.",
                    mm = "ဆုံးရှုံးဖို့မတွေ့ဖူး။ ချမ်းသာဖို့ပဲတွေ့တယ်။"
                ),
                "3" to FortuneAnswer(
                    en = "No loss at all.",
                    mm = "ဆုံးရှုံးဖို့ မတွေ့ပါဘူးဗျာ"
                ),
                "4" to FortuneAnswer(
                    en = "Even if there is a loss, you won't be alone – it will involve many others. But I see no loss.",
                    mm = "ရှုံးမယ်ဆိုရင်လည်း တစ်ယောက်ထဲမဟုတ်ဘူးဗျ။ အများနဲ့ခံရမှာ။ ဆုံးရှုံးစရာလဲမမြင်ပါဘူး"
                ),
                "5" to FortuneAnswer(
                    en = "You have good luck – how could you possibly lose anything?",
                    mm = "ကံကောင်းနေတယ်ဗျာ။ ဘယ်လိုလုပ်ပြီဆုံးရှုံးမှာလဲ။"
                ),
                "6" to FortuneAnswer(
                    en = "No loss – it's just a small change.",
                    mm = "ဆုံးရှုံးမှာမဟုတ်ပါဘူး။ အပြောင်းအလဲလေးဖြစ်တာပါ"
                ),
                "7" to FortuneAnswer(
                    en = "No loss is seen. Take a trip by water to wash away any bad luck.",
                    mm = "ဆုံးရှုံးဖို့မမြင်ပါဘူး။ ရေလမ်းခရီးထွက်လိုက်ပါလား။ အညံ့ရှိရင်လည်း ကြေသွားမှာပါ"
                ),
                "8" to FortuneAnswer(
                    en = "No loss is seen. Go and consult your devoted monk or spiritual teacher – he will tell you what to do.",
                    mm = "ဆုံးရှုံးဖို့မတွေ့ပါဘူး။ ကိုးကွယ်နေတဲ့ဆရာတော်ကြီးကို သွားပြီးလျှောက်လိုက်ပါ။ သူကဘယ်လိုလုပ်ရမယ်ဆိုတာ မိန်.မှာလိမ့်မယ်"
                ),
                "9" to FortuneAnswer(
                    en = "No loss is seen. Discuss it with the person you already have in mind – that person will be very helpful.",
                    mm = "ဆုံးရှုံးမှုမရှိပါဘူး။ သင့်အနေနဲ့ တိုင်ပင်မယ်လို့ယူဆထားတဲ့လူနဲ့ တိုင်ပင်လိုက်ပါ။ သူကပြောပြလိမ့်မယ်။ ဒီလူဟာအတော့်ကိုအသုံးဝင်တဲ့လူပဲ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q13",
            en = "What about the opposition from enemies or opponents?",
            mm = "ရန်သူ /ရန်ဘက်က အတိုက်အခံလုပ်ခြင်းနှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Don't underestimate your enemy – even a spark can burn down a building. Take it a little seriously. If you do, you will win.",
                    mm = "ရန်သူကို သေးတယ်ဆိုပြီးတော့မပေါ့နဲ့နော်။ မီးပွားဆိုတာ ပြသာဒ်အထိတက်ပြီးလောင်နိုင်တယ်။ နည်းနည်းဖိဖိစီးစီးလုပ်လိုက်ပါ။ အဲဒါဆိုရင်တော့သင် နိုင်မှာပါ။"
                ),
                "1" to FortuneAnswer(
                    en = "Not a real enemy – just someone who will cause you a little disappointment.",
                    mm = "သင့်ရန်သူဆိုတာကလည်း ဖြစ်ဖြစ်မြောက်မြောက် မဟုတ်ပါဘူး။ သင့်ကိုစိတ်အနှောက်ယှက်လာပြီးပေးရုံလေးပေးတာပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "Be a little careful – your enemy is like a wounded animal.",
                    mm = "နည်းနည်းတော့သတိထားပါ ရန်သူက ကျားနာခဲ ခဲလိမ့်မယ်။"
                ),
                "3" to FortuneAnswer(
                    en = "You don't need to worry about the enemy. Offer four yellow flowers at your home shrine around ten in the morning.",
                    mm = "သင့်ရန်သူကို သင်ကမှုရမှာမဟုတ်ပါဘူး။ ပန်းဝါလေးပွင့် မနက်ဆယ်နာရီလောက်မှာ အိမ်ဘုရားမှာ လှူလိုက်ပါ"
                ),
                "4" to FortuneAnswer(
                    en = "The enemy cannot stand against you – you will win.",
                    mm = "သင့်ရန်သူဟာသင့်ကိုမယှဉ်နိုင်ပါဘူး။ သင်ပဲနိုင်မှာပါ"
                ),
                "5" to FortuneAnswer(
                    en = "Offer nine incense sticks at a powerful shrine at nine o'clock on the ninth day. You will hear news of your enemy's downfall.",
                    mm = "အမွှေးတိုင်ကိုးတိုင် တန်ခိုးကြီးဘုရားမှာ 9 ရက်နေ့ 9နာရီမှာ လှူလိုက်ပါ။ သင့်ရန်သူ ကျဆုံးသွားတဲ့သတင်းကိုကြားရပါလိမ့်မည်"
                ),
                "6" to FortuneAnswer(
                    en = "Not really an enemy – just a minor nuisance. It will fade away on its own.",
                    mm = "ရန်သူလို့တောင်ပြောလို့မရပါဘူး ။ မပြောပလောက်တာလေးတွေပါ။ သူ့ဟာသူငြိမ်းသွားပါလိမ့်မယ်။"
                ),
                "7" to FortuneAnswer(
                    en = "Donate seven or seven hundred units of money to a beggar. Then stay calm and don't make any commotion. You will surely defeat your enemy.",
                    mm = "သူတောင်းစားကို ငွေခုနစ်ကျပ် (ဒါမှမဟုတ်) ငွေ 700ကျပ်လှူလိုက်ပါ။ ပြီးတော့ လောလောဆယ်အချိန်လေးမှာ ပူပူဆူဆူ မလုပ်ပါနဲ့။ ငြိမ်နေပါ။ သင့်ရန်သူကိုနိုင်မှာ သေချာပါတယ်။"
                ),
                "8" to FortuneAnswer(
                    en = "Wear ochre or brown-colored clothes for nine days – you will surely win.",
                    mm = "ယောဂီရောင်ဝတ်စုံလေး ၉ ရက်လောက်ဝတ်ထားလိုက်စမ်းပါ။ သင့်ရဲ့လူကိုနိုင်မှာ သေချာပါတယ်။"
                ),
                "9" to FortuneAnswer(
                    en = "The enemy can't even face you – they have big problems of their own at home. You will win.",
                    mm = "ရန်သူဟာ သင့်ကိုယှဉ်ပြိုင်ဖို့မပြောနဲ့။ သူတို့အိမ်တွင်းရေးပြဿနာက အတော့်ကိုကြီးနေတယ်။ သင် နိုင်မှာပါ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q14",
            en = "How is the person who is far away – are they doing well or not?",
            mm = "အဝေးရောက်နေသူ၏ကောင်းခြင်းဆိုးခြင်းနှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "It's not bad – they are just being asked some questions by the relevant people and need to give explanations. That's all.",
                    mm = "ညံ့တာတော့မဟုတ်ဖူးနော်။ သက်ဆိုင်ရာ မေးလားစစ်လားလုပ်နေတာ။ အဲဒါကို သူက ဖြေနေရတာ ဒါပါပဲ။"
                ),
                "1" to FortuneAnswer(
                    en = "That person is in poor health right now.",
                    mm = "အဝေးမှာရောက်နေတဲ့လူဟာ ကျန်းမာရေးညံ့နေတယ်"
                ),
                "2" to FortuneAnswer(
                    en = "That person is living happily and comfortably, near a large compound or garden.",
                    mm = "အဝေးရောက်နေတဲ့သူဟာ ချမ်းသာပျော်ရွှင်လျက်ရှိပါတယ်။ သူရောက်နေတဲ့နေရာဟာ ခြံကြီးတစ်ခြံ ပန်းခြံကြီးတစ်ခုနဲ့ နီးတယ်။"
                ),
                "3" to FortuneAnswer(
                    en = "No need to ask – everything is fine with that person.",
                    mm = "သူ့အတွက်မမေးပါနဲ့။ အားလုံးအဆင်ပြေနေတယ်။"
                ),
                "4" to FortuneAnswer(
                    en = "No need to worry about that person. If possible, offer three glasses of drinking water at a shrine.",
                    mm = "သူ့အတွက်ဘာမှမစိုးရိမ်ပါနဲ့။ ဖြစ်နိုင်ရင်တော့ ဖန်ခွက်သုံးလုံးနဲ့ ဘုရားကို သောက်တော်ရေသုံးခွက် လှူပေးပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "They are not actually in trouble – you are just imagining it. Everything will turn out fine.",
                    mm = "ဒုက္ခရောက်နေတာမဟုတ်ပါဘူး။ ဒုက္ခလို့ ထင်နေတာပါ။ အားလုံးကောင်းသွားမှာပါ။"
                ),
                "6" to FortuneAnswer(
                    en = "Everything is fine, but they may need to move to a different place.",
                    mm = "အားလုံးအဆင်ပြေပါတယ်။ ဒါပေမဲ့ နေရာရွှေ့ရမယ်ထင်တယ်။"
                ),
                "7" to FortuneAnswer(
                    en = "Everything is fine – they have even found a good companion.",
                    mm = "အားလုံးအဆင်ပြေနေပါတယ်။ အဖော်ကောင်းတစ်ယောက်တောင် တွေ့နေတယ်။"
                ),
                "8" to FortuneAnswer(
                    en = "Don't worry – a song of \"health, wealth, and safety\" is meant for them.",
                    mm = "မပူပါနဲ့။ \"ကျန်းကျန်းမာမာ ချမ်းချမ်းသာသာ ဘေးရန်ကာဆီးမည်\"ဆိုတဲ့သီချင်းဟာ သူ့အတွက်ဖြစ်နေပါပြီ"
                ),
                "9" to FortuneAnswer(
                    en = "Don't worry – they are in a very good situation.",
                    mm = "ဘာမှမပူပါနဲ့။ အလွန်ကောင်းတဲ့အခြေအနေရှိပါတယ်။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q15",
            en = "Will I get married soon?",
            mm = "မကြာမီ အိမ်ထောင်ကျခြင်း /အိမ်ထောင်မကျခြင်းနှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "You will surely get married – to a spouse with strong moral integrity.",
                    mm = "မုချအိမ်ထောင်ကျပေလိမ့်မည်။ သီလသမာဓိရှိသော အိမ်ထောင်ဘက်နှင့် ရပေလိမ့်မည်။"
                ),
                "1" to FortuneAnswer(
                    en = "You will surely get married this year – and your spouse will be a very good match for you.",
                    mm = "ဤနှစ်အတွင်း၌ မုချအိမ်ထောင်ကျလိမ့်မယ်။ အိမ်ထောင်ဘက်မှာ အလွန်သင့်တော်သော အိမ်ထောင်ဘက်ဖြစ်ပေလိမ့်မည်။"
                ),
                "2" to FortuneAnswer(
                    en = "No marriage this year – it will happen after the new year.",
                    mm = "ယခုနှစ်တွင်အိမ်ထောင်ကျရန်မရှိပါ။ နှစ်ကူးပြီးမှ ကျပေလိမ့်မည်။"
                ),
                "3" to FortuneAnswer(
                    en = "You will definitely get married – and your spouse will be a very good match for you.",
                    mm = "မုချ အိမ်ထောင်ကျလိမ့်မယ်။ အိမ်ထောင်ဘက်မှာ အလွန်သင့်တော်သော အိမ်ထောင်ဘက်ဖြစ်ပေလိမ့်မည်။"
                ),
                "4" to FortuneAnswer(
                    en = "You will find a very suitable spouse – one who is wealthy.",
                    mm = "အလွန်သင့်တော်သော အိမ်ထောင်ဖက်ဖြစ်လိမ့်မည်။ ချမ်းသာလိမ့်မည်။"
                ),
                "5" to FortuneAnswer(
                    en = "Marriage is not possible at this time.",
                    mm = "အိမ်ထောင်ကျရန်မဖြစ်နိုင်ပါ။"
                ),
                "6" to FortuneAnswer(
                    en = "You will get married – to the very person you first rejected before.",
                    mm = "အိမ်ထောင်ကျပေလိမ့်မည်။ သင် ပထမ ငြင်းဆိုထားသူနှင့်ပင် အိမ်ထောင်ကျလိမ့်မည်။"
                ),
                "7" to FortuneAnswer(
                    en = "You will definitely get married – to someone in the field of law or justice.",
                    mm = "မုချ အိမ်ထောင်ကျလိမ့်မည်။ တရားရေးနှင့် ပတ်သက်သောသူနှင့် အိမ်ထောင်ကျလိမ့်မည်။"
                ),
                "8" to FortuneAnswer(
                    en = "You will get married – that is certain.",
                    mm = "အိမ်ထောင်ကျပေလိမ့်မည်။ သေချာပါသည်။"
                ),
                "9" to FortuneAnswer(
                    en = "Haven't you already bought a home to live in after marriage?",
                    mm = "အိမ်ထောင်ကျပြီးလျှင် နေရန်အတွက် အိမ်ပင်ဝယ်ထားပြီးမဟုတ်ပါလော။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q16",
            en = "Will my spouse be beneficial to me or not?",
            mm = "သင်၏အိမ်ထောင်ဘက်သည် သင့်အား အကျိုးပေးခြင်း /မပေးခြင်းနှင့် ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Your spouse will often make you feel stressed and uneasy.",
                    mm = "သည့်အိမ်ထောင်ဘက်သည် သင့်အား မကြာခဏ စိတ်ကျဉ်းကြပ်အောင်လုပ်ပေလိမ့်မည်။"
                ),
                "1" to FortuneAnswer(
                    en = "Your spouse will be very beneficial to you.",
                    mm = "သင်၏အိမ်ထောင်ဘက်သည် သင့်ကို ကောင်းစွာအကျိုးပေးလိမ့်မည်။"
                ),
                "2" to FortuneAnswer(
                    en = "Your spouse will not be very beneficial to you.",
                    mm = "သင်၏အိမ်ထောင်ဘက်သည် သင့်ကို ကောင်းစွာအကျိုးပေးလိမ့်မည် မဟုတ်ပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "Not only will your spouse not benefit you, but they will also cause delays in your work.",
                    mm = "သင်၏အိမ်ထောင်ဘက်သည် သင့်အားအကျိုးမပေးသည့်အပြင် အလုပ်များကိုလည်း နှောင့်နှေးအောင် လုပ်ပေလိမ့်မည်။"
                ),
                "4" to FortuneAnswer(
                    en = "Not only will your spouse not benefit you, but they will also restrict you from doing many things.",
                    mm = "သင်၏အိမ်ထောင်ဘက်သည် သင့်အားအကျိုးမပေးရုံမျှမက ဟိုဟာမလုပ်ရ ဒီဟာမလုပ်ရဟု တားမြစ်ပေလိမ့်မည်။"
                ),
                "5" to FortuneAnswer(
                    en = "Your spouse will be very beneficial to you.",
                    mm = "သင်၏အိမ်ထောင်ဘက်သည် သင့်ကိုကောင်းစွာအကျိုးပေးပါလိမ့်မည်။"
                ),
                "6" to FortuneAnswer(
                    en = "Your spouse will often have different opinions from you on many matters.",
                    mm = "သင်၏အိမ်ထောင်ဘက်သည် အဆင့်သဖြင့် သင်နှင့် အယူအဆမတူဘဲရှိပေလိမ့်မည်။"
                ),
                "7" to FortuneAnswer(
                    en = "Your spouse will be very beneficial to you.",
                    mm = "သင်၏အိမ်ထောင်ဘက်သည် သင့်ကို ကောင်းစွာအကျိုးပြုလိမ့်မည်။"
                ),
                "8" to FortuneAnswer(
                    en = "Your spouse will not be beneficial to you.",
                    mm = "သင်၏အိမ်ထောင်ဘက်သည် သင့်ကိုအကျိုးမပေးသော အိမ်ထောင်ဘက်ဖြစ်သည်။"
                ),
                "9" to FortuneAnswer(
                    en = "Your spouse cannot be beneficial to you.",
                    mm = "သင်၏အိမ်ထောင်ဘက်သည် သင့်ကိုအကျိုးမပေးနိုင်ပေ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q17",
            en = "Will my financial situation be good at this stage of life?",
            mm = "ဤအသက်ပိုင်းတွင် စီးပွားရေးလက်ရွှင်ခြင်း ရှိ၏လော /မရှိ၏လောနှင့် ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Even lost properties will come back – don't worry at all. This is the time when your fortune turns around and starts fresh.",
                    mm = "ဆုံးရှုံးသွားတဲ့စီးပွားရေးတွေတောင် ပြန်ဖြစ်လာဦးမယ်။ လုံးဝမပူပါနဲ့။ ဒီအချိန်ဟာ ကံတရား ပြန်လည်ဆန်းသစ်ပြီး ပြန်လည်ကောင်းမွန်လာမှာပါ။"
                ),
                "1" to FortuneAnswer(
                    en = "Your financial situation is very good – this is an extraordinarily good time.",
                    mm = "စီးပွားရေးဟာ အလွန်ကိုကောင်းပါတယ်။ ထူးထူးခြားခြားကောင်းမယ့်အချိန်ဖြစ်ပါတယ်။"
                ),
                "2" to FortuneAnswer(
                    en = "Your finances will be good – don't worry.",
                    mm = "စီးပွားရေးကောင်းမှာပါ။ ဘာမှမပူပါနဲ့"
                ),
                "3" to FortuneAnswer(
                    en = "Not only will your finances be good, but you will also become wealthy.",
                    mm = "စီးပွားရေကောင်းရုံမဟုတ်ပါဘူး။ ချမ်းသာသွားမှာပါ"
                ),
                "4" to FortuneAnswer(
                    en = "Soon your finances will rise noticeably higher and higher.",
                    mm = "မကြာခင် သိသိသာသာကြီး စီးပွားတက်လာမှာပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "It is a little bad at this time.",
                    mm = "ဒီချိန်မှာ နည်းနည်းလေးညံ့တယ်။"
                ),
                "6" to FortuneAnswer(
                    en = "This is the best time – wealth and prosperity are coming.",
                    mm = "အကောင်းဆုံးအချိန်မှာပဲ။ ချမ်းသာကြွယ်ဝခြင်းဖြစ်ပါတော့မယ်။"
                ),
                "7" to FortuneAnswer(
                    en = "Don't worry about your finances – they will be good.",
                    mm = "စီးပွားရေးအတွက် မပူပါနဲ့။ ကောင်းမှာပါ။"
                ),
                "8" to FortuneAnswer(
                    en = "It is a little bad right now, but it will get better soon.",
                    mm = "ဒီအချိန်မှာနည်းနည်းညံ့နေတယ်။ ဒါပေမယ့် မကြာခင်ပြန်ကောင်းမှာပါ။"
                ),
                "9" to FortuneAnswer(
                    en = "Your finances are good, but a disloyal partner is causing a small setback.",
                    mm = "စီးပွားရေးကောင်းပါတယ်။ ဒါပေမယ့် လုပ်ဖော်ကိုင်ဖက်တဦးက ဖေါက်သွားတဲ့အတွက် နည်းနည်းလေး ညံ့သွားတာပါ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q18",
            en = "Is there any chance of unexpected large income or winning the lottery?",
            mm = "မမျှော်လင့်သော ငွေကြေးအစုလိုက်အပုံလိုက်ရခြင်း ( သို့မဟုတ်) ထီပေါက်ခြင်းရှိနိုင် /မရှိနိုင်နှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Try your luck together with a talkative woman – you are close to winning.",
                    mm = "စကားဗလောင်းဗလဲပြောတတ်တဲ့ မိန်းမတယောက်နဲ့စပ်ပြီး ထီထိုးစမ်းပါ။ ပေါက်ကိန်းကနီးနေပြီ"
                ),
                "1" to FortuneAnswer(
                    en = "You could win the lottery or receive a large unexpected income – this is a very good time.",
                    mm = "ဒီအချိန်မှာ ထီလဲပေါက်နိုင်ပါတယ်။ ငွေအစုလိုက်အပုံလိုက်လည်း ဝင်နိုင်တယ်။ အတော်ကောင်းတဲ့ အချိန်ပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "This is the kind of time when you could win the lottery. Try your luck when a child comes to your house.",
                    mm = "ထီပေါက်တတ်တဲ့အချိန်မျိုးပါပဲ။ အိမ်မှာကလေးတစ်ယောက် ရောက်လာရင် ထိုးပေတော့။"
                ),
                "3" to FortuneAnswer(
                    en = "You will surely win the lottery. Choose a ticket with many 4s in the number.",
                    mm = "မုချ ထီပေါက်တော့မယ်။ 4ဂဏန်းများများပါတဲ့ထီလက်မှတ်ကို ရွေးထိုးပါ။"
                ),
                "4" to FortuneAnswer(
                    en = "Ask a dark-skinned young person to buy a lottery ticket for you – you could win. There are also other chances of large income.",
                    mm = "အသားမဲမဲလူငယ်လေးတယောက်ကိုသွားပြီး ထီထိုးခိုင်းစမ်းပါ။ ထီလည်းပေါက်နိုင်တယ်။ တခြားနည်းနဲ့လည်း ငွေအစုလိုက်အပုံလိုက် ရနိုင်ပါတယ်။"
                ),
                "5" to FortuneAnswer(
                    en = "There is a chance to win the lottery next month.",
                    mm = "နောက်လမှာ ထီပေါက်ကိန်းရှိတယ်။"
                ),
                "6" to FortuneAnswer(
                    en = "You could receive a large unexpected income.",
                    mm = "ငွေအစုလိုက်အပုံလိုက်ရနိုင်ပါတယ်"
                ),
                "7" to FortuneAnswer(
                    en = "Next year definitely holds a chance to win the lottery – do nothing this year.",
                    mm = "နောက်နှစ်မှာတော့ ထီပေါက်ကိန်းသေချာနေပြီ။ ဒီနှစ်မှာတော့ ဘာမှမလုပ်ပါနဲ့။"
                ),
                "8" to FortuneAnswer(
                    en = "Recite a loving-kindness chant and buy lottery tickets on the 6th, 15th, or 24th of the month – there is a chance to win.",
                    mm = "မေတ္တာသုတ်ရွတ်ပြီး အင်္ဂလိပ် 6 ရက်နေ့ 15 ရက်နေ့ 24 ရက်နေ့တွေမှာ ထီထိုးစေချင်ပါတယ်။ ထီပေါက်ကိန်းရှိပါတယ်။"
                ),
                "9" to FortuneAnswer(
                    en = "Wait for a good sign before buying lottery tickets – the chance to win is there.",
                    mm = "နမိတ်ကောင်းလေးစောင့်ပြီး ထီထိုးစေချင်ပါတယ်။ ထီပေါက်ကိန်းကရှိနေတယ်။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q19",
            en = "Will I find a good place to live?",
            mm = "ယခုအခါ၌ နေထိုင်ရန်အိမ်ကို ရှာနေသည်။ ကောင်းသောနေရာထိုင်ခင်းကိုရရှိမည်/ မရရှိမည်နှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "You will find some places, but when you like one, the owner doesn't; and when the owner likes one, you don't.",
                    mm = "အိမ်တွေ နေရာတွေကတော.တွေ့ပါတယ်၊၊ ဒါပေမယ့် ကိုယ်ကကြိုက်ပြီး သူတို့ကမကြိုက်ဘူးဖြစ်နေတယ်။ အဲဒီလိုပဲ သူတို့ကကြိုက်ပြီး ကိုယ်ကမကြိုက်ဘူး ဖြစ်နေတယ်။"
                ),
                "1" to FortuneAnswer(
                    en = "A brown-skinned person will help you find a good house in a good location.",
                    mm = "အသားညိုညိုလူတယောက် ရှာပေးလိမ့်မည်။ ကောင်းတဲ့အိမ် ကောင်းတဲ့နေရာ ရလိမ့်မယ်။"
                ),
                "2" to FortuneAnswer(
                    en = "Through a young person, you will find a place – and it will be very good.",
                    mm = "လူငယ်တစ်ယောက်ကို အကြောင်းပြုပြီး နေရာထိုင်ခင်းရပါလိမ့်မည်။ အလွန်ကောင်းတယ်။"
                ),
                "3" to FortuneAnswer(
                    en = "A person with strong moral integrity will help you find a very good place.",
                    mm = "သီလသမာဓိရှိတဲ့ ပုဂ္ဂိုလ် ရှာပေးလိမ့်မယ်။ အလွန်ကောင်းတဲ့ နေရာထိုင်ခင်းကို ရလိမ့်မယ်။"
                ),
                "4" to FortuneAnswer(
                    en = "You will get the best place – don't worry at all.",
                    mm = "အကောင်းဆုံးနေရာထိုင်ခင်းကို ရပါလိမ့်မယ်။ ဘာမှမပူပါနဲ့။"
                ),
                "5" to FortuneAnswer(
                    en = "You will find a place, but it may not be exactly what you want.",
                    mm = "နေရာထိုင်ခင်းကတော့ရပါလိမ့်မယ်။ ဒါပေမယ့် စိတ်တိုင်းကျ မဖြစ်နိုင်ပါဘူး။"
                ),
                "6" to FortuneAnswer(
                    en = "Within nine days, you will find a very good place.",
                    mm = "9 ရက်အတွင်း အလုပ်ကောင်းတဲ့ နေရာထိုင်ခင်းကို ရလိမ့်မယ်။"
                ),
                "7" to FortuneAnswer(
                    en = "Don't worry – you will find a nice place with good neighbors.",
                    mm = "ဘာမှမပူပါနဲ့။ အိမ်နီးနားချင်းကောင်းရှိတဲ့ နေရာမှာနေဖို့ အဆင်ပြေသွားလိမ့်မယ်။"
                ),
                "8" to FortuneAnswer(
                    en = "Finding a place at this time is not easy – you will need to try a little harder.",
                    mm = "ဒီအချိန်မှာနေရာထိုင်ခင်း ရှာရတာ မလွယ်ဘူးနော်။ နည်းနည်းတော့ကြိုးစားရလိမ့်မယ်။"
                ),
                "9" to FortuneAnswer(
                    en = "Don't worry about finding a place – donate 6 or 60 units of money to a beggar, and then you will find one.",
                    mm = "နေရာထိုင်ခင်းအတွက်မပူပါနဲ့။ သူတောင်းစားကို ငွေ 6 ကျပ် (သို့မဟုတ်) ငွေ 60 ကျပ် လှူပြီးရှာလိုက်ပါ။ ရပါလိမ့်မယ်။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q20",
            en = "There is a dispute about the house – should I stay or move out?",
            mm = "အိမ်နှင့်ပတ်သက်၍ အချင်းများနေသည်။ မိမိက ဆက်လက်နေထိုင်ရမည် / မနေထိုင်ရမည်နှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Hang a picture of a yogi, hermit, or holy person at the head of your bed – the plan to move out will fall through.",
                    mm = "ဝ\tယောဂီတစ်ဦးဖြစ်စေ ရသေ့တစ်ဦးဖြစ်စေ တစ်ဦးဦးရဲ့ပုံကို ခေါင်းရင်းမှာချိတ်လိုက်ပါ။ သင်လို့ပြောင်းရမယ်. အစီအစဉ်ပျက်သွားပါလိမ့်မယ်။"
                ),
                "1" to FortuneAnswer(
                    en = "Whatever happens, you will still live there.",
                    mm = "ဘာပဲဖြစ်ဖြစ်ဆက်ပြီးနေရမှာပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "Place a walking stick upright on the house – you will continue living there.",
                    mm = "အိမ်ပေါ်မှာ တောင်ဝှေးတချောင်း ထောင်ထားလိုက်ပါ။ ဆက်ပြီးနေရမှာပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "How many times has this happened before? In the end, you won't actually move out.",
                    mm = "အခုလိုပြောင်းရမယ်ဖြစ်တာ ဘယ်နှစ်ကြိမ်ရှိပြီလဲ။ တကယ်လည်းမပြောင်းရပါဘူး။"
                ),
                "4" to FortuneAnswer(
                    en = "There is no reason to move out. Whatever happens, you will stay. Offer three glasses of drinking water at the shrine.",
                    mm = "ပြောင်းဖို့အကြောင်းမရှိပါဘူး။ ဘာတွေပဲဖြစ်နေဖြစ်နေ ဆက်ပြီးနေရမှာပါ။ ဖန်ခွက် 3လုံးနဲ့ ဘုရားသောက်တော်ရေလှူလိုက်ပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "Draw 18 small stars on the house, then bathe with water mixed from two ponds – you won't need to move.",
                    mm = "အိမ်မှာကြယ်ကလေး 18လုံးရေးလိုက်ပါ။ ပြီးတော့ ရေကန်နှစ်ကန်ကခပ်တဲ့ရေကို ပေါင်းပြီးလောင်းချိုးလိုက်ပါ။ ပြောင်းစရာမလိုတော့ပါဘူး။"
                ),
                "6" to FortuneAnswer(
                    en = "There is absolutely no reason to move out. Happiness and success will still come to this house.",
                    mm = "လုံးဝပြောင်းဖို့ အကြောင်းမရှိပါဘူး။ ပျော်ရွှင်မှုတွေ အောင်မြင်မှုတွေက သည်အိမ်ပေါ်မှာရဦးမှာပါ။"
                ),
                "7" to FortuneAnswer(
                    en = "No need to move out – you will stay. There may be some frustration, but you will become wealthy in the end.",
                    mm = "ပြောင်းစရာမရှိပါဘူး။ အဲဒီအိမ်မှာဆက်ပြီးနေရမှာပါ။ စိတ်တော့နည်းနည်းညစ်ရမယ်။ ဒါပေမဲ့ချမ်းသာသွားလိမ့်မယ်။"
                ),
                "8" to FortuneAnswer(
                    en = "Is there a dark-skinned person in your current household? If yes, no need to move. If not, it would be good to invite one to stay for a while.",
                    mm = "သင်တို့အခုနေတဲ့မိသားစုထဲမှာအသားမဲမဲလူတစ်ယောက်ရှိတယ်မဟုတ်လား။ အဲဒီလိုရှိတယ်ဆိုရင်တော့ ပြောင်းစရာမလိုပါဘူး။ တကယ်လို့မရှိဘူးဆိုရင်တော့ အသားမဲမဲလူတစ်ယောက် ခဏလောက်ခေါ်ထားလိုက်ရင် ကောင်းပါတယ်။"
                ),
                "9" to FortuneAnswer(
                    en = "No need to move out. There is a young person among those asking you to move – they will object, so you won't have to leave.",
                    mm = "ပြောင်းစရာမလိုပါဘူး။ သင်တို့ကိုပြောင်းခိုင်းတဲ့လူတစ်စုမှာ လူငယ်တစ်ယောက်ပါတယ်။ သူက ကန့်ကွက်လို့ သင်တို့မပြောင်းဖြစ်ပါဘူး။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q21",
            en = "Do I have the luck to live in an excellent house in this life?",
            mm = "မိမိတွင် ဤတသက်၌ အိမ်ကောင်းရာကောင်းနှင့် နေထိုင်ရမည့်ကံ ပါချင်း / မပါခြင်းနှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "In your younger years, you will move around a lot – but as you grow older, you will settle calmly in a good house.",
                    mm = "ငယ်စဉ်မှာတော့ဟိုပြောင်းဒီရွှေ့ပေါ့ဗျာ။ အသက်ကြီးလာရင်တော့ အိမ်ကောင်းရာကောင်းနဲ့ တည်တည်ငြိမ်ငြိမ် နေရမှာပါ"
                ),
                "1" to FortuneAnswer(
                    en = "Yes, you have that luck – don't worry.",
                    mm = "၎င်းကံပါပါသည်။ ပူစရာမလိုပါဘူး"
                ),
                "2" to FortuneAnswer(
                    en = "You are definitely destined to live in a good house.",
                    mm = "အိမ်ကောင်းရာကောင်းနှင့်နေရမည့်ကံ မုချပါပါသည်"
                ),
                "3" to FortuneAnswer(
                    en = "Yes, you have that luck.",
                    mm = "၎င်းကံပါပါသည်"
                ),
                "4" to FortuneAnswer(
                    en = "You are a fortunate one who will live in a good house.",
                    mm = "အိမ်ကောင်းရာကောင်း နဲ့နေရမယ့် ကံထူးရှင်ပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "You are destined to move frequently from place to place.",
                    mm = "မကြာခဏဟိုပြောင်းဒီပြောင်းလုပ်ရမယ့် ကံပါတယ်"
                ),
                "6" to FortuneAnswer(
                    en = "You have the luck to live in a good house – don't worry.",
                    mm = "အိမ်ကောင်းရာကောင်းတဲ့နေ့ဖို့ ကံပါပါတယ်။ မပူပါနဲ့"
                ),
                "7" to FortuneAnswer(
                    en = "Not only will you have a good house, but also good neighbors – it's your destiny.",
                    mm = "အိမ်ကောင်းရာကောင်းရရုံမဟုတ်ဘူး။ အိမ်နီးနားချင်းကောင်းကိုရအုံးမယ်။ ကံကပါတာကိုး"
                ),
                "8" to FortuneAnswer(
                    en = "You may live in a good house, but household matters may not be peaceful.",
                    mm = "အိမ်ရာကောင်းနဲ့ နေချင်နေရမယ်။ ဒါပေမယ့်အိမ်တွင်းရေးက မသာယာဘူး။"
                ),
                "9" to FortuneAnswer(
                    en = "You have little luck for living in a great or excellent house.",
                    mm = "အိမ်ကြီးအိမ်ကောင်းတဲ့နေ့ဖို့ ကံနည်းတယ်ဗျ"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q22",
            en = "Is my workplace or shop stable?",
            mm = "မိမိ၏ဆိုင် (သို့မဟုတ်) အလုပ်လုပ်သောနေရာသည် တည်မြဲ၏လော /မတည်မြဲ၏လောနှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "This is just to cause you stress – no need to move at all.",
                    mm = "ဝ\tစိတ်ညစ်အောင်လုပ်တာပါ။ ပြောင်းစရာမလိုပါဘူး။"
                ),
                "1" to FortuneAnswer(
                    en = "Why wouldn't it be stable? It will be.",
                    mm = "ဘာဖြစ်လို့မတည်မြဲရမှာလဲဗျာ။"
                ),
                "2" to FortuneAnswer(
                    en = "It will be stable – don't worry.",
                    mm = "တည်မြဲပါတယ်ဗျာ ။ ဘာမှမပူပါနဲ့"
                ),
                "3" to FortuneAnswer(
                    en = "There may be a nearby relocation.",
                    mm = "အနီးအပါးအပြောင်းအရွှေ့တော့ရှိတယ်ဗျ"
                ),
                "4" to FortuneAnswer(
                    en = "A move may come up. To prevent it, take a trip by water – then it will stay stable.",
                    mm = "ပြောင်းရွှေ့ရမယ့်ကိစ္စပေါ်လိမ့်မယ်။ မပေါ်အောင် ယတြာချေတဲ့အနေနဲ့ ရေကြောင်းခရီးလေးတစ်ခုသွားလိုက်ပါလား။ အဲဒါဆိုရင် မြဲသွားမှာပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "There is no reason to move – it will be stable.",
                    mm = "ပြောင်းစရာအကြောင်းမရှိပါဘူး ။ မြဲမှာပါ။"
                ),
                "6" to FortuneAnswer(
                    en = "You are thinking of asking someone to speak on your behalf to stop the move, aren't you? Go ahead and ask – it won't move.",
                    mm = "မပြောင်းရအောင် လူတစ်ယောက်ကို ပြောခိုင်းမယ်လို့ ကြံထားတယ် မဟုတ်လား။ ပြောပစ်လိုက်စမ်းပါ။ မပြောင်းရတော့ဘူး။"
                ),
                "7" to FortuneAnswer(
                    en = "No move – but the chance is small. Draw and hang a picture of a knife piercing a red heart – it will stay.",
                    mm = "မပြောင်းရပါဘူး။ နည်းပါတယ်။ အသည်းနီရဲရဲပေါ်မှာ ဓါးစိုက်နေတဲ့ပုံကို ရေးပြီးချိတ်ထားလိုက်စမ်းပါ။ မပြောင်းရတော့ဘူး"
                ),
                "8" to FortuneAnswer(
                    en = "No need to move – it will be stable.",
                    mm = "ပြောင်းဖို့မလိုပါဘူး။ မြဲမှာပါ။"
                ),
                "9" to FortuneAnswer(
                    en = "There will be serious discussions, but you won't have to move yet.",
                    mm = "အကြိတ်အနယ်တော့ ပြောရမှာပေါ့ဗျာ။ ဒါပေမဲ့ မပြောင်းရသေးပါဘူး။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q23",
            en = "What about the name and logo of my shop/business?",
            mm = "မိမိလုပ်ကိုင်ရမည့်လုပ်ငန်း၌ မှည့်ခေါ်ရမည်အမည်နှင့် အသုံးပြုရမည့် လိုဂိုတံဆိပ်နှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Choose a name that means \"rebirth\" or \"rising again\" – like Phoenix or Awakening. Use a cartwheel as your logo.",
                    mm = "သင်၏လုပ်ငန်းကို တစ်ကြော့ပြန်ပြီး နိုးထလာခြင်းအဓိပ္ပါယ်ရောက်အောင် အမည်ပေးပါ။ လိုဂိုတံဆိပ်ကို လှည်းဘီးပုံသဏ္ဍာန်ပြုလုပ်ပါ။ ဥပမာအားဖြင့် ဆုံဆည်းရာ တစ်ကျော့ပြန် ဆုံနေကျ နိုးထခြင်း စသည်တို့ကဲ့သို့ ဖြစ်ရပါမည်။"
                ),
                "1" to FortuneAnswer(
                    en = "Choose a name that includes one of these letters: A, E, O, Ay, or Hon. Use a peeled coconut as your logo.",
                    mm = "သင်၏လုပ်ငန်းကို (အ၊ ဣ၊ ဥ၊ ဧ၊ သြ) စာလုံးတစ်လုံးပါအောင်အမည်မှည့်ပါ။ လိုဂိုတံဆိပ်ကို အခွံချွတ်ပြီးသား အုန်းသီးတစ်လုံး၏ပုံကို အသုံးပြုပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "Choose a name that includes one of these letters: K, Kh, G, Ga, or Ng. Use two silver coins as your logo.",
                    mm = "သင့်လုပ်ငန်းကို ( က၊ ခ၊ ဂ၊ ဃ၊ င) စာလုံးတစ်လုံးပါအောင်အမည်ပေးပါ။ လိုဂိုတံဆိပ်ကို ငွေဒင်္ဂါးနှစ်ပြားပုံကို အသုံးပြုပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "Choose a name that includes one of these letters: S, Se, Z, Ze, or Ny. Use a picture of a book as your logo.",
                    mm = "သင်၏လုပ်ငန်းကို (စ၊ ဆ၊ ဇ၊ ဈ၊ ည) စာလုံးတစ်လုံးပါအောင်အမည်ပေးပါ။ လိုဂိုတံဆိပ်ကို စာအုပ်တအုပ်၏ပုံကိုအသုံးပြုပါ။"
                ),
                "4" to FortuneAnswer(
                    en = "Choose a name that includes one of these letters: Y, R, L, or W. Use a picture of four fingers raised up as your logo.",
                    mm = "သင်၏လုပ်ငန်းကို (ယ၊ ရ၊ လ၊ ဝ) စာလုံးတလုံးပါအောင်အမည်ပေးပါ။ လိုဂိုတံဆိပ်ကို လက်လေးချောင်းထောင်နေပုံကို အသုံးပြုပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "Choose a name that includes one of these letters: P, Ph, B, Bh, or M. Use a five-pointed star as your logo.",
                    mm = "သင့်လုပ်ငန်းကို (ပ၊ ဖ၊ ဗ၊ ဘ၊ မ) စာလုံးတစ်လုံးပါအောင်အမည်ပေးပါ။ လိုဂိုတံဆိပ်ကို ငါးထောင့်ကြယ်ပါသော ကြယ်ပွင့်၏ပုံကို အသုံးပြုပါ။"
                ),
                "6" to FortuneAnswer(
                    en = "Choose a name that includes one of these letters: Th or H. Use a picture of a ruddy shelduck as your logo.",
                    mm = "သင်၏လုပ်ငန်းကို (သ၊ ဟ) စာလုံးတစ်လုံးပါအောင်အမည်ပေးပါ။ လိုဂိုတံဆိပ်ကို ဟင်္သာပုံကိုအသုံးပြုပါ။"
                ),
                "7" to FortuneAnswer(
                    en = "Choose a name that includes one of these letters: T, Ht, D, Dh, or N. Use a picture of an ox as your logo.",
                    mm = "သင်၏လုပ်ငန်းကို (တ၊ ထ၊ ဓ၊ ဒ၊ န) စာလုံးတစ်လုံးပါအောင်အမည်ပေးပါ။ လိုဂိုတံဆိပ်ကို နွားလားဥဿဘပုံကို အသုံးပြုပါ။"
                ),
                "8" to FortuneAnswer(
                    en = "Choose a name that includes one of these letters: Y, R, or L. Use two overlapping squares or an eight-pointed star as your logo.",
                    mm = "သင်၏လုပ်ငန်းကို (ယ၊ ရ၊ ဠ) စာလုံးတစ်လုံးပါအောင် အမည်မှည့်ပေးပါ။ လိုဂိုတံဆိပ်ကို စတုရန်း 2 ခုထပ်ထားပုံ (သို့မဟုတ်) ရှစ်ထောင့်ကြယ်ပုံသဏ္ဍာန်ကို အသုံးပြုပါ"
                ),
                "9" to FortuneAnswer(
                    en = "Choose any name you like – just make sure the total value of its letters adds up to nine. Use the number 9 as your logo.",
                    mm = "သင်၏လုပ်ငန်းကို ကြိုက်ရာအမည်ပေးပါ။ အမည်စာလုံးများကို ပေါင်းလိုက်လျှင် ကိုးဂဏန်းဖြစ်ပါစေ။ ဥပမာ သစ္စာ၊ ပင်းယ၊ ရိုးမ၊ လမင်း စသည့်တို့ကဲ့သို့ဖြစ်ရပါမည်။ လိုဂိုတံဆိပ်ကို (၉)ဂဏန်းအသုံးပြုပါ ၊၊ (၉)မှာ အင်္ဂလိပ် (ကိုး) မြန်မာ (ကိုး) ရပါသည်။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q24",
            en = "Will I get the financial help I am going to ask for?",
            mm = "ထိုသူ့ထံသို့ သွား၍ ငွေရေးကြေးရေးအကူညီတောင်းမည်။ ရနိုင်၏လော /မရနိုင်၏လောနှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Just go – you will get it.",
                    mm = "သွားသာသွားပါ ရမှာပါ"
                ),
                "1" to FortuneAnswer(
                    en = "Don't go – you won't get it.",
                    mm = "မသွားပါနဲ့ မရနိုင်ပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "You will get it – make sure you go.",
                    mm = "ရမှာပါ။ သွားဖြစ်အောင်သွားလိုက်ပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "You will definitely get it – that person is also in a good position right now.",
                    mm = "သေချာပေါက်ရမှာပါ။ သူ့အနေနဲ့လည်း အတော်ကိုပြေလည်နေတဲ့ အချိန်ပါ။"
                ),
                "4" to FortuneAnswer(
                    en = "Don't go – you won't get it. That person is also in trouble.",
                    mm = "မသွားပါနဲ့ ရမှာမဟုတ်ပါဘူး။ သူလည်း ဒုက္ခရောက်နေတယ်။"
                ),
                "5" to FortuneAnswer(
                    en = "Don't go – you won't get it. They have problems at home too.",
                    mm = "မသွားပါနဲ့ မရဘူး ။ သူတို့အိမ်တွင်းရေးလည်း မပြေလည်ဘူး။"
                ),
                "6" to FortuneAnswer(
                    en = "Make sure you go – they will be willing to give it to you.",
                    mm = "သွားဖြစ်အောင် သွားလိုက်စမ်းပါ ။ လိုလိုချင်ချင်ကို ပေးမှာပါ"
                ),
                "7" to FortuneAnswer(
                    en = "Don't expect to get it – it will only be empty talk.",
                    mm = "ရလိမ့်မယ်လို့မထင်နဲ့။ အပြောသာ အဖတ်တင်လိမ့်မယ်။"
                ),
                "8" to FortuneAnswer(
                    en = "You won't get it – don't bother going.",
                    mm = "မရပါဘူးသွားမနေပါနဲ့"
                ),
                "9" to FortuneAnswer(
                    en = "Don't go – you won't get it.",
                    mm = "မသွားပါနဲ့မရပါဘူး"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q25",
            en = "Will I receive the inheritance/legacy?",
            mm = "မိမိသည်အမွေရနိုင်ခြင်း မျဉ်းကြောင်းပေါ်တွင်ရှိ၏။ အမွေရနိုင်၏လော /မရနိုင်၏လောနှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "It is more likely that you won't get it, and this inheritance matter will drag on without a clear resolution.",
                    mm = "မရဘို့ကများတယ်။ ပြီးတော့အဲဒီအမွေကိစ္စဟာမပြီးမပြတ်ဘဲ တိုးလို့တန်းလန်းကြီးဖြစ်နေမှာပါ။"
                ),
                "1" to FortuneAnswer(
                    en = "You will surely get it.",
                    mm = "ကျိန်းသေ ရမှာပါ"
                ),
                "2" to FortuneAnswer(
                    en = "Someone in between may interfere and cause trouble, so the inheritance could be lost.",
                    mm = "ကြားကလူတစ်ယောက်ဝင်ပြီးချောက်တွန်းလို့ အမွေရဖို့ကိစ္စ ပျက်သွားတတ်တယ်။"
                ),
                "3" to FortuneAnswer(
                    en = "Don't keep your hopes up for that inheritance.",
                    mm = "အဲဒီ ရမယ့်အမွေလောက်ကိုမျှော်လင့်မနေပါနဲ့ ။"
                ),
                "4" to FortuneAnswer(
                    en = "You will surely get it – they cannot refuse to give it, and the people around won't allow that either.",
                    mm = "ကျိန်းသေရမှာပါ။ မပေးလို့ကိုမရပါဘူး။ ပတ်ဝန်းကျင်ကလည်း ခံမှာမဟုတ်ဘူး။"
                ),
                "5" to FortuneAnswer(
                    en = "Why are you even asking? You already know you will get it.",
                    mm = "ဘာဖြစ်လို့မေးနေတာလဲ ။ ရမယ်ဆိုတာသိလျက်သားနဲ့။"
                ),
                "6" to FortuneAnswer(
                    en = "You will not get it.",
                    mm = "ရမှာမဟုတ်ပါဘူး"
                ),
                "7" to FortuneAnswer(
                    en = "You will get it, but you may need to have some discussions.",
                    mm = "ရတော့ရမှာပါ။ နဲနဲလေးတော့ စကားပြောရမယ်"
                ),
                "8" to FortuneAnswer(
                    en = "You will surely get it.",
                    mm = "ကျိန်းသေရမှာပါ"
                ),
                "9" to FortuneAnswer(
                    en = "You will not get it.",
                    mm = "မရပါဘူး"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q26",
            en = "Is it good to enter into a business partnership with this person?",
            mm = "ဤသူနှင့် စပ်တူ အလုပ်လုပ်လိုပါသည်၊၊ အကျိုးရှိ /မရှိနှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "It's best not to do it at all.",
                    mm = "လုံးဝမလုပ်ရင်ကောင်းဆုံးပဲ။"
                ),
                "1" to FortuneAnswer(
                    en = "Just go ahead – it will definitely be beneficial.",
                    mm = "လုပ်လိုက်စမ်းပါ။ မုချအကျိုးရှိမယ့်ကိစ္စပါပဲ။"
                ),
                "2" to FortuneAnswer(
                    en = "If your partner is young, go ahead. If they are older, don't.",
                    mm = "စပ်တူလုပ်မည့်သူဟာလူငယ်ဆိုရင် လုပ်လိုက်ပါ။ လူကြီးဆိုလျှင်မလုပ်ပါနဲ့။"
                ),
                "3" to FortuneAnswer(
                    en = "Make sure you do it – it will be very beneficial.",
                    mm = "လုပ်ဖြစ်အောင်လုပ်လိုက်စမ်းပါ။ အရမ်းအကျိုးရှိမှာပါ။"
                ),
                "4" to FortuneAnswer(
                    en = "Don't do it – it will not be beneficial.",
                    mm = "မလုပ်ပါနဲ.။ အကျိုးရှိဖို့မတွေ.ဘူး"
                ),
                "5" to FortuneAnswer(
                    en = "Go ahead with this person – it will be beneficial. Just bear with them as they can be a bit talkative.",
                    mm = "ဒီလူနဲ့လုပ်ပါ။ အကျိုးရှိမှာပါပဲ။ နည်းနည်းတော့ပစိပစပ်များတယ်။ အဲဒါလေး သည်းခံရုံပါပဲ"
                ),
                "6" to FortuneAnswer(
                    en = "Make sure you do it. It may not go well at first, but later it will become very beneficial.",
                    mm = "လုပ်ဖြစ်အောင်လုပ်ပါ။ အစပိုင်းမှာတော့ သိပ်ဆင်ပြေမှာ မဟုတ်ဘူး။ နောက်ပိုင်းမှာ အရမ်းအကျိုးရှိမယ်နော်။"
                ),
                "7" to FortuneAnswer(
                    en = "Don't decide yet – wait about a month before making your choice.",
                    mm = "လုပ်မယ်လို့လည်း မဆုံးဖြတ်ပါနဲ့အုံး။ မလုပ်ဘူးလို့လည်း မဆုံးဖြတ်ပါနဲ့အုံး။ ခဏလေး တလလောက်အချိန်ဆွဲပြီးမှ ဆုံးဖြတ်လိုက်ပါ။"
                ),
                "8" to FortuneAnswer(
                    en = "You should work with that person.",
                    mm = "အဲဒီလူနဲ့ အလုပ်လုပ်သင့်ပါတယ်။"
                ),
                "9" to FortuneAnswer(
                    en = "Decide to do it, but make sure you find out everything you can about that person first.",
                    mm = "လုပ်ဖို့တော့ဆုံးဖြတ်လိုက်ပါ။ ဒါပေမဲ့ ဒီလူအကြောင်းပြည့်စုံအောင် စုံစမ်းလိုက်ပါ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q27",
            en = "Am I suitable for the business I want to start?",
            mm = "မိမိသည် လုပ်ငန်းတစ်ခု လုပ်လိုပါသည်၊၊ ဤအလုပ်နှင့် သင့်တော်၏ /မသင့်တော်၏နှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Do it – you will become wealthy in no time.",
                    mm = "လုပ်လိုက်ပါ။ နေချင်းညချင်း ကြီးပွားချမ်းသာ သွားပေလိမ့်မယ်။"
                ),
                "1" to FortuneAnswer(
                    en = "Don't do it – it's not suitable. It will only bring you frustration.",
                    mm = "မလုပ်ပါနဲ့။ မသင့်တော်ဘူး။ လုပ်ရင်စိတ်ညစ်ရလိမ့်မယ်။"
                ),
                "2" to FortuneAnswer(
                    en = "Make sure you do it – it's a very good business.",
                    mm = "လုပ်ဖြစ်အောင်လုပ်လိုက်စမ်းပါ။ အတော့ကို ကောင်းတဲ့အလုပ်ဖြစ်တယ်။"
                ),
                "3" to FortuneAnswer(
                    en = "You already know this is the right work for you – why are you even asking?",
                    mm = "လုပ်ရမဲ့အလုပ်ဆိုတာ သင်လည်းသိပြီးသားပါ။ ဘာဖြစ်လို့မေးနေတာလဲ။"
                ),
                "4" to FortuneAnswer(
                    en = "I strongly encourage you to do it.",
                    mm = "လုပ်ဖြစ်အောင် လုပ်စေချင်ပါတယ်။"
                ),
                "5" to FortuneAnswer(
                    en = "It's best not to do it – if you go ahead, you may end up regretting it.",
                    mm = "မလုပ်မိရင် အကောင်းဆုံးပဲ။ သွားပြီးလုပ်ရင်တော့ ရူးသွားနိုင်ပါတယ်။"
                ),
                "6" to FortuneAnswer(
                    en = "Just do it – it will bring great benefits.",
                    mm = "လုပ်စမ်းပါဗျာ။ အကျိုးပေး ထူးသွားမှာပါ။"
                ),
                "7" to FortuneAnswer(
                    en = "Many helpers will come forward – make sure you do it.",
                    mm = "ကူညီမယ့်သူတွေလည်း ပေါ်ပေါက်လာမှာပါ။ လုပ်ဖြစ်အောင် လုပ်လိုက်စမ်းပါ။"
                ),
                "8" to FortuneAnswer(
                    en = "You can do it, but be careful about who you choose as your partner.",
                    mm = "လုပ်တော့လုပ်ပါဗျာ။ ဒါပေမယ့် တွဲမယ့်အတွဲကိုလည်း ဂရုစိုက်ပါ။"
                ),
                "9" to FortuneAnswer(
                    en = "It's best not to do it – don't go ahead.",
                    mm = "မလုပ်လျှင်အကောင်းဆုံးပဲ။ မလုပ်ပါနှင့်။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q28",
            en = "Should I buy this item/thing?",
            mm = "မိမိသည် ဤပစ္စည်းကိုဝယ်ယူလို၏။ ဝယ်သင့်၏ /မသင့်၏နှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "You should buy it – just close your eyes and go ahead with the purchase.",
                    mm = "ဝယ်သင့်တာပေါ့ဗျာ။ မျက်စေ့ မှိတ်ပြီး ဝယ်ချလိုက်စမ်းပါ"
                ),
                "1" to FortuneAnswer(
                    en = "Don't buy it – it won't be of any benefit to you once you have it.",
                    mm = "မဝယ်ပါနဲ့ဗျာ။ ကိုယ့်လက်ထဲရောက်တော့လည်း အကျိုးပေးမှာမဟုတ်ဘူး။"
                ),
                "2" to FortuneAnswer(
                    en = "You should buy it – go ahead and get it.",
                    mm = "ဝယ်သင့်ပါတယ်ဗျာ။ ဝယ်လိုက်စမ်းပါ"
                ),
                "3" to FortuneAnswer(
                    en = "You should definitely buy it – you already know it's well worth the price.",
                    mm = "အရမ်းကိုဝယ်သင့်ပါတယ်။ တန်လည်းအဝောာ်.ကိုတန်ပါတယ်ဆိုတာ သင်လည်းသိသားပဲ။"
                ),
                "4" to FortuneAnswer(
                    en = "Don't buy it – it will bring you trouble.",
                    mm = "မဝယ်ပါနဲ့ ။ ဒုက္ခရောက်လိမ့်မယ်"
                ),
                "5" to FortuneAnswer(
                    en = "Go ahead and buy it – I urge you ten times over!",
                    mm = "ဝယ်စမ်းပါဗျာ။ အဲဒါကို ဝယ်ဖို့ (၁၀)ကြိမ် တိုက်တွန်းတယ်ဗျာ"
                ),
                "6" to FortuneAnswer(
                    en = "Don't go for it – it will bring you trouble.",
                    mm = "သွားမလုပ်နဲ့။ ဒုက္ခတွေရောက်လာလိမ့်မယ်။"
                ),
                "7" to FortuneAnswer(
                    en = "It's about time you bought it – you should have done so long ago.",
                    mm = "ဝယ်သင့်တာ ကြာပြီဗျ"
                ),
                "8" to FortuneAnswer(
                    en = "You must buy it – you would be foolish not to.",
                    mm = "ဝယ်ရမယ်။ အဲဒါကိုမဝယ်မိရင် မိုက်ရာကျသွားမယ်။"
                ),
                "9" to FortuneAnswer(
                    en = "Buy it, but there may be some family arguments over it – those will pass too.",
                    mm = "ဝယ်တော့ဝယ်။ အိမ်တွင်းရေးစကားများရမယ်။ ပြီးတော့လည်းပြီးသွားမှာပါပဲ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q29",
            en = "Should I sell this item/thing?",
            mm = "ဤပစ္စည်းကိုရောင်းလိုပါသည်။ ရောင်းသင့် /မရောင်းသင့်နဲ့ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Don't sell it – you will regret it afterward.",
                    mm = "သွားမရောင်းပါနဲ့။ ရောင်းပြီးမှ မှားမှန်းသိလိမ့်မယ်။"
                ),
                "1" to FortuneAnswer(
                    en = "You should sell it – it's best to get rid of it.",
                    mm = "ရောင်းသင့်တာပေါ့ဗျာ။ ရောင်းပစ်လိုက်ရင် အကောင်းဆုံးပဲ။"
                ),
                "2" to FortuneAnswer(
                    en = "Don't sell it – it won't be easy to get another one like this again.",
                    mm = "မရောင်းပါနဲ့။ ဒါမျိုး နောက်ထပ်ရဖို့ မလွယ်တော့ဘူး။"
                ),
                "3" to FortuneAnswer(
                    en = "Selling it is good – don't delay.",
                    mm = "ရောင်းရင်ကောင်းပါတယ်။ အချိန်စွဲမနေပါနဲ့။"
                ),
                "4" to FortuneAnswer(
                    en = "Please don't sell it.",
                    mm = "မရောင်းပါနဲ့ဗျာ"
                ),
                "5" to FortuneAnswer(
                    en = "Yes, you should sell it.",
                    mm = "ရောင်းရမှာပေါ့"
                ),
                "6" to FortuneAnswer(
                    en = "Keep it without selling – its price will go up soon.",
                    mm = "မရောင်းပဲထားလိုက်စမ်းပါ။ မကြာခင် ဒီပစွည်းက ဈေးတက်လာလိမ့်မယ်။"
                ),
                "7" to FortuneAnswer(
                    en = "Take some time to think about it – whatever you decide, wait at least 12 days before making a final choice.",
                    mm = "အချိန်နည်းနည်းဆွဲပြီးစဉ်းစားပါ။ ဘာပဲဖြစ်ဖြစ် 12 ရက်ကျော်မှဆုံးဖြတ်ပါ။"
                ),
                "8" to FortuneAnswer(
                    en = "Just sell it – don't worry about checking the price. Selling it off is the best option.",
                    mm = "ရောင်းလိုက်တော့။ ဈေးတွေဘာတွေကြည့်မနေနဲ့။ ရောင်းပစ်လိုက်တာကောင်းဆုံးပဲ။"
                ),
                "9" to FortuneAnswer(
                    en = "Name a firm price and sell it.",
                    mm = "မာမာတင်းတင်း စျေးခေါ်ပြီး ရောင်းလိုက်စမ်းပါဗျာ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q30",
            en = "Will my intended major donation be successful?",
            mm = "မိမိသည် အလှူကြီးပြုရန် ရည်ရွယ်ထား၏။ ပြုဖြစ် /မပြုဖြစ်နဲ့ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "The time is not yet right for such a donation.",
                    mm = "အလှူကြီးပြုဖို့ အချိန်လိုသေးတယ်။"
                ),
                "1" to FortuneAnswer(
                    en = "It is still a bit early for such a donation – wait a little longer.",
                    mm = "အလှုပြုဖို့က အချိန်နည်းနည်းလိုသေးတယ်။ စောင့်လိုက်ပါဦး"
                ),
                "2" to FortuneAnswer(
                    en = "The time is not quite right yet – you will need to wait.",
                    mm = "အလှူကြီးပြုဖို့ အခါနည်းနည်းလိုနေတယ်။ အချိန်ယူရဦးမယ်။"
                ),
                "3" to FortuneAnswer(
                    en = "You will definitely make this large donation.",
                    mm = "ကျိန်းသေ အလှူကြီးပြုရမှာပါ"
                ),
                "4" to FortuneAnswer(
                    en = "A donation? Don't even ask – it is certain that you will do it.",
                    mm = "အလှုပြုဖို့ကိစ္စလား။ မေးမနေပါနဲ့။ ပြုရမှာသေချာပါတယ်။"
                ),
                "5" to FortuneAnswer(
                    en = "You are certainly destined to make this large donation.",
                    mm = "မုချ အလှူကြီးပြုရမဲ့ကိန်းရှိပါတယ်။"
                ),
                "6" to FortuneAnswer(
                    en = "You already have the luck for this donation – just put in a little more effort, and it will happen.",
                    mm = "အလှူကြီးပြုဖို့ကံကရှိနေပြီသားပါ။ နဲနဲလေးကြိုးစားလိုက်ပါ။ ဖြစ်ပါပြီ။"
                ),
                "7" to FortuneAnswer(
                    en = "You will surely make this large donation.",
                    mm = "မုချ အလှူကြီးပြုရမှာပါ"
                ),
                "8" to FortuneAnswer(
                    en = "You have the good fortune and virtue to make a great donation.",
                    mm = "အလှူကြီးပြုဖို့ ပါရမီကံပါပါတယ်။"
                ),
                "9" to FortuneAnswer(
                    en = "You will surely make this large donation – don't worry about it.",
                    mm = "မုချအလှူကြီးပြုရမှာပါ။ အဲဒီအတွက်မပူပါနဲ့။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q31",
            en = "Will I get the chance to be a missionary/on a spiritual journey?",
            mm = "မိမိသည် ဤဘဝတွင် သာသနာပြုလို၏။ ထိုသို့ပြုလုပ်ရန် ကံပါ /မပါနှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "You have the virtue and destiny to do missionary work.",
                    mm = "သာသနာပြုဖို့ပါရမီပါပါတယ်"
                ),
                "1" to FortuneAnswer(
                    en = "Yes, you have that destiny – don't worry.",
                    mm = "သင်က သာသနာပြုဖို့ကံ ပါ,ပါတယ်။"
                ),
                "2" to FortuneAnswer(
                    en = "Whenever you plan to do missionary work, obstacles tend to appear. Just focus on your own spiritual growth instead.",
                    mm = "သာသနာပြုမယ်လို့ကြံစည်တိုင်း အနှောက်အယှက်တွေ ပေါ်လာတတ်တယ်။ ကိုယ့်အတွက်ကို လုပ်ပေတော့။"
                ),
                "3" to FortuneAnswer(
                    en = "You are surely meant to do missionary work.",
                    mm = "မုချ သာသနာပြုရမည့်သူပါ"
                ),
                "4" to FortuneAnswer(
                    en = "Isn't it more important to know yourself first before doing missionary work? Once you understand that, you will know what to do.",
                    mm = "သာသနာပြုဖို့အတွက် သင်ကိုယ်တိုင်တရားရဖို့က အရေးကြီးတယ်မဟုတ်လား။ အဲဒါကိုသဘောပေါက်ရင် သင်ဘာလုပ်ရမယ်ဆိုတာ သိတယ်မဟုတ်လား။"
                ),
                "5" to FortuneAnswer(
                    en = "You will surely do missionary work.",
                    mm = "မုချသာသနာပြုရမှာပါ။"
                ),
                "6" to FortuneAnswer(
                    en = "You have the virtue and destiny to do missionary work.",
                    mm = "သာသနာပြုဖို့ ပါရမီပါ,ပါတယ်။"
                ),
                "7" to FortuneAnswer(
                    en = "You will take on this path very soon.",
                    mm = "အမြန်ဆုံး သာသနာပြုရပါလိမ့်မယ်။"
                ),
                "8" to FortuneAnswer(
                    en = "You will do it – and you will also become wealthy enough to support it.",
                    mm = "သာသနာပြုရမှာပါ။ ပြုနိုင်အောင်လည်း ချမ်းသာလိမ့်ဦးမည်။"
                ),
                "9" to FortuneAnswer(
                    en = "Forget about missionary work for now – focus on knowing yourself and doing things for your own growth first.",
                    mm = "သာသနာတွေ ဘာတွေပြုမနေပါနဲ့။ ကိုယ့်အတွက်ကိုရအောင် လုပ်စမ်းပါ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q32",
            en = "What should I do to get rid of bad luck and fulfill my desires?",
            mm = "မိမိ၏ကံဇာတာအညံ့များ ပြေပျောက်ရန်အတွက်လည်းကောင်း  လိုအင်ဆန္ဒများပြည့်ရန်အတွက်လည်းကောင်း ယတြာချေခြင်းနှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Offer ten yellow flowers at the Sunday corner of a pagoda.",
                    mm = "ပန်းဝါ (၁၀) ပွင့်၊ တနင်္ဂနွေထောင့်မှာ လှူလိုက်ပါ။"
                ),
                "1" to FortuneAnswer(
                    en = "Cook coconut rice with duck curry and donate it to a monastery.",
                    mm = "အုန်းထမင်းနဲ့ ဘဲသားဟင်းချက်ပြီး ဘုန်းကြီးကျောင်းကိုလှူလိုက်ပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "Go to a monastery and offer coffee and cakes as a donation.",
                    mm = "ကော်ဖီနဲ့ကိတ်မုန့်ကို ဘုန်းကြီးကျောင်းသို့သွားပြီး လှူလိုက်ပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "Prepare oiled rice and donate it at a nunnery resthouse.",
                    mm = "ဆီထမင်းထိုးပြီး မယ်သီလရှင်ဇရပ်မှာ သွားပြီးလှူလိုက်ပါ။"
                ),
                "4" to FortuneAnswer(
                    en = "Buy some semolina pudding with poppy seeds and offer it to a monk.",
                    mm = "ရွှေကြည်မုန့်ကိုဝယ်ပြီး ဘုန်းကြီးကိုကပ်လိုက်ပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "Mix boiled beans with oil and salt, and donate them early in the morning at a monastery.",
                    mm = "ပဲပြုတ်ကိုဆီဆမ်း၊ ဆားဖြူးပြီး မနက်စောစော ဘုန်းကြီးကျောင်းကိုသွားပြီး လှူလိုက်ပါ။"
                ),
                "6" to FortuneAnswer(
                    en = "Cook your favorite dish and donate it at a monastery.",
                    mm = "သင်နှစ်သက်တဲ့ ဟင်းတခွက်ကောင်းကောင်းချက်ပြီး ဘုန်းကြီးကျောင်းကိုသွားပြီး လှူလိုက်ပါ"
                ),
                "7" to FortuneAnswer(
                    en = "Boil about a viss of milk and donate it at a nunnery resthouse.",
                    mm = "နွားနို့တစ်ပိဿာလောက်ကျိုပြီး မယ်သီလရှင်ဇရပ်မှာသွားပြီး လှူလိုက်ပါ။"
                ),
                "8" to FortuneAnswer(
                    en = "Offer nine kinds of sweet foods at your home shrine.",
                    mm = "ယို (9) မျိုးနဲ့ သင်၏အိမ်ဘုရားမှာပဲ ဆွမ်းတော်ကပ်လိုက်စမ်းပါ။"
                ),
                "9" to FortuneAnswer(
                    en = "Light ten candles and offer them at a place connected to your birth planet.",
                    mm = "ဖယောင်းတိုင် (၁၀) တိုင် ကိုယ်မွေးတဲ့ဂြိုလ်တိုင်မှာသွားပြီး ထွန်းညှိပူဇော်လိုက်ပါ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q33",
            en = "Am I in any danger or facing harmful incidents at this age?",
            mm = "ယခုအသက်အပိုင်းတွင် ထိမိခိုက်မိအန္တရာယ်ရှိ /မရှိနှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "No danger of accidents – and no other harm either.",
                    mm = "ထိမိခိုက်မိအန္တရာယ်လည်းမရှိဘူး။ တခြားအန္တရာယ်လည်းမတွေ့ပါဘူး။"
                ),
                "1" to FortuneAnswer(
                    en = "There is no danger of such accidents.",
                    mm = "ထိမိခိုက်မိအန္တရာယ်မရှိနိုင်ပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "None at all.",
                    mm = "လုံးဝမရှိနိုင်ပါ"
                ),
                "3" to FortuneAnswer(
                    en = "Not only is there no such danger – there is also a chance of wealth coming your way.",
                    mm = "ထိမိခိုက်မိ အန္တရာယ်မရှိရုံမဟုတ်ဘူး။ ချမ်းသာဖို့ကိန်း ရှိနေပါတယ်။"
                ),
                "4" to FortuneAnswer(
                    en = "Be a little careful – a minor accident could happen.",
                    mm = "နည်းနည်းတော့သတိထားပါ။ နဲနဲပါးပါးတော့ ထိမိခိုက်မိတတ်ပါတယ်။"
                ),
                "5" to FortuneAnswer(
                    en = "No – absolutely no such danger.",
                    mm = "လုံးဝ ထိမိခိုက်မိအန္တရာယ်မရှိပါဘူး။"
                ),
                "6" to FortuneAnswer(
                    en = "A very small incident might occur – but nothing to worry about.",
                    mm = "နည်းနည်းပါးပါးဖြစ်တတ်ပါတယ်။ စိုးရိမ်စရာ မဟုတ်ပါဘူး။"
                ),
                "7" to FortuneAnswer(
                    en = "I do see a danger of accidents.",
                    mm = "ထိမိခိုက်မိ အန္တရာယ်မြင်ပါတယ်။"
                ),
                "8" to FortuneAnswer(
                    en = "No danger at all – just stay peaceful and calm.",
                    mm = "အန္တရာယ်လုံးဝမတွေ့ပါဘူးဗျာ။ စိတ်ချမ်းသာ လက်ချမ်းသာ နေစမ်းပါ။"
                ),
                "9" to FortuneAnswer(
                    en = "No, none.",
                    mm = "မရှိပါဘူးဗျာ"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q34",
            en = "What is the situation for the person hospitalized with a serious injury?",
            mm = "ထိမိခိုက်မိအန္တရာယ်ကြောင့် ဆေးရုံသို့ရောက်နေသည်။ ထိုသူ၏အခြေအနေကောင်းခြင်း /မကောင်းခြင်းနှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Follow the remedy in this book – the condition is still a little bad.",
                    mm = "ယတြာချေထားပါ။ ဒီစာအုပ်မှာ ယတြာပါပါတယ်။နဲနဲညံ့နေသေးတယ်။"
                ),
                "1" to FortuneAnswer(
                    en = "A bit bad – close and proper treatment is needed.",
                    mm = "နည်းနည်းဆိုးတယ်။ ကြပ်ကြပ်မတ်မတ်ကုသဖို့လိုတယ်။"
                ),
                "2" to FortuneAnswer(
                    en = "Not very bad, but the condition is a little poor.",
                    mm = "အဆိုးကြီးတော.မဟုတ်ပါဘူး။ နည်းနည်းပါးပါးတော့ အခြေနေက ညံ့နေတယ်။"
                ),
                "3" to FortuneAnswer(
                    en = "Nothing to worry about at all.",
                    mm = "စိုးရိမ်စရာလုံးဝမရှိပါ"
                ),
                "4" to FortuneAnswer(
                    en = "It is worrisome – find the remedy section in this book and follow it now.",
                    mm = "စိုးရိမ်စရာရှိပါတယ်။ ဒီစာအုပ်မှာပါတဲ့ ယတြာကိုရှာပြီး ယတြာချေထားလိုက်ပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "The patient has turned in our favor – no need to worry now.",
                    mm = "လူနာဟာကိုယ့်ဘက် ပါလာပြီ။ ဘာမှမပူပါနဲ့။"
                ),
                "6" to FortuneAnswer(
                    en = "Stay calm – no need to worry. No life danger and nothing else to fear.",
                    mm = "အေးအေးနေလိုက်စမ်းပါ။ ဘာမှမပူပါနဲ့။ အသက်အန္တရာယ်လည်းစိုးရိမ်စရာမရှိပါဘူး။တစ်ခြားစိုးရိမ်စရာလည်း မရှိပါဘူးဗျာ။"
                ),
                "7" to FortuneAnswer(
                    en = "Find the remedy section in this book and follow it – we are going through a concerning condition.",
                    mm = "ဒီစာအုပ်ထဲမှာပါတဲ့ ယတြာကိုရှာပြီး ချေထားလိုက်ပါ။ စိုးရိမ်စရာအခြေအနေတစ်ခုကို ဖြတ်သန်းနေပါတယ်။"
                ),
                "8" to FortuneAnswer(
                    en = "No worries at all – no danger to life and nothing else to fear.",
                    mm = "လုံးဝမစိုးရိမ်ပါနဲ့။ အသက်ဘေးလည်းမစိုးရိမ်ရပါဖူး။ အခြားဘာမှလဲ စိုးရိမ်စရာ မရှိပါဘူး။"
                ),
                "9" to FortuneAnswer(
                    en = "The patient has turned in our favor – no need to worry.",
                    mm = "လူနာကကိုယ့်ဘက်ပါလာပြီ။ ဘာမှမပူပါနဲ့။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q35",
            en = "What about the lawsuit / legal case against me?",
            mm = "အမှုဖြစ်ခြင်းနှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "You will win – you have a very smart lawyer.",
                    mm = "သင်ပဲနိုင်မှာပါ။ သင်ငှားထားတဲ့ရှေ့နေက သိပ်ပြီးတော်တာပဲ။"
                ),
                "1" to FortuneAnswer(
                    en = "The chances of winning or losing are uncertain – our side is currently weaker.",
                    mm = "နိုင်ဖို့ရှုံးဖို့က ကိုယ့်ဘက်က အားနည်းနေတယ်နော်။"
                ),
                "2" to FortuneAnswer(
                    en = "You will surely win – do a good deed by releasing birds from a cage.",
                    mm = "ကျိန်းသေနိုင်မှာပါ။ ငှက်လွှတ်တဲ့ကုသိုလ် လုပ်လိုက်ပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "Why are you even asking? You already know you will win.",
                    mm = "ဘာဖြစ်လို့မေးနေတာလဲ။ နိုင်မယ်ဆိုတာ သိနေမှပဲ။"
                ),
                "4" to FortuneAnswer(
                    en = "You will surely win – you will hear good news after six days.",
                    mm = "နိုင်မှာသေချာပါတယ်။ ခြောက်ရက်ကြာတဲ့အခါ သတင်းထူးကြားရမှာပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "The other side is very nasty – try to overcome that.",
                    mm = "တဖက်ကတအားညစ်နေတယ်။ အဲ့တာလေးကို ကျော်လွှားဖို့လုပ်လိုက်ပါ။"
                ),
                "6" to FortuneAnswer(
                    en = "Our side is slightly weaker.",
                    mm = "ကိုယ့်ဘက်က နည်းနည်းအားနည်းနေတယ်။"
                ),
                "7" to FortuneAnswer(
                    en = "You are sure to win – heaven itself is on your side.",
                    mm = "ကိုယ်နိုင်မှာသေချာပါတယ်။ ကောင်းကင်ဘုံက ကိုယ့်ဘက်ကပါနေပြီပဲ။"
                ),
                "8" to FortuneAnswer(
                    en = "No need to overthink this case – you will be the winner.",
                    mm = "ဒီအမှုဟာ စဉ်းစားစရာမလိုပါဘူး။ သင်ပဲနိုင်မှာပါ။"
                ),
                "9" to FortuneAnswer(
                    en = "You are sure to win this case – take a short trip as a symbolic gesture.",
                    mm = "ဒီအမှုက သင်နိုင်မှာသေချာပါတယ်။ ခရီးတိုလေးတခု ယတြာသဘောနဲ့ ထွက်လိုက်ပါ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q36",
            en = "Is there a way out of our current difficult problem?",
            mm = "ဖြေရခက်နေသော ပြဿနာနှင့်ကြုံတွေ့နေရသည်။ ထွက်ပေါက်ရနိုင် /မရနိုင်နှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Two elderly people or an old monk will show you the way out – don't worry.",
                    mm = "အသက်ကြီးကြီးလူနှစ်ယောက်၊ ဒါမှမဟုတ် ဘုန်းကြီးတစ်ပါးဆီက ထွက်ပေါက်တွေ့သွားမှာပါ။ မပူပါနဲ့ဗျာ။"
                ),
                "1" to FortuneAnswer(
                    en = "A way out will not come easily or soon.",
                    mm = "ထွက်ပေါက်က တော်တော်နဲ့ရဦးမှာမဟုတ်ဘူး။"
                ),
                "2" to FortuneAnswer(
                    en = "Don't worry – heaven will provide you with a way out.",
                    mm = "ဘာမှမပူပါနဲ့ဗျာ။ သင့်ကိုကောင်းကင်ဘုံက ထွက်ပေါက်ပေးမှာပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "You will surely find a way out. You will hear news of a nearby woman giving birth – go and give a gift to that child.",
                    mm = "ကျိန်းသေထွက်ပေါက်ရပါမယ်။ သင့်အိမ်နားမှာ မိန်းကလေးတယောက် ကလေးမျက်နှာမြင်တာ ကြားရလိမ့်မယ်။ အဲဒီကလေးကို လက်ဆောင်တခု သွားပေးလိုက်ပါ။"
                ),
                "4" to FortuneAnswer(
                    en = "The angels are on your side – a way out will come. Don't worry.",
                    mm = "နတ် သိကြားတွေ မလို့ ထွက်ပေါက်ရပြီဗျာ။ မပူပါနဲ့။"
                ),
                "5" to FortuneAnswer(
                    en = "Many people love you – a kind person will help you find a way out.",
                    mm = "ခင်ဗျားကို လူချစ်လူခင်များပါတယ်။ မေတ္တာရှိတဲ့လူတစ်ဦးဦးက ခင်ဗျားကို ထွက်ပေါက်ရှာပေးလိမ့်မယ်။"
                ),
                "6" to FortuneAnswer(
                    en = "A way out will not come anytime soon.",
                    mm = "တော်တော်နဲ့ ထွက်ပေါက်တွေ့ဦးမှာမဟုတ်ဘူး။"
                ),
                "7" to FortuneAnswer(
                    en = "A chick comes out of an egg without any opening – you have many ways out!",
                    mm = "အပေါက်မရှိတဲ့ ဥထဲကနေ ကြက်ကလေးထွက်လာတာပဲဗျာ။ ခင်ဗျားက ထွက်ပေါက်တွေ အများကြီးပဲ။"
                ),
                "8" to FortuneAnswer(
                    en = "What are you worried about? A way out will come from an unexpected source – surely you will find one.",
                    mm = "ဘာတွေများပူနေတာလဲ။ မမျှော်လင့်တဲ့ဆီကနေ ထွက်ပေါက်တွေ့လိမ့်မယ်။ ကျိန်းသေပါတယ်ဗျာ။ ထွက်ပေါက်တွေ့လိမ့်မယ်။"
                ),
                "9" to FortuneAnswer(
                    en = "What are you worried about? There is a way out – everything will be resolved soon.",
                    mm = "ဘာတွေများပူနေတာလည်း။ ထွက်ပေါက်ရှိပါတယ်။ မကြာခင် ပြေလည်သွားမှာပါ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q37",
            en = "Will I win or lose against my opponent/enemy in this competition?",
            mm = "ရန်သူနှင့်ယှဉ်ပြိုင်နေရသည်။ မိမိက အနိုင်ရခြင်း /ရှုံးနိမ့်ခြင်းနဲ့ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "What are you asking for? You already know you will surely win.",
                    mm = "သင်ဘာတွေ မေးနေတာလဲ။ နိုင်မယ်သေချာတယ်ဆိုတာ သင်လည်း သိပြီးသားမဟုတ်လား။"
                ),
                "1" to FortuneAnswer(
                    en = "You will surely win – and a good ally will also come your way.",
                    mm = "နိုင်မှာ သေချာပါတယ်ဗျာ။ ခင်များအတွက် အဖော်ကောင်းတစ်ယောက် ရောက်လာဦးမှာပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "You will surely win. Feed a black cat, donate a walking stick to an elderly person, and offer a yellow flower at your home shrine.",
                    mm = "ကျိန်းသေနိုင်ပါတယ်ဗျာ။ ကြောင်နက်တကောင်ကို အစာကြွေးလိုက်ပါ။ အသက်ကြီးရွယ်အိုတယောက်ကို တောင်ဝှေးလှူလိုက်ပါ။ ပန်းဝါတပွင့် အိမ်ဘုရားမှာပဲ လှူလိုက်ပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "You will hear good news within eight days – news of your victory.",
                    mm = "(၈) ရက်အတွင်း သတင်းထူးကြားမှာပါ။ သင်နိုင်တယ်ဆိုတဲ့ သတင်းလေ။"
                ),
                "4" to FortuneAnswer(
                    en = "You will hear news of your victory within five days. Don't worry – offer five incense sticks daily if possible.",
                    mm = "ငါးရက်အတွင်း နိုင်ပြီဆိုတဲ့သတင်းကို ကြားရလိမ့်မယ်။ ဘာမှမပူပါနဲ့။ အမွေးတိုင် (၅) တိုင် ပူဇော်လိုက်ပါ။ နေ့စဉ်ဆိုရင် ပိုကောင်းပါတယ်၊၊"
                ),
                "5" to FortuneAnswer(
                    en = "At first, it may seem like you are losing – but now the tide has turned in your favor. Don't worry.",
                    mm = "အစပိုင်းမှာ သင်က ရှုံးမလိုလိုပဲ။ ဒါပေမယ့် သင့်ဘက်က နိုင်ချေပေါ်သွားပြီ။ မပူပါနဲ့။"
                ),
                "6" to FortuneAnswer(
                    en = "You have a chance to win – but someone on your side may betray you. Be careful.",
                    mm = "သင် နိုင်ဖို့ကိန်းရှိပါတယ်။ သင့်ဘက်ကတယောက် ဖေါက်လိမ့်မယ်။ သတိထားလိုက်ပါ။"
                ),
                "7" to FortuneAnswer(
                    en = "Your luck is currently weak – take extra care.",
                    mm = "သင်.ဘက်က ကံဇာတာနည်းနေတယ်။ ဂရုစိုက်လိုက်ပါ။"
                ),
                "8" to FortuneAnswer(
                    en = "Invite a dark-skinned child into your home – you will surely win.",
                    mm = "သင့်အိမ်ကို အသားမဲမဲကလေးတစ်ယောက် ခေါ်ထားလိုက်ပါ။ သင် ကျိန်းသေ နိုင်လိမ့်မည်။"
                ),
                "9" to FortuneAnswer(
                    en = "Give mooncakes to your neighbors on a full-moon day – your chance of winning will appear.",
                    mm = "အိမ်နီးနားချင်းကို လပြည့်နေ့မှာ လမုန့်ဝေလိုက်ပါ။ နိုင်ချေပေါ်သွားလိမ့်မည်။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q38",
            en = "Is there any negative energy or bad disturbance in my home?",
            mm = "မိမိယခုရောက်ရှိနေထိုင်နေရသော အိမ်ခြံတွင် မကောင်းသောအနှောင့်အယှက်ရှိ /မရှိနဲ့ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "There is a troublemaker – recite holy verses to remove it.",
                    mm = "အနှောက်အယှက်ရှိသည်။ ပရိရွတ်လိုက်ပါ။"
                ),
                "1" to FortuneAnswer(
                    en = "There is nothing of the sort.",
                    mm = "မည်သည့်အနှောင့်အယှက်မျှမရှိပါ"
                ),
                "2" to FortuneAnswer(
                    en = "No need to worry – there is nothing like that.",
                    mm = "စိုးရိမ်စရာမလိုပါ။ မည်သည့်အနှောင့်အယှက်မျှမရှိပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "Yes, there is something – but it will not cause you any trouble. No need to drive it away.",
                    mm = "အနှောက်အယှက်ရှိပါသည်။ သင့်ကိုဒုက်ခပေးမည်မဟုတ်ပါ။ မောင်း၍မထုတ်ပါနှင့်။"
                ),
                "4" to FortuneAnswer(
                    en = "It is not a disturbance – it is a protector. Just leave it be.",
                    mm = "အနှောက်အယှက်မဟုတ်ပါ။ အစောင့်အရှောက်ဖြစ်ပါတယ်။ ထားလိုက်ပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "Yes, it is troublesome. Spray vinegar in the suspicious area – it will go away.",
                    mm = "အနှောင့်အယှက်ဖြစ်ပါသည်။ မသင်္ကာသောနေရာကို ရှာလကာရည်ဖျန်းလိုက်ပါ။ အနှောက်အရှက်ကင်းသွားပါမည်။"
                ),
                "6" to FortuneAnswer(
                    en = "There is nothing like that – live with confidence.",
                    mm = "အနှောင့်အယှက်မရှိပါ။ စိတ်ချလက်ချနေပါ။"
                ),
                "7" to FortuneAnswer(
                    en = "This is a very pure place – there is nothing troubling here.",
                    mm = "အလွန်သန့်သောနေရာဖြစ်ပါသည်။ မည်သည်.အနှောက်ယှက်မှမရှိပါ။"
                ),
                "8" to FortuneAnswer(
                    en = "There is something troublesome, but it will not bother you.",
                    mm = "အနှောက်အယှက်ရှိပါသည်။ သို့သော် သင့်ကို နှောက်ယှက်လိမ့်မည်မဟုတ်ပါ။"
                ),
                "9" to FortuneAnswer(
                    en = "It is not a troublemaker – it is a protector. Do not drive it out.",
                    mm = "အနှောက်အယှက်မဟုတ်ပါ။ အစောင့်အရှောက်ဖြစ်ပါတယ်။ မောင်းမထုတ်ပါနှင့်။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q39",
            en = "Will my planned long journey be successful?",
            mm = "မိမိသည် ခရီးဝေးသွားရန်ကြံစည်နေ၏။ အောင်မြင်ခြင်းရှိနိုင် /မရှိနိုင်နှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Make sure you go – it will be very, very good.",
                    mm = "သွားဖြစ်အောင်သွားလိုက်ပါ။ အလွန်ကောင်းပါတယ်။"
                ),
                "1" to FortuneAnswer(
                    en = "You will not make this trip yet.",
                    mm = "သွားဖြစ်ဦးမယ်မဟုတ်ပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "You will surely go – everything along the way will be fine. Don't worry.",
                    mm = "မုချသွားဖြစ်လိမ့်မည်။ လမ်းခရီးတွင်လည်း အားလုံးအဆင်ပြေမည်။ ဘာမှမပူပါနဲ့။"
                ),
                "3" to FortuneAnswer(
                    en = "There will be some obstacles just before you leave – but you will still make the trip.",
                    mm = "သွားခါနီးတွင် အနှောင့်အယှက်တွေ.လိမ့်မည်။သို့သော် သွားဖြစ်လိမ့်မည်"
                ),
                "4" to FortuneAnswer(
                    en = "There is danger – it would be best not to go.",
                    mm = "အန္တရယ်ရှိပါသည်။ မသွားလျှင် အကောင်းဆုံးဖြစ်မည်။"
                ),
                "5" to FortuneAnswer(
                    en = "You will surely go – no danger and no difficulties.",
                    mm = "မုချသွားဖြစ်လိမ့်မည်။ အန္တရာယ်လည်းမရှိ အခက်အခဲလည်းမရှိပါ။"
                ),
                "6" to FortuneAnswer(
                    en = "What are you asking for? Go bravely – everything is already set.",
                    mm = "ဘာတွေလျှောက်ပြီးမေးနေတာလဲ။ ရဲရဲသွားစမ်းပါ။ အားလုံးကောင်းနေပြီပဲ။"
                ),
                "7" to FortuneAnswer(
                    en = "There is no trip as good as this one – make sure you go.",
                    mm = "ဒီခရီးလောက်ကောင်းတာ ဒီခရီးပဲရှိတယ်။ သွားဖြစ်အောင်သွားလိုက်စမ်းပါ။"
                ),
                "8" to FortuneAnswer(
                    en = "There will be some difficulties, but you will still make the trip.",
                    mm = "အခက်အခဲနည်းနည်းတော့ရှိပါတယ်။ ဒါပေမဲ့သွားရမှာပါ။"
                ),
                "9" to FortuneAnswer(
                    en = "You will hear good news about this trip soon.",
                    mm = "ဒီခရီးနဲ့ပတ်သက်တဲ့ ကောင်းသတင်း မကြာခင်ကြားရမှာပါ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q40",
            en = "Is it good to keep this person as a trusted companion or employee?",
            mm = "မိမိသည်ဤသူကို လူယုံထားလိုသည်။ အလုပ်ခန့်လိုသည်။ ကောင်း၏ /မကောင်း၏နှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "That person is not good – that's all I'll say.",
                    mm = "ဒီလူ မကောင်းဘူးနော်။ ဒါပဲပြောမယ်။"
                ),
                "1" to FortuneAnswer(
                    en = "Trust them – they are very reliable.",
                    mm = "လူယုံထားလိုက်ပါ။ အလွန်စိတ်ချရတဲ့လူဖြစ်တယ်။"
                ),
                "2" to FortuneAnswer(
                    en = "They are very good – capable and skilled at handling work.",
                    mm = "အလွန်ကောင်းတဲ့သူပါ။ လုပ်တတ်ကိုင်တတ်တဲ့သူဖြစ်တယ်။"
                ),
                "3" to FortuneAnswer(
                    en = "It would be best not to trust this person.",
                    mm = "ဒီလူကို လူယုံမထားရင် အကောင်းဆုံးပဲ။"
                ),
                "4" to FortuneAnswer(
                    en = "If you trust them, you might as well handcuff yourself first.",
                    mm = "ဒီလူကို လူယုံထားမယ်ဆိုရင်တော့ သင့်လက်ကိုသင် အရင်လက်ထိပ်ခတ်လိုက်ပါဦး။"
                ),
                "5" to FortuneAnswer(
                    en = "Go ahead – trust them and give them the position.",
                    mm = "ဒီလူကို လူယုံခန့်ဖြစ်အောင်ခန့်လိုက်ပါ။"
                ),
                "6" to FortuneAnswer(
                    en = "Hire them immediately – within six days, you will see they are suitable.",
                    mm = "ချက်ချင်းခန့်လိုက်ပါ။ သူဟာ အဲဒီနေရာနဲ့သင့်တော်ကြောင်း 6 ရက်အတွင်းမှာ သင်သိသွားလိမ့်မယ်။"
                ),
                "7" to FortuneAnswer(
                    en = "Trust them – this person has proper respect and integrity.",
                    mm = "အဲဒီလူကို လူယုံခန့်လိုက်ပါ။ ဒီလူဟာ အနန္တောအနန္တငါးပါးကို ကြည်ညိုလေးစားသူဖြစ်တယ်။"
                ),
                "8" to FortuneAnswer(
                    en = "Give them the position – all the things you have lost will come back to you.",
                    mm = "အဲဒီလူကို လူယုံခန်.လိုက်စမ်းပါ။ သင် ဆုံးရှုံးပြီးသောအရာတွေ ပြန်ပြီး ရလာလိမ့်မယ်။"
                ),
                "9" to FortuneAnswer(
                    en = "Don't trust them – give them no position. They will surely betray you.",
                    mm = "ဒီလူ့ကို လူယုံလည်းမထားနဲ့။ ဘာမှလည်းတာဝန်မပေးနဲ့။ သေသေချာချာ သစ္စာဖောက်သွားလိမ့်မယ်။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q41",
            en = "Is it good for me to work abroad/overseas?",
            mm = "နိုင်ငံရပ်ခြား ခရီးဝေးကိုသွားရောက် အလုပ်လုပ်လိုသည်။ ကောင်း၏ /မကောင်း၏နှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Make sure you go – you will become rich as soon as you arrive.",
                    mm = "သွားဖြစ်အောင်သွားစမ်းပါ။ ဟိုရောက်တာနဲ့ချမ်းသာပြီမှတ်။"
                ),
                "1" to FortuneAnswer(
                    en = "It's good – go as soon as possible.",
                    mm = "ကောင်းပါတယ်ဗျာ။ မြန်မြန်သွားစမ်းပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "Don't go – it will be worse than staying here.",
                    mm = "မသွားပါနဲ့ဗျာ။ ဒီမှာနေတာထက်တောင်ဆိုးမယ်။"
                ),
                "3" to FortuneAnswer(
                    en = "It's very, very good – make sure you go.",
                    mm = "ကောင်းပါတယ်ဗျာ။ အတော့်ကိုကောင်းတာပါပဲ။ သွားဖြစ်အောင်သွားစမ်းပါ။"
                ),
                "4" to FortuneAnswer(
                    en = "If this is your second time going, go quickly. If it's your first time, go early.",
                    mm = "ဒါဟာ ဒုတိယအကြိမ်လုပ်တယ်ဆိုရင်တော့ မြန်မြန်လုပ်လိုက်ပါ။ ပထမအကြိမ်ဆိုရင်တော့ ခပ်စောစောလေးလုပ်လိုက်ပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "Don't go – not even to the house next door. Just stay calmly at home.",
                    mm = "မသွားပါနဲ့ဗျာ။ နိင်ငံခြားမပြောနဲ့ ဟိုဘက်အိမ်တောင်မသွားနဲ့။ အေးအေးဆေးဆေး ကိုယ့်အိမ်မှာကိုယ်နေစမ်းပါ။"
                ),
                "6" to FortuneAnswer(
                    en = "Make sure you go.",
                    mm = "သွားဖြစ်အောင်သွားစမ်းပါ။"
                ),
                "7" to FortuneAnswer(
                    en = "It's best to go.",
                    mm = "သွားရင်အကောင်းဆုံးပဲ"
                ),
                "8" to FortuneAnswer(
                    en = "The time you go will be the time you become rich.",
                    mm = "သွားတဲ့အချိန်ဟာ ချမ်းသာတဲ့အချိန်ပဲလို့ နားလည်ပါတယ်ဗျာ။"
                ),
                "9" to FortuneAnswer(
                    en = "Don't go – there is a risk to your life.",
                    mm = "မသွားပါနဲ့။ အသက်အန္တရာယ်စိုးရိမ်ရတယ်နော်။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q42",
            en = "Can I recover my lost property or money?",
            mm = "မိမိသည် ပစ္စည်း (သို့မဟုတ်) ငွေပျောက်ဆုံးနေ၏။ ပြန်၍ ရနိုင် /မရနိုင်နှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "The chances of getting it back are very high.",
                    mm = "ရဖို့ဘက်က မဲသာနေပါပြီ"
                ),
                "1" to FortuneAnswer(
                    en = "You will surely get it back – place a walking stick at the place where you lost it.",
                    mm = "မုချပြန်ပြီး ရမှာပါဗျာ။ တောင်ဝှေးတချောင်း ပစ္စည်းပျောက်နေတဲ့နေရာမှာ ထားလိုက်စမ်းပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "The lost item will surely return to you – place a yellow flower and a stick at the spot where it was lost. If possible, feed a black cat there as well.",
                    mm = "ပျောက်ဆုံးနေသောပစ္စည်း မုချပြန်ရမှာပါ။ ပျောက်ဆုံးသောနေရာတွင် ပန်းဝါတပွင့် တုတ်တစ်ချောင်းထားလိုက်ပါ။ ဖြစ်နိုင်လျှင် ထိုနေရာတွင် ကြောင်နက်ကိုအစာကျွေးလိုက်ပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "You will get it back – sprinkle water mixed from two ponds or lakes at the place where it was lost.",
                    mm = "ပြန်ပြီးရမှာပါ။ ပျောက်ဆုံးနေသောနေရာတွင် ရေကန်နှစ်ကန်မှရေကို အနည်းငယ်စပ်ပြီး ပက်ဖျန်းထားလိုက်ပါ။"
                ),
                "4" to FortuneAnswer(
                    en = "It will not be easy to get it back.",
                    mm = "ရဖို့မလွယ်ပါ"
                ),
                "5" to FortuneAnswer(
                    en = "You will surely get it back.",
                    mm = "ရမှာသေချာပါတယ်ဗျာ"
                ),
                "6" to FortuneAnswer(
                    en = "Just assume that you have already gotten it back.",
                    mm = "ရပြီဟု သဘောထားလိုက်စမ်းပါ"
                ),
                "7" to FortuneAnswer(
                    en = "It will not be easy to get back – the item has moved to another place.",
                    mm = "ရဖို့မလွယ်ပါ။ ပစ္စည်းက တစ်နေရာကို ရောက်သွားပြီ။"
                ),
                "8" to FortuneAnswer(
                    en = "You will get it back – don't worry.",
                    mm = "ရပြီဗျာ ။ မပူပါနဲ့"
                ),
                "9" to FortuneAnswer(
                    en = "You will surely get it back.",
                    mm = "ကျိန်းသေ ပြန်ပြီးရမှာပါ"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q43",
            en = "Will the traveler return back safely?",
            mm = "ခရီးသွားသူ ပြန်လည်ရောက်ရှိခြင်း /မရောက်ရှိခြင်းနှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "They have not returned yet – you will need to wait a little longer.",
                    mm = "မရောက်သေးပါဘူး။ နည်းနည်းတော့စောင့်ရပါဦးမယ်။"
                ),
                "1" to FortuneAnswer(
                    en = "They will return – you can count on it.",
                    mm = "ပြန်ရောက်မှာပါ။ လောင်းတောင်လောင်းရဲသေးတယ်။"
                ),
                "2" to FortuneAnswer(
                    en = "There is some trouble preventing them from returning.",
                    mm = "ပြန်ရောက်ဖို့ကိစ္စမှာ အနှောက်အယှက်ကတွေ့နေတယ်။"
                ),
                "3" to FortuneAnswer(
                    en = "They will return within three days.",
                    mm = "3 ရက်အတွင်းပြန်ရောက်မှာပါ"
                ),
                "4" to FortuneAnswer(
                    en = "They will return safely and in good condition.",
                    mm = "ချမ်းချမ်းသာသာ ပြည့်ပြည့်စုံစုံနဲ့ ပြန်ရောက်လာမှာပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "There is some work keeping them there – but they will return soon.",
                    mm = "ဟိုမှာ အလုပ်ဖြစ်စရာရှိနေလို့ပါ။ မကြာခင် ပြန်ရောက်လာလိမ့်မယ်။"
                ),
                "6" to FortuneAnswer(
                    en = "They will definitely return.",
                    mm = "ပြန်ရောက်ဖို့သေချာပါတယ်ဗျာ။"
                ),
                "7" to FortuneAnswer(
                    en = "Some matters are holding them back – but they will return soon.",
                    mm = "အကြောင်းအရာတခုက ဆွဲထားသလိုဖြစ်နေတယ်။ ဒါကြောင့် တိုးလို့တန်းလန်းကြီးဖြစ်နေတာပါ။ ပြန်ရောက်တော့မှာပါ။"
                ),
                "8" to FortuneAnswer(
                    en = "It will still be some time before they return.",
                    mm = "ပြန်ရောက်ဖို့လိုသေးတယ်"
                ),
                "9" to FortuneAnswer(
                    en = "Just assume that they have already returned.",
                    mm = "ပြန်ရောက်ပြီလို့ပဲသဘောထားလိုက်ပါတော့"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q44",
            en = "Will the couple get divorced for good or reconcile?",
            mm = "လင်မယား အပြီးအပိုင် ကွဲသွားခြင်း /မကွဲသွားခြင်းနှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "It will be difficult to bring them back together.",
                    mm = "ပြန်ပေါင်းဖို့ ခက်လိမ့်မယ်။"
                ),
                "1" to FortuneAnswer(
                    en = "This is nothing new – they will not separate. It's just temporary.",
                    mm = "ဒီလိုဖြစ်တာရိုးနေပါပြီ။ မကွဲပါဘူး ။ ခဏပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "They will not separate.",
                    mm = "မကွဲပါဘူးဗျာ"
                ),
                "3" to FortuneAnswer(
                    en = "They will hover between breaking up and staying together, but no divorce will happen.",
                    mm = "ကွဲသလိုလို မကွဲသလိုလိုနဲ့ တို့လို့တန်းလန်းကြီးဖြစ်နေမှာပါ။ မကွဲပါဘူး။"
                ),
                "4" to FortuneAnswer(
                    en = "Things are getting worse and worse – it will be hard to bring them back together.",
                    mm = "အပျက်ပျက်နဲ့နှာခေါင်းသွေးထွက်နေပြီဗျို့။ ပြန်ပေါင်းဖို့ခက်လိမ့်မယ်။"
                ),
                "5" to FortuneAnswer(
                    en = "No, they will not separate.",
                    mm = "မကွဲပါဘူးဗျာ"
                ),
                "6" to FortuneAnswer(
                    en = "They are close, yet drifting apart.",
                    mm = "နီးလျက်နဲ့ဝေးသွားလိမ့်မယ်"
                ),
                "7" to FortuneAnswer(
                    en = "If they don't want to separate, they should invite a dark-skinned child to help mediate and reconcile.",
                    mm = "မကွဲချင်ရင်တော့ အသားမဲမဲကလေးတစ်ယောက်ကိုခေါ်ထားပြီး စေ့စပ်ရမှာပဲ။"
                ),
                "8" to FortuneAnswer(
                    en = "No, they will not separate.",
                    mm = "မကွဲပါဘူးဗျာ"
                ),
                "9" to FortuneAnswer(
                    en = "They will not separate at all.",
                    mm = "လုံးဝမကွဲပါဘူး"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q45",
            en = "Will the person trying to harm or divide my friendships succeed?",
            mm = "လူတယောက်သည် မိမိအား အကွက်ဆင်၍ ဒုက္ခပေးနေ၏။ (သို့မဟုတ်) မိမိနှင့် သင့်မြတ်သူကြားထဲတွင် ခြေထိုးနေ၏။ ထိုကိစ္စနှင့်ပတ်သက်၍ထိခိုက်နိုင်ခြင်း /မထိခိုက်နိုင်ခြင်းနှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Offer nine yellow flowers – no need to worry. If possible, release a bird as a good deed. Everything will be resolved.",
                    mm = "ပန်းဝါ(၉)ပွင့်လှူလိုက်ပါ။ ဘာမှစိုးရိမ်စရာမလိုပါဘူး။ ဖြစ်နိုင်ရင် ငှက်တကောင်ပါလွှတ်ပြီး ကုသိုလ်လုပ်လိုက်ပါ။ အားလုံးပြီးသွားမှာပါ။"
                ),
                "1" to FortuneAnswer(
                    en = "No impact at all.",
                    mm = "လုံးဝထိခိုက်စရာမရှိပါ"
                ),
                "2" to FortuneAnswer(
                    en = "That person is certainly wicked, but you will not be troubled by them.",
                    mm = "အဲဒီလူ စိတ်ထားယုတ်တာတော့ သေချာပေါက်ပဲ။ ဒါပေမယ့် သင့်ကို ထိခိုက်လိမ့်မည် မဟုတ်ပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "This person is worse than just a little evil – they could even break apart a married couple.",
                    mm = "ဒီလူဟာ နည်းနည်းဆိုးတဲ့လူမဟုတ်ဘူးနော်။ လင်မယားကြားထဲမှာတောင် ကွဲသွားအောင် ချောက်တွန်းတဲ့ မကောင်းဆိုးဝါးတစ်မျိုးပဲ။"
                ),
                "4" to FortuneAnswer(
                    en = "Don't worry – nothing will happen. Donate a walking stick to an elderly person and offer a flower at the shrine.",
                    mm = "ဘာမှမပူပါနဲ့၊ ဘာမှမဖြစ်ပါဘူး။ အသက်အရွယ်ကြီးတဲ့သူကို တောင်ဝှေးတချောင်းလှူလိုက်ပါ။ ဘုရားကို ပန်းပွင့်တပွင့်လှူလိုက်ပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "No trouble at all.",
                    mm = "ဘယ်လိုမှဒုက္ခရောက်စရာမရှိပါဘူး"
                ),
                "6" to FortuneAnswer(
                    en = "There is no trouble here.",
                    mm = "ဒုက္ခရောက်ဖို့မရှိပါဘူး"
                ),
                "7" to FortuneAnswer(
                    en = "Make a strong resolution and do good deeds for nine days – that person will disappear from your life.",
                    mm = "အဓိဌာန် 9 ရက်လောက် ဝင်လိုက်စမ်းပါ။ ဒီလူ ဂျွန်းတောင်ပစ်သွားအုံးမယ်၊၊"
                ),
                "8" to FortuneAnswer(
                    en = "Take two cups of water, pour one into the other, and repeat this 14 times in front of your house – everything will be resolved.",
                    mm = "ရေခွက် (J) နှစ်ခွက်ကို လက်နဲ့ကိုင်ပြီး တစ်ခွက်ထဲကရေကို အခြားတစ်ခွက်ထဲထည့်လိုက်ပါ။ ဟိုဘက်ထဲရောက်သွားတဲ့ ရေကို ဒီဘက်ထဲ ပြန်ထည့်လိုက်။ အိမ်ရှေ့ထွက်ပြီး 14 ခါလောက် လုပ်လိုက်စမ်းပါ။ အားလုံးပြီးသွားမှာပါ"
                ),
                "9" to FortuneAnswer(
                    en = "Just a little frustration – nothing serious will come of it.",
                    mm = "စိတ်နည်းနည်း ညစ်ရတာပါ။ ဘာမှတော့မဖြစ်ပါဘူး။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q46",
            en = "Is this situation life-threatening/fatal or not?",
            mm = "အသက်အန္တရာယ်ရှိနိုင် /မရှိနိုင်နှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "You are just asking for the sake of asking – how could any life danger possibly happen to you?",
                    mm = "ခင်ဗျားမို့ဗျာ ဒာလုပ်မရှိ အလုပ်ရှာပြီး မဟုတ်တာတွေမေးနေတယ်။ ဘယ်လိုလုပ်ပြီး အသက်အန္တရာယ်စိုးရိမ်ရမှာလဲဗျာ"
                ),
                "1" to FortuneAnswer(
                    en = "No danger to your life at all – what kind of question is that?!",
                    mm = "လုံးဝအသက်အန္တရာယ်မရှိပါဘူးဗျာ။ ဘာတွေ လျှောက်မေးနေတာလဲ။"
                ),
                "2" to FortuneAnswer(
                    en = "None at all – just stay peaceful.",
                    mm = "လုံးဝမရှိပါ။ စိတ်အေးချမ်းသာစွာနေပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "Don't even ask about life danger – there is nothing to worry about.",
                    mm = "အသက်အန္တရာယ်နဲ့ပတ်သက်လို့တော့ မမေးပါနဲ့။ ဘာမှစိုးရိမ်စရာမရှိပါဘူး။"
                ),
                "4" to FortuneAnswer(
                    en = "A female deity is watching over you – don't worry.",
                    mm = "သင့်ကို မယ်တော်တစ်ပါးက အတိအကျ စောင့်ရှောက်နေတယ်။ ဘာမှမပူပါနဲ့။"
                ),
                "5" to FortuneAnswer(
                    en = "There is nothing to worry about – why are you asking such things?",
                    mm = "ပူစရာမဟုတ်တာဗျာ။ ဘာတွေလျှောက်ပြီးမေးနေတာလဲ။"
                ),
                "6" to FortuneAnswer(
                    en = "You have good karma – why are you even asking?",
                    mm = "သင်ဟာ ကံကောင်းနေသူတစ်ယောက်ပါ ။ ဘယ့်နှယ်ကြောင့် မေးနေရတာတုန်း။"
                ),
                "7" to FortuneAnswer(
                    en = "There is opposition, but it is not a life-threatening danger.",
                    mm = "အတိုက်အခံတော့ရှိပါတယ်။ အသက်အန္တရယ်တော့မဟုတ်ပါဘူး"
                ),
                "8" to FortuneAnswer(
                    en = "Being careful never hurts – just be a little more aware.",
                    mm = "သတိဆိုတာ ပိုတယ်လို့မရှိပါဘူးဗျာ။ နည်းနည်းလေး သတိထားလိုက်စမ်းပါ။"
                ),
                "9" to FortuneAnswer(
                    en = "Nothing to worry about at all.",
                    mm = "ဘာမှပူစရာမလိုပါဘူး"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q47",
            en = "Are my doubts and suspicions about this matter correct?",
            mm = "သံသယရှိခြင်းသည် မှန်၏ /မှား၏နှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "This doubt will be resolved soon on its own.",
                    mm = "အဲဒီသံသယစိတ်ဟာ မကြာခင် ဖြေရှင်းပီးသားဖြစ်သွားမှာပါ"
                ),
                "1" to FortuneAnswer(
                    en = "Let go of that doubtful mind – it will bring you nothing good. It's not true.",
                    mm = "အဲဒီသံသယစိတ်ကို ဖျောက်လိုက်ပါ။ အကျိုးမရှိပါဘူး။ ဟုတ်လည်းမဟုတ်ဘဲနဲ့။"
                ),
                "2" to FortuneAnswer(
                    en = "A doubtful eye sees doubtful things – but in reality, there is nothing. Forget your doubts.",
                    mm = "သံသယမျက်လုံးနဲ့ကြည့်ရင် သံသယဖြစ်စရာတွေ တွေ့တတ်တယ်ဗျာ။ တကယ်တော့ဘာမှမဟုတ်ပါဘူး။ သံသယစိတ်တွေဖျောက်လိုက်ပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "Hatred begins with doubt – there is nothing to it. Let go of your suspicion.",
                    mm = "လူလူချင်းမုန်းတယ်ဆိုတာ သံသယစိတ်က စတာဗျ။ ဘာမှမဟုတ်ဘဲနဲ့ သံသယစိတ်တွေ ဖျောက်လိုက်စမ်းပါ\""
                ),
                "4" to FortuneAnswer(
                    en = "Don't be suspicious – you will be mistaken.",
                    mm = "သံသယမထားစမ်းပါနဲ့။ မှားကုန်လိမ့်မယ်။"
                ),
                "5" to FortuneAnswer(
                    en = "Unnecessary doubts can cause great harm to others.",
                    mm = "ဘာမှမဟုတ်ဘဲနဲ့ သံသယထားရင် တဘက်လူဟာ အများကြီးနစ်နာပါတယ်။"
                ),
                "6" to FortuneAnswer(
                    en = "Let go of your doubts – everything is fine as it is.",
                    mm = "သံသယစိတ်တွေဖျောက်လိုက်ပါ။ အားလုံးဟာ သူ့အတိုင်းတိုင်း ကောင်းနေပါတယ်။"
                ),
                "7" to FortuneAnswer(
                    en = "Get rid of those doubts as quickly as possible.",
                    mm = "သံသယစိတ်ကိုအမြန်ဆုံးဖျောက်လိုက်ပါ။"
                ),
                "8" to FortuneAnswer(
                    en = "Doubt will cause you more trouble than anyone else – let it go immediately.",
                    mm = "သံသယစိတ်ဟာ တခြားလူအား ဒီလောက် ဒုက္ခမပေးဘူး။ သင့်ကိုသာပေးလိမ့်မယ်။ အမြန်ဆုံး အဲဒီစိတ်ကို ဖျောက်လိုက်ပါ။"
                ),
                "9" to FortuneAnswer(
                    en = "This is not caused by that person – it is the work of evil spirits. Sprinkle holy water around the whole house.",
                    mm = "ခင်ဗျား အခုလိုသံသယဖြစ်နေတာ ဒီလူ့ကြောင့်မဟုတ်ဘူး။ မကောင်းဆိုးဝါးတွေက လုပ်နေတာ။ တစ်အိမ်လုံးကို ပရိတ်ရေနဲ့ ပက်လိုက်စမ်းပါ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q48",
            en = "How will the struggle regarding this case/issue turn out?",
            mm = "အကြောင်းအရာတစ်ခုကို စိတ်ပူနေရခြင်းနှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "It's okay to be worried, but try not to let it bring you down.",
                    mm = "ပူစရာရှိတော့လည်း ပူတာပေါ့ဗျာ။ ဒါပေမယ့် စိတ်ထောင်းကိုယ်ကျေ မဖြစ်အောင်တော့ နေတတ်စမ်းပါ။"
                ),
                "1" to FortuneAnswer(
                    en = "You are worrying for no reason – it will soon pass.",
                    mm = "သင့်ရဲ့စိတ်ပူနေရခြင်းဟာ အကြောင်းမဲ့ ပူနေရခြင်းဖြစ်ပါတယ်။ မကြာခင် ငြိမ်းအေးသွားမှာပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "Your worry will fade after a little frustration – it's nothing serious.",
                    mm = "သင့်ရဲ့စိတ်ပူပန်ခြင်းဟာ နည်းနည်းလေးစိတ်ညစ်ပြီးရင် ပြီးသွားမှာပါ။ ဘာမှလည်းမဟုတ်ပါဘူး။"
                ),
                "3" to FortuneAnswer(
                    en = "That worried feeling will soon calm down.",
                    mm = "မကြာခင်မှာ အဲဒီစိတ်ပူပန်ခြင်းဟာ ငြိမ်းအေးသွားမှာပါ။"
                ),
                "4" to FortuneAnswer(
                    en = "You are worrying about something that isn't worth worrying about.",
                    mm = "စိတ်ပူပန်စရာမဟုတ်ဘဲ ပူပန်နေတာပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "There is no need to worry – you are just worrying for the sake of worrying.",
                    mm = "ပူပန်ဘို့ကို မလိုပါဘူး။ ကိုယ့်ဟာကို ပူခြင်လို့ ပူနေတယ်"
                ),
                "6" to FortuneAnswer(
                    en = "Don't worry – there is nothing to be concerned about. It will sort itself out.",
                    mm = "မပူပါနဲ့ဗျာ။ ပူစရာမှမဟုတ်ဘဲ။ သူ့ဟာသူ ပြီးသွားမှာပါ။"
                ),
                "7" to FortuneAnswer(
                    en = "Don't worry – there are many people who will help you.",
                    mm = "မပူပါနဲ့။ သင့်ကို ကူညီမည့်သူတွေက အများကြီးပဲ။"
                ),
                "8" to FortuneAnswer(
                    en = "Don't worry about things that aren't worth worrying about.",
                    mm = "ပူစရာမဟုတ်တာ မပူပါနဲ့တော့။"
                ),
                "9" to FortuneAnswer(
                    en = "Your worries will disappear very soon.",
                    mm = "သင့်ရဲ့ပူပန်စရာတွေဟာ အမြန်ဆုံးပျောက်ကင်းသွားမှာပါ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q49",
            en = "How is my love affair/relationship right now?",
            mm = "အချစ်ရေးသည် ယခုအခါ၌ ကောင်း၏လော /မကောင်း၏လောနှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Everything will be fine – no need to worry.",
                    mm = "အားလုံးအဆင်ပြေမှာပါ။ ပူစရာကို မလိုဘူး။"
                ),
                "1" to FortuneAnswer(
                    en = "Your love life is very good at this time.",
                    mm = "သင့်ရဲ့အချစ်ရေးဟာ ခုအခါမှာ အလွန်ကောင်းပါတယ်။"
                ),
                "2" to FortuneAnswer(
                    en = "Your love life is good – but an ill-minded person will try to interfere.",
                    mm = "သင့်ရဲ့အချစ်ရေးဟာကောင်းပါတယ်။ ဒါပေမယ့် စိတ်ကောင်းမရှိတဲ့ လူတစ်ယောက်ဝင်ပြီး နှောက်ယှက်လိမ့်မယ်။"
                ),
                "3" to FortuneAnswer(
                    en = "There may be a little disappointment in love – but nothing serious.",
                    mm = "အချစ်ရေးအတွက်တော့ နည်းနည်းစိတ်ညစ်ရမယ်။ ဘာမှတော့မဖြစ်ပါဘူး၊၊"
                ),
                "4" to FortuneAnswer(
                    en = "Everything in your love life is fine – don't worry.",
                    mm = "အချစ်ရေးကိစ္စ အားလုံးကောင်းမွန်နေပါတယ်။ မပူပါနဲ့။"
                ),
                "5" to FortuneAnswer(
                    en = "Everything is good – nothing to worry about.",
                    mm = "အားလုံးကောင်းပါတယ်။ ပူစရာကို မရှိဘူး။"
                ),
                "6" to FortuneAnswer(
                    en = "Family matters may interfere with your love life – but it will settle down soon.",
                    mm = "အိမ်တွင်းရေးကြောင့် အချစ်ရေးမှာလာပြီး အနှောက်အယှက်တွေ့တတ်တယ်။ ဒါပေမဲ့ မကြာခင် ပြီးသွားမှာပါ။"
                ),
                "7" to FortuneAnswer(
                    en = "Nothing to worry about – everything is good.",
                    mm = "ဘာမှ ပူစရာမလိုပါဘူးဗျာ။ အားလုံးကောင်းနေပါတယ်။"
                ),
                "8" to FortuneAnswer(
                    en = "No need to worry – everything is fine.",
                    mm = "ပူစရာကိုမလိုတာပါ။ အားလုံးအဆင်ပြေပါတယ်။"
                ),
                "9" to FortuneAnswer(
                    en = "A big obstacle will come – you will need to face it.",
                    mm = "အနှောက်အယှက် ကြီးကြီးမားမားတခု တွေ့လိမ့်မယ်ဗျို့။ အဲဒါကိုတော့ ရင်ဆိုင်လိုက်ပါ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q50",
            en = "Is this wedding/marriage a good idea?",
            mm = "မင်္ဂလာဆောင်ခြင်းနှင့်ပတ်သက်၍ ကောင်းခြင်း /ဆိုးခြင်းနှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "You cannot do it yet – there are still too many obstacles. Take your time and do it calmly.",
                    mm = "မလုပ်ဖြစ်သေးပါဘူး။ နှောက်ယှက်မယ့်ဟာတွေက အများကြီးရှိသေးတယ်။ အေးအေးဆေးဆေး လုပ်စမ်းပါ။"
                ),
                "1" to FortuneAnswer(
                    en = "It will keep you busy – just do it quickly.",
                    mm = "အလုပ်များမှာတစ်ခုပါပဲ။ အမြန်ဆုံး လုပ်လိုက်ပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "Do it fast – everything will be fine.",
                    mm = "အမြန်ဆုံးလုပ်ပါ။ အဆင်ပြေပါတယ်။"
                ),
                "3" to FortuneAnswer(
                    en = "The signs say yes – do it quickly.",
                    mm = "လုပ်ပါလို့အချက်ပေးတယ်ဗျာ။ မြန်မြန်လုပလိုက်ပါ။"
                ),
                "4" to FortuneAnswer(
                    en = "What else would you rather do than get married? This is the right time for it.",
                    mm = "ခင်များအဖို့ မင်္ဂလာဆောင်တဲ့အလုပ်ကလွဲလို့ ဘာတွေလုပ်ချင်သေးလို့လည်း။ ဒီအချိန်မှာ မင်္ဂလာဆောင်ရမယ်။"
                ),
                "5" to FortuneAnswer(
                    en = "You should get married.",
                    mm = "မင်္ဂလာဆောင်သင့်ပါတယ်။"
                ),
                "6" to FortuneAnswer(
                    en = "Getting married? Yes – go ahead and marry.",
                    mm = "မင်္ဂလာဆောင်မလို.လား။ ဆောင်တော့... .ဆောင်တော့။"
                ),
                "7" to FortuneAnswer(
                    en = "You will become wealthy after marriage – get married soon.",
                    mm = "မင်္ဂလာဆောင်ပြီးရင် သဌေးဖြစ်ဦးမှာ။ မြန်မြန်ဆောင်ပေတော့။"
                ),
                "8" to FortuneAnswer(
                    en = "The sooner, the better.",
                    mm = "မြန်လေကောင်းလေပဲဗျို့"
                ),
                "9" to FortuneAnswer(
                    en = "Don't do it yet – wait a while longer.",
                    mm = "မလုပ်နဲ့ဦး။ အချိန်ဆွဲထားပါ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q51",
            en = "Will I get promoted to a higher rank?",
            mm = "မိမိသည် ရာထူးတက်နိုင်ခြင်းရှိ /မရှိနှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "It will be delayed a little. Find the remedy section in this book and follow it.",
                    mm = "နည်းနည်းနှောင့်နှေးလိမ့်မယ်။ ဒီစာအုပ်ထဲမှာ ယတြာပါတယ်။ ရှာပြီးချေလိုက်ပါ။"
                ),
                "1" to FortuneAnswer(
                    en = "You will definitely get promoted.",
                    mm = "ကျိန်းသေရာထူးတက်မယ်ဗျာ"
                ),
                "2" to FortuneAnswer(
                    en = "There is some opposition, but the promotion will still come.",
                    mm = "အတိုက်အခံလေးတော့ရှိတယ်။ တက်တော့တက်မှာပဲ။"
                ),
                "3" to FortuneAnswer(
                    en = "You will need to wait a little longer.",
                    mm = "အချိန်နည်းနည်းလေးစောင့်ရဦးမယ်"
                ),
                "4" to FortuneAnswer(
                    en = "There is some interference – settle that first.",
                    mm = "အနှောက်အယှက်ကရှိနေတယ်။ အဲဒီအနှောက်ယှက်ကို အရင်ရှင်းစမ်းပါအုံး။"
                ),
                "5" to FortuneAnswer(
                    en = "You will surely get it.",
                    mm = "မုချတက်မယ်ဗျာ"
                ),
                "6" to FortuneAnswer(
                    en = "Some jealous people are trying to ruin your promotion.",
                    mm = "သင် ရာထူးတက်မှာကို မနာလိုတဲ့သူတွေက လိုက်ပြီးဖျက်နေကြတယ်။"
                ),
                "7" to FortuneAnswer(
                    en = "You will get promoted.",
                    mm = "ရာထူးတက်မှာပါ"
                ),
                "8" to FortuneAnswer(
                    en = "You will hear good news within nine days – promotion is certain. Offer nine yellow flowers at a shrine.",
                    mm = "9 ရက်အတွင်းသတင်းထူးလိမ့်မယ်။ ရာထူး မုချတက်မယ်။ ပန်းဝါ(၉) ပွင့် လှူလိုက်ပါ"
                ),
                "9" to FortuneAnswer(
                    en = "It is certain – don't worry about it.",
                    mm = "ကျိန်းသေပါတယ်ဗျာ။ ဒီကိစ္စအတွက်မပူပါနဲ့။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q52",
            en = "Will my workplace or business venue have to relocate?",
            mm = "အလုပ်နေရာ ပြောင်းရခြင်း ရှိ /မရှိနှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "A change is coming soon.",
                    mm = "ပြောင်းရတော့မယ်"
                ),
                "1" to FortuneAnswer(
                    en = "It will not change for now.",
                    mm = "လောလောဆယ် မပြောင်းရသေးပါဘူးဗျာ"
                ),
                "2" to FortuneAnswer(
                    en = "No change yet.",
                    mm = "မပြောင်းရသေးပါဘူး"
                ),
                "3" to FortuneAnswer(
                    en = "Yes – a change is coming soon, and it is certain. The new place will be very good.",
                    mm = "ပြောင်းရတော့မယ်ဗျို.။ သေချာတယ်။ ပြောင်းတဲ့နေရာသိပ်ပြီးကောင်းတယ်"
                ),
                "4" to FortuneAnswer(
                    en = "You will be moving to a good place soon.",
                    mm = "နေရာကောင်းကိုပြောင်းရတော့မယ်"
                ),
                "5" to FortuneAnswer(
                    en = "At first it may seem like no change is needed, but it will happen – and it will be a good change.",
                    mm = "မပြောင်းရတော့ဘူးလိုလိုနဲ့ ပြောင်းသွားရလိမ့်မယ်။ ပြောင်းရတာဟာ ကောင်းပါတယ်။"
                ),
                "6" to FortuneAnswer(
                    en = "No change yet – some cleanup or clearance is still needed here first.",
                    mm = "မပြောင်းရသေးဘူး။ ဒီနေရာက အမှိုက်တွေရှင်းရဦးမယ်။"
                ),
                "7" to FortuneAnswer(
                    en = "No change yet.",
                    mm = "မပြောင်းရသေးပါဘူး"
                ),
                "8" to FortuneAnswer(
                    en = "You are heading toward a good place – and it won't take long.",
                    mm = "နေရာကောင်းကို ရောက်တော့မယ်။ သိပ်ပြီးမကြာတော့ပါဘူး။"
                ),
                "9" to FortuneAnswer(
                    en = "There are people trying to prevent the change from happening.",
                    mm = "မပြောင်းရအောင် လိုက်ပြီးလုပ်နေတဲ့သူတွေ ရှိတယ်နော်။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q53",
            en = "Will I get the job/position I applied for?",
            mm = "မိမိက အလုပ်တစ်ခုရရန်အတွက် အလုပ်လျှောက်ထားသည်။ ရနိုင် /မရနိုင်နှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "It will be difficult, but you will get it.",
                    mm = "ခက်တော့ ခက်တယ်။ဒါပေမဲ့ ရမှာပါ။"
                ),
                "1" to FortuneAnswer(
                    en = "You will surely get it.",
                    mm = "ကျိန်းသေရပါတယ်ဗျာ"
                ),
                "2" to FortuneAnswer(
                    en = "Don't worry about the job – just consider it already yours.",
                    mm = "အလုပ်အတွက်မပူပါနဲ့။ ရပြီလို့သဘောထားလိုက်ပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "You will certainly get it.",
                    mm = "သေချာပေါက်ရပါပြီဗျာ"
                ),
                "4" to FortuneAnswer(
                    en = "It is certain that you will get it.",
                    mm = "အလုပ်ရမှာသေချာပါတယ်"
                ),
                "5" to FortuneAnswer(
                    en = "Don't worry about not getting it – you will surely get it.",
                    mm = "မရမှာ မပူနဲ့။ ကျိန်းသေရမှာပါ။"
                ),
                "6" to FortuneAnswer(
                    en = "Just assume that you already have it.",
                    mm = "ရပြီလို့ သဘောထားပေတော့။"
                ),
                "7" to FortuneAnswer(
                    en = "It's a must – you will get it.",
                    mm = "မရလို့မဖြစ်ဘူးလေ။ ရ ရမှာပေါ့။"
                ),
                "8" to FortuneAnswer(
                    en = "There will be some difficulties.",
                    mm = "အခက်အခဲတွေ့လိမ့်မယ်"
                ),
                "9" to FortuneAnswer(
                    en = "Go to a monastery with a dark-skinned person and donate a walking stick – after that, you will surely get the job.",
                    mm = "အသားမဲမဲလူတစ်ယောက်နဲ့အတူ ဘုန်းကြီးကျောင်းကိုသွားပြီး တောင်ဝှေးလှူလိုက်ပါ။ အဲဒီလိုလုပ်လိုက်ရင် အလုပ်ရမှာ ကျိန်းသေပါတယ်။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q54",
            en = "Will I win the contest or race I am participating in?",
            mm = "မိမိသည် ပြိုင်ပွဲတစ်ခုသို့ ဝင်ရန်ရှိသည်။ ထိုပြိုင်ပွဲတွင်အနိုင်ရခြင်းရှိ /မရှိနှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "You will win.",
                    mm = "နိုင်မှာပဲ"
                ),
                "1" to FortuneAnswer(
                    en = "You are a winner.",
                    mm = "အနိုင်ရပါတယ်ဗျာ"
                ),
                "2" to FortuneAnswer(
                    en = "You will win.",
                    mm = "နိုင်မှာပါဗျာ"
                ),
                "3" to FortuneAnswer(
                    en = "Don't worry – you will win.",
                    mm = "မနိုင်မှာ မပူပါနဲ့'။"
                ),
                "4" to FortuneAnswer(
                    en = "Just consider it a win already.",
                    mm = "နိုင်ပြီလို့မှတ်ပေတော့။"
                ),
                "5" to FortuneAnswer(
                    en = "You will surely win.",
                    mm = "နိုင်မှာသေချာပါတယ်"
                ),
                "6" to FortuneAnswer(
                    en = "You have already won.",
                    mm = "နိုင်ပြီးသားပါဗျာ"
                ),
                "7" to FortuneAnswer(
                    en = "You will not just win – you will exceed beyond winning.",
                    mm = "နိုင်တာထက် ဟိုဘက်တောင် လွန်နေသေးတယ်။"
                ),
                "8" to FortuneAnswer(
                    en = "The chances of losing are higher – be careful.",
                    mm = "ရှုံးဖို့ကများနေတယ်။ ဂရုစိုက်ပါ။"
                ),
                "9" to FortuneAnswer(
                    en = "It's a win.",
                    mm = "နိုင်ပြီဗျို့"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q55",
            en = "What theme/content should I choose for my painting or story?",
            mm = "မိမိသည် ပန်းချီဆွဲလို၏ ။ ဇာတ်လမ်းရေးလို၏။ မည်သည့်အကြောင်းအရာကို ဆွဲရမည်။ မည်သည့်ဇာတ်လမ်းကို ရေးရမည်ကိုဟောသော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Start with a quick and agile person.",
                    mm = "လျင်မြန်သွက်လက်တဲ့ လူတယောက်အကြောင်းကနေ စလိုက်ပါ။"
                ),
                "1" to FortuneAnswer(
                    en = "Start with a ruined building.",
                    mm = "ပြိုပျက်နေသော အဆောက်အဦးကြီးကနေ စလိုက်ပါ"
                ),
                "2" to FortuneAnswer(
                    en = "Start with a couple standing beside a building.",
                    mm = "အဆောက်အဦးကြီးဘေးက စုံတွဲတတွဲနဲ့ စလိုက်ပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "Start with a traveler riding a white horse.",
                    mm = "မြင်းဖြူကလေးနဲ့ ခရီးသွားနေတဲ့ လူတယောက်နဲ့ စလိုက်ပါ။"
                ),
                "4" to FortuneAnswer(
                    en = "Start with a young person in a grassy field.",
                    mm = "မြက်ခင်းပြင်တစ်ခုနှင့် လူငယ်လေးအကြောင်းက စလိုက်ပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "Start with a young woman who loves animals.",
                    mm = "တိရစ္ဆာန်ချစ်တဲ့ မိန်းကလေးတစ်ယောက်နဲ့ စလိုက်ပါ"
                ),
                "6" to FortuneAnswer(
                    en = "Start with a talented young person with many skills.",
                    mm = "စွမ်းရည်ရှိတဲ့ လူငယ်တစ်ယောက်နဲ့ စလိုက်ပါ။"
                ),
                "7" to FortuneAnswer(
                    en = "Start with a happy family.",
                    mm = "ပျော်ရွှင်စရာကောင်းတဲ့ မိသားစုကနေ စလိုက်ပါ။"
                ),
                "8" to FortuneAnswer(
                    en = "Start with a young person who can dance.",
                    mm = "ကတတ်တဲ့ လူငယ်လေးတယောက်နဲ့ စလိုက်ပါ။"
                ),
                "9" to FortuneAnswer(
                    en = "Start with a person who went from poverty to wealth.",
                    mm = "ဆင်းရဲဒုက္ခရောက်နေရာကနေ ချမ်းသာသွားတဲ့လူတယောက်အကြောင်းကနေ စလိုက်ပါ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q56",
            en = "Will my efforts to achieve my goal be successful?",
            mm = "မိမိသည် တစ်စုံတစ်ခုကို အောင်မြင်ရန် ကြိုးပမ်းနေ၏။ ထိုကြိုးပမ်းမှုသည် အောင်မြင်ရန် ရှိခြင်း /မရှိခြင်းနှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Whether it seems crazy or not – if you try like crazy, how can you not succeed?",
                    mm = "ထူးရင်ထူး မထူးရင်ရူးပေါ့ဗျာ။ ရူးမတတ်ကြိုးစားရင် ဘာလို့ မအောင်မြင်ရမှာလဲ။"
                ),
                "1" to FortuneAnswer(
                    en = "For now, success may not come yet – but it will surely come. You have the destiny for it.",
                    mm = "အခုလောလောဆယ်တော့ အောင်မြင်ဖို့မရှိသေးဘူး။ ဒါပေမဲ့မုချအောင်မြင်မှာပါ။ အောင်မြင်ဖို့ ပါရမီကိုရှိတယ်။"
                ),
                "2" to FortuneAnswer(
                    en = "Success is certain.",
                    mm = "အောင်မြင်ဖို့သေချာပါတယ်"
                ),
                "3" to FortuneAnswer(
                    en = "You have the virtue and destiny for success.",
                    mm = "အောင်မြင်ဖို့ပါရမီရှိပါတယ်\""
                ),
                "4" to FortuneAnswer(
                    en = "It will not be a small success – it will be a huge one.",
                    mm = "နဲနဲနောနော အောင်မြင်မှာမဟုတ်ဘူးနော်။ အရမ်းကိုအောင်မြင်မှာပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "Success is certain. Whether you believe it or not, dedicate your work to a female deity.",
                    mm = "မုချအောင်မြင်မယ်။ ဘာပဲဖြစ်ဖြစ် ယုံတာ မယုံတာ အပထားပြီး မယ်တော်တစ်ပါးပါးကို တိုင်တည်ပြီးအလုပ်လုပ်ပါ။"
                ),
                "6" to FortuneAnswer(
                    en = "You will need to put in a lot of effort to succeed.",
                    mm = "အောင်မြင်ဖို့အတော်.ကို ကြိုးစားရမယ်ဗျို"
                ),
                "7" to FortuneAnswer(
                    en = "With perseverance, success will come.",
                    mm = "ဇွဲနဲ့ကြိုးစားရင်အောင်မြင်မှာပါ"
                ),
                "8" to FortuneAnswer(
                    en = "It will be a success – you cannot escape it, because success will follow you.",
                    mm = "အောင်မြင်မယ်ဗျာ။ပြေးလို့ကို မလွတ်ဘူး။ အောင်မြင်မှုက ခင်ဗျားနောက်ကို လိုက်လာမှာပါ။"
                ),
                "9" to FortuneAnswer(
                    en = "More and more successes will keep coming.",
                    mm = "အောင်မြင်မှုတွေကဆက်တိုက်ဖြစ်လာမှာ"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q57",
            en = "Will I become famous or gain recognition?",
            mm = "မိမိ၌ ကျော်ဇောကိတ္တိကံပါ / မပါနှင့် ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "The flow may stop just short of fame – try again and again. Don't give up easily.",
                    mm = "ကျော်ကြားမယ်လုပ်လိုက် အချီကပြတ်သွားလိုက်။ ပြန်ပြီးကြိုးစားလိုက်။ အကြိမ်ကြိမ် အထပ်ထပ်လုပ်ရမယ်နော်။ စိတ်ဓာတ်ကျလို့တော့မရဘူး"
                ),
                "1" to FortuneAnswer(
                    en = "Yes – you are about to become famous.",
                    mm = "မုချကျော်ကြားတော့မယ့်သူပါဗျာ"
                ),
                "2" to FortuneAnswer(
                    en = "Fame is certain for you.",
                    mm = "ကျော်ဇောခြင်းဟာသင့်အဖို့ သေချာပါတယ်။"
                ),
                "3" to FortuneAnswer(
                    en = "You will be famous in two areas.",
                    mm = "အကြောင်းအရာနှစ်ခုမှာ ကျော်ဇောလိမ့်မယ်"
                ),
                "4" to FortuneAnswer(
                    en = "Your fame will spread widely.",
                    mm = "ကျော်ဇောတဲ့အတိုင်းအတာဟာ ကျယ်ကျယ်ပြန့်ပြန့်ဖြစ်လိမ့်မယ်နော်"
                ),
                "5" to FortuneAnswer(
                    en = "Whether you believe it or not, dedicate your work to a female deity – fame will come.",
                    mm = "ကိုယ်ယုံတာ မယုံတာကိုအပထား။ ကိုယ်လုပ်တဲ့အလုပ်မှာ ကျော်ဇောဖို့ မယ်တော်တပါးပါးကို တိုင်တည်ပြီးလုပ်ပါ။"
                ),
                "6" to FortuneAnswer(
                    en = "You will surely become famous.",
                    mm = "မုချကျော်ကြားမှာပါ"
                ),
                "7" to FortuneAnswer(
                    en = "You will need to put in a lot of effort.",
                    mm = "အတော်.ကိုကြိုးစားရမယ်ဗျို့"
                ),
                "8" to FortuneAnswer(
                    en = "You will need to try extremely hard – but the destiny for fame is there.",
                    mm = "မတရားကြီး ကြိုးစားရမယ်နော်။ ကျော်ဇောဖို့ကံတော့ ပါပါတယ်။"
                ),
                "9" to FortuneAnswer(
                    en = "You need to work in a competitive way – then fame will come.",
                    mm = "အပြိုင်အဆိုင်နှင့်ကြိုးစားရမှာနော်။ အဲဒါဆိုရင် ကျော်ကြားပါပြီ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q58",
            en = "Will the person I help turn out to be ungrateful later?",
            mm = "မိမိသည် ဒုက္ခရောက်နေသူကိုကယ်တင်လိုသည်။ ထိုသူသည်မိမိကိုကျေးဇူးကန်းမည်လော / မကန်းမည်လောနှင့်ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Go ahead and help – they will not be ungrateful.",
                    mm = "ကယ်မှာ ကယ်လိုက်ပါဗျာ။ ကျေးဇူးကန်းမှာ မဟုတ်ဘူး။"
                ),
                "1" to FortuneAnswer(
                    en = "No, they will not be ungrateful – just help them as you should.",
                    mm = "မကန်းပါဘူးဗျာ။ ကယ်စရာရှိတာ ကယ်လိုက်စမ်းပါ"
                ),
                "2" to FortuneAnswer(
                    en = "It is worth helping – they will not be ungrateful.",
                    mm = "ကယ်ကောင်းပါတယ်ဗျာ။ ကျေးဇူးမကန်းပါဘူး။"
                ),
                "3" to FortuneAnswer(
                    en = "Don't do it – it will end in loss for you.",
                    mm = "သွားမလုပ်နဲ့။ နွားပါပြုတ်တဲ့ကိန်းဆိုက်လိမ့်မယ်။"
                ),
                "4" to FortuneAnswer(
                    en = "Be sure to help them – they are a grateful person.",
                    mm = "ကယ်ဖြစ်အောင် ကယ်လိုက်စမ်းပါဗျာ။ ကျေးဇူးသိတတ်တဲ့သူပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "Be sure to help them – ungratefulness is not easy for them.",
                    mm = "ကယ်ဖြစ်အောင် ကယ်လိုက်စမ်းပါဗျာ။ ကျေးဇူးကန်းတယ်ဆိုတာ လွယ်တဲ့အလုပ်မဟုတ်ဘူးဗျ။"
                ),
                "6" to FortuneAnswer(
                    en = "Don't worry about gratitude – just help them. They will not be ungrateful.",
                    mm = "သူကကျေးဇူး သိတတ်တာ မသိတတ်တာ မစဉ်းစားနဲ့။ ကိုယ်က ကယ်ဖြစ်အောင် ကယ်လိုက်စမ်းပါ။ ကျေးဇူးလည်းမကန်း ပါဘူး။"
                ),
                "7" to FortuneAnswer(
                    en = "Be sure to help them – they truly know how to be grateful.",
                    mm = "ကယ်ဖြစ်အောင် ကယ်လိုက်ပါ။ အတော့်ကို ကျေးဇူးသိတတ်တဲ့လူပါ။"
                ),
                "8" to FortuneAnswer(
                    en = "They will not be ungrateful – just go ahead and help.",
                    mm = "ကျေးဇူးမကန်းပါဘူး။ ကယ်လိုက်ပါ။"
                ),
                "9" to FortuneAnswer(
                    en = "Just focus on helping – it is not your job to judge their gratitude. And they will not be ungrateful.",
                    mm = "ကိုယ်က ကယ်ဖို့ပဲစဉ်းစားပါ။ သူ ကျေးဇူးကန်းတာ မကန်းတာ ကိုယ်.အလုပ်လဲမဟုတ်ဘူး။ ကျေးဇူးမကန်းပါဘူး ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q59",
            en = "Is this thing genuine or fake?",
            mm = "မိမိသည် တစ်စုံတစ်ခုသော ပစ္စည်းကို အတုဖြစ်သည် /အစစ်ဖြစ်သည်ကို ဝေခွဲရန် ခက်နေ၏။ ထိုအကြောင်းနှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "There are signs that make it seem fake – but it is actually real.",
                    mm = "အတုလို့ထင်စရာ ချက်လေးတွေတွေ့တယ်။ ဒါပေမယ့် အစစ်ပါဗျာ"
                ),
                "1" to FortuneAnswer(
                    en = "It is fake.",
                    mm = "အတု"
                ),
                "2" to FortuneAnswer(
                    en = "It cannot be determined right now – wait a while.",
                    mm = "လောလောဆယ်တော့ပြောလို့မရသေးဘူး။ အချိန်စောင့်လိုက်ပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "It is truly real – real, real, real.",
                    mm = "အစစ်မှ အစစ်ဗျာ။ စစ်...စစ်...စစ်..လို့တောင် မည်နေသေး။"
                ),
                "4" to FortuneAnswer(
                    en = "It is most likely fake.",
                    mm = "အတုဖြစ်ဖို့များပါတယ်"
                ),
                "5" to FortuneAnswer(
                    en = "It is most likely fake.",
                    mm = "အတုဖြစ်ဖို့ကများပါတယ်ဗျ"
                ),
                "6" to FortuneAnswer(
                    en = "It cannot be identified right now – wait a while.",
                    mm = "လောလောဆယ် ဝေခွဲလို့မရသေးပါဘူး။ အချိန်စောင့်လိုက်ပါဦး"
                ),
                "7" to FortuneAnswer(
                    en = "It is fake.",
                    mm = "အတုပါဗျာ"
                ),
                "8" to FortuneAnswer(
                    en = "It is real – a genuine one.",
                    mm = "အစစ်ဗျို့ ..အစစ်"
                ),
                "9" to FortuneAnswer(
                    en = "It is fake.",
                    mm = "အတုပါဗျာ"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q60",
            en = "How will I perform in my exam or interview?",
            mm = "စာမေးပွဲဖြေခြင်း /အင်တာဗျူးဝင်ခြင်းနှင့် ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "If someone like you can't pass, then who will?",
                    mm = "သင့်လိုလူမှမအောင်ရင် ဘယ်သူအောင်မှာလဲ"
                ),
                "1" to FortuneAnswer(
                    en = "You will need to work hard, but you will pass.",
                    mm = "အကြိတ်အနယ် ဖြေရလိမ့်မယ်။ အောင်တော့အောင်မှာပါ။"
                ),
                "2" to FortuneAnswer(
                    en = "No matter how hard you try or how smart you are – luck is not on your side.",
                    mm = "ဘယ်လောက်ပဲကြိုးစားကြိုးစား ဘယ်လောက်ပဲတော်တော်၊ ကံ,ကမပါဘူးလေ။"
                ),
                "3" to FortuneAnswer(
                    en = "You could pass it even in your sleep.",
                    mm = "အိပ်ပြီးဖြေတာတောင် အောင်ပါတယ်ဗျာ"
                ),
                "4" to FortuneAnswer(
                    en = "Don't just aim for a pass – you could even get top marks.",
                    mm = "အောင်ရုံလောက်အတွက်နဲ့တော့ မမေးပါနဲ့ဗျား။ ဂုဏ်ထူးတောင် ထွက်နိုင်ပါတယ်ဗျာ။"
                ),
                "5" to FortuneAnswer(
                    en = "You will pass with many distinctions.",
                    mm = "ဂုဏ်ထူးတွေ တသီကြီးနဲ့အောင်မှာပါ"
                ),
                "6" to FortuneAnswer(
                    en = "You will pass with flying colors.",
                    mm = "ထူးထူးချွန်ချွန်အောင်လိမ့်မယ်ဗျို့။"
                ),
                "7" to FortuneAnswer(
                    en = "Yes, you will pass.",
                    mm = "အောင်မှာပါဗျာ"
                ),
                "8" to FortuneAnswer(
                    en = "You will get some distinctions.",
                    mm = "ဂုဏ်ထူးထွက်လိမ့်မယ်"
                ),
                "9" to FortuneAnswer(
                    en = "Be happy – you have passed!",
                    mm = "ပျော်ပေတော့ ပျော်ပေတော့။ အောင်ပြီ....အောင်ပြီ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q61",
            en = "Will I be able to meet the person I want to see soon?",
            mm = "မိမိတွေ့လိုသောသူနဲ့ တွေ့ခွင့်ရနိုင် /မရနိုင်နဲ့ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "You will meet them – don't worry.",
                    mm = "တွေ့မှာပါဗျာ။ မပူပါနဲ့။"
                ),
                "1" to FortuneAnswer(
                    en = "You will not be able to meet them at this time.",
                    mm = "လောလောဆယ် တွေ့ခွင့်ရအုံးမှာမဟုတ်ဘူး"
                ),
                "2" to FortuneAnswer(
                    en = "You will surely meet them.",
                    mm = "မုချတွေ့ခွင့်ရမှာပါ"
                ),
                "3" to FortuneAnswer(
                    en = "Don't worry – you will meet them.",
                    mm = "မတွေ့မှာ မပူပါနဲ့ဗျာ"
                ),
                "4" to FortuneAnswer(
                    en = "You will meet them, get what you ask for, and your wish will come true.",
                    mm = "တွေ့လည်းတွေ့မယ်။ တောင်းတဲ့အကူအညီလည်းရမယ်။ ကိုယ်ဖြစ်ချင်တာလည်း ဖြစ်ရလိမ့်မယ်။"
                ),
                "5" to FortuneAnswer(
                    en = "Just consider that you will meet them.",
                    mm = "တွေ့ရပြီလို့မှတ်ပေတော့"
                ),
                "6" to FortuneAnswer(
                    en = "This is not a good time to meet – it's not possible right now.",
                    mm = "ဒီအချိန်မတွေ့နဲ့ဦးဗျ။ မကောင်းသေးဘူး။ တွေ့မှာလည်းမဟုတ်ဘူး။"
                ),
                "7" to FortuneAnswer(
                    en = "You will certainly meet them.",
                    mm = "တွေ့မှာသေချာပါတယ်ဗျာ"
                ),
                "8" to FortuneAnswer(
                    en = "You are trying to meet them? That person also wants to meet you.",
                    mm = "ခင်ဗျားကတွေ့ဖို့ကြိုးစားနေတာလား။ သူကလည်း တွေ့ချင်ပါသတဲ့ဗျာ။"
                ),
                "9" to FortuneAnswer(
                    en = "Don't try to meet them at this time.",
                    mm = "အခုအချိန်မှာ တွေ့ဖို့မကြိုးစားပါနဲ့ဦး။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q62",
            en = "Will my plan or idea be successful?",
            mm = "မိမိ အကြံအစည်တခုရှိနေ၏။ ထိုအကြံအစည် အောင်မြင်ခြင်းရှိ /မရှိနဲ့ဆိုင်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Just take it as already successful – don't worry.",
                    mm = "အောင်ပြီလို့ သဘောထားပေတော့။ ဘာမှမပူပါနဲ့။"
                ),
                "1" to FortuneAnswer(
                    en = "Your plan will succeed. Bathe in water taken from two ponds or lakes – it will make it even more certain.",
                    mm = "အကြံအစည်အောင်မှာပါဗျာ။ ရေကန်နှစ်ကန်ကရေကိုခပ်ပြီး ရေချိူးလိုက်စမ်းပါ။ ပိုပြီးသေချာသွားတာပေါ့။"
                ),
                "2" to FortuneAnswer(
                    en = "Make a strong resolution and do good deeds for nine days – success will come.",
                    mm = "အဓိဌာန် ကိုးရက်ဝင်လိုက်ပါ။ အောင်မြင်သွားမှာပါ။"
                ),
                "3" to FortuneAnswer(
                    en = "Move your bed to a new position – it will bring success.",
                    mm = "အိပ်ရာရွှေအိပ်လိုက်ပါ၊၊ အောင်မြင်သွားပါလိမ့်မယ်။"
                ),
                "4" to FortuneAnswer(
                    en = "Success is certain – I can already hear the sound of success!",
                    mm = "အောင်မှာသေချာပါတယ်။ အောင် ...အောင်..လို့တောင်မည်နေပါပြီ။"
                ),
                "5" to FortuneAnswer(
                    en = "It is sure to succeed.",
                    mm = "အောင်မှာသေချာပါတယ်"
                ),
                "6" to FortuneAnswer(
                    en = "Just consider it already a success.",
                    mm = "အောင်ပြီလို့မှတ်ပေတော့"
                ),
                "7" to FortuneAnswer(
                    en = "Invite a monk to your home and offer alms.",
                    mm = "ဘုန်းကြီးပင့်ပီး ဆွမ်းကပ်လိုက်ပါဗျာ"
                ),
                "8" to FortuneAnswer(
                    en = "Just consider it a success – it cannot fail now.",
                    mm = "အောင်မြင်ပြီလို့မှတ်ပေတော့။ မလွဲတော့ဘူး။"
                ),
                "9" to FortuneAnswer(
                    en = "Do your work with love and kindness – success is certain.",
                    mm = "မေတ္တာပို.ပြီး အလုပ်လုပ်ပါဗျာ။ အောင်မှာသေချာပါတယ်"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q63",
            en = "Will my major project or public building construction succeed?",
            mm = "အများနှင့်ဆိုင်သော အဆောက်အဦကြီး /စီမံချက်ကြီးတစ်ခုပြုလိုလို၏။ ထိုကိစ္စနှင့်ပတ်သက်၍ အထမြောက်နိုင်ခြင်းရှိ /မရှိနှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Success is already there – stop wasting time asking and just do it.",
                    mm = "အောင်ပါပြီဗျာ။ အချိန်ကုန်ခံပြီး ခင်ဗျားတို့မေးနေတယ်။ လုပ်လိုက်ပေတော့။"
                ),
                "1" to FortuneAnswer(
                    en = "It will surely succeed.",
                    mm = "ကျိန်းသေ အထမြောက်ပါမယ်ဗျာ"
                ),
                "2" to FortuneAnswer(
                    en = "Dress as a yogi and make a strong resolution for nine days – then start your work. It will surely succeed.",
                    mm = "ယောဂီဝတ်နဲ့ ကိုးရက်လောက် အဓိဋ္ဌာန်ဝင်လိုက်ပါ။ ပြီးမှ အဲဒီအလုပ်ကို လုပ်ပါ။ မုချအောင်မြင်မယ်ဗျာ။"
                ),
                "3" to FortuneAnswer(
                    en = "Move your bed or change where you sleep – then start your work. It will surely succeed.",
                    mm = "အိပ်ရာရွှေပြီးအိပ်မှ အဲဒီအလုပ်ကို လုပ်ပါ။ မုချအောင်မြင်မယ်ဗျာ။"
                ),
                "4" to FortuneAnswer(
                    en = "It will succeed – just make sure you go ahead and do it.",
                    mm = "အောင်မြင်မှာပါဗျာ။ လုပ်ဖြစ်အောင်သာ လုပ်လိုက်ပါ"
                ),
                "5" to FortuneAnswer(
                    en = "Success is already certain – why are you even asking?",
                    mm = "အောင်မြင်ဖို့သေချာနေပါပြီ။ ဘာဖြစ်လို့မေးနေတာလဲ။"
                ),
                "6" to FortuneAnswer(
                    en = "100% success – don't worry.",
                    mm = "ရာနှုန်းအပြည့် အောင်မြင်လိမ့်မယ်။ ပူကိုမပူပါနဲ့။"
                ),
                "7" to FortuneAnswer(
                    en = "Offer alms to a monk first – then carry out your plan. Success is very likely.",
                    mm = "ဘုန်းတော်ကြီးတပါးကိုဆွမ်းကပ်ပြီးမှလုပ်ပါ။ အောင်မြင်ဖို့များပါတယ်။"
                ),
                "8" to FortuneAnswer(
                    en = "You are the one meant to do this – if someone else does it, it will fail.",
                    mm = "ခင်ဗျားလုပ်မှကို အောင်မြင်မှာနော်။ တစ်ခြားလူလုပ်ရင် အဲတာကြီးပျက်သွားလိမ့်မယ်။"
                ),
                "9" to FortuneAnswer(
                    en = "Do it while reciting loving-kindness verses – success is certain. It is as if the project is waiting for you.",
                    mm = "မေတ္တာသုတ်ရွတ်ပြီး လုပ်စမ်းပါဗျာ။ ဒောာင်မြင်ပါတယ်။ သင်လုပ်ဖို့ကို စောင့်နေတဲ့အတိုင်းပါပဲ။"
                ),
            )
        ),
        FortuneQuestion(
            id = "Q64",
            en = "What is the advice for starting a business, mounting a signboard, moving house, or launching a venture?",
            mm = "လုပ်ငန်းစခြင်း၊ ဆိုင်းဘုတ်တင်ခြင်း၊ အိမ်တက်ခြင်း ၊ သင်္ဘောရေချခြင်း၊ ပန္နက်ရိုက်ခြင်းနှင့်ပတ်သက်သော အဟော",
            answers = mapOf(
                "0" to FortuneAnswer(
                    en = "Offer nine cups of drinking water at the shrine, then start your work – everything will go smoothly.",
                    mm = "ဘုရားစင်မှာ သောက်တော်ရေ (၉) ခွက် လှူပြီး သင့်ရဲ့လုပ်ငန်းကို စ,လိုက်ပါ။ အားလုံးအဆင်ပြေသွားပါလိမ့်မယ်။"
                ),
                "1" to FortuneAnswer(
                    en = "Just start – keep a long knife placed beside you.",
                    mm = "စပေတော့။ အနားမှာ ဓါးရှည်တချောင်း ထောင်ထားဖို့လိုတယ်။"
                ),
                "2" to FortuneAnswer(
                    en = "Just start – keep a straight tree branch about 5–6 feet long beside you.",
                    mm = "စပေတော့။ အနားမှာ လူတစ်ရပ်လောက်ရှိတဲ့ သစ်ကိုင်းဖြောင့်ဖြောင့်တစ်ချောင်း ထောင်ထားဖို့လိုတယ်"
                ),
                "3" to FortuneAnswer(
                    en = "Don't start now – wait until the 9th day, counting from today.",
                    mm = "ဒီအချိန် မစ,ပါနဲ့အုံး။ ဒီကနေ (၈)ရက်ပြည့်ပြီ (၉) ရက်မြောက်တဲ့နေ့မှာ စလိုက်ပါ။"
                ),
                "4" to FortuneAnswer(
                    en = "Start with nine yellow flowers placed in a pot beside you.",
                    mm = "ပန်းအဝါ (၉) ပွင့် အိုးထဲမှာထိုးပြီး အနားမှာထား၍ စလိုက်ပါ။"
                ),
                "5" to FortuneAnswer(
                    en = "Don't start now – wait until after 18 days from today for good timing.",
                    mm = "ဒီအချိန် မစ,ပါနဲ့အုံး။ ဒီကနေ 18 ရက်ကျော်မှ အခါကောင်းမိမယ်။ စောင့်လိုက်ပါ"
                ),
                "6" to FortuneAnswer(
                    en = "Wait for two more days from this morning – then the timing will be right.",
                    mm = "ဒီနေ့မနက်ပဲ နှစ်ရက်တိတိစောင့်လိုက်ပါ။ အဲဒီကျမှ အခါကောင်းတွေ့လိမ့်မယ်"
                ),
                "7" to FortuneAnswer(
                    en = "Don't start yet – someone from your group will take a trip. Start when that happens.",
                    mm = "လောလောဆယ် မစ,နဲ့အုံး။ သင်တို့အုပ်စု သင်တို့အပေါင်းအသင်းထဲက တစ်ယောက်ယောက်ခရီးထွက်လိမ့်မယ်။ အဲဒီကျမှ စ,လိုက်ပါ။"
                ),
                "8" to FortuneAnswer(
                    en = "The timing is good now – start at 4 o'clock in the morning. It's better to burn four incense sticks when you start.",
                    mm = "အခု အခါမိနေပြီ။ မနက်ဖြန်မနက် 4 နာရီမှာ စလိုက်ပေတော့ ။ အမွှေးတိုင် 4 တိုင် ထွန်းပြီး စ ရင် ပိုကောင်းပါတယ်။"
                ),
                "9" to FortuneAnswer(
                    en = "Just start – keep a table clock or wall clock beside you when you begin.",
                    mm = "စ,ပေတော့။ စတဲ့အခါမှာ စားပွဲတင်နာရီ ဒါမှမဟုတ် နံရံကပ်နာရီ အဝိုင်းတစ်ခု အနားမှာထားပြီး စ,လိုက်ပါ။"
                ),
            )
        )
    )
    val questions: List<FortuneQuestion>
        get() = fortuneData

    fun getQuestionById(id: String): FortuneQuestion? {
        return fortuneData.find { it.id == id }
    }
}
