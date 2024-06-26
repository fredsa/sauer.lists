import { Injectable, OnDestroy, signal } from '@angular/core';
import { Auth, GoogleAuthProvider, User, authState, signInAnonymously, signInWithPopup, signOut } from '@angular/fire/auth';
import { DocumentData, Firestore, QueryDocumentSnapshot, SnapshotOptions, collection, doc, runTransaction } from '@angular/fire/firestore';
import { Router } from '@angular/router';
import { Transaction } from 'firebase/firestore';
import { EMPTY, Observable, Subscription } from 'rxjs';
import { LogService } from './log.service';

export const SigninContinueParam = 'continue';

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

  usersCollection = collection(this.firestore, 'users')
    .withConverter<UserEntity, DocumentData>(userConverter);

  private readonly userSubscription: Subscription | undefined;

  public readonly firebaseUser: Observable<User | null> = EMPTY;
  public user = signal<User | null>(null);

  constructor(
    private log: LogService,
    private auth: Auth,
    private firestore: Firestore,
    private router: Router,
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
    const ref = doc<UserEntity, DocumentData>(this.usersCollection, user.id);
    try {
      await runTransaction<void>(this.firestore, async (transaction: Transaction) => {
        const doc = await transaction.get<UserEntity, DocumentData>(ref);
        if (!doc.exists()) {
          transaction.set<UserEntity, DocumentData>(ref, user);
        } else {
          transaction.update<UserEntity, DocumentData>(ref, {
            ...user,
          });
        }
      });
    } catch (err: any) {
      this.log.error('Update user transaction failed', err);
    }
  }

  ngOnDestroy(): void {
    console.log('************* signin service ngOnDestroy() ***************');
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  async signin() {
    return await signInWithPopup(this.auth, new GoogleAuthProvider());
  }

  async signinAnonymously() {
    return await signInAnonymously(this.auth);
  }

  async signout() {
    return await signOut(this.auth)
      .then(() => {
        console.log('signed out at', this.router.routerState.snapshot.url)
        this.router.navigate(['signout'], { queryParams: { [SigninContinueParam]: this.router.routerState.snapshot.url } });
      });
  }

}
