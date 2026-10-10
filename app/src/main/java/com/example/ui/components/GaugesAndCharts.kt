package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*

/**
 * Enterprise Executive KPI Card for modern factory dashboards with support for
 * rich visual accessories like mini speedometers and donut rings.
 */
@Composable
fun ExecutiveKpiCard(
  title: String,
  valueText: String,
  targetText: String? = null,
  trendText: String? = null,
  isPositiveTrend: Boolean = true,
  progressFraction: Float? = null,
  accentColor: Color = Color(0xFF1976D2),
  endVisual: (@Composable () -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0)))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // Title row with subtle category indicator and trend badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.weight(1f, fill = false)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(accentColor)
          )
          Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF475569),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        if (trendText != null) {
          Surface(
            color = if (isPositiveTrend) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
            shape = RoundedCornerShape(4.dp)
          ) {
            Text(
              text = trendText,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = if (isPositiveTrend) Color(0xFF2E7D32) else Color(0xFFC62828),
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
              maxLines = 1,
              softWrap = false
            )
          }
        }
      }

      // Large bold primary metric + End Visual (Speedometer / Donut)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = valueText,
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF0F172A),
          letterSpacing = (-0.5).sp
        )

        if (endVisual != null) {
          endVisual()
        }
      }

      // Linear attainment progress bar if applicable (when no endVisual)
      if (progressFraction != null && endVisual == null) {
        val clamped = progressFraction.coerceIn(0f, 1f)
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(5.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(Color(0xFFF1F5F9))
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(clamped)
                .fillMaxHeight()
                .clip(RoundedCornerShape(3.dp))
                .background(accentColor)
            )
          }
        }
      }

      // Contextual target / benchmark label
      if (targetText != null) {
        Text(
          text = targetText,
          fontSize = 10.sp,
          fontWeight = FontWeight.Medium,
          color = Color(0xFF64748B),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}

/**
 * Compact Mini Speedometer Gauge for KPI stat cards
 */
@Composable
fun MiniSpeedometerGauge(
  value: Double,
  targetValue: Double = 75.0,
  modifier: Modifier = Modifier.size(54.dp, 36.dp)
) {
  val clampedValue = value.coerceIn(0.0, 100.0).toFloat()
  val fraction = clampedValue / 100f
  val startAngle = 150f
  val sweepAngle = 240f

  Canvas(modifier = modifier) {
    val strokeWidth = 5.dp.toPx()
    val arcSize = Size(size.width - strokeWidth, (size.height * 1.6f) - strokeWidth)
    val arcTopLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

    // Track
    drawArc(
      color = Color(0xFFE2E8F0),
      startAngle = startAngle,
      sweepAngle = sweepAngle,
      useCenter = false,
      topLeft = arcTopLeft,
      size = arcSize,
      style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    // Progress color
    val activeColor = when {
      clampedValue >= targetValue -> Color(0xFF10B981)
      clampedValue >= 60f -> Color(0xFF0284C7)
      else -> Color(0xFFEF4444)
    }

    drawArc(
      color = activeColor,
      startAngle = startAngle,
      sweepAngle = (fraction * sweepAngle).coerceIn(0.1f, sweepAngle),
      useCenter = false,
      topLeft = arcTopLeft,
      size = arcSize,
      style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )

    // Needle
    val centerX = size.width / 2f
    val centerY = size.height * 0.85f
    val needleLength = size.width * 0.38f
    val angleRad = Math.toRadians((startAngle + fraction * sweepAngle).toDouble())
    val endX = centerX + (needleLength * Math.cos(angleRad)).toFloat()
    val endY = centerY + (needleLength * Math.sin(angleRad)).toFloat()

    drawLine(
      color = Color(0xFF1E293B),
      start = Offset(centerX, centerY),
      end = Offset(endX, endY),
      strokeWidth = 2.dp.toPx(),
      cap = StrokeCap.Round
    )
    drawCircle(
      color = Color(0xFF0F172A),
      radius = 3.dp.toPx(),
      center = Offset(centerX, centerY)
    )
  }
}

/**
 * Compact Mini Donut Progress Ring for KPI stat cards
 */
@Composable
fun MiniDonutProgress(
  fraction: Float,
  accentColor: Color = Color(0xFF10B981),
  modifier: Modifier = Modifier.size(36.dp)
) {
  Canvas(modifier = modifier) {
    val strokeWidth = 4.5.dp.toPx()
    val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
    val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

    drawArc(
      color = Color(0xFFF1F5F9),
      startAngle = -90f,
      sweepAngle = 360f,
      useCenter = false,
      topLeft = topLeft,
      size = arcSize,
      style = Stroke(width = strokeWidth)
    )

    drawArc(
      color = accentColor,
      startAngle = -90f,
      sweepAngle = (fraction.coerceIn(0f, 1f) * 360f),
      useCenter = false,
      topLeft = topLeft,
      size = arcSize,
      style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
  }
}

/**
 * Combined Operator and Machine Allocation Stat Card
 */
@Composable
fun OperatorMachineMetricCard(
  operatorsCount: Int,
  machinesCount: Int,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0)))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFEFF6FF)),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Group, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(18.dp))
        }
        Column {
          Text("Operators", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
          Text("$operatorsCount", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
        }
      }

      Box(
        modifier = Modifier
          .width(1.dp)
          .height(30.dp)
          .background(Color(0xFFE2E8F0))
      )

      Row(
        modifier = Modifier.weight(1f).padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F3FF)),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(18.dp))
        }
        Column {
          Text("Machines", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
          Text("$machinesCount", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
        }
      }
    }
  }
}

