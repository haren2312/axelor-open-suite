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
package com.axelor.apps.hr.service.leave;

import com.axelor.apps.hr.db.Employee;
import com.axelor.apps.hr.db.LeaveLine;
import com.axelor.apps.hr.db.LeaveManagement;
import com.axelor.apps.hr.db.LeaveReason;
import com.axelor.apps.hr.db.LeaveRequest;
import com.axelor.apps.hr.db.repo.LeaveLineRepository;
import com.axelor.apps.hr.db.repo.LeaveRequestRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class LeaveLineServiceImpl implements LeaveLineService {
  protected LeaveLineRepository leaveLineRepository;
  protected LeaveRequestRepository leaveRequestRepository;

  @Inject
  public LeaveLineServiceImpl(
      LeaveLineRepository leaveLineRepository, LeaveRequestRepository leaveRequestRepository) {
    this.leaveLineRepository = leaveLineRepository;
    this.leaveRequestRepository = leaveRequestRepository;
  }

  @Override
  public LeaveLine getLeaveLine(LeaveRequest leaveRequest) {

    if (leaveRequest.getEmployee() == null) {
      return null;
    }

    return leaveLineRepository
        .all()
        .filter(
            "self.employee = ?1 AND self.leaveReason = ?2",
            leaveRequest.getEmployee(),
            leaveRequest.getLeaveReason())
        .fetchOne();
  }

  @Override
  @Transactional
  public LeaveLine addLeaveReasonOrCreateIt(Employee employee, LeaveReason leaveReason) {
    return getLeaveReasonToJustify(employee, leaveReason)
        .orElseGet(() -> createLeaveReasonToJustify(employee, leaveReason));
  }

  protected Optional<LeaveLine> getLeaveReasonToJustify(
      Employee employee, LeaveReason leaveReason) {
    if (employee.getLeaveLineList() != null) {
      return employee.getLeaveLineList().stream()
          .filter(leaveLine -> leaveReason.equals(leaveLine.getLeaveReason()))
          .findAny();
    }
    return Optional.empty();
  }

  protected LeaveLine createLeaveReasonToJustify(Employee employee, LeaveReason leaveReason) {
    LeaveLine leaveLineEmployee = new LeaveLine();
    leaveLineEmployee.setLeaveReason(leaveReason);
    leaveLineEmployee.setEmployee(employee);
    if (leaveReason != null) {
      leaveLineEmployee.setName(leaveReason.getName());
    }

    leaveLineRepository.save(leaveLineEmployee);
    return leaveLineEmployee;
  }

  @Override
  @Transactional
  public void updateDaysToValidate(LeaveLine leaveLine) {

    List<LeaveRequest> leaveRequests =
        leaveRequestRepository
            .all()
            .filter(
                "self.statusSelect = :statusSelect AND self.leaveReason = :leaveReason AND self.employee = :employee")
            .bind("statusSelect", LeaveRequestRepository.STATUS_AWAITING_VALIDATION)
            .bind("leaveReason", leaveLine.getLeaveReason())
            .bind("employee", leaveLine.getEmployee())
            .fetch();

    BigDecimal daysToValidate = BigDecimal.ZERO;

    for (LeaveRequest request : leaveRequests) {
      if (request.getInjectConsumeSelect() == LeaveRequestRepository.SELECT_CONSUME) {
        daysToValidate = daysToValidate.add(request.getDuration());
      } else {
        daysToValidate = daysToValidate.subtract(request.getDuration());
      }
    }

    leaveLine.setDaysToValidate(daysToValidate);
  }

  @Override
  public LeaveLine createNewLeaveLine(LeaveReason leaveReason, LeaveManagement leaveManagement) {
    LeaveLine newLeaveLine = new LeaveLine();
    newLeaveLine.addLeaveManagementListItem(leaveManagement);
    newLeaveLine.setLeaveReason(leaveReason);
    return newLeaveLine;
  }
}
