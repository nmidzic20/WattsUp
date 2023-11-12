package hr.foi.air.wattsup // ktlint-disable filename

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import hr.foi.air.wattsup.viewmodels.ChargerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class ChargerViewModelTest {

    // Rule for LiveData to execute each task synchronously
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var chargingObserver: Observer<Boolean>

    @Mock
    lateinit var percentageChargedObserver: Observer<Float>

    @Mock
    lateinit var amountNecessaryObserver: Observer<Float>

    private lateinit var viewModel: ChargerViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Set the main dispatcher for testing - necessary because of coroutine launched
        // in startCharging method inside ChargerViewModel
        Dispatchers.setMain(Dispatchers.Unconfined)

        // ViewModel with mocked dependencies
        viewModel = ChargerViewModel()

        viewModel.charging.observeForever(chargingObserver)
        viewModel.percentageChargedUntilFull.observeForever(percentageChargedObserver)
        viewModel.amountNecessaryForFullCharge.observeForever(amountNecessaryObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testChargingToggle() {
        assertEquals(false, viewModel.charging.value)
        viewModel.toggleCharging({})
        assertEquals(true, viewModel.charging.value)
        viewModel.toggleCharging({})
        assertEquals(false, viewModel.charging.value)
    }

    @Test
    fun testOpenFullChargeAlertDialog() {
        assertEquals(false, viewModel.openFullChargeAlertDialog.value)
        viewModel.setOpenFullChargeAlertDialog(true)
        assertEquals(true, viewModel.openFullChargeAlertDialog.value)
        viewModel.setOpenFullChargeAlertDialog(false)
        assertEquals(false, viewModel.openFullChargeAlertDialog.value)
    }

    @Test
    fun testChargingInProgress() = runBlocking {
        // Attach mock observers to LiveData properties
        viewModel.percentageChargedUntilFull.observeForever(percentageChargedObserver)
        viewModel.amountNecessaryForFullCharge.observeForever(amountNecessaryObserver)

        assertEquals(false, viewModel.charging.value)
        viewModel.toggleCharging({})

        // Simulate 10 second charging
        delay(10000)
        viewModel.toggleCharging({})

        verify(percentageChargedObserver).onChanged(ArgumentMatchers.floatThat { it in 0.08f..0.1f })
        verify(amountNecessaryObserver).onChanged(ArgumentMatchers.floatThat { it in 0.85f..0.95f })

        viewModel.percentageChargedUntilFull.removeObserver(percentageChargedObserver)
        viewModel.amountNecessaryForFullCharge.removeObserver(amountNecessaryObserver)
    }
}
