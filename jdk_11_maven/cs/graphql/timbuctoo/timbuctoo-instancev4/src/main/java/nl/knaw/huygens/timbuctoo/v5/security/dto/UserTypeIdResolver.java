package nl.knaw.huygens.timbuctoo.v5.security.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;

public class UserTypeIdResolver extends TypeIdResolverBase {
  public static final String TYPENAME = "user";
  public static final JavaType JAVA_TYPE = TypeFactory.defaultInstance().uncheckedSimpleType(User.class);

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
