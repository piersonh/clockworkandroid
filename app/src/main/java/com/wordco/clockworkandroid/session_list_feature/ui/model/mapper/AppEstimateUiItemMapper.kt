package com.wordco.clockworkandroid.session_list_feature.ui.model.mapper

import com.wordco.clockworkandroid.core.domain.model.AppEstimate
import com.wordco.clockworkandroid.session_list_feature.ui.model.AppEstimateUiItem

fun AppEstimate.toAppEstimateUiItem() : AppEstimateUiItem {
    return AppEstimateUiItem(
        low = low,
        high = high,
    )
}