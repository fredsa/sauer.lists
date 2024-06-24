import { Injectable, OnDestroy, signal } from '@angular/core';
import { Auth, GoogleAuthProvider, User, UserInfo, authState, signInAnonymously, signInWithPopup, signOut } from '@angular/fire/auth';
import { EMPTY, Observable, Subscription } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SigninService implements OnDestroy {

  private readonly userSubscription: Subscription | undefined;
  private readonly firebaseUser: Observable<User | null> = EMPTY;
  
  public user = signal<User | null>(null);

  constructor(
    private auth: Auth,
  ) {
    this.firebaseUser = authState(this.auth);
    this.userSubscription = this.firebaseUser.subscribe((user: User | null) => {
      console.log('user', user?.uid, user?.email);
      this.user.set(user);
    });
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
