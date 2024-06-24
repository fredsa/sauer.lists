import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { RouterOutlet } from '@angular/router';
import { BackendService } from './backend.service';
import { DivConsoleComponent } from './div-console/div-console.component';
import { ListComponent } from './list/list.component';
import { NewListDialogComponent } from './new-list-dialog/new-list-dialog.component';
import { SignInComponent } from './sign-in/sign-in.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    MatButtonModule,
    ListComponent,
    SignInComponent,
    DivConsoleComponent,
    AsyncPipe,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {

  readonly dialog = inject(MatDialog);

  constructor(
    public backend: BackendService,
  ) { }

  openDialog(): void {
    const dialogRef = this.dialog.open(NewListDialogComponent, {
      data: {},
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed', result);
      if (result == undefined) {
        return;
      }

      this.backend.createList(result);
    });
  }
}
