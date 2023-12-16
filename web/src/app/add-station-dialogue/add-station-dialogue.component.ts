import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UserManagerService } from '../services/user-manager.service';
import { Router } from '@angular/router';
import Map from 'ol/Map';
import TileLayer from 'ol/layer/Tile';
import View from 'ol/View';
import { OSM } from 'ol/source';
import { fromLonLat, toLonLat, transform } from 'ol/proj';
import VectorSource from 'ol/source/Vector';
import VectorLayer from 'ol/layer/Vector';
import { Feature } from 'ol';
import { Circle, Point } from 'ol/geom';
import Fill from 'ol/style/Fill';
import Stroke from 'ol/style/Stroke';
import Style from 'ol/style/Style';
import { map } from 'rxjs';
import Icon from 'ol/style/Icon';
import { Coordinate } from 'ol/coordinate';
import { TemplateBindingParseResult } from '@angular/compiler';

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
  dialogueInputContainerIsVisible: boolean = true;
  dialogueMapIsVisible: boolean = false;
  map!: Map | null;
  markerCoordinates?: Coordinate;
  tempName?: string;
  tempLongitude?: string;
  tempLatitude?: string;

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

    if (this.checkFields()) {
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
    } else {
      window.alert("Check field values!");
    }
  }

  openMap() {
    this.saveInputValues()
    this.dialogueInputContainerIsVisible = false;
    this.dialogueMapIsVisible = true;
    setTimeout(() => {
      this.drawMap();
    }, 200);
  }

  saveInputValues() {
    this.stationNameInput = document.getElementById("stationName") as HTMLInputElement;
    this.longitudeInput = document.getElementById("longitude") as HTMLInputElement;
    this.latitudeInput = document.getElementById("latitude") as HTMLInputElement;
    if (this.stationNameInput.value != "") this.tempName = this.stationNameInput.value
    if (this.longitudeInput.value != "") this.tempLongitude = this.longitudeInput.value
    if (this.latitudeInput.value != "") this.tempLatitude = this.latitudeInput.value
  }

  closeMap() {
    this.dialogueInputContainerIsVisible = true;
    this.dialogueMapIsVisible = false;

    if (this.map) {
      this.map.setTarget(undefined);
      this.map = null;
    }

    this.loadTempValues()
  }

  loadTempValues() {
    setTimeout(() => {
      this.stationNameInput = document.getElementById("stationName") as HTMLInputElement;
      this.longitudeInput = document.getElementById("longitude") as HTMLInputElement;
      this.latitudeInput = document.getElementById("latitude") as HTMLInputElement;
      if (this.tempName != undefined) this.stationNameInput.value = this.tempName!!;
      if (this.tempLongitude != undefined) this.longitudeInput.value = this.tempLongitude!!;
      if (this.tempLatitude != undefined) this.latitudeInput.value = this.tempLatitude!!;
      console.log(this.tempLongitude)
    }, 100)
  }

  drawMap() {
    if (this.map == null) {
      this.createMap()
      const vectorSource = new VectorSource();
      const vectorLayer = new VectorLayer({
        source: vectorSource,
      });
      this.map!!.addLayer(vectorLayer);

      const marker = new Feature({
        geometry: new Point(this.map!!.getView().getCenter()!!),
      });

      const markerStyle = new Style({
        image: new Icon({
          src: '../../assets/MapMarker.png',
          scale: 0.2,
        }),
      });

      marker.setStyle(markerStyle);
      vectorSource.addFeature(marker);

      this.map!!.on('moveend', () => {
        marker.getGeometry()!!.setCoordinates(this.map!!.getView().getCenter()!!);
        this.markerCoordinates = marker.getGeometry()!!.getCoordinates();
      });
    } else {
      this.map.updateSize();
    }
  }

  createMap() {
    const varazdinCoordinates = [16.33778, 46.30444] //longitude, latitude

    this.map = new Map({
      layers: [
        new TileLayer({
          source: new OSM(),
        }),
      ],
      target: 'map',
      controls: [],
      view: new View({
        center: fromLonLat(varazdinCoordinates),
        zoom: 14, maxZoom: 18,
      }),
    });
  }

  loadMarkerCoordinatesToInputFields() {
    this.closeMap()
    setTimeout(() => {
      this.longitudeInput = document.getElementById("longitude") as HTMLInputElement;
      this.latitudeInput = document.getElementById("latitude") as HTMLInputElement;
      let coordinates = toLonLat(this.markerCoordinates!!)
      this.longitudeInput.value = coordinates[0].toString();
      this.latitudeInput.value = coordinates[1].toString();
    }, 100)
  }

  checkFields(): boolean {
    let isValid = true;

    this.stationNameInput = document.getElementById("stationName") as HTMLInputElement;
    this.longitudeInput = document.getElementById("longitude") as HTMLInputElement;
    this.latitudeInput = document.getElementById("latitude") as HTMLInputElement;
    if (this.stationNameInput.value == "" || this.longitudeInput.value == "" || this.latitudeInput.value == "") isValid = false;

    return isValid;
  }
}
