import { Component, OnInit } from '@angular/core';
import { UserManagerService } from '../services/user-manager.service';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.scss']
})
export class StatisticsComponent implements OnInit {

  constructor(private router: Router ,private userManagerService: UserManagerService) { }
  async ngOnInit(): Promise<void> {
    let tokens = this.userManagerService.getTokens();
    if (tokens && tokens.jwtInfo?.id != undefined) {
      if (await this.userManagerService.validTokens(tokens)) {
        
        const cards = await this.getCards(tokens.jwtInfo.id, tokens.jwt);
        cards.forEach((card: any) => {
          console.log(card.id);
        });

      } else {
        this.router.navigate(['/login']);
      }
    } else {
      this.router.navigate(['/login']);
    }
  }

  async getCards(userId: string, jwt: string) {
    let header = new Headers();
    header.set("Accept", "application/json");
    header.set("Authorization", "Bearer " + jwt);
    let parameters = { method: 'GET', headers: header};

    try {
      let response = await fetch(environment.apiUrl + "/Card/" + userId, parameters);
      let body = await response.json();
      if (response.status == 200) {
        return body;
      } else {
        console.error(body.message);
      }
    } catch (error) {
      console.error(error);
    }
  }

}
