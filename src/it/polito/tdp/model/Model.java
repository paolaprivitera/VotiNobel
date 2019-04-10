package it.polito.tdp.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.tdp.dao.EsameDAO;

public class Model {
	
	// gestione degli esami letti da db
	private List<Esame> esami;
	
	// gestione della ricorsione
	private List<Esame> best;
	private double media_best;
	
	public Model() {
		EsameDAO dao = new EsameDAO();
		this.esami = dao.getTuttiEsami();
	}
	
	/**
	 * Trova la combinazione di corsi avente la somma dei crediti richiesta
	 * che abbia la media dei voti massima
	 * @param numeroCrediti
	 * @return l'elenco dei corsi ottimale, oppure {@code null} se
	 * non esiste alcuna combinazione di corsi che assomma al numero
	 * esatto di crediti. 
	 */
	
	public List<Esame> calcolaSottoinsiemeEsami(int numeroCrediti) {
		
		// Inizializzo 
		best = null;
		media_best = 0.0;
		
		Set<Esame> parziale = new HashSet<>();
		
		cerca(parziale, 0, numeroCrediti); // Avvio la ricorsione
		
		return best;
	}

	private void cerca(Set<Esame> parziale, int L, int m) {

		// casi terminali?
		int crediti = sommaCrediti(parziale);
		
		if(crediti > m) 
			return;
		
		if(crediti == m) {
			double media = calcolaMedia(parziale);
			
			if(media > media_best) {
				// best = parziale; SBAGLIATO!!! Deve essere clonato
				best = new ArrayList<Esame>(parziale);
				media_best = media;
				// Ho trovato che questa soluzione parziale migliora l'ottimo
				return; // Da questo ramo della ricorsione non scende piu'
			}
			else {
				return;
			}
		}
		
		// di sicuro crediti < m
		if(L==esami.size()) // Sono all'ultimo livello della ricorsione
			return;
		
		
		// Generiamo sotto-problemi
		// esami[L] e' da aggiungere o no?
		
		// 1. provo a non aggiungerlo -> Richiamo la procedura ricorsiva passandogli
		// parziale (senza aggiungere niente) ed L+1
		
		cerca(parziale, L+1, m); // Non devo fare backtrack perche' non ho aggiunto niente
		
		// 2. provo ad aggiungerlo
		
		parziale.add(esami.get(L));
		cerca(parziale, L+1, m); // Parziale adesso e' modificato rispetto a quello ricevuto all'inizio
		parziale.remove(esami.get(L)); // Faccio backtracking
		
	}

	private double calcolaMedia(Set<Esame> parziale) {
		double media = 0.0;
		int crediti = 0;
		for(Esame e : parziale) {
			media += e.getVoto()*e.getCrediti();
			crediti += e.getCrediti();
		}
		
		return media/crediti;
	}

	private int sommaCrediti(Set<Esame> parziale) {
		int somma = 0;
		
		for (Esame e : parziale) {
			somma+= e.getCrediti();
		}
		
		return somma;
	}
	
}
