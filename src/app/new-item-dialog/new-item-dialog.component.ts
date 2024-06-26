import { Component, inject, model } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogClose, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatFormFieldModule, MatLabel } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

export interface NewItemDialogData {
}

@Component({
  selector: 'app-new-item-dialog',
  standalone: true,
  imports: [
    MatLabel,
    FormsModule, MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDialogTitle, MatDialogContent, MatDialogActions, MatDialogClose,
  ],
  templateUrl: './new-item-dialog.component.html',
  styleUrl: './new-item-dialog.component.scss'
})
export class NewItemDialogComponent {
  readonly data = inject<NewItemDialogData>(MAT_DIALOG_DATA);

  constructor(
    public dialogRef: MatDialogRef<NewItemDialogComponent>,
  ) { }

  readonly itemName = model();
}
