/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#include "OneboxThriftService_types.h"

#include <algorithm>

namespace OneboxThriftService {

int _kService_StatusValues[] = {
  Service_Status::Service_Status_Online,
  Service_Status::Service_Status_Offline,
  Service_Status::Service_Status_Error,
  Service_Status::Service_Status_Pause,
  Service_Status::Service_Status_Uninitial
};
const char* _kService_StatusNames[] = {
  "Service_Status_Online",
  "Service_Status_Offline",
  "Service_Status_Error",
  "Service_Status_Pause",
  "Service_Status_Uninitial"
};
const std::map<int, const char*> _Service_Status_VALUES_TO_NAMES(::apache::thrift::TEnumIterator(5, _kService_StatusValues, _kService_StatusNames), ::apache::thrift::TEnumIterator(-1, NULL, NULL));

const char* File_Node::ascii_fingerprint = "B1450930277FAE79F3E997B1270AA97B";
const uint8_t File_Node::binary_fingerprint[16] = {0xB1,0x45,0x09,0x30,0x27,0x7F,0xAE,0x79,0xF3,0xE9,0x97,0xB1,0x27,0x0A,0xA9,0x7B};

uint32_t File_Node::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;


  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->id);
          this->__isset.id = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->parent);
          this->__isset.parent = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->name);
          this->__isset.name = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->type);
          this->__isset.type = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->size);
          this->__isset.size = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->mtime);
          this->__isset.mtime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->ctime);
          this->__isset.ctime = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 8:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->flags);
          this->__isset.flags = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 9:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->extraType);
          this->__isset.extraType = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      default:
        xfer += iprot->skip(ftype);
        break;
    }
    xfer += iprot->readFieldEnd();
  }

  xfer += iprot->readStructEnd();

  return xfer;
}

uint32_t File_Node::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("File_Node");

  xfer += oprot->writeFieldBegin("id", ::apache::thrift::protocol::T_I64, 1);
  xfer += oprot->writeI64(this->id);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("parent", ::apache::thrift::protocol::T_I64, 2);
  xfer += oprot->writeI64(this->parent);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("name", ::apache::thrift::protocol::T_STRING, 3);
  xfer += oprot->writeString(this->name);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("type", ::apache::thrift::protocol::T_I32, 4);
  xfer += oprot->writeI32(this->type);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("size", ::apache::thrift::protocol::T_I64, 5);
  xfer += oprot->writeI64(this->size);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("mtime", ::apache::thrift::protocol::T_I64, 6);
  xfer += oprot->writeI64(this->mtime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("ctime", ::apache::thrift::protocol::T_I64, 7);
  xfer += oprot->writeI64(this->ctime);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("flags", ::apache::thrift::protocol::T_I32, 8);
  xfer += oprot->writeI32(this->flags);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("extraType", ::apache::thrift::protocol::T_I32, 9);
  xfer += oprot->writeI32(this->extraType);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(File_Node &a, File_Node &b) {
  using ::std::swap;
  swap(a.id, b.id);
  swap(a.parent, b.parent);
  swap(a.name, b.name);
  swap(a.type, b.type);
  swap(a.size, b.size);
  swap(a.mtime, b.mtime);
  swap(a.ctime, b.ctime);
  swap(a.flags, b.flags);
  swap(a.extraType, b.extraType);
  swap(a.__isset, b.__isset);
}

const char* TransTask_RootNode::ascii_fingerprint = "2290BAE3EAAB6B9FB3F8D1C7039A5324";
const uint8_t TransTask_RootNode::binary_fingerprint[16] = {0x22,0x90,0xBA,0xE3,0xEA,0xAB,0x6B,0x9F,0xB3,0xF8,0xD1,0xC7,0x03,0x9A,0x53,0x24};

uint32_t TransTask_RootNode::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;


  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->group);
          this->__isset.group = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->source);
          this->__isset.source = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 3:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->parent);
          this->__isset.parent = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 4:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->name);
          this->__isset.name = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 5:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->type);
          this->__isset.type = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 6:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->fileType);
          this->__isset.fileType = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 7:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->status);
          this->__isset.status = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 8:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->statusEx);
          this->__isset.statusEx = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 9:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->userId);
          this->__isset.userId = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 10:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->userType);
          this->__isset.userType = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 11:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->userName);
          this->__isset.userName = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 12:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->priority);
          this->__isset.priority = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 13:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->size);
          this->__isset.size = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 14:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->transedSize);
          this->__isset.transedSize = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 15:
        if (ftype == ::apache::thrift::protocol::T_I32) {
          xfer += iprot->readI32(this->errorCode);
          this->__isset.errorCode = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      default:
        xfer += iprot->skip(ftype);
        break;
    }
    xfer += iprot->readFieldEnd();
  }

  xfer += iprot->readStructEnd();

  return xfer;
}

