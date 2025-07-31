package com.github.jpaquerydslmybatis.service.storage;

import com.github.jpaquerydslmybatis.common.exception.CustomBadRequestException;
import com.github.jpaquerydslmybatis.common.exception.CustomBindException;
import com.github.jpaquerydslmybatis.repository.db2.jpa.employees.EmployeesRepository;
import com.github.jpaquerydslmybatis.web.dto.employees.EmpExcelRequest;
import com.github.jpaquerydslmybatis.web.dto.employees.EmpListResponse;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ExcelService {
    private final EmployeesRepository employeesRepository;

    @Transactional("db2TransactionManager")
    public long importExcelToDb(MultipartFile file) {
        //액셀파일을 읽어서 Entity로 변환하고 DB에 저장하는 로직을 구현합니다.
        //예시로, Apache POI 라이브러리를 사용하여 엑셀 파일을 읽을 수 있습니다.
        //Apache POI를 사용한 엑셀 파일 읽기 예시
        long result = 0;
        try(Workbook workbook = WorkbookFactory.create(file.getInputStream())){
            Sheet sheet = workbook.getSheetAt(0);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                // 각 행을 읽고 필요한 데이터를 추출하여 Entity로 변환
                if(sheet.getRow(rowIndex).getCell(0) == null)
                    continue;
                EmpExcelRequest request = createEmpExcelRequest(sheet, rowIndex);

                result += employeesRepository.insertEmployeeByExcel(request);
            }
        }catch (IOException e) {
            // 예외 처리 로직
            throw CustomBindException.of().customMessage("엑셀 파일을 읽는 중 오류가 발생했습니다.").build();
        }catch (PersistenceException sqlException) {
            throw CustomBadRequestException.of().customMessage("엑셀 파일에 잘못된 데이터가 있습니다.")
                    .systemMessage(sqlException.getMessage()).build();
        }catch (DataIntegrityViolationException sqlException) {
            throw CustomBadRequestException.of().customMessage("사원번호 중복으로 인한 오류가 발생했습니다.")
                    .systemMessage(sqlException.getMessage()).build();
        }
        return result;
    }

    private EmpExcelRequest createEmpExcelRequest(Sheet sheet, int rowIndex) {
        long employeeId = (long) sheet.getRow(rowIndex).getCell(0).getNumericCellValue();
        String firstName = sheet.getRow(rowIndex).getCell(1).getStringCellValue();
        String lastName = sheet.getRow(rowIndex).getCell(2).getStringCellValue();
        String email = sheet.getRow(rowIndex).getCell(3).getStringCellValue();
        String phoneNumber = sheet.getRow(rowIndex).getCell(4).getStringCellValue();
        String hireDateStr = sheet.getRow(rowIndex).getCell(5).getStringCellValue();
        LocalDate hireDate = LocalDate.parse(hireDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Cell jobIdCell = sheet.getRow(rowIndex).getCell(6);
        String jobId = jobIdCell != null && jobIdCell.getCellType() == CellType.STRING ?
                sheet.getRow(rowIndex).getCell(6).getStringCellValue() : null;
        Double salary = sheet.getRow(rowIndex).getCell(7).getNumericCellValue();

        Cell commissionPctCell = sheet.getRow(rowIndex).getCell(8);
        Double commissionPct = commissionPctCell != null && commissionPctCell.getCellType() == CellType.NUMERIC ?
                sheet.getRow(rowIndex).getCell(8).getNumericCellValue() : null;

        Cell managerIdCell = sheet.getRow(rowIndex).getCell(9);
        Long managerId = managerIdCell != null && managerIdCell.getCellType() == CellType.NUMERIC ?
                (long) sheet.getRow(rowIndex).getCell(9).getNumericCellValue() : null;

        Cell departmentIdCell = sheet.getRow(rowIndex).getCell(10);
        Long departmentId = departmentIdCell != null && departmentIdCell.getCellType() == CellType.NUMERIC ?
                (long) sheet.getRow(rowIndex).getCell(10).getNumericCellValue() : null;
        String rrn = sheet.getRow(rowIndex).getCell(11).getStringCellValue();
        return EmpExcelRequest.builder()
                .hireDate(hireDate)
                .employeeId(employeeId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phoneNumber(phoneNumber)
                .jobId(jobId)
                .salary(salary)
                .commissionPct(commissionPct)
                .managerId(managerId)
                .departmentId(departmentId)
                .rrn(rrn)
                .build();

    }
}
