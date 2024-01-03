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
  amountCharged: number = 0;
  timeCharged: string = "";
  weekStartDate: Date = new Date();
  weekEndDate: Date = new Date();

  chartData: ChartConfiguration<'bar'>['data'] = {
    datasets: []
  };
  chartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: (value) => `${value} kWh`
        }
      }
    },
    plugins: {
      tooltip: {
        callbacks: {
          label: (item) => `${item.formattedValue} kWh`
        }
      }
    }
  };

  constructor(private router: Router ,private userManagerService: UserManagerService) { }

  async ngOnInit(): Promise<void> {
    let tokens = this.userManagerService.getTokens();
    if (tokens && tokens.jwtInfo?.id != undefined) {
      if (await this.userManagerService.validTokens(tokens)) {       

        const cards = await this.getCards(tokens.jwtInfo.id, tokens.jwt);

        const eventsPromises = cards.map((card: any) => this.getEvents(card.id, tokens!.jwt));
        const eventsArray = await Promise.all(eventsPromises);
        this.events = eventsArray.flat();

        this.calculateTimeAndAmountCharged();
        
        this.setWeekDates(new Date());
        this.chartData = this.calculateChartData();

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
        const events = body as ChargingEvent[];
        return this.parseChargingEventDates(events);
      } else {
        console.error(body.message);
        throw new Error("Failed to fetch events");
      }
    } catch (error) {
      console.error(error);
      throw new Error("Failed to fetch events");
    }
  }

  parseChargingEventDates(events: ChargingEvent[]): ChargingEvent[] {
    return events.map(event => ({
      ...event,
      startedAt: new Date(event.startedAt),
      endedAt: new Date(event.endedAt),
    }));
  }

  calculateTimeAndAmountCharged() {
    let timeCharged = 0;

    this.events.forEach(event => {
      this.amountCharged += event.volumeKwh;

      const duration = event.endedAt.getTime() - event.startedAt.getTime();
      timeCharged += duration;
    });
    
    this.timeCharged = this.formatTime(timeCharged);
    this.amountCharged = Math.round(this.amountCharged * 100) / 100;
  }

  formatTime(milliseconds: number): string {
    const totalMinutes = Math.floor(milliseconds / (1000 * 60));
    const hours = Math.floor(totalMinutes / 60);
    const minutes = totalMinutes % 60;
  
    return `${hours}h ${minutes}m`;
  }

  calculateChartData(): ChartConfiguration<'bar'>['data'] {
    const daysOfWeek = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
    const kwhVolume = [0, 0, 0, 0, 0, 0, 0];
  
    this.events.forEach((event) => {
      if(event.startedAt < this.weekStartDate || event.startedAt > this.weekEndDate) return;

      let dayOfWeek = new Date(event.startedAt).getDay();
      dayOfWeek = dayOfWeek-- == -1 ? 6 : dayOfWeek--;
      kwhVolume[dayOfWeek] += event.volumeKwh;
    });
  
    const chartData: ChartConfiguration<'bar'>['data'] = {
      labels: daysOfWeek,
      datasets: [
        {
          data: kwhVolume,
          borderColor: '#11cb54',
          backgroundColor: '#11cb54',
          borderRadius: 6,
        },
      ],
    };
  
    return chartData;
  }

  setWeekDates(date: Date): void {
    let dayOfWeek = date.getDay();
    dayOfWeek = dayOfWeek-- == -1 ? 6 : dayOfWeek--;
  
    this.weekStartDate.setDate(date.getDate() - dayOfWeek);
    this.weekStartDate.setHours(0, 0, 0, 0);
    this.weekStartDate = new Date(this.weekStartDate);
  
    this.weekEndDate.setDate(this.weekStartDate.getDate() + 6);
    this.weekEndDate.setHours(23, 59, 59, 999);
    this.weekEndDate = new Date(this.weekEndDate);
  }

  prevWeek(): void {
    this.weekStartDate.setDate(this.weekStartDate.getDate() - 7);
    this.weekStartDate = new Date(this.weekStartDate);

    this.weekEndDate.setDate(this.weekEndDate.getDate() - 7);
    this.weekEndDate = new Date(this.weekEndDate);

    this.chartData = this.calculateChartData();
  }
  
  nextWeek(): void {
    this.weekStartDate.setDate(this.weekStartDate.getDate() + 7);
    this.weekStartDate = new Date(this.weekStartDate);

    this.weekEndDate.setDate(this.weekEndDate.getDate() + 7);
    this.weekEndDate = new Date(this.weekEndDate);

    this.chartData = this.calculateChartData();
  }

  calculateDuration(event: ChargingEvent): string {
    const duration = event.endedAt.getTime() - event.startedAt.getTime();
    return this.formatTime(duration);

  }

}