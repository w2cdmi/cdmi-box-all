#include "Network.h"
#include "Utility.h"
#include "ErrorCode.h"

#include <winbase.h>
#include <IPHlpApi.h>

#include "UserContextMgr.h"
#include "UserContext.h"
#include "NotifyMgr.h"
#include "Utility.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("CNetwork")
#endif

CNetwork::CNetwork()
{
}

CNetwork::~CNetwork()
{
}
PSHARE_STR_NETWORK_INFO CNetwork::FindNetwork(SHARE_STR_NETWORK_INFO_VECTOR& strNetworkInfos, int ComboIndex)
{

	for (uint32_t i=0; i < strNetworkInfos.size(); i++)
	{
		if ( ComboIndex == strNetworkInfos[i]->ComboIndex )
		{
			return strNetworkInfos[i];
		}
	}
	return NULL;
}

PSHARE_STR_NETWORK_INFO CNetwork::FindNetwork(SHARE_STR_NETWORK_INFO_VECTOR& strNetworkInfos, const char* adaptername)
{
	if( !adaptername ) return NULL;

	for (uint32_t i=0; i < strNetworkInfos.size(); i++)
	{
		const char* substr = strstr(adaptername, strNetworkInfos[i]->AdapterName);
		if ( substr )
		{
			return strNetworkInfos[i];
		}
	}
	return NULL;
}

