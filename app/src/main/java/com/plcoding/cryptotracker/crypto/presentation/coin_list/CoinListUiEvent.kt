package com.plcoding.cryptotracker.crypto.presentation.coin_list

import com.plcoding.cryptotracker.core.domain.util.NetworkError

sealed interface CoinListUiEvent {
    data class Error(val error: NetworkError) : CoinListUiEvent
}