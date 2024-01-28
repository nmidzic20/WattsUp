import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit{
  errorMessageBox? : HTMLElement;
  loading = false;

  constructor(private router: Router){

  }

  ngOnInit(): void {
    this.errorMessageBox = document.getElementById("message") as HTMLElement;
  }

  async submitRegistration(form: NgForm){
    if(form.valid){
      let firstName = form.controls['firstname'].value;
      let lastName = form.controls['lastname'].value;
      let email = form.controls['email'].value;
      let password = form.controls['password'].value;
      let header = new Headers();

      header.set("Content-Type", "application/json");
      header.set("accept", "text/plain");
      let body = {firstName: firstName, lastName: lastName, email: email, password: password}
      let parameters = {method: 'POST', headers: header, body: JSON.stringify(body)};

      try{
        this.loading = true;
        let response = await fetch(environment.apiUrl + "/Users", parameters);

        if(response.status == 200){
          this.router.navigate(['/login']);
        }else{
          this.loading = false;
          let errorMessage = JSON.parse(await response.text()).message;
          this.errorMessageBox!!.innerHTML = response.status.toString() + ": " + errorMessage;
        }
      }catch (error){
        this.loading = false;
        this.errorMessageBox!!.innerHTML = (error as Error).message
      }
      
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
