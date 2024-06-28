import { JsonPipe } from '@angular/common';
import { AfterContentInit, Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { ActivatedRoute, Router } from '@angular/router';
import { SigninService } from '../signin.service';

@Component({
  selector: 'app-sign-in',
  standalone: true,
  imports: [
    JsonPipe,
    MatButtonModule,
  ],
  templateUrl: './sign-in.component.html',
  styleUrl: './sign-in.component.scss'
})
export class SignInComponent implements AfterContentInit {

  constructor(
    public signin: SigninService,
    private router: Router,
    private route: ActivatedRoute,
  ) { }

  ngAfterContentInit(): void {
    this.route.queryParamMap.subscribe(map => {
      const path = map.get('continue');
      // console.log('Continue path', path);

      this.signin.firebaseUser.subscribe(fb => {
        const action = this.route.routeConfig?.path;
        switch (action) {
          case 'signin':
            if (fb && path) {
              // console.log('Continuing after', action, 'to', path);
              this.router.navigate([path]);
            }
            break;
          case 'signout':
            if (!fb && path) {
              // console.log('Continuing after', action, 'to', path);
              this.router.navigate([path]);
            }
            break;
          default:
            throw 'Unhandled path ' + path;
        }
        // console.log('NOT continuing after', action, 'to', path);
      });
    });
  }

}
