#pragma once
#include <stdint.h>
#include <xstring>
#include <string>
#include <sstream>
#include <tchar.h>
#include <memory>
#include <windows.h>
#include <IPTypes.h>
#include <vector>

#ifdef __cplusplus
extern "C"
{
#endif
	extern errno_t memset_s(void* dest, size_t destMax, int c, size_t count);

#ifdef __cplusplus
}
#endif  /* __cplusplus */

namespace SystemInfo
{
	//MACHINE_TYPE
	#define		MACHINE_TYPE_PC  0
	#define		MACHINE_TYPE_NOTEBOOK 1
	#define		MACHINE_TYPE_CLOUD 2
	#define		MACHINE_TYPE_UNKNOWN 0xFF


	//PROCESSOR_TYPE
	#define		PROCESSOR_TYPE_32 0
	#define		PROCESSOR_TYPE_64 1
	#define		PROCESSOR_TYPE_UNKNOWN 0xFF


	// OPERATING_SYSTEM_BITS
	#define		OPERATING_SYSTEM_32 0
	#define		OPERATING_SYSTEM_64 1
	#define		OPERATING_SYSTEM_UNKNOWN 0xFF



	// POWER_TYPE
	#define		POWER_TYPE_OFFLINE 0
	#define		POWER_TYPE_ONLINE 1
	#define		POWER_TYPE_UNKNOWN 0xFF


	// POWER_MANAGER_TYPE
	#define		POWER_MANAGER_TYPE_HIGHT 0
	#define		POWER_MANAGER_TYPE_MIDDLE 1
	#define		POWER_MANAGER_TYPE_LOW 2
	#define		POWER_MANAGER_TYPE_CRITICAL 8
	#define		POWER_MANAGER_TYPE_CHARGING 128
	#define		POWER_MANAGER_TYPE_UNKNOWN 0xFF


	typedef struct str_Power_Info
	{
		BYTE	PowerType;
		BYTE	PowerManageType;
		BYTE	PowerLifePercent;
		DWORD	PowerLifeTime;

		str_Power_Info()
		{
			PowerType = POWER_TYPE_UNKNOWN;
			PowerLifePercent = 0;
			PowerLifeTime = 0;
		}
	}STR_POWER_INFO, *PSTR_POWER_INFO;

	typedef struct str_Processor_Info
	{
		uint32_t	Processor_Architecture;
		uint32_t	Processor_Type;

		bool Is64Bit_OS()  
		{      
			bool bRetVal = false;      

			if (Processor_Architecture == PROCESSOR_ARCHITECTURE_AMD64 
				|| Processor_Architecture == PROCESSOR_ARCHITECTURE_IA64 )    
			{
				bRetVal = true;    
			}
			
			return bRetVal;  
		}

		str_Processor_Info()
		{
			Processor_Architecture = PROCESSOR_ARCHITECTURE_UNKNOWN;
			Processor_Type = 0;
		}
	}STR_PROCESSOR_INFO, *PSTR_PROCESSOR_INFO;

	typedef struct str_OperatingSystem_Info
	{
		std::wstring system_version;
		BYTE		 system_bits;
		std::wstring system_name;
	}STR_OPERATINGSYSTEM_INFO, *PSTR_OPERATINGSYSTEM_INFO;

	typedef struct str_NetWork_Info
	{
		DWORD ComboIndex;
		char AdapterName[MAX_ADAPTER_NAME_LENGTH + 4];
		char Description[MAX_ADAPTER_DESCRIPTION_LENGTH + 4];
		UINT AddressLength;
		BYTE Address[MAX_ADAPTER_ADDRESS_LENGTH];
		DWORD Index;
		UINT Type;
		UINT DhcpEnabled;
		PIP_ADDR_STRING CurrentIpAddress;
		IP_ADDR_STRING IpAddressList;
		IP_ADDR_STRING GatewayList;
		IP_ADDR_STRING DhcpServer;
		BOOL HaveWins;
		IP_ADDR_STRING PrimaryWinsServer;
		IP_ADDR_STRING SecondaryWinsServer;
		time_t LeaseObtained;
		time_t LeaseExpires;
		INTERNAL_IF_OPER_STATUS dwOperStatus;
		DWORD dwInOctets;
		DWORD dwInOctetsDiff;
		DWORD dwOutOctets;
		DWORD dwOutOctetsDiff;
		DWORD dwSpeed;
		float UseRate;

		str_NetWork_Info()
		{
			DWORD ComboIndex = 0;
			memset_s(AdapterName, sizeof(AdapterName), 0, sizeof(AdapterName));
			memset_s(Description, MAX_ADAPTER_DESCRIPTION_LENGTH + 4, 0, MAX_ADAPTER_DESCRIPTION_LENGTH + 4);
			AddressLength = 0;
			memset_s(Address, MAX_ADAPTER_ADDRESS_LENGTH, 0, MAX_ADAPTER_ADDRESS_LENGTH);
			Index = 0;
			Type = 0;
			DhcpEnabled = 0;
			CurrentIpAddress = NULL;
			//IP_ADDR_STRING IpAddressList;
			//IP_ADDR_STRING GatewayList;
			//IP_ADDR_STRING DhcpServer;
			HaveWins = FALSE;
			//IP_ADDR_STRING PrimaryWinsServer;
			//IP_ADDR_STRING SecondaryWinsServer;
			LeaseObtained = 0;
			LeaseExpires = 0;
			dwOperStatus = IF_OPER_STATUS_NON_OPERATIONAL;
			dwInOctets = 0;
			dwInOctetsDiff = 0;
			dwOutOctets = 0;
			dwOutOctetsDiff = 0;
			dwSpeed = 0;
			UseRate = 0;
		}
	}STR_NETWORK_INFO, *PSTR_NETWORK_INFO;
	