/**
 * Grouped Bar Chart comparing Line Target (Royal Blue) vs Production Output (Emerald Green)
 * with clear values, background grid guidelines, and lines on X-axis.
 */
@Composable
fun TargetVsOutputBarChart(
  title: String,
  lines: List<LinePerformancePoint>,
  modifier: Modifier = Modifier,
  onLineClick: ((String) -> Unit)? = null
) {
  val displayLines = lines.ifEmpty {
    listOf(
      LinePerformancePoint("J1", targetPcs = 500, achievementPcs = 450),
      LinePerformancePoint("K1", targetPcs = 400, achievementPcs = 340),
      LinePerformancePoint("L1", targetPcs = 350, achievementPcs = 280),
      LinePerformancePoint("N1", targetPcs = 450, achievementPcs = null),
      LinePerformancePoint("N2", targetPcs = 620, achievementPcs = 560),
      LinePerformancePoint("O1", targetPcs = 520, achievementPcs = 480),
      LinePerformancePoint("P1", targetPcs = 480, achievementPcs = 420),
      LinePerformancePoint("Q1", targetPcs = 600, achievementPcs = 520)
    )
  }

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(2.dp),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Header with Legend
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
          Text(text = "Daily piece comparison across active lines", fontSize = 10.sp, color = Color(0xFF64748B))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(8.dp).background(Color(0xFF1E88E5), RoundedCornerShape(2.dp)))
            Text(text = "Target", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
          }
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(8.dp).background(Color(0xFF10B981), RoundedCornerShape(2.dp)))
            Text(text = "Output", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
          }
        }
      }

      // Chart body with grid and grouped bars
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(170.dp)
      ) {
        // Y-axis grid guidelines: 700, 500, 300, 0
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          listOf("700", "500", "300", "100", "0").forEach { label ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF94A3B8),
                modifier = Modifier.width(26.dp)
              )
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(1.dp)
                  .background(Color(0xFFF1F5F9))
              )
            }
          }
        }

        // Grouped bars layer
        Row(
          modifier = Modifier
            .fillMaxSize()
            .padding(start = 28.dp, bottom = 4.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.Bottom
        ) {
          val maxVal = 750f

          displayLines.forEach { line ->
            val targetHeight = ((line.targetPcs / maxVal) * 125f).coerceIn(8f, 125f).dp
            val outputHeight = (((line.achievementPcs ?: 0) / maxVal) * 125f).coerceIn(4f, 125f).dp

            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Bottom,
              modifier = Modifier
                .padding(horizontal = 2.dp)
                .then(if (onLineClick != null) Modifier.clickable { onLineClick(line.lineId) } else Modifier)
            ) {
              Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.Bottom
              ) {
                // Target Bar
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = "${line.targetPcs}",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                  )
                  Box(
                    modifier = Modifier
                      .width(13.dp)
                      .height(targetHeight)
                      .background(Color(0xFF1E88E5), RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                  )
                }

                // Output Bar
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  if (line.achievementPcs != null) {
                    Text(
                      text = "${line.achievementPcs}",
                      fontSize = 7.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF047857)
                    )
                    Box(
                      modifier = Modifier
                        .width(13.dp)
                        .height(outputHeight)
                        .background(Color(0xFF10B981), RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    )
                  } else {
                    Text(
                      text = "-",
                      fontSize = 7.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF94A3B8)
                    )
                    Box(
                      modifier = Modifier
                        .width(13.dp)
                        .height(8.dp)
                        .background(Color(0xFFCBD5E1), RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = line.lineId,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Line Efficiency Chart comparing Today Efficiency vs Monthly Efficiency
 */
@Composable
fun LineEfficiencyBarChart(
  title: String,
  lines: List<LinePerformancePoint>,
  modifier: Modifier = Modifier,
  onLineClick: ((String) -> Unit)? = null
) {
  val displayLines = lines.ifEmpty {
    listOf(
      LinePerformancePoint("J1", targetPcs = 500, todayEfficiencyPct = 70.0, monthlyEfficiencyPct = 68.0),
      LinePerformancePoint("K1", targetPcs = 400, todayEfficiencyPct = 66.0, monthlyEfficiencyPct = 64.0),
      LinePerformancePoint("L1", targetPcs = 350, todayEfficiencyPct = 64.0, monthlyEfficiencyPct = 65.0),
      LinePerformancePoint("N2", targetPcs = 620, todayEfficiencyPct = 76.0, monthlyEfficiencyPct = 72.0),
      LinePerformancePoint("O1", targetPcs = 520, todayEfficiencyPct = 72.0, monthlyEfficiencyPct = 70.0),
      LinePerformancePoint("P1", targetPcs = 480, todayEfficiencyPct = 68.0, monthlyEfficiencyPct = 66.0),
      LinePerformancePoint("Q1", targetPcs = 600, todayEfficiencyPct = 71.0, monthlyEfficiencyPct = 69.0),
      LinePerformancePoint("R1", targetPcs = 380, todayEfficiencyPct = 66.0, monthlyEfficiencyPct = 65.0)
    )
  }

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(2.dp),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
          Text(text = "Target Benchmark: 75% | Today vs Monthly Avg", fontSize = 10.sp, color = Color(0xFF64748B))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(8.dp).background(Color(0xFF0284C7), RoundedCornerShape(2.dp)))
            Text(text = "Today", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
          }
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(modifier = Modifier.size(8.dp).background(Color(0xFFF59E0B), RoundedCornerShape(2.dp)))
            Text(text = "Month", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
          }
        }
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(150.dp)
      ) {
        // Grid lines: 100%, 80%, 60%, 40%, 0%
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          listOf("100%", "80%", "60%", "40%", "0%").forEach { label ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = label,
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF94A3B8),
                modifier = Modifier.width(28.dp)
              )
              Box(
                modifier = Modifier
                  .weight(1f)
                  .height(1.dp)
                  .background(if (label == "80%") Color(0xFFBFDBFE) else Color(0xFFF1F5F9))
              )
            }
          }
        }

        Row(
          modifier = Modifier
            .fillMaxSize()
            .padding(start = 30.dp, bottom = 4.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.Bottom
        ) {
          displayLines.forEach { line ->
            val todayEff = line.todayEfficiencyPct ?: 0.0
            val monthEff = line.monthlyEfficiencyPct
            val todayH = ((todayEff / 100f) * 110f).coerceIn(4.0, 110.0).dp
            val monthH = ((monthEff / 100f) * 110f).coerceIn(4.0, 110.0).dp

            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Bottom,
              modifier = Modifier
                .padding(horizontal = 2.dp)
                .then(if (onLineClick != null) Modifier.clickable { onLineClick(line.lineId) } else Modifier)
            ) {
              Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.Bottom
              ) {
                // Today Eff Bar
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  if (line.todayEfficiencyPct != null) {
                    Text(
                      text = "${todayEff.toInt()}%",
                      fontSize = 7.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF0369A1)
                    )
                  }
                  Box(
                    modifier = Modifier
                      .width(11.dp)
                      .height(todayH)
                      .background(Color(0xFF0284C7), RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                  )
                }

                // Month Eff Bar
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = "${monthEff.toInt()}%",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB45309)
                  )
                  Box(
                    modifier = Modifier
                      .width(11.dp)
                      .height(monthH)
                      .background(Color(0xFFF59E0B), RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                  )
                }
              }

              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = line.lineId,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Top 5 Defects Horizontal Pareto Bar Chart matching Image 1
 */
