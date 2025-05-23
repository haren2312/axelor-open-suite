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
package com.axelor.apps.hr.service.timesheet;

import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.hr.db.Employee;
import com.axelor.apps.hr.db.TSTimer;
import com.axelor.apps.hr.db.Timesheet;
import com.axelor.apps.hr.db.TimesheetLine;
import com.axelor.apps.project.db.Project;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public interface TimesheetLineService {

  /**
   * Compute duration from hours or to hours.
   *
   * @param timesheet a timesheet containing the time preference.
   * @param duration the duration to be converted.
   * @param toHours if we convert the duration to hours, or if we convert from hours.
   * @return the computed duration.
   * @throws AxelorException
   */
  BigDecimal computeHoursDuration(Timesheet timesheet, BigDecimal duration, boolean toHours)
      throws AxelorException;

  /**
   * Compute duration from hours or to hours.
   *
   * @param timePref a select containing the type of the duration. Can be minute, hours or days.
   * @param duration the duration to be converted
   * @param dailyWorkHrs The hours of work during a day.
   * @param toHours if we convert the duration to hours, or if we convert from hours.
   * @return the computed duration.
   * @throws AxelorException
   */
  BigDecimal computeHoursDuration(
      String timePref, BigDecimal duration, BigDecimal dailyWorkHrs, boolean toHours)
      throws AxelorException;

  TimesheetLine updateTimesheetLine(
      TimesheetLine timesheetLine,
      Project project,
      Product product,
      Employee employee,
      LocalDate date,
      Timesheet timesheet,
      BigDecimal hours,
      String comments);

  /**
   * Compute the total duration of timesheet lines. Add each duration only if the timesheet is
   * validated or confirmed.
   *
   * @param timesheetLineList a list of timesheet lines.
   * @return a {@link java.time.Duration}.
   */
  Duration computeTotalDuration(List<TimesheetLine> timesheetLineList);

  public void checkDailyLimit(
      Timesheet timesheet, TimesheetLine currentTimesheetLine, BigDecimal hoursDuration)
      throws AxelorException;

  Integer getDailyLimitFromApp();

  boolean isExceedingDailyLimit(
      BigDecimal totalHoursDuration, BigDecimal hoursDuration, int dailyLimit);

  Product getDefaultProduct(TimesheetLine timesheetLine);

  void resetTimesheetLineTimer(TSTimer tsTimer);
}
