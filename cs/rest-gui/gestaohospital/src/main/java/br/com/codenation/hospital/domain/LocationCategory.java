package br.com.codenation.hospital.domain;

public enum LocationCategory {
	HOSPITAL("HOSPITAL"),
    PATIENT("PACIENTE");
 
    private String descricao;
 
    LocationCategory(String descricao) {
        this.descricao = descricao;
    }
 
    public String getDescricao() {
        return descricao;
    }
}
