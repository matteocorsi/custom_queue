package diario.alimentare.coda.queue;

import org.json.*;

/**
 * Interfaccia di conversione di un oggetto java in JSon, per l'invio
 *
 * @param <T> tipo di oggetto da gstire
 * @author Matteo
 */
@Deprecated
public interface JSonAdapter<T> {
    /**
     * restituisce l'oggetto JSON da inviare
     *
     * @param obj oggetto da convertire
     * @return JSON
     * @throws Exception in caso di errori di conversione
     */
    public JSONObject convert(T obj) throws Exception;
}
