public class MatrizAdj{

    private int qtdVertices;
    private int[][] matriz;
    private long verticesExpandidos;
    private long operacaoComparacao;

    public MatrizAdj(int qtdVertices){

        this.qtdVertices = qtdVertices;
        this.matriz = new int[qtdVertices][qtdVertices];

    }

    public void addAresta(int origem, int destino, int valor){

        if(origem >= 0 && origem < qtdVertices && destino >= 0 && destino < qtdVertices){

            matriz[origem][destino] = valor;
            matriz[destino][origem] = valor;

        }else{
            System.out.println("Vértice inválido!");
        }
    }


    public void mostrarMatriz(){

        System.out.println("Matriz de adjacência: ");
        for(int i = 0; i < qtdVertices; i++){
            for(int j = 0; j < qtdVertices; j++){
                System.out.print(matriz[i][j] + "\t");
            }
            System.out.println();
        }
    }


    public int solucaoDijkstra(int origem, int destino){

        verticesExpandidos = 0;
        operacaoComparacao = 0;

        int[] distancias = new int[qtdVertices];
        boolean[] visitados = new boolean[qtdVertices];

        // inicio
        for(int i=0; i < qtdVertices; i++){
            distancias[i] = Integer.MAX_VALUE;                     // ditância inicial = infinito
            visitados[i] = false;                                  // começa sem nenhum vertice ter sido visitado
        }

        distancias[origem] = 0;

        // quando todos os vertices já tiverem sido visitados o algoritmo encerra
        for (int contagem = 0; contagem < qtdVertices; contagem++){

           int menorDistancia = Integer.MAX_VALUE;
           int verticeW = -1;

           for (int i = 0; i < qtdVertices; i++){
                operacaoComparacao ++;
                if(!visitados[i] && distancias[i] < menorDistancia){
                    menorDistancia = distancias[i];
                    verticeW = i;
                }
            }

            if (verticeW == -1 || verticeW == destino){
                break;
            }

            visitados[verticeW] = true;
            verticesExpandidos++;

            for(int vVez = 0; vVez < qtdVertices; vVez++){
                operacaoComparacao ++;
                if(!visitados[vVez] &&
                    matriz[verticeW][vVez] != 0 &&
                    distancias[verticeW] != Integer.MAX_VALUE &&
                    distancias[verticeW] + matriz[verticeW][vVez] < distancias[vVez]){
                        
                        distancias[vVez] = distancias[verticeW] + matriz[verticeW][vVez];
                }  
            }
        }
        
        return distancias[destino];
        
    }

    public long getVerticeExpandidos(){
        return verticesExpandidos;
    }

    public long getOperacaoComparacao(){
        return operacaoComparacao;
    }

}