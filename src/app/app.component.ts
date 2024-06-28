import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { DivConsoleComponent } from './div-console/div-console.component';
import { SignInComponent } from './sign-in/sign-in.component';
import { ToolbarComponent } from './toolbar/toolbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    DivConsoleComponent,
    ToolbarComponent,
    SignInComponent,
    RouterOutlet,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {

  constructor(
  ) { }

}
