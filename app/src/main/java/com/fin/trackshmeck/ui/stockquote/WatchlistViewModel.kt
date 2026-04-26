package com.fin.trackshmeck.ui.stockquote

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fin.trackshmeck.data.model.WatchlistItem
import com.fin.trackshmeck.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val repository: StockRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val expandedIds: MutableStateFlow<Set<Long>> = MutableStateFlow(
        savedStateHandle.get<ArrayList<Long>>("expandedIds")?.toSet() ?: emptySet()
    )

    private val _focusItemId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<WatchlistUiState> = combine(
        repository.observeWatchlist(),
        expandedIds,
        _focusItemId,
    ) { items, expanded, focusId ->
        WatchlistUiState(
            items = items.map { it.toUi(isExpanded = it.id in expanded) },
            isLoading = false,
            focusItemId = focusId,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WatchlistUiState())

    init {
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
            repository.refreshAll()
        }
    }

    fun onToggleExpand(id: Long) {
        val current = expandedIds.value
        val updated = if (id in current) current - id else current + id
        expandedIds.value = updated
        savedStateHandle["expandedIds"] = ArrayList(updated.toList())
    }

    fun onTickerChanged(id: Long, newTicker: String) {
        viewModelScope.launch {
            repository.updateTicker(id, newTicker)
        }
    }

    fun onQuantityChanged(id: Long, newQuantity: Double) {
        viewModelScope.launch {
            repository.updateQuantity(id, newQuantity)
        }
    }

    fun onAddItem() {
        viewModelScope.launch {
            val newId = repository.addItem("", 0.0)
            _focusItemId.value = newId
        }
    }

    fun onFocusConsumed() {
        _focusItemId.value = null
    }

    fun onDeleteItem(id: Long) {
        viewModelScope.launch {
            repository.deleteItem(id)
        }
    }

    private fun WatchlistItem.toUi(isExpanded: Boolean) = WatchlistItemUi(
        id = id,
        ticker = ticker,
        price = currentPrice,
        quantity = quantity,
        value = value,
        change = change,
        percentChange = percentChange,
        highPrice = highPrice,
        lowPrice = lowPrice,
        openPrice = openPrice,
        previousClose = previousClose,
        companyName = companyName,
        country = country,
        currency = currency,
        exchange = exchange,
        industry = industry,
        ipoDate = ipoDate,
        logoUrl = logoUrl,
        marketCap = marketCap,
        phone = phone,
        sharesOutstanding = sharesOutstanding,
        webUrl = webUrl,
        isExpanded = isExpanded,
        fetchError = fetchError,
    )
}
