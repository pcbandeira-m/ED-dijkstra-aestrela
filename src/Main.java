import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    // Códigos ANSI para cores e controle do cursor no terminal
    private static final String RESET = "\033[0m";
    private static final String PAREDE = "\033[90m█ " + RESET;       // Cinza escuro
    private static final String LIVRE = "\033[37m· " + RESET;        // Ponto discreto
    private static final String INICIO = "\033[1;36mS " + RESET;     // Ciano negrito (Start)
    private static final String SAIDA = "\033[1;33mE " + RESET;      // Amarelo negrito (Exit)
    private static final String RASTRO = "\033[1;32m* " + RESET;     // Verde negrito (Caminho já percorrido)
    private static final String AGENTE = "\033[1;97;42m@ " + RESET;  // Branco sobre fundo verde (Posição atual)

    public static void main(String[] args) throws InterruptedException {
        String caminhoArquivo = "labirinto1.dat";
        List<String> linhasArquivo = new ArrayList<>();

        // 1. Leitura do arquivo .dat
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (!linha.isEmpty()) {
                    linhasArquivo.add(linha);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo " + caminhoArquivo + ": " + e.getMessage());
            return;
        }

        int N = linhasArquivo.size();
        int M = linhasArquivo.get(0).length();
        int[][] labirinto = new int[N][M];

        int pontoPartida = -1;
        List<Integer> saidas = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                int valor = Character.getNumericValue(linhasArquivo.get(i).charAt(j));
                labirinto[i][j] = valor;
                int idVertice = i * M + j;

                if (valor == 2) {
                    pontoPartida = idVertice;
                } else if (valor == 3) {
                    saidas.add(idVertice);
                }
            }
        }

        // 2. Construção do grafo na Matriz de Adjacência
        MatrizAdj grafo = new MatrizAdj(N, M);
        int[][] direcoes = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (labirinto[i][j] != 1) {
                    int origem = i * M + j;
                    for (int[] dir : direcoes) {
                        int ni = i + dir[0];
                        int nj = j + dir[1];
                        if (ni >= 0 && ni < N && nj >= 0 && nj < M && labirinto[ni][nj] != 1) {
                            int destino = ni * M + nj;
                            grafo.addAresta(origem, destino, 1);
                        }
                    }
                }
            }
        }

        // 3. Aquecimento da JVM (Warm-up)
        int iteracoesWarmup = 5_000;
        for (int i = 0; i < iteracoesWarmup; i++) {
            grafo.solucaoDijkstra(pontoPartida, saidas);
            grafo.solucaoAStar(pontoPartida, saidas);
        }

        // 4. Medição de desempenho (sem interferência visual)
        int iteracoesMedicao = 1_000;

        long tempoTotalDijkstra = 0;
        int custoDijkstra = 0;
        for (int i = 0; i < iteracoesMedicao; i++) {
            long inicio = System.nanoTime();
            custoDijkstra = grafo.solucaoDijkstra(pontoPartida, saidas);
            tempoTotalDijkstra += (System.nanoTime() - inicio);
        }
        long tempoMedioDijkstra = tempoTotalDijkstra / iteracoesMedicao;
        List<Integer> caminhoDijkstra = grafo.getCaminho();
        long expandidosDijkstra = grafo.getVerticeExpandidos();
        long comparacoesDijkstra = grafo.getOperacaoComparacao();

        long tempoTotalAStar = 0;
        int custoAStar = 0;
        for (int i = 0; i < iteracoesMedicao; i++) {
            long inicio = System.nanoTime();
            custoAStar = grafo.solucaoAStar(pontoPartida, saidas);
            tempoTotalAStar += (System.nanoTime() - inicio);
        }
        long tempoMedioAStar = tempoTotalAStar / iteracoesMedicao;
        List<Integer> caminhoAStar = grafo.getCaminho();
        long expandidosAStar = grafo.getVerticeExpandidos();
        long comparacoesAStar = grafo.getOperacaoComparacao();

        // 5. Animação passo a passo da solução encontrada pelo A*
        if (custoAStar != Integer.MAX_VALUE && !caminhoAStar.isEmpty()) {
            animarSolucao(labirinto, caminhoAStar, N, M, 180); // 180ms por passo
        }

        // 6. Exibição do Relatório Comparativo logo abaixo do labirinto finalizado
        System.out.println("\n=== COMPARATIVO: DIJKSTRA vs A* ===");
        System.out.println("Dimensões do Labirinto: " + N + "x" + M + " (" + (N * M) + " vértices)\n");

        imprimirResultado("DIJKSTRA", custoDijkstra, tempoMedioDijkstra, expandidosDijkstra, comparacoesDijkstra, caminhoDijkstra, M);
        imprimirResultado("A* (Manhattan)", custoAStar, tempoMedioAStar, expandidosAStar, comparacoesAStar, caminhoAStar, M);
        double percentualAStar = ((double) tempoMedioAStar / tempoMedioDijkstra) * 100.0;
        System.out.printf("Tempo do A* em relação ao Dijkstra: %.2f%%%n%n", percentualAStar);
    }

    private static void animarSolucao(int[][] labirinto, List<Integer> caminho, int N, int M, int delayMs) throws InterruptedException {
        // Matriz de estado visual: 0=livre, 1=parede, 2=início, 3=saída, 4=rastro, 5=agente atual
        int[][] estado = new int[N][M];
        for (int i = 0; i < N; i++) {
            System.arraycopy(labirinto[i], 0, estado[i], 0, M);
        }

        // Esconde o cursor do terminal durante a animação
        System.out.print("\033[?25l");

        try {
            for (int passo = 0; passo < caminho.size(); passo++) {
                int idAtual = caminho.get(passo);
                int rAtual = idAtual / M;
                int cAtual = idAtual % M;

                // Marca o passo anterior como rastro (*), se não for o início
                if (passo > 0) {
                    int idAnterior = caminho.get(passo - 1);
                    int rAnt = idAnterior / M;
                    int cAnt = idAnterior % M;
                    if (labirinto[rAnt][cAnt] == 0) {
                        estado[rAnt][cAnt] = 4;
                    }
                }

                // Coloca o agente (@) na posição atual
                estado[rAtual][cAtual] = 5;

                // Limpa a tela e reposiciona o cursor no topo
                System.out.print("\033[H\033[2J");
                System.out.flush();

                System.out.println("=== TRAÇANDO CAMINHO ÓTIMO (A*) ===");
                System.out.printf("Passo %d/%d -> Coordenada: (%d, %d)%n%n", passo, caminho.size() - 1, rAtual, cAtual);

                renderizarFrame(estado, N, M);

                Thread.sleep(delayMs);
            }

            // Frame final: transforma o agente na saída alcançada
            int idFinal = caminho.get(caminho.size() - 1);
            estado[idFinal / M][idFinal % M] = 3;

            System.out.print("\033[H\033[2J");
            System.out.flush();
            System.out.println("=== SAÍDA ALCANÇADA COM SUCESSO! ===");
            System.out.printf("Total de passos: %d%n%n", caminho.size() - 1);
            renderizarFrame(estado, N, M);

        } finally {
            // Garante que o cursor do terminal volte a aparecer ao terminar
            System.out.print("\033[?25h");
        }
    }

    private static void renderizarFrame(int[][] estado, int N, int M) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                switch (estado[i][j]) {
                    case 1 -> sb.append(PAREDE);
                    case 2 -> sb.append(INICIO);
                    case 3 -> sb.append(SAIDA);
                    case 4 -> sb.append(RASTRO);
                    case 5 -> sb.append(AGENTE);
                    default -> sb.append(LIVRE);
                }
            }
            sb.append("\n");
        }
        System.out.print(sb);
    }

    private static void imprimirResultado(String nome, int custo, long tempoNano, long expandidos, long comparacoes, List<Integer> caminho, int colunas) {
        System.out.println("--- " + nome + " ---");
        if (custo == Integer.MAX_VALUE) {
            System.out.println("Nenhuma saída encontrada!");
            return;
        }
        System.out.printf("Tempo gasto: %.4f ms (%d ns)%n", tempoNano / 1_000_000.0, tempoNano);
        System.out.println("Passos (Custo): " + custo);
        System.out.println("Vértices expandidos: " + expandidos);
        System.out.println("Operações de comparação: " + comparacoes);
        System.out.print("Coordenadas (linha, col): ");
        for (int i = 0; i < caminho.size(); i++) {
            int id = caminho.get(i);
            System.out.print("(" + (id / colunas) + "," + (id % colunas) + ")");
            if (i < caminho.size() - 1) System.out.print(" -> ");
        }
        System.out.println("\n");
    }
}