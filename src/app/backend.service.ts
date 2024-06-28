import { Injectable } from '@angular/core';
import { DocumentData, DocumentReference, Firestore, QueryDocumentSnapshot, SnapshotOptions, collection, collectionSnapshots, deleteDoc, doc, onSnapshot, query, runTransaction, setDoc, where } from '@angular/fire/firestore';
import { Transaction } from 'firebase/firestore';
import { Observable, Subscription, map } from 'rxjs';
import { LogService } from './log.service';
import { SigninService } from './signin.service';

export interface ListEntity {
  id: string,
  ref?: DocumentReference,
  name: string,
  items?: string[],
  users: string[],
};

const listConverter = {
  toFirestore(list: ListEntity): DocumentData {
    return {
      name: list.name,
      users: list.users,
      items: list.items || [],
    };
  },
  fromFirestore(
    snapshot: QueryDocumentSnapshot,
    options: SnapshotOptions
  ): ListEntity {
    const data = snapshot.data(options)!;
    const castdata = data as Partial<ListEntity>
    return <ListEntity>{
      id: snapshot.id,
      name: castdata.name,
      users: castdata.users,
      items: castdata.items,
    };
  }
};

@Injectable({
  providedIn: 'root'
})
export class BackendService {

  listsSubscription: Subscription | undefined;

  constructor(
    private log: LogService,
    private signin: SigninService,
    private firestore: Firestore,
  ) { }

  listsCollection = collection(this.firestore, 'lists')
    .withConverter<ListEntity, DocumentData>(listConverter);

  getLists() {
    const q = query(this.listsCollection, where("users", "array-contains", this.signin.user()!.uid));
    const snapshots = collectionSnapshots(q) as Observable<QueryDocumentSnapshot<ListEntity, DocumentData>[]>;
    return snapshots.pipe(
      map(snaps => snaps.map(snap => snap.data())),
    );
  }

  getList(listId: string, callback: (list: ListEntity | undefined) => void) {
    return onSnapshot(doc(this.listsCollection, listId), snap => {
      callback(snap.data());
    });
  }

  deleteList(listId: string) {
    return deleteDoc(doc(this.listsCollection, listId));
  }

  createList(listName: string) {
    const ref = doc(this.listsCollection);
    return setDoc(ref,
      <ListEntity>{
        name: listName,
        users: [this.signin.user()?.uid],
      }).then(() => {
        return ref.id;
      });
  };

  async addItem(listId: string, item: string) {

    const ref = doc<ListEntity, DocumentData>(this.listsCollection, listId);
    try {
      await runTransaction<void>(this.firestore, async (transaction: Transaction) => {
        const doc = await transaction.get<ListEntity, DocumentData>(ref);
        if (!doc.exists()) {
          throw 'List does not exist: ' + listId;
        } else {
          transaction.update<ListEntity, DocumentData>(ref, {
            items: [item, ...doc.data().items!],
          });
        }
      });
    } catch (err: any) {
      this.log.error('Add item transaction failed', err);
    }
  }

}
