import { Component, OnInit } from '@angular/core';
import { UserManagerService } from '../services/user-manager.service';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { ChargingEvent } from '../interfaces/Event';
import { ChartConfiguration } from 'chart.js';

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.scss']
})
export class StatisticsComponent implements OnInit {
  events: ChargingEvent[] = [];

  barChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [ 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'],
    datasets: [
      { 
        data: [ 65, 59, 80, 81, 56, 55, 40 ],
        borderColor: '#11cb54',
        backgroundColor: '#11cb54',
        borderRadius: 6,
      }
    ]
  };

  public barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
  };

  constructor(private router: Router ,private userManagerService: UserManagerService) { }

  async ngOnInit(): Promise<void> {
    let tokens = this.userManagerService.getTokens();
    if (tokens && tokens.jwtInfo?.id != undefined) {
      if (await this.userManagerService.validTokens(tokens)) {

        const cards = await this.getCards(tokens.jwtInfo.id, tokens.jwt);
        cards.forEach(async (card: any) => {
          this.events.push(...await this.getEvents(card.id, tokens!.jwt));
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

  async getEvents(cardId: number, jwt: string): Promise<ChargingEvent[]> {
    let header = new Headers();
    header.set("Accept", "application/json");
    header.set("Authorization", "Bearer " + jwt);
    let parameters = { method: 'GET', headers: header};

    try {
      let response = await fetch(environment.apiUrl + "/Event/forCard/" + cardId, parameters);
      let body = await response.json();
      if (response.status == 200) {
        return body as ChargingEvent[];
      } else {
        console.error(body.message);
        throw new Error("Failed to fetch events");
      }
    } catch (error) {
      console.error(error);
      throw new Error("Failed to fetch events");
    }
  }

}
