/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
#ifndef OneboxSyncService_TYPES_H
#define OneboxSyncService_TYPES_H

#include <thrift/Thrift.h>
#include <thrift/TApplicationException.h>
#include <thrift/protocol/TProtocol.h>
#include <thrift/transport/TTransport.h>

#include <thrift/cxxfunctional.h>


namespace Onebox { namespace SyncService {

struct Authen_Type {
  enum type {
    Authen_Type_Normal = 0,
    Authen_Type_Domain = 1
  };
};

extern const std::map<int, const char*> _Authen_Type_VALUES_TO_NAMES;

struct Service_Status {
  enum type {
    Service_Status_Online = 0,
    Service_Status_Offline = 1,
    Service_Status_Error = 2,
    Service_Status_Pause = 3,
    Service_Status_Uninitial = 4
  };
};

extern const std::map<int, const char*> _Service_Status_VALUES_TO_NAMES;

struct File_Type {
  enum type {
    File_Type_Dir = 0,
    File_Type_File = 1
  };
};

extern const std::map<int, const char*> _File_Type_VALUES_TO_NAMES;

struct Share_Right {
  enum type {
    Share_Right_R = 0,
    Share_Right_RW = 1
  };
};

extern const std::map<int, const char*> _Share_Right_VALUES_TO_NAMES;

struct OverlayIcon_Status {
  enum type {
    OverlayIcon_Status_None = 0,
    OverlayIcon_Status_Synced = 1,
    OverlayIcon_Status_Syncing = 2,
    OverlayIcon_Status_NoActionDelete = 3,
    OverlayIcon_Status_Invalid = 4
  };
};

extern const std::map<int, const char*> _OverlayIcon_Status_VALUES_TO_NAMES;

typedef struct _Trans_Speed_Info__isset {
  _Trans_Speed_Info__isset() : upload(false), download(false) {}
  bool upload;
  bool download;
} _Trans_Speed_Info__isset;

class Trans_Speed_Info {
 public:

  static const char* ascii_fingerprint; // = "F33135321253DAEB67B0E79E416CA831";
  static const uint8_t binary_fingerprint[16]; // = {0xF3,0x31,0x35,0x32,0x12,0x53,0xDA,0xEB,0x67,0xB0,0xE7,0x9E,0x41,0x6C,0xA8,0x31};

  Trans_Speed_Info() : upload(0), download(0) {
  }

  virtual ~Trans_Speed_Info() throw() {}

  int64_t upload;
  int64_t download;

  _Trans_Speed_Info__isset __isset;

  void __set_upload(const int64_t val) {
    upload = val;
  }

  void __set_download(const int64_t val) {
    download = val;
  }

