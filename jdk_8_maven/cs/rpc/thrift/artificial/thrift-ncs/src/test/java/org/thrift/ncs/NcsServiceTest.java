package org.thrift.ncs;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.thrift.ncs.service.Dto;
import org.thrift.ncs.service.NcsServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * created by manzhang on 2021/10/21
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = NcsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NcsServiceTest {

    @Autowired
    protected NcsServiceImpl service;


    @Test
    public void testTriangleByNativeService() throws Exception {
        Dto dto = service.checkTriangle(3, 4,5);
        assertEquals(1, dto.resultAsInt);
    }

}
