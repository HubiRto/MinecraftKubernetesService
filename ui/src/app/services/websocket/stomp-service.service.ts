import {Injectable} from '@angular/core';

import Stomp from 'stompjs';
import SockJS from 'sockjs-client/dist/sockjs.js';
import {interval, Observable, Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class StompServiceService {
  private stompClient: any;
  private randomNumberSubject: Subject<number> = new Subject<number>();

  constructor() {
  }

  connect(): void {
    const socket = new SockJS('http://localhost:8080/sba-websocket');
    this.stompClient = new Stomp.over(socket);
    this.stompClient.connect({}, () => {

      interval(1000).subscribe(() => {
        this.stompClient.send('/ws/randomNumber', {}, '');
      });
      // this.stompClient.send('/ws/randomNumber', {}, '');

      this.stompClient.subscribe('/topic/randomNumber', (message: any) => {
        this.randomNumberSubject.next(JSON.parse(message.body));
      });
    });
  }

  getRandomNumber(): Observable<number> {
    return this.randomNumberSubject.asObservable();
  }
}
