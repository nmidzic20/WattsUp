import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-password-reset',
  templateUrl: './password-reset.component.html',
  styleUrls: ['./password-reset.component.scss']
})
export class PasswordResetComponent implements OnInit {
  errorMessageBox? : HTMLElement;

  ngOnInit(): void {
    this.errorMessageBox = document.getElementById("message") as HTMLElement;
  }

  async submitEmail(form: NgForm){
    if(form.valid){
      this.errorMessageBox!!.innerHTML = ""

    }else{
      this.errorMessageBox!!.innerHTML = "Neispravno unesena e-mail adresa."
    }
  }
}
