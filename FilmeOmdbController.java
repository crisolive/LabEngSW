package br.com.locadora.filmeomdb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.locadora.ator.Ator;
import br.com.locadora.ator.AtorService;
import br.com.locadora.filme.Filme;
import br.com.locadora.filme.FilmeRepository;
import br.com.locadora.usuario.Usuario;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/filmeomdb")
public class FilmeOmdbController {
	
	private OmdbService omdbService = new OmdbService();
	
	@Autowired
	private  AtorService atorService;
	
	@Autowired
	private FilmeRepository filmeRepository;
	
	@GetMapping("/buscaromdb")
	public String exibirPaginaBusca(@RequestParam(required = false) String termo, Model model, HttpSession session) {

		// Adicione as linhas para identificar o usuário
		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		model.addAttribute("usuarioLogado", usuario.getUser()); 
		model.addAttribute("activePage", "filmes");
		if (termo != null && !termo.isEmpty()) {
			try {
				// Busca no OMDB e adiciona ao Model
				String resultado = omdbService.buscarFilmePorTitulo(termo);
				List<FilmeOmdb> filmeOdbc = JsonParser.extracaoFilmeOmdb(resultado);
				if (filmeOdbc.isEmpty())
					model.addAttribute("erro", "Filme não encontrado");
				model.addAttribute("resultados", filmeOdbc);
				model.addAttribute("termo", termo);

				session.setAttribute("resultadosFilmes", filmeOdbc);
				session.setAttribute("termoBusca", termo);
			} catch (Exception e) {
				model.addAttribute("erro", "Erro ao buscar no OMDB");
			}
		}
		return "filmeomdb/buscaromdb";
	}


	@PostMapping("/receberFilme")
	@Transactional
	public String receberFilme(@ModelAttribute FilmeOmdb filmeOmdb, 
			HttpSession session, 
			Model model,
			RedirectAttributes redirectAttributes) throws JsonMappingException, JsonProcessingException {
		
		// Adicione as linhas para identificar o usuário
		String message= null;
		Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
		model.addAttribute("usuarioLogado", usuario.getUser()); 
		model.addAttribute("activePage", "filmes");

		//buscar no ombd com o id para receber todos os atributos
		String resultado = omdbService.buscarFilmePorId(filmeOmdb.getImdbID());
		//desmembrando o filme
		Filme filme = JsonParser.extracaoFilme(resultado);

		//gravar o filme no BD
		List <Ator> atoresTemp  = filme.getAtores();
		filme.setAtores(new ArrayList<Ator>());
		//testar se filme já existe
		if (filmeRepository.existsByImdbId(filme.getImdbId()) ) {
			message = "O filme consta na base de dados";
		}
		else {
			Filme filmeSalvo =  filmeRepository.save(filme);
			if (atoresTemp != null && !atoresTemp.isEmpty()) {
				List <Ator> atoresPersistidos   = new ArrayList<Ator>();
				for (Ator ator : atoresTemp) {
					Ator atorExistente = atorService.findByNome(ator.getNome());
					if (atorExistente != null) {
		                atoresPersistidos.add(atorExistente); // Reutiliza ator existente
		                System.out.println("ator existente: " + ator.getNome());
		            } else {
		                Ator novoator = atorService.save(ator); // Salva novo ator
		                if (novoator != null) {
		                    atoresPersistidos.add(novoator);
		                }
		            }
				}
				filmeSalvo.setAtores(atoresTemp);
				filmeRepository.save(filmeSalvo);
				message = "O filme adicionado "+ filme.getTitulo();
			}
		}
		///////////////////////////////////////////
		
		redirectAttributes.addFlashAttribute("message", message);
		List<FilmeOmdb> resultados = (List<FilmeOmdb>) session.getAttribute("resultadosFilmes");
		String termo = (String) session.getAttribute("termoBusca");
		model.addAttribute("resultados", resultados);
		model.addAttribute("termo", termo);
		return "filmeomdb/buscaromdb";
	}
}
