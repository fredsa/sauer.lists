import { ErrorHandler, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LogService } from '../log.service';

@NgModule({
  declarations: [],
  imports: [
    CommonModule
  ]
})
export class ErrorHandlerModule implements ErrorHandler {
  constructor(
    private logService: LogService,
  ) { }

  handleError(err: any): void {
    this.logService.handle(err);
  }
}
