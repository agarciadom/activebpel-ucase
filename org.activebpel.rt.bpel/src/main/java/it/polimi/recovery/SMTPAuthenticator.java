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

package it.polimi.recovery;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
* SimpleAuthenticator is used to do simple authentication
* when the SMTP server requires it.
*/
public class SMTPAuthenticator extends Authenticator
{
	private String username;
	private String password;
	
    public SMTPAuthenticator(String username, String password)
	{
		this.username = username;
		this.password = password;
	}

	public PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(this.username, this.password);
    }
}
