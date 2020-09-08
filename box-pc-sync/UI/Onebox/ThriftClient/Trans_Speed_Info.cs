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
  /// Trans speed information
  /// </summary>
  #if !SILVERLIGHT
  [Serializable]
  #endif
  public partial class Trans_Speed_Info : TBase
  {
    private long _upload;
    private long _download;

    public long Upload
    {
      get
      {
        return _upload;
      }
      set
      {
        __isset.upload = true;
        this._upload = value;
      }
    }

    public long Download
    {
      get
      {
        return _download;
      }
      set
      {
        __isset.download = true;
        this._download = value;
      }
    }


    public Isset __isset;
    #if !SILVERLIGHT
    [Serializable]
    #endif
    public struct Isset {
      public bool upload;
      public bool download;
    }

    public Trans_Speed_Info() {
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
              Upload = iprot.ReadI64();
            } else { 
              TProtocolUtil.Skip(iprot, field.Type);
            }
            break;
          case 2:
            if (field.Type == TType.I64) {
              Download = iprot.ReadI64();
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
      TStruct struc = new TStruct("Trans_Speed_Info");
      oprot.WriteStructBegin(struc);
      TField field = new TField();
      if (__isset.upload) {
        field.Name = "upload";
        field.Type = TType.I64;
        field.ID = 1;
        oprot.WriteFieldBegin(field);
        oprot.WriteI64(Upload);
        oprot.WriteFieldEnd();
      }
      if (__isset.download) {
        field.Name = "download";
        field.Type = TType.I64;
        field.ID = 2;
        oprot.WriteFieldBegin(field);
        oprot.WriteI64(Download);
        oprot.WriteFieldEnd();
      }
      oprot.WriteFieldStop();
      oprot.WriteStructEnd();
    }

    public override string ToString() {
      StringBuilder sb = new StringBuilder("Trans_Speed_Info(");
      sb.Append("Upload: ");
      sb.Append(Upload);
      sb.Append(",Download: ");
      sb.Append(Download);
      sb.Append(")");
      return sb.ToString();
    }

  }

}
