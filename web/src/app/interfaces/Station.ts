export interface Station {
    name: string;
    latitude: number;
    longitude: number;
    dateAdded: Date;
    dateAddedFormated: string;
    addedBy: string;
    lastSync: Date;
    lastSyncFormated: string;
    status: string;
}