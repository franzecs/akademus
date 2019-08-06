package com.ikytus.ak.domain.enums;

public enum TipoEstagio {
	
	SIMULADO(1, "Simulado"),
	REALCIVIL(2, "Real Civil"),
	REALPENAL(3,"Real Penal"),
	CURRICULAR(4,"Curricular"),
	NAOCURRICULAR(5,"Não Curricular"),
	SELECAO11JEC(6, "Pré-seleção 11º JEC"),
	MEDIACAO(7,"Mediação"),
	TRABALHISTA(8,"Trabalhista"),
	ACOMPANHAMENTOPROCESSUAL(9," Acompanhamento Processual");
		
	private int cod;
	private String descricao;
	
	private TipoEstagio(int cod, String descricao) {
		this.cod = cod;
		this.descricao=descricao;
	}

	public int getCod() {
		return cod;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static TipoEstagio toEnum(Integer cod) {
		if(cod==null) {
			return null;
		}
		
		for (TipoEstagio x : TipoEstagio.values()) {
			if(cod.equals(x.getCod())) {
				return x;
			}
		}
		throw new IllegalArgumentException("Id inválido: " + cod);
	}

}
