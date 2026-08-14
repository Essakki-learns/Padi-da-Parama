package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TertiaryEmerald
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HeatmapView(
    activeDates: Set<String>,
    modifier: Modifier = Modifier
) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val weeks = 7
    val daysPerWeek = 7
    val totalDays = weeks * daysPerWeek

    // Generate days matrix for the last 7 weeks
    val todayCal = Calendar.getInstance()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("study_heatmap_view")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "STUDY & HABIT CONSISTENCY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Less", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(MaterialTheme.colorScheme.surfaceVariant))
                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(TertiaryEmerald.copy(alpha = 0.5f)))
                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(TertiaryEmerald))
                Text("More", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (w in 0 until weeks) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (d in 0 until daysPerWeek) {
                        val dayOffset = (weeks - 1 - w) * 7 + (daysPerWeek - 1 - d)
                        val cal = Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_YEAR, -dayOffset)
                        }
                        val dateKey = sdf.format(cal.time)
                        val isDone = activeDates.contains(dateKey)

                        val cellColor = if (isDone) {
                            TertiaryEmerald
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        }

                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(cellColor)
                        )
                    }
                }
            }
        }
    }
}
