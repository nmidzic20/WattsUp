package hr.foi.air.wattsup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import hr.foi.air.wattsup.core.CardManager
import hr.foi.air.wattsup.viewmodels.ScanViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ScanViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var booleanObserver: Observer<Boolean>

    private lateinit var viewModel: ScanViewModel
    private lateinit var cardManager: CardManager

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = ScanViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}