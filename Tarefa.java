
import java.time.LocalDate;
import java.util.ArrayList;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class Tarefa implements Registro {
    private int id;

    // Chave Estrangeira
    private int idCategoria;
    private ArrayList<Integer> idEtiquetas;

    // Atributos da classe Tarefa
    private String nome;
    private LocalDate inicio;
    private LocalDate fim;
    private Byte status;
    private Byte prioridade;

    // Métodos Set's
    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setInicio(LocalDate inicio) {
        this.inicio = inicio;
    }

    public void setFim(LocalDate fim) {
        this.fim = fim;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public void setPrioridade(Byte prioridade) {
        this.prioridade = prioridade;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public void setIdEtiquetas(ArrayList<Integer> idEtiquetas) {
        this.idEtiquetas = idEtiquetas;
    }
    // Fim Métodos Set's

    // Métodos Get's
    public int getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public LocalDate getInicio() {
        return this.inicio;
    }

    public LocalDate getFim() {
        return this.fim;
    }

    public Byte getStatus() {
        return this.status;
    }

    public Byte getPrioridade() {
        return this.prioridade;
    }

    public int getIDCategoria() {
        return this.idCategoria;
    }

    public ArrayList<Integer> getIDEtiquetas() {
        return this.idEtiquetas;
    }
    // Fim Métodos Get's

    // Método toByteArray
    public byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeInt(this.id);
            dos.writeUTF(this.nome);
            dos.writeInt((int) this.inicio.toEpochDay());
            dos.writeInt((int) this.fim.toEpochDay());
            dos.writeByte(this.status);
            dos.writeByte(this.prioridade);
            dos.writeInt(this.idCategoria);
            dos.writeInt(this.idEtiquetas.size());
            for (int i = 0; i < this.idEtiquetas.size(); i++) {
                dos.writeInt(this.idEtiquetas.get(i));
            }
        } catch (Exception e) {
            System.out.println("Deu bobs ao converter Tarefa para array de byte");
            System.out.println(e.getMessage());
        }
        return baos.toByteArray();
    }

    // Método fromByteArray
    public void fromByteArray(byte[] array) {
        ByteArrayInputStream bais = new ByteArrayInputStream(array);
        DataInputStream dis = new DataInputStream(bais);
        try {
            this.id = dis.readInt(); // Lê o ID
            this.nome = dis.readUTF(); // Lê o nome
            this.inicio = LocalDate.ofEpochDay(dis.readInt()); // Lê a data de início
            this.fim = LocalDate.ofEpochDay(dis.readInt()); // Lê a data de fim
            this.status = dis.readByte(); // Lê o status
            this.prioridade = dis.readByte(); // Lê a prioridade
            this.idCategoria = dis.readInt(); // Lê o ID da categoria

            // Lê a lista de etiquetas
            int size = dis.readInt(); // Lê o tamanho da lista de etiquetas
            this.idEtiquetas = new ArrayList<>(size); // Inicializa a lista com o tamanho correto
            for (int i = 0; i < size; i++) {
                this.idEtiquetas.add(dis.readInt()); // Lê cada ID de etiqueta e adiciona à lista
            }
        } catch (Exception e) {
            System.out.println("Erro ao converter vetor de byte pra objeto tarefa");
            e.printStackTrace();
        }
    }

    // Construtores
    public Tarefa(String nome, LocalDate inicio, LocalDate fim, byte status, byte prioridade) {
        this.nome = nome;
        this.inicio = inicio;
        this.fim = fim;
        this.status = status;
        this.prioridade = prioridade;
        this.idCategoria = -1;
        this.idEtiquetas = new ArrayList<Integer>();
    }

    public Tarefa() {
        this.id = -1;
        this.inicio = null;
        this.fim = null;
        this.status = -1;
        this.prioridade = -1;
        this.idCategoria = -1;
        this.idEtiquetas = new ArrayList<Integer>();
    }

    // Fim Construtores

    @Override
    public String toString() {
        return getArgumentList();
    }

    private String getArgumentList() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id:........... ").append(Integer.toString(this.id)).append("\n");
        sb.append("Nome:......... ").append(nome).append("\n");
        sb.append("Inicio:....... ").append(inicio).append("\n");
        sb.append("Fim:.......... ").append(fim).append("\n");
        sb.append("Status:....... ").append(status).append("\n");
        sb.append("Prioridade:... ").append(prioridade).append("\n");
        return sb.toString();
    }

}
