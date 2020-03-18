import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class HospitalService {

  apiUrl = 'http://localhost:8080/v1/hospitais/';
  apiUrl2 = 'http://localhost:8080/v1/hospitais/3/pacientes/';
  apiUrl3 = 'http://localhost:8080/v1/hospitais/3/estoque/';

  constructor(private httpClient: HttpClient) { }

  listarHospitais() {
    return this.httpClient.get(this.apiUrl);
  }
  adicionar(hospital: any) {
    return this.httpClient.post(this.apiUrl, hospital);
  }
  listarPacientes() {
    return this.httpClient.get(this.apiUrl2);
  }
  adicionarPaciente(paciente: any) {
    return this.httpClient.post(this.apiUrl, paciente);
  }
  listarProdutos() {
    return this.httpClient.get(this.apiUrl3);
  }
  adicionarProduto(produto: any) {
    return this.httpClient.post(this.apiUrl, produto);
  }
}
