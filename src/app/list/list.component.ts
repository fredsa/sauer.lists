import { Component, Input } from '@angular/core';
import { MatList, MatListItem } from '@angular/material/list';

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
  list = '';
}
