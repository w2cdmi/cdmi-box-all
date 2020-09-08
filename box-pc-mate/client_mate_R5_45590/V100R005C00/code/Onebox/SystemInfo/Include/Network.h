#pragma once
#include "SystemInfoDefine.h"
using namespace SystemInfo;

class CNetwork
{
public:
	CNetwork();
	~CNetwork();

	int32_t GetNetworkInfoMIB( SHARE_STR_NETWORK_INFO_VECTOR& network_Infos );
	int32_t GetNetworkInfoAdapter( SHARE_STR_NETWORK_INFO_VECTOR& network_infos );

	PSHARE_STR_NETWORK_INFO FindNetwork(SHARE_STR_NETWORK_INFO_VECTOR& strNetworkInfos, const char* adaptername);
	PSHARE_STR_NETWORK_INFO FindNetwork(SHARE_STR_NETWORK_INFO_VECTOR& strNetworkInfos, int ComboIndex);
private:

};
