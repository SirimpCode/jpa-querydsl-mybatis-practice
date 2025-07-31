package com.github.jpaquerydslmybatis.common.converter.custom;


import com.github.jpaquerydslmybatis.common.myenum.MyEnumInterface;
import com.github.jpaquerydslmybatis.common.myenum.RoleEnum;
import jakarta.persistence.AttributeConverter;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;

import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//이것을 상속한 구현 클래스들에게 @Component 어노테이션을 붙여서 스프링 빈으로 등록해야 converter 가 동작 한다.
@jakarta.persistence.Converter// converter 가 이미 임포트 되어있기에 다른임포트문은 직접입력함
public abstract class MyConverter<T extends Enum<T> & MyEnumInterface> implements AttributeConverter<T, String>, Converter<String, T> {

    private final Map<String, T> valueToEnumMap;
    public MyConverter(Class<T> targetEnumClass) {

        this.valueToEnumMap = EnumSet.allOf(targetEnumClass).stream()
                .flatMap(myEnum -> Stream.of(
                        Map.entry(myEnum.getValue(), myEnum),
                        Map.entry(
                                targetEnumClass.equals(RoleEnum.class) && myEnum.name().startsWith("ROLE_")
                                        ? myEnum.name().substring(5)
                                        : myEnum.name(),
                                myEnum
                        )//두개의 맵을 평탄하게 맵으로 합침 하나는 getValue()로, 하나는 enum 상수값을 키값으로
                ))
                .collect(Collectors.toUnmodifiableMap(
                                Map.Entry::getKey, Map.Entry::getValue
                        )
                );
    }

    @Override//request parameter로 들어오는 경우 사용됨
    public T convert(@NonNull String source) {
        T result = valueToEnumMap.get(source.toUpperCase());
        if(result!=null) return result;

        throw new IllegalArgumentException("No enum constant for value: " + source);
    }

    @Override//null 인 경우 jpa 가 호출 안함
    public String convertToDatabaseColumn(T myEnum) {
        return myEnum.getValue();
    }

    @Override
    public T convertToEntityAttribute(String myEnumName) {
        return myEnumName==null ? null : valueToEnumMap.get(myEnumName);
    }


}

