import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserManagerService } from '../services/user-manager.service';
import { environment } from 'src/environments/environment';
import { Station, StationResponse } from '../interfaces/Station';
import Map from 'ol/Map';
import TileLayer from 'ol/layer/Tile';
import { OSM } from 'ol/source';
import { Feature, View } from 'ol';
import { fromLonLat } from 'ol/proj';
import VectorSource from 'ol/source/Vector';
import VectorLayer from 'ol/layer/Vector';
import { Point } from 'ol/geom';
import Style from 'ol/style/Style';
import Icon from 'ol/style/Icon';
import { Coordinate } from 'ol/coordinate';

@Component({
  selector: 'app-map-page',
  templateUrl: './map-page.component.html',
  styleUrls: ['./map-page.component.scss']
})
export class MapPageComponent implements OnInit{
  stations: Station[] = [];
  map!: Map | null;
  private vectorSource: VectorSource = new VectorSource();
  private vectorLayer: VectorLayer<VectorSource> = new VectorLayer({
    source: this.vectorSource,
  });

  constructor(private router: Router, private userManagerService: UserManagerService) { }

  async ngOnInit(): Promise<void> {
    let tokens = this.userManagerService.getTokens();
    if (tokens) {
      if (await this.userManagerService.validTokens(tokens)) {
          await this.getStations(tokens.jwt);
          this.drawMap();
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

  drawMap(){
    if (this.map == null){
      this.createMap();
      this.map!!.addLayer(this.vectorLayer);
      this.stations.forEach((station) => {
        let coordinates = fromLonLat([station.longitude, station.latitude]);
        let marker = this.createMarkerFeature(coordinates, station.status);
        this.vectorSource.addFeature(marker);
      });
    }else{
      this.map.updateSize();
    }
  }

  private createMarkerFeature(coordinates: Coordinate, status: string): Feature{
    let marker = new Feature({
      geometry: new Point(coordinates),
    });

    let stationMarkerIconPath;
    if (status == "In use"){
      stationMarkerIconPath = '../../assets/station-in-use.png';
    }else if (status == "Offline"){
      stationMarkerIconPath = '../../assets/station-offline.png';
    }else{
      stationMarkerIconPath = '../../assets/station-available.png';
    }

    let markerStyle = new Style({
      image: new Icon({
        src: stationMarkerIconPath,
        scale: 0.6,
      }),
    });
    marker.setStyle(markerStyle);

    return marker;
  }

  private createMap(){
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
