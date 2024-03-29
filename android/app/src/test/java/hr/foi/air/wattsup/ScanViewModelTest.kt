package hr.foi.air.wattsup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import hr.foi.air.wattsup.repository.TestRepositoryImpl
import hr.foi.air.wattsup.viewmodels.ScanViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ScanViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    private lateinit var viewModel: ScanViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = ScanViewModel(TestRepositoryImpl())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // TODO test
}
