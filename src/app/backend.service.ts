import { Injectable, effect } from '@angular/core';
import { CollectionReference, DocumentData, DocumentReference, Firestore, QueryDocumentSnapshot, collection, collectionSnapshots, deleteDoc, doc, query, setDoc, where } from '@angular/fire/firestore';
import { Subscription } from 'rxjs';
import { SigninService } from './signin.service';

export interface UserEntity {
  id: string,
  lists: string[];
}

export interface ListEntity {
  id: string,
  ref?: DocumentReference,
  name: string,
  items?: string[],
  users: string[],
};

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  lists!: ListEntity[];

  listsSubscription: Subscription | undefined;

  listsCollection = collection(this.firestore, 'lists');

  constructor(
    private signin: SigninService,
    private firestore: Firestore,
  ) {
    // Re-run when logged in user changes.
    effect(() => {
      if (this.listsSubscription) {
        console.log('Unsubcribing', this.listsSubscription);
        this.listsSubscription.unsubscribe();
      }

      this.listsSubscription = this.subscribeToLists(signin.user()?.uid);
    });
  }

  subscribeToLists(uid: string | undefined) {
    if (!uid) {
      console.log('collection not ready yet');
      this.lists = [];
      return;
    }

    console.log('collection… ready, uid is', uid);
    const q = query(this.listsCollection, where("users", "array-contains", uid));
    const snapshots = collectionSnapshots(q);
    return snapshots.subscribe((snaps: QueryDocumentSnapshot[]) => {
      this.lists = snaps
        .map(snap => <ListEntity>{
          id: snap.id,
          ref: snap.ref,
          // path: snap.ref.path,
          //...snap.metadata,
          ...snap.data(),
        });
    });
  }

  deleteList(listId: string) {
    deleteDoc(doc(this.listsCollection, listId));
  }

  createList(listName: string) {
    setDoc(doc(this.listsCollection),
      <ListEntity>{
        name: listName,
        users: [this.signin.user()?.uid],
      });
  };
}
