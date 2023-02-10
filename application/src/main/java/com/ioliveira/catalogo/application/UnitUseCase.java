package com.ioliveira.catalogo.application;

public abstract class UnitUseCase<IN> {

    public abstract void execute(IN in);
}