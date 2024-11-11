package com.plcoding.cryptotracker.crypto.presentation.coin_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cryptotracker.core.domain.util.onError
import com.plcoding.cryptotracker.core.domain.util.onSuccess
import com.plcoding.cryptotracker.crypto.domain.Coin
import com.plcoding.cryptotracker.crypto.domain.CoinDataSource
import com.plcoding.cryptotracker.crypto.presentation.models.CoinListUiState
import com.plcoding.cryptotracker.crypto.presentation.models.CoinUi
import com.plcoding.cryptotracker.crypto.presentation.models.toCoinUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

class CoinListViewModel(
    private val coinDataSource: CoinDataSource
) : ViewModel() {

    private val _state = MutableStateFlow(CoinListUiState())
    val state = _state.onStart {
        loadCoins()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = CoinListUiState()
    )

    private val _events = Channel<CoinListUiEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: CoinListUiAction) {
        when (action) {
            is CoinListUiAction.OnCoinClick -> {
                selectCoin(action.coinUi)
            }
        }
    }

    private fun selectCoin(coinUi: CoinUi) {
        _state.update {
            it.copy(selectedCoin = coinUi)
        }
        viewModelScope.launch {
            coinDataSource.getCoinHistory(
                coinId = coinUi.id,
                start = ZonedDateTime.now().minusDays(5),
                end = ZonedDateTime.now()
            ).onSuccess { history ->
                println(history)

            }.onError { error ->
                _state.update { it.copy(isLoading = false) }
                _events.send(CoinListUiEvent.Error(error = error))
            }
        }
    }

    private fun loadCoins() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            coinDataSource.getCoins().onSuccess { coins ->
                _state.update { uiState ->
                    uiState.copy(
                        isLoading = false,
                        coins = coins.map {
                            it.toCoinUi()
                        }
                    )
                }
            }.onError { error ->
                _state.update { it.copy(isLoading = false) }
                _events.send(CoinListUiEvent.Error(error = error))
            }
        }
    }

}