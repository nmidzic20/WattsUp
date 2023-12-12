package hr.foi.air.wattsup.utils

import androidx.lifecycle.MutableLiveData
import hr.foi.air.wattsup.network.models.EventPOSTResponseBody

object CurrentEvent {
    val currentEvent: MutableLiveData<EventPOSTResponseBody> by lazy {
        MutableLiveData<EventPOSTResponseBody>()
    }
}