@Composable
fun TopDefectsBarChart(
  title: String,
  defects: List<DefectSummary>,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(2.dp),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
        Text(text = "Defects Count & DHU %", fontSize = 10.sp, color = Color(0xFF64748B))
      }

      val colors = listOf(
        Color(0xFFEA580C),
        Color(0xFF0284C7),
        Color(0xFF0D9488),
        Color(0xFF4F46E5),
        Color(0xFF64748B)
      )

      val defaultDefects = if (defects.isNotEmpty()) defects else listOf(
        DefectSummary("Broken stitch", 45, 0.42),
        DefectSummary("Puckering", 34, 0.31),
        DefectSummary("Needle mark", 28, 0.25),
        DefectSummary("Open seam", 22, 0.19),
        DefectSummary("Oil spot / dirt", 16, 0.14)
      )

      val maxCount = defaultDefects.firstOrNull()?.count ?: 50

      defaultDefects.take(5).forEachIndexed { index, defect ->
        val barColor = colors[index % colors.size]
        val fraction = (defect.count.toFloat() / maxCount.toFloat()).coerceIn(0.12f, 1f)

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Surface(
                color = barColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(3.dp)
              ) {
                Text(
                  text = "#${index + 1}",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = barColor,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
              Text(
                text = defect.defectType,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF334E68)
              )
            }

            Text(
              text = "${defect.count} pcs (${defect.dhu}% DHU)",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0F172A)
            )
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(9.dp)
              .clip(RoundedCornerShape(4.dp))
              .background(Color(0xFFF1F5F9))
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth(fraction = fraction)
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(barColor)
            )
          }
        }
      }

      Surface(
        color = Color(0xFFEFF6FF),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Quality Standard: Overall OQL target ≤ 2.0% • DHU target ≤ 1.5%",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1D4ED8)
          )
        }
      }
    }
  }
}

