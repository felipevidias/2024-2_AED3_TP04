import java.io.*;
import java.nio.file.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class LZWBackupHandler extends LZW {

    private static final int tamanho = 1024;
    private static final String fonte = "./dados";
    private static final String backup = "./backup/";

    public void createBackup() throws Exception {
        String backupDate = getCurrentDate();
        File backupFolder = prepareBackupFolder(backupDate);
        String backupFilePath = backupFolder + "/backup.lzw";

        try (FileOutputStream backupFileStream = new FileOutputStream(backupFilePath)) {
            File[] filesToBackup = fetchFiles(fonte);

            for (File file : filesToBackup) {
                if (file.isFile()) {
                    processFileForBackup(file, backupFileStream);
                }
            }
            System.out.println("Backup feito com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(0));
    }

    private File prepareBackupFolder(String date) {
        File folder = new File(backup + date);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    private File[] fetchFiles(String directory) {
        File folder = new File(directory);
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("O caminho nao é um diretorio.");
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("Nenhum arquivo encontrado no diretorio.");
            return new File[0];
        }
        return files;
    }

    private void processFileForBackup(File file, FileOutputStream outputStream) throws Exception {
        System.out.println("Comprimindo arquivo: " + file.getName());
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] compressedData = compressStream(fileInputStream);
            writeToBackupFile(outputStream, file.getName(), compressedData);
        }
    }

    private byte[] compressStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream compressedStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[tamanho];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] compressedChunk = super.codifica(sliceArray(buffer, bytesRead));
            compressedStream.write(compressedChunk);
        }
        return compressedStream.toByteArray();
    }

    private void writeToBackupFile(FileOutputStream outputStream, String fileName, byte[] compressedData)
            throws IOException {
        byte[] fileNameBytes = fileName.getBytes("UTF-8");
        outputStream.write(convertIntToBytes(fileNameBytes.length));
        outputStream.write(fileNameBytes);
        outputStream.write(convertIntToBytes(compressedData.length));
        outputStream.write(compressedData);
    }

    public void restoreBackup(String backupFilePath) throws Exception {
        File backupFile = new File(backupFilePath);

        try (FileInputStream inputStream = new FileInputStream(backupFile)) {
            while (inputStream.available() > 0) {
                restoreSingleFile(inputStream);
            }
        }
    }

    private void restoreSingleFile(FileInputStream inputStream) throws Exception {
        String fileName = readFileName(inputStream);
        byte[] compressedData = readCompressedData(inputStream);
        byte[] decompressedData = decompressStream(new ByteArrayInputStream(compressedData));
        saveRestoredFile(fileName, decompressedData);
    }

    private String readFileName(FileInputStream inputStream) throws IOException {
        byte[] nameLengthBytes = new byte[4];
        inputStream.read(nameLengthBytes);
        int nameLength = convertBytesToInt(nameLengthBytes);

        byte[] nameBytes = new byte[nameLength];
        inputStream.read(nameBytes);
        return new String(nameBytes, "UTF-8");
    }

    private byte[] readCompressedData(FileInputStream inputStream) throws IOException {
        byte[] dataLengthBytes = new byte[4];
        inputStream.read(dataLengthBytes);
        int dataLength = convertBytesToInt(dataLengthBytes);

        byte[] compressedData = new byte[dataLength];
        inputStream.read(compressedData);
        return compressedData;
    }

    private void saveRestoredFile(String fileName, byte[] decompressedData) throws IOException {
        Path filePath = Paths.get(fonte + "/" + fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, decompressedData);
        System.out.println("Arquivo " + fileName + " restaurado com sucesso!");
    }

    private byte[] decompressStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream decompressedStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[tamanho];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] decompressedChunk = super.decodifica(sliceArray(buffer, bytesRead));
            decompressedStream.write(decompressedChunk);
        }
        return decompressedStream.toByteArray();
    }

    private byte[] sliceArray(byte[] array, int length) {
        byte[] slicedArray = new byte[length];
        System.arraycopy(array, 0, slicedArray, 0, length);
        return slicedArray;
    }

    private byte[] convertIntToBytes(int value) {
        return new byte[] {
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    private int convertBytesToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
    }
}
