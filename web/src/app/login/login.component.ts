import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  errorMessageBox? : HTMLElement;

  constructor(private router: Router){

  }

  ngOnInit(): void {
    this.errorMessageBox = document.getElementById("message") as HTMLElement;
  }

  async submitLogin(form: NgForm){
    if(form.valid){
      //implement endpoint fetch
    }else{
      this.errorMessageBox!!.innerHTML = this.showErrorMessage(form);
    }
  }

  showErrorMessage(form: NgForm){
    let errMsg = "";
    let email = form.controls['email'].errors;
    let password = form.controls['password'].errors;

    if(email?.['required']) errMsg += "Email is required! <br>";
    if(password?.['required']) errMsg += "Password is required! <br>";

    return errMsg;
  }
}
