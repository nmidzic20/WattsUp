import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-new-password-submission',
  templateUrl: './new-password-submission.component.html',
  styleUrls: ['./new-password-submission.component.scss']
})
export class NewPasswordSubmissionComponent implements OnInit {
  loading = false;
  errorMessageBox? : HTMLElement;

  ngOnInit(): void {
    this.errorMessageBox = document.getElementById("message") as HTMLElement;
  }

  async submitNewPassword(form: NgForm){
    if(form.valid){
      if(!this.checkPasswordConfirmation(form)){
        return;
      }
      this.errorMessageBox!!.innerHTML = ""

      let header = new Headers();
      header.set("Content-Type", "application/json");
      header.set("accept", "text/plain");
      let body = {} // make valid body
      let parameters = {method: 'POST', headers: header, body: JSON.stringify(body)};

      try{
        this.loading = true;
        let response = await fetch(environment.apiUrl + "/", parameters); // make correct url

        if(response.status == 200){
          
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
      this.errorMessageBox!!.innerHTML = "Invalid password. Password must be at least 6 characters long."
    }
  }

  checkPasswordConfirmation(form: NgForm){
    let password = form.controls['password'].value;
    let confirmPassword = form.controls['passwordConfirm'].value;
    if(password != confirmPassword){
      this.errorMessageBox!!.innerHTML = "Password and password confirmation do not match!";
      return false;
    }
    return true;
  }
}
