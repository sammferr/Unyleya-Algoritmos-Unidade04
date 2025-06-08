package Projeto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Esta classe cria um grafo para representar sabores de pizza
// Cada sabor é um ponto (nó) e as ligações mostram quais sabores costumam ser pedidos juntos
public class GrafoPizza {
    // Estrutura principal: um mapa que guarda as conexões entre sabores
    // Para cada sabor, temos outro mapa que mostra com quais outros ele se conecta
    private Map<String, Map<String, Integer>> grafo;
    
    // Método construtor - prepara a estrutura vazia para começar a usar
    public GrafoPizza() {
        this.grafo = new HashMap<>();
    }
    
    // Cria uma ligação dupla entre dois sabores
    // Por exemplo: se margherita e pepperoni são pedidos juntos, 
    // ambos ficam conectados um ao outro
    public void adicionarConexao(String sabor1, String sabor2) {
        // Conecta o primeiro sabor ao segundo
        criarLigacao(sabor1, sabor2);

        // Conecta o segundo sabor ao primeiro
        // Assim garantimos que a conexão funciona nos dois sentidos
        criarLigacao(sabor2, sabor1);
    }
    
    // Método auxiliar que faz a ligação em uma direção apenas
    // Também conta quantas vezes essa combinação aparece
    private void criarLigacao(String origem, String destino) {
        // Se é a primeira vez que vemos este sabor, criamos espaço para ele
        grafo.putIfAbsent(origem, new HashMap<>());

        // Pega todas as conexões que este sabor já tem
        Map<String, Integer> conexoes = grafo.get(origem);

        // Aumenta em 1 o contador desta combinação (ou começa com 1 se for nova)
        conexoes.put(destino, conexoes.getOrDefault(destino, 0) + 1);
    }
    
    // Mostra na tela todas as conexões que existem no grafo
    public void exibirGrafo() {
        if (grafo.isEmpty()) {
            System.out.println("Nenhuma conexão encontrada.");
            return;
        }
        
        // Conjunto para controlar quais conexões já foram mostradas
        // Evita repetir a mesma informação duas vezes
        Set<String> jaExibidas = new HashSet<>();
        
        // Percorre todos os sabores cadastrados
        for (String sabor : grafo.keySet()) {
            Map<String, Integer> conexoes = grafo.get(sabor);
            
            // Para cada sabor, verifica todas suas conexões
            for (Map.Entry<String, Integer> conexao : conexoes.entrySet()) {
                String outroSabor = conexao.getKey();
                int quantasVezes = conexao.getValue();
                
                // Cria uma chave única para esta dupla de sabores
                // Sempre coloca em ordem alfabética para evitar duplicatas
                String identificador = sabor.compareTo(outroSabor) < 0 ? 
                    sabor + " ↔ " + outroSabor : outroSabor + " ↔ " + sabor;
                
                // Só exibe se ainda não foi mostrada esta combinação
                if (!jaExibidas.contains(identificador)) {
                    System.out.println("   " + identificador + 
                                      " (pedidos juntos " + quantasVezes + " vezes)");
                    jaExibidas.add(identificador);
                }
            }
        }
    }
    
    // Permite que outras classes acessem o grafo se precisarem
    public Map<String, Map<String, Integer>> getGrafo() {
        return grafo;
    }
}
