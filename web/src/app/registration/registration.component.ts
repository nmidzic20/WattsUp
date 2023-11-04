import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit{
  firstName: string = '';
  lastName: string = '';
  email: string = '';
  password: string = '';
  message: string = '';

  constructor(private router: Router){

  }

  ngOnInit(): void {
  }

  submitRegistration(form: NgForm){
    if(form.valid){
      this.message = "successfully submited";
    }else{
      this.message = "not valid"
    }
  }
}
