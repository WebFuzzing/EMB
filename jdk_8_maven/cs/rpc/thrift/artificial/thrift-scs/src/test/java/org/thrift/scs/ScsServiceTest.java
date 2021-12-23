package org.thrift.scs;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thrift.scs.service.ScsServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * created by manzhang on 2021/10/23
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ScsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScsServiceTest {

    @Autowired
    protected ScsServiceImpl service;


    @Test
    public void testCalcByNativeService() throws Exception {
        String value = service.calc("plus", 1, 2);
        assertEquals("3.0", value);
    }

}
