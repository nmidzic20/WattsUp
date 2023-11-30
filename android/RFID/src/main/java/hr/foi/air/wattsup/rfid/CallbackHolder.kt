package hr.foi.air.wattsup.rfid

import hr.foi.air.wattsup.core.CardScanCallback

object CallbackHolder {
    var rfidScanCallback: CardScanCallback? = null
}
