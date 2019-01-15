package org.apache.fineract.exceptions;

import org.apache.fineract.R;

import java.io.IOException;

/**
 * @author Rajan Maurya
 *         On 23/09/17.
 */
public class NoConnectivityException extends IOException {

    @Override
    public String getMessage() {
        return String.valueOf(R.string.No_connectivity_exception);
    }
}
