import java.util.ArrayList;

public class ArquivoEtiqueta extends Arquivo<Etiqueta> {

    private ArvoreBMais<ParEtiquetaId> arvoreB;

    /* Criando o Arquivo de Etiqueta */
    public ArquivoEtiqueta() throws Exception {
        super(Etiqueta.class.getConstructor(), "ArquivoEtiqueta");
        try {
            arvoreB = new ArvoreBMais<>(ParEtiquetaId.class.getConstructor(), 5, "./dados/ArvoresEtiquetas");
        } catch (Exception e) {
            System.out.println("Erro ao inicializar a árvore B+ para etiquetas:");
            System.out.println(e.getMessage());
            throw new Exception();
        }
    }

    /* CRUD DE ETIQUETA */

    /*
     * Método para criar uma nova etiqueta.
     * Retorna o ID da etiqueta criada.
     */
    public int create(String nomeEtiqueta) throws Exception {
        try {
            // Criando a nova etiqueta
            Etiqueta etiqueta = new Etiqueta(nomeEtiqueta);
            System.out.println("Criando nova etiqueta: " + nomeEtiqueta);

            // Salvando no arquivo
            int id = super.create(etiqueta);
            etiqueta.setId(id);

            // Adicionando na árvore B+
            arvoreB.create(new ParEtiquetaId(etiqueta.getNome(), etiqueta.getId()));

            System.out.println("Etiqueta criada com sucesso! ID: " + id);
            return id;
        } catch (Exception e) {
            System.out.println("Erro ao criar a etiqueta:");
            System.out.println(e.getMessage());
            throw new Exception("Erro ao criar etiqueta.");
        }
    }

    /*
     * Método para ler as tarefas associadas a uma etiqueta.
     * Retorna a lista de tarefas.
     */
    public ArrayList<Tarefa> read(String nomeEtiqueta) throws Exception {
        ArrayList<Tarefa> tarefas = new ArrayList<>();
        try {
            System.out.println("Buscando tarefas da etiqueta: " + nomeEtiqueta);

            // Lendo na árvore B+
            ArrayList<ParEtiquetaId> etiquetas = arvoreB.read(new ParEtiquetaId(nomeEtiqueta));

            if (etiquetas.isEmpty()) {
                System.out.println("Etiqueta não encontrada: " + nomeEtiqueta);
                throw new Exception("Etiqueta inexistente");
            }

            System.out.println("Etiqueta encontrada. ID: " + etiquetas.get(0).getId());

            // Lendo as tarefas associadas
            ArquivoTarefas arquivoTarefas = new ArquivoTarefas();
            tarefas = arquivoTarefas.read(etiquetas.get(0));

            if (tarefas.isEmpty()) {
                System.out.println("Nenhuma tarefa associada à etiqueta.");
            } else {
                System.out.println("Tarefas encontradas: " + tarefas.size());
            }

        } catch (Exception e) {
            System.out.println("Erro ao ler as tarefas da etiqueta:");
            System.out.println(e.getMessage());
        }
        return tarefas;
    }

    /*
     * Método para atualizar o nome de uma etiqueta.
     * Retorna true se atualizado com sucesso.
     */
    public boolean update(String nomeEtiqueta, String novaEtiqueta) throws Exception {
        try {
            System.out.println("Atualizando etiqueta: " + nomeEtiqueta + " para " + novaEtiqueta);

            // Lendo na árvore B+
            ArrayList<ParEtiquetaId> etiquetas = arvoreB.read(new ParEtiquetaId(nomeEtiqueta));

            if (etiquetas.isEmpty()) {
                System.out.println("Etiqueta não encontrada: " + nomeEtiqueta);
                throw new Exception("Etiqueta inexistente");
            }

            Etiqueta etiqueta = new Etiqueta(novaEtiqueta);
            etiqueta.setId(etiquetas.get(0).getId());

            // Atualizando no arquivo
            if (super.update(etiqueta)) {
                System.out.println("Etiqueta atualizada no arquivo.");
            }

            // Atualizando na árvore B+
            arvoreB.delete(etiquetas.get(0));
            arvoreB.create(new ParEtiquetaId(etiqueta.getNome(), etiqueta.getId()));

            System.out.println("Etiqueta atualizada com sucesso!");
            return true;

        } catch (Exception e) {
            System.out.println("Erro ao atualizar a etiqueta:");
            System.out.println(e.getMessage());
        }
        return false;
    }

    /*
     * Método para deletar uma etiqueta.
     * Retorna true se excluída com sucesso.
     */
    public boolean delete(String nomeEtiqueta) throws Exception {
        try {
            System.out.println("Tentando deletar a etiqueta: " + nomeEtiqueta);

            // Lendo na árvore B+
            ParEtiquetaId parEtiquetaId = new ParEtiquetaId(nomeEtiqueta);
            ArrayList<ParEtiquetaId> etiquetas = arvoreB.read(parEtiquetaId);

            if (etiquetas.isEmpty()) {
                System.out.println("Etiqueta não encontrada na árvore B+: " + nomeEtiqueta);
                throw new Exception("Etiqueta inexistente");
            }

            System.out.println(
                    "Etiqueta encontrada na árvore B+: " + etiquetas.get(0).getNome() + " com ID: "
                            + etiquetas.get(0).getId());

            // Verificando se há tarefas associadas
            ArquivoTarefas arquivoTarefas = new ArquivoTarefas();
            ArrayList<Tarefa> tarefas = arquivoTarefas.read(etiquetas.get(0));

            if (!tarefas.isEmpty()) {
                System.out.println("Não é possível excluir, pois existem tarefas associadas à etiqueta.");
                throw new Exception("Tarefas existentes dentro desta etiqueta");
            }

            // Excluindo do arquivo e da árvore B+
            return super.delete(etiquetas.get(0).getId()) ? arvoreB.delete(etiquetas.get(0)) : false;

        } catch (Exception e) {
            System.out.println("Erro ao deletar a etiqueta:");
            System.out.println(e.getMessage());
        }
        return false;
    }

    /*
     * Método para listar todas as etiquetas.
     * Retorna a lista de etiquetas.
     */
    public ArrayList<Etiqueta> listar() throws Exception {
        ArrayList<Etiqueta> etiquetas = new ArrayList<>();
        try {
            System.out.println("Listando todas as etiquetas...");

            etiquetas = super.list();

            if (etiquetas.isEmpty()) {
                System.out.println("Nenhuma etiqueta foi criada ainda.");
                throw new Exception("Nenhuma etiqueta encontrada.");
            }

            for (Etiqueta etiqueta : etiquetas) {
                System.out.println("ID: " + etiqueta.getId() + " - Nome da Etiqueta: " + etiqueta.getNome());
            }

        } catch (Exception e) {
            System.out.println("Erro ao listar etiquetas:");
            System.out.println(e.getMessage());
        }
        return etiquetas;
    }
}
