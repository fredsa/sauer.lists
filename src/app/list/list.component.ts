import { Component, Input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { BackendService, ListEntity } from '../backend.service';
import { MatDialog } from '@angular/material/dialog';
import { NewItemDialogComponent } from '../new-item-dialog/new-item-dialog.component';

@Component({
  selector: 'app-list',
  standalone: true,
  imports: [
    MatListModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    // FormsModule, MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './list.component.html',
  styleUrl: './list.component.scss'
})
export class ListComponent {

  constructor(
    private dialog: MatDialog,
    public backend: BackendService,
  ) { }

  @Input()
  list!: ListEntity;

  public newItem = "";

  openNewItemDialog(listId: string): void {
    const dialogRef = this.dialog.open(NewItemDialogComponent, {
      data: {},
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed', result);
      if (result == undefined) {
        return;
      }

      this.backend.addItem(listId, result);
    });
  }
}
