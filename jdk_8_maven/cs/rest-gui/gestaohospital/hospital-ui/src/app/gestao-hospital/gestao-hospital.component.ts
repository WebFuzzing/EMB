import { Component, OnInit } from '@angular/core';
import { HospitalService } from '../hospital.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-gestao-hospital',
  templateUrl: './gestao-hospital.component.html',
  styleUrls: ['./gestao-hospital.component.css']
})
export class GestaoHospitalComponent implements OnInit {

  title = 'Sistema de Gestão Hospitalar';
  hospital = {};
  hospitais = [];
  paciente = {};
  pacientes = [];
  produto = {};
  produtos = [];

  constructor(
    private hospitalService: HospitalService,
    private messageService: MessageService
    ) { }

  ngOnInit() {
    this.consultar();
    this.consultarPaciente();
    this.consultarProduto();
   }
  consultar() {
    this.hospitalService.listarHospitais()
    .subscribe(resposta => this.hospitais = resposta as any);
  }
  adicionar() {
    this.hospitalService.adicionar(this.hospital)
    .subscribe(() => {
      this.hospital = {};
      this.consultar();
      this.messageService.add({
        severity: 'success',
        summary: 'Hospital adicionado com sucesso!'
      });
    },
    resposta => {
      let msg = 'Erro inesperado. Tente novamente.';
      if (resposta.error.message) {
        msg = resposta.error.message;
      }
      this.messageService.add({
        severity: 'error',
        summary: msg
      });
    });
  }
  consultarPaciente() {
    this.hospitalService.listarPacientes()
    .subscribe(resposta => this.pacientes = resposta as any);
  }
  adicionarPaciente() {
    this.hospitalService.adicionarPaciente(this.paciente)
    .subscribe(() => {
      this.paciente = {};
      this.consultarPaciente();
      this.messageService.add({
        severity: 'success',
        summary: 'Paciente adicionado com sucesso!'
      });
    },
    resposta => {
      let msg = 'Erro inesperado. Tente novamente.';
      if (resposta.error.message) {
        msg = resposta.error.message;
      }
      this.messageService.add({
        severity: 'error',
        summary: msg
      });
    });
  }
  consultarProduto() {
    this.hospitalService.listarProdutos()
    .subscribe(resposta => this.produtos = resposta as any);
  }
  adicionarProduto() {
    this.hospitalService.adicionarProduto(this.produto)
    .subscribe(() => {
      this.produto = {};
      this.consultarProduto();
      this.messageService.add({
        severity: 'success',
        summary: 'Produto adicionado com sucesso!'
      });
    },
    resposta => {
      let msg = 'Erro inesperado. Tente novamente.';
      if (resposta.error.message) {
        msg = resposta.error.message;
      }
      this.messageService.add({
        severity: 'error',
        summary: msg
      });
    });
  }
}
