#include "CSystemInfoMgr.h"

#include <stdio.h>
#include <stdlib.h>
#define _WIN32_DCOM
#include <iostream>

using namespace std;

#include <boost/thread.hpp>
#include <boost/function.hpp>
#include <boost/bind.hpp>

#include "UserContextMgr.h"
#include "UserContext.h"
#include "NotifyMgr.h"
#include "Utility.h"

#include "Power.h"
#include "HandDisk.h"
#include "OpertingSystem.h"
#include "Network.h"

#ifndef MODULE_NAME
#define MODULE_NAME ("CCSystemInfoMgr")
#endif

enum WorkMode
{
	WorkMode_Online, 
	WorkMode_Offline, 
	WorkMode_Error, 
	WorkMode_Pause, 
	WorkMode_Uninitial
};

#define SCAN_INTERVAL_TIME 1 * 60

class CCSystemInfoMgrImpl;

CCSystemInfoMgr* CCSystemInfoMgr::m_instance_ = NULL;

class SYSTEMINFO_DLL_EXPORT CCSystemInfoMgrImpl : public CCSystemInfoMgr
{
public:
	PSTR_SOFTANDHARDSYSTEMINFO GetDeviceInfo()
	{
		if ( m_system_info )
		{
			return m_system_info;
		}

		m_system_info = new STR_SOFTANDHARDSYSTEMINFO();
		if ( !m_system_info ) return NULL;

		m_power.GetPowerInfo(m_system_info->PowerInfo);
		if ( MAXUINT8 == m_system_info->PowerInfo.PowerLifePercent )
		{
			m_system_info->MachineType = MACHINE_TYPE_PC;
		}
		else
		{
			m_system_info->MachineType = MACHINE_TYPE_NOTEBOOK;
		}

		GetProcessorInfo(m_system_info->ProccesorInfo);
		m_opertingsystem.GetOperatingSystemInfo( m_system_info->systemInfo);
		m_network.GetNetworkInfoAdapter(m_system_info->NetworkInfo);
		m_network.GetNetworkInfoMIB(m_system_info->NetworkInfo);
		m_handdisk.GetHDInfo(m_system_info->HDInfo);

		return m_system_info;
	}

	virtual STR_OPERATINGSYSTEM_INFO& GetOperatingSystemInfo()
	{
		m_opertingsystem.GetOperatingSystemInfo(m_operatingsystem_info);
		return m_operatingsystem_info;
	}
	virtual STR_POWER_INFO& GetPowerInfo()
	{
		m_power.GetPowerInfo(m_power_info);
		return m_power_info;
	}

	virtual STR_PROCESSOR_INFO& GetProcessorInfo()
	{
		GetProcessorInfo(m_processor_info);
		return m_processor_info;
	}
	virtual STR_HD_INFOS& GetHDInfo()
	{
		m_handdisk.GetHDInfo(m_hd_info);
		return m_hd_info;
	}
	virtual SHARE_STR_NETWORK_INFO_VECTOR& GetNetworkInfo()
	{
		return m_network_info_new;
	}

	virtual BYTE GetMachineType()
	{
		return m_Machine_Type;
	}
	
	virtual float GetNetworkUseRate(int ComboIndex)
	{
		float userate = 0;
		PSHARE_STR_NETWORK_INFO network = m_network.FindNetwork(m_network_info_new, ComboIndex);
		if ( network )
		{
			userate = network->UseRate;
		}
		return userate > 0 ? userate : 0;
	}
private:
	

	int32_t GetProcessorInfo( STR_PROCESSOR_INFO& strProcessorInfo )
	{
		SYSTEM_INFO info; 
		GetSystemInfo( &info );

		strProcessorInfo.Processor_Architecture = info.wProcessorArchitecture;
		strProcessorInfo.Processor_Type = info.dwProcessorType;

		return 0;
	}

	int32_t GetNetworkInfo( SHARE_STR_NETWORK_INFO_VECTOR& strNetworkInfos)
	{
		m_network.GetNetworkInfoAdapter(strNetworkInfos);
		m_network.GetNetworkInfoMIB(strNetworkInfos);
		return 0;
	}

