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
import com.ikytus.ak.repositories.TurmaEstagioRepository;
import com.ikytus.ak.util.pageable.pageConfig;

@Service
public class AlunoService implements AbstractService<Aluno> {

	@Autowired
	private AlunoRepository repository;

	@Autowired
	private CursoRepository cursoRepository;

	@Autowired
	private TurmaEstagioRepository turmaEstagioRepository;

	private UsuarioService us;

	@Autowired
	private pageConfig pconfig;

	@Autowired
	private PasswordEncoder pe;

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
			aluno.setSenha(pe.encode(aluno.getMatricula()));
		}
		aluno.setTipo(TipoUsuario.ALUNO);
		if (!aluno.getPerfis().contains(Perfil.ALUNO)) {
			aluno.addPerfil(Perfil.ALUNO);
		}
	}

	public List<Curso> getCursos() {
		return cursoRepository.findAll();
	}

	public List<TurmaEstagio> getTurmas(Aluno aluno) {
		List<TurmaEstagio> turmasSelecionadas = new ArrayList<>();
		List<String> tipos = new ArrayList<>();

		System.out.println(aluno.getSemestre());

		if (aluno.getSemestre().equals("10º")) {
			for (TurmaEstagio t : turmaEstagioRepository.findBySemestre("2018.1")) {
				for (Estagio e : aluno.getEstagios()) {

					tipos.add(e.getTurma().getTipoEstagio().getDescricao());
					if (!tipos.contains(t.getTipoEstagio().getDescricao())) {
						turmasSelecionadas.add(t);
					}
				}
			}
		} else {
			for (TurmaEstagio t : turmaEstagioRepository.findBySemestre("2018.1")) {
				if (t.getDisponivel().contains(aluno.getSemestre())) {
					for (Estagio e : aluno.getEstagios()) {

						tipos.add(e.getTurma().getTipoEstagio().getDescricao());
						if (!tipos.contains(t.getTipoEstagio().getDescricao())) {
							turmasSelecionadas.add(t);
						}
					}
				}
			}
		}
		return turmasSelecionadas;
	}
}