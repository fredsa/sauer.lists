import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { AvatarComponent } from '../avatar/avatar.component';

@Component({
  selector: 'app-toolbar',
  standalone: true,
  imports: [
    MatIconModule,
    MatButtonModule,
    AvatarComponent,
  ],
  templateUrl: './toolbar.component.html',
  styleUrl: './toolbar.component.scss'
})
export class ToolbarComponent {

}
