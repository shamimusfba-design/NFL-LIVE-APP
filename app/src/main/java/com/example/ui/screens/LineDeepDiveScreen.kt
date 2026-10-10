package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NflRepository
import com.example.model.PeriodScope
import com.example.ui.components.SpeedometerGauge
import com.example.ui.theme.NflRoyalBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineDeepDiveScreen(
  repository: NflRepository,
  lineId: String,
  onNavigateBack: () -> Unit
) {
  var selectedPeriod by remember { mutableStateOf(PeriodScope.TODAY) }
  var selectedLineId by remember { mutableStateOf(lineId) }
  var showLineSwitcher by remember { mutableStateOf(false) }

  val lineSetup = remember(selectedLineId) { repository.getLineSetup(selectedLineId) }
  val lineObj = remember(selectedLineId) { repository.allLinesMaster.find { it.id == selectedLineId } }
  val floorName = lineObj?.floor ?: lineSetup.floor

  val monthlyTarget = remember(floorName) { repository.getMonthlyTarget(floorName) }

  val hourlyProd = remember(selectedLineId, selectedPeriod) {
    repository.getHourlyProductionData(selectedLineId, selectedPeriod)
  }

  val hourlyQuality = remember(selectedLineId, selectedPeriod) {
    repository.getHourlyQualityData(selectedLineId, selectedPeriod)
  }

  val paretoDefects = remember(selectedLineId) {
    repository.getParetoDefects(selectedLineId)
  }

  // Summary Metrics
  val totalTarget = hourlyProd.lastOrNull()?.totalTarget ?: lineSetup.targetDailyPcs
  val totalAchieve = hourlyProd.lastOrNull()?.totalAchieve ?: 420
  val overallAchievePct = if (totalTarget > 0) Math.round((totalAchieve.toDouble() / totalTarget) * 1000.0) / 10.0 else 0.0
  val todayEffPct = if (hourlyProd.isNotEmpty()) hourlyProd.filter { it.efficiencyPct > 0 }.map { it.efficiencyPct }.average().let { if (it.isNaN()) 68.5 else Math.round(it * 10.0) / 10.0 } else 68.5

  val avgDhu = hourlyQuality.map { it.dhuPct }.average().let { if (it.isNaN()) 1.85 else Math.round(it * 100.0) / 100.0 }
  val avgOql = hourlyQuality.map { it.oqlPct }.average().let { if (it.isNaN()) 3.55 else Math.round(it * 100.0) / 100.0 }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Line $selectedLineId — Deep-Dive",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            )
            Text(
              text = "$floorName • ${lineSetup.itemName} (SMV: ${String.format("%.2f", lineSetup.smv)})",
              color = Color(0xFFBAE6FD),
              fontSize = 10.sp
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("deep_dive_back_btn")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        actions = {
          Box {
            TextButton(onClick = { showLineSwitcher = true }) {
              Text("Switch Line", color = Color(0xFFFDE047), fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
            DropdownMenu(expanded = showLineSwitcher, onDismissRequest = { showLineSwitcher = false }) {
              repository.allLinesMaster.filter { it.floor == floorName }.forEach { line ->
                DropdownMenuItem(
                  text = { Text("Line ${line.id}") },
                  onClick = {
                    selectedLineId = line.id
                    showLineSwitcher = false
                  }
                )
              }
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
      )
    },
    containerColor = Color(0xFFF8FAFC)
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Time Horizon Filter Chips (Today, Yesterday, Weekly, Monthly)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("Today", "Yesterday", "Weekly", "Monthly").forEach { periodLabel ->
          val isSelected = when (periodLabel) {
            "Today" -> selectedPeriod == PeriodScope.TODAY
            "Yesterday" -> selectedPeriod == PeriodScope.TODAY // simulated past day
            "Weekly" -> selectedPeriod == PeriodScope.WEEK
            "Monthly" -> selectedPeriod == PeriodScope.MONTH
            else -> false
          }
          Surface(
            onClick = {
              selectedPeriod = when (periodLabel) {
                "Weekly" -> PeriodScope.WEEK
                "Monthly" -> PeriodScope.MONTH
                else -> PeriodScope.TODAY
              }
            },
            shape = RoundedCornerShape(8.dp),
            color = if (isSelected) Color(0xFF0F172A) else Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF0F172A) else Color(0xFFCBD5E1)),
            modifier = Modifier.weight(1f)
          ) {
            Text(
              text = periodLabel,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) Color.White else Color(0xFF334155),
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(vertical = 8.dp)
            )
          }
        }
      }

      // ==========================================
      // SECTION A: PRODUCTION PERFORMANCE & HOURLY
      // ==========================================
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          // Section Title
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF0284C7))
              Text("Production & Efficiency Analysis", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF0F172A))
            }
            Surface(color = Color(0xFFE0F2FE), shape = RoundedCornerShape(4.dp)) {
              Text("Item: ${lineSetup.itemName}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
          }

          // KPI Cards Row (Today Eff, Update Eff, Total Achieve, Overall Achieve %)
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KpiCard(
              title = "Today Eff",
              value = "${todayEffPct}%",
              subtext = "Target: ${lineSetup.targetEfficiencyPct.toInt()}%",
              valueColor = if (todayEffPct >= lineSetup.targetEfficiencyPct) Color(0xFF16A34A) else Color(0xFFD97706),
              modifier = Modifier.weight(1f)
            )
            KpiCard(
              title = "Update Eff",
              value = "${monthlyTarget.updateEfficiencyPct}%",
              subtext = "Monthly Avg",
              valueColor = Color(0xFF2563EB),
              modifier = Modifier.weight(1f)
            )
            KpiCard(
              title = "Total Output",
              value = "$totalAchieve",
              subtext = "Tgt: $totalTarget pcs",
              valueColor = Color(0xFF0F172A),
              modifier = Modifier.weight(1f)
            )
            KpiCard(
              title = "Achieve %",
              value = "${overallAchievePct}%",
              subtext = "Output Ratio",
              valueColor = if (overallAchievePct >= 85) Color(0xFF16A34A) else Color(0xFFD97706),
              modifier = Modifier.weight(1f)
            )
          }

          // Line Efficiency Speedometer Dial
          SpeedometerGauge(
            value = todayEffPct,
            targetValue = lineSetup.targetEfficiencyPct,
            modifier = Modifier.fillMaxWidth().height(130.dp)
          )

          // Column / Bar Chart: Hourly Target vs Output
          Text("Column Chart: Hourly Target vs Output (Pieces)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
          HourlyTargetVsOutputChart(hourlyProd)

          // Hourly Production Table (Horizontally Scrollable)
          Text("Hour-by-Hour Production Matrix:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState())
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .widthIn(min = 540.dp),
              verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
              // Table Header
              Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                  Text("Hour", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
                  Text("Hourly Tgt", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
                  Text("Achieve", color = Color(0xFF38BDF8), fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
                  Text("Total Tgt", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
                  Text("Total Ach", color = Color(0xFF4ADE80), fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
                  Text("Efficiency %", color = Color(0xFFFBBF24), fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.1f))
                  Text("Achieve %", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
                }
              }

              // Table Rows
              hourlyProd.forEachIndexed { idx, row ->
                val isEven = idx % 2 == 0
                Surface(
                  color = if (isEven) Color(0xFFF8FAFC) else Color.White,
                  shape = RoundedCornerShape(4.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)) {
                    Text(row.hourSlot, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.3f))
                    Text("${row.hourlyTarget}", fontSize = 9.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
                    Text(
                      text = if (row.achieveOutput > 0) "${row.achieveOutput}" else "--",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (row.achieveOutput >= row.hourlyTarget) Color(0xFF16A34A) else Color(0xFFD97706),
                      textAlign = TextAlign.End,
                      modifier = Modifier.weight(1.0f)
                    )
                    Text("${row.totalTarget}", fontSize = 9.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
                    Text("${row.totalAchieve}", fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.0f))
                    Text(
                      text = if (row.efficiencyPct > 0) "${row.efficiencyPct}%" else "--",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = if (row.efficiencyPct >= lineSetup.targetEfficiencyPct) Color(0xFF16A34A) else Color(0xFFD97706),
                      textAlign = TextAlign.End,
                      modifier = Modifier.weight(1.1f)
                    )
                    Text(
                      text = if (row.achievePct > 0) "${row.achievePct}%" else "--",
                      fontSize = 9.sp,
                      textAlign = TextAlign.End,
                      modifier = Modifier.weight(1.0f)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // ==========================================
      // SECTION B: QUALITY & INSPECTION ANALYTICS
      // ==========================================
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          // Section Title
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF16A34A))
              Text("Quality & Defect Intelligence", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF0F172A))
            }
            Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
              Text("QA Audited", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
          }

          // Source Attribution Notes (Requested by user)
          Surface(
            color = Color(0xFFF1F5F9),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text("📌 DHU data sourced from Endline Quality Inspector inspection.", fontSize = 10.sp, color = Color(0xFF334155), fontWeight = FontWeight.Medium)
              Text("📌 OQL data sourced from Auditor inspections (conducted bi-hourly).", fontSize = 10.sp, color = Color(0xFF334155), fontWeight = FontWeight.Medium)
            }
          }

          // Quality KPI Cards Row (Today DHU, Today OQL, Monthly DHU, Monthly OQL)
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KpiCard(
              title = "Today DHU",
              value = "${avgDhu}%",
              subtext = "Endline QC",
              valueColor = if (avgDhu <= 2.0) Color(0xFF16A34A) else Color(0xFFDC2626),
              modifier = Modifier.weight(1f)
            )
            KpiCard(
              title = "Today OQL",
              value = "${avgOql}%",
              subtext = "Bi-hourly Audit",
              valueColor = if (avgOql <= 3.5) Color(0xFF16A34A) else Color(0xFFD97706),
              modifier = Modifier.weight(1f)
            )
            KpiCard(
              title = "Monthly DHU",
              value = "${monthlyTarget.targetDhuPct}%",
              subtext = "Target Standard",
              valueColor = Color(0xFF2563EB),
              modifier = Modifier.weight(1f)
            )
            KpiCard(
              title = "Monthly OQL",
              value = "${monthlyTarget.targetOqlPct}%",
              subtext = "AQL 2.5 Standard",
              valueColor = Color(0xFF475569),
              modifier = Modifier.weight(1f)
            )
          }

          // Column Chart: Hour vs DHU %
          Text("Column Chart: Hour vs DHU %", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
          HourVsDhuChart(hourlyQuality)

          // Pareto Top 5 Defects Donut / Pie Chart
          Text("Pareto Breakdown: Top 5 Major Defects + Others", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
          ParetoDefectPieChart(paretoDefects)

          // Quality Hourly Table (Hour, DHU, OQL, Major Defects Name)
          Text("Quality Hourly Audit Breakdown:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState())
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .widthIn(min = 500.dp),
              verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
              Surface(color = Color(0xFF0F172A), shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                  Text("Hour", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
                  Text("DHU % (Endline)", color = Color(0xFFF87171), fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.1f))
                  Text("OQL % (Auditor)", color = Color(0xFFFBBF24), fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1.1f))
                  Text("Major Defects Name", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(2.0f))
                }
              }

              hourlyQuality.forEachIndexed { idx, qRow ->
                val isEven = idx % 2 == 0
                Surface(
                  color = if (isEven) Color(0xFFF8FAFC) else Color.White,
                  shape = RoundedCornerShape(4.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(qRow.hourSlot, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.3f))
                    Text("${qRow.dhuPct}%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), textAlign = TextAlign.End, modifier = Modifier.weight(1.1f))
                    Text("${qRow.oqlPct}%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706), textAlign = TextAlign.End, modifier = Modifier.weight(1.1f))
                    Text(qRow.majorDefectsName, fontSize = 9.sp, color = Color(0xFF334155), modifier = Modifier.weight(2.0f))
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

@Composable
private fun KpiCard(
  title: String,
  value: String,
  subtext: String,
  valueColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = Color(0xFFF8FAFC),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(title, fontSize = 9.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
      Text(value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = valueColor)
      Text(subtext, fontSize = 8.sp, color = Color(0xFF94A3B8))
    }
  }
}

// Chart 1: Hourly Target vs Output Bar Chart
@Composable
private fun HourlyTargetVsOutputChart(rows: List<com.example.model.HourlyProductionRow>) {
  val maxVal = rows.maxOfOrNull { maxOf(it.hourlyTarget, it.achieveOutput) }?.coerceAtLeast(1) ?: 100

  Card(
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = Modifier
      .fillMaxWidth()
      .height(140.dp)
  ) {
    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.SpaceBetween) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(modifier = Modifier.size(8.dp).background(Color(0xFF94A3B8), RoundedCornerShape(2.dp)))
          Text("Target", fontSize = 9.sp, color = Color(0xFF64748B))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(modifier = Modifier.size(8.dp).background(Color(0xFF0284C7), RoundedCornerShape(2.dp)))
          Text("Actual Output", fontSize = 9.sp, color = Color(0xFF0284C7), fontWeight = FontWeight.Bold)
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(90.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        rows.take(8).forEach { r ->
          val tgtFrac = (r.hourlyTarget.toFloat() / maxVal).coerceIn(0.1f, 1.0f)
          val achFrac = (r.achieveOutput.toFloat() / maxVal).coerceIn(0.02f, 1.0f)

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.weight(1f)
          ) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(2.dp),
              verticalAlignment = Alignment.Bottom
            ) {
              // Target bar
              Box(
                modifier = Modifier
                  .width(6.dp)
                  .height((tgtFrac * 65).dp)
                  .background(Color(0xFFCBD5E1), RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
              )
              // Actual Output bar
              Box(
                modifier = Modifier
                  .width(8.dp)
                  .height((achFrac * 65).dp)
                  .background(
                    if (r.achieveOutput >= r.hourlyTarget) Color(0xFF16A34A) else Color(0xFF0284C7),
                    RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                  )
              )
            }
            Text(
              text = r.hourSlot.substringBefore(" ").take(2),
              fontSize = 8.sp,
              color = Color(0xFF64748B),
              modifier = Modifier.padding(top = 2.dp)
            )
          }
        }
      }
    }
  }
}

// Chart 2: Hour vs DHU %
@Composable
private fun HourVsDhuChart(rows: List<com.example.model.HourlyQualityRow>) {
  val maxDhu = 3.0f

  Card(
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = Modifier
      .fillMaxWidth()
      .height(130.dp)
  ) {
    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.SpaceBetween) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Standard Max DHU Threshold: 2.0%", fontSize = 9.sp, color = Color(0xFF94A3B8))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Box(modifier = Modifier.size(8.dp).background(Color(0xFFEF4444), RoundedCornerShape(2.dp)))
          Text("DHU %", fontSize = 9.sp, color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(85.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        rows.take(8).forEach { r ->
          val dhuFrac = (r.dhuPct.toFloat() / maxDhu).coerceIn(0.1f, 1.0f)
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.weight(1f)
          ) {
            Text(
              text = "${r.dhuPct}%",
              fontSize = 8.sp,
              fontWeight = FontWeight.Bold,
              color = if (r.dhuPct > 2.0) Color(0xFFDC2626) else Color(0xFF16A34A)
            )
            Box(
              modifier = Modifier
                .width(10.dp)
                .height((dhuFrac * 50).dp)
                .background(
                  if (r.dhuPct > 2.0) Color(0xFFEF4444) else Color(0xFF10B981),
                  RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                )
            )
            Text(
              text = r.hourSlot.substringBefore(" ").take(2),
              fontSize = 8.sp,
              color = Color(0xFF64748B),
              modifier = Modifier.padding(top = 2.dp)
            )
          }
        }
      }
    }
  }
}

