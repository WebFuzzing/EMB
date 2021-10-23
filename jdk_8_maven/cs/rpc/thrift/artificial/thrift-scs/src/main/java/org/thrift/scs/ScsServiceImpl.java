package org.thrift.scs;

import org.apache.thrift.TException;
import org.springframework.stereotype.Service;
import org.thrift.scs.imp.*;

/**
 * created by manzhang on 2021/10/23
 *
 */
@Service
public class ScsServiceImpl implements ScsService.Iface{

    @Override
    public String calc(String op, double arg1, double arg2) throws TException {
        return Calc.subject(op, arg1, arg2);
    }

    @Override
    public String cookie(String name, String val, String site) throws TException {
        return Cookie.subject(name, val, site);
    }

    @Override
    public String costfuns(int i, String s) throws TException {
        return Costfuns.subject(i, s);
    }

    @Override
    public String dateParse(String dayname, String monthname) throws TException {
        return DateParse.subject(dayname,monthname);
    }

    @Override
    public String fileSuffix(String directory, String file) throws TException {
        return FileSuffix.subject(directory, file);
    }

    @Override
    public String notyPevar(int i, String s) throws TException {
        return NotyPevar.subject(i, s);
    }

    @Override
    public String ordered4(String w, String x, String z, String y) throws TException {
        return Ordered4.subject(w, x, z, y);
    }

    @Override
    public String pat(String txt, String pat) throws TException {
        return Pat.subject(txt, pat);
    }

    @Override
    public String regex(String txt) throws TException {
        return Regex.subject(txt);
    }

    @Override
    public String text2txt(String word1, String word2, String word3) throws TException {
        return Text2Txt.subject(word1, word2, word3);
    }

    @Override
    public String title(String sex, String title) throws TException {
        return Title.subject(sex, title);
    }
}
