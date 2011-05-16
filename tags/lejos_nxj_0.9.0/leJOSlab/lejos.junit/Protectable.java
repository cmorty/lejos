
/**
 * A <em>Protectable</em> can be run and can throw a Throwable.
 *
 * @see TestResult
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public interface Protectable {

    /**
     * Run the the following method protected.
     * 
     * @throws Throwable can be raised in some error case
     */
    void protect() throws Throwable;
}