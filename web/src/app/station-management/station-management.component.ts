import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserManagerService } from '../services/user-manager.service';
import { Station } from '../interfaces/Station';

@Component({
  selector: 'app-station-management',
  templateUrl: './station-management.component.html',
  styleUrls: ['./station-management.component.scss']
})
export class StationManagementComponent implements OnInit {
  totalStations = 32;
  availableStations = 12;
  unavailableStations = 8;
  offlineStations = 1;
  stations: Station[] = [
    {
      name: 'Station 1',
      latitude: 37.7749,
      longitude: -122.4194,
      dateAdded: new Date(),
      dateAddedFormated: new Date().toLocaleDateString('hr', { year: 'numeric', month: 'long', day: 'numeric' }),
      addedBy: 'User 1',
      lastSync: new Date(),
      lastSyncFormated: new Date().toLocaleDateString('hr', { year: 'numeric', month: 'long', day: 'numeric' }),
      status: 'Online'
    },
    {
      name: 'Station 2',
      latitude: 34.0522,
      longitude: -118.2437,
      dateAdded: new Date(),
      dateAddedFormated: new Date().toLocaleDateString('hr', { year: 'numeric', month: 'long', day: 'numeric' }),
      addedBy: 'User 2',
      lastSync: new Date(),
      lastSyncFormated: new Date().toLocaleDateString('hr', { year: 'numeric', month: 'long', day: 'numeric' }),
      status: 'Offline'
    },
    {
      name: 'Station 1',
      latitude: 37.7749,
      longitude: -122.4194,
      dateAdded: new Date(),
      dateAddedFormated: new Date().toLocaleDateString('hr', { year: 'numeric', month: 'long', day: 'numeric' }),
      addedBy: 'User 1',
      lastSync: new Date(),
      lastSyncFormated: new Date().toLocaleDateString('hr', { year: 'numeric', month: 'long', day: 'numeric' }),
      status: 'Online'
    },
    {
      name: 'Station 2',
      latitude: 34.0522,
      longitude: -118.2437,
      dateAdded: new Date(),
      dateAddedFormated: new Date().toLocaleDateString('hr', { year: 'numeric', month: 'long', day: 'numeric' }),
      addedBy: 'User 2',
      lastSync: new Date(),
      lastSyncFormated: new Date().toLocaleDateString('hr', { year: 'numeric', month: 'long', day: 'numeric' }),
      status: 'Offline'
    },
    {
      name: 'Station 1',
      latitude: 37.7749,
      longitude: -122.4194,
      dateAdded: new Date(),
      dateAddedFormated: new Date().toLocaleDateString('hr', { year: 'numeric', month: 'long', day: 'numeric' }),
      addedBy: 'User 1',
      lastSync: new Date(),
      lastSyncFormated: new Date().toLocaleDateString('hr', { year: 'numeric', month: 'long', day: 'numeric' }),
      status: 'Online'
    },
    {
      name: 'Station 2',
      latitude: 34.0522,
      longitude: -118.2437,
      dateAdded: new Date(),
      dateAddedFormated: new Date().toLocaleDateString('hr', { year: 'numeric', month: 'long', day: 'numeric' }),
      addedBy: 'User 2',
      lastSync: new Date(),
      lastSyncFormated: new Date().toLocaleDateString('hr', { year: 'numeric', month: 'long', day: 'numeric' }),
      status: 'Error'
    },
  ];

  constructor(private router: Router, private userManagerService: UserManagerService){}

  async ngOnInit(): Promise<void> {
    let tokens = this.userManagerService.getTokens();
    /*
    if (tokens) {
      if (await this.userManagerService.validTokens(tokens)) {
        if(tokens.jwtInfo?.role != 'Admin') {
          this.router.navigate(['/map']);
        }
      } else {
        this.router.navigate(['/login']);
      }
    } else {
      this.router.navigate(['/login']);
    }*/
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'Online':
        return 'green';
      case 'Offline':
        return 'red';
      default:
        return 'yellow';
    }
  }
}
