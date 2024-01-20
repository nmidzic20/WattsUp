package hr.foi.air.wattsup

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UITest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun wrongLoginTest() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        composeTestRule.onNodeWithText("User mode").performClick()
        composeTestRule.onNodeWithText("E-mail").performTextInput("a")
        composeTestRule.onNodeWithText("Password").performTextInput("a")
        composeTestRule.onAllNodesWithText("Login")[0].performClick()
        Thread.sleep(500)
        composeTestRule.waitForIdle()
        assertTrue(uiDevice.hasObject(By.text("Login")))
    }
}