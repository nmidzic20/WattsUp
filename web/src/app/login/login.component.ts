import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { UserManagerService } from '../services/user-manager.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  errorMessageBox? : HTMLElement;

  constructor(private router: Router, private userManagerService: UserManagerService){

  }

  async ngOnInit(): Promise<void> {
    this.errorMessageBox = document.getElementById("message") as HTMLElement;
    let tokens = this.userManagerService.getTokens();

    if (tokens) {
      if (await this.userManagerService.validTokens(tokens)) {
        this.router.navigate(['/map']);
      }
    }
  }

  async submitLogin(form: NgForm){
    if(form.valid){
      let email = form.controls['email'].value;
      let password = form.controls['password'].value;
      let header = new Headers();

      header.set("Content-Type", "application/json");
      header.set("accept", "text/plain");
      let body = {email: email, password: password}
      let parameters = {method: 'POST', headers: header, body: JSON.stringify(body)};

      try{
        let response = await fetch(environment.apiUrl + "/Users/Login", parameters);
        let body = await response.text();
        if(response.status == 200){
          let bodyJSON = JSON.parse(body);
          this.userManagerService.saveTokens(bodyJSON.jwt, bodyJSON.refreshToken, bodyJSON.refreshTokenExpiresAt);

          this.router.navigate(['/map']);
        }else{
          let errorMessage = JSON.parse(body).message;
          this.errorMessageBox!!.innerHTML = response.status.toString() + ": " + errorMessage;
        }
      }catch (error){
        this.errorMessageBox!!.innerHTML = (error as Error).message
      }
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
