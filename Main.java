
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {
  public static void main(String[] args) throws Exception {
    CrudTarefas crudTarefas = new CrudTarefas();
    CrudCategorias crudCategorias = new CrudCategorias();
    CrudEtiquetas crudEtiquetas = new CrudEtiquetas();
    LZWBackupHandler backupLZW = new LZWBackupHandler();
    /* Criando o Scanner e Resposta para ler a aentrada do usuário */
    Scanner scanf = new Scanner(System.in);
    int resposta = 0;
    while (true) {
      try {

        /* Printando na tela as Opções */
        System.out.println("---MENU---");

        System.out.println("0) Sair......................");
        System.out.println("1) Tarefas...................");
        System.out.println("2) Categorias................");
        System.out.println("3) Etiquetas.................");
        System.out.println("4) Realizar Backup dos Dados.");
        System.out.println("5) Restaurar Backup dos Dados");

        resposta = scanf.nextInt();

        /* Switch de acordo com a escolha do usuário */
        switch (resposta) {
          case 0:
            scanf.close();
            System.out.println("Saindo...");
            System.exit(0);
          case 1:
            crudTarefas.iniciarTarefas();
            break;
          case 2:
            crudCategorias.iniciarCategoria();
            break;
          case 3:
            crudEtiquetas.iniciarEtiqueta();
            break;
          case 4:
            realizarBackup(backupLZW);
            break;
          case 5:
            restaurarBackup(backupLZW);
            break;
          default:
            System.out.println("Opção Inválida");
        }

      } catch (Exception e) {
        e.printStackTrace();
        System.out.println(e.getMessage());
      }
    }

  }

  // Método para realizar backup
  public static void realizarBackup(LZWBackupHandler backupLZW) {
    try {
      backupLZW.createBackup();
    } catch (Exception e) {
      System.out.println("Erro ao realizar backup: " + e.getMessage());
    }
  }

  // Método para restaurar backup
  public static void restaurarBackup(LZWBackupHandler backupLZW) {
    try {
      // Chamar diretamente o método restoreBackup para listar e restaurar o backup
      backupLZW.restoreBackup();
    } catch (Exception e) {
      System.out.println("Erro ao restaurar backup: " + e.getMessage());
    }
  }

  public static class CrudTarefas {
    public static ArquivoTarefas arquivoTarefas;
    public static ArquivoCategorias arquivoCategorias;
    public static ArquivoEtiqueta arquivoEtiqueta;
    Scanner scanf = new Scanner(System.in);

    // Método para iniciar as operações de tarefas
    public void iniciarTarefas() throws Exception {
      arquivoTarefas = new ArquivoTarefas();
      arquivoCategorias = new ArquivoCategorias();
      arquivoEtiqueta = new ArquivoEtiqueta();
      int resposta = 0;
      System.out.println("---TAREFAS---");

      System.out.println("1) Incluir..................");
      System.out.println("2) Buscar...................");
      System.out.println("3) Alterar..................");
      System.out.println("4) Excluir..................");
      System.out.println("5) Atualizar Etiqueta.......");
      System.out.println("6) Retornar ao Menu Anterior");

      resposta = scanf.nextInt();

      switch (resposta) {
        case 1:
          criarTarefa();
          break;
        case 2:
          listarTarefas();
          break;
        case 3:
          atualizarTarefa();
          break;
        case 4:
          Deletar();
          break;
        case 5:
          atualizarEtiqueta();
          break;
        case 6:
          break;
        default:
          System.out.println("Opção Invalida");
      }

    }

    /* Interface Deletando Tarefa */
    public void Deletar() throws Exception {

      try {

        String termo;
        int numeroTarefa = -1;
        scanf.nextLine();
        ArrayList<Tarefa> tarefas = null;
        while (tarefas == null || tarefas.size() == 0) {
          System.out.println("Digite o termo que deseja pesquisar no banco de tarefas: ");
          termo = scanf.nextLine();
          tarefas = listarTarefas(termo);
          if (tarefas == null || tarefas.size() == 0) {
            System.out.println("Erro ao buscar tarefas, tente novamente com um termo diferente");
          }
        }
        while (numeroTarefa < 0 || numeroTarefa > tarefas.size()) {
          System.out.println(
              "Digite o número da Tarefa que deseja deletar\nObs: digite 0 para cancelar (favor ignorar a mensagem de erro)");
          numeroTarefa = scanf.nextInt();
          if (numeroTarefa < 0 || numeroTarefa > tarefas.size()) {
            System.out.println("Tarefa não encontrada, tente novamente");
          }
        }
        if (arquivoTarefas.delete(tarefas.get(numeroTarefa - 1))) {
          System.out.println("Tarefa deletada com sucesso:");
        } else {
          System.out.println("Erro ao deletar a tarefa");
        }
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }

    /* Interface Listando Tarefa */

    public ArrayList<Tarefa> listarTarefas(String termo) throws Exception {
      ArrayList<Tarefa> tarefas = null;
      try {

        termo = termo.toLowerCase();
        tarefas = arquivoTarefas.listar(termo);
        for (int i = 0; i < tarefas.size(); i++) {
          System.out.println((i + 1) + "º Tarefa " + "\n" + "Nome da Tarefa: " + tarefas.get(i).getNome() + "\n"
              + "Data de Inicio: "
              + tarefas.get(i).getInicio() + "\n" + "Data de Fim: " + tarefas.get(i).getFim() + "\n" +
              "Status: " + tarefas.get(i).getStatus() + "\n" + "Prioridade: " + tarefas.get(i).getPrioridade() + "\n");
        }
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
      return tarefas;
    }

    public void listarTarefas() throws Exception {
      try {
        scanf.nextLine();
        System.out.println("Digite o termo que deseja buscar no banco de tarefas: ");
        String titulo = scanf.nextLine();
        titulo = titulo.toLowerCase();
        ArrayList<Tarefa> tarefas = arquivoTarefas.listar(titulo);
        for (int i = 0; i < tarefas.size(); i++) {
          System.out.println((i + 1) + "º Tarefa " + "\n" + "Nome da Tarefa: " + tarefas.get(i).getNome() + "\n"
              + "Data de Inicio: "
              + tarefas.get(i).getInicio() + "\n" + "Data de Fim: " + tarefas.get(i).getFim() + "\n" +
              "Status: " + tarefas.get(i).getStatus() + "\n" + "Prioridade: " + tarefas.get(i).getPrioridade() + "\n");
        }
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }

    /* Interface Atualizando Tarefa */
    public void atualizarTarefa() throws Exception {
      String termo, input;
      int numeroTarefa = -1;
      Tarefa t = new Tarefa(), old;
      ArrayList<Tarefa> tarefas = null;
      try {
        // Evitando Buffer
        scanf.nextLine();
        while (tarefas == null || tarefas.size() == 0) {
          System.out.println("Digite o termo que deseja pesquisar no banco de tarefas: ");
          termo = scanf.nextLine();
          tarefas = listarTarefas(termo);
          if (tarefas == null || tarefas.size() == 0) {
            System.out.println("Erro ao buscar tarefas, tente novamente com um termo diferente");
          }
        }
        while (numeroTarefa < 0 || numeroTarefa > tarefas.size()) {
          System.out.println(
              "Digite o número da Tarefa que deseja atualizar\nObs: digite 0 para cancelar (favor ignorar a mensagem de erro)");
          numeroTarefa = scanf.nextInt();
          if (numeroTarefa < 0 || numeroTarefa > tarefas.size()) {
            System.out.println("Tarefa não encontrada, tente novamente");
          }
        }
        old = tarefas.get(numeroTarefa - 1);
        System.out.println("Tarefa Selecionada: " + old.getNome());

        // Evitando Buffer
        scanf.nextLine();
        System.out.println("Digite seu novo nome");
        t.setNome(scanf.nextLine());

        // Scanneando a Data de Inicio
        LocalDate data = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (data == null) {
          System.out.println("Digite a data de inicio (No formato dd/MM/yyyy)");
          input = scanf.nextLine();
          try {
            data = LocalDate.parse(input, formatter);
          } catch (Exception e) {
            System.out.println("Data inválida, favor utilizar o formato (dd/MM/yyyy)");
            data = null;
          }
          if (data != null) {
            t.setInicio(data);
          }
        }

        data = null;
        // Scanneando a Data Final
        while (data == null) {
          System.out.println("Digite a data do Fim (No formato dd/MM/yyyy)");
          input = scanf.nextLine();
          try {
            data = LocalDate.parse(input, formatter);
          } catch (Exception e) {
            System.out.println("Data inválida, favor utilizar o formato (dd/MM/yyyy)");
            data = null;
          }
          if (data != null) {
            t.setFim(data);
          }
        }

        // Scanneando o Status da Tarefa
        System.out.println("Digite os Status da tarefa (0 para não iniciado, 1 para em andamento e 2 para finalizado)");
        t.setStatus((byte) scanf.nextInt());

        // Scanneando a Prioridade da Tarefa
        System.out.println("Digite a prioridade da nvoa Tarefa (Um numero inteiro)");
        t.setPrioridade((byte) scanf.nextInt());

        if (arquivoTarefas.update(old, t)) {
          System.out.println("Tarefa atualizada com sucesso");
        } else {
          System.out.println("Erro ao atualizar a tarefa");
        }

        t = null;

      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    /* Interface de Criação da Tarefa */
    public void criarTarefa() throws Exception {

      Tarefa t = new Tarefa();
      String input;
      try {
        /* Evitando Buffer */
        scanf.nextLine();

        /* Scanneando o Nome da Tarefa */
        System.out.println("Digite o nome da tarefa");
        t.setNome(scanf.nextLine());

        System.out.println("Digite o índice da categoria que desseja adicionar esta tarefa");
        System.out.println();
        arquivoCategorias.listar();
        System.out.println();
        t.setIdCategoria(scanf.nextInt());

        // Evitando Buffer
        scanf.nextLine();

        // Definindo etiquetas
        int newEtiqueta = 1;
        ArrayList<Etiqueta> etiquetas = new ArrayList<>();
        ArrayList<Integer> posEtiquetasLista = new ArrayList<>();
        System.out.println("Deseja adicionar alguma etiqueta ? (1 para sim, 0 para não)");
        newEtiqueta = scanf.nextInt();
        while (newEtiqueta == 1) {
          System.out.println("Digite o índice da etiqueta que deseja adicionar a esta tarefa");
          System.out.println();
          etiquetas = arquivoEtiqueta.listar();
          System.out.println();
          posEtiquetasLista.add(scanf.nextInt() - 1);
          System.out.println("Deseja adicionar mais etiquetas? (1 para sim, 0 para não)");
          newEtiqueta = scanf.nextInt();
        }
        ArrayList<Integer> aux = new ArrayList<>();
        for (int i = 0; i < posEtiquetasLista.size(); i++) {
          aux.add(etiquetas.get(posEtiquetasLista.get(i)).getId());
        }
        t.setIdEtiquetas(aux);

        /* Scanneando a Data de Inicio */
        LocalDate data = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        scanf.nextLine();
        while (data == null) {
          System.out.println("Digite a data de inicio (No formato dd/MM/yyyy)");
          input = scanf.nextLine();
          try {
            data = LocalDate.parse(input, formatter);
          } catch (Exception e) {
            System.out.println("Data inválida, favor utilizar o formato (dd/MM/yyyy)");
            data = null;
          }
          if (data != null) {
            t.setInicio(data);
          }
        }

        data = null;
        /* Scanneando a Data Final */
        while (data == null) {
          System.out.println("Digite a data do Fim (No formato dd/MM/yyyy)");
          input = scanf.nextLine();
          try {
            data = LocalDate.parse(input, formatter);
          } catch (Exception e) {
            System.out.println("Data inválida, favor utilizar o formato (dd/MM/yyyy)");
            data = null;
          }
          if (data != null) {
            t.setFim(data);
          }
        }

        /* Scanneando o Status da Tarefa */
        System.out.println("Digite os Status da tarefa (0 para não iniciado, 1 para em andamento e 2 para finalizado)");
        t.setStatus((byte) scanf.nextInt());

        /* Scanneando a Prioridade da Tarefa */
        System.out.println("Digite a prioridade da Tarefa (Um numero inteiro)");
        t.setPrioridade((byte) scanf.nextInt());

        arquivoTarefas.create(t);

        t = null;

      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    // Interface de Adição de Etiqueta
    public void atualizarEtiqueta() throws Exception {
      String termo;
      int numeroTarefa = -1;
      Tarefa old = new Tarefa();
      ArrayList<Tarefa> tarefas = null;
      try {
        // Evitando Buffer
        scanf.nextLine();
        while (tarefas == null || tarefas.size() == 0) {
          System.out.println("Digite o termo que deseja pesquisar no banco de tarefas: ");
          termo = scanf.nextLine();
          tarefas = listarTarefas(termo);
          // System.out.println("Tarefas: " + tarefas.size());
          if (tarefas == null || tarefas.size() == 0) {
            System.out.println("Erro ao buscar tarefas, tente novamente com um termo diferente");
          }
        }
        while (numeroTarefa < 0 || numeroTarefa > tarefas.size()) {
          System.out.println(
              "Digite o número da Tarefa que deseja atualizar\nObs: digite 0 para cancelar (favor ignorar a mensagem de erro)");
          numeroTarefa = scanf.nextInt();
          if (numeroTarefa < 0 || numeroTarefa > tarefas.size()) {
            System.out.println("Tarefa não encontrada, tente novamente");
          } else if (numeroTarefa == 0) {
            return;
          }
        }
        old = tarefas.get(numeroTarefa - 1);
        System.out.println("Tarefa Selecionada: " + old.getNome());
      } catch (Exception e) {
        e.printStackTrace();
      }

      // Definindo etiquetas
      int newEtiqueta = 1;
      ArrayList<Etiqueta> etiquetas = new ArrayList<>();
      ArrayList<Integer> posEtiquetasLista = new ArrayList<>();
      System.out.println("Deseja remover etiquetas ? (1 para sim, 0 para não)");
      newEtiqueta = scanf.nextInt();
      while (newEtiqueta == 1) {
        System.out.println("Digite o índice da etiqueta que deseja remover dessa tarefa");
        System.out.println();
        etiquetas = arquivoEtiqueta.listar();
        System.out.println();
        posEtiquetasLista.add(scanf.nextInt() - 1);
        System.out.println("Deseja remover mais etiquetas? (1 para sim, 0 para não)");
        newEtiqueta = scanf.nextInt();
      }
      ArrayList<Integer> removed = new ArrayList<>();
      for (int i = 0; i < posEtiquetasLista.size(); i++) {
        removed.add(etiquetas.get(posEtiquetasLista.get(i)).getId());
      }
      posEtiquetasLista.clear();
      System.out.println("Deseja adicionar etiquetas ? (1 para sim, 0 para não)");
      newEtiqueta = scanf.nextInt();
      while (newEtiqueta == 1) {
        System.out.println("Digite o índice da etiqueta que deseja adicionar dessa tarefa");
        System.out.println();
        etiquetas = arquivoEtiqueta.listar();
        System.out.println();
        posEtiquetasLista.add(scanf.nextInt() - 1);
        System.out.println("Deseja adicionar mais etiquetas? (1 para sim, 0 para não)");
        newEtiqueta = scanf.nextInt();
      }
      ArrayList<Integer> added = new ArrayList<>();
      for (int i = 0; i < posEtiquetasLista.size(); i++) {
        added.add(etiquetas.get(posEtiquetasLista.get(i)).getId());
      }

      if (arquivoTarefas.updateEtiquetas(old, removed, added)) {
        System.out.println("Etiquetas atualizadas com sucesso");
      } else {
        System.out.println("Erro ao atualizar as etiquetas");
      }
    }
  }

  public static class CrudCategorias {
    public static ArquivoCategorias categoria;

    Scanner scanf = new Scanner(System.in);

    public void iniciarCategoria() throws Exception {
      categoria = new ArquivoCategorias();
      int resposta = 0;
      System.out.println("---CATEGORIAS---");

      System.out.println("1) Incluir..................");
      System.out.println("2) Buscar...................");
      System.out.println("3) Alterar..................");
      System.out.println("4) Excluir..................");
      System.out.println("5) Retornar ao Menu Anterior");

      resposta = scanf.nextInt();

      switch (resposta) {
        case 1:
          criarCategoria();
          break;
        case 2:
          listarCategoria();
          break;
        case 3:
          atualizarCategoria();
          break;
        case 4:
          deletarCategoria();
          break;
        case 5:
          break;
        default:
          System.out.println("Opção Inválida");
          break;
      }
    }

    public void criarCategoria() throws Exception {
      try {
        System.out.println("Digite o nome da Categoria a ser criada:");
        scanf.nextLine(); // Limpa o buffer
        String nomeCategoria = scanf.nextLine();

        if (nomeCategoria.isEmpty()) {
          System.out.println("O nome da categoria não pode ser vazio.");
          return;
        }

        // Cria a nova categoria
        categoria.create(nomeCategoria);
        System.out.println("Categoria criada com sucesso!");
      } catch (Exception e) {
        System.out.println("Erro ao criar a categoria: " + e.getMessage());
      }
    }

    public void listarCategoria() throws Exception {
      String nomeCategoria;
      try {
        scanf.nextLine();
        System.out.println("Digite o nome da Categoria que deseja listar as tarefas");
        System.out.println();
        categoria.listar();

        nomeCategoria = scanf.nextLine();

        ArrayList<Tarefa> t = categoria.read(nomeCategoria);

        for (int i = 0; i < t.size(); i++) {
          System.out.println("\n" + "Nome da Tarefa: " + t.get(i).getNome() + "\n" + "Data de Inicio: "
              + t.get(i).getInicio() + "\n" + "Data de Fim: " + t.get(i).getFim() + "\n" +
              "Status: " + t.get(i).getStatus() + "\n" + "Prioridade: " + t.get(i).getPrioridade() + "\n");
        }

      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }

    public void atualizarCategoria() throws Exception {
      String nomeCategoria, novaCategoria;
      try {
        scanf.nextLine();
        System.out.println("Digite o nome da Categoria que deseja atualizar");
        System.out.println();
        categoria.listar();
        nomeCategoria = scanf.nextLine();

        System.out.println("Digite o nome da nova categoria");
        novaCategoria = scanf.nextLine();

        categoria.update(nomeCategoria, novaCategoria);
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
      System.out.println("Atualizado com sucesso");
    }

    public void deletarCategoria() throws Exception {
      try {
        System.out.println("Digite o número da Categoria que deseja deletar:");
        ArrayList<Categoria> categorias = categoria.listar(); // Lista todas as categorias

        // Mostra as categorias disponíveis com seus índices
        for (int i = 0; i < categorias.size(); i++) {
          System.out.println((i + 1) + " - " + categorias.get(i).getNome());
        }

        // Lê o índice escolhido
        int index = scanf.nextInt();
        index -= 1;
        while (index < 0 || index >= categorias.size()) {
          System.out.println("Digite um índice válido:");
          index = scanf.nextInt();
        }

        // Captura o nome da categoria selecionada
        scanf.nextLine(); // Limpa o buffer antes de ler o próximo input
        String nomeCategoria = categorias.get(index).getNome();

        // Tenta deletar a categoria
        if (categoria.delete(nomeCategoria)) {
          System.out.println("Deletado com sucesso: " + nomeCategoria);
        } else {
          System.out.println("Erro ao deletar a categoria.");
        }
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }

  }

  public static class CrudEtiquetas {
    public static ArquivoEtiqueta arqEtiqueta;
    Scanner scanf = new Scanner(System.in);

    public void iniciarEtiqueta() throws Exception {
      arqEtiqueta = new ArquivoEtiqueta();
      int resposta = 0;
      System.out.println("---ETIQUETAS---");

      System.out.println("1) Incluir..................");
      System.out.println("2) Buscar...................");
      System.out.println("3) Alterar..................");
      System.out.println("4) Excluir..................");
      System.out.println("5) Retornar ao Menu Anterior");

      resposta = scanf.nextInt();

      switch (resposta) {
        case 1:
          criarEtiqueta();
          break;
        case 2:
          listarEtiqueta();
          break;
        case 3:
          atualizarEtiqueta();
          break;
        case 4:
          deletarEtiqueta();
          break;
        case 5:
          break;
        default:
          System.out.println("Opção Inválida");
          break;
      }
    }

    public void criarEtiqueta() throws Exception {
      try {
        /* Limpar o buffer */
        scanf.nextLine();
        System.out.println("Digite o nome da Etiqueta a ser Criada");
        arqEtiqueta.create(scanf.nextLine());
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
      System.out.println("Criado com sucesso");
      System.out.println();
      arqEtiqueta.listar();
    }

    public void listarEtiqueta() throws Exception {
      String nomeEtiqueta;
      try {
        scanf.nextLine();
        System.out.println("Digite o nome da etiqueta que deseja listar as tarefas");
        System.out.println();
        arqEtiqueta.listar();

        nomeEtiqueta = scanf.nextLine();

        ArrayList<Tarefa> t = arqEtiqueta.read(nomeEtiqueta);

        for (int i = 0; i < t.size(); i++) {
          System.out.println("\n" + "Nome da Tarefa: " + t.get(i).getNome() + "\n" + "Data de Inicio: "
              + t.get(i).getInicio() + "\n" + "Data de Fim: " + t.get(i).getFim() + "\n" +
              "Status: " + t.get(i).getStatus() + "\n" + "Prioridade: " + t.get(i).getPrioridade() + "\n");
        }

      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }

    public void atualizarEtiqueta() throws Exception {
      String nomeEtiqueta, novaEtiqueta;
      try {
        scanf.nextLine();
        System.out.println("Digite o nome da etiqueta que deseja atualizar");
        System.out.println();
        arqEtiqueta.listar();
        nomeEtiqueta = scanf.nextLine();

        System.out.println("Digite o nome da nova etiqueta");
        novaEtiqueta = scanf.nextLine();

        arqEtiqueta.update(nomeEtiqueta, novaEtiqueta);
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
      System.out.println("Atualizado com sucesso");
    }

    public void deletarEtiqueta() throws Exception {
      String nomeEtiqueta;
      try {
        scanf.nextLine();
        System.out.println(
            "Digite o índice da etiqueta que deseja deletar\n Obs: digite 0 para cancelar (favor ignorar a mensagem de erro)");
        System.out.println();
        ArrayList<Etiqueta> etiquetas = arqEtiqueta.listar();
        int index = scanf.nextInt();
        while (index < 0 || index > etiquetas.size()) {
          System.out.println("Digite um índice válido");
          index = scanf.nextInt();
        }
        nomeEtiqueta = etiquetas.get(index - 1).getNome();
        if (arqEtiqueta.delete(nomeEtiqueta)) {
          System.out.println("Deletado com sucesso: " + nomeEtiqueta);
        }
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }
}
