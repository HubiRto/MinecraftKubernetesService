import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class AuthServiceService {
  private baseURL: string = "http://localhost:8080/api/v1/auth"

  constructor(private http: HttpClient) { }
}