int32_t CNetwork::GetNetworkInfoMIB( SHARE_STR_NETWORK_INFO_VECTOR& network_Infos )
{
	DWORD dwSize = 0;
	DWORD dwRetVal = 0;

	/* variables used for GetIfTable and GetIfEntry */
	MIB_IFTABLE *pIfTable = NULL;
	MIB_IFROW *pIfRow = NULL;
	char* pcTable = NULL;

	// Allocate memory for our pointers.
	pcTable = (char *)new char[sizeof (MIB_IFTABLE)];

	if (pcTable == NULL) {
		return 1;
	}
	// Make an initial call to GetIfTable to get the
	// necessary size into dwSize
	dwSize = sizeof (MIB_IFTABLE);
	memset_s(pcTable, dwSize, 0, dwSize);
	pIfTable = (MIB_IFTABLE *)pcTable;
	if (::GetIfTable(pIfTable, &dwSize, FALSE) == ERROR_INSUFFICIENT_BUFFER) 
	{
		delete[] pcTable;
		pcTable = NULL;

		pcTable = (char *)new char[dwSize];

		if (!pcTable)
		{
			return 0;
		}
	}
	SERVICE_INFO(MODULE_NAME, 0, "GetNetworkInfoMIB 0");
	// Make a second call to GetIfTable to get the actual
	// data we want.
	PSHARE_STR_NETWORK_INFO network_info = NULL;
	memset_s(pcTable, dwSize, 0, dwSize);
	pIfTable = (MIB_IFTABLE*)pcTable;

	bool isNetwork = false;

	if ((dwRetVal = ::GetIfTable(pIfTable, &dwSize, FALSE)) == NO_ERROR) 
	{
		SERVICE_INFO(MODULE_NAME, 0, "GetNetworkInfoMIB 1");
		for (uint32_t i = 0; i < pIfTable->dwNumEntries; i++) 
		{
			pIfRow = (MIB_IFROW *) & pIfTable->table[i];

			network_info = FindNetwork(network_Infos, SD::Utility::String::wstring_to_string(pIfRow->wszName).c_str());
			if( !network_info || !network_info.get() ) continue;

			network_info->Type = pIfRow->dwType;
			network_info->dwSpeed = pIfRow->dwSpeed;
			network_info->dwInOctets = pIfRow->dwInOctets;
			network_info->dwOutOctets = pIfRow->dwOutOctets;
			network_info->dwOperStatus = pIfRow->dwOperStatus;
		}
		SERVICE_INFO(MODULE_NAME, 0, "GetNetworkInfoMIB 2");
	} 
	else 
	{
		if (pcTable != NULL) 
		{
			delete[] pcTable;
			pcTable = NULL;
		}  
		SERVICE_INFO(MODULE_NAME, 0, "GetNetworkInfoMIB 3");
		return 1;
	}

	if (pcTable != NULL) 
	{
		delete[] pcTable;
		pcTable = NULL;
	}  
	SERVICE_INFO(MODULE_NAME, 0, "GetNetworkInfoMIB 4");
	return 0;
}
int32_t CNetwork::GetNetworkInfoAdapter( SHARE_STR_NETWORK_INFO_VECTOR& network_infos )
{
	PIP_ADAPTER_INFO pAdapterInfo;
	PIP_ADAPTER_INFO pAdapter = NULL;
	char* cAdapterInfo = NULL;
	DWORD dwRetVal = 0;

	ULONG ulOutBufLen = sizeof (IP_ADAPTER_INFO);
	pAdapterInfo = new IP_ADAPTER_INFO;
	if (pAdapterInfo == NULL) {
		return 1;
	}
	// Make an initial call to GetAdaptersInfo to get
	// the necessary size into the ulOutBufLen variable
	if ( GetAdaptersInfo(pAdapterInfo, &ulOutBufLen) == ERROR_BUFFER_OVERFLOW ) 
	{
		delete pAdapterInfo;
		pAdapterInfo = NULL;

		pAdapterInfo = (PIP_ADAPTER_INFO)new char[ulOutBufLen];

		if (pAdapterInfo == NULL) 
		{
			return LOCAL_NETWORK_INFO_GET_ERROR;
		}
	}
	SERVICE_INFO(MODULE_NAME, 0, "GetNetworkInfoAdapter 0");
	if ((dwRetVal = GetAdaptersInfo(pAdapterInfo, &ulOutBufLen)) == NO_ERROR) 
	{
		SERVICE_INFO(MODULE_NAME, 0, "GetNetworkInfoAdapter 1");
		pAdapter = pAdapterInfo;

		int32_t index = 0;
		bool isnetwork = false;
		static bool issend = false;

		network_infos.clear();

		while ( pAdapter ) 
		{
			PSHARE_STR_NETWORK_INFO network_info;

			network_info.reset(new STR_NETWORK_INFO);
			if( !network_info || !network_info.get() )
			{
				if (pAdapterInfo)
				{
					delete[] pAdapterInfo;
					pAdapterInfo = NULL;
				}
				return RT_ERROR;
			}

			network_info->ComboIndex = pAdapter->ComboIndex;
			memcpy_s(network_info->AdapterName, MAX_ADAPTER_NAME_LENGTH + 4, pAdapter->AdapterName, MAX_ADAPTER_NAME_LENGTH + 4);
			memcpy_s(network_info->Description, MAX_ADAPTER_DESCRIPTION_LENGTH + 4, pAdapter->Description, MAX_ADAPTER_DESCRIPTION_LENGTH + 4);
			network_info->AddressLength = pAdapter->AddressLength;
			memcpy_s(network_info->Address, MAX_ADAPTER_ADDRESS_LENGTH, pAdapter->Address, MAX_ADAPTER_ADDRESS_LENGTH);
			network_info->Index = pAdapter->Index;
			network_info->DhcpEnabled = pAdapter->DhcpEnabled;
			network_info->CurrentIpAddress = pAdapter->CurrentIpAddress;
			network_info->IpAddressList = pAdapter->IpAddressList;
			network_info->GatewayList = pAdapter->GatewayList;
			network_info->DhcpServer = pAdapter->DhcpServer;
			network_info->HaveWins = pAdapter->HaveWins;
			network_info->PrimaryWinsServer = pAdapter->PrimaryWinsServer;
			network_info->SecondaryWinsServer = pAdapter->SecondaryWinsServer;
			network_info->LeaseObtained = pAdapter->LeaseObtained;
			network_info->LeaseExpires = pAdapter->LeaseExpires;
			network_info->Type = pAdapter->Type;

			network_infos.push_back(network_info);
			pAdapter = pAdapter->Next;
		}
	} 
	else
	{
		if (pAdapterInfo)
		{
			delete[] pAdapterInfo;
			pAdapterInfo = NULL;
		}
		SERVICE_INFO(MODULE_NAME, 0, "GetNetworkInfoAdapter 2");
		return LOCAL_NETWORK_INFO_GET_ERROR;
	}
	SERVICE_INFO(MODULE_NAME, 0, "GetNetworkInfoAdapter 3");
	if (pAdapterInfo)
	{
		delete[] pAdapterInfo;
		pAdapterInfo = NULL;
	}
	SERVICE_INFO(MODULE_NAME, 0, "GetNetworkInfoAdapter 4");
	return RT_OK;
}
