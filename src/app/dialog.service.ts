import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { BackendService } from './backend.service';
import { NewListDialogComponent } from './new-list-dialog/new-list-dialog.component';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  constructor(
    private backend: BackendService,
    private dialog: MatDialog,
    private router: Router,
  ) { }

  openNewListDialog(): void {
    const dialogRef = this.dialog.open(NewListDialogComponent, {
      data: {},
    });

    dialogRef.afterClosed().subscribe(result => {
      // console.log('The dialog was closed', result);
      if (result == undefined) {
        return;
      }

      this.backend.createList(result)
      .then(listId => {
        this.router.navigate(['list', listId]);
      });
    });
  }

}
