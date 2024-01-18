import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-password-reset',
  templateUrl: './password-reset.component.html',
  styleUrls: ['./password-reset.component.scss']
})
export class PasswordResetComponent implements OnInit {
  resetTokenExists = false;
  resetToken?: string | null;

  constructor(private route: ActivatedRoute) { }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      if (params.hasOwnProperty('usertoken')) {
        this.resetToken = params['usertoken'];
        this.resetTokenExists = true;
      }
    });
  }
}
