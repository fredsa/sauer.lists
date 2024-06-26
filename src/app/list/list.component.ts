import { Component, Input } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { BackendService, ListEntity } from '../backend.service';

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
    public backend: BackendService,
  ) { }

  @Input()
  list!: ListEntity;

  public newItem = "";

  addItem() {

  }
}
