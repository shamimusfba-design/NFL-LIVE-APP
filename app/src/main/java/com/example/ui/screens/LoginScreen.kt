package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
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

private val LoginBg = Color(0xFF061426)
private val LoginSurface = Color(0xFF0B2038)
private val LoginSurfaceAlt = Color(0xFF0E2945)
private val LoginBorder = Color(0xFF234565)
private val LoginText = Color(0xFFF3F7FC)
private val LoginMuted = Color(0xFFA8BCD2)
private val LoginCyan = Color(0xFF22B8F0)
private val LoginTeal = Color(0xFF20D3A2)

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
  var showAdminSetupDialog by remember { mutableStateOf(false) }
  var newAdminId by remember { mutableStateOf("admin") }
  var newAdminPass by remember { mutableStateOf("123") }
  var newAdminName by remember { mutableStateOf("System Admin") }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Brush.verticalGradient(listOf(Color(0xFF061426), Color(0xFF092543), Color(0xFF04101F))))
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Spacer(Modifier.height(8.dp))

      // Official company logo: reuse the existing vector asset unchanged.
      Surface(
        color = Color.White,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 10.dp,
        border = BorderStroke(1.dp, LoginTeal.copy(alpha = 0.65f)),
        modifier = Modifier.size(78.dp)
      ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(7.dp)) {
          Icon(
            painter = painterResource(com.example.R.drawable.ic_nfl_logo),
            contentDescription = "Northern Fashions Ltd. company logo",
            modifier = Modifier.fillMaxSize(),
            tint = Color.Unspecified
          )
        }
      }
      Spacer(Modifier.height(14.dp))
      Text("NFL LIVE", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp, color = LoginText)
      Spacer(Modifier.height(3.dp))
      Text("NORTHERN FASHIONS LTD.", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp, color = LoginMuted)
      Spacer(Modifier.height(14.dp))
      Surface(
        color = Color(0xFF063A3A).copy(alpha = 0.75f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, LoginTeal.copy(alpha = 0.6f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = LoginTeal, modifier = Modifier.size(15.dp))
          Text("SECURE WORKSPACE", color = LoginTeal, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
      }
      Spacer(Modifier.height(18.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LoginSurface.copy(alpha = 0.96f)),
        border = BorderStroke(1.dp, LoginBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Text("Welcome back", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = LoginText)
          Text("Sign in to your workspace", fontSize = 14.sp, color = LoginMuted)
          Spacer(Modifier.height(2.dp))

          OutlinedTextField(
            value = usernameInput,
            onValueChange = { usernameInput = it; errorMessage = null },
            label = { Text("Employee ID", color = LoginMuted) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = LoginCyan) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("login_id_input"),
            shape = RoundedCornerShape(14.dp),
            colors = loginFieldColors()
          )
          OutlinedTextField(
            value = passwordInput,
            onValueChange = { passwordInput = it; errorMessage = null },
            label = { Text("Password", color = LoginMuted) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = LoginCyan) },
            trailingIcon = {
              IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                  imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                  contentDescription = if (passwordVisible) "Hide password" else "Show password",
                  tint = LoginMuted
                )
              }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().testTag("login_password_input"),
            shape = RoundedCornerShape(14.dp),
            colors = loginFieldColors()
          )

          AnimatedVisibility(visible = errorMessage != null) {
            Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF4A1F2B), modifier = Modifier.fillMaxWidth()) {
              Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFFFA3AE), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(errorMessage ?: "", color = Color(0xFFFFC6CD), fontSize = 12.sp)
              }
            }
          }

          Button(
            onClick = {
              if (usernameInput.isBlank() || passwordInput.isBlank()) {
                errorMessage = "Please enter both Employee ID and password."
                return@Button
              }
              if (repository.login(usernameInput, passwordInput)) onLoginSuccess()
              else errorMessage = "The Employee ID or password is incorrect."
            },
            modifier = Modifier.fillMaxWidth().height(54.dp).testTag("login_submit_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LoginCyan, contentColor = Color(0xFF041526))
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(10.dp))
            Text("Sign in securely", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }

          TextButton(onClick = { errorMessage = "Please contact your system administrator for sign-in assistance." }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Need help signing in?", color = LoginCyan, fontSize = 12.sp)
          }

          // Single developer attribution placed directly below Need Help
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 2.dp, bottom = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "This app is developed by Shamim",
              color = Color(0xFF67E8F9),
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(2.dp))
            Text(
              text = "IPE & Planning",
              color = LoginMuted,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }

          HorizontalDivider(color = LoginBorder)
          TextButton(
            onClick = { showAdminSetupDialog = true },
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = LoginMuted, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Configure administrator access", color = LoginMuted, fontSize = 12.sp)
          }
        }
      }

      Spacer(Modifier.height(18.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LoginSurface.copy(alpha = 0.82f)),
        border = BorderStroke(1.dp, LoginBorder)
      ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Quick access", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LoginText)
            Spacer(Modifier.weight(1f))
            Text("ROLE SHORTCUTS", fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = LoginMuted)
          }
          Text("Select your account to fill the sign-in fields.", fontSize = 11.sp, color = LoginMuted)
          users.forEach { user ->
            Surface(
              onClick = {
                usernameInput = user.id
                passwordInput = user.password
                errorMessage = null
              },
              shape = RoundedCornerShape(13.dp),
              color = LoginSurfaceAlt,
              border = BorderStroke(1.dp, LoginBorder),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(LoginCyan.copy(alpha = 0.12f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = when (user.role) {
                      UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                      UserRole.SUPERVISOR -> Icons.Default.Groups
                      UserRole.QUALITY_REPORTER, UserRole.QUALITY_MANAGER, UserRole.AUDITOR -> Icons.Default.Verified
                      else -> Icons.Default.Person
                    },
                    contentDescription = null,
                    tint = LoginCyan,
                    modifier = Modifier.size(21.dp)
                  )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                  Text(user.name, color = LoginText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                  Text(user.role.displayName, color = LoginMuted, fontSize = 11.sp)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = "Select account", tint = LoginMuted)
              }
            }
          }
        }
      }
      Spacer(Modifier.height(22.dp))
      Text("NORTHERN FASHIONS LTD.", color = LoginMuted.copy(alpha = 0.85f), fontSize = 10.sp, letterSpacing = 1.5.sp)
      Spacer(Modifier.height(8.dp))
    }
  }

  if (showAdminSetupDialog) {
    AlertDialog(
      onDismissRequest = { showAdminSetupDialog = false },
      containerColor = LoginSurface,
      titleContentColor = LoginText,
      textContentColor = LoginMuted,
      title = { Text("Configure administrator access", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Set the initial administrator account for this device.", fontSize = 12.sp)
          OutlinedTextField(value = newAdminName, onValueChange = { newAdminName = it }, label = { Text("Administrator name") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = loginFieldColors())
          OutlinedTextField(value = newAdminId, onValueChange = { newAdminId = it }, label = { Text("Administrator ID") }, singleLine = true, modifier = Modifier.fillMaxWidth(), colors = loginFieldColors())
          OutlinedTextField(
            value = newAdminPass, onValueChange = { newAdminPass = it }, label = { Text("Administrator password") },
            visualTransformation = PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth(), colors = loginFieldColors()
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
          colors = ButtonDefaults.buttonColors(containerColor = LoginCyan, contentColor = LoginBg)
        ) { Text("Save & sign in", fontWeight = FontWeight.Bold) }
      },
      dismissButton = { TextButton(onClick = { showAdminSetupDialog = false }) { Text("Cancel", color = LoginMuted) } }
    )
  }
}

@Composable
private fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
  focusedTextColor = LoginText,
  unfocusedTextColor = LoginText,
  focusedBorderColor = LoginCyan,
  unfocusedBorderColor = LoginBorder,
  cursorColor = LoginCyan,
  focusedContainerColor = LoginBg.copy(alpha = 0.35f),
  unfocusedContainerColor = LoginBg.copy(alpha = 0.35f),
  focusedLabelColor = LoginCyan,
  unfocusedLabelColor = LoginMuted
)
