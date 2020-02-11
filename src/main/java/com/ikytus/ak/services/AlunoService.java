package com.ikytus.ak.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ikytus.ak.domain.Aluno;
import com.ikytus.ak.domain.Curso;
import com.ikytus.ak.domain.Estagio;
import com.ikytus.ak.domain.TurmaEstagio;
import com.ikytus.ak.domain.enums.Perfil;
import com.ikytus.ak.domain.enums.TipoUsuario;
import com.ikytus.ak.repositories.AlunoRepository;
import com.ikytus.ak.repositories.CursoRepository;
import com.ikytus.ak.repositories.EstagioRepository;
import com.ikytus.ak.repositories.TurmaEstagioRepository;
import com.ikytus.ak.util.Tools;
import com.ikytus.ak.util.pageable.pageConfig;

@Service
public class AlunoService implements AbstractService<Aluno> {

	@Autowired
	private AlunoRepository repository;

	@Autowired
	private CursoRepository cursoRepository;

	@Autowired
	private TurmaEstagioRepository turmaEstagioRepository;
	
	@Autowired
	private EstagioRepository estagioRepository;

	@Autowired
	private pageConfig pconfig;

	@Autowired
	private PasswordEncoder pe;
	
	@Autowired
	private Tools tl;

	public void salvar(Aluno aluno) {
		setAtributes(aluno);
		this.repository.save(aluno);
	}

	public void salvar(Aluno aluno, String foto) {
		aluno.setImg(foto);
		setAtributes(aluno);
		this.repository.save(aluno);
	}

	public Page<Aluno> findByName(String nome, Optional<Integer> pageSize, Optional<Integer> page, Pageable pageable,
			String ordem) {
		return repository.findByNomeContainingIgnoreCase(Optional.ofNullable(nome).orElse("%"),
				pconfig.showPage(pageSize, page, pageable, Optional.ofNullable(ordem).orElse("nome")));
	}
	
	public Page<Aluno> findByFilter(String curso, String semestre,String nome, Optional<Integer> pageSize, Optional<Integer> page, Pageable pageable,
			String ordem) {
		
		return repository.findByCursoNomeContainingAndSemestreContainingAndNomeContainingIgnoreCase(Optional.ofNullable(curso).orElse("%"), Optional.ofNullable(semestre).orElse("%"), Optional.ofNullable(nome).orElse("%"),
				pconfig.showPage(pageSize, page, pageable, Optional.ofNullable(ordem).orElse("nome")));
	}
	

	public Aluno findOne(Long codigo) {
		return repository.findOne(codigo);
	}

	public void delete(Long codigo) {
		repository.delete(codigo);
	}

	private void setAtributes(Aluno aluno) {
		aluno.setNome(aluno.getNome().toUpperCase());
		aluno.setEndereco(aluno.getEndereco().toUpperCase());
		aluno.setBairro(aluno.getBairro().toUpperCase());
		aluno.setEmail(aluno.getEmail().toLowerCase());
		if (!aluno.getSenha().trim().isEmpty()) {
			aluno.setSenha(pe.encode(aluno.getSenha()));
		} else {
			aluno.setSenha(pe.encode(aluno.getMatricula())); // senha ao editar = matricula
		}
		aluno.setTipo(TipoUsuario.ALUNO);
		if (!aluno.getPerfis().contains(Perfil.ALUNO)) {
			aluno.addPerfil(Perfil.ALUNO);
		}
	}

	public List<Curso> getCursos() {
		return cursoRepository.findAll();
	}
	
	public Aluno getAlunoLogado() {
		Aluno aluno = (Aluno) tl.getUsuario();
		return aluno;
	}
	
	public List<Estagio> getEstagios(){
		return estagioRepository.findByAluno(getAlunoLogado());
	}
	public List<Estagio> getEstagiosAluno(Aluno aluno){
		return estagioRepository.findByAluno(aluno);
	}

	public List<TurmaEstagio> getTurmas() {
		List<TurmaEstagio> turmasSelecionadas = new ArrayList<>();
		List<String> tipos = new ArrayList<>();
		List<TurmaEstagio> turmasAluno = new ArrayList<>();
		
		for (Estagio e: getEstagios()) {
			tipos.add(e.getTurma().getTipoEstagio().getDescricao());
			turmasAluno.add(e.getTurma());
		}
		
		if (getAlunoLogado().getSemestre().equals("10º")||getAlunoLogado().getSemestre().equals("9º")) {
			for (TurmaEstagio t : turmaEstagioRepository.findBySemestre("2020.1")) { //seleciona as turmas de estágio do semestre
				if (t.getDisponivel().contains(getAlunoLogado().getSemestre())) { //verifica se a turma está disponível para o semestre do aluno
					if(!turmasAluno.contains(t)) { // verifica se o aluno já se cadastrou na turma
						if(!t.isLotada()) { // verifica se a turma de estágio está lotada
							turmasSelecionadas.add(t);
						}
					}
				}
			}
		} else {
			for (TurmaEstagio t : turmaEstagioRepository.findBySemestre("2020.1")) {//seleciona as turmas de estágio do semestre
				if (t.getDisponivel().contains(getAlunoLogado().getSemestre())) { //verifica se a turma está disponível para o semestre do aluno
					if(!tipos.contains(t.getTipoEstagio().getDescricao())) { //verifica se o aluno já se cadastrou no tipo de estágio
						if(!t.isLotada()) { // verifica se a turma de estágio está lotada
							turmasSelecionadas.add(t);
						}
					}
				}
			}
		}
		return turmasSelecionadas;
	}
}
