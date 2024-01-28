export interface ChargingEvent {
    id: number;
    chargerId: number;
    cardId: number;
    startedAt: Date;
    endedAt: Date;
    volumeKwh: number;
  }