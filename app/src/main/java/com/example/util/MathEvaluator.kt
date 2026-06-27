package com.example.util

import kotlin.math.*

object MathEvaluator {
    fun evaluate(str: String): Double {
        val cleanStr = str
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", "3.141592653589793")
            .replace("e", "2.718281828459045")
            .replace("%", "*0.01")
            .replace("\\s".toRegex(), "")

        return Parser(cleanStr).parse()
    }

    private class Parser(private val cleanStr: String) {
        private var pos = -1
        private var ch = 0

        private fun nextChar() {
            ch = if (++pos < cleanStr.length) cleanStr[pos].code else -1
        }

        private fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < cleanStr.length) throw IllegalArgumentException("Unexpected character: " + cleanStr[pos].toChar())
            return x
        }

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                if (eat('+'.code)) x += parseTerm() // addition
                else if (eat('-'.code)) x -= parseTerm() // subtraction
                else return x
            }
        }

        private fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                if (eat('*'.code)) x *= parseFactor() // multiplication
                else if (eat('/'.code)) {
                    val divisor = parseFactor()
                    if (divisor == 0.0) throw ArithmeticException("Division by zero")
                    x /= divisor // division
                } else return x
            }
        }

        private fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor() // unary plus
            if (eat('-'.code)) return -parseFactor() // unary minus

            var x: Double
            val startPos = pos
            if (eat('('.code)) { // parentheses
                x = parseExpression()
                if (!eat(')'.code)) throw IllegalArgumentException("Missing closing parenthesis")
            } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) { // numbers
                while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                val numStr = cleanStr.substring(startPos, pos)
                x = numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $numStr")
            } else if (ch >= 'a'.code && ch <= 'z'.code || ch == '√'.code) { // functions
                if (ch == '√'.code) {
                    nextChar()
                    if (eat('('.code)) {
                        val arg = parseExpression()
                        if (!eat(')'.code)) throw IllegalArgumentException("Missing closing parenthesis")
                        if (arg < 0) throw IllegalArgumentException("Square root of negative number")
                        x = sqrt(arg)
                    } else {
                        // Support square root without parenthesis for simple numbers, e.g. √9
                        val numStart = pos
                        while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                        if (pos > numStart) {
                            val numStr = cleanStr.substring(numStart, pos)
                            val arg = numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number after square root")
                            if (arg < 0) throw IllegalArgumentException("Square root of negative number")
                            x = sqrt(arg)
                        } else {
                            throw IllegalArgumentException("Expected number or parentheses after √")
                        }
                    }
                } else {
                    while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                    val func = cleanStr.substring(startPos, pos)
                    if (eat('('.code)) {
                        val arg = parseExpression()
                        if (!eat(')'.code)) throw IllegalArgumentException("Missing closing parenthesis after function argument")
                        x = when (func) {
                            "sin" -> sin(Math.toRadians(arg))
                            "cos" -> cos(Math.toRadians(arg))
                            "tan" -> tan(Math.toRadians(arg))
                            "log" -> log10(arg)
                            "ln" -> ln(arg)
                            "sqrt" -> {
                                if (arg < 0) throw IllegalArgumentException("Square root of negative number")
                                sqrt(arg)
                            }
                            else -> throw IllegalArgumentException("Unknown function: $func")
                        }
                    } else {
                        throw IllegalArgumentException("Unknown keyword: $func")
                    }
                }
            } else {
                throw IllegalArgumentException("Unexpected character: " + ch.toChar())
            }

            // Check for power (^)
            if (eat('^'.code)) {
                x = x.pow(parseFactor())
            }

            // Check for factorial (!)
            if (eat('!'.code)) {
                x = factorial(x)
            }

            return x
        }

        private fun factorial(n: Double): Double {
            if (n < 0.0 || n > 170.0 || n != floor(n)) {
                throw IllegalArgumentException("Factorial is only defined for non-negative integers up to 170")
            }
            var result = 1.0
            for (i in 2..n.toInt()) {
                result *= i
            }
            return result
        }
    }
}
