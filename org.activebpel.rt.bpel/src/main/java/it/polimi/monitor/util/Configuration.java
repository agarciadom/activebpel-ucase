/*
  Copyright 2007 Politecnico di Milano
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

package it.polimi.monitor.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Configuration implements IConfiguration {

    private static final ResourceBundle RESOURCE_BUNDLE
        = ResourceBundle.getBundle("config");

    public String getString(final String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException ex) {
            System.err.println("The key\"" + key + "\" is not in config file");
            return '!' + key + '!';
        }
    }
}
