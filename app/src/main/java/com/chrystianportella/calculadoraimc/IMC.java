package com.chrystianportella.calculadoraimc;

import java.util.Calendar;

public class IMC {
    public double Altura;
    public double Peso;
    public Calendar Data;
    public double IMC = (Peso / (Altura * Altura));
}
