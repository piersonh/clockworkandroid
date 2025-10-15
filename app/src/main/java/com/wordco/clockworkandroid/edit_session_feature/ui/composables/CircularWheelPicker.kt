package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.drop
import kotlin.math.abs


/**
 * A circular, infinite wheel picker for selecting an item from a list.
 *
 * This component is stateless and relies on a [WheelPickerState] to manage its state.
 *
 * @param T The type of items in the list.
 * @param modifier The modifier to be applied to the picker.
 * @param state The state object that holds and controls the picker's state.
 * @param itemContent The composable lambda to display a single item. It provides the item
 * and its selection state.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> CircularWheelPicker(
    modifier: Modifier = Modifier,
    state: WheelPickerState<T>,
    itemContent: @Composable (item: T, isSelected: Boolean) -> Unit,
) {
    val totalHeight = state.itemHeight * state.numberOfDisplayedItems
    val haptics = LocalHapticFeedback.current

    // scroll to the initial designated item on first frame
    LaunchedEffect(state.initialItem) {
        state.scrollToItem(state.initialItem)
    }

    // Trigger haptic feedback when the centered item changes
    LaunchedEffect(state.centeredItemIndex) {
        snapshotFlow { state.centeredItemIndex }
            .drop(1) // ignore the initial composition
            .collect {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
    }


    LazyColumn(
        modifier = modifier.height(totalHeight),
        state = state.listState,
        flingBehavior = rememberSnapFlingBehavior(lazyListState = state.listState)
    ) {
        items(count = Int.MAX_VALUE, key = { it }) { index ->
            // prevent index out of bounds if items list is empty
            if (state.items.isEmpty()) return@items

            val itemIndex = index % state.items.size
            val isSelected = (index == state.centeredItemIndex)

            Box(
                modifier = Modifier
                    .height(state.itemHeight)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                itemContent(state.items[itemIndex], isSelected)
            }
        }
    }
}


/**
 * State holder for the [CircularWheelPicker].
 */
class WheelPickerState<T>(
    val items: List<T>,
    val initialItem: T,
    internal val listState: LazyListState,
    val itemHeight: Dp,
    val numberOfDisplayedItems: Int,
) {
    /** The index of the item currently in the center of the picker. */
    val centeredItemIndex by derivedStateOf {
        val layoutInfo = listState.layoutInfo
        val visibleItemsInfo = layoutInfo.visibleItemsInfo
        if (visibleItemsInfo.isEmpty()) {
            -1
        } else {
            // Calculate the center relative to the viewport's own coordinate system.
            val viewportCenter = layoutInfo.viewportSize.height / 2

            // Now we are comparing two relative positions, which is correct.
            visibleItemsInfo.minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }?.index ?: -1
        }
    }

    /** The item object currently selected in the picker. */
    val currentItem: T?
        get() = if (centeredItemIndex != -1 && items.isNotEmpty()) {
            items[centeredItemIndex % items.size]
        } else {
            null
        }

    /**
     * Animate scrolls to the specified item.
     */
    suspend fun scrollToItem(item: T) {
        if (items.isNotEmpty()) {
            val initialIndex = items.indexOf(item).coerceAtLeast(0)
            val centralIndex = (Int.MAX_VALUE / 2) - ((Int.MAX_VALUE / 2) % items.size)

            // FIX: Calculate how many items should be above the centered item.
            val paddingItems = (numberOfDisplayedItems - 1) / 2

            // Scroll to the index that places the target item in the middle.
            val targetIndex = centralIndex + initialIndex - paddingItems
            listState.scrollToItem(targetIndex, 0) // The offset is now always 0.
        }
    }
}

/**
 * Creates and remembers a [WheelPickerState].
 */
@Composable
fun <T> rememberWheelPickerState(
    items: List<T>,
    initialItem: T,
    itemHeight: Dp,
    numberOfDisplayedItems: Int = 3,
): WheelPickerState<T> {
    require(numberOfDisplayedItems % 2 != 0) { "numberOfDisplayedItems must be an odd number." }
    val listState = rememberLazyListState(0)

    return remember(items, initialItem, itemHeight, numberOfDisplayedItems) {
        WheelPickerState(
            items = items,
            initialItem = initialItem,
            listState = listState,
            itemHeight = itemHeight,
            numberOfDisplayedItems = numberOfDisplayedItems,
        )
    }
}