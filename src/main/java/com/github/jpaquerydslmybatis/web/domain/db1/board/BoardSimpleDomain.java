package com.github.jpaquerydslmybatis.web.domain.db1.board;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@Alias("BoardSimpleDomain")// 없어도 typeAliasesPackage 설정 해놨기 때문에
// Mybatis가 자동으로 BoardSimpleDomain 클래스명이 별칭으로 등록된다.
// ide 에서 자동완성 기능을 사용할 수 있도록 별칭을 지정해준다.
public class BoardSimpleDomain {
    private Long boardId;
    private String title;
    private String content;
}
