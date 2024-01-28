import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SSEService {
  private eventSource!: EventSource;

  constructor() {}

  connect(url: string): Observable<MessageEvent> {
    this.eventSource = new EventSource(url);

    return new Observable<MessageEvent>((observer) => {
      this.eventSource.onmessage = (event: MessageEvent) => {
        observer.next(event);
      };

      this.eventSource.onerror = (error) => {
        observer.error(error);
      };
    });
  }

  closeConnection() {
    if (this.eventSource) {
      this.eventSource.close();
    }
  }
}
