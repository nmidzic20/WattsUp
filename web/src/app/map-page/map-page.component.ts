import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserManagerService } from '../services/user-manager.service';
import { environment } from 'src/environments/environment';
import { Station, StationResponse } from '../interfaces/Station';

@Component({
  selector: 'app-map-page',
  templateUrl: './map-page.component.html',
  styleUrls: ['./map-page.component.scss']
})
export class MapPageComponent implements OnInit{
  stations: Station[] = [];

  constructor(private router: Router, private userManagerService: UserManagerService) { }

  async ngOnInit(): Promise<void> {
    let tokens = this.userManagerService.getTokens();
    if (tokens) {
      if (await this.userManagerService.validTokens(tokens)) {
          await this.getStations(tokens.jwt);
      } else {
        this.router.navigate(['/login']);
      }
    } else {
      this.router.navigate(['/login']);
    }
  }

  async getStations(jwt: string){
    let header = new Headers();
    header.set("Content-Type", "application/json");
    header.set("accept", "text/plain");
    header.set("Authorization", "Bearer " + jwt);
    let parameters = { method: 'GET', headers: header};

    try {
      let response = await fetch(environment.apiUrl + "/Charger", parameters);
      let body = await response.json();
      if (response.status == 200) {
        this.stations = body.map((station: StationResponse) => this.mapStationResponseToStation(station));
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
}
