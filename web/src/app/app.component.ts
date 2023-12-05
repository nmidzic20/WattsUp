import { Component } from '@angular/core';
import { UserManagerService } from './services/user-manager.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor(private userManagerService: UserManagerService, private router: Router) { }

  async ngOnInit(): Promise<void> {
    let tokens = this.userManagerService.getTokensFromLocalStorage();

    if (tokens) {
      if (await this.userManagerService.validTokens(tokens)) {
        this.router.navigate(['/map']);
      } else {
        this.router.navigate(['/login']);
      }
    } else {
      this.router.navigate(['/login']);
    }
  }
}
