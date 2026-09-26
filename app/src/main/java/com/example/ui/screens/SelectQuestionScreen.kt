package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FortuneDataProvider
import com.example.data.FortuneQuestion
import com.example.model.AccessTier
import com.example.ui.model.QuestionCategory
import com.example.ui.model.QuestionCategoryMapper
import com.example.ui.theme.MysticBorderMuted
import com.example.ui.theme.MysticGold
import com.example.ui.theme.MysticGoldBright
import com.example.ui.theme.MysticPrimaryPurple
import com.example.ui.theme.MysticSurface
import com.example.ui.theme.MysticTextPrimary
import com.example.ui.theme.MysticTextSecondary
import com.example.util.HapticHelper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SelectQuestionScreen(
    currentLang: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    freeReadingsRemaining: Int,
    prototypeReadingsRemaining: Int = 0,
    accessTier: AccessTier,
    isQuotaExhaustedDialogVisible: Boolean,
    onDismissQuotaDialog: () -> Unit,
    onConnectWalletForWeb3: () -> Unit,
    onQuestionHoldConfirmed: (String) -> Unit,
    onBackToHome: () -> Unit,
    connectedWallet: String? = null,
    onDisconnectWallet: (() -> Unit)? = null,
    pendingQuestionId: String? = null,
    onSelectQuestionForConfirmation: (String) -> Unit = {},
    onDismissQuestionConfirmation: () -> Unit = {},
    onConfirmQuestion: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isBurmese = currentLang == "mm"
    val allQuestions = remember { FortuneDataProvider.fortuneData }
    var selectedCategory by rememberSaveable { mutableStateOf(QuestionCategory.ALL) }

    // Search filter: If user searches "7", it matches both "7" and "၇" and text
    val normalizedQuery = remember(searchQuery) {
        FortuneDataProvider.normalizeSearch(searchQuery)
    }

    val filteredQuestions = remember(normalizedQuery, selectedCategory, allQuestions) {
        allQuestions.filter { q ->
            val matchesCategory = QuestionCategoryMapper.matchesCategory(q.id, selectedCategory)
            if (!matchesCategory) return@filter false

            if (normalizedQuery.isEmpty()) {
                true
            } else {
                val normEn = FortuneDataProvider.normalizeSearch(q.en)
                val normMm = FortuneDataProvider.normalizeSearch(q.mm)
                val normId = FortuneDataProvider.normalizeSearch(q.id)
                normEn.contains(normalizedQuery) || normMm.contains(normalizedQuery) || normId.contains(normalizedQuery)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("select_question_screen")
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToHome,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MysticSurface)
                    .border(1.dp, MysticBorderMuted, CircleShape)
                    .testTag("back_to_home_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MysticPrimaryPurple,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isBurmese) "မေးခွန်း ရွေးချယ်ပါ" else "Select Your Question",
                    color = MysticTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isBurmese) "မေးခွန်းပေါင်း ၆၄ ခုအနက်မှ ရွေးပါ" else "Choose from 64 sacred queries",
                    color = MysticTextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Daily Quota Indicator Badge
        QuotaIndicatorBadge(
            currentLang = currentLang,
            freeReadingsRemaining = freeReadingsRemaining,
            accessTier = accessTier
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (connectedWallet != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1B2E))
                    .border(1.dp, MysticGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Solana: ${connectedWallet.take(6)}...${connectedWallet.takeLast(4)}",
                        color = MysticGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (onDisconnectWallet != null) {
                    Text(
                        text = if (isBurmese) "ထွက်မည်" else "Disconnect",
                        color = MysticTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onDisconnectWallet() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("question_search_bar"),
            placeholder = {
                Text(
                    text = if (isBurmese) "ရှာဖွေရန် (ဥပမာ- 7 သို့မဟုတ် မေးခွန်း)..." else "Search question or number (e.g. 7)...",
                    color = MysticTextSecondary.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MysticPrimaryPurple
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = MysticTextSecondary
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MysticSurface,
                unfocusedContainerColor = MysticSurface,
                focusedBorderColor = MysticPrimaryPurple,
                unfocusedBorderColor = MysticBorderMuted,
                focusedTextColor = MysticTextPrimary,
                unfocusedTextColor = MysticTextPrimary,
                cursorColor = MysticGold
            )
        )

        // Category Chips Row
        CategoryChipsRow(
            selectedCategory = selectedCategory,
            onCategorySelected = { category -> selectedCategory = category },
            isBurmese = isBurmese,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Question List with tap-to-select interaction
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("question_list"),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredQuestions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                            .testTag("empty_questions_state"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBurmese) "မေးခွန်း ရှာမတွေ့ပါ" else "No matching questions found",
                            color = MysticTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(filteredQuestions, key = { it.id }) { question ->
                    TapToSelectQuestionCardItem(
                        question = question,
                        isBurmese = isBurmese,
                        onSelected = { onSelectQuestionForConfirmation(question.id) }
                    )
                }
            }
        }
    }

    // Confirmation Dialog before starting reading
    val pendingQuestion = remember(pendingQuestionId) {
        if (pendingQuestionId != null) FortuneDataProvider.getQuestionById(pendingQuestionId) else null
    }

    if (pendingQuestion != null) {
        ConfirmQuestionDialog(
            question = pendingQuestion,
            isBurmese = isBurmese,
            onConfirm = onConfirmQuestion,
            onDismiss = onDismissQuestionConfirmation
        )
    }

    // Quota Exhaustions Dialog / Bottom Sheet
    if (isQuotaExhaustedDialogVisible) {
        val isWalletRequiredState = prototypeReadingsRemaining > 0
        AlertDialog(
            onDismissRequest = onDismissQuotaDialog,
            containerColor = MysticSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = if (isWalletRequiredState) {
                        if (isBurmese) "နေ့စဉ် အခမဲ့ကန့်သတ်ချက် ပြည့်သွားပါပြီ" else "Daily Free Limit Reached"
                    } else {
                        if (isBurmese) "နေ့စဉ် ကြည့်ရှုခွင့် ပြည့်သွားပါပြီ" else "Daily Reading Limit Reached"
                    },
                    color = MysticGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isWalletRequiredState) {
                        if (isBurmese) "ယနေ့အတွက် အခမဲ့ဟောကိန်း (၂) ကြိမ် ပြည့်သွားပါပြီ။ ဆက်လက်ကြည့်ရှုရန် ဝေါလက် ချိတ်ဆက်ပါ သို့မဟုတ် Seeker Elite ကို အသုံးပြုပါ။"
                        else "You've used your 2 free readings for today.\nConnect Wallet to Continue or Unlock Seeker Elite."
                    } else {
                        if (isBurmese) "ယနေ့အတွက် ကြည့်ရှုခွင့် (၅) ကြိမ် စလုံး ပြည့်သွားပါပြီ။\n(အခမဲ့ ၂ ကြိမ် + ဝေါလက် ၃ ကြိမ်)"
                        else "You have used all 5 readings available today\n(2 free readings + 3 wallet-enabled prototype readings)."
                    },
                    color = MysticTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                if (isWalletRequiredState) {
                    Button(
                        onClick = onConnectWalletForWeb3,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCBA6FF),
                            contentColor = Color(0xFF2B203B)
                        ),
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                        modifier = Modifier.testTag("quota_dialog_connect_wallet_button")
                    ) {
                        Text(
                            text = if (isBurmese) "ဝေါလက် ချိတ်ဆက်မည်" else "Connect Wallet",
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onDismissQuotaDialog,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MysticTextSecondary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder().copy(
                            brush = SolidColor(MysticBorderMuted)
                        ),
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                        modifier = Modifier.testTag("quota_dialog_cancel_button")
                    ) {
                        Text(text = if (isBurmese) "ပယ်ဖျက်မည်" else "Cancel")
                    }
                }
            },
            dismissButton = {
                if (isWalletRequiredState) {
                    OutlinedButton(
                        onClick = onDismissQuotaDialog,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MysticTextSecondary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder().copy(
                            brush = SolidColor(MysticBorderMuted)
                        ),
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                        modifier = Modifier.testTag("quota_dialog_cancel_button")
                    ) {
                        Text(text = if (isBurmese) "ပယ်ဖျက်မည်" else "Cancel")
                    }
                }
            }
        )
    }
}

