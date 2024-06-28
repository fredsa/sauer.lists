import { Component, signal } from '@angular/core';
import { SigninService } from '../signin.service';

@Component({
  selector: 'app-avatar',
  standalone: true,
  imports: [
  ],
  templateUrl: './avatar.component.html',
  styleUrl: './avatar.component.scss'
})
export class AvatarComponent {
  working = signal(false);

  constructor(
    public signin: SigninService,
  ) { }

  signinout() {
    this.working.set(true);
    setTimeout(() => {
      if (this.signin.user()?.uid) {
        this.signin.signout().finally(() => {
          this.working.set(false);
        });
      } else {
        this.signin.signin().finally(() => {
          this.working.set(false);
        });
      }
    });
  }
}
