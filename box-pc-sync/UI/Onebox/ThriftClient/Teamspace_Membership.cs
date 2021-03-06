/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
using System;
using System.Collections;
using System.Collections.Generic;
using System.Text;
using System.IO;
using Thrift;
using Thrift.Collections;
using System.Runtime.Serialization;
using Thrift.Protocol;
using Thrift.Transport;

namespace Onebox.ThriftClient
{

  /// <summary>
  /// user teamspace data
  /// </summary>
  #if !SILVERLIGHT
  [Serializable]
  #endif
  public partial class Teamspace_Membership : TBase
  {
    private long _id;
    private string _teamRole;
    private string _role;
    private Teamspace_Info _teamspace;
    private Teamspace_Member_Info _member;

    public long Id
    {
      get
      {
        return _id;
      }
      set
      {
        __isset.id = true;
        this._id = value;
      }
    }

    public string TeamRole
    {
      get
      {
        return _teamRole;
      }
      set
      {
        __isset.teamRole = true;
        this._teamRole = value;
      }
    }

    public string Role
    {
      get
      {
        return _role;
      }
      set
      {
        __isset.role = true;
        this._role = value;
      }
    }

    public Teamspace_Info Teamspace
    {
      get
      {
        return _teamspace;
      }
      set
      {
        __isset.teamspace = true;
        this._teamspace = value;
      }
    }

    public Teamspace_Member_Info Member
    {
      get
      {
        return _member;
      }
      set
      {
        __isset.member = true;
        this._member = value;
      }
    }


    public Isset __isset;
    #if !SILVERLIGHT
    [Serializable]
    #endif
    public struct Isset {
      public bool id;
      public bool teamRole;
      public bool role;
      public bool teamspace;
      public bool member;
    }

    public Teamspace_Membership() {
    }

    public void Read (TProtocol iprot)
    {
      TField field;
      iprot.ReadStructBegin();
      while (true)
      {
        field = iprot.ReadFieldBegin();
        if (field.Type == TType.Stop) { 
          break;
        }
        switch (field.ID)
        {
          case 1:
            if (field.Type == TType.I64) {
              Id = iprot.ReadI64();
            } else { 
              TProtocolUtil.Skip(iprot, field.Type);
            }
            break;
          case 2:
            if (field.Type == TType.String) {
              TeamRole = iprot.ReadString();
            } else { 
              TProtocolUtil.Skip(iprot, field.Type);
            }
            break;
          case 3:
            if (field.Type == TType.String) {
              Role = iprot.ReadString();
            } else { 
              TProtocolUtil.Skip(iprot, field.Type);
            }
            break;
          case 4:
            if (field.Type == TType.Struct) {
              Teamspace = new Teamspace_Info();
              Teamspace.Read(iprot);
            } else { 
              TProtocolUtil.Skip(iprot, field.Type);
            }
            break;
          case 5:
            if (field.Type == TType.Struct) {
              Member = new Teamspace_Member_Info();
              Member.Read(iprot);
            } else { 
              TProtocolUtil.Skip(iprot, field.Type);
            }
            break;
          default: 
            TProtocolUtil.Skip(iprot, field.Type);
            break;
        }
        iprot.ReadFieldEnd();
      }
      iprot.ReadStructEnd();
    }

    public void Write(TProtocol oprot) {
      TStruct struc = new TStruct("Teamspace_Membership");
      oprot.WriteStructBegin(struc);
      TField field = new TField();
      if (__isset.id) {
        field.Name = "id";
        field.Type = TType.I64;
        field.ID = 1;
        oprot.WriteFieldBegin(field);
        oprot.WriteI64(Id);
        oprot.WriteFieldEnd();
      }
      if (TeamRole != null && __isset.teamRole) {
        field.Name = "teamRole";
        field.Type = TType.String;
        field.ID = 2;
        oprot.WriteFieldBegin(field);
        oprot.WriteString(TeamRole);
        oprot.WriteFieldEnd();
      }
      if (Role != null && __isset.role) {
        field.Name = "role";
        field.Type = TType.String;
        field.ID = 3;
        oprot.WriteFieldBegin(field);
        oprot.WriteString(Role);
        oprot.WriteFieldEnd();
      }
      if (Teamspace != null && __isset.teamspace) {
        field.Name = "teamspace";
        field.Type = TType.Struct;
        field.ID = 4;
        oprot.WriteFieldBegin(field);
        Teamspace.Write(oprot);
        oprot.WriteFieldEnd();
      }
      if (Member != null && __isset.member) {
        field.Name = "member";
        field.Type = TType.Struct;
        field.ID = 5;
        oprot.WriteFieldBegin(field);
        Member.Write(oprot);
        oprot.WriteFieldEnd();
      }
      oprot.WriteFieldStop();
      oprot.WriteStructEnd();
    }

    public override string ToString() {
      StringBuilder sb = new StringBuilder("Teamspace_Membership(");
      sb.Append("Id: ");
      sb.Append(Id);
      sb.Append(",TeamRole: ");
      sb.Append(TeamRole);
      sb.Append(",Role: ");
      sb.Append(Role);
      sb.Append(",Teamspace: ");
      sb.Append(Teamspace== null ? "<null>" : Teamspace.ToString());
      sb.Append(",Member: ");
      sb.Append(Member== null ? "<null>" : Member.ToString());
      sb.Append(")");
      return sb.ToString();
    }

  }

}