/**
 * Industrial Radial Speedometer Gauge with needle indicator, multi-colored zones,
 * and target benchmark tick.
 */
@Composable
fun SpeedometerGauge(
  value: Double,
  targetValue: Double = 75.0,
  minVal: Double = 0.0,
  maxVal: Double = 100.0,
  modifier: Modifier = Modifier,
  gaugeHeight: Int = 135
) {
  val clampedValue = value.coerceIn(minVal, maxVal).toFloat()
  val fraction = ((clampedValue - minVal) / (maxVal - minVal)).toFloat()
  val targetFraction = ((targetValue.coerceIn(minVal, maxVal) - minVal) / (maxVal - minVal)).toFloat()

  val startAngle = 150f
  val sweepAngle = 240f

  Box(
    modifier = modifier.height(gaugeHeight.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 6.dp)) {
      val strokeWidth = 14.dp.toPx()
      val arcSize = Size(size.width - strokeWidth, (size.height * 1.6f) - strokeWidth)
      val arcTopLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

      // Background track
      drawArc(
        color = Color(0xFFE2E8F0),
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = arcTopLeft,
        size = arcSize,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
      )

      // Color zones
      // Zone 1: 0% - 60% Red
      val z1Sweep = 0.60f * sweepAngle
      drawArc(
        color = Color(0xFFEF4444),
        startAngle = startAngle,
        sweepAngle = z1Sweep,
        useCenter = false,
        topLeft = arcTopLeft,
        size = arcSize,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
      )

      // Zone 2: 60% - 75% Amber
      val z2Sweep = 0.15f * sweepAngle
      drawArc(
        color = Color(0xFFF59E0B),
        startAngle = startAngle + z1Sweep,
        sweepAngle = z2Sweep,
        useCenter = false,
        topLeft = arcTopLeft,
        size = arcSize,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
      )

      // Zone 3: 75% - 85% Sky Blue
      val z3Sweep = 0.10f * sweepAngle
      drawArc(
        color = Color(0xFF0284C7),
        startAngle = startAngle + z1Sweep + z2Sweep,
        sweepAngle = z3Sweep,
        useCenter = false,
        topLeft = arcTopLeft,
        size = arcSize,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
      )

      // Zone 4: 85% - 100% Green
      val z4Sweep = 0.15f * sweepAngle
      drawArc(
        color = Color(0xFF10B981),
        startAngle = startAngle + z1Sweep + z2Sweep + z3Sweep,
        sweepAngle = z4Sweep,
        useCenter = false,
        topLeft = arcTopLeft,
        size = arcSize,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
      )

      // Center pivot point
      val centerX = size.width / 2f
      val centerY = size.height * 0.82f
      val needleLength = size.width * 0.36f

      // Target indicator tick
      val targetAngleDeg = startAngle + (targetFraction * sweepAngle)
      val targetRad = Math.toRadians(targetAngleDeg.toDouble())
      val tickInner = needleLength * 0.85f
      val tickOuter = needleLength * 1.18f
      drawLine(
        color = Color(0xFF0F172A),
        start = Offset(centerX + (tickInner * Math.cos(targetRad)).toFloat(), centerY + (tickInner * Math.sin(targetRad)).toFloat()),
        end = Offset(centerX + (tickOuter * Math.cos(targetRad)).toFloat(), centerY + (tickOuter * Math.sin(targetRad)).toFloat()),
        strokeWidth = 3.dp.toPx(),
        cap = StrokeCap.Round
      )

      // Needle pointer
      val currentAngleDeg = startAngle + (fraction * sweepAngle)
      val angleRad = Math.toRadians(currentAngleDeg.toDouble())
      val needleEndX = centerX + (needleLength * Math.cos(angleRad)).toFloat()
      val needleEndY = centerY + (needleLength * Math.sin(angleRad)).toFloat()

      drawLine(
        color = Color(0xFF0F172A),
        start = Offset(centerX, centerY),
        end = Offset(needleEndX, needleEndY),
        strokeWidth = 3.5.dp.toPx(),
        cap = StrokeCap.Round
      )

      // Pivot Cap
      drawCircle(
        color = Color(0xFF0F172A),
        radius = 8.dp.toPx(),
        center = Offset(centerX, centerY)
      )
      drawCircle(
        color = if (clampedValue >= targetValue) Color(0xFF10B981) else Color(0xFFF59E0B),
        radius = 4.dp.toPx(),
        center = Offset(centerX, centerY)
      )
    }

    // Centered Digital Readout
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(bottom = 2.dp)
    ) {
      Text(
        text = "${String.format("%.1f", value)}%",
        fontSize = 22.sp,
        fontWeight = FontWeight.Black,
        color = Color(0xFF0F172A)
      )
      Text(
        text = "TARGET: ${targetValue.toInt()}%",
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF64748B)
      )
    }
  }
}

