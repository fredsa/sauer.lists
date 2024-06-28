import { AfterViewInit, Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { RouterLink } from '@angular/router';
import { AvatarComponent } from '../avatar/avatar.component';
import { BackendService, ListEntity } from '../backend.service';
import { DialogService } from '../dialog.service';
import { EMPTY, Observable, map } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { SigninService } from '../signin.service';

@Component({
  selector: 'app-toolbar',
  standalone: true,
  imports: [
    MatIconModule,
    MatButtonModule,
    AvatarComponent,
    MatMenuModule,
    RouterLink,
    AsyncPipe,
  ],
  templateUrl: './toolbar.component.html',
  styleUrl: './toolbar.component.scss'
})
export class ToolbarComponent {

  lists$: Observable<ListEntity[]> = EMPTY;

  constructor(
    public signin: SigninService,
    public backend: BackendService,
    public dialog: DialogService,
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
