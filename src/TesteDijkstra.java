public class TesteDijkstra {

    public static void main(String[] args) {
        // Criando um grid 30x30 (900 vértices) para gerar carga real de processamento
        int linhas = 30;
        int colunas = 30;
        char[][] mapa = new char[linhas][colunas];

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                mapa[i][j] = '0';
            }
        }

        // Adiciona uma barreira vertical no meio para obrigar o algoritmo a desviar
        for (int i = 5; i < 25; i++) {
            mapa[i][15] = '#';
        }

        // Monta o grafo na MatrizAdj
        MatrizAdj grafo = new MatrizAdj(linhas * colunas);
        int[] dLinha = {-1, 1, 0, 0};
        int[] dColuna = {0, 0, -1, 1};

        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                if (mapa[l][c] == '#') continue;
                int u = l * colunas + c;

                for (int d = 0; d < 4; d++) {
                    int vizL = l + dLinha[d];
                    int vizC = c + dColuna[d];
                    if (vizL >= 0 && vizL < linhas && vizC >= 0 && vizC < colunas && mapa[vizL][vizC] != '#') {
                        int v = vizL * colunas + vizC;
                        grafo.addAresta(u, v, 1);
                    }
                }
            }
        }

        // Origem no canto superior esquerdo (2, 2) e Destino no canto inferior direito (27, 27)
        int origem = 2 * colunas + 2;
        int destino = 27 * colunas + 27;

        // 1. AQUECIMENTO DA JVM (Warm-up) - ignora o tempo dessas primeiras rodadas
        for (int i = 0; i < 50; i++) {
            grafo.solucaoDijkstra(origem, destino);
        }

        // 2. MEDIÇÃO REAL (média de 100 execuções para precisão estatística)
        int repeticoes = 100;
        long tempoTotalNano = 0;
        int custoCaminho = 0;

        for (int i = 0; i < repeticoes; i++) {
            long inicio = System.nanoTime();
            custoCaminho = grafo.solucaoDijkstra(origem, destino);
            long fim = System.nanoTime();

            tempoTotalNano += (fim - inicio);
        }

        double tempoMedioMs = (tempoTotalNano / (double) repeticoes) / 1_000_000.0;

        System.out.println("=== TESTE: DIJKSTRA (Matriz de Adjacência) ===");
        System.out.println("Tamanho do Grafo      : " + (linhas * colunas) + " vértices (" + linhas + "x" + colunas + ")");
        System.out.println("Custo do Caminho      : " + custoCaminho);
        System.out.println("Vértices Expandidos   : " + grafo.getVerticeExpandidos());
        System.out.println("Total de Comparações  : " + grafo.getOperacaoComparacao());
        System.out.printf("Tempo Médio (100x)    : %.4f ms (%.0f ns)\n", tempoMedioMs, (tempoTotalNano / (double) repeticoes));
    }
}