/**
 * Floor Efficiency Speedometer Gauge Card with line dials row
 */
@Composable
fun SpeedometerGaugeCard(
  title: String,
  currentEfficiency: Double,
  targetEfficiency: Double = 75.0,
  lines: List<LinePerformancePoint> = emptyList(),
  modifier: Modifier = Modifier,
  onLineClick: ((String) -> Unit)? = null
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(2.dp),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
          Text(text = "Speedometer dial with needle & multi-zone thresholds", fontSize = 10.sp, color = Color(0xFF64748B))
        }

        Surface(
          color = if (currentEfficiency >= targetEfficiency) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = if (currentEfficiency >= targetEfficiency) "ON TARGET" else "BELOW TARGET",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (currentEfficiency >= targetEfficiency) Color(0xFF166534) else Color(0xFFB45309),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      // Large Speedometer Dial
      SpeedometerGauge(
        value = currentEfficiency,
        targetValue = targetEfficiency,
        modifier = Modifier.fillMaxWidth().height(140.dp)
      )

      // Speedometer Zone Legend
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(modifier = Modifier.size(6.dp).background(Color(0xFFEF4444), CircleShape))
          Text("<60% Low", fontSize = 8.sp, color = Color(0xFF64748B))
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(modifier = Modifier.size(6.dp).background(Color(0xFFF59E0B), CircleShape))
          Text("60-75% Caution", fontSize = 8.sp, color = Color(0xFF64748B))
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(modifier = Modifier.size(6.dp).background(Color(0xFF0284C7), CircleShape))
          Text("75-85% Target", fontSize = 8.sp, color = Color(0xFF64748B))
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(modifier = Modifier.size(6.dp).background(Color(0xFF10B981), CircleShape))
          Text(">85% High", fontSize = 8.sp, color = Color(0xFF64748B))
        }
      }

      // Line-by-Line Mini-Speedometers
      if (lines.isNotEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFF1F5F9))
        )
        Text(
          text = "Line-by-Line Speedometer Dials (Tap line to open deep-dive):",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF475569)
        )
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          lines.forEach { line ->
            val eff = line.todayEfficiencyPct ?: 0.0
            Surface(
              color = Color(0xFFF8FAFC),
              shape = RoundedCornerShape(8.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
              modifier = Modifier.clickable { onLineClick?.invoke(line.lineId) }
            ) {
              Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
              ) {
                Text(line.lineId, fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
                MiniSpeedometerGauge(value = eff, targetValue = line.targetEfficiencyPct)
                Text(
                  text = "${eff.toInt()}%",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (eff >= line.targetEfficiencyPct) Color(0xFF16A34A) else Color(0xFFD97706)
                )
              }
            }
          }
        }
      }
    }
  }
}

