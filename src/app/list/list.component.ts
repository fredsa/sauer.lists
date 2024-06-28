import { AsyncPipe, JsonPipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { DocumentData, DocumentSnapshot } from '@angular/fire/firestore';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { EMPTY, Observable, from } from 'rxjs';
import { BackendService, ListEntity } from '../backend.service';
import { NewItemDialogComponent } from '../new-item-dialog/new-item-dialog.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-list',
  standalone: true,
  imports: [
    MatListModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatInputModule,
    JsonPipe,
    AsyncPipe,
  ],
  templateUrl: './list.component.html',
  styleUrl: './list.component.scss'
})
export class ListComponent {

  constructor(
    private dialog: MatDialog,
    private router: Router,
    public backend: BackendService,
  ) { }

  list!: ListEntity | undefined;

  @Input()
  set listId(listId: string) {
    // console.log('set listId', listId)
    this.backend.getList(listId, (list: ListEntity | undefined) => {
      this.list = list;
    });
  }

  public newItem = "";

  openNewItemDialog(listId: string): void {
    const dialogRef = this.dialog.open(NewItemDialogComponent, {
      data: {},
    });

    dialogRef.afterClosed().subscribe(result => {
      // console.log('The dialog was closed', result);
      if (result == undefined) {
        return;
      }

      this.backend.addItem(listId, result);
    });
  }

  deleteList(listId: string) {
    this.backend.deleteList(listId)
      .then(() => {
        this.router.navigate(['']);
      })
  }
}
