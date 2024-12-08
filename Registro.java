
public interface Registro {
    public void setId(int id);

    public int getId();

    public byte[] toByteArray();

    public void fromByteArray(byte[] array);
}
