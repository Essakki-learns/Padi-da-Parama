package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

val PRESET_FINANCE_CATEGORIES = listOf(
    "Food & Canteen",
    "Books & Stationery",
    "Tech & Subscriptions",
    "Travel & Metro",
    "Tuition & Fees",
    "Others"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    onDismiss: () -> Unit,
    onSave: (type: String, amount: Double, category: String, account: String, description: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var type by remember { mutableStateOf("EXPENSE") } // "EXPENSE" or "INCOME"
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Food & Canteen") }
    var selectedAccount by remember { mutableStateOf("UPI / GPay") }

    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val sheetBgColor = if (isDark) Color(0xFF1E293B) else Color.White

    val expenseActiveBg = TertiaryEmerald
    val incomeActiveBg = Color(0xFFF59E0B) // Warm Amber / Yellow

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = sheetBgColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF475569) else Color(0xFFCBD5E1))
                )
            }
        },
        modifier = modifier.testTag("add_transaction_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Log Transaction",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Top Section: Two Toggle Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Expense Toggle (Green with arrow down)
                val isExpense = type == "EXPENSE"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isExpense) expenseActiveBg else if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9))
                        .border(
                            width = if (isExpense) 0.dp else 1.dp,
                            color = if (isDark) Color(0xFF475569) else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { type = "EXPENSE" }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Expense",
                            tint = if (isExpense) Color.White else if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Expense",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isExpense) Color.White else if (isDark) Color.White else Color(0xFF0F172A)
                        )
                    }
                }

                // Income / Allowance Toggle (Yellow/Amber with arrow up)
                val isIncome = type == "INCOME"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isIncome) incomeActiveBg else if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9))
                        .border(
                            width = if (isIncome) 0.dp else 1.dp,
                            color = if (isDark) Color(0xFF475569) else Color(0xFFE2E8F0),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { type = "INCOME" }
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Income / Allowance",
                            tint = if (isIncome) Color.White else if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Income / Allowance",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isIncome) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isIncome) Color.White else if (isDark) Color.White else Color(0xFF0F172A)
                        )
                    }
                }
            }

            // Amount Input Field (Numeric, required, with ₹ prefix)
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                        amountText = input
                    }
                },
                label = { Text("Amount *") },
                prefix = {
                    Text(
                        "₹ ",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                placeholder = { Text("0.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                colors = appTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction_amount_field")
            )

            // Description / Merchant Input Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description / Merchant") },
                placeholder = { Text(if (type == "EXPENSE") "e.g. Canteen Lunch, Notebooks, Metro card" else "e.g. Monthly stipend, Parents allowance") },
                singleLine = true,
                colors = appTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("transaction_description_field")
            )

            // Category Picker: Horizontally Scrollable List with Highlight
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PRESET_FINANCE_CATEGORIES.forEach { categoryName ->
                        val isSelected = selectedCategory == categoryName
                        // Highlight with distinct yellow/amber accent when selected
                        val chipBg = if (isSelected) Color(0xFFFDE68A) else if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9)
                        val chipTextColor = if (isSelected) Color(0xFF78350F) else if (isDark) Color.White else Color(0xFF334155)
                        val chipBorderColor = if (isSelected) Color(0xFFF59E0B) else if (isDark) Color(0xFF475569) else Color(0xFFE2E8F0)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(chipBg)
                                .border(1.dp, chipBorderColor, RoundedCornerShape(20.dp))
                                .clickable { selectedCategory = categoryName }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = categoryName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = chipTextColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Bottom Actions: Cancel & Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("cancel_transaction_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text("Cancel")
                }

                val parsedAmt = amountText.toDoubleOrNull() ?: 0.0
                Button(
                    onClick = {
                        if (parsedAmt > 0) {
                            onSave(type, parsedAmt, selectedCategory, selectedAccount, description.trim())
                            onDismiss()
                        }
                    },
                    enabled = parsedAmt > 0,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(48.dp)
                        .testTag("save_transaction_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == "EXPENSE") TertiaryEmerald else incomeActiveBg,
                        contentColor = Color.White
                    )
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
