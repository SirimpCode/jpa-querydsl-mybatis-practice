package com.github.jpaquerydslmybatis.web.controller.employees;

import com.github.jpaquerydslmybatis.common.myenum.Gender;
import com.github.jpaquerydslmybatis.service.employees.EmpService;
import com.github.jpaquerydslmybatis.service.storage.ExcelService;
import com.github.jpaquerydslmybatis.web.dto.employees.EmpListResponse;
import com.github.jpaquerydslmybatis.web.dto.employees.EmpListResponseWrap;
import com.github.jpaquerydslmybatis.web.dto.employees.chart.ChartResponse;
import com.github.jpaquerydslmybatis.web.dto.employees.chart.EmpSearchCondition;
import com.github.jpaquerydslmybatis.web.dto.employees.chart.PieChartResponse;
import com.github.jpaquerydslmybatis.web.dto.response.CustomResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/emp")
@RequiredArgsConstructor
public class EmpRestController {
    private final EmpService empService;
    private final ExcelService excelService;
    @GetMapping("/list")
    public CustomResponse<EmpListResponseWrap> empList(
                                                   @RequestParam(required = false) List<Long> departmentIds,
                                                   @RequestParam(required = false) Gender gender
    ) {
        EmpListResponseWrap employees = empService.getAllEmployees(departmentIds,gender);
        return CustomResponse.ofOk("사원목록조회성공", employees);
    }
    @PostMapping("/excel-download")
    public ResponseEntity<Resource> excelDownload(
            HttpServletResponse response,
            @RequestParam(required = false) List<Long> departmentIds,
            @RequestParam(required = false) Gender gender){
        EmpListResponseWrap employees = empService.getAllEmployees(departmentIds,gender);
        // 엑셀 다운로드 로직을 여기에 추가
        // === 조회결과물인 empList 를 가지고 엑셀 시트 생성하기 ===
        // 시트를 생성하고, 행을 생성하고, 셀을 생성하고, 셀안에 내용을 넣어주면 된다.
        List<EmpListResponse> empList = employees.getEmpList();


        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Employees");
            String fileName = "Employees";
            int rowIndex = 0;

            //최상단 스타일
            CellStyle mergeRowStyle = workbook.createCellStyle();
            mergeRowStyle.setAlignment(HorizontalAlignment.CENTER);//가로기준정렬
            mergeRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);//세로기준정렬
            // CellStyle 배경색(ForegroundColor)만들기
            // setFillForegroundColor 메소드에 IndexedColors Enum인자를 사용한다.
            // setFillPattern은 해당 색을 어떤 패턴으로 입힐지를 정한다.
            mergeRowStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            mergeRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//채우기패턴
            //헤더 스타일
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);//가로기준정렬
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);//세로기준정렬
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);//채우기패턴
            headerStyle.setBorderTop(BorderStyle.THICK);
            headerStyle.setBorderBottom(BorderStyle.THICK);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // CellStyle 천단위 쉼표, 금액
            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            // Cell 폰트(Font) 설정하기
            // 폰트 적용을 위해 POI 라이브러리의 Font 객체를 생성해준다.
            // 해당 객체의 세터를 사용해 폰트를 설정해준다. 대표적으로 글씨체, 크기, 색상, 굵기만 설정한다.
            // 이후 CellStyle의 setFont 메소드를 사용해 인자로 폰트를 넣어준다.
            Font topFont = workbook.createFont();
            topFont.setFontName("나눔고딕");
            topFont.setFontHeightInPoints((short) 30);
            topFont.setColor(IndexedColors.WHITE.getIndex());
            topFont.setBold(true);
            mergeRowStyle.setFont(topFont);

            //헤더폰트
            Font headerFont = workbook.createFont();
            headerFont.setFontName("나눔고딕");
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setColor(IndexedColors.BLACK.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            //최상단 셀병합하기
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7)); // 첫번째 행, 첫번째 열부터 마지막 열까지 병합
            Row mergeRow = sheet.createRow(rowIndex++);
            Cell mergeCell = mergeRow.createCell(0);
            mergeCell.setCellValue("우리 회사 사원 정보");
            mergeCell.setCellStyle(mergeRowStyle); // 최상단 스타일 적용


            // 헤더 생성
            Row headerRow = sheet.createRow(rowIndex++);

            // 헤더 셀 스타일 적용
            List<String> cellValues = List.of("부서 번호", "부서명", "사원 번호", "사원명", "입사일자", "월급", "성별", "나이");
            for(int i = 0; i < cellValues.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(cellValues.get(i));
                cell.setCellStyle(headerStyle); // 헤더 스타일 적용

            }

            // 데이터 행 생성
            for( EmpListResponse emp : empList) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(emp.getDepartmentId() != null ? emp.getDepartmentId() : 0L);
                row.createCell(1).setCellValue(emp.getDepartmentName() != null ? emp.getDepartmentName() : "없음");
                row.createCell(2).setCellValue(emp.getEmployeeId());
                row.createCell(3).setCellValue(emp.getFirstName() + " " + emp.getLastName());
                row.createCell(4).setCellValue(emp.getRefinedHireDate());
                Cell salCell = row.createCell(5);
                salCell.setCellValue(emp.getSalaryNumberType());
                salCell.setCellStyle(moneyStyle);
                row.createCell(6).setCellValue(emp.getGender());
                row.createCell(7).setCellValue(emp.getAge());
            }
            // 시트 너비 자동 조정
            for (int i = 0; i < cellValues.size(); i++) {
                //이것보다 살짝 크게
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, (int) (sheet.getColumnWidth(i) * 1.5));
            }

            //액셀로 저장하기
            workbook.write(out);
            ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

            // 파일 이름 인코딩 처리
            String encodedFileName = URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+encodedFileName)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @PostMapping("/excel-upload")
    public CustomResponse<Long> excelUpload(
            @RequestParam("file") MultipartFile file
    ){
//        Excel 파일을 업로드 하면 엑셀데이터를 데이터베이스 테이블에 insert
        long insertResult = excelService.importExcelToDb(file);
        return CustomResponse.ofOk("엑셀 업로드 성공", insertResult);
    }
    @GetMapping("/chart-pie")
    public CustomResponse<ChartResponse<PieChartResponse>> getPieChartData(
            @RequestParam String condition
    ) {
        EmpSearchCondition empSearchCondition = EmpSearchCondition.from(condition);
        ChartResponse<PieChartResponse> chartResponse = empService.getPieChartData(empSearchCondition);

        return CustomResponse.ofOk("차트 데이터 조회 성공", chartResponse);
    }

}