/**
 * Donut / Ring Chart showing Output Attainment & Variance Breakdown
 */
@Composable
fun DonutAttainmentCard(
  title: String,
  targetPcs: Int,
  actualPcs: Int,
  modifier: Modifier = Modifier
) {
  val fraction = if (targetPcs > 0) (actualPcs.toFloat() / targetPcs.toFloat()).coerceIn(0f, 1f) else 0f
  val percent = (fraction * 100).toInt()
  val gap = targetPcs - actualPcs

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(2.dp),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
          Text(text = "Donut chart of pieces attained vs remaining gap", fontSize = 10.sp, color = Color(0xFF64748B))
        }
        Surface(
          color = if (percent >= 80) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = if (percent >= 80) "HEALTHY RUN" else "OUTPUT GAP",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (percent >= 80) Color(0xFF166534) else Color(0xFFB45309),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        // Large Donut Canvas
        Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
          Canvas(modifier = Modifier.size(110.dp)) {
            val strokeWidth = 14.dp.toPx()
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

            // Background track (Remaining gap)
            drawArc(
              color = Color(0xFFFED7AA),
              startAngle = -90f,
              sweepAngle = 360f,
              useCenter = false,
              topLeft = topLeft,
              size = arcSize,
              style = Stroke(width = strokeWidth)
            )

            // Completed arc
            drawArc(
              color = Color(0xFF10B981),
              startAngle = -90f,
              sweepAngle = fraction * 360f,
              useCenter = false,
              topLeft = topLeft,
              size = arcSize,
              style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$percent%", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
            Text("ATTAINED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
          }
        }

        // Legend & Metrics Column
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(start = 12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(modifier = Modifier.size(10.dp).background(Color(0xFF10B981), RoundedCornerShape(2.dp)))
            Column {
              Text("Achieved Output", fontSize = 10.sp, color = Color(0xFF64748B))
              Text("$actualPcs pcs", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(modifier = Modifier.size(10.dp).background(Color(0xFF0284C7), RoundedCornerShape(2.dp)))
            Column {
              Text("Target Goal", fontSize = 10.sp, color = Color(0xFF64748B))
              Text("$targetPcs pcs", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(modifier = Modifier.size(10.dp).background(Color(0xFFF97316), RoundedCornerShape(2.dp)))
            Column {
              Text("Production Gap", fontSize = 10.sp, color = Color(0xFF64748B))
              Text("${if (gap > 0) "-$gap" else "+${-gap}"} pcs", fontSize = 13.sp, fontWeight = FontWeight.Black, color = if (gap > 0) Color(0xFFDC2626) else Color(0xFF16A34A))
            }
          }
        }
      }
    }
  }
}

/**
 * Quality Defects Donut Chart (Top 5 Pareto Breakdown)
 */
@Composable
fun ParetoDonutDefectsCard(
  title: String,
  defects: List<DefectSummary>,
  modifier: Modifier = Modifier
) {
  val sliceColors = listOf(
    Color(0xFFDC2626), // Red
    Color(0xFFEA580C), // Orange
    Color(0xFFD97706), // Amber
    Color(0xFF2563EB), // Blue
    Color(0xFF7C3AED), // Purple
    Color(0xFF64748B)  // Slate
  )

  val totalDefects = defects.sumOf { it.count }.coerceAtLeast(1)

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(2.dp),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
          Text(text = "Donut breakdown of Top 5 defects and Pareto distribution", fontSize = 10.sp, color = Color(0xFF64748B))
        }
        Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(4.dp)) {
          Text("PARETO DONUT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Donut Canvas
        Box(modifier = Modifier.size(115.dp), contentAlignment = Alignment.Center) {
          Canvas(modifier = Modifier.size(105.dp)) {
            var startAngle = -90f
            defects.take(5).forEachIndexed { idx, d ->
              val pct = (d.count.toFloat() / totalDefects.toFloat())
              val sweep = pct * 360f
              val color = sliceColors[idx % sliceColors.size]
              drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = 22.dp.toPx(), cap = StrokeCap.Butt)
              )
              startAngle += sweep
            }
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Top 5", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
            Text("DEFECTS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
          }
        }

        // Defect list legend
        Column(
          modifier = Modifier.weight(1f).padding(start = 12.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          defects.take(5).forEachIndexed { idx, d ->
            val pct = ((d.count.toFloat() / totalDefects.toFloat()) * 100).toInt()
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.size(7.dp).background(sliceColors[idx % sliceColors.size], CircleShape))
                Text(d.defectType, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), maxLines = 1, overflow = TextOverflow.Ellipsis)
              }
              Text("$pct% (${d.count})", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            }
          }
        }
      }
    }
  }
}

