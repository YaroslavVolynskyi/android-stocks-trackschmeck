package com.fin.trackshmeck.ui.stockquote

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WatchlistRoute(
    modifier: Modifier = Modifier,
    viewModel: WatchlistViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WatchlistScreen(
        uiState = uiState,
        onToggleExpand = viewModel::onToggleExpand,
        onTickerChanged = viewModel::onTickerChanged,
        onQuantityChanged = viewModel::onQuantityChanged,
        onAddItem = viewModel::onAddItem,
        onDeleteItem = viewModel::onDeleteItem,
        onFocusConsumed = viewModel::onFocusConsumed,
        modifier = modifier,
    )
}

@Composable
fun WatchlistScreen(
    uiState: WatchlistUiState,
    onToggleExpand: (Long) -> Unit,
    onTickerChanged: (Long, String) -> Unit,
    onQuantityChanged: (Long, Double) -> Unit,
    onAddItem: () -> Unit,
    onDeleteItem: (Long) -> Unit,
    onFocusConsumed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddItem) {
                Icon(Icons.Default.Add, contentDescription = "Add stock")
            }
        },
    ) { padding ->
        if (uiState.isLoading && uiState.items.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp
                ),
            ) {
                items(uiState.items, key = { it.id }) { item ->
                    WatchlistCard(
                        item = item,
                        shouldFocus = uiState.focusItemId == item.id,
                        onToggleExpand = { onToggleExpand(item.id) },
                        onTickerChanged = { onTickerChanged(item.id, it) },
                        onQuantityChanged = { onQuantityChanged(item.id, it) },
                        onDelete = { onDeleteItem(item.id) },
                        onFocusConsumed = onFocusConsumed,
                    )
                }
            }
        }
    }
}

@Composable
fun WatchlistCard(
    item: WatchlistItemUi,
    shouldFocus: Boolean,
    onToggleExpand: () -> Unit,
    onTickerChanged: (String) -> Unit,
    onQuantityChanged: (Double) -> Unit,
    onDelete: () -> Unit,
    onFocusConsumed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tickerFocusRequester = remember { FocusRequester() }
    var tickerText by remember(item.ticker) { mutableStateOf(item.ticker) }
    var quantityText by remember(item.quantity) {
        mutableStateOf(if (item.quantity == 0.0) "" else item.quantity.toBigDecimal().stripTrailingZeros().toPlainString())
    }

    LaunchedEffect(shouldFocus) {
        if (shouldFocus) {
            tickerFocusRequester.requestFocus()
            onFocusConsumed()
        }
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Collapsed row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Ticker (editable)
                Column(modifier = Modifier.width(72.dp)) {
                    BasicTextField(
                        value = tickerText,
                        onValueChange = { tickerText = it.uppercase() },
                        modifier = Modifier.focusRequester(tickerFocusRequester),
                        textStyle = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { onTickerChanged(tickerText) }
                        ),
                        decorationBox = { inner ->
                            if (tickerText.isEmpty()) {
                                Text(
                                    "TICK",
                                    style = LocalTextStyle.current.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    ),
                                )
                            }
                            inner()
                        },
                    )
                    if (item.fetchError != null) {
                        Text(
                            text = item.fetchError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }

                Spacer(Modifier.width(8.dp))

                // Price
                Text(
                    text = "$${formatPrice(item.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                )

                // Quantity (editable)
                BasicTextField(
                    value = quantityText,
                    onValueChange = { newVal ->
                        val filtered = newVal.filter { it.isDigit() || it == '.' }
                        quantityText = filtered
                        filtered.toDoubleOrNull()?.let { onQuantityChanged(it) }
                    },
                    modifier = Modifier.width(56.dp),
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    decorationBox = { inner ->
                        if (quantityText.isEmpty()) {
                            Text(
                                "Qty",
                                style = LocalTextStyle.current.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                ),
                            )
                        }
                        inner()
                    },
                )

                Spacer(Modifier.width(8.dp))

                // Value (calculated)
                Text(
                    text = "$${formatPrice(item.value)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(80.dp),
                )

                // Details/Hide button
                TextButton(onClick = onToggleExpand) {
                    Text(if (item.isExpanded) "Hide" else "Details")
                }
            }

            // Expanded details
            AnimatedVisibility(visible = item.isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))

                    item.companyName?.let { DetailRow("Company", it) }
                    item.exchange?.let { DetailRow("Exchange", it) }
                    item.industry?.let { DetailRow("Industry", it) }
                    item.country?.let { DetailRow("Country", it) }
                    item.currency?.let { DetailRow("Currency", it) }
                    DetailRow("Open", "$${formatPrice(item.openPrice)}")
                    DetailRow("High", "$${formatPrice(item.highPrice)}")
                    DetailRow("Low", "$${formatPrice(item.lowPrice)}")
                    DetailRow("Prev Close", "$${formatPrice(item.previousClose)}")
                    DetailRow("Change", "${formatPrice(item.change)} (${formatPrice(item.percentChange)}%)")
                    item.marketCap?.let { DetailRow("Market Cap", formatLargeNumber(it)) }
                    item.sharesOutstanding?.let { DetailRow("Shares Out", formatLargeNumber(it)) }
                    item.ipoDate?.let { DetailRow("IPO Date", it) }
                    item.webUrl?.let { DetailRow("Website", it) }
                    item.phone?.let { DetailRow("Phone", it) }

                    Spacer(Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        IconButton(onClick = onDelete) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(100.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

private fun formatPrice(value: Double): String =
    "%.2f".format(value)

private fun formatLargeNumber(value: Double): String = when {
    value >= 1_000_000 -> "%.2fT".format(value / 1_000_000)
    value >= 1_000 -> "%.2fB".format(value / 1_000)
    value >= 1 -> "%.2fM".format(value)
    else -> "%.2f".format(value)
}
