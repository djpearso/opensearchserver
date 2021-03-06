/**   
 * License Agreement for OpenSearchServer
 *
 * Copyright (C) 2014-2015 Emmanuel Keller / Jaeksoft
 * 
 * http://www.open-search-server.com
 * 
 * This file is part of OpenSearchServer.
 *
 * OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenSearchServer. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/
package com.jaeksoft.searchlib.web.servlet.ui;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractUIServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9091818818966940410L;

	protected abstract void service(UITransaction transaction) throws Exception;

	private final boolean requireLogging;

	protected AbstractUIServlet() {
		requireLogging = true;
	}

	protected AbstractUIServlet(boolean requireLogging) {
		this.requireLogging = requireLogging;
	}

	@Override
	final public void service(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		try {
			UITransaction transaction = new UITransaction(request, response);
			if (requireLogging) {
				if (!transaction.session.isLogged()) {
					transaction.redirectContext(LoginServlet.PATH);
					return;
				}
			}
			service(transaction);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
