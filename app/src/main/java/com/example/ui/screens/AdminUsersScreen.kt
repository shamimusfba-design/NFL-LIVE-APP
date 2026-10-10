package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.example.model.UserAccount
import com.example.model.UserRole
import com.example.ui.theme.NflRoyalBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
  repository: NflRepository,
  onNavigateBack: () -> Unit
) {
  val users by repository.users.collectAsState()
  var showAddUserDialog by remember { mutableStateOf(false) }
  var editingUser by remember { mutableStateOf<UserAccount?>(null) }

  // New / Edit User Form State
  var formId by remember { mutableStateOf("") }
  var formName by remember { mutableStateOf("") }
  var formPassword by remember { mutableStateOf("1234") }
  var formRole by remember { mutableStateOf(UserRole.SUPERVISOR) }
  var formFloor by remember { mutableStateOf(FloorScope.SEW_2) }
  var formLine by remember { mutableStateOf("K1") }

  var permProdRead by remember { mutableStateOf(true) }
  var permProdWrite by remember { mutableStateOf(true) }
  var permQualityRead by remember { mutableStateOf(true) }
  var permQualityWrite by remember { mutableStateOf(true) }
  var permNptRead by remember { mutableStateOf(true) }
  var permNptWrite by remember { mutableStateOf(true) }
  var permMorningRead by remember { mutableStateOf(true) }
  var permMorningWrite by remember { mutableStateOf(true) }
  var permMonthlyRead by remember { mutableStateOf(true) }
  var permMonthlyWrite by remember { mutableStateOf(true) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("User & Access Control", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            Text("Admin Module: User Assignment & Permissions", fontSize = 10.sp, color = Color(0xFFBAE6FD))
          }
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("admin_back_btn")) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        actions = {
          IconButton(onClick = {
            formId = ""
            formName = ""
            formPassword = "123"
            formRole = UserRole.SUPERVISOR
            formFloor = FloorScope.SEW_2
            formLine = "K1"
            showAddUserDialog = true
          }) {
            Icon(Icons.Default.PersonAdd, contentDescription = "Add User", tint = Color(0xFFFDE047))
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = {
          formId = ""
          formName = ""
          formPassword = "123"
          formRole = UserRole.SUPERVISOR
          formFloor = FloorScope.SEW_2
          formLine = "K1"
          showAddUserDialog = true
        },
        containerColor = NflRoyalBlue,
        contentColor = Color.White
      ) {
        Icon(Icons.Default.Add, contentDescription = "Create User")
      }
    },
    containerColor = Color(0xFFF8FAFC)
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      item {
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF1D4ED8))
            Column {
              Text("Task & Line Wise Access Assignment", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E3A8A))
              Text("Assign users to specific floors/lines and configure module Read & Write permissions.", fontSize = 10.sp, color = Color(0xFF475569))
            }
          }
        }
      }

      items(users, key = { it.id }) { user ->
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(user.name, fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF0F172A))
                Text("User ID: ${user.id} • Password: ${user.password}", fontSize = 11.sp, color = Color(0xFF64748B))
              }
              Surface(
                color = when (user.role) {
                  UserRole.ADMIN -> Color(0xFFFEE2E2)
                  UserRole.SUPERVISOR -> Color(0xFFDBEAFE)
                  UserRole.MECHANIC -> Color(0xFFFEF3C7)
                  else -> Color(0xFFDCFCE7)
                },
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = user.role.displayName,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = when (user.role) {
                    UserRole.ADMIN -> Color(0xFF991B1B)
                    UserRole.SUPERVISOR -> Color(0xFF1E40AF)
                    UserRole.MECHANIC -> Color(0xFF92400E)
                    else -> Color(0xFF166534)
                  },
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Text(
              text = "Assigned Scope: ${user.floor.displayName} • Line: ${user.assignedLine}",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF334155)
            )

            // Permissions badges
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              PermissionBadge("Prod", user.canProductionWrite)
              PermissionBadge("QC", user.canQualityWrite)
              PermissionBadge("NPT", user.canNptWrite)
              PermissionBadge("Morning", user.canMorningTargetWrite)
              PermissionBadge("Monthly", user.canMonthlyTargetWrite)
            }

            if (!user.isAdmin) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                TextButton(onClick = {
                  editingUser = user
                  formId = user.id
                  formName = user.name
                  formPassword = user.password
                  formRole = user.role
                  formFloor = user.floor
                  formLine = user.assignedLine
                  permProdRead = user.canProductionRead
                  permProdWrite = user.canProductionWrite
                  permQualityRead = user.canQualityRead
                  permQualityWrite = user.canQualityWrite
                  permNptRead = user.canNptRead
                  permNptWrite = user.canNptWrite
                  permMorningRead = user.canMorningTargetRead
                  permMorningWrite = user.canMorningTargetWrite
                  permMonthlyRead = user.canMonthlyTargetRead
                  permMonthlyWrite = user.canMonthlyTargetWrite
                  showAddUserDialog = true
                }) {
                  Text("Edit", fontSize = 11.sp)
                }
                TextButton(onClick = { repository.deleteUser(user.id) }) {
                  Text("Delete", fontSize = 11.sp, color = Color(0xFFDC2626))
                }
              }
            }
          }
        }
      }
    }
  }

  // DIALOG: Create / Edit User & Permissions
  if (showAddUserDialog) {
    AlertDialog(
      onDismissRequest = {
        showAddUserDialog = false
        editingUser = null
      },
      title = { Text(if (editingUser != null) "Edit User & Permissions" else "Create New User Account", fontWeight = FontWeight.Bold) },
      text = {
        Column(
          modifier = Modifier.verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = formId,
            onValueChange = { formId = it },
            label = { Text("User ID *") },
            placeholder = { Text("e.g. sup_kabir, qc_akter") },
            enabled = editingUser == null,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = formName,
            onValueChange = { formName = it },
            label = { Text("Full Name *") },
            placeholder = { Text("e.g. Kabir Hossain") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = formPassword,
            onValueChange = { formPassword = it },
            label = { Text("Password *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          // Floor
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(FloorScope.SEW_1, FloorScope.SEW_2, FloorScope.SEW_3, FloorScope.FINISHING, FloorScope.ALL).forEach { flr ->
              Surface(
                onClick = { formFloor = flr },
                shape = RoundedCornerShape(4.dp),
                color = if (formFloor == flr) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                modifier = Modifier.weight(1f)
              ) {
                Text(
                  text = when (flr) {
                    FloorScope.SEW_1 -> "Sew 1"
                    FloorScope.SEW_2 -> "Sew 2"
                    FloorScope.SEW_3 -> "Sew 3"
                    FloorScope.FINISHING -> "Finish"
                    FloorScope.ALL -> "All"
                  },
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (formFloor == flr) Color.White else Color(0xFF334155),
                  textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                  modifier = Modifier.padding(vertical = 4.dp)
                )
              }
            }
          }

          OutlinedTextField(
            value = formLine,
            onValueChange = { formLine = it },
            label = { Text("Assigned Line (e.g. K1, J1, All)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          // Module Permissions Toggles
          Text("Module Permissions (Read / Write):", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF0F172A))

          PermToggle("Production Entry", permProdWrite) { permProdWrite = it }
          PermToggle("Quality Inspection (DHU / OQL)", permQualityWrite) { permQualityWrite = it }
          PermToggle("NPT & Maintenance", permNptWrite) { permNptWrite = it }
          PermToggle("Morning Target Setup", permMorningWrite) { permMorningWrite = it }
          PermToggle("Monthly Target Setup", permMonthlyWrite) { permMonthlyWrite = it }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (formId.isNotBlank() && formName.isNotBlank() && formPassword.isNotBlank()) {
              val account = UserAccount(
                id = formId.trim(),
                name = formName.trim(),
                password = formPassword.trim(),
                role = formRole,
                floor = formFloor,
                assignedLine = formLine.trim().ifEmpty { "K1" },
                canProductionRead = permProdRead,
                canProductionWrite = permProdWrite,
                canQualityRead = permQualityRead,
                canQualityWrite = permQualityWrite,
                canNptRead = permNptRead,
                canNptWrite = permNptWrite,
                canMorningTargetRead = permMorningRead,
                canMorningTargetWrite = permMorningWrite,
                canMonthlyTargetRead = permMonthlyRead,
                canMonthlyTargetWrite = permMonthlyWrite,
                isAdmin = formRole == UserRole.ADMIN
              )
              repository.saveUser(account)
              showAddUserDialog = false
              editingUser = null
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = NflRoyalBlue)
        ) {
          Text("Save User", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = {
          showAddUserDialog = false
          editingUser = null
        }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun PermissionBadge(module: String, hasWrite: Boolean) {
  Surface(
    color = if (hasWrite) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
    shape = RoundedCornerShape(4.dp)
  ) {
    Text(
      text = if (hasWrite) "$module: W" else "$module: R",
      fontSize = 9.sp,
      fontWeight = FontWeight.Bold,
      color = if (hasWrite) Color(0xFF166534) else Color(0xFF64748B),
      modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    )
  }
}

@Composable
private fun PermToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(label, fontSize = 11.sp, color = Color(0xFF334155))
    Switch(checked = checked, onCheckedChange = onCheckedChange)
  }
}
