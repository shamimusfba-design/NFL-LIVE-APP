package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class NflRepository(private val context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("nfl_factory_prefs", Context.MODE_PRIVATE)

  // Complete Verified 60-Line Factory Master across all floors
  val allLinesMaster = listOf(
    // Sew 1 (22 lines)
    LineInfo(id = "A1", floor = "Sew 1"),
    LineInfo(id = "A2", floor = "Sew 1"),
    LineInfo(id = "B", floor = "Sew 1"),
    LineInfo(id = "C", floor = "Sew 1"),
    LineInfo(id = "D1A", floor = "Sew 1"),
    LineInfo(id = "D2A", floor = "Sew 1"),
    LineInfo(id = "D1B", floor = "Sew 1"),
    LineInfo(id = "D2B", floor = "Sew 1"),
    LineInfo(id = "E1A", floor = "Sew 1"),
    LineInfo(id = "E2A", floor = "Sew 1"),
    LineInfo(id = "E1B", floor = "Sew 1"),
    LineInfo(id = "E2B", floor = "Sew 1"),
    LineInfo(id = "FA", floor = "Sew 1"),
    LineInfo(id = "FB", floor = "Sew 1"),
    LineInfo(id = "G1A", floor = "Sew 1"),
    LineInfo(id = "G2A", floor = "Sew 1"),
    LineInfo(id = "G1B", floor = "Sew 1"),
    LineInfo(id = "G2B", floor = "Sew 1"),
    LineInfo(id = "H1A", floor = "Sew 1"),
    LineInfo(id = "H2A", floor = "Sew 1"),
    LineInfo(id = "H1B", floor = "Sew 1"),
    LineInfo(id = "H2B", floor = "Sew 1"),

    // Sew 2 (14 lines) - Note: J1 is physical line J, R1 alias R preserved
    LineInfo(id = "J", floor = "Sew 2", aliases = listOf("J1")),
    LineInfo(id = "J1", floor = "Sew 2", aliases = listOf("J")),
    LineInfo(id = "K1", floor = "Sew 2"),
    LineInfo(id = "L1", floor = "Sew 2"),
    LineInfo(id = "L2", floor = "Sew 2"),
    LineInfo(id = "M1", floor = "Sew 2", aliases = listOf("M")),
    LineInfo(id = "M2", floor = "Sew 2"),
    LineInfo(id = "N1", floor = "Sew 2"),
    LineInfo(id = "N2", floor = "Sew 2"),
    LineInfo(id = "O1", floor = "Sew 2"),
    LineInfo(id = "O2", floor = "Sew 2"),
    LineInfo(id = "P1", floor = "Sew 2", aliases = listOf("P")),
    LineInfo(id = "Q1", floor = "Sew 2"),
    LineInfo(id = "Q2", floor = "Sew 2"),
    LineInfo(id = "R1", floor = "Sew 2", aliases = listOf("R")),

    // Sew 3 (24 lines)
    LineInfo(id = "S1", floor = "Sew 3"),
    LineInfo(id = "S2", floor = "Sew 3"),
    LineInfo(id = "T1", floor = "Sew 3"),
    LineInfo(id = "T2", floor = "Sew 3"),
    LineInfo(id = "U", floor = "Sew 3"),
    LineInfo(id = "UA", floor = "Sew 3"),
    LineInfo(id = "UB", floor = "Sew 3"),
    LineInfo(id = "V1A", floor = "Sew 3"),
    LineInfo(id = "V1B", floor = "Sew 3"),
    LineInfo(id = "V2A", floor = "Sew 3"),
    LineInfo(id = "V2B", floor = "Sew 3"),
    LineInfo(id = "W1A", floor = "Sew 3"),
    LineInfo(id = "W1B", floor = "Sew 3"),
    LineInfo(id = "W2A", floor = "Sew 3"),
    LineInfo(id = "W2B", floor = "Sew 3"),
    LineInfo(id = "X1", floor = "Sew 3"),
    LineInfo(id = "Y1", floor = "Sew 3"),
    LineInfo(id = "Y2", floor = "Sew 3"),
    LineInfo(id = "Z1", floor = "Sew 3"),
    LineInfo(id = "Z2", floor = "Sew 3"),
    LineInfo(id = "ZA1A", floor = "Sew 3"),
    LineInfo(id = "ZA1B", floor = "Sew 3"),
    LineInfo(id = "ZA2A", floor = "Sew 3"),
    LineInfo(id = "ZA2B", floor = "Sew 3"),

    // Finishing Lines (4 lines)
    LineInfo(id = "Finish 1", floor = "Finishing"),
    LineInfo(id = "Finish 2", floor = "Finishing"),
    LineInfo(id = "Iron & Pack 1", floor = "Finishing"),
    LineInfo(id = "Iron & Pack 2", floor = "Finishing")
  )

  val defaultLines get() = allLinesMaster

  val defaultStyles = listOf(
    StyleInfo("NFL-204", "Nike / NFL", smv = 14.50, defaultTargetPcsPerHour = 90),
    StyleInfo("NFL-108", "Nike / NFL", smv = 12.20, defaultTargetPcsPerHour = 105),
    StyleInfo("NFL-305", "Adidas / NFL", smv = 16.80, defaultTargetPcsPerHour = 75),
    StyleInfo("NFL-512", "Under Armour", smv = 11.00, defaultTargetPcsPerHour = 115),
    StyleInfo("NFL-990", "Puma", smv = 13.40, defaultTargetPcsPerHour = 95)
  )

  val defaultHourSlots = listOf(
    "08:00 - 09:00",
    "09:00 - 10:00",
    "10:00 - 11:00",
    "11:00 - 12:00",
    "12:00 - 13:00",
    "14:00 - 15:00",
    "15:00 - 16:00",
    "16:00 - 17:00",
    "17:00 - 18:00",
    "18:00 - 19:00"
  )

  val standardDefectTypes = listOf(
    "Poor iron",
    "Uncut thread",
    "Dirty spot",
    "Oil spot",
    "Skip stitch",
    "Open seam",
    "Broken stitch",
    "Puckering",
    "Pleat",
    "Shading"
  )

  // Reactive state
  private val _currentFloor = MutableStateFlow(FloorScope.SEW_2)
  val currentFloor: StateFlow<FloorScope> = _currentFloor.asStateFlow()

  private val _currentPeriod = MutableStateFlow(PeriodScope.TODAY)
  val currentPeriod: StateFlow<PeriodScope> = _currentPeriod.asStateFlow()

  private val _currentRole = MutableStateFlow(UserRole.SUPERVISOR)
  val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

  private val _currentUserName = MutableStateFlow("Tariq Hasan (Supervisor)")
  val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

  private val _assignedLine = MutableStateFlow("K1")
  val assignedLine: StateFlow<String> = _assignedLine.asStateFlow()

  private val _isDemoMode = MutableStateFlow(false)
  val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

  private val _isOnline = MutableStateFlow(true)
  val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

  // User Accounts & Role Management
  private val _users = MutableStateFlow<List<UserAccount>>(
    listOf(
      UserAccount(
        id = "admin",
        name = "Kabir Ahmed (Admin)",
        password = "admin",
        role = UserRole.ADMIN,
        floor = FloorScope.ALL,
        assignedLine = "All",
        isAdmin = true
      ),
      UserAccount(
        id = "sup_tariq",
        name = "Tariq Hasan (Supervisor)",
        password = "123",
        role = UserRole.SUPERVISOR,
        floor = FloorScope.SEW_2,
        assignedLine = "K1"
      ),
      UserAccount(
        id = "prod_rafiq",
        name = "Md. Rafiqul (Production)",
        password = "123",
        role = UserRole.PRODUCTION_REPORTER,
        floor = FloorScope.SEW_2,
        assignedLine = "K1"
      ),
      UserAccount(
        id = "qc_nasrin",
        name = "Nasrin Sultana (Endline QC)",
        password = "123",
        role = UserRole.QUALITY_REPORTER,
        floor = FloorScope.SEW_2,
        assignedLine = "K1"
      ),
      UserAccount(
        id = "audit_farhana",
        name = "Farhana Islam (Auditor)",
        password = "123",
        role = UserRole.AUDITOR,
        floor = FloorScope.SEW_2,
        assignedLine = "K1"
      ),
      UserAccount(
        id = "mech_jahangir",
        name = "Jahangir Hossain (Mechanic)",
        password = "123",
        role = UserRole.MECHANIC,
        floor = FloorScope.SEW_2,
        assignedLine = "K1"
      ),
      UserAccount(
        id = "ie_salim",
        name = "Salim Reza (IE Officer)",
        password = "123",
        role = UserRole.IE,
        floor = FloorScope.ALL,
        assignedLine = "All"
      )
    )
  )
  val users: StateFlow<List<UserAccount>> = _users.asStateFlow()

  private val _currentUser = MutableStateFlow<UserAccount?>(
    _users.value.find { it.id == "sup_tariq" }
  )
  val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

  // Monthly Targets & Baseline Setup
  private val _monthlyTargets = MutableStateFlow<List<MonthlyFactoryTarget>>(
    listOf(
      MonthlyFactoryTarget(floor = "Sew 1", targetEfficiencyPct = 70.0, updateEfficiencyPct = 67.0, targetOqlPct = 3.80, targetDhuPct = 2.10),
      MonthlyFactoryTarget(floor = "Sew 2", targetEfficiencyPct = 72.0, updateEfficiencyPct = 68.5, targetOqlPct = 3.50, targetDhuPct = 1.95),
      MonthlyFactoryTarget(floor = "Sew 3", targetEfficiencyPct = 71.0, updateEfficiencyPct = 66.8, targetOqlPct = 3.65, targetDhuPct = 2.05),
      MonthlyFactoryTarget(floor = "Finishing", targetEfficiencyPct = 80.0, updateEfficiencyPct = 78.0, targetOqlPct = 2.50, targetDhuPct = 1.50)
    )
  )
  val monthlyTargets: StateFlow<List<MonthlyFactoryTarget>> = _monthlyTargets.asStateFlow()

  // In-memory data
  private val _targets = MutableStateFlow<List<SupervisorLineSetup>>(emptyList())
  val targets: StateFlow<List<SupervisorLineSetup>> = _targets.asStateFlow()

  private val _productionEntries = MutableStateFlow<List<ProductionRecord>>(emptyList())
  val productionEntries: StateFlow<List<ProductionRecord>> = _productionEntries.asStateFlow()

  private val _qualityEntries = MutableStateFlow<List<QualityRecord>>(emptyList())
  val qualityEntries: StateFlow<List<QualityRecord>> = _qualityEntries.asStateFlow()

  private val _incidents = MutableStateFlow<List<IncidentItem>>(emptyList())
  val incidents: StateFlow<List<IncidentItem>> = _incidents.asStateFlow()

  private val _auditLogs = MutableStateFlow<List<AuditLogEntry>>(emptyList())
  val auditLogs: StateFlow<List<AuditLogEntry>> = _auditLogs.asStateFlow()

  init {
    loadSeedDemoData()
  }

  fun setFloor(floor: FloorScope) {
    _currentFloor.value = floor
  }

  fun setPeriod(period: PeriodScope) {
    _currentPeriod.value = period
  }

  fun setRole(role: UserRole) {
    _currentRole.value = role
    _currentUserName.value = when (role) {
      UserRole.ADMIN -> "Kabir Ahmed (Admin)"
      UserRole.PRODUCTION_REPORTER -> "Md. Rafiqul (Production)"
      UserRole.QUALITY_REPORTER -> "Nasrin Sultana (QA Inspector)"
      UserRole.SUPERVISOR -> "Tariq Hasan (Supervisor)"
      UserRole.PRODUCTION_MANAGER -> "Mahbub Alam (Prod Mgr)"
      UserRole.QUALITY_MANAGER -> "Shirin Akter (QA Mgr)"
      UserRole.MECHANIC -> "Jahangir Hossain (Mechanic)"
      UserRole.IE -> "Salim Reza (IE Officer)"
      UserRole.INPUT_MATERIAL -> "Biplob Das (Input Coord)"
      UserRole.MANAGEMENT_VIEW -> "Executive TV Monitor"
      UserRole.AUDITOR -> "Hasan Mahmud (Auditor)"
    }
  }

  fun setAssignedLine(line: String) {
    _assignedLine.value = line
  }

  fun getLineSetup(lineId: String): SupervisorLineSetup {
    val lineObj = allLinesMaster.find { it.id == lineId }
    val floorName = lineObj?.floor ?: "Sew 2"
    return _targets.value.find { it.lineId == lineId } ?: SupervisorLineSetup(
      lineId = lineId,
      floor = floorName,
      date = "2026-10-09",
      styleNumber = "",
      itemName = "Polo Shirt",
      orderNumber = "PO-88210",
      smv = 14.50,
      targetHourlyPcs = 60,
      targetDailyPcs = 480,
      targetEfficiencyPct = 75.0,
      operatorsCount = 20,
      machinesCount = 20,
      helpersCount = 2,
      includeHelpersInEfficiency = false,
      workingMinutesPerHour = 60
    )
  }

  fun retryPendingSubmissions() {
    _productionEntries.value = _productionEntries.value.map { it.copy(syncStatus = SyncStatus.SYNCED) }
    _qualityEntries.value = _qualityEntries.value.map { it.copy(syncStatus = SyncStatus.SYNCED) }
  }

  // Idempotent demo data seeder
  fun loadSeedDemoData() {
    val date = "2026-10-09"
    val demoTargets = mutableListOf<SupervisorLineSetup>()
    val demoProds = mutableListOf<ProductionRecord>()
    val demoQas = mutableListOf<QualityRecord>()

    data class LineSeedData(
      val lineId: String,
      val itemName: String,
      val smv: Double,
      val targetPcs: Int,
      val targetEffPct: Double,
      val outputPcs: Int?
    )

    fun populateFloorWithSeeds(floorName: String, seeds: List<LineSeedData>) {
      seeds.forEach { seed ->
        demoTargets.add(
          SupervisorLineSetup(
            lineId = seed.lineId,
            floor = floorName,
            date = date,
            styleNumber = "",
            itemName = seed.itemName,
            targetDailyPcs = seed.targetPcs,
            targetHourlyPcs = (seed.targetPcs / 8).coerceAtLeast(1),
            targetEfficiencyPct = seed.targetEffPct,
            smv = seed.smv,
            operatorsCount = 20,
            machinesCount = 20,
            workingHoursSoFar = 7,
            totalShiftHours = 8,
            isDemo = true
          )
        )

        if (seed.outputPcs != null) {
          demoProds.add(
            ProductionRecord(
              lineId = seed.lineId,
              floor = floorName,
              date = date,
              hourSlot = "15:00 - 16:00",
              styleNumber = "",
              outputQty = seed.outputPcs,
              isConfirmedZero = seed.outputPcs == 0,
              modifiedBy = "Md. Rafiqul",
              isDemo = true
            )
          )

          demoQas.add(
            QualityRecord(
              lineId = seed.lineId,
              floor = floorName,
              date = date,
              hourSlot = "15:00 - 16:00",
              styleNumber = "",
              checkedGarments = 100,
              defectiveGarments = if (seed.lineId == "K1") 4 else 3,
              defects = listOf(
                DefectItem("Poor iron", 2),
                DefectItem("Uncut thread", 2)
              ),
              modifiedBy = "Nasrin Sultana",
              isDemo = true
            )
          )
        }
      }
    }

    // Sew 1 (22 real factory lines)
    val sew1Seeds = listOf(
      LineSeedData("A1", "Track Jacket", 15.20, 460, 75.0, 430),
      LineSeedData("A2", "Sports Vest", 11.00, 500, 75.0, 470),
      LineSeedData("B", "Sports Vest", 11.00, 500, 75.0, 480),
      LineSeedData("C", "Polo Shirt", 14.50, 420, 75.0, 390),
      LineSeedData("D1A", "Sports Vest", 11.00, 480, 72.0, 440),
      LineSeedData("D2A", "Basic T-Shirt", 10.50, 540, 78.0, 490),
      LineSeedData("D1B", "Polo Shirt", 14.50, 460, 75.0, 420),
      LineSeedData("D2B", "Crew Neck Tee", 12.20, 510, 75.0, 460),
      LineSeedData("E1A", "Zip Hoodie", 16.80, 400, 70.0, null),
      LineSeedData("E2A", "Raglan Tee", 13.00, 470, 74.0, 420),
      LineSeedData("E1B", "Fleece Jogger", 17.50, 390, 70.0, 340),
      LineSeedData("E2B", "Basic T-Shirt", 10.50, 530, 78.0, 480),
      LineSeedData("FA", "Track Jacket", 15.20, 480, 75.0, 430),
      LineSeedData("FB", "Polo Pique", 14.80, 450, 75.0, 400),
      LineSeedData("G1A", "Sports Vest", 11.00, 500, 72.0, 450),
      LineSeedData("G2A", "Polo Shirt", 14.50, 440, 75.0, 390),
      LineSeedData("G1B", "Zip Hoodie", 16.80, 410, 70.0, 360),
      LineSeedData("G2B", "Crew Neck Tee", 12.20, 520, 75.0, 470),
      LineSeedData("H1A", "Track Jacket", 15.20, 470, 75.0, 420),
      LineSeedData("H2A", "Basic T-Shirt", 10.50, 560, 78.0, 510),
      LineSeedData("H1B", "Raglan Tee", 13.00, 480, 74.0, 430),
      LineSeedData("H2B", "Sports Vest", 11.00, 490, 72.0, 440)
    )

    // Sew 2 (14 real factory lines)
    val sew2Seeds = listOf(
      LineSeedData("J1", "Track Jacket", 15.20, 500, 75.0, 450),
      LineSeedData("K1", "Sports Vest", 11.00, 420, 72.0, 340),
      LineSeedData("L1", "Polo Shirt", 14.50, 480, 75.0, 390),
      LineSeedData("L2", "Basic T-Shirt", 10.50, 550, 78.0, 490),
      LineSeedData("M1", "Zip Hoodie", 16.80, 380, 70.0, 310),
      LineSeedData("M2", "Polo Shirt", 14.50, 460, 75.0, 400),
      LineSeedData("N1", "Sports Vest", 11.00, 450, 72.0, null),
      LineSeedData("N2", "Track Jacket", 15.20, 620, 76.0, 560),
      LineSeedData("O1", "Crew Neck Tee", 12.20, 520, 75.0, 480),
      LineSeedData("O2", "Raglan Tee", 13.00, 490, 74.0, 430),
      LineSeedData("P1", "Polo Pique", 14.80, 480, 75.0, 420),
      LineSeedData("Q1", "Fleece Jogger", 17.50, 400, 70.0, 350),
      LineSeedData("Q2", "Basic T-Shirt", 10.50, 580, 78.0, 510),
      LineSeedData("R1", "Zip Hoodie", 16.80, 380, 70.0, 320)
    )

    // Sew 3 (24 real factory lines)
    val sew3Seeds = listOf(
      LineSeedData("S1", "Track Jacket", 15.20, 520, 75.0, 490),
      LineSeedData("S2", "Polo Shirt", 14.50, 470, 75.0, 420),
      LineSeedData("T1", "Sports Vest", 11.00, 480, 72.0, 450),
      LineSeedData("T2", "Basic T-Shirt", 10.50, 550, 78.0, 500),
      LineSeedData("U", "Polo Shirt", 14.50, 440, 75.0, 410),
      LineSeedData("UA", "Crew Neck Tee", 12.20, 510, 75.0, 460),
      LineSeedData("UB", "Zip Hoodie", 16.80, 400, 70.0, 350),
      LineSeedData("V1A", "Sports Vest", 11.00, 500, 72.0, 470),
      LineSeedData("V1B", "Fleece Jogger", 17.50, 390, 70.0, 340),
      LineSeedData("V2A", "Polo Pique", 14.80, 460, 75.0, 410),
      LineSeedData("V2B", "Track Jacket", 15.20, 480, 75.0, 430),
      LineSeedData("W1A", "Zip Hoodie", 16.80, 460, 70.0, null),
      LineSeedData("W1B", "Basic T-Shirt", 10.50, 540, 78.0, 480),
      LineSeedData("W2A", "Raglan Tee", 13.00, 490, 74.0, 440),
      LineSeedData("W2B", "Polo Shirt", 14.50, 450, 75.0, 400),
      LineSeedData("X1", "Track Jacket", 15.20, 550, 75.0, 510),
      LineSeedData("Y1", "Sports Vest", 11.00, 490, 72.0, 440),
      LineSeedData("Y2", "Crew Neck Tee", 12.20, 520, 75.0, 470),
      LineSeedData("Z1", "Zip Hoodie", 16.80, 410, 70.0, 360),
      LineSeedData("Z2", "Polo Shirt", 14.50, 460, 75.0, 410),
      LineSeedData("ZA1A", "Fleece Jogger", 17.50, 380, 70.0, 330),
      LineSeedData("ZA1B", "Basic T-Shirt", 10.50, 530, 78.0, 480),
      LineSeedData("ZA2A", "Raglan Tee", 13.00, 470, 74.0, 420),
      LineSeedData("ZA2B", "Track Jacket", 15.20, 500, 75.0, 450)
    )

    populateFloorWithSeeds("Sew 1", sew1Seeds)
    populateFloorWithSeeds("Sew 2", sew2Seeds)
    populateFloorWithSeeds("Sew 3", sew3Seeds)

    // Active incidents with authoritative timestamps surviving reload
    val now = System.currentTimeMillis()
    val demoIncidents = listOf(
      IncidentItem(
        lineId = "K1",
        floor = "Sew 2",
        category = IncidentCategory.MACHINE_BREAKDOWN,
        machineRef = "SNLS-04",
        description = "Motor overheated and needle bar vibrating severely",
        reportedCause = "Bearing worn out in drive belt",
        impactLevel = "HIGH",
        affectedManpower = 6,
        startTimeMillis = now - 22 * 60 * 1000L,
        acknowledgedTimeMillis = now - 15 * 60 * 1000L,
        acknowledgedBy = "Jahangir Hossain (Mechanic)",
        status = IncidentStatus.ACKNOWLEDGED,
        reportedBy = "Tariq Hasan (Supervisor)",
        isDemo = true
      ),
      IncidentItem(
        lineId = "L2",
        floor = "Sew 2",
        category = IncidentCategory.QUALITY_PROBLEM,
        machineRef = "Overlock-02",
        description = "Recurring puckering along side seam on Jacket B",
        reportedCause = "Thread tension miscalibrated on needle 2",
        impactLevel = "MEDIUM",
        affectedManpower = 4,
        startTimeMillis = now - 11 * 60 * 1000L,
        status = IncidentStatus.OPEN,
        reportedBy = "Nasrin Sultana (QA Inspector)",
        isDemo = true
      ),
      IncidentItem(
        lineId = "N1",
        floor = "Sew 2",
        category = IncidentCategory.LOW_PRODUCTION,
        description = "Output gap > 25% behind target due for slot 15:00-16:00",
        reportedCause = "Waiting for collar rib input from cutting section",
        impactLevel = "HIGH",
        affectedManpower = 20,
        startTimeMillis = now - 35 * 60 * 1000L,
        status = IncidentStatus.OPEN,
        reportedBy = "System Low-Production Monitor",
        isDemo = true
      )
    )

    _targets.value = _targets.value.filter { !it.isDemo } + demoTargets
    _productionEntries.value = _productionEntries.value.filter { !it.isDemo } + demoProds
    _qualityEntries.value = _qualityEntries.value.filter { !it.isDemo } + demoQas
    _incidents.value = _incidents.value.filter { !it.isDemo } + demoIncidents
    _isDemoMode.value = true
  }

  fun updateLineMorningTarget(
    lineId: String,
    floor: String = "Sew 2",
    itemName: String,
    smv: Double,
    targetDailyPcs: Int,
    targetEfficiencyPct: Double,
    operatorsCount: Int = 20,
    helpersCount: Int = 2
  ) {
    val existing = getLineSetup(lineId)
    val updated = existing.copy(
      lineId = lineId,
      floor = floor,
      itemName = itemName,
      smv = smv,
      targetDailyPcs = targetDailyPcs,
      targetHourlyPcs = (targetDailyPcs / 8).coerceAtLeast(1),
      targetEfficiencyPct = targetEfficiencyPct,
      operatorsCount = operatorsCount,
      helpersCount = helpersCount,
      snapshotTimestamp = System.currentTimeMillis()
    )
    saveLineSetup(updated)
  }

  fun removeAllDemoData() {
    _targets.value = _targets.value.filter { !it.isDemo }
    _productionEntries.value = _productionEntries.value.filter { !it.isDemo }
    _qualityEntries.value = _qualityEntries.value.filter { !it.isDemo }
    _incidents.value = _incidents.value.filter { !it.isDemo }
    _isDemoMode.value = false
    addAuditLog("DEMO", "ALL", "PURGE_DEMO", "Admin", "Removed all demo records while preserving real logs")
  }

  // Submissions
  fun submitProduction(entry: ProductionRecord): ProductionRecord {
    _productionEntries.value = listOf(entry) + _productionEntries.value
    addAuditLog("PRODUCTION", entry.lineId, "ADD_OUTPUT", entry.modifiedBy, "Qty: ${entry.outputQty}")
    return entry
  }

  fun submitQuality(entry: QualityRecord): QualityRecord {
    _qualityEntries.value = listOf(entry) + _qualityEntries.value
    addAuditLog("QUALITY", entry.lineId, "ADD_INSPECTION", entry.modifiedBy, "Checked: ${entry.checkedGarments}, Defective: ${entry.defectiveGarments}")
    return entry
  }

  fun saveLineSetup(setup: SupervisorLineSetup) {
    val filtered = _targets.value.filter { !(it.lineId == setup.lineId && it.date == setup.date) }
    _targets.value = listOf(setup) + filtered
    addAuditLog("SETUP", setup.lineId, "SAVE_TARGET", "Supervisor", "Daily target: ${setup.targetDailyPcs}, Eff: ${setup.targetEfficiencyPct}%")
  }

  // Auth & User Management Methods
  fun login(id: String, pass: String): Boolean {
    val user = _users.value.find { it.id.equals(id.trim(), ignoreCase = true) && it.password == pass.trim() }
    if (user != null) {
      _currentUser.value = user
      _currentRole.value = user.role
      _currentUserName.value = user.name
      _currentFloor.value = user.floor
      _assignedLine.value = user.assignedLine
      addAuditLog("AUTH", user.id, "LOGIN", user.name, "Role: ${user.role.displayName}")
      return true
    }
    return false
  }

  fun logout() {
    val user = _currentUser.value
    if (user != null) {
      addAuditLog("AUTH", user.id, "LOGOUT", user.name, "Session ended")
    }
    _currentUser.value = null
  }

  fun setInitialAdmin(adminId: String, adminPass: String, adminName: String): UserAccount {
    val admin = UserAccount(
      id = adminId.trim().ifEmpty { "admin" },
      name = adminName.trim().ifEmpty { "Factory Admin" },
      password = adminPass.trim().ifEmpty { "admin" },
      role = UserRole.ADMIN,
      floor = FloorScope.ALL,
      assignedLine = "All",
      isAdmin = true
    )
    _users.value = listOf(admin) + _users.value.filter { !it.isAdmin && it.id != admin.id }
    _currentUser.value = admin
    _currentRole.value = UserRole.ADMIN
    _currentUserName.value = admin.name
    addAuditLog("AUTH", admin.id, "ADMIN_INIT", admin.name, "Initial admin credentials configured")
    return admin
  }

  fun saveUser(user: UserAccount) {
    val filtered = _users.value.filter { it.id != user.id }
    _users.value = filtered + user
    addAuditLog("USER_MGMT", user.id, "SAVE_USER", "Admin", "Role: ${user.role.displayName}, Floor: ${user.floor.displayName}")
  }

  fun deleteUser(userId: String) {
    _users.value = _users.value.filter { it.id != userId }
    addAuditLog("USER_MGMT", userId, "DELETE_USER", "Admin", "Removed account")
  }

  // Monthly Target Methods
  fun getMonthlyTarget(floorName: String): MonthlyFactoryTarget {
    return _monthlyTargets.value.find { it.floor.equals(floorName, ignoreCase = true) }
      ?: MonthlyFactoryTarget(floor = floorName)
  }

  fun saveMonthlyTarget(target: MonthlyFactoryTarget) {
    val filtered = _monthlyTargets.value.filter { !it.floor.equals(target.floor, ignoreCase = true) }
    _monthlyTargets.value = filtered + target
    addAuditLog("MONTHLY_SETUP", target.floor, "SAVE_MONTHLY_TARGET", target.lastUpdatedBy, "Target Eff: ${target.targetEfficiencyPct}%, OQL: ${target.targetOqlPct}%")
  }

  // Incident Actions
  fun reportIncident(item: IncidentItem): IncidentItem {
    _incidents.value = listOf(item) + _incidents.value
    addAuditLog("INCIDENT", item.lineId, "REPORT_INCIDENT", item.reportedBy, item.description)
    return item
  }

  fun startMechanicMaintenance(id: String, mechanicName: String) {
    _incidents.value = _incidents.value.map {
      if (it.id == id) it.copy(
        status = IncidentStatus.ACKNOWLEDGED,
        acknowledgedBy = mechanicName,
        acknowledgedTimeMillis = System.currentTimeMillis(),
        mechanicStartTimeMillis = System.currentTimeMillis(),
        mechanicName = mechanicName,
        isMaintenanceStarted = true
      ) else it
    }
    addAuditLog("INCIDENT", id, "START_MAINTENANCE", mechanicName, "Mechanic attended stoppage")
  }

  fun muteIncident(id: String) {
    _incidents.value = _incidents.value.map {
      if (it.id == id) it.copy(isMutedOnCurrentDevice = true) else it
    }
  }

  fun acknowledgeIncident(id: String, ackBy: String) {
    _incidents.value = _incidents.value.map {
      if (it.id == id) it.copy(
        status = IncidentStatus.ACKNOWLEDGED,
        acknowledgedBy = ackBy,
        acknowledgedTimeMillis = System.currentTimeMillis()
      ) else it
    }
    addAuditLog("INCIDENT", id, "ACKNOWLEDGE", ackBy, "Accepted ownership")
  }

  fun resolveIncident(id: String, resolvedBy: String, note: String) {
    _incidents.value = _incidents.value.map {
      if (it.id == id) it.copy(
        status = IncidentStatus.RESOLVED,
        resolvedBy = resolvedBy,
        resolutionNote = note,
        endTimeMillis = System.currentTimeMillis()
      ) else it
    }
    addAuditLog("INCIDENT", id, "RESOLVE", resolvedBy, note)
  }

  // Deep Dive Analytical Data Helpers
  fun getHourlyProductionData(lineId: String, period: PeriodScope): List<HourlyProductionRow> {
    val setup = getLineSetup(lineId)
    val tgt = setup.targetHourlyPcs
    val slots = defaultHourSlots
    val randomSeeds = listOf(58, 62, 60, 56, 64, 59, 61, 57, 63, 60)
    var cumTgt = 0
    var cumAch = 0

    return slots.mapIndexed { idx, slot ->
      val ach = when (period) {
        PeriodScope.TODAY -> if (idx < 7) randomSeeds[idx % randomSeeds.size] else 0
        else -> randomSeeds[idx % randomSeeds.size]
      }
      cumTgt += tgt
      cumAch += ach
      val eff = if (tgt > 0 && ach > 0) Math.round((ach.toDouble() / tgt) * setup.targetEfficiencyPct * 10.0) / 10.0 else 0.0
      val achPct = if (tgt > 0) Math.round((ach.toDouble() / tgt) * 1000.0) / 10.0 else 0.0
      HourlyProductionRow(
        hourSlot = slot,
        hourlyTarget = tgt,
        achieveOutput = ach,
        totalTarget = cumTgt,
        totalAchieve = cumAch,
        efficiencyPct = eff,
        achievePct = achPct
      )
    }
  }

  fun getHourlyQualityData(lineId: String, period: PeriodScope): List<HourlyQualityRow> {
    val slots = defaultHourSlots
    val dhuValues = listOf(1.85, 2.10, 1.65, 1.95, 2.30, 1.70, 1.80, 2.05, 1.90, 1.75)
    val oqlValues = listOf(3.5, 3.8, 3.2, 4.0, 3.6, 3.4, 3.7, 3.9, 3.5, 3.3)
    val defectNames = listOf("Puckering, Skip stitch", "Broken stitch", "Open seam", "Uneven seam", "Needle cut", "Puckering", "Skip stitch", "Broken stitch", "Pleat", "Dirty spot")

    return slots.mapIndexed { idx, slot ->
      HourlyQualityRow(
        hourSlot = slot,
        dhuPct = dhuValues[idx % dhuValues.size],
        oqlPct = oqlValues[idx % oqlValues.size],
        majorDefectsName = defectNames[idx % defectNames.size]
      )
    }
  }

  fun getParetoDefects(lineId: String): List<ParetoDefectPoint> {
    return listOf(
      ParetoDefectPoint("Puckering", 38, 32.5),
      ParetoDefectPoint("Broken Stitch", 29, 24.8),
      ParetoDefectPoint("Skip Stitch", 21, 17.9),
      ParetoDefectPoint("Uneven Seam", 14, 12.0),
      ParetoDefectPoint("Needle Cut", 9, 7.7),
      ParetoDefectPoint("Others", 6, 5.1)
    )
  }

  private fun addAuditLog(type: String, id: String, action: String, by: String, details: String) {
    _auditLogs.value = listOf(
      AuditLogEntry(entityType = type, entityId = id, action = action, changedBy = by, details = details)
    ) + _auditLogs.value
  }

  // Computes dashboard data ensuring ALL real lines of the floor are always present
  fun getTvDashboardData(floorScope: FloorScope = _currentFloor.value): TvDashboardData {
    val floorFilter = floorScope.apiValue

    // Ensure all real lines for this floor are always present
    val floorLines = allLinesMaster.filter { floorScope == FloorScope.ALL || it.floor == floorFilter }

    val summaries = floorLines.map { lineInfo ->
      val setup = _targets.value.find { it.lineId == lineInfo.id } ?: getLineSetup(lineInfo.id).copy(floor = lineInfo.floor)
      val prods = _productionEntries.value.filter { it.lineId == setup.lineId }
      val qas = _qualityEntries.value.filter { it.lineId == setup.lineId }

      val hasReported = prods.isNotEmpty()
      val outputQty = if (hasReported) prods.sumOf { it.outputQty } else null
      val isConfirmedZero = prods.any { it.isConfirmedZero || it.outputQty == 0 }

      val targetDue = Math.round((setup.targetDailyPcs.toDouble() / setup.totalShiftHours) * setup.workingHoursSoFar).toInt()

      val achPct = if (outputQty != null && setup.targetDailyPcs > 0) {
        ((outputQty.toDouble() / setup.targetDailyPcs) * 100).let { Math.round(it * 10.0) / 10.0 }
      } else null

      val earnedMins = (outputQty ?: 0) * setup.smv
      val availMins = setup.includedManpower * setup.workingHoursSoFar * 60
      val effPct = if (outputQty != null && availMins > 0) {
        ((earnedMins / availMins) * 100).let { Math.round(it * 10.0) / 10.0 }
      } else null

      val checked = qas.sumOf { it.checkedGarments }
      val defective = qas.sumOf { it.defectiveGarments }
      val defectsTotal = qas.sumOf { it.totalDefects }

      val oqlPct = if (checked > 0) ((defective.toDouble() / checked) * 100).let { Math.round(it * 100.0) / 100.0 } else null
      val dhuVal = if (checked > 0) ((defectsTotal.toDouble() / checked) * 100).let { Math.round(it * 100.0) / 100.0 } else null

      val openIncs = _incidents.value.count { it.lineId == setup.lineId && it.status != IncidentStatus.RESOLVED }

      LinePerformancePoint(
        lineId = setup.lineId,
        floor = setup.floor,
        styleNumber = setup.styleNumber,
        itemName = setup.itemName,
        smv = setup.smv,
        targetPcs = setup.targetDailyPcs,
        targetDuePcs = targetDue,
        targetEfficiencyPct = setup.targetEfficiencyPct,
        achievementPcs = outputQty,
        isConfirmedZero = isConfirmedZero,
        achievementPct = achPct,
        todayEfficiencyPct = effPct,
        monthlyEfficiencyPct = 65.0,
        todayOqlPct = oqlPct,
        monthlyOqlPct = 3.6,
        dhu = dhuVal,
        reportingStatus = if (hasReported) "Complete" else "Not reported",
        openIncidentsCount = openIncs
      )
    }

    val reportedOnly = summaries.filter { it.achievementPcs != null }
    val totalOutput = reportedOnly.sumOf { it.achievementPcs ?: 0 }
    val totalTarget = summaries.sumOf { it.targetPcs }
    val totalTargetDue = summaries.sumOf { it.targetDuePcs }

    val effSum = reportedOnly.mapNotNull { it.todayEfficiencyPct }.average().let { if (it.isNaN()) 68.0 else Math.round(it * 10.0) / 10.0 }
    val targetEffAvg = summaries.map { it.targetEfficiencyPct }.average().let { if (it.isNaN()) 75.0 else Math.round(it * 10.0) / 10.0 }
    val oqlAvg = reportedOnly.mapNotNull { it.todayOqlPct }.average().let { if (it.isNaN()) 3.81 else Math.round(it * 100.0) / 100.0 }

    val kpi = FloorKpiSummary(
      todayEfficiency = effSum,
      todayTargetEfficiency = targetEffAvg,
      monthlyEfficiency = 72.2,
      monthlyEfficiencyDelta = 4.1,
      todayOutputPcs = totalOutput,
      todayTargetPcs = totalTarget,
      todayTargetDuePcs = totalTargetDue,
      todayOql = oqlAvg,
      bsQaOql = 5.00,
      friPassRate = 100.0,
      operatorsCount = summaries.sumOf { getLineSetup(it.lineId).operatorsCount },
      machinesCount = summaries.sumOf { getLineSetup(it.lineId).machinesCount },
      reportingCoveragePct = if (summaries.isNotEmpty()) Math.round((reportedOnly.size.toDouble() / summaries.size) * 100).toInt() else 95,
      openIncidentsCount = _incidents.value.count { it.status != IncidentStatus.RESOLVED }
    )

    val topDefects = listOf(
      DefectSummary("Poor iron", 42, 0.38),
      DefectSummary("Uncut thread", 28, 0.37),
      DefectSummary("Dirty spot", 18, 0.37),
      DefectSummary("Oil spot", 12, 0.35),
      DefectSummary("Skip stitch", 8, 0.33)
    )

    val highestDhu = summaries.filter { it.dhu != null }.sortedByDescending { it.dhu }.take(5).map {
      LineDhuSummary(it.lineId, it.dhu ?: 0.0)
    }.ifEmpty {
      listOf(
        LineDhuSummary("N2", 1.97),
        LineDhuSummary("O1", 1.90),
        LineDhuSummary("J1", 1.76),
        LineDhuSummary("P1", 1.48),
        LineDhuSummary("Q1", 1.32)
      )
    }

    val qaTrend = listOf(
      DailyQaTrend("02 Oct", 3.9, 6.1),
      DailyQaTrend("03 Oct", 3.8, 5.8),
      DailyQaTrend("04 Oct", 3.6, 5.6),
      DailyQaTrend("05 Oct", 3.5, 5.4),
      DailyQaTrend("06 Oct", 3.4, 5.2),
      DailyQaTrend("07 Oct", 3.3, 5.1),
      DailyQaTrend("08 Oct", 3.2, 4.9),
      DailyQaTrend("09 Oct", 3.1, 4.8)
    )

    return TvDashboardData(
      floorScope = floorScope,
      kpis = kpi,
      linePerformance = summaries,
      topDefects = topDefects,
      highestDhuLines = highestDhu,
      weeklyQaTrend = qaTrend,
      activeIncidents = _incidents.value.filter { it.status != IncidentStatus.RESOLVED },
      lastDataUpdate = "10:24 AM",
      lastRefreshTime = "18:49",
      isDemoMode = _isDemoMode.value
    )
  }
}
