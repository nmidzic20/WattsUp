import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { RegistrationComponent } from './registration/registration.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { LoginComponent } from './login/login.component';
import { StationManagementComponent } from './station-management/station-management.component';
import { AddStationDialogueComponent } from './add-station-dialogue/add-station-dialogue.component';
import { MapPageComponent } from './map-page/map-page.component';
import { StatisticsComponent } from './statistics/statistics.component';
import { NgChartsModule } from 'ng2-charts';
import { PasswordResetComponent } from './password-reset/password-reset.component';
import { EmailSubmissionComponent } from './password-reset/email-submission/email-submission.component';
import { NewPasswordSubmissionComponent } from './password-reset/new-password-submission/new-password-submission.component';

@NgModule({
  declarations: [
    AppComponent,
    RegistrationComponent,
    NotFoundComponent,
    LoginComponent,
    StationManagementComponent,
    AddStationDialogueComponent,
    MapPageComponent,
    StatisticsComponent,
    PasswordResetComponent,
    EmailSubmissionComponent,
    NewPasswordSubmissionComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    NgChartsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
