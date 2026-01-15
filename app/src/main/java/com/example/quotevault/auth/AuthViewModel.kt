package com.example.quotevault.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.quotes.QuoteRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: QuoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val currentUser = repository.getCurrentUser()
            _uiState.value = _uiState.value.copy(
                isLoggedIn = currentUser != null
            )
        }
    }

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun toggleIsLogin() {
        _uiState.value = _uiState.value.copy(isLogin = !_uiState.value.isLogin)
    }

    fun signIn(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = repository.signIn(_uiState.value.email, _uiState.value.password)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(isLoggedIn = true)
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(error = result.exceptionOrNull()?.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun signUp(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = repository.signUp(
                    _uiState.value.email,
                    _uiState.value.password,
                    _uiState.value.name
                )
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(isLoggedIn = true)
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(error = result.exceptionOrNull()?.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun resetPassword() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val result = repository.resetPassword(_uiState.value.email)
                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        showResetSuccess = true,
                        error = "Password reset email sent!"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(error = result.exceptionOrNull()?.message)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val isLogin: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showResetSuccess: Boolean = false,
    val isLoggedIn: Boolean = false
)