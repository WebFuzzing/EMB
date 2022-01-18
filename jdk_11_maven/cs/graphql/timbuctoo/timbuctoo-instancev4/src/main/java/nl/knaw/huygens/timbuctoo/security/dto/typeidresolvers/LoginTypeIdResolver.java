package nl.knaw.huygens.timbuctoo.security.dto.typeidresolvers;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import nl.knaw.huygens.timbuctoo.security.dto.Login;

public class LoginTypeIdResolver  extends TypeIdResolverBase {


  public static final String TYPENAME = "login";
  public static final JavaType JAVA_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(Login.class);

  @Override
  public void init(JavaType baseType) {
  }

  @Override
  public String idFromValue(Object value) {
    return TYPENAME;
  }

  @Override
  public String idFromValueAndType(Object value, Class<?> suggestedType) {
    return TYPENAME;
  }

  @Override
  public String idFromBaseType() {
    return TYPENAME;
  }

  @Override
  public JavaType typeFromId(DatabindContext context, String id) {
    return JAVA_TYPE;
  }

  @Override
  public JsonTypeInfo.Id getMechanism() {
    return JsonTypeInfo.Id.NAME;
  }
}