/**
 * Live Line Performance Matrix Table
 * Displays full industrial status of all production lines.
 */
@Composable
fun LiveLinePerformanceTable(
  lines: List<LinePerformancePoint>,
  onLineClick: (LinePerformancePoint) -> Unit = {},
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(2.dp),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Line Performance Matrix",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A)
          )
          Text(
            text = "${lines.size} Real Lines • Item Name & SMV Tracking • ⇄ Scroll table",
            fontSize = 10.sp,
            color = Color(0xFF64748B)
          )
        }

        Surface(
          color = Color(0xFFE0F2FE),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = "Live Sync",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0369A1),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
          )
        }
      }

      // Horizontally scrollable container for crisp display on all screen sizes
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .widthIn(min = 540.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Table Header Row
          Surface(
            color = Color(0xFF0F172A),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 7.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("Line", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f))
              Text("Item Name", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.6f))
              Text("SMV", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
              Text("Target", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
              Text("Actual", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
              Text("Tgt %", color = Color(0xFFFBBF24), fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
              Text("Eff %", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
              Text("Status", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.weight(1.3f))
            }
          }

          // Table Rows
          lines.forEachIndexed { index, line ->
            val isEven = index % 2 == 0
            val hasReported = line.achievementPcs != null
            val output = line.achievementPcs ?: 0
            val variance = output - line.targetPcs
            val eff = line.todayEfficiencyPct ?: 0.0

            Surface(
              color = if (isEven) Color(0xFFFAFAFA) else Color.White,
              shape = RoundedCornerShape(6.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onLineClick(line) }
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 8.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Line Number (Clean - no style number clutter)
                Text(
                  text = line.lineId,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF0F172A),
                  modifier = Modifier.weight(1.1f)
                )

                // Item Name Column
                Text(
                  text = line.itemName,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFF334155),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.weight(1.6f)
                )

                // SMV Column (Standard Minute Value)
                Text(
                  text = String.format("%.2f", line.smv),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF0284C7),
                  textAlign = TextAlign.End,
                  modifier = Modifier.weight(1.0f)
                )

                // Target Pcs
                Text(
                  text = "${line.targetPcs}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFF475569),
                  textAlign = TextAlign.End,
                  modifier = Modifier.weight(1.0f)
                )

                // Actual Pcs
                Text(
                  text = if (hasReported) "$output" else "--",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (variance >= 0) Color(0xFF059669) else Color(0xFFD97706),
                  textAlign = TextAlign.End,
                  modifier = Modifier.weight(1.0f)
                )

                // Target Efficiency % (Morning setup)
                Text(
                  text = "${line.targetEfficiencyPct.toInt()}%",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFFB45309),
                  textAlign = TextAlign.End,
                  modifier = Modifier.weight(1.0f)
                )

                // Actual Efficiency %
                Text(
                  text = if (hasReported) "${eff.toInt()}%" else "--",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = when {
                    !hasReported -> Color(0xFF94A3B8)
                    eff >= line.targetEfficiencyPct -> Color(0xFF059669)
                    eff >= (line.targetEfficiencyPct - 10.0) -> Color(0xFFD97706)
                    else -> Color(0xFFDC2626)
                  },
                  textAlign = TextAlign.End,
                  modifier = Modifier.weight(1.0f)
                )

                // Status chip
                Box(
                  modifier = Modifier.weight(1.3f),
                  contentAlignment = Alignment.Center
                ) {
                  when {
                    line.openIncidentsCount > 0 -> {
                      Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(4.dp)) {
                        Text(
                          text = "INCIDENT",
                          fontSize = 8.sp,
                          fontWeight = FontWeight.Black,
                          color = Color(0xFFDC2626),
                          modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                      }
                    }
                    !hasReported -> {
                      Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(4.dp)) {
                        Text(
                          text = "PENDING",
                          fontSize = 8.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF64748B),
                          modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                      }
                    }
                    eff >= line.targetEfficiencyPct -> {
                      Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                        Text(
                          text = "ON TARGET",
                          fontSize = 8.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF166534),
                          modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                      }
                    }
                    else -> {
                      Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                        Text(
                          text = "UNDER",
                          fontSize = 8.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF92400E),
                          modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
