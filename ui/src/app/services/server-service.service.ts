import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {catchError, tap} from "rxjs/operators";
import {ServerName} from "../interfaces/server-name";
import {CustomResponse} from "../interfaces/custom-response";

@Injectable({
  providedIn: 'root'
})
export class ServerServiceService {
  private readonly apiUrl = 'http://localhost:8080/api/v1/server';

  constructor(private http: HttpClient) {
  }

  servers$ = <Observable<ServerName>>
    this.http.get<ServerName>(`${this.apiUrl}/all`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      );

  delete$ = (serverId: number) => <Observable<CustomResponse>>
    this.http.delete<CustomResponse>(`${this.apiUrl}/delete/${serverId}`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      );

  logs$ = (id: string) => <Observable<CustomResponse>>
    this.http.get<CustomResponse>(`${this.apiUrl}/logs/${id}`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      );

  info$ = (id: string) => <Observable<CustomResponse>>
    this.http.get<CustomResponse>(`${this.apiUrl}/info/${id}`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      );

  ramUsage$ = (id: string) => <Observable<CustomResponse>>
    this.http.get<CustomResponse>(`${this.apiUrl}/usage/ram/${id}`)
      .pipe(
        tap(console.log),
        catchError(this.handleError)
      );

  // getServersNames(): Observable<ServerName> {
  //   return this.http.get<ServerName>('http://localhost:8080/api/v1/server/allNames')
  // }

  private handleError(error: HttpErrorResponse): Observable<never> {
    return throwError(`An error occurred - Error code: ${error.status}`);
  }
}
