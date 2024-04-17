import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import SockJS from 'sockjs-client/dist/sockjs.js';
import {Client, Stomp, StompSubscription} from "@stomp/stompjs";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private stompClient: Client;
  private stompSubscription: StompSubscription;

  constructor() {
    const socket = new SockJS('http://localhost:8080/sba-websocket');
    this.stompClient = Stomp.over(socket);
    this.stompClient.debug = null; // Wyłącz debugowanie, aby nie wyświetlać zbędnych komunikatów w konsoli
  }

  connect(): Observable<any> {
    return new Observable(observer => {
      this.stompClient.activate();
      this.stompClient.onConnect = (frame) => {
        observer.next(true); // Informacja o połączeniu nawiązanym
      };
      this.stompClient.onStompError = (error) => {
        observer.error(error); // Informacja o błędzie połączenia
      };
    });
  }

  send(message: any, destination: string): void {
    this.stompClient.publish({destination: destination, body: JSON.stringify(message)});
  }

  subscribe(topic: string): Observable<any> {
    return new Observable(observer => {
      this.stompSubscription = this.stompClient.subscribe(topic, (response) => {
        observer.next(JSON.parse(response.body)); // Odbieramy odpowiedź z serwera i przekazujemy do obserwatora
      });
    });
  }

  unsubscribe(): void {
    if (this.stompSubscription) {
      this.stompSubscription.unsubscribe(); // Anulujemy subskrypcję, gdy nie jest już potrzebna
    }
  }
}
