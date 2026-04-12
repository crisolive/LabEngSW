package com.example.frota.api;

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

import com.example.frota.caminhao.Caminhao;
import com.example.frota.caminhao.CaminhaoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/caminhaoOmdb")
public class CaminhaoOmdbController {
	
	@Autowired
	private OmdbService omdbService;
	
	@Autowired
	private CaminhaoRepository caminhaoRepository;
	
	@GetMapping("/buscaromdb")
	public String exibirPaginaBusca(@RequestParam(required = false) String termo,
			Model model, 
			HttpSession session) {
		
		if (termo != null && !termo.isEmpty())  {
			try {
				// Busca no OMDB e adiciona ao Model
				String resultado = omdbService.buscarFilmePorTitulo(termo);
				List<CaminhaoOmdb> caminhaoOdbc = JsonParser.extracaoFilmeOmdb(resultado);
				if (caminhaoOdbc.isEmpty()) {
					model.addAttribute("erro", "Caminhao não encontrado");
				} else {
					model.addAttribute("resultados", caminhaoOdbc);
					session.setAttribute("resultadosFilmes", caminhaoOdbc);
	                session.setAttribute("termo", termo);
				}
			} catch (Exception e) {
				model.addAttribute("erro", "Erro ao buscar no OMDB");
			}
		}
		
		model.addAttribute("termo", termo);
		return "caminhaoOmdb/buscaromdb";
	}


	@PostMapping("/receberCaminhao")
	@Transactional
	public String receberFilme(@ModelAttribute CaminhaoOmdb caminhaoOmdb, 
			HttpSession session, 
			Model model,
			RedirectAttributes redirectAttributes) throws JsonMappingException, JsonProcessingException {
		
		String message= null;

		//buscar no ombd com o id para receber todos os atributos
		String resultado = omdbService.buscarFilmePorId(caminhaoOmdb.getImdbID());

		//desmembrando o caminhao
		Caminhao caminhao = JsonParser.extracaoFilme(resultado);
		caminhaoRepository.save(caminhao);
		
		message = "Caminhao adicionado "+ caminhao.getModelo();	
		redirectAttributes.addFlashAttribute("message", message);
		
		List<CaminhaoOmdb> resultados = (List<CaminhaoOmdb>) session.getAttribute("resultadosFilmes");
		String termo = (String) session.getAttribute("termo");
		if (resultados != null) {
	        redirectAttributes.addFlashAttribute("resultados", resultados);
	        redirectAttributes.addFlashAttribute("termo", termo);
	    }
		
		return "redirect:/caminhaoOmdb/buscaromdb" + (termo != null ? "?termo=" + termo : "");
	}
}
