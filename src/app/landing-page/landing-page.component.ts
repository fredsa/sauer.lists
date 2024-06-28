import { AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink } from '@angular/router';
import { EMPTY, Observable, map } from 'rxjs';
import { BackendService, ListEntity } from '../backend.service';
import { SigninService } from '../signin.service';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [
    MatCardModule,
    MatIconModule,
    RouterLink,
    AsyncPipe,
  ],
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.scss'
})
export class LandingPageComponent {
  
    lists$: Observable<ListEntity[]> = EMPTY;

  constructor(
    public signin: SigninService,
    public backend: BackendService,
    public router: Router,
  ) {
    this.signin.firebaseUser.pipe(
      map(fb => {
        if (fb) {
          this.lists$ = this.backend.getLists();
        }
      }),
    ).subscribe();
  }

}
