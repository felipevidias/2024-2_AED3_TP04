# README - **TP4: Backup Compactado**

### **Desenvolvido por:**  
Ana Cristina, Felipe Vilhena Dias, Kenia Teixeira, Lucas Gabriel  

---

### **Resumo**  
O projeto **TP4: Backup Compactado** implementa um sistema de backup eficiente utilizando o algoritmo **LZW** para compactação e descompactação de arquivos.  

As principais funcionalidades incluem:  
- Criação de backups compactados.  
- Restauração de backups com seleção de versões.  
- Utilização de estruturas otimizadas para manipulação de dados, como **fluxos de bytes compactados**.  

O sistema garante economia de espaço e recuperação confiável de dados, proporcionando uma taxa de compressão eficiente, dependendo dos dados armazenados.

---

### **Funcionalidades**  

1. **Criação de Backup Compactado:**  
   - Compacta todos os arquivos do diretório de origem usando o algoritmo **LZW**.  
   - Salva os backups no diretório configurado, organizados por data e hora.  

2. **Restauração de Backup:**  
   - Permite selecionar e restaurar uma versão específica de backup.  
   - Descompacta os arquivos e os restaura no diretório original.  

3. **Seleção de Versão:**  
   - Exibe as versões disponíveis para recuperação.  
   - Garante flexibilidade na escolha do backup a ser restaurado.  

4. **Taxa de Compressão:**  
   - Mede a eficiência do processo de compressão, comparando os tamanhos dos arquivos originais e compactados.  

---

### **Descrição das Classes e Métodos**

#### **Classe LZWBackupHandler**  

Gerencia o processo de backup e restauração, utilizando o algoritmo **LZW**.  

**Atributos:**  
- `int bufferSize`: Tamanho do buffer usado na compactação.  
- `String sourceDir`: Diretório de origem dos arquivos.  
- `String backupDir`: Diretório de armazenamento dos backups.  

**Métodos:**  
- `createBackup()`:  
  Criar um backup compactado com base nos arquivos do diretório de origem.  
- `restoreBackup()`:  
  Selecionar e restaurar uma versão de backup.  
- `compressStream(InputStream inputStream)`:  
  Compactar dados de entrada usando o algoritmo **LZW**.  
- `decompressStream(InputStream inputStream)`:  
  Descompactar dados utilizando o mesmo algoritmo.  
- `selectBackupVersion()`:  
  Listar as versões disponíveis para recuperação e permitir a seleção.  

---

#### **Classe LZW**  

Implementa o algoritmo de compactação e descompactação **LZW**.  

**Atributos:**  
- `int BITS_POR_INDICE`: Número de bits usados para representar os índices no dicionário (padrão: 12 bits).  

**Métodos:**  
- `codifica(byte[] msgBytes)`:  
  Realizar a compactação de uma mensagem.  
- `decodifica(byte[] msgCodificada)`:  
  Realizar a descompactação de uma mensagem.  

---

### **Classe VetorDeBits**  

A classe `VetorDeBits` fornece uma estrutura para manipular bits individualmente, utilizando a classe `BitSet` do Java como base. Ela permite a manipulação eficiente de bits, além de conversão para arrays de bytes e representação textual.

**Atributos:**  
- `BitSet vetor`:  
  Objeto da classe `BitSet` que armazena os bits manipulados pela classe.  

**Métodos:**  
- `VetorDeBits()`:  
  Construtor padrão que inicializa o vetor de bits com um único bit definido como `1` no índice `0`.  

- `VetorDeBits(int n)`:  
  Inicializa o vetor de bits com tamanho mínimo `n` e define o bit no índice `n`.  

- `VetorDeBits(byte[] v)`:  
  Constrói o vetor de bits a partir de um array de bytes fornecido como entrada.  

- `byte[] toByteArray()`:  
  Retorna o vetor de bits como um array de bytes, ideal para persistência ou transmissão de dados.  

- `void set(int i)`:  
  Define o bit no índice `i` como `1`. Caso o índice seja maior que o comprimento atual, ajusta o vetor dinamicamente.  

- `void clear(int i)`:  
  Define o bit no índice `i` como `0`. Caso o índice seja maior que o comprimento atual, ajusta o vetor dinamicamente.  

- `boolean get(int i)`:  
  Retorna o estado do bit no índice `i` (`true` para `1` e `false` para `0`).  

- `int length()`:  
  Retorna o comprimento do vetor de bits, excluindo o bit adicional de controle.  

- `int size()`:  
  Retorna o tamanho interno do vetor de bits.  

- `String toString()`:  
  Retorna uma representação textual do vetor de bits, composta por `0`s e `1`s.  

---


### **Checklist**  

- O sistema permite criar backups compactados? **Sim**  
- O sistema permite restaurar backups selecionando versões? **Sim**  
- A compactação utiliza o algoritmo LZW? **Sim**  
- As funcionalidades foram testadas? **Sim**  

---

### **Respostas às Perguntas**

1. **Há uma rotina de compactação usando o algoritmo LZW para fazer backup dos arquivos?**  
   **Sim**, o método `createBackup()` realiza a compactação dos arquivos utilizando o algoritmo LZW.  

2. **Há uma rotina de descompactação usando o algoritmo LZW para recuperação dos arquivos?**  
   **Sim**, o método `restoreBackup()` utiliza o LZW para descompactar os dados e restaurá-los no diretório original.  

3. **O usuário pode escolher a versão a recuperar?**  
   **Sim**, o método `selectBackupVersion()` exibe as versões disponíveis para recuperação e permite que o usuário escolha qual restaurar.  

4. **Qual foi a taxa de compressão alcançada por esse backup?**  
   A taxa de compressão varia conforme os dados originais, mas durante os testes observamos uma redução média de **30-50%** no tamanho dos arquivos.  

5. **O trabalho está funcionando corretamente?**  
   **Sim**, todas as funcionalidades foram implementadas e testadas, incluindo a compactação, descompactação e restauração de backups.  

6. **O trabalho está completo?**  
   **Sim**, todas as funcionalidades planejadas foram implementadas com sucesso.  

7. **O trabalho é original e não a cópia de um trabalho de um colega?**  
   O algoritmo LZW foi implementado com base no aprendizado em sala de aula, utilizando referências fornecidas pelo professor Kutova.

---

### **Instruções para Execução**  

1. Compile o programa com:  
   ```bash
   javac Main.java
