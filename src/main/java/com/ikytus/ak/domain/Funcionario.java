package com.ikytus.ak.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.ikytus.ak.domain.enums.TipoUsuario;;

@Entity
public class Funcionario extends Usuario{
	private static final long serialVersionUID = 1L;
	
	@Column(length = 7)
	private String funcional;

	public Funcionario() {
		super();
	}

	public Funcionario(Long id, String nome, String cpf, Date dtnasc, TipoUsuario tipo, String senha, String endereco,
			String bairro, String email, String funcional) {
		super(id,nome, cpf, dtnasc, tipo, senha, endereco, bairro, email);
		this.funcional = funcional;
	}

	public String getFuncional() {
		return funcional;
	}

	public void setFuncional(String funcional) {
		this.funcional = funcional;
	}
}
