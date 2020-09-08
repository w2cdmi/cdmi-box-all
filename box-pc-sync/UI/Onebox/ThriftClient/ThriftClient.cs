using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Thrift;
using Thrift.Collections;
using Thrift.Protocol;
using Thrift.Transport;
using Microsoft.Win32;

namespace Onebox.ThriftClient
{
    public class Client
    {
        public static  Int32 ThriftPort = 0;
        private static Object _LockClient = new Object();

        public SyncService.Client getClient()
        {
            lock (_LockClient)
            {
                TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
                TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
                SyncService.Client client = new SyncService.Client(protocol);
                transport.Open();
                return client;
            }
        }

        public int initUserContext(long uiHandle, string confPath)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            int _Return = client.initUserContext(uiHandle,confPath);
            transport.Close();
            return _Return;
        }

        public int releaseUserContext()
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            int _Return = client.releaseUserContext( );
            transport.Close();
            return _Return;
        }

        public int getServiceStatus()
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            int _Return = client.getServiceStatus();
            transport.Close();
            return _Return;
        }

       public int changeServiceWorkMode(Service_Status status)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            int _Return = client.changeServiceWorkMode(status);
            transport.Close();
            return _Return;
        }

        public int login(int type, string username, string password, string domain)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            int _Return = client.login(type,username,password,domain);
            transport.Close();
            return _Return;
        }

        public int logout()
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            int _Return = client.logout();
            transport.Close();
            return _Return;
        }

        public string encyptString(string src)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            string _Return = client.encyptString(src);
            transport.Close();
            return _Return;
        }

        public string decyptString(string src)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            string _Return = client.decyptString(src);
            transport.Close();
            return _Return;
        }

        public int updateConfigure()
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            int _Return = client.updateConfigure();
            transport.Close();
            return _Return;
        }

        public Trans_Speed_Info getTransSpeed()
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            Trans_Speed_Info _Return = client.getTransSpeed();
            transport.Close();
            return _Return;
        }

        public long getUserId()
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            long  _Return = client.getUserId();
            transport.Close();
            return _Return;
        }

        public Update_Info getUpdateInfo()
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            Update_Info _Return = client.getUpdateInfo();
            transport.Close();
            return _Return;
        }

        public int downloadClient(string downloadUrl, string location)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            int _Return = client.downloadClient(downloadUrl, location);
            transport.Close();
            return _Return;
        }

        public List<List_Info> listRemoteDir(long parent, long owner_id)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            List<List_Info> _Return = client.listRemoteDir(parent, owner_id);
            transport.Close();
            return _Return;
        }

        public List<Teamspace_Membership> listTeamspacesByUser(long userId)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();

            List<Teamspace_Membership> _Return = client.listTeamspacesByUser(userId);
            transport.Close();
            return _Return;
        }

        public long upload(string path, long parent, long ownerId)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            long _Return = client.upload(path, parent, ownerId);
            transport.Close();
            return _Return;
        }

        public List<Error_Info> listError(int offset, int limit)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            List<Error_Info> _Return = client.listError(offset, limit);
            transport.Close();
            return _Return;
        }

        public List<Share_User_Info> listDomainUsers(string keyword)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            List<Share_User_Info> _Return = client.listDomainUsers(keyword);
            transport.Close();
            return _Return;
        }

        public List<Share_User_Info> listShareUsers(string path)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            List<Share_User_Info> _Return = client.listShareUsers(path);
            transport.Close();
            return _Return;
        }

        public int setShareMember(string path, List<Share_User_Info> shareUserInfos, string emailMsg)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            int _Return = client.setShareMember(path, shareUserInfos, emailMsg);
            transport.Close();
            return _Return;
        }

        public int delShareMember(string path, Share_User_Info shareUserInfo)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            int _Return = client.delShareMember(path, shareUserInfo);
            transport.Close();
            return _Return;
        }

        public int cancelShare(string path)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            int _Return = client.cancelShare(path);
            transport.Close();
            return _Return;
        }

        public Share_Link_Info getShareLink(string path)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            Share_Link_Info _Return = client.getShareLink(path);
            transport.Close();
            return _Return;
        }

        public Share_Link_Info modifyShareLink(string path, Share_Link_Info shareLinkInfo)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            Share_Link_Info _Return = client.modifyShareLink(path, shareLinkInfo);
            transport.Close();
            return _Return;
        }

        public int delShareLink(string path)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            int _Return = client.delShareLink(path);
            transport.Close();
            return _Return;
        }

        public string getRandomString()
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            string _Return = client.getRandomString();
            transport.Close();
            return _Return;
        }

        public int sendEmail(string type, string path, Share_Link_Info shareLinkInfo, string emailMsg, List<string> mailto)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            int _Return = client.sendEmail( type,  path,  shareLinkInfo,  emailMsg,  mailto);
            transport.Close();
            return _Return;
        }

        public Dictionary<string, Share_User_Info> listBatchDomainUsers(List<string> keyword)
        {
            TTransport transport = new Thrift.Transport.TSocket("127.0.0.1", ThriftPort);
            TProtocol protocol = new Thrift.Protocol.TBinaryProtocol(transport);
            SyncService.Client client = new SyncService.Client(protocol);
            transport.Open();
            Dictionary<string, Share_User_Info> _Return = client.listBatchDomainUsers(keyword);
            transport.Close();
            return _Return;
        }
    }
}
