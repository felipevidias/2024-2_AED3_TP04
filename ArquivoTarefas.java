
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ArquivoTarefas extends Arquivo<Tarefa> {
    /* Instanciando o Objeto "ArvoreBMais" */
    ArvoreBMais<ParIDcIDt> arvoreB;
    ArvoreBMais<ParIDEtiquetacID> arvoreB2;

    /*
     * Instanciando o Objeto "StopWords" que representa o centro de atividades
     * da lista invertida
     */
    StopWords stopWords;

    public ArquivoTarefas() throws Exception {

        super(Tarefa.class.getConstructor(), "arquivoTarefas");
        try {
            arvoreB = new ArvoreBMais<>(ParIDcIDt.class.getConstructor(), 5, "./dados/ArvoreTarefas");
            arvoreB2 = new ArvoreBMais<>(ParIDEtiquetacID.class.getConstructor(), 5, "./dados/ArvoreTarefasEtiquetas");
        } catch (Exception e) {
            throw new Exception("Erro na criação da Arvore");
        }
        try {
            stopWords = new StopWords();
        } catch (Exception e) {
            throw new Exception("Erro na criação da lista invertida");
        }
    }

    /* Método de Criação da Tarefa. Retornando o ID */
    @Override
    public int create(Tarefa tarefa) throws Exception {
        int id = super.create(tarefa);
        tarefa.setId(id);
        System.out.println(id);
        arvoreB.create(new ParIDcIDt(tarefa.getIDCategoria(), id));
        ArrayList<Integer> idEtiquetas = tarefa.getIDEtiquetas();
        for (int i = 0; i < idEtiquetas.size(); i++) {
            arvoreB2.create(new ParIDEtiquetacID(idEtiquetas.get(i), id));
        }
        stopWords.inserir(tarefa.getNome(), id);
        return id;
    }

    /* Método de Leitura. Lendo os ID's do ArrayList. Retorna as Tarefas */
    public ArrayList<Tarefa> read(ParNomeId parNomeId) throws Exception {

        ArrayList<Tarefa> t = new ArrayList<>();
        ArrayList<ParIDcIDt> id = new ArrayList<>();
        id = arvoreB.read(new ParIDcIDt(parNomeId.getId()));
        for (int i = 0; i < id.size(); i++) {
            t.add(super.read(id.get(i).getId2()));
        }
        return t;
    }

    public ArrayList<Tarefa> read(ParEtiquetaId parEtiquetaId) throws Exception {

        ArrayList<Tarefa> t = new ArrayList<>();
        ArrayList<ParIDEtiquetacID> id = new ArrayList<>();
        id = arvoreB2.read(new ParIDEtiquetacID(parEtiquetaId.getId()));
        for (int i = 0; i < id.size(); i++) {
            t.add(super.read(id.get(i).getId2()));
        }
        return t;
    }

    public boolean update(Tarefa tarefa, Tarefa update) {
        boolean result = false;
        update.setId(tarefa.getId());

        try {
            String[] chaves = stopWords.stopWordsCheck(tarefa.getNome());
            for (int i = 0; i < chaves.length; i++) {
                System.out.println(chaves[i]);
                chaves[i] = chaves[i].toLowerCase();
                stopWords.lista.delete(chaves[i], tarefa.getId());
            }
            stopWords.inserir(update.getNome(), update.getId());
            result = super.update(update);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public boolean delete(Tarefa tarefa) {
        boolean result = false;
        try {
            result = super.delete(tarefa.getId())
                    ? arvoreB.delete(new ParIDcIDt(tarefa.getIDCategoria(), tarefa.getId()))
                    : false;
            String[] chaves = stopWords.stopWordsCheck(tarefa.getNome());
            ArrayList<Integer> idEtiquetas = tarefa.getIDEtiquetas();
            for (int i = 0; i < idEtiquetas.size(); i++) {
                arvoreB2.delete(new ParIDEtiquetacID(idEtiquetas.get(i), tarefa.getId()));
            }
            for (int i = 0; i < chaves.length; i++) {
                chaves[i] = chaves[i].toLowerCase();
                stopWords.lista.delete(chaves[i], tarefa.getId());
            }
            stopWords.lista.decrementaEntidades();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    public ArrayList<Tarefa> listar(String titulo) throws Exception {
        ArrayList<ElementoLista> elementos = new ArrayList<>();
        String[] chaves = stopWords.stopWordsCheck(titulo);
        for (int i = 0; i < chaves.length; i++) {

            if (chaves[i] != "" && chaves[i] != " ") {
                try {
                    ElementoLista[] elementoEncontrados;
                    elementoEncontrados = stopWords.lista.read(chaves[i]);
                    if (elementoEncontrados == null) {
                        System.out.println("Nenhuma tarefa encontrada com esse termo");
                    } else {
                        for (int j = 0; j < elementoEncontrados.length; j++) {
                            float frequencia = elementoEncontrados[j].getFrequencia();
                            float idf = stopWords.lista.numeroEntidades();

                            idf /= elementoEncontrados.length;

                            ElementoLista elementoAux = new ElementoLista(elementoEncontrados[j].getId(),
                                    frequencia * idf);

                            boolean existe = false;
                            for (int z = 0; z < elementos.size(); z++) {
                                if (elementos.get(z).getId() == elementoAux.getId()) {
                                    elementos.get(z).setFrequencia(
                                            elementos.get(z).getFrequencia() + elementoAux.getFrequencia());
                                    existe = true;
                                    z = elementos.size();
                                }
                            }
                            if (!existe) {
                                elementos.add(elementoAux);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        // Ordenar a lista de elementos pela frequência TF-IDF
        Collections.sort(elementos, new Comparator<ElementoLista>() {
            @Override
            public int compare(ElementoLista e1, ElementoLista e2) {
                return Float.compare(e2.getFrequencia(), e1.getFrequencia());
            }
        });

        // Converter os elementos ordenados em tarefas
        ArrayList<Tarefa> tarefas = new ArrayList<>();
        for (ElementoLista elemento : elementos) {
            tarefas.add(super.read(elemento.getId()));
        }

        return tarefas;
    }

    public boolean updateEtiquetas(Tarefa tarefa, ArrayList<Integer> removed, ArrayList<Integer> added) {
        boolean result = false;

        try {
            ArrayList<Integer> idEtiquetas = tarefa.getIDEtiquetas();

            if (idEtiquetas.size() > 0) {
                for (int i = 0; i < removed.size(); i++) {
                    boolean existe = false;
                    for (int j = 0; j < idEtiquetas.size(); j++) {
                        if (removed.get(i) == idEtiquetas.get(j)) {

                            existe = true;
                        } else if (j == idEtiquetas.size() - 1 && !existe) {
                            System.out.println("Etiqueta não encontrada");
                        }
                    }
                    if (existe) {
                        arvoreB2.delete(new ParIDEtiquetacID(removed.get(i), tarefa.getId()));
                        idEtiquetas.remove(removed.get(i));
                    }
                }
            } else if (removed.size() > 0 && idEtiquetas.size() == 0) {
                System.out.println("Não há etiquetas para serem removidas");
            }
            for (int i = 0; i < added.size(); i++) {
                boolean existe = false;
                if (idEtiquetas.size() > 0) {
                    for (int j = 0; j < idEtiquetas.size(); j++) {

                        if (added.get(i) == idEtiquetas.get(j)) {
                            System.out.println("Etiqueta já existente");
                            existe = true;
                        }
                    }
                }
                if (!existe) {

                    idEtiquetas.add(added.get(i));
                    arvoreB2.create(new ParIDEtiquetacID(added.get(i), tarefa.getId()));
                }
            }
            boolean update = super.update(tarefa);
            tarefa.setIdEtiquetas(idEtiquetas);
            if (update) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return result;
    }
}
