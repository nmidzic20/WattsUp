package hr.foi.air.wattsup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import hr.foi.air.wattsup.network.models.Event
import hr.foi.air.wattsup.viewmodels.HistoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class HistoryViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var booleanObserver: Observer<List<Event?>>

    private lateinit var viewModel: HistoryViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = HistoryViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testResetEvents() {
        viewModel.events.observeForever(booleanObserver)
        viewModel.resetEvents()
        assertEquals(emptyList<Event?>(), viewModel.events.value)
    }
}