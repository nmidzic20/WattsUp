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
  messageBox?: HTMLElement;

  constructor(private router: Router){

  }

  ngOnInit(): void {
      this.firstNameInput = document.getElementById("firstname") as HTMLInputElement;
      this.lastNameInput = document.getElementById("lastname") as HTMLInputElement;
      this.emailInput = document.getElementById("email") as HTMLInputElement;
      this.passwordInput = document.getElementById("password") as HTMLInputElement;
      this.messageBox = document.getElementById("message") as HTMLElement;
  }

  validateInput(){
    let isValid = true;
    let firstname = this.firstNameInput?.value;
    let lastname = this.lastNameInput?.value;
    let email = this.emailInput?.value;
    let password = this.passwordInput?.value;
    let errorMessage = "";

    if (firstname == ""){
      isValid = false;
      errorMessage += "First name field is empty!";
    } 
    if (lastname == ""){
      isValid = false;
      errorMessage += "Last name field is empty!";
    } 
    if (email == ""){
      isValid = false;
      errorMessage += "Email field is empty!";
    } 
    if (password == ""){
      isValid = false;
      errorMessage += "Password field is empty!";
    }
    if (password!!.length < 6){
      isValid = false;
      errorMessage += "Password must be at least 6 characters long!";
    }
    this.messageBox!!.innerHTML = errorMessage;
    
    return isValid;
  }
}
