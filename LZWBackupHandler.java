import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class LZWBackupHandler extends LZW {

    private static final int bufferSize = 1024;
    private static final String sourceDir = "./dados";
    private static final String backupDir = "./backup/";

    public void createBackup() throws Exception {
        String backupDate = getCurrentDate();
        File backupFolder = new File(backupDir + backupDate);
        if (!backupFolder.exists() && !backupFolder.mkdirs()) {
            throw new IOException("Falha ao criar o diretório de backup.");
        }

        String backupFilePath = backupFolder.getAbsolutePath() + "/backup.lzw";

        long originalSize = 0; // Tamanho total dos arquivos originais
        long compressedSize = 0; // Tamanho do arquivo compactado

        try (FileOutputStream backupStream = new FileOutputStream(backupFilePath)) {
            File[] filesToBackup = new File(sourceDir).listFiles(File::isFile);

            if (filesToBackup == null || filesToBackup.length == 0) {
                System.out.println("Nenhum arquivo encontrado para backup.");
                return;
            }

            for (File file : filesToBackup) {
                System.out.println("Comprimindo arquivo: " + file.getName());
                originalSize += file.length(); // Soma o tamanho do arquivo original

                try (FileInputStream fileStream = new FileInputStream(file)) {
                    byte[] compressedData = compressStream(fileStream);
                    compressedSize += compressedData.length; // Soma o tamanho dos dados comprimidos
                    writeFileToBackup(backupStream, file.getName(), compressedData);
                }
            }

            System.out.println("Backup concluído com sucesso!");
        }

        // Exibe a taxa de compressão
        if (originalSize > 0) {
            double compressionRate = (1 - ((double) compressedSize / originalSize)) * 100;
            System.out.printf("Taxa de compressão: %.2f%%%n", compressionRate);
        } else {
            System.out.println("Tamanho original dos arquivos é zero. Não foi possível calcular a taxa de compressão.");
        }
    }

    public void restoreBackup() throws Exception {
        String backupFilePath = selectBackupVersion();
        if (backupFilePath == null) {
            System.out.println("Nenhum backup selecionado para restauração.");
            return;
        }

        File backupFile = new File(backupFilePath);
        if (!backupFile.exists()) {
            throw new FileNotFoundException("Arquivo de backup não encontrado.");
        }

        try (FileInputStream backupStream = new FileInputStream(backupFile)) {
            while (backupStream.available() > 0) {
                String fileName = readFileName(backupStream);
                byte[] compressedData = readCompressedData(backupStream);
                byte[] decompressedData = decompressStream(new ByteArrayInputStream(compressedData));

                Path restoredFilePath = Paths.get(sourceDir, fileName);
                Files.createDirectories(restoredFilePath.getParent());
                Files.write(restoredFilePath, decompressedData);
                System.out.println("Arquivo restaurado: " + fileName);
            }
        }
    }

    private byte[] compressStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream compressedStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] compressedChunk = super.codifica(sliceArray(buffer, bytesRead));
            compressedStream.write(compressedChunk);
        }
        return compressedStream.toByteArray();
    }

    private byte[] decompressStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream decompressedStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] decompressedChunk = super.decodifica(sliceArray(buffer, bytesRead));
            decompressedStream.write(decompressedChunk);
        }
        return decompressedStream.toByteArray();
    }

    private void writeFileToBackup(FileOutputStream backupStream, String fileName, byte[] compressedData)
            throws IOException {
        byte[] fileNameBytes = fileName.getBytes("UTF-8");
        backupStream.write(intToBytes(fileNameBytes.length));
        backupStream.write(fileNameBytes);
        backupStream.write(intToBytes(compressedData.length));
        backupStream.write(compressedData);
    }

    private String readFileName(FileInputStream backupStream) throws IOException {
        int fileNameLength = bytesToInt(readBytes(backupStream, 4));
        return new String(readBytes(backupStream, fileNameLength), "UTF-8");
    }

    private byte[] readCompressedData(FileInputStream backupStream) throws IOException {
        int dataLength = bytesToInt(readBytes(backupStream, 4));
        return readBytes(backupStream, dataLength);
    }

    private byte[] readBytes(InputStream inputStream, int length) throws IOException {
        byte[] buffer = new byte[length];
        int bytesRead = inputStream.read(buffer);
        if (bytesRead != length) {
            throw new EOFException("Dados insuficientes durante a leitura.");
        }
        return buffer;
    }

    private byte[] sliceArray(byte[] array, int length) {
        byte[] result = new byte[length];
        System.arraycopy(array, 0, result, 0, length);
        return result;
    }

    private byte[] intToBytes(int value) {
        return new byte[] {
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    private int bytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                (bytes[3] & 0xFF);
    }

    public String selectBackupVersion() {
        // Aqui você pode implementar uma lógica para permitir ao usuário selecionar uma
        // versão
        // Ou então verificar se o diretório de backups contém arquivos válidos.
        Scanner scanf = new Scanner(System.in);
        System.out.println("Digite o número da versão do backup (exemplo: 2024-12-08): ");
        String backupVersion = scanf.nextLine();

        // Verifica se o arquivo de backup para essa versão existe
        String backupFilePath = backupDir + backupVersion + "/backup.lzw";
        File backupFile = new File(backupFilePath);
        if (backupFile.exists()) {
            return backupFilePath;
        } else {
            System.out.println("Backup não encontrado para a versão: " + backupVersion);
            return null;
        }
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
