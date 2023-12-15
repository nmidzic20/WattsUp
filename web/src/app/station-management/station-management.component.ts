import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserManagerService } from '../services/user-manager.service';

@Component({
  selector: 'app-station-management',
  templateUrl: './station-management.component.html',
  styleUrls: ['./station-management.component.scss']
})
export class StationManagementComponent implements OnInit {
  isAddStationDialogueVisible: boolean = false;

  constructor(private router: Router, private userManagerService: UserManagerService){}

  async ngOnInit(): Promise<void> {
    let tokens = this.userManagerService.getTokens();

/*     if (tokens) {
      if (await this.userManagerService.validTokens(tokens)) {
        if(tokens.jwtInfo?.role != 'Admin') {
          this.router.navigate(['/map']);
        }
      } else {
        this.router.navigate(['/login']);
      }
    } else {
      this.router.navigate(['/login']);
    } */
  }

  openAddStationDialogue() {
    this.isAddStationDialogueVisible = true;
  }

  async getStations(jwt: string){
    console.log("Refreshing...");
  }
}
