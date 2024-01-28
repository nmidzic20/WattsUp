import { Component } from '@angular/core';
import { UserManagerService } from './services/user-manager.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  isUser = false;
  isAdmin = false;

  constructor(private userManagerService: UserManagerService, private router: Router) { }

  async ngOnInit(): Promise<void> {
    this.userManagerService.isLoggedIn$.subscribe(async (loggedIn) => {
      if (loggedIn) {
        let tokens = this.userManagerService.getTokens();

        if (tokens) {
          this.isUser = tokens.jwtInfo?.role == 'User';
          this.isAdmin = tokens.jwtInfo?.role == 'Admin';
        }

        this.isUser = this.userManagerService.getTokens()?.jwtInfo?.role == 'User';
        this.isAdmin = this.userManagerService.getTokens()?.jwtInfo?.role == 'Admin';
      }
    });
  }

  logOut() {
    this.userManagerService.removeTokens();
    this.isUser = false;
    this.isAdmin = false;
    this.router.navigate(['/login']);
  }
}
