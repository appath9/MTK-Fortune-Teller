package com.example.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.FortuneDataProvider
import com.example.data.FortunePreferences
import com.example.data.FortuneRepository
import com.example.data.room.FortuneRecordEntity
import com.example.model.AccessTier
import com.example.model.AppScreen
import com.example.model.FortuneState
import com.example.model.INTRO_STEPS
import com.example.model.SealingState
import com.example.util.SignatureConfirmationStatus
import com.example.util.SolanaWalletManager
import com.example.util.TransactionFailureOutcome
import com.example.util.classifyTransactionFailure
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.milliseconds

class FortuneViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = FortunePreferences(application)
    private val repository = FortuneRepository(application)

    val sealedHistoryRecords: StateFlow<List<FortuneRecordEntity>> = repository.allRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun navigateToHistory() {
        val current = _uiState.value.currentScreen
        _uiState.update { it.copy(previousScreen = current, currentScreen = AppScreen.History, isDrawerOpen = false) }
    }

    fun getEffectiveAccessTier(): AccessTier {
        return if (BuildConfig.DEBUG && preferences.isMockSeekerEliteEnabled) {
            AccessTier.SEEKER_ELITE
        } else {
            AccessTier.FREE
        }
    }

    private val _uiState = MutableStateFlow(
        FortuneState(
            currentScreen = AppScreen.Home,
            currentLang = preferences.currentLang,
            lastIntroDate = null,
            accessTier = getEffectiveAccessTier(),
            isMockSeekerElite = preferences.isMockSeekerEliteEnabled,
            freeReadingsRemaining = if (getEffectiveAccessTier() == AccessTier.SEEKER_ELITE) 999 else preferences.getFreeReadingsRemaining(),
            prototypeReadingsRemaining = if (getEffectiveAccessTier() == AccessTier.SEEKER_ELITE) 999 else preferences.getPrototypeReadingsRemaining()
        )
    )
    val uiState: StateFlow<FortuneState> = _uiState.asStateFlow()

    private val _connectedWallet = MutableStateFlow<String?>(null)
    val connectedWallet: StateFlow<String?> = _connectedWallet.asStateFlow()

    private val activeAttestationReconciliations = Collections.newSetFromMap(ConcurrentHashMap<String, Boolean>())
    private val activeOfferingReconciliations = Collections.newSetFromMap(ConcurrentHashMap<String, Boolean>())

    private var currentSealingJob: Job? = null

    fun toggleMockSeekerElite() {
        if (!BuildConfig.DEBUG) return
        val newMock = !preferences.isMockSeekerEliteEnabled
        preferences.isMockSeekerEliteEnabled = newMock
        val tier = getEffectiveAccessTier()
        _uiState.update {
            it.copy(
                accessTier = tier,
                isMockSeekerElite = newMock,
                freeReadingsRemaining = if (tier == AccessTier.SEEKER_ELITE) 999 else preferences.getFreeReadingsRemaining(),
                prototypeReadingsRemaining = if (tier == AccessTier.SEEKER_ELITE) 999 else preferences.getPrototypeReadingsRemaining()
            )
        }
    }

    // Call when MWA returns a connected public key:
    fun onWalletConnected(base58Address: String) {
        _connectedWallet.value = base58Address
    }

    fun disconnectWallet() {
        _connectedWallet.value = null
    }

    init {
        // Asynchronously load preferences and safely initialize background services on Dispatchers.IO
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val savedLang = preferences.currentLang
                val savedIntroDate = preferences.lastIntroDate
                val freeRemaining = preferences.getFreeReadingsRemaining()
                val prototypeRemaining = preferences.getPrototypeReadingsRemaining()
                val tier = getEffectiveAccessTier()
                val mockEnabled = preferences.isMockSeekerEliteEnabled
                
                // Pre-warm data structures off the main thread
                FortuneDataProvider.questions

                _uiState.update {
                    it.copy(
                        currentLang = savedLang,
                        lastIntroDate = savedIntroDate,
                        accessTier = tier,
                        isMockSeekerElite = mockEnabled,
                        freeReadingsRemaining = if (tier == AccessTier.SEEKER_ELITE) 999 else freeRemaining,
                        prototypeReadingsRemaining = if (tier == AccessTier.SEEKER_ELITE) 999 else prototypeRemaining
                    )
                }
            } catch (e: Exception) {
                Log.w("FortuneViewModel", "Error loading background preferences", e)
            }
        }
    }

    fun toggleLanguage() {
        val newLang = if (_uiState.value.currentLang == "mm") "en" else "mm"
        _uiState.update { it.copy(currentLang = newLang) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                preferences.currentLang = newLang
            } catch (e: Exception) {
                Log.w("FortuneViewModel", "Failed to persist language change", e)
            }
        }
    }

    fun setLanguage(lang: String) {
        _uiState.update { it.copy(currentLang = lang) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                preferences.currentLang = lang
            } catch (e: Exception) {
                Log.w("FortuneViewModel", "Failed to persist language change", e)
            }
        }
    }

    fun nextIntroStep() {
        _uiState.update {
            val nextStep = (it.introStep + 1).coerceAtMost(INTRO_STEPS.size - 1)
            it.copy(introStep = nextStep)
        }
    }

    fun prevIntroStep() {
        _uiState.update {
            val prevStep = (it.introStep - 1).coerceAtLeast(0)
            it.copy(introStep = prevStep)
        }
    }

    fun setIntroStep(step: Int) {
        _uiState.update {
            it.copy(introStep = step.coerceIn(0, INTRO_STEPS.size - 1))
        }
    }

    fun acceptIntro() {
        val today = FortunePreferences.getTodayDateString()
        _uiState.update {
            it.copy(
                lastIntroDate = today,
                currentScreen = AppScreen.Home
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                preferences.lastIntroDate = today
            } catch (e: Exception) {
                Log.w("FortuneViewModel", "Failed to persist intro date", e)
            }
        }
    }

    fun openGuide() {
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.Intro,
                introStep = 0
            )
        }
    }

    fun onPortalTapped() {
        // Tapping the main portal directly navigates to Question Selection Screen
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.SelectQuestion,
                searchQuery = ""
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectQuestionForConfirmation(questionId: String) {
        if (_uiState.value.isSubmittingTransaction || _uiState.value.isReconcilingAttestation) return
        if (_uiState.value.currentScreen != AppScreen.SelectQuestion) return
        _uiState.update { it.copy(pendingQuestionId = questionId) }
    }

    fun dismissQuestionConfirmation() {
        _uiState.update { it.copy(pendingQuestionId = null) }
    }

    fun confirmSelectedQuestion() {
        val pendingId = _uiState.value.pendingQuestionId ?: return
        if (_uiState.value.currentScreen != AppScreen.SelectQuestion) return
        _uiState.update { it.copy(pendingQuestionId = null) }
        onQuestionHoldConfirmed(pendingId)
    }

    // Hold-to-Cast completion: enforces quota exhaustion gatekeeping
    fun onQuestionHoldConfirmed(questionId: String) {
        val effectiveTier = getEffectiveAccessTier()
        if (effectiveTier == AccessTier.SEEKER_ELITE) {
            _uiState.update {
                it.copy(
                    accessTier = effectiveTier,
                    isWeb3Mode = true,
                    freeReadingsRemaining = 999,
                    prototypeReadingsRemaining = 999,
                    selectedQuestionId = questionId,
                    pendingQuestionId = null,
                    currentScreen = AppScreen.AnimationPlaceholder,
                    isQuotaExhaustedDialogVisible = false
                )
            }
            return
        }

        val freeRemaining = preferences.getFreeReadingsRemaining()
        val prototypeRemaining = preferences.getPrototypeReadingsRemaining()

        if (freeRemaining > 0) {
            _uiState.update {
                it.copy(
                    accessTier = effectiveTier,
                    isWeb3Mode = false,
                    freeReadingsRemaining = freeRemaining,
                    prototypeReadingsRemaining = prototypeRemaining,
                    selectedQuestionId = questionId,
                    pendingQuestionId = null,
                    currentScreen = AppScreen.AnimationPlaceholder,
                    isQuotaExhaustedDialogVisible = false
                )
            }
        } else if (prototypeRemaining > 0) {
            val isWalletConnected = !_connectedWallet.value.isNullOrBlank()
            if (!isWalletConnected) {
                _uiState.update {
                    it.copy(
                        isQuotaExhaustedDialogVisible = true,
                        selectedQuestionId = questionId,
                        pendingQuestionId = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        accessTier = effectiveTier,
                        isWeb3Mode = true,
                        freeReadingsRemaining = 0,
                        prototypeReadingsRemaining = prototypeRemaining,
                        selectedQuestionId = questionId,
                        pendingQuestionId = null,
                        currentScreen = AppScreen.AnimationPlaceholder,
                        isQuotaExhaustedDialogVisible = false
                    )
                }
            }
        } else {
            // Total daily quota exhausted (2 free + 3 Web3 = 5 completed)
            _uiState.update {
                it.copy(
                    isQuotaExhaustedDialogVisible = true,
                    selectedQuestionId = questionId,
                    pendingQuestionId = null
                )
            }
        }
    }

    fun dismissQuotaExhaustedDialog() {
        _uiState.update { it.copy(isQuotaExhaustedDialogVisible = false) }
    }

    fun proceedToWeb3ModeFromQuotaDialog() {
        val qId = _uiState.value.selectedQuestionId ?: "Q1"
        val prototypeRemaining = preferences.getPrototypeReadingsRemaining()

        if (prototypeRemaining > 0) {
            _uiState.update {
                it.copy(
                    isQuotaExhaustedDialogVisible = false,
                    isWeb3Mode = true,
                    freeReadingsRemaining = 0,
                    prototypeReadingsRemaining = prototypeRemaining,
                    selectedQuestionId = qId,
                    currentScreen = AppScreen.AnimationPlaceholder
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isQuotaExhaustedDialogVisible = true,
                    selectedQuestionId = qId
                )
            }
        }
    }

    fun selectFinalChoice(choice: Int) {
        if (_uiState.value.currentScreen != AppScreen.AnimationPlaceholder || _uiState.value.selectedQuestionId == null) return

        val effectiveTier = getEffectiveAccessTier()
        if (effectiveTier == AccessTier.SEEKER_ELITE) {
            _uiState.update {
                it.copy(
                    accessTier = effectiveTier,
                    finalChoice = choice,
                    freeReadingsRemaining = 999,
                    prototypeReadingsRemaining = 999,
                    currentScreen = AppScreen.Result,
                    isResultRevealed = false,
                    txSignature = null,
                    txTimestamp = null,
                    txError = null,
                    sealingState = SealingState.IDLE,
                    sealingError = null
                )
            }
            return
        }

        // Quota consumption happens here exactly once upon final choice reveal
        if (!_uiState.value.isWeb3Mode) {
            preferences.incrementDailyReading()
            val freeRem = preferences.getFreeReadingsRemaining()
            val protoRem = preferences.getPrototypeReadingsRemaining()
            _uiState.update {
                it.copy(
                    accessTier = effectiveTier,
                    finalChoice = choice,
                    freeReadingsRemaining = freeRem,
                    prototypeReadingsRemaining = protoRem,
                    currentScreen = AppScreen.Result,
                    isResultRevealed = false,
                    txSignature = null,
                    txTimestamp = null,
                    txError = null,
                    sealingState = SealingState.IDLE,
                    sealingError = null
                )
            }
        } else {
            preferences.incrementPrototypeWeb3Reading()
            val freeRem = preferences.getFreeReadingsRemaining()
            val protoRem = preferences.getPrototypeReadingsRemaining()
            _uiState.update {
                it.copy(
                    accessTier = effectiveTier,
                    finalChoice = choice,
                    freeReadingsRemaining = freeRem,
                    prototypeReadingsRemaining = protoRem,
                    currentScreen = AppScreen.Result,
                    isResultRevealed = false,
                    txSignature = null,
                    txTimestamp = null,
                    txError = null,
                    sealingState = SealingState.IDLE,
                    sealingError = null
                )
            }
        }
    }

    fun markResultRevealed() {
        _uiState.update { it.copy(isResultRevealed = true) }
    }

    fun sealCurrentReadingOnChain(
        sender: ActivityResultSender,
        walletManager: SolanaWalletManager,
        reconciliationDelayMs: Long = 1500L,
        onError: (String) -> Unit
    ) {
        val choice = _uiState.value.finalChoice ?: 0
        val questionId = _uiState.value.selectedQuestionId ?: "Q1"
        if (_uiState.value.isSubmittingTransaction || _uiState.value.isReconcilingAttestation) return

        val attestationId = "ORCL_${UUID.randomUUID()}"

        if (BuildConfig.DEBUG) {
            Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=SEALING_START")
        }

        _uiState.update {
            it.copy(
                isWeb3Mode = true,
                isSubmittingTransaction = true,
                isReconcilingAttestation = false,
                attestationStatusText = "Connecting wallet & sealing on Solana Devnet... 🔮",
                attestationId = attestationId,
                txSignature = null,
                sealingState = SealingState.WALLET_CONNECTING,
                txError = null,
                sealingError = null
            )
        }

        currentSealingJob = viewModelScope.launch {
            var connectedPubkey: String? = null
            var currentAttemptSignature: String? = null
            try {
                val blockhash = walletManager.fetchLatestBlockhash()

                walletManager.recordOracleAttestation(
                    sender = sender,
                    questionId = questionId,
                    chosenNumber = choice,
                    blockhash = blockhash,
                    attestationId = attestationId,
                    onAuthorizedWallet = { base58 ->
                        connectedPubkey = base58
                        onWalletConnected(base58)
                        _uiState.update { current ->
                            if (current.attestationId != attestationId) return@update current
                            current.copy(
                                sealingState = SealingState.SIGNING_DEVNET_MEMO,
                                attestationStatusText = "Signing Oracle Memo transaction..."
                            )
                        }
                    },
                    onSuccess = { txSig ->
                        currentAttemptSignature = txSig
                        if (BuildConfig.DEBUG) {
                            Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=ON_SUCCESS_RECEIVED hasCapturedSignature=true")
                        }
                        _uiState.update { current ->
                            if (current.attestationId != attestationId) return@update current
                            current.copy(
                                txSignature = txSig,
                                txTimestamp = System.currentTimeMillis(),
                                attestationStatusText = "Confirming transaction on Solana Devnet..."
                            )
                        }

                        viewModelScope.launch(Dispatchers.IO) {
                            try {
                                val pollResult = walletManager.pollSignatureConfirmation(txSig)
                                if (_uiState.value.attestationId != attestationId) return@launch
                                when (pollResult.status) {
                                    SignatureConfirmationStatus.CONFIRMED,
                                    SignatureConfirmationStatus.FINALIZED -> {
                                        if (BuildConfig.DEBUG) {
                                            Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=TERMINAL_STATE state=CONFIRMED")
                                        }
                                        persistConfirmedReadingIfNeeded(
                                            questionId = questionId,
                                            chosenNumber = choice,
                                            txSignature = txSig,
                                            attestationId = attestationId
                                        )

                                        _uiState.update { current ->
                                            if (current.attestationId != attestationId) return@update current
                                            current.copy(
                                                isSubmittingTransaction = false,
                                                isReconcilingAttestation = false,
                                                attestationStatusText = null,
                                                sealingState = SealingState.CONFIRMED,
                                                currentScreen = AppScreen.Result,
                                                isResultRevealed = true
                                            )
                                        }
                                    }
                                    SignatureConfirmationStatus.FAILED -> {
                                        if (BuildConfig.DEBUG) {
                                            Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=TERMINAL_STATE state=ERROR error=\"${pollResult.error}\"")
                                        }
                                        val err = pollResult.error ?: "Transaction failed on-chain."
                                        _uiState.update { current ->
                                            if (current.attestationId != attestationId) return@update current
                                            current.copy(
                                                isSubmittingTransaction = false,
                                                isReconcilingAttestation = false,
                                                sealingState = SealingState.ERROR,
                                                sealingError = err,
                                                attestationStatusText = null,
                                                txError = err
                                            )
                                        }
                                        withContext(Dispatchers.Main) {
                                            if (_uiState.value.attestationId == attestationId) {
                                                onError(err)
                                            }
                                        }
                                    }
                                    SignatureConfirmationStatus.NOT_FOUND,
                                    SignatureConfirmationStatus.PROCESSED -> {
                                        handleAttestationFailureOrReconcile(
                                            walletManager = walletManager,
                                            userWalletBase58 = connectedPubkey,
                                            attestationId = attestationId,
                                            fallbackErrorMsg = "Transaction confirmation timeout. Verifying via reconciliation...",
                                            outcome = TransactionFailureOutcome.UnknownTransactionOutcome("Transaction confirmation timeout. Verifying via reconciliation..."),
                                            reconciliationDelayMs = reconciliationDelayMs,
                                            onError = onError
                                        )
                                    }
                                }
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Exception) {
                                val err = e.message ?: "Transaction confirmation failed."
                                handleAttestationFailureOrReconcile(
                                    walletManager = walletManager,
                                    userWalletBase58 = connectedPubkey,
                                    attestationId = attestationId,
                                    fallbackErrorMsg = err,
                                    outcome = classifyTransactionFailure(e, currentAttemptSignature, "The Oracle attestation was cancelled.", err),
                                    reconciliationDelayMs = reconciliationDelayMs,
                                    onError = onError
                                )
                            }
                        }
                    },
                    onErrorOutcome = { outcome ->
                        handleAttestationFailureOrReconcile(
                            walletManager = walletManager,
                            userWalletBase58 = connectedPubkey,
                            attestationId = attestationId,
                            fallbackErrorMsg = outcome.message,
                            outcome = outcome,
                            reconciliationDelayMs = reconciliationDelayMs,
                            onError = onError
                        )
                    },
                    onError = { errorMsg ->
                        handleAttestationFailureOrReconcile(
                            walletManager = walletManager,
                            userWalletBase58 = connectedPubkey,
                            attestationId = attestationId,
                            fallbackErrorMsg = errorMsg,
                            outcome = null,
                            reconciliationDelayMs = reconciliationDelayMs,
                            onError = onError
                        )
                    }
                )
            } catch (e: CancellationException) {
                handleAttestationFailureOrReconcile(
                    walletManager = walletManager,
                    userWalletBase58 = connectedPubkey,
                    attestationId = attestationId,
                    fallbackErrorMsg = "The Oracle attestation was cancelled.",
                    outcome = classifyTransactionFailure(e, currentAttemptSignature, "The Oracle attestation was cancelled."),
                    reconciliationDelayMs = reconciliationDelayMs,
                    onError = onError
                )
            } catch (e: Exception) {
                val msg = e.message ?: "The Oracle attestation was cancelled."
                handleAttestationFailureOrReconcile(
                    walletManager = walletManager,
                    userWalletBase58 = connectedPubkey,
                    attestationId = attestationId,
                    fallbackErrorMsg = msg,
                    outcome = classifyTransactionFailure(e, currentAttemptSignature, "The Oracle attestation was cancelled.", msg),
                    reconciliationDelayMs = reconciliationDelayMs,
                    onError = onError
                )
            }
        }
    }

    fun onOracleNumberSelected(
        sender: ActivityResultSender,
        walletManager: SolanaWalletManager,
        choice: Int,
        onError: (String) -> Unit
    ) {
        selectFinalChoice(choice)
    }

    private fun handleAttestationFailureOrReconcile(
        walletManager: SolanaWalletManager,
        userWalletBase58: String?,
        attestationId: String,
        fallbackErrorMsg: String,
        outcome: TransactionFailureOutcome? = null,
        reconciliationDelayMs: Long = 1500L,
        onError: (String) -> Unit
    ) {
        val resolvedOutcome = outcome ?: TransactionFailureOutcome.UnknownTransactionOutcome(fallbackErrorMsg)
        val outcomeName = resolvedOutcome::class.java.simpleName

        if (BuildConfig.DEBUG) {
            Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=FAILURE_HANDLER_START outcome=$outcomeName outcomeMessage=\"${resolvedOutcome.message}\" hasAuthorizedWallet=${!userWalletBase58.isNullOrBlank()}")
        }

        if (_uiState.value.attestationId == attestationId &&
            (_uiState.value.sealingState == SealingState.ERROR || _uiState.value.sealingState == SealingState.CONFIRMED)) {
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=FAILURE_HANDLER_IGNORED reason=\"already in terminal state (${_uiState.value.sealingState})\" outcome=$outcomeName")
            }
            return
        }

        val outcomeMessage = when (resolvedOutcome) {
            is TransactionFailureOutcome.ExplicitUserDeclined -> resolvedOutcome.message
            is TransactionFailureOutcome.UnknownTransactionOutcome -> resolvedOutcome.message
        }

        if (userWalletBase58.isNullOrBlank() || attestationId.isBlank()) {
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=FAILURE_HANDLER_NO_WALLET reconciliationStarts=false maxPolls=0 terminalState=ERROR reason=\"no wallet or attestationId\"")
            }
            _uiState.update { current ->
                if (current.attestationId != attestationId) return@update current
                current.copy(
                    isSubmittingTransaction = false,
                    isReconcilingAttestation = false,
                    sealingState = SealingState.ERROR,
                    sealingError = outcomeMessage,
                    attestationStatusText = null,
                    txError = outcomeMessage
                )
            }
            viewModelScope.launch(Dispatchers.Main) {
                if (_uiState.value.attestationId == attestationId) {
                    onError(outcomeMessage)
                }
            }
            return
        }

        val maxPolls = when (resolvedOutcome) {
            is TransactionFailureOutcome.ExplicitUserDeclined -> 0
            is TransactionFailureOutcome.UnknownTransactionOutcome -> 8
        }

        if (maxPolls == 0) {
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=FAILURE_HANDLER_EXPLICIT_DECLINED reconciliationStarts=false maxPolls=0 terminalState=ERROR")
            }
            _uiState.update { current ->
                if (current.attestationId != attestationId) return@update current
                current.copy(
                    isSubmittingTransaction = false,
                    isReconcilingAttestation = false,
                    sealingState = SealingState.ERROR,
                    sealingError = outcomeMessage,
                    attestationStatusText = null,
                    txError = outcomeMessage
                )
            }
            viewModelScope.launch(Dispatchers.Main) {
                if (_uiState.value.attestationId == attestationId) {
                    onError(outcomeMessage)
                }
            }
            return
        }

        if (_uiState.value.isReconcilingAttestation && _uiState.value.attestationId == attestationId) {
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=FAILURE_HANDLER_ALREADY_RECONCILING reconciliationStarts=false maxPolls=$maxPolls")
            }
            return
        }

        if (BuildConfig.DEBUG) {
            Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=RECONCILIATION_START outcome=$outcomeName reconciliationStarts=true maxPolls=$maxPolls")
        }

        _uiState.update { current ->
            if (current.attestationId != attestationId) return@update current
            current.copy(
                isSubmittingTransaction = true,
                isReconcilingAttestation = true,
                sealingState = SealingState.RECONCILING,
                attestationStatusText = "Verifying on-chain attestation..."
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val added = activeAttestationReconciliations.add(attestationId)
            if (!added) {
                if (BuildConfig.DEBUG) {
                    Log.w("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=DUPLICATE_RECONCILIATION_DETECTED message=\"More than one reconciliation coroutine launched for same attestationId!\"")
                }
            }

            try {
                if (BuildConfig.DEBUG) {
                    Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=RECONCILIATION_COROUTINE_START poll=0 maxPolls=$maxPolls")
                }
                var foundSignature: String? = null
                var rpcErrorCount = 0

                for (attempt in 1..maxPolls) {
                    if (_uiState.value.attestationId != attestationId) return@launch
                    if (BuildConfig.DEBUG) {
                        Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=RECONCILIATION_POLL poll=$attempt maxPolls=$maxPolls")
                    }
                    try {
                        val sig = walletManager.reconcileAttestation(userWalletBase58, attestationId)
                        if (sig != null) {
                            foundSignature = sig
                            break
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        rpcErrorCount++
                        Log.w("FortuneViewModel", "RPC error during reconciliation attempt $attempt", e)
                    }
                    delay(reconciliationDelayMs.milliseconds)
                }

                if (_uiState.value.attestationId != attestationId) return@launch

                if (foundSignature != null) {
                    if (BuildConfig.DEBUG) {
                        Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=RECONCILIATION_COMPLETED hasCapturedSignature=true")
                        Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=TERMINAL_STATE state=CONFIRMED")
                    }
                    persistConfirmedReadingIfNeeded(
                        questionId = _uiState.value.selectedQuestionId ?: "Q1",
                        chosenNumber = _uiState.value.finalChoice ?: 0,
                        txSignature = foundSignature,
                        attestationId = attestationId
                    )

                    _uiState.update { current ->
                        if (current.attestationId != attestationId) return@update current
                        current.copy(
                            txSignature = foundSignature,
                            txTimestamp = System.currentTimeMillis(),
                            isSubmittingTransaction = false,
                            isReconcilingAttestation = false,
                            sealingState = SealingState.CONFIRMED,
                            attestationStatusText = null,
                            currentScreen = AppScreen.Result,
                            isResultRevealed = true
                        )
                    }
                } else if (rpcErrorCount >= maxPolls) {
                    if (BuildConfig.DEBUG) {
                        Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=RECONCILIATION_COMPLETED rpcErrors=$rpcErrorCount maxPolls=$maxPolls")
                        Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=TERMINAL_STATE state=ERROR reason=\"RPC errors\"")
                    }
                    val verificationErrMsg = "Unable to verify the Oracle attestation. Please check your connection and try again."
                    _uiState.update { current ->
                        if (current.attestationId != attestationId) return@update current
                        current.copy(
                            isSubmittingTransaction = false,
                            isReconcilingAttestation = false,
                            sealingState = SealingState.ERROR,
                            sealingError = verificationErrMsg,
                            attestationStatusText = null,
                            txError = verificationErrMsg
                        )
                    }
                    withContext(Dispatchers.Main) {
                        if (_uiState.value.attestationId == attestationId) {
                            onError(verificationErrMsg)
                        }
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=RECONCILIATION_COMPLETED exhausted=true maxPolls=$maxPolls")
                        Log.d("MWADiagnostics", "operation=SEALING attemptId=$attestationId event=TERMINAL_STATE state=ERROR reason=\"Reconciliation exhausted\"")
                    }
                    val errorMsgToUse = if (outcomeMessage.isNotBlank()) outcomeMessage else "The Oracle attestation was cancelled."
                    _uiState.update { current ->
                        if (current.attestationId != attestationId) return@update current
                        current.copy(
                            isSubmittingTransaction = false,
                            isReconcilingAttestation = false,
                            sealingState = SealingState.ERROR,
                            sealingError = errorMsgToUse,
                            attestationStatusText = null,
                            txError = errorMsgToUse
                        )
                    }
                    withContext(Dispatchers.Main) {
                        if (_uiState.value.attestationId == attestationId) {
                            onError(errorMsgToUse)
                        }
                    }
                }
            } finally {
                activeAttestationReconciliations.remove(attestationId)
            }
        }
    }

    private suspend fun persistConfirmedReadingIfNeeded(
        questionId: String,
        chosenNumber: Int,
        txSignature: String,
        attestationId: String
    ) {
        try {
            val question = FortuneDataProvider.getQuestionById(questionId) ?: return
            val answerObj = question.answers[chosenNumber.toString()]
            val answerText = if (_uiState.value.currentLang == "mm") answerObj?.mm ?: "" else answerObj?.en ?: ""
            val questionText = if (_uiState.value.currentLang == "mm") question.mm else question.en

            val entity = FortuneRecordEntity(
                timestamp = System.currentTimeMillis(),
                questionId = questionId,
                questionText = questionText,
                chosenNumber = chosenNumber,
                answerText = answerText,
                txSignature = txSignature,
                attestationId = attestationId
            )
            repository.insertRecord(entity)
            Log.i("FortuneViewModel", "Confirmed reading persisted idempotently to Room: attestationId=$attestationId")
        } catch (e: Exception) {
            Log.e("FortuneViewModel", "Failed to persist confirmed reading to Room", e)
        }
    }

    fun onTryAgain() {
        val s = _uiState.value
        if (s.isSubmittingTransaction || s.isReconcilingAttestation) return

        val freeRem = preferences.getFreeReadingsRemaining()
        val protoRem = preferences.getPrototypeReadingsRemaining()
        val tier = getEffectiveAccessTier()
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.SelectQuestion,
                selectedQuestionId = null,
                pendingQuestionId = null,
                finalChoice = null,
                searchQuery = "",
                accessTier = tier,
                isMockSeekerElite = preferences.isMockSeekerEliteEnabled,
                freeReadingsRemaining = if (tier == AccessTier.SEEKER_ELITE) 999 else freeRem,
                prototypeReadingsRemaining = if (tier == AccessTier.SEEKER_ELITE) 999 else protoRem,
                isSubmittingTransaction = false,
                isReconcilingAttestation = false,
                attestationStatusText = null,
                attestationId = null,
                txSignature = null,
                txError = null,
                txTimestamp = null,
                isOfferingInFlight = false,
                offeringStatusText = null,
                offeringTxSignature = null,
                offeringConfirmed = false,
                offeringUnknownConfirmation = false,
                offeringError = null,
                sealingState = SealingState.IDLE,
                sealingError = null,
                isQuotaExhaustedDialogVisible = false,
                isResultRevealed = false
            )
        }
    }

    fun navigateToHome() {
        val s = _uiState.value
        if (s.isSubmittingTransaction || s.isReconcilingAttestation) return

        val freeRem = preferences.getFreeReadingsRemaining()
        val protoRem = preferences.getPrototypeReadingsRemaining()
        val tier = getEffectiveAccessTier()
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.Home,
                selectedQuestionId = null,
                pendingQuestionId = null,
                finalChoice = null,
                searchQuery = "",
                introStep = 0,
                isDrawerOpen = false,
                isHelpModalOpen = false,
                accessTier = tier,
                isMockSeekerElite = preferences.isMockSeekerEliteEnabled,
                freeReadingsRemaining = if (tier == AccessTier.SEEKER_ELITE) 999 else freeRem,
                prototypeReadingsRemaining = if (tier == AccessTier.SEEKER_ELITE) 999 else protoRem,
                isSubmittingTransaction = false,
                isReconcilingAttestation = false,
                attestationStatusText = null,
                attestationId = null,
                txSignature = null,
                txError = null,
                txTimestamp = null,
                isOfferingInFlight = false,
                offeringStatusText = null,
                offeringTxSignature = null,
                offeringConfirmed = false,
                offeringUnknownConfirmation = false,
                offeringError = null,
                sealingState = SealingState.IDLE,
                sealingError = null,
                isQuotaExhaustedDialogVisible = false,
                isResultRevealed = false
            )
        }
    }

    fun openDrawer() {
        _uiState.update { it.copy(isDrawerOpen = true) }
    }

    fun closeDrawer() {
        _uiState.update { it.copy(isDrawerOpen = false) }
    }

    fun openHelpModal() {
        _uiState.update { it.copy(isHelpModalOpen = true, isDrawerOpen = false) }
    }

    fun closeHelpModal() {
        _uiState.update { it.copy(isHelpModalOpen = false) }
    }

    fun onMakeOracleOffering(
        sender: ActivityResultSender,
        walletManager: SolanaWalletManager,
        reconciliationDelayMs: Long = 1500L,
        onStatusMessage: (String) -> Unit
    ) {
        if (_uiState.value.isOfferingInFlight) return

        val offeringId = "OFFERING_${UUID.randomUUID()}"
        var userWalletBase58: String? = null
        var currentAttemptOfferingSignature: String? = null

        if (BuildConfig.DEBUG) {
            Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=OFFERING_START")
        }

        _uiState.update {
            it.copy(
                isOfferingInFlight = true,
                offeringId = offeringId,
                offeringTxSignature = null,
                offeringStatusText = "Preparing Oracle Offering...",
                offeringError = null,
                offeringConfirmed = false,
                offeringUnknownConfirmation = false
            )
        }

        viewModelScope.launch {
            try {
                _uiState.update { current ->
                    if (current.offeringId != offeringId) return@update current
                    current.copy(offeringStatusText = "Waiting for wallet approval...")
                }

                val blockhash = walletManager.fetchLatestBlockhash()

                walletManager.sendOracleOffering(
                    sender = sender,
                    blockhash = blockhash,
                    offeringId = offeringId,
                    onAuthorizedWallet = { base58 ->
                        userWalletBase58 = base58
                        onWalletConnected(base58)
                    },
                    onSuccess = { offeringSig ->
                        currentAttemptOfferingSignature = offeringSig
                        if (BuildConfig.DEBUG) {
                            Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=ON_SUCCESS_RECEIVED hasCapturedSignature=true")
                        }
                        _uiState.update { current ->
                            if (current.offeringId != offeringId) return@update current
                            current.copy(
                                offeringTxSignature = offeringSig,
                                offeringStatusText = "Confirming on Solana Devnet..."
                            )
                        }

                        viewModelScope.launch(Dispatchers.IO) {
                            val pollResult = walletManager.pollSignatureConfirmation(offeringSig)
                            if (_uiState.value.offeringId != offeringId) return@launch
                            when (pollResult.status) {
                                SignatureConfirmationStatus.CONFIRMED,
                                SignatureConfirmationStatus.FINALIZED -> {
                                    if (BuildConfig.DEBUG) {
                                        Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=TERMINAL_STATE state=CONFIRMED")
                                    }
                                    _uiState.update { current ->
                                        if (current.offeringId != offeringId) return@update current
                                        current.copy(
                                            isOfferingInFlight = false,
                                            offeringConfirmed = true,
                                            offeringUnknownConfirmation = false,
                                            offeringStatusText = "✨ Oracle Offering Confirmed",
                                            offeringError = null
                                        )
                                    }
                                }
                                SignatureConfirmationStatus.FAILED -> {
                                    if (BuildConfig.DEBUG) {
                                        Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=TERMINAL_STATE state=ERROR error=\"${pollResult.error}\"")
                                    }
                                    val err = pollResult.error ?: "The Oracle offering failed."
                                    _uiState.update { current ->
                                        if (current.offeringId != offeringId) return@update current
                                        current.copy(
                                            isOfferingInFlight = false,
                                            offeringConfirmed = false,
                                            offeringUnknownConfirmation = false,
                                            offeringTxSignature = null,
                                            offeringStatusText = null,
                                            offeringError = err
                                        )
                                    }
                                    withContext(Dispatchers.Main) {
                                        if (_uiState.value.offeringId == offeringId) {
                                            onStatusMessage(err)
                                        }
                                    }
                                }
                                SignatureConfirmationStatus.NOT_FOUND,
                                SignatureConfirmationStatus.PROCESSED -> {
                                    handleOfferingFailureOrReconcile(
                                        walletManager = walletManager,
                                        userWalletBase58 = userWalletBase58,
                                        offeringId = offeringId,
                                        offeringSignature = offeringSig,
                                        fallbackErrorMsg = "Offering confirmation timeout. Verifying via reconciliation...",
                                        outcome = TransactionFailureOutcome.UnknownTransactionOutcome("Offering confirmation timeout. Verifying via reconciliation..."),
                                        reconciliationDelayMs = reconciliationDelayMs,
                                        onStatusMessage = onStatusMessage
                                    )
                                }
                            }
                        }
                    },
                    onErrorOutcome = { outcome ->
                        handleOfferingFailureOrReconcile(
                            walletManager = walletManager,
                            userWalletBase58 = userWalletBase58,
                            offeringId = offeringId,
                            offeringSignature = currentAttemptOfferingSignature,
                            fallbackErrorMsg = outcome.message,
                            outcome = outcome,
                            reconciliationDelayMs = reconciliationDelayMs,
                            onStatusMessage = onStatusMessage
                        )
                    },
                    onError = { errorMsg ->
                        handleOfferingFailureOrReconcile(
                            walletManager = walletManager,
                            userWalletBase58 = userWalletBase58,
                            offeringId = offeringId,
                            offeringSignature = currentAttemptOfferingSignature,
                            fallbackErrorMsg = errorMsg,
                            outcome = null,
                            reconciliationDelayMs = reconciliationDelayMs,
                            onStatusMessage = onStatusMessage
                        )
                    }
                )
            } catch (e: CancellationException) {
                handleOfferingFailureOrReconcile(
                    walletManager = walletManager,
                    userWalletBase58 = userWalletBase58,
                    offeringId = offeringId,
                    offeringSignature = currentAttemptOfferingSignature,
                    fallbackErrorMsg = "The Oracle offering was cancelled.",
                    outcome = classifyTransactionFailure(e, currentAttemptOfferingSignature, "The Oracle offering was cancelled."),
                    reconciliationDelayMs = reconciliationDelayMs,
                    onStatusMessage = onStatusMessage
                )
            } catch (e: Exception) {
                val msg = e.message ?: "The Oracle offering was cancelled."
                handleOfferingFailureOrReconcile(
                    walletManager = walletManager,
                    userWalletBase58 = userWalletBase58,
                    offeringId = offeringId,
                    offeringSignature = currentAttemptOfferingSignature,
                    fallbackErrorMsg = msg,
                    outcome = classifyTransactionFailure(e, currentAttemptOfferingSignature, "The Oracle offering was cancelled.", msg),
                    reconciliationDelayMs = reconciliationDelayMs,
                    onStatusMessage = onStatusMessage
                )
            }
        }
    }

    private fun handleOfferingFailureOrReconcile(
        walletManager: SolanaWalletManager,
        userWalletBase58: String?,
        offeringId: String,
        offeringSignature: String? = null,
        fallbackErrorMsg: String,
        outcome: TransactionFailureOutcome? = null,
        reconciliationDelayMs: Long = 1500L,
        onStatusMessage: (String) -> Unit
    ) {
        val knownSig = offeringSignature ?: _uiState.value.offeringTxSignature
        val resolvedOutcome = outcome ?: TransactionFailureOutcome.UnknownTransactionOutcome(fallbackErrorMsg)
        val outcomeName = resolvedOutcome::class.java.simpleName

        if (BuildConfig.DEBUG) {
            Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=FAILURE_HANDLER_START outcome=$outcomeName outcomeMessage=\"${resolvedOutcome.message}\" hasAuthorizedWallet=${!userWalletBase58.isNullOrBlank()}")
        }

        if (_uiState.value.offeringId == offeringId && !_uiState.value.isOfferingInFlight && (_uiState.value.offeringError != null || _uiState.value.offeringConfirmed)) {
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=FAILURE_HANDLER_IGNORED reason=\"already in terminal state\" outcome=$outcomeName")
            }
            return
        }

        val outcomeMessage = when (resolvedOutcome) {
            is TransactionFailureOutcome.ExplicitUserDeclined -> resolvedOutcome.message
            is TransactionFailureOutcome.UnknownTransactionOutcome -> resolvedOutcome.message
        }

        if (userWalletBase58.isNullOrBlank() || offeringId.isBlank()) {
            val termState = if (!knownSig.isNullOrBlank()) "UNKNOWN_CONFIRMATION" else "ERROR"
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=FAILURE_HANDLER_NO_WALLET reconciliationStarts=false maxPolls=0 terminalState=$termState reason=\"no wallet or offeringId\"")
            }
            if (!knownSig.isNullOrBlank()) {
                _uiState.update { current ->
                    if (current.offeringId != offeringId) return@update current
                    current.copy(
                        isOfferingInFlight = false,
                        offeringConfirmed = false,
                        offeringUnknownConfirmation = true,
                        offeringTxSignature = knownSig,
                        offeringStatusText = null,
                        offeringError = null
                    )
                }
            } else {
                _uiState.update { current ->
                    if (current.offeringId != offeringId) return@update current
                    current.copy(
                        isOfferingInFlight = false,
                        offeringConfirmed = false,
                        offeringUnknownConfirmation = false,
                        offeringStatusText = null,
                        offeringError = outcomeMessage
                    )
                }
                viewModelScope.launch(Dispatchers.Main) {
                    if (_uiState.value.offeringId == offeringId) {
                        onStatusMessage(outcomeMessage)
                    }
                }
            }
            return
        }

        val maxPolls = when (resolvedOutcome) {
            is TransactionFailureOutcome.ExplicitUserDeclined -> 0
            is TransactionFailureOutcome.UnknownTransactionOutcome -> 8
        }

        if (maxPolls == 0) {
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=FAILURE_HANDLER_EXPLICIT_DECLINED reconciliationStarts=false maxPolls=0 terminalState=ERROR")
            }
            _uiState.update { current ->
                if (current.offeringId != offeringId) return@update current
                current.copy(
                    isOfferingInFlight = false,
                    offeringConfirmed = false,
                    offeringUnknownConfirmation = false,
                    offeringTxSignature = null,
                    offeringStatusText = null,
                    offeringError = outcomeMessage
                )
            }
            viewModelScope.launch(Dispatchers.Main) {
                if (_uiState.value.offeringId == offeringId) {
                    onStatusMessage(outcomeMessage)
                }
            }
            return
        }

        if (_uiState.value.isOfferingInFlight && _uiState.value.offeringStatusText == "Verifying Oracle offering on Devnet..." && _uiState.value.offeringId == offeringId) {
            if (BuildConfig.DEBUG) {
                Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=FAILURE_HANDLER_ALREADY_VERIFYING reconciliationStarts=false maxPolls=$maxPolls")
            }
            return
        }

        if (BuildConfig.DEBUG) {
            Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=RECONCILIATION_START outcome=$outcomeName reconciliationStarts=true maxPolls=$maxPolls")
        }

        _uiState.update { current ->
            if (current.offeringId != offeringId) return@update current
            current.copy(
                isOfferingInFlight = true,
                offeringStatusText = "Verifying Oracle offering on Devnet..."
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            val added = activeOfferingReconciliations.add(offeringId)
            if (!added) {
                if (BuildConfig.DEBUG) {
                    Log.w("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=DUPLICATE_RECONCILIATION_DETECTED message=\"More than one reconciliation coroutine launched for same offeringId!\"")
                }
            }

            try {
                if (BuildConfig.DEBUG) {
                    Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=RECONCILIATION_COROUTINE_START poll=0 maxPolls=$maxPolls")
                }
                var foundSignature: String? = null
                var rpcErrorCount = 0

                for (attempt in 1..maxPolls) {
                    if (_uiState.value.offeringId != offeringId) return@launch
                    if (BuildConfig.DEBUG) {
                        Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=RECONCILIATION_POLL poll=$attempt maxPolls=$maxPolls")
                    }
                    try {
                        val sig = walletManager.reconcileOffering(userWalletBase58, offeringId)
                        if (sig != null) {
                            foundSignature = sig
                            break
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        rpcErrorCount++
                        Log.w("FortuneViewModel", "RPC error during offering reconciliation attempt $attempt", e)
                    }
                    delay(reconciliationDelayMs.milliseconds)
                }

                if (_uiState.value.offeringId != offeringId) return@launch

                if (foundSignature != null) {
                    if (BuildConfig.DEBUG) {
                        Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=RECONCILIATION_COMPLETED hasCapturedSignature=true")
                        Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=TERMINAL_STATE state=CONFIRMED")
                    }
                    _uiState.update { current ->
                        if (current.offeringId != offeringId) return@update current
                        current.copy(
                            isOfferingInFlight = false,
                            offeringConfirmed = true,
                            offeringUnknownConfirmation = false,
                            offeringTxSignature = foundSignature,
                            offeringStatusText = "✨ Oracle Offering Confirmed",
                            offeringError = null
                        )
                    }
                } else if (!knownSig.isNullOrBlank()) {
                    if (BuildConfig.DEBUG) {
                        Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=RECONCILIATION_COMPLETED hasCapturedSignature=true")
                        Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=TERMINAL_STATE state=UNKNOWN_CONFIRMATION")
                    }
                    _uiState.update { current ->
                        if (current.offeringId != offeringId) return@update current
                        current.copy(
                            isOfferingInFlight = false,
                            offeringConfirmed = false,
                            offeringUnknownConfirmation = true,
                            offeringTxSignature = knownSig,
                            offeringStatusText = null,
                            offeringError = null
                        )
                    }
                } else if (rpcErrorCount >= maxPolls) {
                    if (BuildConfig.DEBUG) {
                        Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=RECONCILIATION_COMPLETED rpcErrors=$rpcErrorCount maxPolls=$maxPolls")
                        Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=TERMINAL_STATE state=ERROR reason=\"RPC errors\"")
                    }
                    val verificationErrMsg = "Unable to verify the Oracle offering. Please check your connection and try again."
                    _uiState.update { current ->
                        if (current.offeringId != offeringId) return@update current
                        current.copy(
                            isOfferingInFlight = false,
                            offeringConfirmed = false,
                            offeringUnknownConfirmation = false,
                            offeringTxSignature = null,
                            offeringStatusText = null,
                            offeringError = verificationErrMsg
                        )
                    }
                    withContext(Dispatchers.Main) {
                        if (_uiState.value.offeringId == offeringId) {
                            onStatusMessage(verificationErrMsg)
                        }
                    }
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=RECONCILIATION_COMPLETED exhausted=true maxPolls=$maxPolls")
                        Log.d("MWADiagnostics", "operation=OFFERING attemptId=$offeringId event=TERMINAL_STATE state=ERROR reason=\"Reconciliation exhausted\"")
                    }
                    val cancelMsg = if (outcomeMessage.isNotBlank()) outcomeMessage else "The Oracle offering was cancelled."
                    _uiState.update { current ->
                        if (current.offeringId != offeringId) return@update current
                        current.copy(
                            isOfferingInFlight = false,
                            offeringConfirmed = false,
                            offeringUnknownConfirmation = false,
                            offeringTxSignature = null,
                            offeringStatusText = null,
                            offeringError = cancelMsg
                        )
                    }
                    withContext(Dispatchers.Main) {
                        if (_uiState.value.offeringId == offeringId) {
                            onStatusMessage(cancelMsg)
                        }
                    }
                }
            } finally {
                activeOfferingReconciliations.remove(offeringId)
            }
        }
    }

    fun resetAllState() {
        val s = _uiState.value
        if (s.isSubmittingTransaction || s.isReconcilingAttestation) return

        val freeRem = preferences.getFreeReadingsRemaining()
        val protoRem = preferences.getPrototypeReadingsRemaining()
        val tier = getEffectiveAccessTier()
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.Home,
                selectedQuestionId = null,
                pendingQuestionId = null,
                finalChoice = null,
                searchQuery = "",
                introStep = 0,
                isDrawerOpen = false,
                isHelpModalOpen = false,
                accessTier = tier,
                isMockSeekerElite = preferences.isMockSeekerEliteEnabled,
                freeReadingsRemaining = if (tier == AccessTier.SEEKER_ELITE) 999 else freeRem,
                prototypeReadingsRemaining = if (tier == AccessTier.SEEKER_ELITE) 999 else protoRem,
                isSubmittingTransaction = false,
                txSignature = null,
                txError = null,
                txTimestamp = null,
                isOfferingInFlight = false,
                offeringStatusText = null,
                offeringTxSignature = null,
                offeringConfirmed = false,
                offeringUnknownConfirmation = false,
                offeringError = null,
                sealingState = SealingState.IDLE,
                sealingError = null,
                isQuotaExhaustedDialogVisible = false,
                isResultRevealed = false
            )
        }
    }

    fun handleBack(): Boolean {
        val s = _uiState.value
        if (s.isSubmittingTransaction || s.isReconcilingAttestation) {
            if (s.sealingState == SealingState.WALLET_CONNECTING && s.txSignature.isNullOrBlank() && s.attestationId != null) {
                currentSealingJob?.cancel()
                currentSealingJob = null
                _uiState.update { current ->
                    current.copy(
                        isSubmittingTransaction = false,
                        isReconcilingAttestation = false,
                        attestationStatusText = null,
                        attestationId = null,
                        sealingState = SealingState.IDLE,
                        sealingError = null
                    )
                }
                return true
            }
            return true
        }
        return when {
            s.pendingQuestionId != null -> {
                dismissQuestionConfirmation()
                true
            }
            s.isDrawerOpen -> {
                closeDrawer()
                true
            }
            s.isHelpModalOpen -> {
                closeHelpModal()
                true
            }
            s.currentScreen == AppScreen.Result || s.currentScreen == AppScreen.AnimationPlaceholder -> {
                onTryAgain()
                true
            }
            s.currentScreen == AppScreen.SelectQuestion -> {
                navigateToHome()
                true
            }
            s.currentScreen == AppScreen.History -> {
                val target = s.previousScreen ?: AppScreen.Home
                _uiState.update { it.copy(currentScreen = target, previousScreen = null) }
                true
            }
            s.currentScreen == AppScreen.Intro -> {
                navigateToHome()
                true
            }
            else -> false // Exit app or default system back
        }
    }
}
