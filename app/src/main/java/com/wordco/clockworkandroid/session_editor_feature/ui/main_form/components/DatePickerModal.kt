package com.wordco.clockworkandroid.session_editor_feature.ui.main_form.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.wordco.clockworkandroid.core.ui.theme.LATO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    datePickerState: DatePickerState,
    onValueChange: (Long?) -> Unit,
    onDismissRequest: () -> Unit,
) {
    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onValueChange(datePickerState.selectedDateMillis)
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
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(
                    "Cancel",
                    fontFamily = LATO,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                selectedDayContainerColor = MaterialTheme.colorScheme.secondary,
                todayContentColor = MaterialTheme.colorScheme.secondary,
                todayDateBorderColor = MaterialTheme.colorScheme.secondary,
                selectedDayContentColor = MaterialTheme.colorScheme.onSecondary
            )
        )
    }
}