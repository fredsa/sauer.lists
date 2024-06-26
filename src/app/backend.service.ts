import { Injectable, effect } from '@angular/core';
import { DocumentData, DocumentReference, Firestore, QueryDocumentSnapshot, SnapshotOptions, collection, collectionSnapshots, deleteDoc, doc, query, runTransaction, setDoc, where } from '@angular/fire/firestore';
import { Transaction } from 'firebase/firestore';
import { Subscription } from 'rxjs';
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

  lists!: ListEntity[];

  listsSubscription: Subscription | undefined;

  constructor(
    private log: LogService,
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

  listsCollection = collection(this.firestore, 'lists')
    .withConverter<ListEntity, DocumentData>(listConverter);

  subscribeToLists(uid: string | undefined) {
    if (!uid) {
      console.log('listsCollection not ready yet');
      this.lists = [];
      return;
    }

    console.log('listsCollection ready, uid is', uid);
    const q = query(this.listsCollection, where("users", "array-contains", uid));
    const snapshots = collectionSnapshots(q);
    return snapshots.subscribe((snaps: QueryDocumentSnapshot[]) => {
      // Uses converter.
      this.lists = snaps.map(snap => snap.data() as ListEntity);
    }, (err: any) => {
      this.log.error("list subscription error", err);
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

  async addItem(listId: string, item: string) {

    const ref = doc<ListEntity, DocumentData>(this.listsCollection, listId);
    try {
      await runTransaction<void>(this.firestore, async (transaction: Transaction) => {
        const doc = await transaction.get<ListEntity, DocumentData>(ref);
        if (!doc.exists()) {
          throw 'List does not exist: ' + listId;
        } else {
          transaction.update<ListEntity, DocumentData>(ref, {
            items: [...doc.data().items!, item],
          });
        }
      });
    } catch (err: any) {
      this.log.error('Add item transaction failed', err);
    }
  }

}
