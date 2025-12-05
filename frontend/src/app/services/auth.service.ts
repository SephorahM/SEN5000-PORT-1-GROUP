import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Update this URL to match your backend server's address and port
  // If testing on an emulator, use 'http://10.0.2.2:5000/api'
  // If testing on a device, use your machine's IP: 'http://YOUR_IP_ADDRESS:5000/api'
  private apiUrl = 'http://localhost:5000/api'; 

  constructor(private http: HttpClient) { }

  login(credentials: any) {
    return this.http.post(`${this.apiUrl}/login`, credentials);
  }
}