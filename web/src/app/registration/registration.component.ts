import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit{
  errorMessageBox? : HTMLElement;

  constructor(private router: Router){

  }

  ngOnInit(): void {
    this.errorMessageBox = document.getElementById("message") as HTMLElement;
  }

  submitRegistration(form: NgForm){
    if(form.valid){
      this.errorMessageBox!!.innerHTML = "Success!";
      //Implement endpoint fetch
    }else{
      this.errorMessageBox!!.innerHTML = this.showErrorMessage(form);
    }
  }

  showErrorMessage(form: NgForm){
    let errMsg = "";
    let firstName = form.controls['firstname'].errors;
    let lastName = form.controls['lastname'].errors;
    let email = form.controls['email'].errors;
    let password = form.controls['password'].errors;

    if(firstName?.['required']) errMsg += "First name is required! <br>";
    if(lastName?.['required']) errMsg += "Last name is required! <br>";
    if(email?.['required']) errMsg += "Email is required! <br>";
    if(email?.['email']) errMsg += "Email is not valid! <br>";
    if(password?.['required']) errMsg += "Password is required! <br>";
    if(password?.['minlength']) errMsg += "Password has to be at least 6 characters long!";

    return errMsg;
  }
}
