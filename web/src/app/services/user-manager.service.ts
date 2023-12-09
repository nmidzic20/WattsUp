import { Injectable } from '@angular/core';
import { jwtDecode } from "jwt-decode";
import { JwtInfo, Tokens } from '../interfaces/Tokens';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserManagerService {
  tokens: Tokens | null = null;
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor() { }

  saveTokens(jwt: string, refreshToken: string, refreshTokenExpiration: string) {
    localStorage.setItem('jwt', jwt);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('refreshTokenExpiration', refreshTokenExpiration);

    this.tokens = {
      jwtInfo: this.getJwtInfo(jwt),
      jwt: jwt,
      refreshToken: refreshToken,
      refreshTokenExpiresAt: refreshTokenExpiration
    };

    this.isLoggedInSubject.next(true);
  }

  removeTokens() {
    localStorage.removeItem('jwt');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('refreshTokenExpiration');

    this.tokens = null;

    this.isLoggedInSubject.next(false);
  }

  getTokens(): Tokens | null {
    if(this.tokens){
      return this.tokens;
    }

    let jwt = localStorage.getItem('jwt');
    let refreshToken = localStorage.getItem('refreshToken');
    let refreshTokenExpiration = localStorage.getItem('refreshTokenExpiration');

    if (jwt && refreshToken && refreshTokenExpiration) {
      return {
        jwtInfo: this.getJwtInfo(jwt),
        jwt: jwt,
        refreshToken: refreshToken,
        refreshTokenExpiresAt: refreshTokenExpiration
      };
    } else {
      return null;
    }
  }

  private getJwtInfo(jwt: string): JwtInfo | null {
    try {
      let decodedToken: any = jwtDecode(jwt);
      return {
        id: decodedToken.id,
        firstName: decodedToken.firstName,
        email: decodedToken.email,
        roleId: decodedToken.roleId,
        role: decodedToken.role,
        exp: decodedToken.exp
      };
    } catch (error) {
      console.error('Error decoding JWT:', error);
      return null;
    }
  }

  async validTokens(tokens: Tokens): Promise<boolean> {
    if (tokens.jwtInfo) {
      if (tokens.jwtInfo.exp * 1000 > Date.now()) {
        return true;
      } else {
        return await this.validRefreshToken(tokens);
      }
    }
    return false;
  }

  private async validRefreshToken(tokens: Tokens): Promise<boolean> {
    let expirationDate = new Date(tokens.refreshTokenExpiresAt);

    if (expirationDate > new Date()) {
      return await this.tryRefreshToken(tokens);
    }
    return false;
  }

  private async tryRefreshToken(tokens: Tokens): Promise<boolean> {
    let header = new Headers();
    header.set("Content-Type", "application/json");
    header.set("accept", "text/plain");
    header.set("Authorization", "Bearer " + tokens.jwt);

    let body = { jwt: tokens.jwt, refreshToken: tokens.refreshToken }
    let parameters = { method: 'POST', headers: header, body: JSON.stringify(body) };

    try {
      let response = await fetch("https://localhost:32770/api/Users/TokenRefresh", parameters);
      let body = await response.text();
      if (response.status == 200) {
        let bodyJSON = JSON.parse(body);
        this.saveTokens(bodyJSON.jwt, bodyJSON.refreshToken, bodyJSON.refreshTokenExpiresAt);
        return true;
      } else {
        let errorMessage = JSON.parse(body).message;
        console.error(errorMessage);
        return false;
      }
    } catch (error) {
      console.error(error);
      return false;
    }
  }
}
