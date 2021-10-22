package org.thrift.ncs;

import org.apache.thrift.TException;
import org.springframework.stereotype.Service;
import org.thrift.ncs.imp.*;

/**
 * use code from jdk_8_maven/cs/rest/artificial/ncs/src/main/java/org/restncs/NcsRest.java
 */
@Service
public class NcsServiceImpl implements NcsService.Iface{
    @Override
    public Dto checkTriangle(int a, int b, int c) throws TException {
        Dto dto = new Dto();
        dto.resultAsInt = TriangleClassification.classify(a,b,c);
        return dto;
    }

    @Override
    public Dto bessj(int n, double x) throws TException {
        if(n <= 2 || n > 1000){
            return null;
        }

        Dto dto = new Dto();
        Bessj bessj = new Bessj();
        dto.resultAsDouble = bessj.bessj(n, x);

        return dto;
    }

    @Override
    public Dto expint(int n, double x) throws TException {

        try{
            Dto dto = new Dto();
            dto.resultAsDouble = Expint.exe(n,x);
            return dto;
        }catch (RuntimeException e){
            return null;
        }
    }

    @Override
    public Dto fisher(int m, int n, double x) throws TException {
        if(m > 1000 || n > 1000){
            return null;
        }

        try{
            Dto dto = new Dto();
            dto.resultAsDouble = Fisher.exe(m, n, x);
            return dto;
        }catch (RuntimeException e){
            return null;
        }
    }

    @Override
    public Dto gammq(double a, double x) throws TException {
        try{
            Dto dto = new Dto();
            Gammq gammq = new Gammq();
            dto.resultAsDouble = gammq.exe(a, x);
            return dto;
        }catch (RuntimeException e){
            return null;
        }
    }

    @Override
    public Dto remainder(int a, int b) throws TException {
        int lim = 10_000;
        if(a > lim || a < -lim || b > lim || b < -lim){
            return null;
        }

        Dto dto = new Dto();
        dto.resultAsInt = Remainder.exe(a,b);

        return dto;
    }
}
