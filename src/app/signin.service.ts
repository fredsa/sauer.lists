import { Injectable, OnDestroy, signal } from '@angular/core';
import { Auth, GoogleAuthProvider, User, authState, signInAnonymously, signInWithPopup, signOut } from '@angular/fire/auth';
import { DocumentData, Firestore, QueryDocumentSnapshot, SnapshotOptions, collection, doc, runTransaction } from '@angular/fire/firestore';
import { EMPTY, Observable, Subscription } from 'rxjs';
import { LogService } from './log.service';

export interface UserEntity {
  id: string,
  email: string | null,
  emailVerified: boolean,
  photoURL: string | null,
}

const userConverter = {
  toFirestore(user: UserEntity): DocumentData {
    return {
      email: user.email,
      emailVerified: user.emailVerified,
      photoURL: user.photoURL,
    };
  },
  fromFirestore(
    snapshot: QueryDocumentSnapshot,
    options: SnapshotOptions
  ): UserEntity {
    const data = snapshot.data(options)!;
    const castdata = data as Partial<UserEntity>
    return <UserEntity>{
      id: snapshot.id,
      email: castdata.email,
      emailVerified: castdata.emailVerified,
      photoURL: castdata.photoURL,
    };
  }
};

@Injectable({
  providedIn: 'root'
})
export class SigninService implements OnDestroy {

  usersCollection = collection(this.firestore, 'users');

  private readonly userSubscription: Subscription | undefined;
  private readonly firebaseUser: Observable<User | null> = EMPTY;

  public user = signal<User | null>(null);

  constructor(
    private log: LogService,
    private auth: Auth,
    private firestore: Firestore,
  ) {
    this.firebaseUser = authState(this.auth);
    this.userSubscription = this.firebaseUser.subscribe((user: User | null) => {
      this.user.set(user);
      if (!user?.uid) {
        return;
      }

      console.log('user', user.uid, user.email);
      this.updateUser({
        id: user.uid,
        email: user.email,
        emailVerified: user.emailVerified,
        photoURL: user.photoURL,
      });

    }, (err: any) => {
      log.error("user subscription error", err);
    });
  }

  async updateUser(user: UserEntity) {
    const sfDocRef = doc(this.usersCollection, user.id);
    try {
      await runTransaction(this.firestore, async (transaction) => {
        const sfDoc = await transaction.get(sfDocRef);
        if (!sfDoc.exists()) {
          transaction.set(sfDocRef, user);
        } else {
          transaction.update(sfDocRef, {
            ...sfDoc.data(),
            ...user,
          });
        }
      });
      console.log("Transaction successfully committed!");
    } catch (err: any) {
      this.log.error('Transaction failed', err);
    }
  }

  ngOnDestroy(): void {
    console.log('************* signin service ngOnDestroy() ***************');
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  async login() {
    return await signInWithPopup(this.auth, new GoogleAuthProvider());
  }

  async loginAnonymously() {
    return await signInAnonymously(this.auth);
  }

  async logout() {
    return await signOut(this.auth);
  }

}
