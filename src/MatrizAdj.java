import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatrizAdj {

    private int qtdVertices;
    private int[][] matriz;
    private long verticesExpandidos;
    private long operacaoComparacao;

    private int linhas;
    private int colunas;
    private int[] predecessores;
    private int destinoAlcancado;

    // Cache para evitar recálculos durante a busca
    private int[] heuristicaCache;
    private boolean[] isDestinoCache;
    private List<Integer> ultimosDestinos;

    public MatrizAdj(int linhas, int colunas) {
        this.linhas = linhas;
        this.colunas = colunas;
        this.qtdVertices = linhas * colunas;
        this.matriz = new int[qtdVertices][qtdVertices];
        this.predecessores = new int[qtdVertices];
        this.heuristicaCache = new int[qtdVertices];
        this.isDestinoCache = new boolean[qtdVertices];
    }

    public void addAresta(int origem, int destino, int valor) {
        if (origem >= 0 && origem < qtdVertices && destino >= 0 && destino < qtdVertices) {
            matriz[origem][destino] = valor;
            matriz[destino][origem] = valor;
        } else {
            System.out.println("Vértice inválido!");
        }
    }

    public void mostrarMatriz() {
        System.out.println("Matriz de adjacência: ");
        for (int i = 0; i < qtdVertices; i++) {
            for (int j = 0; j < qtdVertices; j++) {
                System.out.print(matriz[i][j] + "\t");
            }
            System.out.println();
        }
    }

    // Prepara lookup O(1) de destinos e tabela de heurística (Distância de Manhattan)
    private void prepararMetadadosDestinos(List<Integer> destinos) {
        if (destinos == ultimosDestinos) {
            return;
        }
        this.ultimosDestinos = destinos;
        
        for (int i = 0; i < qtdVertices; i++) {
            isDestinoCache[i] = false;
            int linhaAtual = i / colunas;
            int colAtual = i % colunas;
            int menorDist = Integer.MAX_VALUE;

            for (int dest : destinos) {
                int linhaDest = dest / colunas;
                int colDest = dest % colunas;
                int dist = Math.abs(linhaAtual - linhaDest) + Math.abs(colAtual - colDest);
                if (dist < menorDist) {
                    menorDist = dist;
                }
            }
            heuristicaCache[i] = menorDist;
        }

        for (int dest : destinos) {
            isDestinoCache[dest] = true;
        }
    }

    public int solucaoDijkstra(int origem, List<Integer> destinos) {
        prepararMetadadosDestinos(destinos);
        verticesExpandidos = 0;
        operacaoComparacao = 0;
        destinoAlcancado = -1;

        int[] distancias = new int[qtdVertices];
        boolean[] visitados = new boolean[qtdVertices];

        for (int i = 0; i < qtdVertices; i++) {
            distancias[i] = Integer.MAX_VALUE;
            predecessores[i] = -1;
        }

        distancias[origem] = 0;

        for (int contagem = 0; contagem < qtdVertices; contagem++) {
            int menorDistancia = Integer.MAX_VALUE;
            int verticeW = -1;

            for (int i = 0; i < qtdVertices; i++) {
                operacaoComparacao++;
                if (!visitados[i] && distancias[i] < menorDistancia) {
                    menorDistancia = distancias[i];
                    verticeW = i;
                }
            }

            if (verticeW == -1) {
                break;
            }

            if (isDestinoCache[verticeW]) {
                destinoAlcancado = verticeW;
                break;
            }

            visitados[verticeW] = true;
            verticesExpandidos++;

            int[] linhaAdj = matriz[verticeW];
            int distW = distancias[verticeW];

            for (int vVez = 0; vVez < qtdVertices; vVez++) {
                operacaoComparacao++;
                int peso = linhaAdj[vVez];
                if (!visitados[vVez] && peso != 0 && distW + peso < distancias[vVez]) {
                    distancias[vVez] = distW + peso;
                    predecessores[vVez] = verticeW;
                }
            }
        }

        return destinoAlcancado != -1 ? distancias[destinoAlcancado] : Integer.MAX_VALUE;
    }

    // Algoritmo A* otimizado: mesma alocação de memória do Dijkstra + heurística O(1)
    public int solucaoAStar(int origem, List<Integer> destinos) {
        prepararMetadadosDestinos(destinos);
        verticesExpandidos = 0;
        operacaoComparacao = 0;
        destinoAlcancado = -1;

        final int INF = 1_000_000_000;
        int[] gScore = new int[qtdVertices];
        int[] fScore = new int[qtdVertices];
        boolean[] visitados = new boolean[qtdVertices];

        for (int i = 0; i < qtdVertices; i++) {
            gScore[i] = INF;
            fScore[i] = INF;
            predecessores[i] = -1;
        }

        gScore[origem] = 0;
        fScore[origem] = heuristicaCache[origem];

        for (int contagem = 0; contagem < qtdVertices; contagem++) {
            int menorF = INF;
            int verticeW = -1;

            // Loop de seleção agora é IDÊNTICO ao do Dijkstra (apenas 1 comparação e 1 leitura)
            for (int i = 0; i < qtdVertices; i++) {
                operacaoComparacao++;
                if (!visitados[i] && fScore[i] < menorF) {
                    menorF = fScore[i];
                    verticeW = i;
                }
            }

            if (verticeW == -1) {
                break;
            }

            if (isDestinoCache[verticeW]) {
                destinoAlcancado = verticeW;
                break;
            }

            visitados[verticeW] = true;
            verticesExpandidos++;

            int[] linhaAdj = matriz[verticeW];
            int gAtual = gScore[verticeW];

            for (int vVez = 0; vVez < qtdVertices; vVez++) {
                operacaoComparacao++;
                int peso = linhaAdj[vVez];
                if (!visitados[vVez] && peso != 0) {
                    int novoG = gAtual + peso;
                    if (novoG < gScore[vVez]) {
                        gScore[vVez] = novoG;
                        fScore[vVez] = novoG + heuristicaCache[vVez];
                        predecessores[vVez] = verticeW;
                    }
                }
            }
        }

        return destinoAlcancado != -1 ? gScore[destinoAlcancado] : Integer.MAX_VALUE;
    }

    public List<Integer> getCaminho() {
        List<Integer> caminho = new ArrayList<>();
        if (destinoAlcancado == -1) {
            return caminho;
        }

        int atual = destinoAlcancado;
        while (atual != -1) {
            caminho.add(atual);
            atual = predecessores[atual];
        }
        Collections.reverse(caminho);
        return caminho;
    }

    public long getVerticeExpandidos() {
        return verticesExpandidos;
    }

    public long getOperacaoComparacao() {
        return operacaoComparacao;
    }
}