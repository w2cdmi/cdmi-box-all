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
  /// Node info
  /// </summary>
  #if !SILVERLIGHT
  [Serializable]
  #endif
  public partial class Node_Info : TBase
  {
    private long _remoteId;
    private bool _hasShareLink;

    public long RemoteId
    {
      get
      {
        return _remoteId;
      }
      set
      {
        __isset.remoteId = true;
        this._remoteId = value;
      }
    }

    public bool HasShareLink
    {
      get
      {
        return _hasShareLink;
      }
      set
      {
        __isset.hasShareLink = true;
        this._hasShareLink = value;
      }
    }


    public Isset __isset;
    #if !SILVERLIGHT
    [Serializable]
    #endif
    public struct Isset {
      public bool remoteId;
      public bool hasShareLink;
    }

    public Node_Info() {
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
              RemoteId = iprot.ReadI64();
            } else { 
              TProtocolUtil.Skip(iprot, field.Type);
            }
            break;
          case 2:
            if (field.Type == TType.Bool) {
              HasShareLink = iprot.ReadBool();
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
      TStruct struc = new TStruct("Node_Info");
      oprot.WriteStructBegin(struc);
      TField field = new TField();
      if (__isset.remoteId) {
        field.Name = "remoteId";
        field.Type = TType.I64;
        field.ID = 1;
        oprot.WriteFieldBegin(field);
        oprot.WriteI64(RemoteId);
        oprot.WriteFieldEnd();
      }
      if (__isset.hasShareLink) {
        field.Name = "hasShareLink";
        field.Type = TType.Bool;
        field.ID = 2;
        oprot.WriteFieldBegin(field);
        oprot.WriteBool(HasShareLink);
        oprot.WriteFieldEnd();
      }
      oprot.WriteFieldStop();
      oprot.WriteStructEnd();
    }

    public override string ToString() {
      StringBuilder sb = new StringBuilder("Node_Info(");
      sb.Append("RemoteId: ");
      sb.Append(RemoteId);
      sb.Append(",HasShareLink: ");
      sb.Append(HasShareLink);
      sb.Append(")");
      return sb.ToString();
    }

  }

}
