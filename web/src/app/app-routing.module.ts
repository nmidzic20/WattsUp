import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegistrationComponent } from './registration/registration.component';
import { NotFoundComponent } from './not-found/not-found.component';
import { LoginComponent } from './login/login.component';
import { StationManagementComponent } from './station-management/station-management.component';
import { MapPageComponent } from './map-page/map-page.component';
import { StatisticsComponent } from './statistics/statistics.component';
import { PasswordResetComponent } from './password-reset/password-reset.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'registration', component: RegistrationComponent },
  { path: 'station-management', component: StationManagementComponent },
  { path: 'map', component: MapPageComponent },
  { path: 'statistics', component: StatisticsComponent },
  { path: 'passwordReset', component: PasswordResetComponent },
  { path: '', component: LoginComponent},
  { path: '**', component: NotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
