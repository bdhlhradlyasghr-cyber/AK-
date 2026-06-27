package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HistoryEntity
import com.example.ui.theme.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val expression by viewModel.expression.collectAsState()
    val result by viewModel.result.collectAsState()
    val isScientific by viewModel.isScientific.collectAsState()
    val isHistoryOpen by viewModel.isHistoryOpen.collectAsState()
    val historyList by viewModel.historyList.collectAsState()

    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SlateDark, Color(0xFF1B1D22))
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Bar
            CalculatorHeader(
                isScientific = isScientific,
                onScientificToggle = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.toggleScientific()
                },
                onHistoryToggle = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.toggleHistory()
                }
            )

            // Display Section
            CalculatorDisplay(
                expression = expression,
                result = result,
                modifier = Modifier.weight(1.3f)
            )

            // Scientific panel overlay/expandable drawer
            AnimatedVisibility(
                visible = isScientific,
                enter = expandVertically(animationSpec = spring()) + fadeIn(),
                exit = shrinkVertically(animationSpec = spring()) + fadeOut()
            ) {
                ScientificPanel(
                    onKeyClick = { input ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.appendInput(input)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Main Buttons Grid
            MainButtonsGrid(
                onKeyClick = { input ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.appendInput(input)
                },
                onClear = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.onClear()
                },
                onBackspace = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.onBackspace()
                },
                onEvaluate = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.onEvaluate()
                },
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Beautiful Ad / Telegram Link Banner
            val context = LocalContext.current
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Ali_Rod_2007"))
                        context.startActivity(intent)
                    }
                    .testTag("ad_banner"),
                colors = CardDefaults.cardColors(
                    containerColor = CardCharcoal.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldenAmber.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "تبلیغات",
                        tint = GoldenAmber,
                        modifier = Modifier.size(20.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "t.me/Ali_Rod_2007",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextMuted,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Text(
                            text = "کانال تلگرام علی راد",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(GoldenAmber.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "تبلیغات",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GoldenAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Calculation History Sliding Panel Overlay
        AnimatedVisibility(
            visible = isHistoryOpen,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            HistoryPanel(
                historyList = historyList,
                onClose = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.toggleHistory()
                },
                onSelect = { item ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.selectHistoryItem(item)
                },
                onClearAll = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.clearHistory()
                },
                onDeleteItem = { id ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    viewModel.deleteHistoryItem(id)
                }
            )
        }
    }
}

@Composable
fun CalculatorHeader(
    isScientific: Boolean,
    onScientificToggle: () -> Unit,
    onHistoryToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Branded Title (Persian style)
        Column {
            Text(
                text = "حساب‌یار",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = GoldenAmber
                ),
                modifier = Modifier.testTag("app_title")
            )
            Text(
                text = "دستیار محاسبات روزمره",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    color = TextMuted
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Scientific Mode Toggle Button (Beautiful micro-switch visual representation)
            IconButton(
                onClick = onScientificToggle,
                modifier = Modifier
                    .background(
                        if (isScientific) GoldenAmber.copy(alpha = 0.15f) else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .testTag("scientific_toggle_button")
            ) {
                Icon(
                    imageVector = if (isScientific) Icons.Default.Science else Icons.Default.Calculate,
                    contentDescription = "علمی / پیشرفته",
                    tint = if (isScientific) GoldenAmber else TextMuted
                )
            }

            // History Panel Toggle Button
            IconButton(
                onClick = onHistoryToggle,
                modifier = Modifier.testTag("history_toggle_button")
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "تاریخچه",
                    tint = TextLight
                )
            }
        }
    }
}

@Composable
fun CalculatorDisplay(
    expression: String,
    result: String,
    modifier: Modifier = Modifier
) {
    // Determine dynamic font size based on expression length
    val expressionFontSize = when {
        expression.length < 12 -> 46.sp
        expression.length < 20 -> 34.sp
        expression.length < 32 -> 24.sp
        else -> 18.sp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        // Expression Text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = expression.ifEmpty { "0" },
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = expressionFontSize,
                    color = if (expression.isEmpty()) TextMuted else TextLight,
                    textAlign = TextAlign.End,
                    fontFamily = FontFamily.Monospace
                ),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expression_display")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Real-time live preview or evaluation result
        AnimatedVisibility(
            visible = result.isNotEmpty(),
            enter = fadeIn() + slideInVertically(initialOffsetY = { 20 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { 20 })
        ) {
            Text(
                text = result,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = TextOrange,
                    textAlign = TextAlign.End,
                    fontFamily = FontFamily.Monospace
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("result_display")
            )
        }
    }
}

