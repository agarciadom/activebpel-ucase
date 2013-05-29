/*
 Copyright 2013 Antonio García-Domínguez (reverse engineered from 2008 bytecode)
 This file is part of Dynamo.

 Dynamo is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 3 of the License, or
 (at your option) any later version.

 Dynamo is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.polimi.monitor.nodes;

import it.polimi.exception.WSCoLException;
import it.polimi.monitor.InputMonitor;

import java.util.logging.Logger;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

public class RespTimeNode extends NodeWSCoL {

	private static final Logger LOGGER = Logger.getLogger(RespTimeNode.class.getCanonicalName());
	private static final long serialVersionUID = -6165720165029637590L;

	private String value;

	@Override
	public void evaluate(InputMonitor inputMonitor, Aliases aliases,
			AliasNodes aliasNodes) throws WSCoLException {
		final XmlCursor data = parseXml(inputMonitor.getData());
		final XmlObject[] xmlRT = data.getObject().execQuery("$this/monitor_data/resp_time");
		if (xmlRT.length != 1) {
			throw new WSCoLException("Expected exactly one resp_time element, found " + xmlRT.length);
		}

		final XmlCursor rtCursor = xmlRT[0].newCursor();
		value = rtCursor.getTextValue();
		LOGGER.fine("Response time is " + value);
	}

	@Override
	public Long getMonitoringValue() throws WSCoLException {
		return Long.parseLong(value);
	}

	@Override
	public String toString() {
		return "Resp_Time";
	}

	private static XmlCursor parseXml(String sData) throws WSCoLException {
		try {
			return XmlObject.Factory.parse(sData).newCursor();
		} catch (XmlException e) {
			throw new WSCoLException(e.getLocalizedMessage());
		}
	}

}
