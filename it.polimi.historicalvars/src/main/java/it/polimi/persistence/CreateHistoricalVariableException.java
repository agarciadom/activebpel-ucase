/* Copyright 2007, 2008 , DEEP SE group, Dipartimento di Elettronica e Informazione (DEI), Politecnico di Milano */

/*
 *  License:
 *
 *
 *  This file is part of  DYNAMO .
 *
 *
 *	DYNAMO is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DYNAMO is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DYNAMO.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


/**
 *
 */
package it.polimi.persistence;

import javax.xml.ws.WebFault;



/**
 * @author Luca Galluppi
 */
@WebFault(faultBean = "it.polimi.persistence.FaultBean")
public class CreateHistoricalVariableException extends Exception {
    private static final long serialVersionUID = 11111111111L;

    private FaultBean faultBean;

    public CreateHistoricalVariableException() {
        super();
    }

    public CreateHistoricalVariableException(String mes){
        super(mes);
    }

    public CreateHistoricalVariableException(String message, FaultBean faultBean, Throwable cause) {
        super(message, cause);
        this.faultBean = faultBean;
    }

    public CreateHistoricalVariableException(String message, FaultBean faultBean) {
        super(message);
        this.faultBean = faultBean;
    }

    public FaultBean getFaultInfo() {
        return faultBean;
    }
}
