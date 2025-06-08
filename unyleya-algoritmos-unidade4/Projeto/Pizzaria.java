// PROJETO FINAL - MELHORIAS IMPLEMENTADAS
// Este arquivo contém as partes modificadas para atender
// aos 3 desafios do projeto final:
// 1. Método Alterar Pedido
// 2. Método Gerar Relatório com Grafos
// 3. Método Cálculo de Frete 
// ========================================================================
package Projeto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import Projeto.Pizza.TamanhoPizza;

public class Pizzaria {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Cliente> listaClientes = new ArrayList<>();
        List<Pedido> listaPedidos = new ArrayList<>();

        boolean continuar = true;
        while (continuar) {
            System.out.println("Escolha uma opção: ");
            System.out.println("1 - Fazer um novo pedido.");
            System.out.println("2 - Alterar um pedido."); 
            System.out.println("3 - Adicionar um cliente.");
            System.out.println("4 - Gerar relatório de vendas. ");
            System.out.println("5 - Gerar lista de clientes. ");
            System.out.println("9 - Sair");

            System.out.print("Opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();
            System.out.println();

            switch (opcao) {
                case 1:
                    fazerPedido(scanner, listaPedidos, listaClientes); // METODO QUE FOI ACRESCENTADO O CALCULAR FRETE
                    break;
                case 2:
                    alterarPedido(scanner, listaPedidos); // METODO ACRESCENTADO QUE FALTAVA
                    break;                    
                case 3:
                    listaClientes.add(adicionarcliente(scanner));
                    System.out.println("Cliente adicionado com sucesso!");
                    System.out.println();
                    break;
                case 4:
                    gerarRelatorio(listaPedidos); // METODO ACRESCENTADO QUE FALTAVA
                    break;
                case 5:
                    gerarListaClientes(listaClientes);
                    break;                 
                case 6:
                    System.out.println("Até amanhã ..."); 
                    continuar = false;
                    scanner.close();
                    break;              
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        }                    
    }    

    private static void fazerPedido(Scanner scanner, List<Pedido> listaPedidos, List<Cliente> listaClientes) {
        List<Pizza> pizzas = new ArrayList<>();
        System.out.println("FAZER PEDIDO"); 

        if (listaClientes.isEmpty()) {
            System.out.println("Não há clientes cadastrados. Cadastre um cliente primeiro.");
            return;
        }

        int x = 1;
        System.out.println("Selecione um cliente");
        for (Cliente cliente : listaClientes) {
            System.out.println(x+"   "+cliente.getNome());
            x++;
        }
        System.out.print("Opção: ");
        int cliente = scanner.nextInt();
        scanner.nextLine();

        boolean continuar = true;
        while (continuar) {
            x = 1;
            System.out.println("Qual o tamanho da pizza? ");
            System.out.println("Selecione um tamanho: ");
            for (TamanhoPizza tamanhos : Pizza.TamanhoPizza.values()) {
                System.out.println(x+" - "+tamanhos);
                x++;
            }
            System.out.print("Opção: ");
            int tamanho = scanner.nextInt();
            scanner.nextLine();

            int quantiSabores = 0;

            while (quantiSabores < 1 || quantiSabores > 4) {
                System.out.println("Digite a quantidade de sabores: 1 - 4 ");
                System.out.print("Opção: ");
                quantiSabores = scanner.nextInt();
                scanner.nextLine();
            }  

            Cardapio cardapio = new Cardapio();
            List<String> saboresList = new ArrayList<>();
            List<String> saboresSelect = new ArrayList<>();

            for (int i = 0; i < quantiSabores; i++) {
                System.out.println("Selecione um sabor: ");

                x = 1;
                saboresList.clear();
                for (String sabor : cardapio.getCardapio().keySet()) {
                    saboresList.add(sabor);
                    System.out.println(x+" - "+sabor);
                    x++;
                }  
                System.out.print("Opção: ");
                int opcao = scanner.nextInt();
                scanner.nextLine(); 
                saboresSelect.add(saboresList.get(opcao - 1));       
            } 
            
            Pizza pizza = new Pizza(saboresSelect, cardapio.getPrecoJusto(saboresSelect), TamanhoPizza.getByIndex(tamanho -1));
            pizzas.add(pizza);

            System.out.println("Pizza cadastrada com sucesso!");
            System.out.println();
            System.out.println("Deseja cadastrar mais uma pizza no pedido? ");
            System.out.print("1 - Sim, 2 - Não ");
            int opcao = scanner.nextInt();
            scanner.nextLine(); 

            if (opcao != 1) {
                continuar = false;
            }
        }
        
        double valorPedido = somarPizzas(pizzas);
        Pedido pedido = new Pedido(listaPedidos.size()+1, listaClientes.get(cliente - 1), pizzas, valorPedido);

        //  DESAFIO 3: IMPLEMENTAÇÃO DO CÁLCULO DE FRETE AUTOMÁTICO
        // Solicita a distância e calcula automaticamente o frete
        // baseado numa fórmula que considera distância e peso das pizzas."
        // ========================================================================     
        System.out.println("\n CALCULO DE FRETE");
        System.out.print("Informe a distância até seu endereço (km): ");
        double distancia = scanner.nextDouble();
        scanner.nextLine();

        ///Calcular peso total das pizzas baseado no tamanho
        // Cada tamanho tem um peso específico para o cálculo do frete     
        double pesoTotal = 0.0;
        for (Pizza pizza : pizzas) {
            TamanhoPizza tamanho = pizza.getTamanho();
            switch (tamanho) {
                case BROTO:
                    pesoTotal += 0.6; // Pizza broto = 0.6kg
                    break;
                case GRANDE:
                    pesoTotal += 1.5; // Pizza grande = 1.5kg
                    break;
                case GIGA:
                    pesoTotal += 1.9; // Pizza giga = 1.5kg
                    break;
            }
        }

        // Fórmula de cálculo do frete - considera tanto a distância quanto o peso da entrega
        // Fórmula: (distância × R$ 2,00) + (peso × R$ 1,50)              
        double frete = (distancia * 2.0) + (pesoTotal * 1.5);

        //Garantir frete mínimo de R$ 7,00        
        if (frete < 7.00) {
            frete = 7.00; // Frete mínimo estabelecido
        }
        
        //Calcular valor total com frete incluído
        double valorTotalComFrete = valorPedido + frete;
        
        //Exibir resumo do pedido com frete
        System.out.println("\n RESUMO DO PEDIDO");
        System.out.println("Cliente: " + pedido.getCliente().getNome());
        System.out.println("Quantidade de pizzas: " + pizzas.size());
        System.out.println("Peso total: " + pesoTotal + " kg");
        System.out.println("Distância: " + distancia + " km");
        System.out.println("Valor das pizzas: R$ " + String.format("%.2f", valorPedido));
        System.out.println("Frete: R$ " + String.format("%.2f", frete));
        System.out.println("VALOR TOTAL: R$ " + String.format("%.2f", valorTotalComFrete));
              
        // Finalizar pedido 
        listaPedidos.add(pedido);
        System.out.println("\n PEDIDO FINALIZADO COM SUCESSO!");
        System.out.println("ID do Pedido: " + pedido.getId());
        System.out.println();
    }

    private static double somarPizzas(List<Pizza> pizzas) {
        double valorTotal = 0;
        for (Pizza pizza : pizzas) {
            valorTotal += pizza.getPreco();
        }
        return valorTotal;
    }

    //  DESAFIO 1: MÉTODO ALTERAR PEDIDO
    // Esta funcionalidade permite buscar pedidos por ID ou nome do cliente
    // e adicionar pizzas, remover ou alterar sabores."
    // ========================================================================
    private static void alterarPedido(Scanner scanner, List<Pedido> listaPedidos) {
        System.out.println("ALTERAR PEDIDO");

        if (listaPedidos.isEmpty()) {
            System.out.println("Não há pedidos cadastrados.");
            System.out.println();
            return;
        }

        // Escolher buscar pedido por ID ou Nome
        System.out.println("Como deseja localizar o pedido?");
        System.out.println("1 - Buscar por ID");
        System.out.println("2 - Buscar pelo nome do cliente");
        System.out.print("Escolha: ");
        int tipoBusca = scanner.nextInt();
        scanner.nextLine();

        Pedido pedidoParaAlterar = null;

        // Localizar o pedido baseado na escolha
        switch (tipoBusca) {
            case 1:
                System.out.print("Informe o ID do pedido: "); // busca por ID
                int idBusca = scanner.nextInt();
                scanner.nextLine();
                
                for (Pedido p : listaPedidos) {
                    if (p.getId() == idBusca) {
                        pedidoParaAlterar = p;
                        break;
                    }
                }
                break;
                
            case 2:
                System.out.print("Informe o nome do cliente: ");// busca por Nome
                String nomeBusca = scanner.nextLine();
                
                for (Pedido p : listaPedidos) {
                    if (p.getCliente().getNome().equalsIgnoreCase(nomeBusca)) {
                        pedidoParaAlterar = p;
                        break;
                    }
                }
                break;
        }

        if (pedidoParaAlterar == null) {
            System.out.println("Pedido não localizado!");
            System.out.println();
            return;
        }

        // Mostrar informações do pedido
        System.out.println("\n PEDIDO LOCALIZADO");
        System.out.println("ID: " + pedidoParaAlterar.getId());
        System.out.println("Cliente: " + pedidoParaAlterar.getCliente().getNome());
        System.out.println("Pizzas:");
        
        for (int i = 0; i < pedidoParaAlterar.getPizzas().size(); i++) {
            Pizza p = pedidoParaAlterar.getPizzas().get(i);
            System.out.println("  " + (i + 1) + ") " + p.getSabores() + 
                            " - " + p.getTamanho() + " - R$ " + 
                            String.format("%.2f", p.getPreco()));
        }
        System.out.println("Total: R$ " + String.format("%.2f", pedidoParaAlterar.getValorTotal()));

        // Menu de alterações
        boolean continuarEdicao = true;
        while (continuarEdicao) {
            System.out.println("\n OPÇÕES DE ALTERAÇÃO");
            System.out.println("1 - Incluir nova pizza");
            System.out.println("2 - Excluir pizza");
            System.out.println("3 - Modificar sabores");
            System.out.println("0 - Concluir alterações");
            System.out.print("Escolha: ");
            int opcaoEdicao = scanner.nextInt();
            scanner.nextLine();

            if (opcaoEdicao == 1) {
                // Incluir nova pizza
                System.out.println("\n INCLUIR PIZZA");
                
                System.out.println("Tamanhos disponíveis:");
                int idx = 1;
                for (TamanhoPizza tam : Pizza.TamanhoPizza.values()) {
                    System.out.println(idx + " - " + tam);
                    idx++;
                }
                System.out.print("Tamanho: ");
                int tamEscolhido = scanner.nextInt();
                scanner.nextLine();

                int qtdSabores = 0;
                while (qtdSabores < 1 || qtdSabores > 4) {
                    System.out.print("Quantos sabores (1-4): ");
                    qtdSabores = scanner.nextInt();
                    scanner.nextLine();
                }

                Cardapio menu = new Cardapio();
                List<String> saboresNovos = new ArrayList<>();
                List<String> opcoesSabores = new ArrayList<>();

                for (int i = 0; i < qtdSabores; i++) {
                    System.out.println("Sabores disponíveis:");
                    idx = 1;
                    opcoesSabores.clear();
                    for (String sabor : menu.getCardapio().keySet()) {
                        opcoesSabores.add(sabor);
                        System.out.println(idx + " - " + sabor);
                        idx++;
                    }
                    System.out.print("Sabor " + (i + 1) + ": ");
                    int saborEscolhido = scanner.nextInt();
                    scanner.nextLine();
                    saboresNovos.add(opcoesSabores.get(saborEscolhido - 1));
                }

                Pizza pizzaNova = new Pizza(saboresNovos, menu.getPrecoJusto(saboresNovos), 
                                        TamanhoPizza.getByIndex(tamEscolhido - 1));
                pedidoParaAlterar.getPizzas().add(pizzaNova);
                
                System.out.println("Pizza incluída!");

            } else if (opcaoEdicao == 2) {
                // Excluir pizza
                System.out.println("\n EXCLUIR PIZZA");

                if (pedidoParaAlterar.getPizzas().size() <= 1) {
                    System.out.println("Impossível excluir. Pedido precisa ter pelo menos 1 pizza.");
                    continue;
                }

                System.out.println("Qual pizza excluir?");
                for (int i = 0; i < pedidoParaAlterar.getPizzas().size(); i++) {
                    Pizza p = pedidoParaAlterar.getPizzas().get(i);
                    System.out.println((i + 1) + " - " + p.getSabores() + 
                                    " (" + p.getTamanho() + ")");
                }
                
                System.out.print("Pizza: ");
                int pizzaExcluir = scanner.nextInt();
                scanner.nextLine();

                if (pizzaExcluir >= 1 && pizzaExcluir <= pedidoParaAlterar.getPizzas().size()) {
                    pedidoParaAlterar.getPizzas().remove(pizzaExcluir - 1);
                    System.out.println("Pizza excluída!");
                } else {
                    System.out.println("Opção inválida!");
                }

            } else if (opcaoEdicao == 3) {
                // Modificar sabores
                System.out.println("\n MODIFICAR SABORES");

                System.out.println("Qual pizza modificar?");
                for (int i = 0; i < pedidoParaAlterar.getPizzas().size(); i++) {
                    Pizza p = pedidoParaAlterar.getPizzas().get(i);
                    System.out.println((i + 1) + " - " + p.getSabores() + 
                                    " (" + p.getTamanho() + ")");
                }
                
                System.out.print("Pizza: ");
                int pizzaModificar = scanner.nextInt();
                scanner.nextLine();
                
                if (pizzaModificar >= 1 && pizzaModificar <= pedidoParaAlterar.getPizzas().size()) {
                    Pizza pizzaSelecionada = pedidoParaAlterar.getPizzas().get(pizzaModificar - 1);
                    
                    int novaQtdSabores = 0;
                    while (novaQtdSabores < 1 || novaQtdSabores > 4) {
                        System.out.print("Nova quantidade de sabores (1-4): ");
                        novaQtdSabores = scanner.nextInt();
                        scanner.nextLine();
                    }

                    Cardapio menu = new Cardapio();
                    List<String> novosSabores = new ArrayList<>();
                    List<String> opcoesSabores = new ArrayList<>();

                    for (int i = 0; i < novaQtdSabores; i++) {
                        System.out.println("Sabores disponíveis:");
                        int idx = 1;
                        opcoesSabores.clear();
                        for (String sabor : menu.getCardapio().keySet()) {
                            opcoesSabores.add(sabor);
                            System.out.println(idx + " - " + sabor);
                            idx++;
                        }
                        System.out.print("Novo sabor " + (i + 1) + ": ");
                        int novoSabor = scanner.nextInt();
                        scanner.nextLine();
                        novosSabores.add(opcoesSabores.get(novoSabor - 1));
                    }

                    pizzaSelecionada.setSabores(novosSabores);
                    pizzaSelecionada.setPreco(menu.getPrecoJusto(novosSabores));
                    
                    System.out.println("Sabores modificados!");
                } else {
                    System.out.println("Opção inválida!");
                }

            } else if (opcaoEdicao == 0) {
                continuarEdicao = false;
            } else {
                System.out.println("Opção inválida!");
            }
            
            // Recalcular total após alterações
            if (opcaoEdicao >= 1 && opcaoEdicao <= 3) {
                pedidoParaAlterar.setValorTotal(somarPizzas(pedidoParaAlterar.getPizzas()));
                System.out.println("Valor atualizado: R$ " + 
                                String.format("%.2f", pedidoParaAlterar.getValorTotal()));
            }
        }
        
        System.out.println("Pedido alterado com sucesso!");
        System.out.println();
    }

    private static Cliente adicionarcliente(Scanner scanner) {
        System.out.println("ADICIONAR CLIENTE"); 
        System.out.println(); 
        System.out.print("Digite o nome do cliente: "); 
        String nome = scanner.nextLine();
        System.out.println(); 
        System.out.print("Digite o endereço do cliente: "); 
        String endereco = scanner.nextLine();
        System.out.println(); 
        System.out.print("Digite o telefone do cliente: "); 
        String telefone = scanner.nextLine();
        System.out.println(); 
        System.out.print("Digite o email do cliente: "); 
        String email = scanner.nextLine();
        System.out.println();      

        Cliente cliente = new Cliente(nome, endereco, telefone, email);
        return cliente;
    }

    //  DESAFIO 2: IMPLEMENTAÇÃO DO MÉTODO GERAR RELATÓRIO COMPLETO
    // O segundo desafio era criar um relatório de vendas completo.
    // Este método gera faturamento total, sabores mais pedidos e conexões entre sabores.
    // ========================================================================
    private static void gerarRelatorio(List<Pedido> listaPedidos) {
        System.out.println("RELATÓRIO DE VENDAS COM ANÁLISE DE GRAFOS");
        System.out.println();

        if (listaPedidos.isEmpty()) {
            System.out.println("Não há pedidos para gerar relatório.");
            System.out.println();
            return;
        }

        //Calcular faturamento e contagem de sabores
        double faturamentoTotal = 0;
        Map<String, Integer> contadorSabores = new HashMap<>();
        
        //Criar instância do grafo para conexões entre sabores
        GrafoPizza grafoSabores = new GrafoPizza();

        //Processar todos os pedidos
        for (Pedido pedido : listaPedidos) {
            faturamentoTotal += pedido.getValorTotal();

            for (Pizza pizza : pedido.getPizzas()) {
                List<String> sabores = pizza.getSabores();
                
                // Contar cada sabor individualmente
                for (String sabor : sabores) {
                    contadorSabores.put(sabor, contadorSabores.getOrDefault(sabor, 0) + 1);
                }
                
                // CRIAR CONEXÕES NO GRAFO para sabores que aparecem na mesma pizza
                for (int i = 0; i < sabores.size(); i++) {
                    for (int j = i + 1; j < sabores.size(); j++) {
                        // Adicionar conexão bidirecional entre os sabores
                        grafoSabores.adicionarConexao(sabores.get(i), sabores.get(j));
                    }
                }
            }
        }

        //Calcular estatísticas básicas
        int totalPizzas = 0;
        for (Pedido pedido : listaPedidos) {
            totalPizzas += pedido.getPizzas().size();
        }

        //Exibir faturamento e estatísticas gerais
        System.out.println("FATURAMENTO TOTAL: R$ " + String.format("%.2f", faturamentoTotal));
        System.out.println("TOTAL DE PEDIDOS: " + listaPedidos.size());
        System.out.println("TOTAL DE PIZZAS: " + totalPizzas);
        System.out.println();

        //Ranking dos sabores mais pedidos
        System.out.println("SABORES MAIS PEDIDOS:");
        List<Map.Entry<String, Integer>> saboresOrdenados = contadorSabores.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(3)
            .collect(Collectors.toList());
        
        for (int i = 0; i < saboresOrdenados.size(); i++) {
            Map.Entry<String, Integer> entry = saboresOrdenados.get(i);
            System.out.println((i + 1) + ". " + entry.getKey() + ": " + entry.getValue() + " vezes");
        }
        System.out.println();

        //ANÁLISE DO GRAFO - Usar o método da classe SaborGrafo
        System.out.println("CONEXÕES ENTRE SABORES (GRAFO):");
        System.out.println("Sabores que frequentemente aparecem juntos na mesma pizza:");
        grafoSabores.exibirGrafo();
        System.out.println();

        //Análise detalhada das conexões mais fortes
        System.out.println("ANÁLISE DETALHADA DAS PRINCIPAIS CONEXÕES:");
        Map<String, Map<String, Integer>> grafoCompleto = grafoSabores.getGrafo();
        
        // Encontrar as 3 conexões mais fortes
        String melhorCombinacao = "";
        int maiorFrequencia = 0;
        
        Set<String> conexoesJaExibidas = new HashSet<>();
        
        for (Map.Entry<String, Map<String, Integer>> entrada : grafoCompleto.entrySet()) {
            String sabor1 = entrada.getKey();
            for (Map.Entry<String, Integer> conexao : entrada.getValue().entrySet()) {
                String sabor2 = conexao.getKey();
                int frequencia = conexao.getValue();
                
                // Criar chave única para evitar duplicatas
                String chaveConexao = sabor1.compareTo(sabor2) < 0 ? 
                    sabor1 + "-" + sabor2 : sabor2 + "-" + sabor1;
                
                if (!conexoesJaExibidas.contains(chaveConexao)) {
                    if (frequencia > maiorFrequencia) {
                        maiorFrequencia = frequencia;
                        melhorCombinacao = sabor1 + " + " + sabor2;
                    }
                    conexoesJaExibidas.add(chaveConexao);
                }
            }
        }
        
        //Valores para o negócio baseados no grafo
        System.out.println("VALORES PARA O NEGÓCIO:");
        if (!melhorCombinacao.isEmpty()) {
            System.out.println("• Combinação mais popular: " + melhorCombinacao + 
                            " (aparecem juntos " + maiorFrequencia + " vezes)");
            System.out.println("  → Considere criar uma pizza especial com essa combinação!");
        }
        
        // Verificar sabores que aparecem sempre sozinhos
        System.out.println("• Sabores pedidos predominantemente sozinhos:");
        for (Map.Entry<String, Integer> entry : saboresOrdenados) {
            String sabor = entry.getKey();
            if (!grafoCompleto.containsKey(sabor) || grafoCompleto.get(sabor).isEmpty()) {
                System.out.println("  - " + sabor + " (pizza tradicional)");
            }
        }
        
        // Estatísticas complementares
        System.out.println();
        System.out.println("ESTATÍSTICAS COMPLEMENTARES:");
        System.out.println("• Ticket médio: R$ " + 
                        String.format("%.2f", faturamentoTotal / listaPedidos.size()));
        System.out.println("• Pizzas por pedido: " + 
                        String.format("%.1f", (double) totalPizzas / listaPedidos.size()));
        System.out.println("• Sabores únicos no cardápio: " + contadorSabores.size());
        System.out.println("• Conexões únicas entre sabores: " + conexoesJaExibidas.size());
        
        System.out.println();
        
    }

    private static void gerarListaClientes(List<Cliente> listaClientes) {
        int x = 1;
        if (listaClientes.isEmpty()) {
            System.out.println("Lista de Clientes está vazia.");
            System.out.println();
        } else {
            for (Cliente cliente : listaClientes) {
                System.out.println("Cliente " + x); 
                System.out.println(cliente.getNome()); 
                System.out.println(cliente.getEndereco()); 
                System.out.println(cliente.getTelefone()); 
                System.out.println(cliente.getEmail()); 
                System.out.println();
                x++;
            }  
        }
    }
}



