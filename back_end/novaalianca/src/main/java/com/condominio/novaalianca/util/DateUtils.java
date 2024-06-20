package com.condominio.novaalianca.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Component
public class DateUtils {

    public String mesAtual(){
        return Integer.toString(LocalDate.now().getMonth().minus(0L).getValue());
    }

    public String primeiroDiaMes(){
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toString();
    }

    public String ultimoDiaMes(){
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toString();
    }
}
