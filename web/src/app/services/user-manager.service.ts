import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserManagerService {
  jwt?: string;
  refreshToken?: string;
  refreshTokenExpiration?: string;

  constructor() { }

  saveTokensToLocalStorage(){
    localStorage.setItem ('jwt', this.jwt!!);
    localStorage.setItem ('refreshToken', this.refreshToken!!);
    localStorage.setItem ('refreshTokenExpiration', this.refreshTokenExpiration!!);
  }
}
