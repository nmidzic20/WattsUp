import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-email-submission',
  templateUrl: './email-submission.component.html',
  styleUrls: ['./email-submission.component.scss']
})
export class EmailSubmissionComponent implements OnInit {
  errorMessageBox? : HTMLElement;
  instructionMessage? : HTMLElement;
  loading = false;
  isSubmitAvalible = true;

  ngOnInit(): void {
    this.errorMessageBox = document.getElementById("message") as HTMLElement;
    this.instructionMessage = document.getElementById("instructionMessage") as HTMLElement;
  }

  async submitEmail(form: NgForm){
    if(form.valid){
      this.errorMessageBox!!.innerHTML = ""

      let header = new Headers();
      header.set("Content-Type", "application/json");
      header.set("accept", "text/plain");
      let email = form.controls['email'].value;
      let body = {email: email}
      let parameters = {method: 'POST', headers: header, body: JSON.stringify(body)};

      try{
        this.loading = true;
        let response = await fetch(environment.apiUrl + "/Users/ResetPassword/Email", parameters);

        if(response.status == 200){
          this.loading = false;
          this.isSubmitAvalible = false;
          this.instructionMessage!!.innerHTML = "Email has been sent. Check your inbox in a few minutes."
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
      this.errorMessageBox!!.innerHTML = "Invalid email address."
    }
  }
}
