namespace java org.thrift.scs


service ScsService {

    string calc(1:string op, 2:double arg1, 3:double arg2),

    string cookie(1:string name, 2:string val, 3:string site),

    string costfuns(1:i32 i, 2:string s),

    string dateParse(1:string dayname, 2:string monthname),

    string fileSuffix(1:string directory, 2:string file),

    string notyPevar(1:i32 i, 2:string s),

    string ordered4(1:string w, 2:string x, 3:string z, 4:string y),

    string pat(1:string txt, 2:string pat),

    string regex(1:string txt),

    string text2txt(1:string word1, 2:string word2, 3:string word3),

    string title(1:string sex, 2:string title)
}