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
  /// Update information
  /// </summary>
  #if !SILVERLIGHT
  [Serializable]
  #endif
  public partial class Update_Info : TBase
  {
    private string _versionInfo;
    private string _downloadUrl;

    public string VersionInfo
    {
      get
      {
        return _versionInfo;
      }
      set
      {
        __isset.versionInfo = true;
        this._versionInfo = value;
      }
    }

    public string DownloadUrl
    {
      get
      {
        return _downloadUrl;
      }
      set
      {
        __isset.downloadUrl = true;
        this._downloadUrl = value;
      }
    }


    public Isset __isset;
    #if !SILVERLIGHT
    [Serializable]
    #endif
    public struct Isset {
      public bool versionInfo;
      public bool downloadUrl;
    }

    public Update_Info() {
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
            if (field.Type == TType.String) {
              VersionInfo = iprot.ReadString();
            } else { 
              TProtocolUtil.Skip(iprot, field.Type);
            }
            break;
          case 2:
            if (field.Type == TType.String) {
              DownloadUrl = iprot.ReadString();
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
      TStruct struc = new TStruct("Update_Info");
      oprot.WriteStructBegin(struc);
      TField field = new TField();
      if (VersionInfo != null && __isset.versionInfo) {
        field.Name = "versionInfo";
        field.Type = TType.String;
        field.ID = 1;
        oprot.WriteFieldBegin(field);
        oprot.WriteString(VersionInfo);
        oprot.WriteFieldEnd();
      }
      if (DownloadUrl != null && __isset.downloadUrl) {
        field.Name = "downloadUrl";
        field.Type = TType.String;
        field.ID = 2;
        oprot.WriteFieldBegin(field);
        oprot.WriteString(DownloadUrl);
        oprot.WriteFieldEnd();
      }
      oprot.WriteFieldStop();
      oprot.WriteStructEnd();
    }

    public override string ToString() {
      StringBuilder sb = new StringBuilder("Update_Info(");
      sb.Append("VersionInfo: ");
      sb.Append(VersionInfo);
      sb.Append(",DownloadUrl: ");
      sb.Append(DownloadUrl);
      sb.Append(")");
      return sb.ToString();
    }

  }

}
