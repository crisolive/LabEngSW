package br.com.locadora.filme;

import java.util.List;
import java.util.Optional;

//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface FilmeRepository extends JpaRepository<Filme, Long> {
	@Query("SELECT f FROM Filme f JOIN FETCH f.atores WHERE f.id = :id")
    Optional<Filme> findByIdWithFilmes(@Param("id") Long id);
	
	boolean existsByImdbId (String imdbId);
	
	@Query("UPDATE Filme f SET f.desabilitado = :desabilitado WHERE f.id = :id")
	@Modifying
	public void atualizaStatus(Long id, boolean desabilitado);

	 // Para filtrar por ano de uma data completa
    @Query("SELECT f FROM Filme f WHERE YEAR(f.dataLancamento) = :ano")
    List<Filme> findByDataLancamento(@Param("ano") Integer ano);
    
    //Criar alguma lógica
	List<Filme> findTop5ByOrderByDataLancamentoDesc();
	
}
