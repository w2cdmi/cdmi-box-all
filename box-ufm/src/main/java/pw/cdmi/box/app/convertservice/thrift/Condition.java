/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package pw.cdmi.box.app.convertservice.thrift;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Condition implements org.apache.thrift.TBase<Condition, Condition._Fields>, java.io.Serializable, Cloneable, Comparable<Condition> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Condition");

  private static final org.apache.thrift.protocol.TField OWNE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("owneId", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField OBJECT_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("objectId", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField RESOURCE_GROUP_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("resourceGroupId", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField TASK_SIZE_FIELD_DESC = new org.apache.thrift.protocol.TField("taskSize", org.apache.thrift.protocol.TType.I32, (short)4);
  private static final org.apache.thrift.protocol.TField CS_IP_FIELD_DESC = new org.apache.thrift.protocol.TField("csIp", org.apache.thrift.protocol.TType.STRING, (short)5);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new ConditionStandardSchemeFactory());
    schemes.put(TupleScheme.class, new ConditionTupleSchemeFactory());
  }

  public String owneId; // required
  public List<String> objectId; // required
  public String resourceGroupId; // required
  public int taskSize; // required
  public String csIp; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    OWNE_ID((short)1, "owneId"),
    OBJECT_ID((short)2, "objectId"),
    RESOURCE_GROUP_ID((short)3, "resourceGroupId"),
    TASK_SIZE((short)4, "taskSize"),
    CS_IP((short)5, "csIp");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // OWNE_ID
          return OWNE_ID;
        case 2: // OBJECT_ID
          return OBJECT_ID;
        case 3: // RESOURCE_GROUP_ID
          return RESOURCE_GROUP_ID;
        case 4: // TASK_SIZE
          return TASK_SIZE;
        case 5: // CS_IP
          return CS_IP;
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
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __TASKSIZE_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.OWNE_ID, new org.apache.thrift.meta_data.FieldMetaData("owneId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.OBJECT_ID, new org.apache.thrift.meta_data.FieldMetaData("objectId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    tmpMap.put(_Fields.RESOURCE_GROUP_ID, new org.apache.thrift.meta_data.FieldMetaData("resourceGroupId", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TASK_SIZE, new org.apache.thrift.meta_data.FieldMetaData("taskSize", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.CS_IP, new org.apache.thrift.meta_data.FieldMetaData("csIp", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Condition.class, metaDataMap);
  }

  public Condition() {
  }

  public Condition(
    String owneId,
    List<String> objectId,
    String resourceGroupId,
    int taskSize,
    String csIp)
  {
    this();
    this.owneId = owneId;
    this.objectId = objectId;
    this.resourceGroupId = resourceGroupId;
    this.taskSize = taskSize;
    setTaskSizeIsSet(true);
    this.csIp = csIp;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Condition(Condition other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetOwneId()) {
      this.owneId = other.owneId;
    }
    if (other.isSetObjectId()) {
      List<String> __this__objectId = new ArrayList<String>(other.objectId);
      this.objectId = __this__objectId;
    }
    if (other.isSetResourceGroupId()) {
      this.resourceGroupId = other.resourceGroupId;
    }
    this.taskSize = other.taskSize;
    if (other.isSetCsIp()) {
      this.csIp = other.csIp;
    }
  }

  public Condition deepCopy() {
    return new Condition(this);
  }

  @Override
  public void clear() {
    this.owneId = null;
    this.objectId = null;
    this.resourceGroupId = null;
    setTaskSizeIsSet(false);
    this.taskSize = 0;
    this.csIp = null;
  }

  public String getOwneId() {
    return this.owneId;
  }

  public Condition setOwneId(String owneId) {
    this.owneId = owneId;
    return this;
  }

  public void unsetOwneId() {
    this.owneId = null;
  }

  /** Returns true if field owneId is set (has been assigned a value) and false otherwise */
  public boolean isSetOwneId() {
    return this.owneId != null;
  }

  public void setOwneIdIsSet(boolean value) {
    if (!value) {
      this.owneId = null;
    }
  }

  public int getObjectIdSize() {
    return (this.objectId == null) ? 0 : this.objectId.size();
  }

  public java.util.Iterator<String> getObjectIdIterator() {
    return (this.objectId == null) ? null : this.objectId.iterator();
  }

  public void addToObjectId(String elem) {
    if (this.objectId == null) {
      this.objectId = new ArrayList<String>();
    }
    this.objectId.add(elem);
  }

  public List<String> getObjectId() {
    return this.objectId;
  }

  public Condition setObjectId(List<String> objectId) {
    this.objectId = objectId;
    return this;
  }

  public void unsetObjectId() {
    this.objectId = null;
  }

  /** Returns true if field objectId is set (has been assigned a value) and false otherwise */
  public boolean isSetObjectId() {
    return this.objectId != null;
  }

  public void setObjectIdIsSet(boolean value) {
    if (!value) {
      this.objectId = null;
    }
  }

  public String getResourceGroupId() {
    return this.resourceGroupId;
  }

  public Condition setResourceGroupId(String resourceGroupId) {
    this.resourceGroupId = resourceGroupId;
    return this;
  }

  public void unsetResourceGroupId() {
    this.resourceGroupId = null;
  }

  /** Returns true if field resourceGroupId is set (has been assigned a value) and false otherwise */
  public boolean isSetResourceGroupId() {
    return this.resourceGroupId != null;
  }

  public void setResourceGroupIdIsSet(boolean value) {
    if (!value) {
      this.resourceGroupId = null;
    }
  }

  public int getTaskSize() {
    return this.taskSize;
  }

  public Condition setTaskSize(int taskSize) {
    this.taskSize = taskSize;
    setTaskSizeIsSet(true);
    return this;
  }

  public void unsetTaskSize() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __TASKSIZE_ISSET_ID);
  }

  /** Returns true if field taskSize is set (has been assigned a value) and false otherwise */
  public boolean isSetTaskSize() {
    return EncodingUtils.testBit(__isset_bitfield, __TASKSIZE_ISSET_ID);
  }

  public void setTaskSizeIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __TASKSIZE_ISSET_ID, value);
  }

  public String getCsIp() {
    return this.csIp;
  }

  public Condition setCsIp(String csIp) {
    this.csIp = csIp;
    return this;
  }

  public void unsetCsIp() {
    this.csIp = null;
  }

  /** Returns true if field csIp is set (has been assigned a value) and false otherwise */
  public boolean isSetCsIp() {
    return this.csIp != null;
  }

  public void setCsIpIsSet(boolean value) {
    if (!value) {
      this.csIp = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case OWNE_ID:
      if (value == null) {
        unsetOwneId();
      } else {
        setOwneId((String)value);
      }
      break;

    case OBJECT_ID:
      if (value == null) {
        unsetObjectId();
      } else {
        setObjectId((List<String>)value);
      }
      break;

    case RESOURCE_GROUP_ID:
      if (value == null) {
        unsetResourceGroupId();
      } else {
        setResourceGroupId((String)value);
      }
      break;

    case TASK_SIZE:
      if (value == null) {
        unsetTaskSize();
      } else {
        setTaskSize((Integer)value);
      }
      break;

    case CS_IP:
      if (value == null) {
        unsetCsIp();
      } else {
        setCsIp((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case OWNE_ID:
      return getOwneId();

    case OBJECT_ID:
      return getObjectId();

    case RESOURCE_GROUP_ID:
      return getResourceGroupId();

    case TASK_SIZE:
      return Integer.valueOf(getTaskSize());

    case CS_IP:
      return getCsIp();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case OWNE_ID:
      return isSetOwneId();
    case OBJECT_ID:
      return isSetObjectId();
    case RESOURCE_GROUP_ID:
      return isSetResourceGroupId();
    case TASK_SIZE:
      return isSetTaskSize();
    case CS_IP:
      return isSetCsIp();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof Condition)
      return this.equals((Condition)that);
    return false;
  }

  public boolean equals(Condition that) {
    if (that == null)
      return false;

    boolean this_present_owneId = true && this.isSetOwneId();
    boolean that_present_owneId = true && that.isSetOwneId();
    if (this_present_owneId || that_present_owneId) {
      if (!(this_present_owneId && that_present_owneId))
        return false;
      if (!this.owneId.equals(that.owneId))
        return false;
    }

    boolean this_present_objectId = true && this.isSetObjectId();
    boolean that_present_objectId = true && that.isSetObjectId();
    if (this_present_objectId || that_present_objectId) {
      if (!(this_present_objectId && that_present_objectId))
        return false;
      if (!this.objectId.equals(that.objectId))
        return false;
    }

    boolean this_present_resourceGroupId = true && this.isSetResourceGroupId();
    boolean that_present_resourceGroupId = true && that.isSetResourceGroupId();
    if (this_present_resourceGroupId || that_present_resourceGroupId) {
      if (!(this_present_resourceGroupId && that_present_resourceGroupId))
        return false;
      if (!this.resourceGroupId.equals(that.resourceGroupId))
        return false;
    }

    boolean this_present_taskSize = true;
    boolean that_present_taskSize = true;
    if (this_present_taskSize || that_present_taskSize) {
      if (!(this_present_taskSize && that_present_taskSize))
        return false;
      if (this.taskSize != that.taskSize)
        return false;
    }

    boolean this_present_csIp = true && this.isSetCsIp();
    boolean that_present_csIp = true && that.isSetCsIp();
    if (this_present_csIp || that_present_csIp) {
      if (!(this_present_csIp && that_present_csIp))
        return false;
      if (!this.csIp.equals(that.csIp))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(Condition other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetOwneId()).compareTo(other.isSetOwneId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetOwneId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.owneId, other.owneId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetObjectId()).compareTo(other.isSetObjectId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetObjectId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.objectId, other.objectId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetResourceGroupId()).compareTo(other.isSetResourceGroupId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetResourceGroupId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.resourceGroupId, other.resourceGroupId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTaskSize()).compareTo(other.isSetTaskSize());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTaskSize()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.taskSize, other.taskSize);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetCsIp()).compareTo(other.isSetCsIp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetCsIp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.csIp, other.csIp);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Condition(");
    boolean first = true;

    sb.append("owneId:");
    if (this.owneId == null) {
      sb.append("null");
    } else {
      sb.append(this.owneId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("objectId:");
    if (this.objectId == null) {
      sb.append("null");
    } else {
      sb.append(this.objectId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("resourceGroupId:");
    if (this.resourceGroupId == null) {
      sb.append("null");
    } else {
      sb.append(this.resourceGroupId);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("taskSize:");
    sb.append(this.taskSize);
    first = false;
    if (!first) sb.append(", ");
    sb.append("csIp:");
    if (this.csIp == null) {
      sb.append("null");
    } else {
      sb.append(this.csIp);
    }
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

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class ConditionStandardSchemeFactory implements SchemeFactory {
    public ConditionStandardScheme getScheme() {
      return new ConditionStandardScheme();
    }
  }

  private static class ConditionStandardScheme extends StandardScheme<Condition> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Condition struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // OWNE_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.owneId = iprot.readString();
              struct.setOwneIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // OBJECT_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct.objectId = new ArrayList<String>(_list0.size);
                for (int _i1 = 0; _i1 < _list0.size; ++_i1)
                {
                  String _elem2;
                  _elem2 = iprot.readString();
                  struct.objectId.add(_elem2);
                }
                iprot.readListEnd();
              }
              struct.setObjectIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // RESOURCE_GROUP_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.resourceGroupId = iprot.readString();
              struct.setResourceGroupIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // TASK_SIZE
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.taskSize = iprot.readI32();
              struct.setTaskSizeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // CS_IP
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.csIp = iprot.readString();
              struct.setCsIpIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, Condition struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.owneId != null) {
        oprot.writeFieldBegin(OWNE_ID_FIELD_DESC);
        oprot.writeString(struct.owneId);
        oprot.writeFieldEnd();
      }
      if (struct.objectId != null) {
        oprot.writeFieldBegin(OBJECT_ID_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.objectId.size()));
          for (String _iter3 : struct.objectId)
          {
            oprot.writeString(_iter3);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.resourceGroupId != null) {
        oprot.writeFieldBegin(RESOURCE_GROUP_ID_FIELD_DESC);
        oprot.writeString(struct.resourceGroupId);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(TASK_SIZE_FIELD_DESC);
      oprot.writeI32(struct.taskSize);
      oprot.writeFieldEnd();
      if (struct.csIp != null) {
        oprot.writeFieldBegin(CS_IP_FIELD_DESC);
        oprot.writeString(struct.csIp);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class ConditionTupleSchemeFactory implements SchemeFactory {
    public ConditionTupleScheme getScheme() {
      return new ConditionTupleScheme();
    }
  }

  private static class ConditionTupleScheme extends TupleScheme<Condition> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Condition struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetOwneId()) {
        optionals.set(0);
      }
      if (struct.isSetObjectId()) {
        optionals.set(1);
      }
      if (struct.isSetResourceGroupId()) {
        optionals.set(2);
      }
      if (struct.isSetTaskSize()) {
        optionals.set(3);
      }
      if (struct.isSetCsIp()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetOwneId()) {
        oprot.writeString(struct.owneId);
      }
      if (struct.isSetObjectId()) {
        {
          oprot.writeI32(struct.objectId.size());
          for (String _iter4 : struct.objectId)
          {
            oprot.writeString(_iter4);
          }
        }
      }
      if (struct.isSetResourceGroupId()) {
        oprot.writeString(struct.resourceGroupId);
      }
      if (struct.isSetTaskSize()) {
        oprot.writeI32(struct.taskSize);
      }
      if (struct.isSetCsIp()) {
        oprot.writeString(struct.csIp);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Condition struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        struct.owneId = iprot.readString();
        struct.setOwneIdIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list5 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.objectId = new ArrayList<String>(_list5.size);
          for (int _i6 = 0; _i6 < _list5.size; ++_i6)
          {
            String _elem7;
            _elem7 = iprot.readString();
            struct.objectId.add(_elem7);
          }
        }
        struct.setObjectIdIsSet(true);
      }
      if (incoming.get(2)) {
        struct.resourceGroupId = iprot.readString();
        struct.setResourceGroupIdIsSet(true);
      }
      if (incoming.get(3)) {
        struct.taskSize = iprot.readI32();
        struct.setTaskSizeIsSet(true);
      }
      if (incoming.get(4)) {
        struct.csIp = iprot.readString();
        struct.setCsIpIsSet(true);
      }
    }
  }

}
