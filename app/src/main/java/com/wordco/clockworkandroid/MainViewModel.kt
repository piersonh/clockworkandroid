package com.wordco.clockworkandroid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.wordco.clockworkandroid.core.domain.use_case.ManageFirstLaunchUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    private val manageFirstLaunchUseCase: ManageFirstLaunchUseCase
) : ViewModel() {

    private val _isFirstLaunch = MutableStateFlow(checkFirstLaunchStatus())
    val isFirstLaunch = _isFirstLaunch.asStateFlow()

    private fun checkFirstLaunchStatus(): Boolean {
        return manageFirstLaunchUseCase.isFirstLaunch()
    }

    fun onFirstLaunchHandled() {
        manageFirstLaunchUseCase.setCompleted()
        _isFirstLaunch.value = false
    }

    companion object {


        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                val appContainer = (this[APPLICATION_KEY] as MainApplication).appContainer

                MainViewModel(
                    manageFirstLaunchUseCase = appContainer.manageFirstLaunchUseCase
                )
            }
        }
    }
}