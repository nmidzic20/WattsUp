package hr.foi.air.wattsup.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.foi.air.wattsup.network.NetworkService
import hr.foi.air.wattsup.network.models.EventPOSTBody
import hr.foi.air.wattsup.network.models.EventPOSTResponseBody
import hr.foi.air.wattsup.network.models.EventPUTBody
import hr.foi.air.wattsup.network.models.EventPUTResponseBody
import hr.foi.air.wattsup.utils.CurrentEvent
import hr.foi.air.wattsup.utils.UserCard
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.TimeUnit

class ChargerViewModel : ViewModel() {

    private val maxChargePercentage = 1f

    private val _charging = MutableLiveData(false)
    private val _startTime = MutableLiveData(0L)
    private val _endTime = MutableLiveData(0L)
    private val _timeElapsed = MutableLiveData(0L)

    private val _timeTrackingJob = MutableLiveData<Job?>(null)

    private val _initialChargeAmount = MutableLiveData(0f)
    private val _amountNecessaryForFullCharge =
        MutableLiveData(maxChargePercentage - _initialChargeAmount.value!!)
    private val _currentChargeAmount: MutableLiveData<Float> =
        MutableLiveData(_initialChargeAmount.value)
    private val _percentageChargedUntilFull = MutableLiveData(0f)
    private val _currentChargeVolume = MutableLiveData(0f)

    private val _eventService = NetworkService.eventService

    private val _toastMessage = MutableLiveData<String>()
    private val currentChargeVolume: LiveData<Float> get() = _currentChargeVolume

    val charging: LiveData<Boolean> get() = _charging
    val timeElapsed: LiveData<Long> get() = _timeElapsed

    val percentageChargedUntilFull: LiveData<Float> get() = _percentageChargedUntilFull
    val amountNecessaryForFullCharge: LiveData<Float> get() = _amountNecessaryForFullCharge
    val currentChargeAmount: LiveData<Float> get() = _currentChargeAmount
    // Variable used to track the amount of charge in kWh for current charging session

    private val _openFullChargeAlertDialog = MutableLiveData(false)
    val openFullChargeAlertDialog: LiveData<Boolean> = _openFullChargeAlertDialog

    val toastMessage: LiveData<String> get() = _toastMessage
    fun setOpenFullChargeAlertDialog(value: Boolean) {
        _openFullChargeAlertDialog.value = value
    }

    fun toggleCharging(onFullyCharged: () -> Unit) {
        if (isVehicleFullyCharged()) {
            onFullyCharged()
        } else {
            _charging.value = !_charging.value!!
            if (_charging.value == true) {
                startCharging(onFullyCharged)
            } else {
                stopCharging()
            }
        }
    }

    private fun isVehicleFullyCharged(): Boolean {
        return _amountNecessaryForFullCharge.value!! < 0.01f
    }

    private fun startCharging(onFullyCharged: () -> Unit) {
        _startTime.value = System.currentTimeMillis()
        _currentChargeVolume.value = 0f
        viewModelScope.launch {
            launch {
                val eventPOSTBody = EventPOSTBody(1, UserCard.userCard.value!!.id)
                // chargerID is sent as 1 since charger selection is yet to be implemented
                startEvent(eventPOSTBody)
            }
            while (_charging.value == true) {
                if (_percentageChargedUntilFull.value!! < _amountNecessaryForFullCharge.value!!) {
                    // Update time every second
                    delay(1000)
                    if (!_charging.value!!) {
                        // This check is necessary immediately after delay function in order
                        // to prevent the values below getting updated even after charging was stopped
                        // which can happen since delay function is asynchronous and won't be
                        // cut off as soon as charging is set to false
                        return@launch
                    }
                    _timeElapsed.value = System.currentTimeMillis() - _startTime.value!!
                    _percentageChargedUntilFull.value =
                        (_percentageChargedUntilFull.value!! + 0.01f)
                    _currentChargeAmount.value =
                        (_currentChargeAmount.value!! + 0.01f).coerceIn(0f, 1f)
                    _currentChargeVolume.value = (_currentChargeVolume.value!! + 0.03f)
                    // Arbitrarily selected amount of charge in kWh per second
                } else {
                    // Stop charging automatically if the EV is fully charged
                    _charging.value = false
                    stopCharging()
                    onFullyCharged()
                }
            }
        }
    }

    private fun stopCharging() {
        _endTime.value = System.currentTimeMillis()
        _timeTrackingJob.value?.cancel()

        _timeElapsed.value = 0L
        _percentageChargedUntilFull.value = 0f
        _amountNecessaryForFullCharge.value =
            (maxChargePercentage - currentChargeAmount.value!!).coerceIn(0f, 1f)

        val eventPUTBody = EventPUTBody(
            CurrentEvent.currentEvent.value!!.id,
            currentChargeVolume.value!!,
        )
        viewModelScope.launch {
            stopEvent(eventPUTBody)
        }
    }

    fun formatTime(milliseconds: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun startEvent(eventPOSTBody: EventPOSTBody) {
        _eventService.logEventStart(eventPOSTBody).enqueue(
            object : retrofit2.Callback<EventPOSTResponseBody> {
                override fun onResponse(
                    call: Call<EventPOSTResponseBody>,
                    response: Response<EventPOSTResponseBody>,
                ) {
                    CurrentEvent.currentEvent.value = response.body()
                    Log.i("RES_EVENT", "Event started")
                    Log.i("RES_EVENT", response.body().toString())
                    _toastMessage.value = "Event started successfully"
                }

                override fun onFailure(call: Call<EventPOSTResponseBody>, t: Throwable) {
                    Log.i("Response", t.toString())
                    _toastMessage.value = "Error starting event"
                }
            },
        )
    }

    private fun stopEvent(eventPUTBody: EventPUTBody) {
        _eventService.logEventEnd(eventPUTBody).enqueue(
            object : retrofit2.Callback<EventPUTResponseBody> {
                override fun onResponse(
                    call: Call<EventPUTResponseBody>,
                    response: Response<EventPUTResponseBody>,
                ) {
                    Log.i("RES_EVENT", "Event saved")
                    Log.i("RES_EVENT", response.body().toString())
                    _toastMessage.value = "Event saved successfully"
                }

                override fun onFailure(call: Call<EventPUTResponseBody>, t: Throwable) {
                    Log.i("Response", t.toString())
                    _toastMessage.value = "Error saving event"
                }
            },
        )
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}
