# README - **TP4: Backup Compactado**

### **Desenvolvido por:**  
Ana Cristina, Felipe Vilhena, Kenia Teixeira, Lucas Gabriel  

---

### **Resumo**  
O projeto **TP4: Backup Compactado** implementa um sistema de backup eficiente utilizando o algoritmo **LZW** para compactação e descompactação de arquivos.  

As principais funcionalidades incluem:  
- Criação de backups compactados.  
- Restauração de backups com seleção de versões.  
- Utilização de estruturas otimizadas para manipulação de dados, como **fluxos de bytes compactados**.  

O sistema foca em economia de espaço e recuperação confiável de dados, proporcionando alta taxa de compressão.

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
  Cria um backup compactado com base nos arquivos do diretório de origem.  
- `restoreBackup()`:  
  Permite selecionar e restaurar uma versão de backup.  
- `compressStream(InputStream inputStream)`:  
  Compacta dados de entrada usando o algoritmo **LZW**.  
- `decompressStream(InputStream inputStream)`:  
  Descompacta dados utilizando o mesmo algoritmo.  
- `selectBackupVersion()`:  
  Lista as versões disponíveis para recuperação e permite a seleção.  

---

#### **Classe LZW**  

Implementa o algoritmo de compactação e descompactação **LZW**.  

**Atributos:**  
- `int BITS_POR_INDICE`: Número de bits usados para representar os índices no dicionário (padrão: 12 bits).  

**Métodos:**  
- `codifica(byte[] msgBytes)`:  
  Realiza a compactação de uma mensagem.  
- `decodifica(byte[] msgCodificada)`:  
  Realiza a descompactação de uma mensagem.  

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
   A parte do LZW e implementação do mesmo é original, com auxilio do código do professor Kutova. Porém a parte do TP3 é de outro grupo.

--- 

### **Instruções para Execução**  

1. Execute o Main com: javac Main.java

2. No MENU escolha a opção 4) para realizar o backup : 
        - 0) Sair......................
        - 1) Tarefas...................
        - 2) Categorias................
        - 3) Etiquetas.................
        - 4) Realizar Backup dos Dados.
        - 5) Restaurar Backup dos Dados
   
3. Após a execução do Backup, escolha a opção 5) e escolha qual arquivo ira restaurar os dados 


