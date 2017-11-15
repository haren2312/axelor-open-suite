/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2017 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.stock.exception;

import com.axelor.apps.stock.db.LogisticalFormLine;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.IException;
import com.axelor.i18n.I18n;

public class InvalidLogisticalFormLineDimensions extends AxelorException {

	private static final long serialVersionUID = 354779411257144849L;

	public InvalidLogisticalFormLineDimensions(LogisticalFormLine logisticalFormLine) {
		super(logisticalFormLine, IException.CONFIGURATION_ERROR,
				I18n.get(IExceptionMessage.LOGISTICAL_FORM_LINE_INVALID_DIMENSIONS), logisticalFormLine.getSequence() + 1);
	}

}
