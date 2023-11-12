import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserManagerService {
  jwt?: string;
  refreshToken?: string;
  refreshTokenExpiration?: Date;

  constructor() { }
}