@Composable
fun CategoryChipsRow(
    selectedCategory: QuestionCategory,
    onCategorySelected: (QuestionCategory) -> Unit,
    isBurmese: Boolean,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_chips_row"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(QuestionCategory.entries.toTypedArray(), key = { it.name }) { category ->
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) MysticPrimaryPurple.copy(alpha = 0.25f)
                        else MysticSurface
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) MysticGold else MysticBorderMuted.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("category_chip_${category.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.getLabel(isBurmese),
                    color = if (isSelected) MysticGold else MysticTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun QuotaIndicatorBadge(
    currentLang: String,
    freeReadingsRemaining: Int,
    accessTier: AccessTier,
    modifier: Modifier = Modifier
) {
    val isBurmese = currentLang == "mm"
    val isElite = accessTier == AccessTier.SEEKER_ELITE

    val badgeText = if (isElite) {
        if (isBurmese) "Seeker Elite • အကန့်အသတ်မရှိ ဟောကိန်းများ" else "Seeker Elite • Unlimited Readings"
    } else {
        if (isBurmese) "ယနေ့ အခမဲ့ဟောကိန်း ကျန်ရှိမှု: $freeReadingsRemaining/၂" else "Free Readings Left Today: $freeReadingsRemaining/2"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF231E2E))
            .border(1.dp, MysticGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("quota_indicator_badge"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🔮 $badgeText",
            color = MysticGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TapToSelectQuestionCardItem(
    question: FortuneQuestion,
    isBurmese: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 88.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MysticSurface)
            .border(
                1.dp,
                Color(0xFF4A4654),
                RoundedCornerShape(14.dp)
            )
            .clickable {
                HapticHelper.playClickHaptic(context)
                onSelected()
            }
            .padding(horizontal = 14.dp, vertical = 14.dp)
            .testTag("question_item_${question.id}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MysticPrimaryPurple.copy(alpha = 0.12f))
                    .border(1.dp, MysticPrimaryPurple.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = question.id.replace("Q", ""),
                    color = MysticGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isBurmese) question.mm else question.en,
                    color = MysticTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isBurmese) "ရွေးချယ်ရန် နှိပ်ပါ" else "Tap to select query",
                    color = MysticTextSecondary.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun ConfirmQuestionDialog(
    question: FortuneQuestion,
    isBurmese: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MysticSurface,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier.testTag("confirm_question_dialog"),
        title = {
            Text(
                text = if (isBurmese) "မေးခွန်းကို အတည်ပြုပါ" else "Confirm Your Question",
                color = MysticGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("confirm_question_title")
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF231E2E))
                        .border(1.dp, MysticGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    val questionText = if (isBurmese) question.mm else question.en
                    Text(
                        text = "“$questionText”",
                        color = MysticTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 22.sp,
                        modifier = Modifier.testTag("confirm_question_text")
                    )
                }

                Text(
                    text = if (isBurmese) "ဤမေးခွန်းသည် သင်မေးမြန်းလိုသော မေးခွန်းဟုတ်မဟုတ် သေချာပါစေ။"
                           else "Please make sure this is the question you want to ask.",
                    color = MysticTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    modifier = Modifier.testTag("confirm_question_supporting_text")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFCBA6FF),
                    contentColor = Color(0xFF2B203B)
                ),
                shape = RoundedCornerShape(100.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                modifier = Modifier.testTag("confirm_question_yes_button")
            ) {
                Text(
                    text = if (isBurmese) "ဟုတ်ပါပြီ၊ ဆက်လုပ်မည်" else "YES, PROCEED",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MysticTextSecondary
                ),
                border = ButtonDefaults.outlinedButtonBorder().copy(
                    brush = SolidColor(MysticBorderMuted)
                ),
                shape = RoundedCornerShape(100.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                modifier = Modifier.testTag("confirm_question_no_button")
            ) {
                Text(
                    text = if (isBurmese) "မလုပ်ပါ။ မေးခွန်းပြောင်းမည်" else "NO, CHANGE QUESTION"
                )
            }
        }
    )
}
