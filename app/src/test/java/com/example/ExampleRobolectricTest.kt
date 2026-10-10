package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.NflRepository
import com.example.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NOS Live", appName)
  }

  @Test
  fun `test repository initialization and default seed data`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = NflRepository(context)

    assertTrue(repository.defaultLines.isNotEmpty())
    val lineJ = repository.defaultLines.find { it.id == "J" || it.id == "J1" }
    assertTrue(lineJ != null)
    assertTrue(lineJ!!.matches("J1") && lineJ.matches("J")) // J and J1 confirmed same line
    assertTrue(!lineJ.matches("J2")) // J2 not merged
  }

  @Test
  fun `test 10 roles support and floor scope`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = NflRepository(context)

    assertEquals(10, UserRole.values().size)
    repository.setRole(UserRole.MECHANIC)
    assertEquals(UserRole.MECHANIC, repository.currentRole.value)

    repository.setFloor(FloorScope.SEW_2)
    assertEquals(FloorScope.SEW_2, repository.currentFloor.value)
  }

  @Test
  fun `test incident lifecycle mute acknowledge resolve`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = NflRepository(context)

    val inc = repository.reportIncident(
      IncidentItem(
        lineId = "K1",
        floor = "Sew 2",
        category = IncidentCategory.MACHINE_BREAKDOWN,
        description = "Test motor stoppage",
        reportedBy = "Tester"
      )
    )

    assertEquals(IncidentStatus.OPEN, inc.status)
    repository.acknowledgeIncident(inc.id, "Mechanic1")
    val acked = repository.incidents.value.find { it.id == inc.id }
    assertEquals(IncidentStatus.ACKNOWLEDGED, acked?.status)

    repository.resolveIncident(inc.id, "Mechanic1", "Belt replaced")
    val resolved = repository.incidents.value.find { it.id == inc.id }
    assertEquals(IncidentStatus.RESOLVED, resolved?.status)
    assertTrue(resolved?.endTimeMillis != null)
  }
}
