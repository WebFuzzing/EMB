/**
 * Autogenerated by Thrift Compiler (0.15.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package org.thrift.ncs;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.15.0)", date = "2021-10-21")
public class Dto implements org.apache.thrift.TBase<Dto, Dto._Fields>, java.io.Serializable, Cloneable, Comparable<Dto> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Dto");

  private static final org.apache.thrift.protocol.TField RESULT_AS_INT_FIELD_DESC = new org.apache.thrift.protocol.TField("resultAsInt", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField RESULT_AS_DOUBLE_FIELD_DESC = new org.apache.thrift.protocol.TField("resultAsDouble", org.apache.thrift.protocol.TType.DOUBLE, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new DtoStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new DtoTupleSchemeFactory();

  public int resultAsInt; // required
  public double resultAsDouble; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    RESULT_AS_INT((short)1, "resultAsInt"),
    RESULT_AS_DOUBLE((short)2, "resultAsDouble");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // RESULT_AS_INT
          return RESULT_AS_INT;
        case 2: // RESULT_AS_DOUBLE
          return RESULT_AS_DOUBLE;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __RESULTASINT_ISSET_ID = 0;
  private static final int __RESULTASDOUBLE_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.RESULT_AS_INT, new org.apache.thrift.meta_data.FieldMetaData("resultAsInt", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.RESULT_AS_DOUBLE, new org.apache.thrift.meta_data.FieldMetaData("resultAsDouble", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Dto.class, metaDataMap);
  }

  public Dto() {
  }

  public Dto(
    int resultAsInt,
    double resultAsDouble)
  {
    this();
    this.resultAsInt = resultAsInt;
    setResultAsIntIsSet(true);
    this.resultAsDouble = resultAsDouble;
    setResultAsDoubleIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Dto(Dto other) {
    __isset_bitfield = other.__isset_bitfield;
    this.resultAsInt = other.resultAsInt;
    this.resultAsDouble = other.resultAsDouble;
  }

  public Dto deepCopy() {
    return new Dto(this);
  }

  @Override
  public void clear() {
    setResultAsIntIsSet(false);
    this.resultAsInt = 0;
    setResultAsDoubleIsSet(false);
    this.resultAsDouble = 0.0;
  }

  public int getResultAsInt() {
    return this.resultAsInt;
  }

  public Dto setResultAsInt(int resultAsInt) {
    this.resultAsInt = resultAsInt;
    setResultAsIntIsSet(true);
    return this;
  }

  public void unsetResultAsInt() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __RESULTASINT_ISSET_ID);
  }

  /** Returns true if field resultAsInt is set (has been assigned a value) and false otherwise */
  public boolean isSetResultAsInt() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __RESULTASINT_ISSET_ID);
  }

  public void setResultAsIntIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __RESULTASINT_ISSET_ID, value);
  }

  public double getResultAsDouble() {
    return this.resultAsDouble;
  }

  public Dto setResultAsDouble(double resultAsDouble) {
    this.resultAsDouble = resultAsDouble;
    setResultAsDoubleIsSet(true);
    return this;
  }

  public void unsetResultAsDouble() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __RESULTASDOUBLE_ISSET_ID);
  }

  /** Returns true if field resultAsDouble is set (has been assigned a value) and false otherwise */
  public boolean isSetResultAsDouble() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __RESULTASDOUBLE_ISSET_ID);
  }

  public void setResultAsDoubleIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __RESULTASDOUBLE_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case RESULT_AS_INT:
      if (value == null) {
        unsetResultAsInt();
      } else {
        setResultAsInt((java.lang.Integer)value);
      }
      break;

    case RESULT_AS_DOUBLE:
      if (value == null) {
        unsetResultAsDouble();
      } else {
        setResultAsDouble((java.lang.Double)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case RESULT_AS_INT:
      return getResultAsInt();

    case RESULT_AS_DOUBLE:
      return getResultAsDouble();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case RESULT_AS_INT:
      return isSetResultAsInt();
    case RESULT_AS_DOUBLE:
      return isSetResultAsDouble();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that instanceof Dto)
      return this.equals((Dto)that);
    return false;
  }

  public boolean equals(Dto that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_resultAsInt = true;
    boolean that_present_resultAsInt = true;
    if (this_present_resultAsInt || that_present_resultAsInt) {
      if (!(this_present_resultAsInt && that_present_resultAsInt))
        return false;
      if (this.resultAsInt != that.resultAsInt)
        return false;
    }

    boolean this_present_resultAsDouble = true;
    boolean that_present_resultAsDouble = true;
    if (this_present_resultAsDouble || that_present_resultAsDouble) {
      if (!(this_present_resultAsDouble && that_present_resultAsDouble))
        return false;
      if (this.resultAsDouble != that.resultAsDouble)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + resultAsInt;

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(resultAsDouble);

    return hashCode;
  }

  @Override
  public int compareTo(Dto other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.compare(isSetResultAsInt(), other.isSetResultAsInt());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetResultAsInt()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.resultAsInt, other.resultAsInt);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.compare(isSetResultAsDouble(), other.isSetResultAsDouble());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetResultAsDouble()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.resultAsDouble, other.resultAsDouble);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("Dto(");
    boolean first = true;

    sb.append("resultAsInt:");
    sb.append(this.resultAsInt);
    first = false;
    if (!first) sb.append(", ");
    sb.append("resultAsDouble:");
    sb.append(this.resultAsDouble);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class DtoStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public DtoStandardScheme getScheme() {
      return new DtoStandardScheme();
    }
  }

  private static class DtoStandardScheme extends org.apache.thrift.scheme.StandardScheme<Dto> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Dto struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // RESULT_AS_INT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.resultAsInt = iprot.readI32();
              struct.setResultAsIntIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // RESULT_AS_DOUBLE
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.resultAsDouble = iprot.readDouble();
              struct.setResultAsDoubleIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Dto struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(RESULT_AS_INT_FIELD_DESC);
      oprot.writeI32(struct.resultAsInt);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(RESULT_AS_DOUBLE_FIELD_DESC);
      oprot.writeDouble(struct.resultAsDouble);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class DtoTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public DtoTupleScheme getScheme() {
      return new DtoTupleScheme();
    }
  }

  private static class DtoTupleScheme extends org.apache.thrift.scheme.TupleScheme<Dto> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Dto struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetResultAsInt()) {
        optionals.set(0);
      }
      if (struct.isSetResultAsDouble()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetResultAsInt()) {
        oprot.writeI32(struct.resultAsInt);
      }
      if (struct.isSetResultAsDouble()) {
        oprot.writeDouble(struct.resultAsDouble);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Dto struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.resultAsInt = iprot.readI32();
        struct.setResultAsIntIsSet(true);
      }
      if (incoming.get(1)) {
        struct.resultAsDouble = iprot.readDouble();
        struct.setResultAsDoubleIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

