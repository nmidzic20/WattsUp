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
      //implement error message showing
    }
  }
}
