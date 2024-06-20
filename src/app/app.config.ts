import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { initializeApp, provideFirebaseApp } from '@angular/fire/app';
import { getAuth, provideAuth } from '@angular/fire/auth';
import { getFirestore, provideFirestore } from '@angular/fire/firestore';
import { getFunctions, provideFunctions } from '@angular/fire/functions';

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({ eventCoalescing: true }),
  provideRouter(routes),
  provideAnimationsAsync(),
  provideFirebaseApp(() => initializeApp({
    "projectId": "sauer-lists",
    "appId": "1:460983314604:web:808b1007204e4c2ea4d709",
    "databaseURL": "https://sauer-lists.firebaseio.com",
    "storageBucket": "sauer-lists.appspot.com",
    // https://github.com/angular/angularfire/issues/3452
    // "locationId": "us-central",
    "apiKey": "AIzaSyDsokPvdQq8v1w6qIaRITDPg82QIfgNIiE",
    "authDomain": "sauer-lists.firebaseapp.com",
    "messagingSenderId": "460983314604",
  })),
  provideAuth(() => getAuth()),
  provideFirestore(() => getFirestore()),
  provideFunctions(() => getFunctions()),
  ]
};
