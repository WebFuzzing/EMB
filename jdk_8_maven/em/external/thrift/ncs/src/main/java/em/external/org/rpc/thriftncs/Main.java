package em.external.org.rpc.thriftncs;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.thrift.ncs.client.NcsService;

public class Main {

    public static void main(String[] args) {
        // init client
        TTransport transport = null;
        THttpClient tHttpClient = null;
        try {
//            transport = new TSocket("localhost", 9898);
//            transport.open();
            tHttpClient = new THttpClient("http://localhost:8080/ncs");
            tHttpClient.open();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
        TProtocol protocol = new TBinaryProtocol(tHttpClient);
        NcsService.Client client = new NcsService.Client(protocol);
        try {
            Object res = client.checkTriangle(3,4,5);
            System.out.println(res);
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
