package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NflRepository
import com.example.model.*
import com.example.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  repository: NflRepository,
  onNavigateToProduction: () -> Unit,
  onNavigateToQuality: () -> Unit,
  onNavigateToNpt: () -> Unit,
  onNavigateToSetup: () -> Unit,
  onNavigateToTv: () -> Unit,
  onNavigateToHistory: () -> Unit,
  onNavigateToSettings: () -> Unit,
  onOpenDrawer: () -> Unit = {},
  onNavigateToLineDeepDive: (String) -> Unit = {},
  onNavigateToMonthlySetup: () -> Unit = {},
  onNavigateToAdminUsers: () -> Unit = {}
) {
  val currentFloor by repository.currentFloor.collectAsState()
  val currentPeriod by repository.currentPeriod.collectAsState()
  val isDemoMode by repository.isDemoMode.collectAsState()
  val currentRole by repository.currentRole.collectAsState()
  val userName by repository.currentUserName.collectAsState()
  val incidents by repository.incidents.collectAsState()

  val tvData = remember(currentFloor, currentPeriod, isDemoMode, incidents) {
    repository.getTvDashboardData(currentFloor)
  }

  var selectedChartTab by remember { mutableStateOf("SPEEDOMETER") } // SPEEDOMETER | COLUMN | DONUT | PARETO
  var selectedLineDetail by remember { mutableStateOf<LinePerformancePoint?>(null) }
  var showClearSampleDataDialog by remember { mutableStateOf(false) }

  val openIncidents = incidents.filter { it.status != IncidentStatus.RESOLVED }

  Scaffold(
    topBar = {
      // Enterprise Top Navy Header
      Surface(
        color = Color(0xFF0A2558),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
          // Top Row: Plain NFL Badge, Subtext, Live & DEMO Indicators
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.weight(1f, fill = false)
            ) {
              IconButton(
                onClick = onOpenDrawer,
                modifier = Modifier
                  .size(36.dp)
                  .testTag("home_hamburger_btn")
              ) {
                Icon(
                  Icons.Default.Menu,
                  contentDescription = "Open Navigation Menu",
                  tint = Color.White
                )
              }

              Surface(
                color = Color.White,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.padding(1.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(
                    painter = androidx.compose.ui.res.painterResource(com.example.R.drawable.ic_nos_logo),
                    contentDescription = "NOS Logo",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Unspecified
                  )
                  Text(
                    text = "NOS Live",
                    color = Color(0xFF0A2558),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                  )
                }
              }

              Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                  text = "Northern Operating System",
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "MES Industrial Monitor",
                  color = Color(0xFFB0C4DE),
                  fontSize = 9.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              // Sleek, unified Online status badge with pulsing green indicator (Never wraps!)
              Surface(
                color = Color(0xFF064E3B),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.6f))
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(5.dp),
                  modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(Color(0xFF34D399))
                  )
                  Text(
                    text = "ONLINE",
                    color = Color(0xFF6EE7B7),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    softWrap = false
                  )
                }
              }

              IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.size(28.dp).testTag("home_settings_btn")
              ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(18.dp))
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Floor Filter Tabs: Sew 1 | Sew 2 | Sew 3 | All
          Surface(
            color = Color(0xFF06183B),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
              horizontalArrangement = Arrangement.SpaceEvenly
            ) {
              FloorScope.values().forEach { floor ->
                val isSelected = currentFloor == floor
                Surface(
                  onClick = { repository.setFloor(floor) },
                  color = if (isSelected) Color(0xFF1976D2) else Color.Transparent,
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.weight(1f)
                ) {
                  Text(
                    text = floor.displayName,
                    color = if (isSelected) Color.White else Color(0xFF90CAF9),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 6.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Date, Shift & Period bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Text(text = "📅 09 Oct 2026", color = Color(0xFFE3F2FD), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
              Text(text = "• Shift A (08:00 - 17:00)", color = Color(0xFFB0C4DE), fontSize = 10.sp)
            }

            Surface(
              color = Color(0xFF06183B),
              shape = RoundedCornerShape(6.dp)
            ) {
              Row(modifier = Modifier.padding(2.dp)) {
                PeriodScope.values().forEach { period ->
                  val isSelected = currentPeriod == period
                  Surface(
                    onClick = { repository.setPeriod(period) },
                    color = if (isSelected) Color(0xFF1976D2) else Color.Transparent,
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text(
                      text = period.displayName,
                      color = if (isSelected) Color.White else Color(0xFF90CAF9),
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    },
    containerColor = Color(0xFFF4F6F9)
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // 1. Open Incident / NPT Alert Strip
      if (openIncidents.isNotEmpty()) {
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFDBA74))),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToNpt() }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
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
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFFFEDD5)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = "⚠️", fontSize = 16.sp)
              }
              Column {
                Text(
                  text = "${openIncidents.size} Active Downtime / NPT Incidents",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Black,
                  color = Color(0xFF9A3412)
                )
                val topIncident = openIncidents.first()
                val elapsedMinutes = ((System.currentTimeMillis() - topIncident.startTimeMillis) / 60000).coerceAtLeast(0)
                Text(
                  text = "Line ${topIncident.lineId} (${topIncident.category.displayName}) • $elapsedMinutes mins elapsed",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium,
                  color = Color(0xFFC2410C)
                )
              }
            }

            Surface(
              color = Color(0xFFEA580C),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "Manage →",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }

      // 2. Section Header: Executive KPIs
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "${currentFloor.displayName} — Executive Performance",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A)
          )
          Text(
            text = "Aggregated shift efficiency & quality metrics",
            fontSize = 10.sp,
            color = Color(0xFF64748B)
          )
        }

        Surface(
          color = Color(0xFFE2E8F0),
          shape = RoundedCornerShape(4.dp)
        ) {
          Text(
            text = "Role: ${currentRole.displayName}",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF334E68),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      // Admin-only data reset. It clears sample records only, never non-sample submissions.
      if (currentRole == UserRole.ADMIN) {
        OutlinedButton(
          onClick = { showClearSampleDataDialog = true },
          modifier = Modifier.fillMaxWidth(),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB91C1C)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5))
        ) {
          Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(Modifier.width(8.dp))
          Text("Clear All Sample Data", fontWeight = FontWeight.Bold)
        }
      }

      // 3. Multi-Visual Executive KPI Cards Grid (Speedometer, Donut, Trend, Quality)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ExecutiveKpiCard(
          title = "Today Efficiency",
          valueText = "${tvData.kpis.todayEfficiency}%",
          targetText = "Target: 75.0% (Δ -7.0 pp)",
          trendText = if (tvData.kpis.todayEfficiency >= 75) "ON TARGET" else "UNDER",
          isPositiveTrend = tvData.kpis.todayEfficiency >= 75,
          accentColor = Color(0xFF1E88E5),
          endVisual = {
            MiniSpeedometerGauge(
              value = tvData.kpis.todayEfficiency,
              targetValue = 75.0
            )
          },
          modifier = Modifier.weight(1f)
        )

        ExecutiveKpiCard(
          title = "Monthly Efficiency",
          valueText = "${tvData.kpis.monthlyEfficiency}%",
          targetText = "Last month: 68.1%",
          trendText = "▲ +${tvData.kpis.monthlyEfficiencyDelta} pp",
          isPositiveTrend = true,
          progressFraction = (tvData.kpis.monthlyEfficiency / 100.0).toFloat(),
          accentColor = Color(0xFF0284C7),
          modifier = Modifier.weight(1f)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ExecutiveKpiCard(
          title = "Today Output",
          valueText = "${tvData.kpis.todayOutputPcs} pcs",
          targetText = "Target: ${tvData.kpis.todayTargetPcs} pcs (82.1%)",
          trendText = "-750 pcs gap",
          isPositiveTrend = false,
          accentColor = Color(0xFF10B981),
          endVisual = {
            MiniDonutProgress(
              fraction = (tvData.kpis.todayOutputPcs.toFloat() / tvData.kpis.todayTargetPcs.toFloat()),
              accentColor = Color(0xFF10B981)
            )
          },
          modifier = Modifier.weight(1f)
        )

        ExecutiveKpiCard(
          title = "Today OQL",
          valueText = "${tvData.kpis.todayOql}%",
          targetText = "Baseline Standard: 5.00%",
          trendText = if (tvData.kpis.todayOql <= 5.0) "PASS" else "ALERT",
          isPositiveTrend = tvData.kpis.todayOql <= 5.0,
          progressFraction = (tvData.kpis.todayOql / 10.0).toFloat(),
          accentColor = Color(0xFFF59E0B),
          modifier = Modifier.weight(1f)
        )
      }

      // Secondary KPI Row: FRI Pass Rate & Operators/Machines
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ExecutiveKpiCard(
          title = "FRI Pass Rate",
          valueText = "${tvData.kpis.friPassRate.toInt()}%",
          targetText = "First-time Right Quality",
          trendText = "100% PERFECT",
          isPositiveTrend = true,
          progressFraction = 1.0f,
          accentColor = Color(0xFF059669),
          modifier = Modifier.weight(1f)
        )

        OperatorMachineMetricCard(
          operatorsCount = tvData.kpis.operatorsCount,
          machinesCount = tvData.kpis.machinesCount,
          modifier = Modifier.weight(1f)
        )
      }

      // 4. Quick Action Modules (Entry, Supervisor, TV, History)
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0))),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Factory Action Center",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF0F172A)
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Production Entry
            Surface(
              onClick = onNavigateToProduction,
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFEFF6FF),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDBEAFE)),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(22.dp))
                Text("Prod Entry", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF))
                Text("Hourly Pieces", fontSize = 8.sp, color = Color(0xFF60A5FA))
              }
            }

            // Quality Entry
            Surface(
              onClick = onNavigateToQuality,
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFFFFBEB),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFEF3C7)),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(Icons.Default.FactCheck, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(22.dp))
                Text("QA Inspect", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                Text("OQL & Defects", fontSize = 8.sp, color = Color(0xFFFBBF24))
              }
            }

            // Supervisor NPT
            Surface(
              onClick = onNavigateToNpt,
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFFFF1F2),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE4E6)),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFFBE123C), modifier = Modifier.size(22.dp))
                Text("NPT Downtime", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9F1239))
                Text("Stopwatch", fontSize = 8.sp, color = Color(0xFFFB7185))
              }
            }

            // Smart TV
            Surface(
              onClick = onNavigateToTv,
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFF0FDF4),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDCFCE7)),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(Icons.Default.Tv, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(22.dp))
                Text("Smart TV", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                Text("16:9 Screen", fontSize = 8.sp, color = Color(0xFF4ADE80))
              }
            }
          }
        }
      }

      // 5. Chart Selection & Presentation
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Visual Performance Analytics",
          fontSize = 13.sp,
          fontWeight = FontWeight.Black,
          color = Color(0xFF0F172A)
        )

        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
          listOf(
            "SPEEDOMETER" to "⚡ Speedometer",
            "COLUMN" to "📊 Column",
            "DONUT" to "🍩 Donut",
            "PARETO" to "🎯 Pareto"
          ).forEach { (id, label) ->
            val isSelected = selectedChartTab == id
            Surface(
              onClick = { selectedChartTab = id },
              shape = RoundedCornerShape(6.dp),
              color = if (isSelected) Color(0xFF0F172A) else Color(0xFFE2E8F0)
            ) {
              Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF475569),
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                maxLines = 1,
                softWrap = false
              )
            }
          }
        }
      }

      when (selectedChartTab) {
        "SPEEDOMETER" -> SpeedometerGaugeCard(
          title = "${currentFloor.displayName} — Floor Efficiency Speedometer",
          currentEfficiency = tvData.kpis.todayEfficiency,
          targetEfficiency = 75.0,
          lines = tvData.linePerformance,
          onLineClick = onNavigateToLineDeepDive
        )
        "COLUMN" -> TargetVsOutputBarChart(
          title = "${currentFloor.displayName} — Target vs Output Pieces (Column Chart)",
          lines = tvData.linePerformance,
          onLineClick = onNavigateToLineDeepDive
        )
        "DONUT" -> DonutAttainmentCard(
          title = "${currentFloor.displayName} — Output Attainment (Donut Chart)",
          targetPcs = tvData.kpis.todayTargetPcs,
          actualPcs = tvData.kpis.todayOutputPcs
        )
        "PARETO" -> ParetoDonutDefectsCard(
          title = "${currentFloor.displayName} — Top 5 Defects (Pareto Donut)",
          defects = tvData.topDefects
        )
        else -> TargetVsOutputBarChart(
          title = "${currentFloor.displayName} — Target vs Output Pieces",
          lines = tvData.linePerformance,
          onLineClick = onNavigateToLineDeepDive
        )
      }

      // 6. Complete Live Line Performance Matrix Table (Touching line opens Deep Dive)
      LiveLinePerformanceTable(
        lines = tvData.linePerformance,
        onLineClick = { line -> onNavigateToLineDeepDive(line.lineId) }
      )

      // 7. Footer Info
      Surface(
        color = Color.White,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "NOS Live — Northern Operating System",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF0F172A)
            )
            Text(
              text = "Individual Morning Target Pcs & Efficiency % per line",
              fontSize = 9.sp,
              color = Color(0xFF64748B)
            )
          }

          Button(
            onClick = onNavigateToSetup,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A2558)),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text("🌅 Morning Setup", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }

  if (showClearSampleDataDialog && currentRole == UserRole.ADMIN) {
    AlertDialog(
      onDismissRequest = { showClearSampleDataDialog = false },
      title = { Text("Clear all sample data?", fontWeight = FontWeight.Black) },
      text = {
        Text("This removes only records marked as sample data (production, quality, targets and incidents). Non-sample records are preserved. The sample dataset will not be automatically added again after restarting the app.")
      },
      confirmButton = {
        TextButton(onClick = {
          repository.removeAllDemoData()
          showClearSampleDataDialog = false
        }) {
          Text("Clear Sample Data", color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearSampleDataDialog = false }) { Text("Cancel") }
      }
    )
  }

  // Detail Modal for selected line
  selectedLineDetail?.let { line ->
    AlertDialog(
      onDismissRequest = { selectedLineDetail = null },
      title = {
        Text("Line ${line.lineId} — Live Performance", fontWeight = FontWeight.Black, fontSize = 16.sp)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Running Item:", fontSize = 12.sp, color = Color(0xFF475569))
            Text(line.itemName, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Standard Minute Value (SMV):", fontSize = 12.sp, color = Color(0xFF475569))
            Text("${String.format("%.2f", line.smv)} min/pc", fontWeight = FontWeight.Bold, color = Color(0xFF0284C7))
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Floor:", fontSize = 12.sp, color = Color(0xFF475569))
            Text(line.floor, fontWeight = FontWeight.SemiBold)
          }
          Divider(color = Color(0xFFE2E8F0))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Morning Target Pieces:", fontSize = 12.sp, color = Color(0xFF475569))
            Text("${line.targetPcs} pcs", fontWeight = FontWeight.Bold)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Morning Target Efficiency:", fontSize = 12.sp, color = Color(0xFF475569))
            Text("${line.targetEfficiencyPct.toInt()}%", fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Actual Output Produced:", fontSize = 12.sp, color = Color(0xFF475569))
            Text(if (line.achievementPcs != null) "${line.achievementPcs} pcs" else "Pending submission", fontWeight = FontWeight.Bold)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Actual Line Efficiency:", fontSize = 12.sp, color = Color(0xFF475569))
            Text(
              text = if (line.todayEfficiencyPct != null) "${String.format("%.1f", line.todayEfficiencyPct)}%" else "--",
              fontWeight = FontWeight.Black,
              color = when {
                line.todayEfficiencyPct == null -> Color(0xFF94A3B8)
                line.todayEfficiencyPct >= line.targetEfficiencyPct -> Color(0xFF166534)
                else -> Color(0xFFDC2626)
              }
            )
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Target Output Achievement:", fontSize = 12.sp, color = Color(0xFF475569))
            Text(if (line.achievementPct != null) "${String.format("%.1f", line.achievementPct)}%" else "--", fontWeight = FontWeight.Bold)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Line OQL Defect Rate:", fontSize = 12.sp, color = Color(0xFF475569))
            Text(if (line.todayOqlPct != null) "${String.format("%.2f", line.todayOqlPct)}%" else "0.00%", fontWeight = FontWeight.Bold)
          }
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Reporting Status:", fontSize = 12.sp, color = Color(0xFF475569))
            Text(line.reportingStatus, fontWeight = FontWeight.Bold, color = if (line.achievementPcs != null) Color(0xFF166534) else Color(0xFFD97706))
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { selectedLineDetail = null }) {
          Text("Close", fontWeight = FontWeight.Bold)
        }
      }
    )
  }
}