// Chart 3: Pareto Pie / Donut Chart for Top 5 Defects + Others
@Composable
private fun ParetoDefectPieChart(defects: List<com.example.model.ParetoDefectPoint>) {
  val sliceColors = listOf(
    Color(0xFFDC2626), // Puckering
    Color(0xFFEA580C), // Broken Stitch
    Color(0xFFD97706), // Skip Stitch
    Color(0xFF2563EB), // Uneven Seam
    Color(0xFF7C3AED), // Needle Cut
    Color(0xFF64748B)  // Others
  )

  Card(
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Donut Chart Canvas
      Box(
        modifier = Modifier.size(110.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.size(100.dp)) {
          var startAngle = -90f
          defects.forEachIndexed { idx, p ->
            val sweep = (p.percentage.toFloat() / 100f) * 360f
            val color = sliceColors[idx % sliceColors.size]
            drawArc(
              color = color,
              startAngle = startAngle,
              sweepAngle = sweep,
              useCenter = false,
              style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Butt)
            )
            startAngle += sweep
          }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("Top 5", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
          Text("Pareto", fontSize = 8.sp, color = Color(0xFF64748B))
        }
      }

      // Legend
      Column(
        modifier = Modifier.weight(1f).padding(start = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        defects.forEachIndexed { idx, p ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Box(modifier = Modifier.size(7.dp).background(sliceColors[idx % sliceColors.size], CircleShape))
              Text(p.defectName, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF334155))
            }
            Text("${p.percentage}% (${p.count})", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
          }
        }
      }
    }
  }
}