  bool operator == (const Trans_Speed_Info & rhs) const
  {
    if (!(upload == rhs.upload))
      return false;
    if (!(download == rhs.download))
      return false;
    return true;
  }
  bool operator != (const Trans_Speed_Info &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const Trans_Speed_Info & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(Trans_Speed_Info &a, Trans_Speed_Info &b);

typedef struct _Update_Info__isset {
  _Update_Info__isset() : versionInfo(false), downloadUrl(false) {}
  bool versionInfo;
  bool downloadUrl;
} _Update_Info__isset;

class Update_Info {
 public:

  static const char* ascii_fingerprint; // = "07A9615F837F7D0A952B595DD3020972";
  static const uint8_t binary_fingerprint[16]; // = {0x07,0xA9,0x61,0x5F,0x83,0x7F,0x7D,0x0A,0x95,0x2B,0x59,0x5D,0xD3,0x02,0x09,0x72};

  Update_Info() : versionInfo(), downloadUrl() {
  }

  virtual ~Update_Info() throw() {}

  std::string versionInfo;
  std::string downloadUrl;

  _Update_Info__isset __isset;

  void __set_versionInfo(const std::string& val) {
    versionInfo = val;
  }

  void __set_downloadUrl(const std::string& val) {
    downloadUrl = val;
  }

  bool operator == (const Update_Info & rhs) const
  {
    if (!(versionInfo == rhs.versionInfo))
      return false;
    if (!(downloadUrl == rhs.downloadUrl))
      return false;
    return true;
  }
  bool operator != (const Update_Info &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const Update_Info & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(Update_Info &a, Update_Info &b);

typedef struct _Node_Info__isset {
  _Node_Info__isset() : remoteId(false), hasShareLink(false) {}
  bool remoteId;
  bool hasShareLink;
} _Node_Info__isset;

class Node_Info {
 public:

  static const char* ascii_fingerprint; // = "25038F937443AC9A2A06CEE5209E41BF";
  static const uint8_t binary_fingerprint[16]; // = {0x25,0x03,0x8F,0x93,0x74,0x43,0xAC,0x9A,0x2A,0x06,0xCE,0xE5,0x20,0x9E,0x41,0xBF};

  Node_Info() : remoteId(0), hasShareLink(0) {
  }

  virtual ~Node_Info() throw() {}

  int64_t remoteId;
  bool hasShareLink;

  _Node_Info__isset __isset;

  void __set_remoteId(const int64_t val) {
    remoteId = val;
  }

  void __set_hasShareLink(const bool val) {
    hasShareLink = val;
  }

  bool operator == (const Node_Info & rhs) const
  {
    if (!(remoteId == rhs.remoteId))
      return false;
    if (!(hasShareLink == rhs.hasShareLink))
      return false;
    return true;
  }
  bool operator != (const Node_Info &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const Node_Info & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(Node_Info &a, Node_Info &b);

typedef struct _List_Info__isset {
  _List_Info__isset() : id(false), name(false), type(false), flags(false) {}
  bool id;
  bool name;
  bool type;
  bool flags;
} _List_Info__isset;

class List_Info {
 public:

  static const char* ascii_fingerprint; // = "D7BAF3A18D2E378ACDBBC2C3FAE0DE60";
  static const uint8_t binary_fingerprint[16]; // = {0xD7,0xBA,0xF3,0xA1,0x8D,0x2E,0x37,0x8A,0xCD,0xBB,0xC2,0xC3,0xFA,0xE0,0xDE,0x60};

  List_Info() : id(0), name(), type(0), flags(0) {
  }

  virtual ~List_Info() throw() {}

  int64_t id;
  std::string name;
  int32_t type;
  int32_t flags;

  _List_Info__isset __isset;

  void __set_id(const int64_t val) {
    id = val;
  }

  void __set_name(const std::string& val) {
    name = val;
  }

  void __set_type(const int32_t val) {
    type = val;
  }

  void __set_flags(const int32_t val) {
    flags = val;
  }

  bool operator == (const List_Info & rhs) const
  {
    if (!(id == rhs.id))
      return false;
    if (!(name == rhs.name))
      return false;
    if (!(type == rhs.type))
      return false;
    if (!(flags == rhs.flags))
      return false;
    return true;
  }
  bool operator != (const List_Info &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const List_Info & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(List_Info &a, List_Info &b);

typedef struct _Error_Info__isset {
  _Error_Info__isset() : path(false), errorCode(false) {}
  bool path;
  bool errorCode;
} _Error_Info__isset;

class Error_Info {
 public:

  static const char* ascii_fingerprint; // = "EEBC915CE44901401D881E6091423036";
  static const uint8_t binary_fingerprint[16]; // = {0xEE,0xBC,0x91,0x5C,0xE4,0x49,0x01,0x40,0x1D,0x88,0x1E,0x60,0x91,0x42,0x30,0x36};

  Error_Info() : path(), errorCode(0) {
  }

  virtual ~Error_Info() throw() {}

  std::string path;
  int32_t errorCode;

  _Error_Info__isset __isset;

  void __set_path(const std::string& val) {
    path = val;
  }

  void __set_errorCode(const int32_t val) {
    errorCode = val;
  }

  bool operator == (const Error_Info & rhs) const
  {
    if (!(path == rhs.path))
      return false;
    if (!(errorCode == rhs.errorCode))
      return false;
    return true;
  }
  bool operator != (const Error_Info &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const Error_Info & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(Error_Info &a, Error_Info &b);

typedef struct _Share_User_Info__isset {
  _Share_User_Info__isset() : id(false), userName(false), loginName(false), department(false), email(false), right(false) {}
  bool id;
  bool userName;
  bool loginName;
  bool department;
  bool email;
  bool right;
} _Share_User_Info__isset;

class Share_User_Info {
 public:

  static const char* ascii_fingerprint; // = "6789EAB1B9E9B06E13BFC2442CDFC515";
  static const uint8_t binary_fingerprint[16]; // = {0x67,0x89,0xEA,0xB1,0xB9,0xE9,0xB0,0x6E,0x13,0xBF,0xC2,0x44,0x2C,0xDF,0xC5,0x15};

  Share_User_Info() : id(0), userName(), loginName(), department(), email(), right(0) {
  }

  virtual ~Share_User_Info() throw() {}

  int64_t id;
  std::string userName;
  std::string loginName;
  std::string department;
  std::string email;
  int32_t right;

  _Share_User_Info__isset __isset;

  void __set_id(const int64_t val) {
    id = val;
  }

  void __set_userName(const std::string& val) {
    userName = val;
  }

  void __set_loginName(const std::string& val) {
    loginName = val;
  }

  void __set_department(const std::string& val) {
    department = val;
  }

  void __set_email(const std::string& val) {
    email = val;
  }

  void __set_right(const int32_t val) {
    right = val;
  }

  bool operator == (const Share_User_Info & rhs) const
  {
    if (!(id == rhs.id))
      return false;
    if (!(userName == rhs.userName))
      return false;
    if (!(loginName == rhs.loginName))
      return false;
    if (!(department == rhs.department))
      return false;
    if (!(email == rhs.email))
      return false;
    if (!(right == rhs.right))
      return false;
    return true;
  }
  bool operator != (const Share_User_Info &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const Share_User_Info & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(Share_User_Info &a, Share_User_Info &b);

typedef struct _Share_Link_Info__isset {
  _Share_Link_Info__isset() : url(false), accessCode(false), effectAt(false), expireAt(false) {}
  bool url;
  bool accessCode;
  bool effectAt;
  bool expireAt;
} _Share_Link_Info__isset;

class Share_Link_Info {
 public:

  static const char* ascii_fingerprint; // = "50272E49E7C02012722B8F62131C940B";
  static const uint8_t binary_fingerprint[16]; // = {0x50,0x27,0x2E,0x49,0xE7,0xC0,0x20,0x12,0x72,0x2B,0x8F,0x62,0x13,0x1C,0x94,0x0B};

  Share_Link_Info() : url(), accessCode(), effectAt(0), expireAt(0) {
  }

  virtual ~Share_Link_Info() throw() {}

  std::string url;
  std::string accessCode;
  int64_t effectAt;
  int64_t expireAt;

  _Share_Link_Info__isset __isset;

  void __set_url(const std::string& val) {
    url = val;
  }

  void __set_accessCode(const std::string& val) {
    accessCode = val;
  }

  void __set_effectAt(const int64_t val) {
    effectAt = val;
  }

  void __set_expireAt(const int64_t val) {
    expireAt = val;
  }

  bool operator == (const Share_Link_Info & rhs) const
  {
    if (!(url == rhs.url))
      return false;
    if (!(accessCode == rhs.accessCode))
      return false;
    if (!(effectAt == rhs.effectAt))
      return false;
    if (!(expireAt == rhs.expireAt))
      return false;
    return true;
  }
  bool operator != (const Share_Link_Info &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const Share_Link_Info & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(Share_Link_Info &a, Share_Link_Info &b);

typedef struct _Teamspace_Info__isset {
  _Teamspace_Info__isset() : id(false), name(false), description(false), curNumbers(false), createdAt(false), createdBy(false), createdByUserName(false), ownedBy(false), ownedByUserName(false), status(false), spaceQuota(false), spaceUsed(false), maxVersions(false), maxMembers(false), regionId(false) {}
  bool id;
  bool name;
  bool description;
  bool curNumbers;
  bool createdAt;
  bool createdBy;
  bool createdByUserName;
  bool ownedBy;
  bool ownedByUserName;
  bool status;
  bool spaceQuota;
  bool spaceUsed;
  bool maxVersions;
  bool maxMembers;
  bool regionId;
} _Teamspace_Info__isset;

class Teamspace_Info {
 public:

  static const char* ascii_fingerprint; // = "C88AB3A6D0BB16CC01F9FD5CA2E89E09";
  static const uint8_t binary_fingerprint[16]; // = {0xC8,0x8A,0xB3,0xA6,0xD0,0xBB,0x16,0xCC,0x01,0xF9,0xFD,0x5C,0xA2,0xE8,0x9E,0x09};

  Teamspace_Info() : id(0), name(), description(), curNumbers(0), createdAt(0), createdBy(0), createdByUserName(), ownedBy(0), ownedByUserName(), status(0), spaceQuota(0), spaceUsed(0), maxVersions(0), maxMembers(0), regionId(0) {
  }

  virtual ~Teamspace_Info() throw() {}

  int64_t id;
  std::string name;
  std::string description;
  int32_t curNumbers;
  int64_t createdAt;
  int64_t createdBy;
  std::string createdByUserName;
  int64_t ownedBy;
  std::string ownedByUserName;
  int32_t status;
  int64_t spaceQuota;
  int64_t spaceUsed;
  int32_t maxVersions;
  int32_t maxMembers;
  int32_t regionId;

  _Teamspace_Info__isset __isset;

  void __set_id(const int64_t val) {
    id = val;
  }

  void __set_name(const std::string& val) {
    name = val;
  }

  void __set_description(const std::string& val) {
    description = val;
  }

  void __set_curNumbers(const int32_t val) {
    curNumbers = val;
  }

  void __set_createdAt(const int64_t val) {
    createdAt = val;
  }

  void __set_createdBy(const int64_t val) {
    createdBy = val;
  }

  void __set_createdByUserName(const std::string& val) {
    createdByUserName = val;
  }

  void __set_ownedBy(const int64_t val) {
    ownedBy = val;
  }

  void __set_ownedByUserName(const std::string& val) {
    ownedByUserName = val;
  }

  void __set_status(const int32_t val) {
    status = val;
  }

  void __set_spaceQuota(const int64_t val) {
    spaceQuota = val;
  }

  void __set_spaceUsed(const int64_t val) {
    spaceUsed = val;
  }

  void __set_maxVersions(const int32_t val) {
    maxVersions = val;
  }

  void __set_maxMembers(const int32_t val) {
    maxMembers = val;
  }

  void __set_regionId(const int32_t val) {
    regionId = val;
  }

  bool operator == (const Teamspace_Info & rhs) const
  {
    if (!(id == rhs.id))
      return false;
    if (!(name == rhs.name))
      return false;
    if (!(description == rhs.description))
      return false;
    if (!(curNumbers == rhs.curNumbers))
      return false;
    if (!(createdAt == rhs.createdAt))
      return false;
    if (!(createdBy == rhs.createdBy))
      return false;
    if (!(createdByUserName == rhs.createdByUserName))
      return false;
    if (!(ownedBy == rhs.ownedBy))
      return false;
    if (!(ownedByUserName == rhs.ownedByUserName))
      return false;
    if (!(status == rhs.status))
      return false;
    if (!(spaceQuota == rhs.spaceQuota))
      return false;
    if (!(spaceUsed == rhs.spaceUsed))
      return false;
    if (!(maxVersions == rhs.maxVersions))
      return false;
    if (!(maxMembers == rhs.maxMembers))
      return false;
    if (!(regionId == rhs.regionId))
      return false;
    return true;
  }
  bool operator != (const Teamspace_Info &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const Teamspace_Info & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(Teamspace_Info &a, Teamspace_Info &b);

typedef struct _Teamspace_Member_Info__isset {
  _Teamspace_Member_Info__isset() : id(false), loginName(false), type(false), name(false), description(false) {}
  bool id;
  bool loginName;
  bool type;
  bool name;
  bool description;
} _Teamspace_Member_Info__isset;

class Teamspace_Member_Info {
 public:

  static const char* ascii_fingerprint; // = "43A8F4F7754DCA77126D1683FBD4A442";
  static const uint8_t binary_fingerprint[16]; // = {0x43,0xA8,0xF4,0xF7,0x75,0x4D,0xCA,0x77,0x12,0x6D,0x16,0x83,0xFB,0xD4,0xA4,0x42};

  Teamspace_Member_Info() : id(0), loginName(), type(0), name(), description() {
  }

  virtual ~Teamspace_Member_Info() throw() {}

  int64_t id;
  std::string loginName;
  int32_t type;
  std::string name;
  std::string description;

  _Teamspace_Member_Info__isset __isset;

  void __set_id(const int64_t val) {
    id = val;
  }

  void __set_loginName(const std::string& val) {
    loginName = val;
  }

  void __set_type(const int32_t val) {
    type = val;
  }

  void __set_name(const std::string& val) {
    name = val;
  }

  void __set_description(const std::string& val) {
    description = val;
  }

  bool operator == (const Teamspace_Member_Info & rhs) const
  {
    if (!(id == rhs.id))
      return false;
    if (!(loginName == rhs.loginName))
      return false;
    if (!(type == rhs.type))
      return false;
    if (!(name == rhs.name))
      return false;
    if (!(description == rhs.description))
      return false;
    return true;
  }
  bool operator != (const Teamspace_Member_Info &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const Teamspace_Member_Info & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(Teamspace_Member_Info &a, Teamspace_Member_Info &b);

typedef struct _Teamspace_Membership__isset {
  _Teamspace_Membership__isset() : id(false), teamRole(false), role(false), teamspace(false), member(false) {}
  bool id;
  bool teamRole;
  bool role;
  bool teamspace;
  bool member;
} _Teamspace_Membership__isset;

class Teamspace_Membership {
 public:

  static const char* ascii_fingerprint; // = "333ADDE5823568BDA6F2EFCAF6E7FAD0";
  static const uint8_t binary_fingerprint[16]; // = {0x33,0x3A,0xDD,0xE5,0x82,0x35,0x68,0xBD,0xA6,0xF2,0xEF,0xCA,0xF6,0xE7,0xFA,0xD0};

  Teamspace_Membership() : id(0), teamRole(), role() {
  }

  virtual ~Teamspace_Membership() throw() {}

  int64_t id;
  std::string teamRole;
  std::string role;
  Teamspace_Info teamspace;
  Teamspace_Member_Info member;

  _Teamspace_Membership__isset __isset;

  void __set_id(const int64_t val) {
    id = val;
  }

  void __set_teamRole(const std::string& val) {
    teamRole = val;
  }

  void __set_role(const std::string& val) {
    role = val;
  }

  void __set_teamspace(const Teamspace_Info& val) {
    teamspace = val;
  }

  void __set_member(const Teamspace_Member_Info& val) {
    member = val;
  }

  bool operator == (const Teamspace_Membership & rhs) const
  {
    if (!(id == rhs.id))
      return false;
    if (!(teamRole == rhs.teamRole))
      return false;
    if (!(role == rhs.role))
      return false;
    if (!(teamspace == rhs.teamspace))
      return false;
    if (!(member == rhs.member))
      return false;
    return true;
  }
  bool operator != (const Teamspace_Membership &rhs) const {
    return !(*this == rhs);
  }

  bool operator < (const Teamspace_Membership & ) const;

  uint32_t read(::apache::thrift::protocol::TProtocol* iprot);
  uint32_t write(::apache::thrift::protocol::TProtocol* oprot) const;

};

void swap(Teamspace_Membership &a, Teamspace_Membership &b);

}} // namespace

#endif
