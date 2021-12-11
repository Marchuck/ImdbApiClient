package pl.marchuck.imdbapiclient.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class AbstractViewModel<ViewState : Any, SideEffect : Any>(
    private val initialState: ViewState
) : ViewModel() {

    private val _sideEffects = Channel<SideEffect>()
    val sideEffects = _sideEffects.receiveAsFlow()

    private val _viewState = MutableStateFlow(initialState)
    val viewState: Flow<ViewState> = _viewState

    protected fun pushState(reducer: (ViewState) -> ViewState) {
        val oldState = _viewState.value
        val newState = reducer.invoke(oldState)
        _viewState.value = newState
    }

    protected fun pushSideEffect(sideEffect: SideEffect) {
        viewModelScope.launch { _sideEffects.send(sideEffect) }
    }

    protected fun currentState(): ViewState = _viewState.value
}