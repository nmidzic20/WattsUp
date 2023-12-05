import { Injectable } from '@angular/core';
import { jwtDecode } from "jwt-decode";
import { JwtInfo, Tokens } from '../interfaces/Tokens';

@Injectable({
  providedIn: 'root'
})
export class UserManagerService {

  constructor() { }

  saveTokensToLocalStorage(jwt: string, refreshToken: string, refreshTokenExpiration: string) {
    localStorage.setItem('jwt', jwt);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('refreshTokenExpiration', refreshTokenExpiration);
  }

  getTokensFromLocalStorage(): Tokens | null {
    let jwt = localStorage.getItem('jwt');
    let refreshToken = localStorage.getItem('refreshToken');
    let refreshTokenExpiration = localStorage.getItem('refreshTokenExpiration');

    if (jwt && refreshToken && refreshTokenExpiration) {
      return {
        jwt: this.getJwtInfo(jwt),
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
    if (tokens.jwt) {
      if (tokens.jwt.exp * 1000 > Date.now()) {
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
    header.set("Authorization", "Bearer " + localStorage.getItem('jwt'));

    let body = { jwt: tokens.jwt, refreshToken: tokens.refreshToken }
    let parameters = { method: 'POST', headers: header, body: JSON.stringify(body) };

    try {
      let response = await fetch("https://localhost:32770/api/Users/TokenRefresh", parameters);
      let body = await response.text();
      if (response.status == 200) {
        let bodyJSON = JSON.parse(body);
        this.saveTokensToLocalStorage(bodyJSON.jwt, bodyJSON.refreshToken, bodyJSON.refreshTokenExpiresAt);
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
