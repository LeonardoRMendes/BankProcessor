import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ArquivoRetornoService {
  private apiUrl = 'http://localhost:8080/api/arquivos-retorno';

  constructor(private http: HttpClient) { }

  getAllArquivos(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getArquivoById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  getArquivosByJobId(jobId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/job/${jobId}`);
  }

  getArquivoContent(id: number): Observable<string> {
    return this.http.get(`${this.apiUrl}/${id}/content`, { responseType: 'text' });
  }
}