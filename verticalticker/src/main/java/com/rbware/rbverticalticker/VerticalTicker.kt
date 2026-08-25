package com.rbware.rbverticalticker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow

/**
 * A single entry that has been shown on a [VerticalTicker], oldest first.
 */
@ConsistentCopyVisibility
data class TickerEntry internal constructor(val id: Int, val text: String)

/**
 * Hoisted state for a [VerticalTicker]. Create with [rememberVerticalTickerState] and drive it
 * with [showNext].
 */
@Stable
class VerticalTickerState internal constructor(private val items: List<String>) {

    private val _history = mutableStateListOf<TickerEntry>()

    /** Every entry shown so far, oldest first, capped at [HISTORY_LIMIT]. */
    val history: List<TickerEntry> get() = _history

    val currentItem: String get() = _history.lastOrNull()?.text.orEmpty()

    var currentIndex: Int = 0
        private set

    private var nextId = 0

    init {
        items.firstOrNull()?.let(::push)
    }

    /** Advances to the next entry in the [items] list passed to [rememberVerticalTickerState], wrapping around at the end. */
    fun showNext() {
        if (items.isEmpty()) return
        currentIndex = (currentIndex + 1) % items.size
        push(items[currentIndex])
    }

    /** Displays [item] next, without affecting the [items] list or [currentIndex]. */
    fun showNext(item: String) {
        push(item)
    }

    private fun push(text: String) {
        _history.add(TickerEntry(nextId++, text))
        if (_history.size > HISTORY_LIMIT) {
            _history.removeAt(0)
        }
    }

    private companion object {
        const val HISTORY_LIMIT = 50
    }
}

@Composable
fun rememberVerticalTickerState(items: List<String>): VerticalTickerState =
    remember(items) { VerticalTickerState(items) }

/**
 * Displays the [visibleCount] most recent entries of [state], most recent at the bottom.
 * The ticker always reserves room for [visibleCount] rows, so its bottom edge stays put: as
 * new entries arrive, older ones shift upward and the newest fades in at the bottom, rather
 * than the whole view growing downward. Advance [state] manually by calling
 * [VerticalTickerState.showNext], e.g. from a button click or your own timer.
 *
 * @param state the ticker's hoisted state, see [rememberVerticalTickerState].
 * @param modifier applied to the ticker's container.
 * @param visibleCount how many of the most recent entries to show at once. Defaults to 1.
 * @param topFadeAlpha opacity, from 0 (fully transparent) to 1 (fully opaque, the default), applied
 * to the top edge of the ticker; it ramps linearly up to full opacity at the bottom edge, so older
 * rows appear to fade away as newer ones arrive. 1f disables the effect entirely.
 * @param itemContent how to render a single item; defaults to a single line of [MaterialTheme] text.
 */
@Composable
fun VerticalTicker(
    state: VerticalTickerState,
    modifier: Modifier = Modifier,
    visibleCount: Int = 1,
    topFadeAlpha: Float = 1f,
    itemContent: @Composable (String) -> Unit = { DefaultTickerItem(it) },
) {
    require(visibleCount >= 1) { "visibleCount must be at least 1, was $visibleCount" }
    require(topFadeAlpha in 0f..1f) { "topFadeAlpha must be between 0 and 1, was $topFadeAlpha" }

    val realEntries = state.history.takeLast(visibleCount)
    val placeholderCount = visibleCount - realEntries.size
    val slots = List(placeholderCount) { index -> TickerSlot(key = index - placeholderCount, text = "") } +
        realEntries.map { TickerSlot(key = it.id, text = it.text) }

    LazyColumn(
        modifier = modifier.then(if (topFadeAlpha < 1f) Modifier.topFade(topFadeAlpha) else Modifier),
        userScrollEnabled = false,
    ) {
        items(slots, key = { it.key }) { slot ->
            Box(modifier = Modifier.animateItem()) {
                itemContent(slot.text)
            }
        }
    }
}

/** Masks the drawn content's alpha with a vertical gradient, from [topAlpha] at the top to fully opaque at the bottom. */
private fun Modifier.topFade(topAlpha: Float): Modifier =
    graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()
            drawRect(
                brush = Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = topAlpha),
                    1f to Color.Black,
                ),
                blendMode = BlendMode.DstIn,
            )
        }

/** A single row in the fixed-size [visibleCount]-row window; blank for reserved-but-unfilled rows. */
private data class TickerSlot(val key: Int, val text: String)

@Composable
private fun DefaultTickerItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
