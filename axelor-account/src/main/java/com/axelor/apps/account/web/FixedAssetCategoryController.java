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
package com.axelor.apps.account.web;

import com.axelor.apps.account.db.FixedAssetCategory;
import com.axelor.apps.account.db.repo.FixedAssetTypeRepository;
import com.axelor.apps.account.service.fixedasset.FixedAssetCategoryService;
import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.common.StringUtils;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class FixedAssetCategoryController {
  public void setDepreciationPlanSelectToNone(ActionRequest request, ActionResponse response) {
    try {
      FixedAssetCategory fixedAssetCategory = request.getContext().asType(FixedAssetCategory.class);
      FixedAssetCategoryService fixedAssetCategoryService =
          Beans.get(FixedAssetCategoryService.class);
      fixedAssetCategoryService.setDepreciationPlanSelectToNone(
          fixedAssetCategory,
          FixedAssetTypeRepository.FIXED_ASSET_CATEGORY_TECHNICAL_TYPE_SELECT_ONGOING_ASSET);

      if (StringUtils.notEmpty(fixedAssetCategory.getDepreciationPlanSelect())) {
        response.setValue("depreciationPlanSelect", fixedAssetCategory.getDepreciationPlanSelect());
      }
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }
}
