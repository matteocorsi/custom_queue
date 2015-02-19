package diario.alimentare.coda.queue;

/**
 * classe adapter per la connessione al server WEB di invio dati
 *
 * @author Matteo
 */
public interface ServerSideAdapter<T> {
    /**
     * di occupa dell'invio dei dati. Se l'invio ha successo ritorna true
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public boolean send(T ob) throws Exception;
}
