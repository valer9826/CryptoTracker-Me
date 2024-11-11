package com.plcoding.cryptotracker.crypto.presentation.coin_list

import com.plcoding.cryptotracker.crypto.presentation.models.CoinUi

sealed interface CoinListUiAction {
    data class OnCoinClick(
        val coinUi: CoinUi
    ) : CoinListUiAction
}