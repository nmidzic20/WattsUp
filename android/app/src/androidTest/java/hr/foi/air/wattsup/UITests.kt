package hr.foi.air.wattsup

import android.Manifest
import android.os.SystemClock
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.rule.GrantPermissionRule.grant
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import org.junit.Assert.* // ktlint-disable no-wildcard-imports
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UITests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @JvmField
    @Rule
    val permissionRule: GrantPermissionRule = grant(
        Manifest.permission.NFC,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
    )

    /*@After
    fun tearDownKoin() {
        try {
            stopKoin()
            startKoin {
                modules(dataModule, viewModelsModule)
            }
        } catch (ex: Exception) {
            Log.e("UI_TEST", ex.toString())
        }
    }*/

    @Test
    fun wrongLoginTest() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        composeTestRule.onNodeWithText("User mode").performClick()
        composeTestRule.onNodeWithText("E-mail").performTextInput("a")
        composeTestRule.onNodeWithText("Password").performTextInput("a")
        composeTestRule.onAllNodesWithText("Login")[0].performClick()
        SystemClock.sleep(500)
        composeTestRule.waitForIdle()
        assertTrue(uiDevice.hasObject(By.text("Login")))
    }

    @Test
    fun correctLoginTest() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        composeTestRule.onNodeWithText("User mode").performClick()
        composeTestRule.onNodeWithText("E-mail").performTextInput("user@gmail.com")
        composeTestRule.onNodeWithText("Password").performTextInput("123456")
        composeTestRule.onAllNodesWithText("Login")[0].performClick()
        SystemClock.sleep(1500)
        composeTestRule.waitForIdle()
        assertTrue(uiDevice.hasObject(By.text("User Mode")))
    }

    @Test
    fun cardListShows() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        composeTestRule.onNodeWithText("User mode").performClick()
        composeTestRule.onNodeWithText("E-mail").performTextInput("user@gmail.com")
        composeTestRule.onNodeWithText("Password").performTextInput("123456")
        composeTestRule.onAllNodesWithText("Login")[0].performClick()
        SystemClock.sleep(1500)
        composeTestRule.waitForIdle()
        assertTrue(uiDevice.hasObject(By.text("0x3530A832")))
    }

    @Test
    fun historyListShows() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        composeTestRule.onNodeWithText("User mode").performClick()
        composeTestRule.onNodeWithText("E-mail").performTextInput("user@gmail.com")
        composeTestRule.onNodeWithText("Password").performTextInput("123456")
        composeTestRule.onAllNodesWithText("Login")[0].performClick()
        SystemClock.sleep(1500)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Charging History").performClick()
        composeTestRule.waitForIdle()
        SystemClock.sleep(1500)
        assertTrue(uiDevice.hasObject(By.text("kWh")))
    }
}