	void QueryNetworkState()
	{
		SERVICE_INFO(MODULE_NAME, 0, "GetNetworkInfoAdapter Start");
		m_network.GetNetworkInfoAdapter(m_network_info_new);
		SERVICE_INFO(MODULE_NAME, 0, "GetNetworkInfoMIB Start");
		m_network.GetNetworkInfoMIB(m_network_info_new);
		SERVICE_INFO(MODULE_NAME, 0, "Get Network Info End");

		bool new_wire_isopen = false, new_wifi_isopen = false;

		SHARE_STR_NETWORK_INFO_VECTOR_ITERATOR it;
		for (it=m_network_info_new.begin(); it!=m_network_info_new.end(); it++)
		{
			if( !(*it) || !(*it).get() ) continue;

			SERVICE_INFO(MODULE_NAME, 0, "m_network_info_new -- network index: %d network name: %s network type:%d status:%d."
				, (*it)->ComboIndex
				, (*it)->AdapterName
				, (*it)->Type
				, (*it)->dwOperStatus);

			if ( ( IF_TYPE_IEEE80211 == (*it)->Type 
				|| IF_TYPE_IEEE80216_WMAN == (*it)->Type
				|| IF_TYPE_WWANPP == (*it)->Type
				|| IF_TYPE_WWANPP2 == (*it)->Type )
				&& ( IF_OPER_STATUS_CONNECTING == (*it)->dwOperStatus 
				|| IF_OPER_STATUS_CONNECTED == (*it)->dwOperStatus 
				|| IF_OPER_STATUS_OPERATIONAL == (*it)->dwOperStatus ) )
			{
				new_wifi_isopen = true;
			}
			else if ( IF_OPER_STATUS_CONNECTING == (*it)->dwOperStatus 
				|| IF_OPER_STATUS_CONNECTED == (*it)->dwOperStatus 
				|| IF_OPER_STATUS_OPERATIONAL == (*it)->dwOperStatus )
			{
				new_wire_isopen = true;
			}
		}

		UserContext* defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
		if(NULL==defaultUserContext) return;

		if ( !new_wifi_isopen && !new_wire_isopen
			&& ( !m_network_wifi_isopen || !m_network_wire_isopen ) )
		{
			NOTIFY_PARAM param;
			param.type = NOTIFY_MSG_CHANGE_WORK_MODE;
			param.msg1 = SD::Utility::String::type_to_string<std::wstring>(WorkMode_Offline);

			defaultUserContext->getNotifyMgr()->notify(param);
		}

		if ( !m_network_wifi_isopen && !m_network_wire_isopen 
			&& ( new_wifi_isopen || new_wire_isopen ))
		{
			NOTIFY_PARAM param;
			param.type = NOTIFY_MSG_CHANGE_WORK_MODE;
			param.msg1 = SD::Utility::String::type_to_string<std::wstring>(WorkMode_Online);

			defaultUserContext->getNotifyMgr()->notify(param);
		}

		if ( ( m_network_wire_isopen && !new_wire_isopen && new_wifi_isopen )
			|| ( new_wifi_isopen && !m_network_wifi_isopen ) )
		{
			NOTIFY_PARAM param;
			param.type = NOTIFY_MSG_NETWORK_CHANGE;
			param.msg1 = SD::Utility::String::type_to_string<std::wstring>(IF_OPER_STATUS_DISCONNECTED);

			defaultUserContext->getNotifyMgr()->notify(param);
		}

		m_network_wifi_isopen = new_wifi_isopen;
		m_network_wire_isopen = new_wire_isopen;

		m_network_info_old.clear();
		for (it=m_network_info_new.begin(); it!=m_network_info_new.end(); it++)
		{
			m_network_info_old.push_back(*it);
		}
	}
public:
	CCSystemInfoMgrImpl();

	~CCSystemInfoMgrImpl()
	{
		if (m_system_info)
		{
			delete m_system_info;
			m_system_info = NULL;
		}
		Release();
	}

