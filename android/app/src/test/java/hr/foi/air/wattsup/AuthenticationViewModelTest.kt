package hr.foi.air.wattsup

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import hr.foi.air.wattsup.network.models.Card
import hr.foi.air.wattsup.viewmodels.AuthenticationViewModel
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

class AuthenticationViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var booleanObserver: Observer<Boolean>

    @Mock
    lateinit var stringObserver: Observer<String>

    @Mock
    lateinit var cardObserver: Observer<Card?>

    private lateinit var viewModel: AuthenticationViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = AuthenticationViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testTogglePasswordVisibility() {
        viewModel.passwordVisible.observeForever(booleanObserver)
        viewModel.togglePasswordVisibility()
        assertEquals(true, viewModel.passwordVisible.value)
        viewModel.togglePasswordVisibility()
        assertEquals(false, viewModel.passwordVisible.value)
    }

    @Test
    fun testUpdateEmail() {
        viewModel.email.observeForever(stringObserver)
        viewModel.updateEmail("test@test.com")
        assertEquals("test@test.com", viewModel.email.value)
    }

    @Test
    fun testUpdatePassword() {
        viewModel.password.observeForever(stringObserver)
        viewModel.updatePassword("test")
        assertEquals("test", viewModel.password.value)
    }

    @Test
    fun testUpdateFirstName() {
        viewModel.firstName.observeForever(stringObserver)
        viewModel.updateFirstName("test")
        assertEquals("test", viewModel.firstName.value)
    }

    @Test
    fun testUpdateLastName() {
        viewModel.lastName.observeForever(stringObserver)
        viewModel.updateLastName("test")
        assertEquals("test", viewModel.lastName.value)
    }

    @Test
    fun testUpdateCard() {
        viewModel.card.observeForever(cardObserver)
        viewModel.updateCard(Card(1, "1234567890123456", true))
        assertEquals(Card(1, "1234567890123456", true), viewModel.card.value)
    }

    @Test
    fun testUpdateInvalidEmail() {
        viewModel.invalidEmail.observeForever(booleanObserver)
        viewModel.updateInvalidEmail(true)
        assertEquals(true, viewModel.invalidEmail.value)
    }

    @Test
    fun testUpdateInvalidPassword() {
        viewModel.invalidPassword.observeForever(booleanObserver)
        viewModel.updateInvalidPassword(true)
        assertEquals(true, viewModel.invalidPassword.value)
    }
}