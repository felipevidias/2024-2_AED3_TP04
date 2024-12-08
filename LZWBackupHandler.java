import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LZWBackupHandler extends LZW {

    private static final int BUFFER_SIZE = 1024;
    private static final String SOURCE_DIR = "./dados";
    private static final String BACKUP_DIR = "./backup/";

    public void createBackup() throws Exception {
        String backupDate = getCurrentDate();
        File backupFolder = new File(BACKUP_DIR + backupDate);
        if (!backupFolder.exists() && !backupFolder.mkdirs()) {
            throw new IOException("Falha ao criar o diretório de backup.");
        }

        String backupFilePath = backupFolder.getAbsolutePath() + "/backup.lzw";

        try (FileOutputStream backupStream = new FileOutputStream(backupFilePath)) {
            File[] filesToBackup = new File(SOURCE_DIR).listFiles(File::isFile);

            if (filesToBackup == null || filesToBackup.length == 0) {
                System.out.println("Nenhum arquivo encontrado para backup.");
                return;
            }

            for (File file : filesToBackup) {
                System.out.println("Comprimindo arquivo: " + file.getName());
                try (FileInputStream fileStream = new FileInputStream(file)) {
                    byte[] compressedData = compressStream(fileStream);
                    writeFileToBackup(backupStream, file.getName(), compressedData);
                }
            }
            System.out.println("Backup concluído com sucesso!");
        }
    }

    public void restoreBackup(String backupFilePath) throws Exception {
        File backupFile = new File(backupFilePath);
        if (!backupFile.exists()) {
            throw new FileNotFoundException("Arquivo de backup não encontrado.");
        }

        try (FileInputStream backupStream = new FileInputStream(backupFile)) {
            while (backupStream.available() > 0) {
                String fileName = readFileName(backupStream);
                byte[] compressedData = readCompressedData(backupStream);
                byte[] decompressedData = decompressStream(new ByteArrayInputStream(compressedData));

                Path restoredFilePath = Paths.get(SOURCE_DIR, fileName);
                Files.createDirectories(restoredFilePath.getParent());
                Files.write(restoredFilePath, decompressedData);
                System.out.println("Arquivo restaurado: " + fileName);
            }
        }
    }

    private byte[] compressStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream compressedStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] compressedChunk = super.codifica(sliceArray(buffer, bytesRead));
            compressedStream.write(compressedChunk);
        }
        return compressedStream.toByteArray();
    }

    private byte[] decompressStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream decompressedStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
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

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}
