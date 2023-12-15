export interface Station {
    name: string;
    latitude: number;
    longitude: number;
    dateAddedFormated: string;
    addedBy: string;
    lastSyncFormated: string;
    status: string;
}

export interface StationResponse {
    name: string;
    latitude: number;
    longitude: number;
    createdAt: Date;
    createdBy: string;
    lastSyncAt: Date;
    active: boolean;
}