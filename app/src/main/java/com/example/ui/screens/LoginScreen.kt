package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NflRepository
import com.example.model.UserRole
import com.example.ui.theme.NflGreen
import com.example.ui.theme.NflRoyalBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
  repository: NflRepository,
  onLoginSuccess: () -> Unit
) {
  val users by repository.users.collectAsState()
  var usernameInput by remember { mutableStateOf("supervisor") }
  var passwordInput by remember { mutableStateOf("123") }
  var passwordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  // Admin Initial Setup Modal State
  var showAdminSetupDialog by remember { mutableStateOf(false) }
  var newAdminId by remember { mutableStateOf("admin") }
  var newAdminPass by remember { mutableStateOf("123") }
  var newAdminName by remember { mutableStateOf("System Admin") }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // 1. App Brand Header with Official Company Logo
      Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
        modifier = Modifier.size(80.dp)
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.padding(6.dp)
        ) {
          Icon(
            painter = painterResource(com.example.R.drawable.ic_nfl_logo),
            contentDescription = "Northern Fashion Limited Company Logo",
            modifier = Modifier.size(64.dp),
            tint = Color.Unspecified
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "NFL LIVE",
        fontSize = 26.sp,
        fontWeight = FontWeight.Black,
        color = Color.White
      )
      Text(
        text = "Northern Fashion Limited Live",
        fontSize = 12.sp,
        color = Color(0xFF94A3B8)
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Offline Local System Status Pill (Directly answers user's question!)
      Surface(
        color = Color(0xFF064E3B),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .clip(CircleShape)
              .background(Color(0xFF34D399))
          )
          Text(
            text = "Local System Ready • No Server Upload Needed",
            color = Color(0xFF6EE7B7),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 2. Login Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Text(
            text = "Sign In to Your Account",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
          )
          Text(
            text = "Enter your Designation User ID and Password (Default: 123)",
            fontSize = 11.sp,
            color = Color(0xFF64748B)
          )

          // User ID Field
          OutlinedTextField(
            value = usernameInput,
            onValueChange = {
              usernameInput = it
              errorMessage = null
            },
            label = { Text("User ID (e.g. admin, supervisor, qc)") },
            leadingIcon = {
              Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF64748B))
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_id_input"),
            shape = RoundedCornerShape(8.dp)
          )

          // Password Field
          OutlinedTextField(
            value = passwordInput,
            onValueChange = {
              passwordInput = it
              errorMessage = null
            },
            label = { Text("Password (Simple: 123)") },
            leadingIcon = {
              Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B))
            },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = null,
                  tint = Color(0xFF64748B)
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("login_password_input"),
            shape = RoundedCornerShape(8.dp)
          )

          // Error Message Banner
          AnimatedVisibility(visible = errorMessage != null) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFFEE2E2),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = errorMessage ?: "",
                color = Color(0xFFDC2626),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(10.dp)
              )
            }
          }

          // Login Button
          Button(
            onClick = {
              if (usernameInput.isBlank() || passwordInput.isBlank()) {
                errorMessage = "Please enter both User ID and Password."
                return@Button
              }
              val success = repository.login(usernameInput, passwordInput)
              if (success) {
                onLoginSuccess()
              } else {
                errorMessage = "Invalid User ID or Password. Try ID: supervisor / Pass: 123"
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("login_submit_btn"),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NflRoyalBlue)
          ) {
            Text("Login to NFL LIVE", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }

          // Initial Admin Setup Button
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
          ) {
            TextButton(onClick = { showAdminSetupDialog = true }) {
              Text("⚙️ Configure Custom Admin ID", fontSize = 11.sp, color = Color(0xFF0284C7))
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 3. Quick Role Preset Logins (Designation-wise demo accounts)
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Demo Accounts (Designation Wise):",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Surface(
              color = Color(0xFF0369A1),
              shape = RoundedCornerShape(4.dp)
            ) {
              Text(
                text = "Pass: 123",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Text(
            text = "Tap any role to immediately login and test module features:",
            fontSize = 10.sp,
            color = Color(0xFF94A3B8)
          )

          users.forEach { user ->
            Surface(
              onClick = {
                usernameInput = user.id
                passwordInput = user.password
                repository.login(user.id, user.password)
                onLoginSuccess()
              },
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF0F172A),
              border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text(
                      text = user.name,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp,
                      color = Color.White
                    )
                  }
                  Text(
                    text = "ID: ${user.id}  •  Password: ${user.password}",
                    fontSize = 10.sp,
                    color = Color(0xFF38BDF8),
                    fontWeight = FontWeight.SemiBold
                  )
                }

                Surface(
                  color = when (user.role) {
                    UserRole.ADMIN -> Color(0xFF991B1B)
                    UserRole.SUPERVISOR -> Color(0xFF1D4ED8)
                    UserRole.PRODUCTION_REPORTER -> Color(0xFF047857)
                    UserRole.QUALITY_REPORTER -> Color(0xFFB45309)
                    UserRole.AUDITOR -> Color(0xFF7C3AED)
                    UserRole.PRODUCTION_MANAGER, UserRole.QUALITY_MANAGER -> Color(0xFF0F766E)
                    UserRole.MECHANIC -> Color(0xFFC2410C)
                    UserRole.IE -> Color(0xFF4338CA)
                    UserRole.INPUT_MATERIAL -> Color(0xFF475569)
                    UserRole.MANAGEMENT_VIEW -> Color(0xFF0284C7)
                  },
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    text = user.role.displayName,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  // DIALOG: Initial Admin ID & Password Setup
  if (showAdminSetupDialog) {
    AlertDialog(
      onDismissRequest = { showAdminSetupDialog = false },
      title = { Text("Configure Admin Credentials", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Set initial Admin ID and Password for controlling modules and user assignment:", fontSize = 12.sp)

          OutlinedTextField(
            value = newAdminName,
            onValueChange = { newAdminName = it },
            label = { Text("Admin Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = newAdminId,
            onValueChange = { newAdminId = it },
            label = { Text("Admin User ID") },
            placeholder = { Text("e.g. admin") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = newAdminPass,
            onValueChange = { newAdminPass = it },
            label = { Text("Admin Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (newAdminId.isNotBlank() && newAdminPass.isNotBlank()) {
              repository.setInitialAdmin(newAdminId, newAdminPass, newAdminName)
              usernameInput = newAdminId
              passwordInput = newAdminPass
              showAdminSetupDialog = false
              onLoginSuccess()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = NflRoyalBlue)
        ) {
          Text("Save & Login as Admin", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAdminSetupDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
