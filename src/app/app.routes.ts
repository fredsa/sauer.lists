import { Routes } from '@angular/router';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { ListComponent } from './list/list.component';
import { SignInComponent } from './sign-in/sign-in.component';
import { isSignedInGuard } from './guards.guard';

export const routes: Routes = [
    {
        path: 'signin',
        component: SignInComponent,
    },
    {
        path: 'signout',
        component: SignInComponent,
    },
    {
        path: 'list/:listId',
        component: ListComponent,
        canActivate: [isSignedInGuard],
    },
    {
        path: '',
        component: LandingPageComponent,
        canActivate: [isSignedInGuard],
    },
];