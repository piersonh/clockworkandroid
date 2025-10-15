package com.wordco.clockworkandroid.edit_session_feature.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.core.ui.theme.LATO
import com.wordco.clockworkandroid.edit_session_feature.ui.model.UserEstimate

@Composable
fun EstimatePickerModal(
    estimatePickerState: EstimatePickerState,
    onValueChange: (UserEstimate) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = "Select Estimated Session Duration",
                    style = MaterialTheme.typography.labelMedium
                )
                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Hours",
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontFamily = LATO,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = TextStyle(
                                letterSpacing = 0.02.em // or use TextUnit(value, TextUnitType.Sp)
                            )
                        )

                        Spacer(Modifier.height(8.dp))

                        CircularWheelPicker(
                            state = estimatePickerState.hoursState,
                            modifier = Modifier.width(60.dp),
                            itemContent = { item, isSelected ->
                                val scale by animateFloatAsState(targetValue = if (isSelected) 1.25f else 1f)
                                val color by animateColorAsState(
                                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "%02d".format(item),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = color,
                                    modifier = Modifier.graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                )
                            }
                        )
                    }


                    Spacer(modifier = Modifier.width(16.dp))
                    Text(":", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.width(16.dp))


                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Minutes",
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontFamily = LATO,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = TextStyle(
                                letterSpacing = 0.02.em // or use TextUnit(value, TextUnitType.Sp)
                            )
                        )

                        Spacer(Modifier.height(8.dp))

                        CircularWheelPicker(
                            state = estimatePickerState.minutesState,
                            modifier = Modifier.width(60.dp),
                            itemContent = { item, isSelected ->
                                val scale by animateFloatAsState(targetValue = if (isSelected) 1.25f else 1f)
                                val color by animateColorAsState(
                                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "%02d".format(item),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = color,
                                    modifier = Modifier.graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                )
                            }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismissRequest) {
                        Text(
                            "Cancel",
                            fontFamily = LATO,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                    TextButton(onClick = {
                        onValueChange(estimatePickerState.currentValue)
                        onDismissRequest()
                    }
                    ) {
                        Text(
                            "OK",
                            fontFamily = LATO,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }
        }
    }
}


/**
 * State holder for the [EstimatePickerModal].
 *
 * This class holds the state for both the hours and minutes wheel pickers.
 *
 * @param hoursState The state for the hours picker.
 * @param minutesState The state for the minutes picker.
 */
class EstimatePickerState(
    val hoursState: WheelPickerState<Int>,
    val minutesState: WheelPickerState<Int>
) {
    /** The currently selected time, derived from the state of the two pickers. */
    val currentValue: UserEstimate by derivedStateOf {
        UserEstimate(
            minutesState.currentItem ?: 0,
            hoursState.currentItem ?: 0
        )
    }
}

/**
 * Creates and remembers a [EstimatePickerState].
 *
 * @param initialValue The initial time to display in the pickers.
 * @param itemHeight The height of each item in the wheel pickers.
 */
@Composable
fun rememberEstimatePickerState(
    initialValue: UserEstimate,
    itemHeight: Dp = 48.dp, // A sensible default
): EstimatePickerState {
    val hours = (0..99).reversed().toList()
    val minutes = (0..59).reversed().toList()

    val hoursState = rememberWheelPickerState(
        items = hours,
        initialItem = initialValue.hours,
        itemHeight = itemHeight
    )

    val minutesState = rememberWheelPickerState(
        items = minutes,
        initialItem = initialValue.minutes,
        itemHeight = itemHeight
    )

    return remember(hoursState, minutesState) {
        EstimatePickerState(hoursState, minutesState)
    }
}


@Preview
@Composable
private fun EstimatePickerModalPreview() {
    ClockworkTheme {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues)
            ) {
                val state = rememberEstimatePickerState(
                    initialValue = UserEstimate(0,15)
                )

                EstimatePickerModal(
                    estimatePickerState = state,
                    onValueChange = {},
                    onDismissRequest = {}
                )
            }
        }
    }
}