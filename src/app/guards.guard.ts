import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { SigninContinueParam, SigninService } from './signin.service';

export const isSignedInGuard: CanActivateFn = (route, state) => {
  // console.log('isSignedInGuard', state.url, '…');
  const signin = inject(SigninService);
  const router = inject(Router);
  return signin.firebaseUser.pipe(
    map(fb => {
      // console.log('isSignedInGuard', state.url, !!fb);
      if (!fb) {
        router.navigate(['signin'], { queryParams: { [SigninContinueParam]: state.url } })
      }
      return true;
    }),
  );
};