uint32_t TransTask_RootNode::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("TransTask_RootNode");

  xfer += oprot->writeFieldBegin("group", ::apache::thrift::protocol::T_STRING, 1);
  xfer += oprot->writeString(this->group);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("source", ::apache::thrift::protocol::T_STRING, 2);
  xfer += oprot->writeString(this->source);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("parent", ::apache::thrift::protocol::T_STRING, 3);
  xfer += oprot->writeString(this->parent);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("name", ::apache::thrift::protocol::T_STRING, 4);
  xfer += oprot->writeString(this->name);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("type", ::apache::thrift::protocol::T_I32, 5);
  xfer += oprot->writeI32(this->type);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("fileType", ::apache::thrift::protocol::T_I32, 6);
  xfer += oprot->writeI32(this->fileType);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("status", ::apache::thrift::protocol::T_I32, 7);
  xfer += oprot->writeI32(this->status);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("statusEx", ::apache::thrift::protocol::T_I32, 8);
  xfer += oprot->writeI32(this->statusEx);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("userId", ::apache::thrift::protocol::T_I64, 9);
  xfer += oprot->writeI64(this->userId);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("userType", ::apache::thrift::protocol::T_I32, 10);
  xfer += oprot->writeI32(this->userType);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("userName", ::apache::thrift::protocol::T_STRING, 11);
  xfer += oprot->writeString(this->userName);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("priority", ::apache::thrift::protocol::T_I32, 12);
  xfer += oprot->writeI32(this->priority);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("size", ::apache::thrift::protocol::T_I64, 13);
  xfer += oprot->writeI64(this->size);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("transedSize", ::apache::thrift::protocol::T_I64, 14);
  xfer += oprot->writeI64(this->transedSize);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("errorCode", ::apache::thrift::protocol::T_I32, 15);
  xfer += oprot->writeI32(this->errorCode);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(TransTask_RootNode &a, TransTask_RootNode &b) {
  using ::std::swap;
  swap(a.group, b.group);
  swap(a.source, b.source);
  swap(a.parent, b.parent);
  swap(a.name, b.name);
  swap(a.type, b.type);
  swap(a.fileType, b.fileType);
  swap(a.status, b.status);
  swap(a.statusEx, b.statusEx);
  swap(a.userId, b.userId);
  swap(a.userType, b.userType);
  swap(a.userName, b.userName);
  swap(a.priority, b.priority);
  swap(a.size, b.size);
  swap(a.transedSize, b.transedSize);
  swap(a.errorCode, b.errorCode);
  swap(a.__isset, b.__isset);
}

const char* TeamSpace_Node::ascii_fingerprint = "727CAEA8265A5DE67DBC931F55CD8753";
const uint8_t TeamSpace_Node::binary_fingerprint[16] = {0x72,0x7C,0xAE,0xA8,0x26,0x5A,0x5D,0xE6,0x7D,0xBC,0x93,0x1F,0x55,0xCD,0x87,0x53};

uint32_t TeamSpace_Node::read(::apache::thrift::protocol::TProtocol* iprot) {

  uint32_t xfer = 0;
  std::string fname;
  ::apache::thrift::protocol::TType ftype;
  int16_t fid;

  xfer += iprot->readStructBegin(fname);

  using ::apache::thrift::protocol::TProtocolException;


  while (true)
  {
    xfer += iprot->readFieldBegin(fname, ftype, fid);
    if (ftype == ::apache::thrift::protocol::T_STOP) {
      break;
    }
    switch (fid)
    {
      case 1:
        if (ftype == ::apache::thrift::protocol::T_I64) {
          xfer += iprot->readI64(this->id);
          this->__isset.id = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      case 2:
        if (ftype == ::apache::thrift::protocol::T_STRING) {
          xfer += iprot->readString(this->name);
          this->__isset.name = true;
        } else {
          xfer += iprot->skip(ftype);
        }
        break;
      default:
        xfer += iprot->skip(ftype);
        break;
    }
    xfer += iprot->readFieldEnd();
  }

  xfer += iprot->readStructEnd();

  return xfer;
}

uint32_t TeamSpace_Node::write(::apache::thrift::protocol::TProtocol* oprot) const {
  uint32_t xfer = 0;
  xfer += oprot->writeStructBegin("TeamSpace_Node");

  xfer += oprot->writeFieldBegin("id", ::apache::thrift::protocol::T_I64, 1);
  xfer += oprot->writeI64(this->id);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldBegin("name", ::apache::thrift::protocol::T_STRING, 2);
  xfer += oprot->writeString(this->name);
  xfer += oprot->writeFieldEnd();

  xfer += oprot->writeFieldStop();
  xfer += oprot->writeStructEnd();
  return xfer;
}

void swap(TeamSpace_Node &a, TeamSpace_Node &b) {
  using ::std::swap;
  swap(a.id, b.id);
  swap(a.name, b.name);
  swap(a.__isset, b.__isset);
}

} // namespace
