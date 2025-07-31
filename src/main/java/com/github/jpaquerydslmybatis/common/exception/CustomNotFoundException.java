package com.github.jpaquerydslmybatis.common.exception;

import lombok.Getter;

@Getter
public class CustomNotFoundException extends MakeRuntimeException{


    protected CustomNotFoundException(MakeRuntimeException.ExceptionBuilder<?, ?> exceptionBuilder) {
        super(exceptionBuilder);
    }
    public static of of() {
        return new of();
    }

    public static class of extends MakeRuntimeException.ExceptionBuilder<of, CustomNotFoundException> {

        public of() {
            super(CustomNotFoundException.class);
        }

        @Override
        protected of self() {
            return this;
        }
    }

}