	typedef std::shared_ptr<STR_NETWORK_INFO> PSHARE_STR_NETWORK_INFO;
	typedef std::vector<PSHARE_STR_NETWORK_INFO> SHARE_STR_NETWORK_INFO_VECTOR;
	typedef SHARE_STR_NETWORK_INFO_VECTOR::iterator SHARE_STR_NETWORK_INFO_VECTOR_ITERATOR;

	/*typedef struct str_NetWork_Infos
	{
	uint32_t NetworkNumber;
	SHARE_STR_NETWORK_INFO_VECTOR_ITERATOR NetworkInfos;

	str_NetWork_Infos()
	{
	NetworkNumber = 0;
	NetworkInfos = NULL;
	}

	~str_NetWork_Infos()
	{

	};
	}STR_NETWORK_INFOS, *PSTR_NETWORK_INFOS;*/

	typedef struct str_HD_Info
	{
		std::wstring    Caption;
		std::wstring    Description;
		uint64_t		InstallDate;
		std::wstring    Name;
		std::wstring    Status;
		std::wstring	CreationClassName;
		std::wstring	Manufacturer;
		std::wstring	Model;
		std::wstring	SKU;
		std::wstring	SerialNumber;
		std::wstring	Tag;
		std::wstring	Version;
		std::wstring	PartNumber;
		std::wstring	OtherIdentifyingInfo;
		bool			PoweredOn;
		bool			Removable;
		bool			Replaceable;
		bool			HotSwappable;
		uint64_t		Capacity;
		uint16_t		MediaType;
		std::wstring	MediaDescription;
		bool			WriteProtectOn;
		bool			CleanerMedia;

		str_HD_Info& operator =(const str_HD_Info& right)
		{
			Caption = right.Caption;
			Description = right.Description;
			InstallDate = right.InstallDate;
			Name = right.Name;
			Status = right.Status;
			CreationClassName = right.CreationClassName;
			Manufacturer = right.Manufacturer;
			Model = right.Model;
			SKU = right.SKU;
			SerialNumber = right.SerialNumber;
			Tag = right.Tag;
			Version = right.Version;
			PartNumber = right.PartNumber;
			OtherIdentifyingInfo = right.OtherIdentifyingInfo;
			PoweredOn = right.PoweredOn;
			Removable = right.Removable;
			Replaceable = right.Replaceable;
			HotSwappable = right.HotSwappable;
			Capacity = right.Capacity;
			MediaType = right.MediaType;
			MediaDescription = right.MediaDescription;
			WriteProtectOn = right.WriteProtectOn;
			CleanerMedia = right.CleanerMedia;

			return *this;
		}
		str_HD_Info()
		{
			memset_s(this, sizeof(str_HD_Info), 0, sizeof(str_HD_Info));
		}
	}STR_HD_INFO, *PSTR_HD_INFO;

	typedef struct str_HD_Infos
	{
		uint32_t		HDNumber;
		PSTR_HD_INFO	HDInfo;

		str_HD_Infos()
		{
			HDNumber = 0;
			HDInfo = NULL;
		}

		~str_HD_Infos()
		{
			if (HDInfo)
			{
				delete HDInfo;
				HDInfo = NULL;
			}
		}
	}STR_HD_INFOS, *PSTR_HD_INFOS;

	typedef struct str_SoftAndHardSystemInfo
	{
		uint32_t					MachineType;
		STR_OPERATINGSYSTEM_INFO	systemInfo;
		STR_POWER_INFO				PowerInfo;
		STR_PROCESSOR_INFO			ProccesorInfo;
		STR_HD_INFOS				HDInfo;
		SHARE_STR_NETWORK_INFO_VECTOR			NetworkInfo;

		str_SoftAndHardSystemInfo()
		{
			MachineType = MACHINE_TYPE_UNKNOWN;
		}
	}STR_SOFTANDHARDSYSTEMINFO, *PSTR_SOFTANDHARDSYSTEMINFO;
}

