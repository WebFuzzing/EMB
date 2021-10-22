
namespace java org.thrift.ncs

struct Dto {
    1: i32 resultAsInt,
    2: double resultAsDouble
}

service NcsService {
    /**
     * Check the triangle type of the given three edges
     */
    Dto checkTriangle(1:i32 a, 2:i32 b, 3:i32 c),


    /**
     * bessj
     */
    Dto bessj(1:i32 n, 2:double x),


    /**
     * expint
     */
    Dto expint(1:i32 n, 2:double x),


    /**
     *
     */
    Dto fisher(1:i32 m, 2:i32 n, 3:double x),


    /**
     * gammq
     */
    Dto gammq(1:double a, 2:double x),


    /**
     * remainder
     */
    Dto remainder(1:i32 a, 2:i32 b)
}