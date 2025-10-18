package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
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

    val coroutineScope = rememberCoroutineScope()

    // scroll to the initial designated item on first frame
    LaunchedEffect(state.initialIndex, state.items) {
        state.scrollToListItemIndex(state.initialIndex)
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
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, // disable the ripple effect
                        onClick = {
                            if (!isSelected) {
                                coroutineScope.launch {
                                    state.scrollToAbsoluteIndex(index)
                                }
                            }
                        }
                    ),
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
    val initialIndex: Int,
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
     * Instantly scrolls to the specified index of the [LazyColumn] list.
     */
    suspend fun scrollToAbsoluteIndex(index: Int) {
        if (items.isNotEmpty()) {
            val paddingItems = (numberOfDisplayedItems - 1) / 2
            val targetIndex = (index - paddingItems).coerceAtLeast(0)

            listState.scrollToItem(targetIndex, 0)
        }
    }

    /**
     * Instantly scrolls to the specified index of the [WheelPickerState]'s [items].
     */
    suspend fun scrollToListItemIndex(listItemIndex: Int) {
        if (items.isNotEmpty()) {
            val centralIndex = (Int.MAX_VALUE / 2) - ((Int.MAX_VALUE / 2) % items.size)
            val absoluteIndex = centralIndex + listItemIndex
            scrollToAbsoluteIndex(absoluteIndex)
        }
    }
}

/**
 * Creates and remembers a [WheelPickerState].
 */
@Composable
fun <T> rememberWheelPickerState(
    items: List<T>,
    initialIndex: Int,
    itemHeight: Dp,
    numberOfDisplayedItems: Int = 3,
): WheelPickerState<T> {
    require(numberOfDisplayedItems % 2 != 0) { "numberOfDisplayedItems must be an odd number." }
    val listState = rememberLazyListState(0)

    return remember(items, initialIndex, itemHeight, numberOfDisplayedItems) {
        WheelPickerState(
            items = items,
            initialIndex = initialIndex,
            listState = listState,
            itemHeight = itemHeight,
            numberOfDisplayedItems = numberOfDisplayedItems,
        )
    }
}