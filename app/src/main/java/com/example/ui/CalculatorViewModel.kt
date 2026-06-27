package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.HistoryEntity
import com.example.data.HistoryRepository
import com.example.util.MathEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.floor

class CalculatorViewModel(private val repository: HistoryRepository) : ViewModel() {

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()

    private val _isScientific = MutableStateFlow(false)
    val isScientific: StateFlow<Boolean> = _isScientific.asStateFlow()

    private val _isHistoryOpen = MutableStateFlow(false)
    val isHistoryOpen: StateFlow<Boolean> = _isHistoryOpen.asStateFlow()

    val historyList: StateFlow<List<HistoryEntity>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onBackspace() {
        val current = _expression.value
        if (current.isEmpty()) return

        val functions = listOf("sin(", "cos(", "tan(", "log(", "ln(", "sqrt(")
        var deleted = false
        for (func in functions) {
            if (current.endsWith(func)) {
                _expression.value = current.substring(0, current.length - func.length)
                deleted = true
                break
            }
        }
        if (!deleted) {
            _expression.value = current.dropLast(1)
        }

        updateLiveResult()
    }

    fun onClear() {
        _expression.value = ""
        _result.value = ""
    }

    fun appendInput(input: String) {
        val current = _expression.value

        // Prevent multiple consecutive operators
        val operators = setOf("+", "-", "×", "÷", "^")
        if (input in operators && current.isNotEmpty()) {
            val lastChar = current.last().toString()
            if (lastChar in operators) {
                // Replace last operator with the new one
                _expression.value = current.dropLast(1) + input
                updateLiveResult()
                return
            }
        }

        _expression.value += input
        updateLiveResult()
    }

    fun toggleScientific() {
        _isScientific.value = !_isScientific.value
    }

    fun toggleHistory() {
        _isHistoryOpen.value = !_isHistoryOpen.value
    }

    fun onEvaluate() {
        val currentExpr = _expression.value
        if (currentExpr.isEmpty()) return

        try {
            val evalResult = MathEvaluator.evaluate(currentExpr)
            val formatted = formatResult(evalResult)

            _expression.value = formatted
            _result.value = ""

            // Save to database
            viewModelScope.launch {
                repository.insert(
                    HistoryEntity(
                        expression = currentExpr,
                        result = formatted
                    )
                )
            }
        } catch (e: Exception) {
            _result.value = "Error"
        }
    }

    fun selectHistoryItem(item: HistoryEntity) {
        _expression.value = item.expression
        _result.value = item.result
        _isHistoryOpen.value = false
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clear()
        }
    }

    fun deleteHistoryItem(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    private fun updateLiveResult() {
        val currentExpr = _expression.value
        if (currentExpr.isEmpty()) {
            _result.value = ""
            return
        }

        // Only show live preview if the expression is valid and different from current input
        try {
            val lastChar = currentExpr.last()
            if (lastChar.isDigit() || lastChar == ')' || lastChar == '%' || lastChar == '!') {
                val evalResult = MathEvaluator.evaluate(currentExpr)
                val formatted = formatResult(evalResult)
                if (formatted != currentExpr) {
                    _result.value = formatted
                } else {
                    _result.value = ""
                }
            } else {
                // Don't show live result while user is in the middle of typing an operator or function
                _result.value = ""
            }
        } catch (e: Exception) {
            _result.value = "" // Don't show error during live typing, wait for explicit "="
        }
    }

    private fun formatResult(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "Error"
        return if (value == floor(value)) {
            value.toLong().toString()
        } else {
            // Keep maximum 10 decimal places and trim trailing zeros
            val formatted = String.format(java.util.Locale.US, "%.10f", value)
            formatted.replace("0+$".toRegex(), "").replace("\\.$".toRegex(), "")
        }
    }
}

class CalculatorViewModelFactory(private val repository: HistoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculatorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
