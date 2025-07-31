package com.github.jpaquerydslmybatis.common.exception;

public class CustomServerException  extends MakeRuntimeException{

    protected CustomServerException(MakeRuntimeException.ExceptionBuilder<?, ?> exceptionBuilder) {
        super(exceptionBuilder);
    }
    public static of of() {
        return new of();
    }

    public static class of extends MakeRuntimeException.ExceptionBuilder<of, CustomServerException>{
        public of() {
            super(CustomServerException.class);
        }
        @Override
        protected of self() {
            return this;
        }
    }

}