import { Injectable } from '@angular/core';
import { jwtDecode } from "jwt-decode";
import { JwtInfo, Tokens } from '../interfaces/Tokens';

@Injectable({
  providedIn: 'root'
})
export class UserManagerService {
  jwt?: string;
  refreshToken?: string;
  refreshTokenExpiration?: string;

  constructor() { }

  saveTokensToLocalStorage() {
    localStorage.setItem('jwt', this.jwt!!);
    localStorage.setItem('refreshToken', this.refreshToken!!);
    localStorage.setItem('refreshTokenExpiration', this.refreshTokenExpiration!!);
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

  getJwtInfo(jwt: string): JwtInfo | null {
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

  validTokens(tokens: Tokens): boolean {
    return true;
  }
}
