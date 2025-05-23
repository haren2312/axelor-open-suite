/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2025 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.apps.production.service;

import com.axelor.apps.purchase.db.PurchaseOrder;
import com.axelor.apps.purchase.db.repo.PurchaseOrderRepository;
import com.axelor.apps.purchase.service.PurchaseOrderTypeSelectServiceImpl;
import java.util.Objects;

public class PurchaseOrderTypeSelectProductionServiceImpl
    extends PurchaseOrderTypeSelectServiceImpl {

  @Override
  public void setTypeSelect(PurchaseOrder purchaseOrder) {
    Objects.requireNonNull(purchaseOrder);

    if ((purchaseOrder.getSupplierPartner() != null
            && purchaseOrder.getSupplierPartner().getIsSubcontractor())
        || purchaseOrder.getTypeSelect().equals(PurchaseOrderRepository.TYPE_SUBCONTRACTING)) {
      purchaseOrder.setTypeSelect(PurchaseOrderRepository.TYPE_SUBCONTRACTING);
    } else {
      super.setTypeSelect(purchaseOrder);
    }
  }
}
