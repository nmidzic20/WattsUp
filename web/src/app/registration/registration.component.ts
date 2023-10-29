import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit{
  firstNameInput?: HTMLInputElement;
  lastNameInput?: HTMLInputElement;
  emailInput?: HTMLInputElement;
  passwordInput?: HTMLInputElement;

  constructor(private router: Router){

  }

  ngOnInit(): void {
      this.firstNameInput = document.getElementById("firstname") as HTMLInputElement;
      this.lastNameInput = document.getElementById("lastname") as HTMLInputElement;
      this.emailInput = document.getElementById("email") as HTMLInputElement;
      this.passwordInput = document.getElementById("password") as HTMLInputElement;
  }
}
