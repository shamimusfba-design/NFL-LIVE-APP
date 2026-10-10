package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NflRepository
import com.example.model.FloorScope
import com.example.model.MonthlyFactoryTarget
import com.example.ui.theme.NflRoyalBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlySetupScreen(
  repository: NflRepository,
  onNavigateBack: () -> Unit
) {
  val monthlyTargets by repository.monthlyTargets.collectAsState()
  val userName by repository.currentUserName.collectAsState()

  var selectedFloor by remember { mutableStateOf(FloorScope.SEW_2) }
  val currentTarget = remember(selectedFloor, monthlyTargets) {
    repository.getMonthlyTarget(selectedFloor.apiValue)
  }

  var targetEffText by remember(selectedFloor) { mutableStateOf(currentTarget.targetEfficiencyPct.toString()) }
  var updateEffText by remember(selectedFloor) { mutableStateOf(currentTarget.updateEfficiencyPct.toString()) }
  var targetOqlText by remember(selectedFloor) { mutableStateOf(currentTarget.targetOqlPct.toString()) }
  var targetDhuText by remember(selectedFloor) { mutableStateOf(currentTarget.targetDhuPct.toString()) }
  var friPassText by remember(selectedFloor) { mutableStateOf(currentTarget.friPassRatePct.toString()) }

  var saveSuccessMsg by remember { mutableStateOf<String?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Monthly Baseline & Target Setup", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            Text("মাসিক এফিসিয়েন্সি, ওকিউএল ও কোয়ালিটি টার্গেট কনফিগারেশন", fontSize = 10.sp, color = Color(0xFFBAE6FD))
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("monthly_back_btn")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Floor Selector Chips
      Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Select Factory Floor:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF475569))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(FloorScope.SEW_1, FloorScope.SEW_2, FloorScope.SEW_3, FloorScope.FINISHING).forEach { floor ->
              val isSelected = selectedFloor == floor
              Surface(
                onClick = { selectedFloor = floor },
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF0F172A) else Color(0xFFCBD5E1)),
                modifier = Modifier.weight(1f)
              ) {
                Text(
                  text = when (floor) {
                    FloorScope.SEW_1 -> "Sew 1"
                    FloorScope.SEW_2 -> "Sew 2"
                    FloorScope.SEW_3 -> "Sew 3"
                    FloorScope.FINISHING -> "Finish"
                    else -> floor.displayName
                  },
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else Color(0xFF334155),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                  modifier = Modifier.padding(vertical = 8.dp)
                )
              }
            }
          }
        }
      }

      // 2. Target Fields Card
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF0284C7))
            Text(
              text = "${selectedFloor.displayName} — Monthly Targets (October 2026)",
              fontSize = 14.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color(0xFF0F172A)
            )
          }

          // Row 1: Target Efficiency & Update Efficiency
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = targetEffText,
              onValueChange = { targetEffText = it },
              label = { Text("Monthly Target Eff (%)") },
              placeholder = { Text("e.g. 72.0") },
              modifier = Modifier.weight(1f),
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
              value = updateEffText,
              onValueChange = { updateEffText = it },
              label = { Text("Update Efficiency (%)") },
              placeholder = { Text("e.g. 68.5") },
              modifier = Modifier.weight(1f),
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
          }

          // Row 2: Target OQL & Target DHU
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
              value = targetOqlText,
              onValueChange = { targetOqlText = it },
              label = { Text("Monthly OQL Target (%)") },
              placeholder = { Text("e.g. 3.50") },
              modifier = Modifier.weight(1f),
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
              value = targetDhuText,
              onValueChange = { targetDhuText = it },
              label = { Text("Monthly DHU Target (%)") },
              placeholder = { Text("e.g. 2.00") },
              modifier = Modifier.weight(1f),
              singleLine = true,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
          }

          // Row 3: FRI Pass Rate
          OutlinedTextField(
            value = friPassText,
            onValueChange = { friPassText = it },
            label = { Text("FRI Pass Rate Target (%) — Final Random Inspection") },
            placeholder = { Text("e.g. 100.0") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
          )

          // Feedback
          AnimatedVisibility(visible = saveSuccessMsg != null) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFDCFCE7),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = saveSuccessMsg ?: "",
                color = Color(0xFF166534),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                modifier = Modifier.padding(10.dp)
              )
            }
          }

          // Save Button
          Button(
            onClick = {
              val target = MonthlyFactoryTarget(
                floor = selectedFloor.apiValue,
                targetEfficiencyPct = targetEffText.toDoubleOrNull() ?: 72.0,
                updateEfficiencyPct = updateEffText.toDoubleOrNull() ?: 68.5,
                targetOqlPct = targetOqlText.toDoubleOrNull() ?: 3.50,
                targetDhuPct = targetDhuText.toDoubleOrNull() ?: 2.00,
                friPassRatePct = friPassText.toDoubleOrNull() ?: 100.0,
                lastUpdatedBy = userName
              )
              repository.saveMonthlyTarget(target)
              saveSuccessMsg = "✓ Saved Monthly Targets for ${selectedFloor.displayName} (Eff: ${target.targetEfficiencyPct}%, Update: ${target.updateEfficiencyPct}%, OQL: ${target.targetOqlPct}%)"
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NflRoyalBlue)
          ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Save Monthly Targets & Baselines", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