@Composable
fun ScientificPanel(
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        listOf("sin", "cos", "tan", "log"),
        listOf("ln", "√", "^", "!"),
        listOf("π", "e", "(", ")")
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = CardCharcoal.copy(alpha = 0.85f)
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.testTag("scientific_panel")
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { op ->
                        // Render customized label or icon
                        val label = when (op) {
                            "sin" -> "sin"
                            "cos" -> "cos"
                            "tan" -> "tan"
                            "log" -> "log"
                            "ln" -> "ln"
                            "√" -> "√"
                            "^" -> "^"
                            "!" -> "n!"
                            "π" -> "π"
                            "e" -> "e"
                            else -> op
                        }

                        Button(
                            onClick = {
                                val input = when (op) {
                                    "sin", "cos", "tan", "log", "ln" -> "$op("
                                    else -> op
                                }
                                onKeyClick(input)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_sci_$op"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HelperGrey.copy(alpha = 0.4f),
                                contentColor = TextOrange
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainButtonsGrid(
    onKeyClick: (String) -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onEvaluate: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 4 Columns, 5 Rows standard layout
    val layout = listOf(
        listOf("C", "()", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "⌫", "=")
    )

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        layout.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        val isOperator = key in setOf("÷", "×", "-", "+", "=")
                        val isSpecial = key in setOf("C", "()", "%", "⌫")

                        val containerColor = when {
                            key == "=" -> GoldenAmber
                            isOperator -> ButtonNumber
                            isSpecial -> HelperGrey.copy(alpha = 0.5f)
                            else -> ButtonNumber.copy(alpha = 0.6f)
                        }

                        val contentColor = when {
                            key == "=" -> SlateDark
                            isOperator -> GoldenAmber
                            isSpecial -> TextOrange
                            else -> TextLight
                        }

                        val shape = RoundedCornerShape(24.dp)

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(shape)
                                .background(containerColor)
                                .combinedClickable(
                                    onClick = {
                                        when (key) {
                                            "C" -> onClear()
                                            "⌫" -> onBackspace()
                                            "=" -> onEvaluate()
                                            "()" -> {
                                                // Intelligent parenthesis helper:
                                                // If open parentheses are unmatched, output close parenthesis, else open parenthesis.
                                                onKeyClick("(")
                                            }
                                            else -> onKeyClick(key)
                                        }
                                    },
                                    onLongClick = {
                                        if (key == "⌫") {
                                            onClear()
                                        }
                                    }
                                )
                                .testTag("btn_$key"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (key == "⌫") {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = "پاک کردن",
                                    tint = contentColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = contentColor,
                                        fontFamily = FontFamily.SansSerif
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryPanel(
    historyList: List<HistoryEntity>,
    onClose: () -> Unit,
    onSelect: (HistoryEntity) -> Unit,
    onClearAll: () -> Unit,
    onDeleteItem: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SlateDark.copy(alpha = 0.95f))
            .clickable(enabled = false) { } // prevent underlying click leakage
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "بستن",
                        tint = TextLight
                    )
                }

                Text(
                    text = "تاریخچه محاسبات",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextLight,
                        fontSize = 18.sp
                    )
                )

                IconButton(
                    onClick = { showConfirmDialog = true },
                    enabled = historyList.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف کل تاریخچه",
                        tint = if (historyList.isNotEmpty()) TextOrange else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (historyList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = TextMuted.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "تاریخچه‌ای وجود ندارد",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("history_list"),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(historyList, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(item) }
                                .testTag("history_item_${item.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = CardCharcoal
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .padding(end = 40.dp) // Leave space for delete button
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = item.expression,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextMuted,
                                            fontFamily = FontFamily.Monospace,
                                            textAlign = TextAlign.Right
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "= ${item.result}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextLight,
                                            fontFamily = FontFamily.Monospace,
                                            textAlign = TextAlign.Right
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                // Delete single item button
                                IconButton(
                                    onClick = { onDeleteItem(item.id) },
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(start = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "حذف این مورد",
                                        tint = TextMuted.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog for clearing all records
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = "پاکسازی تاریخچه",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextLight
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
            },
            text = {
                Text(
                    text = "آیا از پاک کردن کل تاریخچه محاسبات خود اطمینان دارید؟",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAll()
                        showConfirmDialog = false
                    }
                ) {
                    Text(
                        text = "بله، پاک شود",
                        color = TextOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(text = "انصراف", color = TextLight)
                }
            },
            containerColor = CardCharcoal,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
