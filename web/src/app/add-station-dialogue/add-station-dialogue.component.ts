import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UserManagerService } from '../services/user-manager.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-add-station-dialogue',
  templateUrl: './add-station-dialogue.component.html',
  styleUrls: ['./add-station-dialogue.component.scss']
})
export class AddStationDialogueComponent {
  @Input() isVisible: boolean = false;
  @Output() closeDialogue: EventEmitter<void> = new EventEmitter<void>();
  @Output() refreshChargerView = new EventEmitter<string>();
  stationNameInput?: HTMLInputElement;
  longitudeInput?: HTMLInputElement;
  latitudeInput?: HTMLInputElement;

  constructor(private router: Router, private userManagerService: UserManagerService) { }

  close() {
    this.isVisible = false;
    this.closeDialogue.emit();
  }

  async checkTokenValidity() {
    let tokens = this.userManagerService.getTokens();
    if (tokens) {
      if (await this.userManagerService.validTokens(tokens)) {
        if (tokens.jwtInfo?.role != 'Admin') {
          this.router.navigate(['/map']);
        } else {
          return;
        }
      } else {
        this.router.navigate(['/login']);
      }
    } else {
      this.router.navigate(['/login']);
    }
  }

  async addStation() {
    await this.checkTokenValidity();

    this.stationNameInput = document.getElementById("stationName") as HTMLInputElement;
    this.longitudeInput = document.getElementById("longitude") as HTMLInputElement;
    this.latitudeInput = document.getElementById("latitude") as HTMLInputElement;
    let stationName = this.stationNameInput?.value;
    let longitude = this.longitudeInput?.value;
    let latitude = this.latitudeInput?.value;

    let tokens = this.userManagerService.getTokens();
    let createdById = tokens?.jwtInfo?.id;

    let header = new Headers();
    header.set("Content-Type", "application/json");
    header.set("accept", "text/plain");
    header.set("Authorization", "Bearer " + this.userManagerService.getTokens()?.jwt);
    let body = { name: stationName, latitude: latitude, longitude: longitude, createdById: createdById };
    console.log(body)
    let parameters = { method: 'POST', headers: header, body: JSON.stringify(body) };

    try {
      let response = await fetch("https://localhost:32770/api/Charger", parameters);
      let body = await response.text();
      if (response.status == 200) {
        this.refreshChargerView.emit(this.userManagerService.getTokens()?.jwt);
        this.close();
      } else {
        let errorMessage = JSON.parse(body).message;
        window.alert(response.status.toString() + ": " + errorMessage);
      }
    } catch (error) {
      window.alert((error as Error).message);
    }
  }
}
