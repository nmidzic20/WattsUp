import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserManagerService } from '../services/user-manager.service';
import { Station, StationResponse } from '../interfaces/Station';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-station-management',
  templateUrl: './station-management.component.html',
  styleUrls: ['./station-management.component.scss']
})
export class StationManagementComponent implements OnInit {
  totalStations = 0;
  availableStations = 0;
  unavailableStations = 0;
  offlineStations = 0;
  stations: Station[] = [];

  constructor(private router: Router, private userManagerService: UserManagerService){}

  async ngOnInit(): Promise<void> {
    let tokens = this.userManagerService.getTokens();
    if (tokens) {
      if (await this.userManagerService.validTokens(tokens)) {
        if(tokens.jwtInfo?.role != 'Admin') {
          this.router.navigate(['/map']);
        } else {
          await this.getStations(tokens.jwt);
        }
      } else {
        this.router.navigate(['/login']);
      }
    } else {
      this.router.navigate(['/login']);
    }
  }

  private async getStations(jwt: string) {
    let header = new Headers();
    header.set("Accept", "application/json");
    header.set("Authorization", "Bearer " + jwt);
    let parameters = { method: 'GET', headers: header};

    try {
      let response = await fetch(environment.apiUrl + "/Charger", parameters);
      let body = await response.json();
      if (response.status == 200) {
        this.stations = body.map((station: StationResponse) => this.mapStationResponseToStation(station));
        this.updateStatistics();
      } else {
        console.error(body.message);
      }
    } catch (error) {
      console.error(error);
    }
  }

  private mapStationResponseToStation(stationResponse: StationResponse): Station {
    return {
      name: stationResponse.name,
      latitude: stationResponse.latitude,
      longitude: stationResponse.longitude,
      dateAddedFormated: new Date(stationResponse.createdAt).toLocaleDateString('hr', {
        year: 'numeric',
        month: 'numeric',
        day: 'numeric',
        minute: 'numeric',
        hour: 'numeric',
      }),
      addedBy: stationResponse.createdBy,
      lastSyncFormated: new Date(stationResponse.lastSyncAt).toLocaleDateString('hr', {
        year: 'numeric',
        month: 'numeric',
        day: 'numeric',
        minute: 'numeric',
        hour: 'numeric',
      }),
      status: this.statusConfig(stationResponse),
    };
  }

  private statusConfig(station: StationResponse): string {
    var lastWeek = new Date(Date.now() - 604800000);
    if (station.active) {
      return 'In use';
    } else if (new Date(station.lastSyncAt) < lastWeek) {
      return 'Offline';
    } else {
      return 'Free';
    }
  }

  private updateStatistics(): void {
    this.totalStations = this.stations.length;
    this.availableStations = this.stations.filter(station => station.status === 'Free').length;
    this.unavailableStations = this.stations.filter(station => station.status === 'In use').length;
    this.offlineStations = this.stations.filter(station => station.status === 'Offline').length;
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'Free':
        return 'green';
      case 'In use':
        return 'red';
      case 'Offline':
        return 'yellow';
      default:
        return '';
    }
  }
}