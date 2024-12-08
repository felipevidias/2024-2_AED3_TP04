
import java.lang.reflect.Constructor;

public class ArquivoTarefa extends Arquivo<Tarefa> {

    public ArquivoTarefa(Constructor<Tarefa> construtor, String name) throws Exception {
        super(construtor, name);
    }
}
