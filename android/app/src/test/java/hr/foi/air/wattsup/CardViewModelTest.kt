package hr.foi.air.wattsup // ktlint-disable filename

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.viewmodels.CardViewModel
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

class CardViewModelTest {

    // Rule for LiveData to execute each task synchronously
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var cardObserver: Observer<Card?>

    @Mock
    lateinit var cardListObserver: Observer<List<Card?>>

    private lateinit var viewModel: CardViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        // Set the main dispatcher for testing - necessary because of coroutine launched
        // in startCharging method inside ChargerViewModel
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testCardUpdate() {
        viewModel = CardViewModel()
        viewModel.card.observeForever(cardObserver)
        viewModel.updateCard(Card(1, "1234567890123456", true))
        verify(cardObserver).onChanged(Card(1, "1234567890123456", true))
    }

    @Test
    fun testResetCardsList() {
        viewModel = CardViewModel()
        viewModel.cards.observeForever(cardListObserver)
        viewModel.resetCards()
        verify(cardListObserver).onChanged(emptyList())
    }
}