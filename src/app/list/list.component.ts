import { Component, Input } from '@angular/core';
import { MatList, MatListItem } from '@angular/material/list';
import { List } from '../backend.service';

@Component({
  selector: 'app-list',
  standalone: true,
  imports: [
    MatList,
    MatListItem,
  ],
  templateUrl: './list.component.html',
  styleUrl: './list.component.scss'
})
export class ListComponent {
  @Input()
  list!: List;
}
