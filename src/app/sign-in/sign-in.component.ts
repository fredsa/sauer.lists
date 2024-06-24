import { JsonPipe } from '@angular/common';
import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
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
export class SignInComponent {

  constructor(
    public signin: SigninService,
  ) { }

}
