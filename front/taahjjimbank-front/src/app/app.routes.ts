import { Routes } from '@angular/router';
import { UserRegisterComponent } from './users/user-register/user-register.component';
import { LoginComponent } from './auth/login/login.component';

export const routes: Routes = [
    { path: 'register', component: UserRegisterComponent },
    { path: 'login', component: LoginComponent },
    { path: '', redirectTo: '/login', pathMatch: 'full' }
];
