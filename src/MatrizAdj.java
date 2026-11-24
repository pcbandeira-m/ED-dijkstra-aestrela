public class MatrizAdj{

    private int qtdVertices;
    private int[][] matriz;

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

        // configuração inicial

        int[] distancias_dw = new int[qtdVertices];
        boolean[] visitados_S = new boolean[qtdVertices];

        // inicio
        for(int i=0; i < qtdVertices; i++){
            distancias_dw[i] = Integer.MAX_VALUE;                     // ditância inicial = infinito
            visitados_S[i] = false;                                  // começa sem nenhum vertice ter sido visitado
        }


        distancias_dw[origem] = 0;
        // fim da configuração inicial

        // quando todos os vertices já tiverem sido visitados o algoritmo encerra
        for (int contagem = 0; contagem < qtdVertices; contagem++){

           int menorDistanciaAtual_dw = Integer.MAX_VALUE;
           int verticeAtual_W = -1;

           for (int i = 0; i < qtdVertices; i++){

                if(!visitados_S[i] && distancias_dw[i] < menorDistanciaAtual_dw){
                    verticeAtual_W = i;
                    menorDistanciaAtual_dw = distancias_dw[i];
                }
            }

            if (verticeAtual_W == -1){
                break;
            }

            visitados_S[verticeAtual_W] = true;

            for(int vVizinho = 0; vVizinho < qtdVertices; vVizinho++){
                
                if(!visitados_S[vVizinho] &&
                    matriz[verticeAtual_W][vVizinho] != 0 &&
                    distancias_dw[verticeAtual_W] + matriz[verticeAtual_W][vVizinho] < distancias_dw[vVizinho]){
                        
                        distancias_dw[vVizinho] = distancias_dw[verticeAtual_W] + matriz[verticeAtual_W][vVizinho];
                }  
            }
        }
        
        return distancias_dw[destino];
        
    }
}