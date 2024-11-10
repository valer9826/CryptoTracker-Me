package com.plcoding.cryptotracker.crypto.presentation.models

import androidx.compose.runtime.Immutable

@Immutable
data class CoinListUiState(
    val isLoading: Boolean = false,
    val coins: List<CoinUi> = emptyList(),
    val selectedCoin: CoinUi? = null
)
