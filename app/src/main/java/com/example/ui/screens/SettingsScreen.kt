package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NflRepository
import com.example.model.FloorScope
import com.example.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  repository: NflRepository,
  onNavigateBack: () -> Unit
) {
  val currentRole by repository.currentRole.collectAsState()
  val assignedLine by repository.assignedLine.collectAsState()
  val currentFloor by repository.currentFloor.collectAsState()
  val isDemoMode by repository.isDemoMode.collectAsState()
  val targets by repository.targets.collectAsState()
  val prods by repository.productionEntries.collectAsState()
  val qas by repository.qualityEntries.collectAsState()
  val incs by repository.incidents.collectAsState()

  var showRoleDropdown by remember { mutableStateOf(false) }
  var showLineDropdown by remember { mutableStateOf(false) }
  var showFloorDropdown by remember { mutableStateOf(false) }
  var confirmPurgeDialog by remember { mutableStateOf(false) }
  var statusMessage by remember { mutableStateOf<String?>(null) }

  val demoTargetsCount = targets.count { it.isDemo }
  val demoProdsCount = prods.count { it.isDemo }
  val demoQasCount = qas.count { it.isDemo }
  val demoIncsCount = incs.count { it.isDemo }
  val totalDemoRows = demoTargetsCount + demoProdsCount + demoQasCount + demoIncsCount

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Factory Settings & Demo Lifecycle", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("settings_back_btn")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF002D62))
      )
    },
    containerColor = Color(0xFFF1F5F9)
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      if (statusMessage != null) {
        Surface(
          color = Color(0xFFE8F5E9),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = statusMessage ?: "",
            color = Color(0xFF2E7D32),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(12.dp)
          )
        }
      }

      // Role & Floor Assignment Card
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("User Role & Floor Scope", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF102A43))

          // 10-Role Selector
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Active User Role:", fontWeight = FontWeight.Medium, fontSize = 12.sp)
              Text("Permissions & alert routing", fontSize = 10.sp, color = Color.Gray)
            }
            Box {
              OutlinedButton(onClick = { showRoleDropdown = true }) {
                Text(currentRole.displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
              DropdownMenu(expanded = showRoleDropdown, onDismissRequest = { showRoleDropdown = false }) {
                UserRole.values().forEach { role ->
                  DropdownMenuItem(
                    text = {
                      Column {
                        Text(role.displayName, fontWeight = FontWeight.Bold)
                        Text(role.group, fontSize = 10.sp, color = Color.Gray)
                      }
                    },
                    onClick = {
                      repository.setRole(role)
                      showRoleDropdown = false
                    }
                  )
                }
              }
            }
          }

          // Floor Scope
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Default Floor Scope:", fontWeight = FontWeight.Medium, fontSize = 12.sp)
            Box {
              OutlinedButton(onClick = { showFloorDropdown = true }) {
                Text(currentFloor.displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
              DropdownMenu(expanded = showFloorDropdown, onDismissRequest = { showFloorDropdown = false }) {
                FloorScope.values().forEach { floor ->
                  DropdownMenuItem(
                    text = { Text(floor.displayName) },
                    onClick = {
                      repository.setFloor(floor)
                      showFloorDropdown = false
                    }
                  )
                }
              }
            }
          }

          // Assigned Line
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Assigned Line Station:", fontWeight = FontWeight.Medium, fontSize = 12.sp)
            Box {
              OutlinedButton(onClick = { showLineDropdown = true }) {
                Text("Line $assignedLine", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
              DropdownMenu(expanded = showLineDropdown, onDismissRequest = { showLineDropdown = false }) {
                repository.allLinesMaster
                  .filter { currentFloor == FloorScope.ALL || it.floor == currentFloor.apiValue }
                  .take(15)
                  .forEach { line ->
                    DropdownMenuItem(
                      text = { Text("Line ${line.id} (${line.floor})") },
                      onClick = {
                        repository.setAssignedLine(line.id)
                        showLineDropdown = false
                      }
                    )
                  }
              }
            }
          }
        }
      }

      // Demo Data Lifecycle Management Card
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Demo Data Lifecycle (Admin)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF102A43))
          Text(
            text = "Demo data provides realistic test metrics across Sew 1, Sew 2, and Sew 3 matching the attached visual designs. Demo rows are isolated from real entries.",
            fontSize = 11.sp,
            color = Color(0xFF546E7A)
          )

          // Counts summary
          Surface(
            color = Color(0xFFF8FAFC),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text("Current Demo Inventory:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Demo Line Targets: $demoTargetsCount", fontSize = 10.sp, color = Color.Gray)
                Text("Demo Output Entries: $demoProdsCount", fontSize = 10.sp, color = Color.Gray)
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Demo Quality Logs: $demoQasCount", fontSize = 10.sp, color = Color.Gray)
                Text("Demo Active Incidents: $demoIncsCount", fontSize = 10.sp, color = Color.Gray)
              }
              Text(
                text = "Total Demo Records: $totalDemoRows",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = if (isDemoMode) Color(0xFF1976D2) else Color.Gray
              )
            }
          }

          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = {
                repository.loadSeedDemoData()
                statusMessage = "✓ Demo data loaded idempotently across all floors!"
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text("Load Demo Data", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = { confirmPurgeDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text("Remove All Demo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }

  // Confirmation dialog for removing all demo data
  if (confirmPurgeDialog) {
    AlertDialog(
      onDismissRequest = { confirmPurgeDialog = false },
      title = { Text("Confirm Removal of Demo Data", fontWeight = FontWeight.Bold) },
      text = {
        Text("This will purge all $totalDemoRows demo records. Real user accounts, settings, line master, and real production records will be completely preserved.")
      },
      confirmButton = {
        Button(
          onClick = {
            repository.removeAllDemoData()
            confirmPurgeDialog = false
            statusMessage = "✓ All demo data removed! Switched to clean live state."
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
        ) {
          Text("Confirm Delete", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { confirmPurgeDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
