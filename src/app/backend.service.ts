import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  constructor() { }

  lists: string[] = ['groceries'];

  createList(listName: string) {
    this.lists.push(listName);
  }
}
