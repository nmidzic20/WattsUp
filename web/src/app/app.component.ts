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
    let tokens = this.userManagerService.getTokens();

    if (tokens) {
      if (await this.userManagerService.validTokens(tokens)) {
        this.isUser = tokens.jwtInfo?.role == 'User';
        this.isAdmin = tokens.jwtInfo?.role == 'Admin';
      }
    }
  }

  logOut() {
    this.userManagerService.removeTokens();
    this.router.navigate(['/login']);
  }
}