	void Work()
	{
		if (!m_instance_) return;
		CCSystemInfoMgrImpl* systeminfomgr = static_cast<CCSystemInfoMgrImpl*>(m_instance_);

		m_network.GetNetworkInfoAdapter(m_network_info_old);
		m_network.GetNetworkInfoMIB(m_network_info_old);

		boost::this_thread::sleep( boost::posix_time::seconds(5) );

		SHARE_STR_NETWORK_INFO_VECTOR_ITERATOR it = m_network_info_old.begin();

		SERVICE_INFO(MODULE_NAME, 0, "Work -- network index: %d network name: %s network type:%d status:%d."
			, (*it)->ComboIndex
			, (*it)->AdapterName
			, (*it)->Type
			, (*it)->dwOperStatus);

		UserContext* defaultUserContext = UserContextMgr::getInstance()->getDefaultUserContext();
		if(NULL==defaultUserContext) return;

		for ( it; it!= m_network_info_old.end(); ++it)
		{
			if( !(*it) || !(*it).get() ) continue;

			if ( ( IF_TYPE_IEEE80211 == (*it)->Type 
					|| IF_TYPE_IEEE80216_WMAN == (*it)->Type
					|| IF_TYPE_WWANPP == (*it)->Type
					|| IF_TYPE_WWANPP2 == (*it)->Type )
					&& ( IF_OPER_STATUS_CONNECTING == (*it)->dwOperStatus 
					|| IF_OPER_STATUS_CONNECTED == (*it)->dwOperStatus 
					|| IF_OPER_STATUS_OPERATIONAL == (*it)->dwOperStatus ) )
				{
					NOTIFY_PARAM param;
					param.type = NOTIFY_MSG_NETWORK_CHANGE;
					param.msg1 = SD::Utility::String::type_to_string<std::wstring>(IF_OPER_STATUS_DISCONNECTED);
					defaultUserContext->getNotifyMgr()->notify(param);

					m_network_wifi_isopen = true;
					break;
				}
		}

		boost::this_thread::sleep( boost::posix_time::seconds(5) );

		m_power.GetPowerInfo(m_power_info);

		if ( MAXUINT8 == m_power_info.PowerLifePercent ) m_Machine_Type = MACHINE_TYPE_PC;
		else m_Machine_Type = MACHINE_TYPE_NOTEBOOK;

		time_t start_time = 0;
		while( m_stop_ )
		{
			boost::this_thread::sleep( boost::posix_time::seconds(10) );
			if ( systeminfomgr && ((time(NULL) - start_time) > SCAN_INTERVAL_TIME) )
			{
				boost::this_thread::sleep( boost::posix_time::seconds(5) );
				systeminfomgr->GetPowerInfo();
				boost::this_thread::sleep( boost::posix_time::seconds(5) );
				systeminfomgr->QueryNetworkState( );
				start_time = time(NULL);
			}
		}
		SERVICE_INFO(MODULE_NAME, 0, "CSystemInfoMgr Exit");
	}

	void init( )
	{
		thread_ = boost::thread(boost::bind(&CCSystemInfoMgrImpl::Work, this));
	}

	void Release()
	{
		m_stop_ = false;
		thread_.interrupt();
		thread_.join();
	}

private:
	bool m_isInitializeSecurity;

protected:
	PSTR_SOFTANDHARDSYSTEMINFO m_system_info;

	STR_OPERATINGSYSTEM_INFO	m_operatingsystem_info;
	STR_POWER_INFO				m_power_info;
	STR_PROCESSOR_INFO			m_processor_info;
	STR_HD_INFOS				m_hd_info;

	BYTE						m_Machine_Type; //pc, notebook

	bool m_stop_;

	boost::thread thread_;
private:
	SHARE_STR_NETWORK_INFO_VECTOR	m_network_info_new;
	SHARE_STR_NETWORK_INFO_VECTOR	m_network_info_old;

	bool m_network_wifi_isopen;
	bool m_network_wire_isopen;

private:
	CPower			m_power;
	CHandDisk		m_handdisk;
	CopertingSystem m_opertingsystem;
	CNetwork		m_network;
};

CCSystemInfoMgrImpl::CCSystemInfoMgrImpl()
	:m_system_info(NULL)
	,m_stop_(true)
	,m_isInitializeSecurity(false)
	,m_network_wifi_isopen(false)
	,m_network_wire_isopen(false)
{
}

CCSystemInfoMgr::~CCSystemInfoMgr()
{
	ISSP_LogExit();
}

CCSystemInfoMgr* CCSystemInfoMgr::Create()
{
	std::wstring logFile = SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER+ONEBOX_APP_DIR+PATH_DELIMITER+L"SystemInfo.log";
	if (0 != ISSP_LogInit(SD::Utility::String::wstring_to_string(SD::Utility::FS::get_system_user_app_path()+PATH_DELIMITER + ONEBOX_APP_DIR + L"\\log4cpp.conf"), TP_FILE, SD::Utility::String::wstring_to_string(logFile)))
	{
		
	}

	CCSystemInfoMgrImpl* temp = new CCSystemInfoMgrImpl();
	
	if ( temp )
	{
		 m_instance_ = static_cast<CCSystemInfoMgr*>( temp );
		 temp->init();
	}
	return m_instance_;
}
