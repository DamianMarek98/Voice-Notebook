import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, Observable, Subject} from 'rxjs';
import * as moment from 'moment';
import {Router} from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';

interface UserInfo {
  jwtToken: string;
  expiresIn: string;
}

@Injectable({providedIn: 'root'})
export class AuthService {

  private jwtHelperService: JwtHelperService;
  private loginSubject = new Subject<any>();

  constructor(private http: HttpClient, private router: Router) {
    this.jwtHelperService = new JwtHelperService();
  }

  private static setSession(userInfo: UserInfo): void {
    const expiresAt = moment().add(userInfo.expiresIn, 'second')
    localStorage.setItem('token', userInfo.jwtToken);
    localStorage.setItem('expires_at', JSON.stringify(expiresAt.valueOf()));
  }

  login(username: string, password: string): Observable<any> {
    return this.http.post<UserInfo>('/api/authenticate', {username, password}).pipe(map(response => {
      AuthService.setSession(response);
      this.loginSubject.next(undefined);
    }));
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('expires_at');
    this.router.navigate(['/login']).then();
  }

  public isLoggedIn() {
    const expiration = this.getExpiration();
    if (expiration) {
      return moment().isBefore(expiration);
    }
    return false;
  }

  getExpiration() {
    const expiration = localStorage.getItem('expires_at');
    const expiresAt = JSON.parse(expiration);
    return moment(expiresAt);
  }

  getUserLogin(): string {
    return this.jwtHelperService.decodeToken(localStorage.getItem('token')).sub;
  }

  getLoginSubjectAsObservable(): Observable<any> {
    return this.loginSubject.asObservable();
  }
}
