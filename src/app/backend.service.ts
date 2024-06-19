import { Injectable } from '@angular/core';

export interface List {
  name: string,
  items?: string[],
};

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  constructor() { }

  lists: List[] = [
    {
      name: 'Groceries',
      items: ['Chips', 'Salsa',]
    }
  ];

  createList(listName: string) {
    this.lists.push({
      name: listName,
    });
  }
}
