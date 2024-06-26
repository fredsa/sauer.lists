import { Component, Inject, inject, model } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogClose, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatFormFieldModule, MatLabel } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

export interface NewListDialogData {
}

@Component({
  selector: 'app-new-list-dialog',
  standalone: true,
  imports: [
    MatLabel,
    FormsModule, MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDialogTitle, MatDialogContent, MatDialogActions, MatDialogClose,
  ],
  templateUrl: './new-list-dialog.component.html',
  styleUrl: './new-list-dialog.component.scss'
})
export class NewListDialogComponent {

  readonly data = inject<NewListDialogData>(MAT_DIALOG_DATA);

  constructor(
    public dialogRef: MatDialogRef<NewListDialogComponent>,
  ) { }

  readonly listName = model();
}
