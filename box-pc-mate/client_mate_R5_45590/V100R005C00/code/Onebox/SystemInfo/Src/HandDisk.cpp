#include "HandDisk.h"
#include <comdef.h>
#include <WbemCli.h>
#include <Wbemidl.h>

#define wszDrive L"\\\\.\\PhysicalDrive0"

BOOL __fastcall DoIdentify( HANDLE hPhysicalDriveIOCTL,                            
						   PSENDCMDINPARAMS pSCIP,                            
						   PSENDCMDOUTPARAMS pSCOP,                            
						   BYTE btIDCmd,                            
						   BYTE btDriveNum,                            
						   PDWORD pdwBytesReturned)
{    
	pSCIP->cBufferSize = IDENTIFY_BUFFER_SIZE;    
	pSCIP->irDriveRegs.bFeaturesReg = 0;    
	pSCIP->irDriveRegs.bSectorCountReg  = 1;    
	pSCIP->irDriveRegs.bSectorNumberReg = 1;    
	pSCIP->irDriveRegs.bCylLowReg  = 0;    
	pSCIP->irDriveRegs.bCylHighReg = 0;
	pSCIP->irDriveRegs.bDriveHeadReg = (btDriveNum & 1) ? 0xB0 : 0xA0;    
	pSCIP->irDriveRegs.bCommandReg = btIDCmd;    
	pSCIP->bDriveNumber = btDriveNum;    
	pSCIP->cBufferSize = IDENTIFY_BUFFER_SIZE;
	return DeviceIoControl(    
		hPhysicalDriveIOCTL,                            
		SMART_RCV_DRIVE_DATA,                            
		(LPVOID)pSCIP,                            
		sizeof(SENDCMDINPARAMS) - 1,                            
		(LPVOID)pSCOP,                            
		sizeof(SENDCMDOUTPARAMS) + IDENTIFY_BUFFER_SIZE - 1,                            
		pdwBytesReturned, NULL);
}

char *__fastcall ConvertToString(DWORD dwDiskData[256], int nFirstIndex, int nLastIndex)
{    
	static char szResBuf[1024];    
	char ss[256];    
	int nIndex = 0;    
	int nPosition = 0;
	for(nIndex = nFirstIndex; nIndex <= nLastIndex; nIndex++)    
	{        
		ss[nPosition] = (char)(dwDiskData[nIndex] / 256);        
		nPosition++;// Get low BYTE for 2nd character        
		ss[nPosition] = (char)(dwDiskData[nIndex] % 256);        
		nPosition++;    
	}// End the string    
	ss[nPosition] = '\0';    
	int i, index=0;    
	for(i=0; i<nPosition; i++)    
	{        
		if(ss[i]==0 || ss[i]==32)    continue;        
		szResBuf[index]=ss[i];        
		index++;    
	}    
	szResBuf[index]=0;
	return szResBuf;
} 

int GetID(int32_t driver)    
{
	//
	std::wstring sFilePath;
	sFilePath = (L"\\\\.\\PHYSICALDRIVE0");
	//sFilePath += static_cast<std::wstring>(driver);
	HANDLE hFile=::CreateFile(sFilePath.c_str(),                     
		GENERIC_READ | GENERIC_WRITE,                     
		FILE_SHARE_READ | FILE_SHARE_WRITE,                     
		NULL, OPEN_EXISTING,                     
		0, NULL);
	DWORD dwBytesReturned;
	GETVERSIONINPARAMS gvopVersionParams;
	DeviceIoControl(hFile,       //         
		SMART_GET_VERSION,                
		NULL,                
		0,                
		&gvopVersionParams,                
		sizeof(gvopVersionParams),                 
		&dwBytesReturned, NULL);

	if(gvopVersionParams.bIDEDeviceMap <= 0)
	{
		CloseHandle(hFile);
		return -2;
	}
	int btIDCmd = 0;    
	SENDCMDINPARAMS InParams;    
	int nDrive =0;    
	btIDCmd = (gvopVersionParams.bIDEDeviceMap >> nDrive & 0x10) ? 0 : 1;//IDE_ATAPI_IDENTIFY : IDE_ATA_IDENTIFY;
	BYTE btIDOutCmd[sizeof(SENDCMDOUTPARAMS) + IDENTIFY_BUFFER_SIZE - 1];
	if(DoIdentify(hFile,                    
		&InParams,                    
		(PSENDCMDOUTPARAMS)btIDOutCmd,                    
		(BYTE)btIDCmd,                    
		(BYTE)nDrive, &dwBytesReturned) == FALSE)  
	{
		CloseHandle(hFile);
		return -3;
	}
	CloseHandle(hFile);
	DWORD dwDiskData[256];    
	USHORT *pIDSector; 
	pIDSector = (USHORT*)((SENDCMDOUTPARAMS*)btIDOutCmd)->bBuffer;    
	for(int i=0; i < 256; i++)    
		dwDiskData[i] = pIDSector[i];    //
	//ZeroMemory(szSerialNumber, sizeof(szSerialNumber));    
	//strcpy(szSerialNumber, ConvertToString(dwDiskData, 10, 19));//
	//ZeroMemory(szModelNumber, sizeof(szModelNumber));    
	//strcpy(szModelNumber, ConvertToString(dwDiskData, 27, 46));

	return 0;
}
BOOL GetDriveGeometry(LPWSTR wszPath, DISK_GEOMETRY *pdg)
{
	HANDLE hDevice = INVALID_HANDLE_VALUE;  // handle to the drive to be examined 
	BOOL bResult   = FALSE;                 // results flag
	DWORD junk     = 0;                     // discard results

	hDevice = CreateFileW(wszPath,          // drive to open
		0,                // no access to the drive
		FILE_SHARE_READ | // share mode
		FILE_SHARE_WRITE, 
		NULL,             // default security attributes
		OPEN_EXISTING,    // disposition
		0,                // file attributes
		NULL);            // do not copy file attributes

	if (hDevice == INVALID_HANDLE_VALUE)    // cannot open the drive
	{
		return (FALSE);
	}

	bResult = DeviceIoControl(hDevice,                       // device to be queried
		IOCTL_DISK_GET_DRIVE_GEOMETRY, // operation to perform
		NULL, 0,                       // no input buffer
		pdg, sizeof(*pdg),            // output buffer
		&junk,                         // # bytes returned
		(LPOVERLAPPED) NULL);          // synchronous I/O

	CloseHandle(hDevice);

	return (bResult);
}

CHandDisk::CHandDisk()
{
}

CHandDisk::~CHandDisk()
{
}

int32_t CHandDisk::GetHDInfo( STR_HD_INFOS& strHDInfo )
{
	HRESULT hres;   
	if( !m_isInitializeSecurity )
	{
		hres =  CoInitializeSecurity(        
			NULL,         
			-1,                          // COM authentication        
			NULL,                        // Authentication services        
			NULL,                        // Reserved        
			RPC_C_AUTHN_LEVEL_DEFAULT,   // Default authentication         
			RPC_C_IMP_LEVEL_IMPERSONATE, // Default Impersonation          
			NULL,                        // Authentication info        
			EOAC_NONE,                   // Additional capabilities         
			NULL                         // Reserved        
			);
		if (FAILED(hres))    
		{        
			return -1;                    // Program has failed.    
		} 

		m_isInitializeSecurity = true;
	}

	// Step 3: ---------------------------------------------------    
	// Obtain the initial locator to WMI -------------------------    
	IWbemLocator *pLoc = NULL;    
	hres = CoCreateInstance(        
		CLSID_WbemLocator,                     
		0,         
		CLSCTX_INPROC_SERVER,         
		IID_IWbemLocator, 
		(LPVOID *) &pLoc);     
	if (FAILED(hres))    
	{        
		CoUninitialize();        
		return -2;                 // Program has failed.    
	}   
	// Step 4: -----------------------------------------------------    
	// Connect to WMI through the IWbemLocator::ConnectServer method    
	IWbemServices *pSvc = NULL;     // Connect to the root\cimv2 namespace with    
	// the current user and obtain pointer pSvc    
	// to make IWbemServices calls.    
	hres = pLoc->ConnectServer(         
		_bstr_t(L"ROOT\\CIMV2"), // Object path of WMI namespace        
		NULL,                    // User name. NULL = current user         
		NULL,                    // User password. NULL = current         
		0,                       // Locale. NULL indicates current         
		NULL,                    // Security flags.         
		0,                       // Authority (e.g. Kerberos)         
		0,                       // Context object          
		&pSvc                    // pointer to IWbemServices proxy         
		);        

	if (FAILED(hres))    
	{        
		CoUninitialize();        
		return -3;                // Program has failed.    
	}    

	// Step 5: --------------------------------------------------    
	// Set security levels on the proxy -------------------------    
	hres = CoSetProxyBlanket(       
		pSvc,                        // Indicates the proxy to set       
		RPC_C_AUTHN_WINNT,           // RPC_C_AUTHN_xxx       
		RPC_C_AUTHZ_NONE,            // RPC_C_AUTHZ_xxx       
		NULL,                        // Server principal name        
		RPC_C_AUTHN_LEVEL_CALL,      // RPC_C_AUTHN_LEVEL_xxx        
		RPC_C_IMP_LEVEL_IMPERSONATE, // RPC_C_IMP_LEVEL_xxx       
		NULL,                        // client identity       
		EOAC_NONE                    // proxy capabilities     
		);    
	if (FAILED(hres))    
	{        
		pSvc->Release();        
		pLoc->Release();            
		CoUninitialize();        
		return -4;               // Program has failed.    
	}    
	// Step 6: --------------------------------------------------    
	// Use the IWbemServices pointer to make requests of WMI ----    
	// For example, get the name of the operating system    
	IEnumWbemClassObject* pEnumerator = NULL;    
	hres = pSvc->ExecQuery(        
		bstr_t("WQL"),         
		bstr_t("SELECT * FROM Win32_DiskDrive"),       
		WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY,         
		NULL,        
		&pEnumerator);        

	if (FAILED(hres))    
	{        
		pSvc->Release();        
		pLoc->Release();        
		CoUninitialize();       
		return -5;               // Program has failed.    
	}    
	// Step 7: -------------------------------------------------    
	// Get the data from the query in step 6 -------------------     
	IWbemClassObject *pclsObj;    
	ULONG uReturn = 0;    

	IEnumWbemClassObject* temp = pEnumerator;

	std::vector<STR_HD_INFO*> hdlist;

	while (pEnumerator)    
	{        
		(void)pEnumerator->Next(WBEM_INFINITE, 
			1,             
			&pclsObj, &uReturn);        
		if(0 == uReturn)        
		{            
			break;        
		}        
		STR_HD_INFO* hdinfo = new STR_HD_INFO();
		VARIANT vtProp;        // Get the value of the Name property        
		CIMTYPE cimtype = 0;
		VariantInit(&vtProp);

		(void)pclsObj->Get(L"SerialNumber", 0, &vtProp, &cimtype, 0);     
		hdinfo->SerialNumber = vtProp.bstrVal; //string   SerialNumber = NULL;

		VariantInit(&vtProp);
		cimtype = 0;
		(void)pclsObj->Get(L"Caption", 0, &vtProp, &cimtype, 0);    
		hdinfo->Caption = vtProp.bstrVal; //string   Caption;

		(void)pclsObj->Get(L"Description", 0, &vtProp, &cimtype, 0);     
		hdinfo->Description = vtProp.bstrVal; //string   Description;

		(void)pclsObj->Get(L"InstallDate", 0, &vtProp, &cimtype, 0);     
		hdinfo->InstallDate = vtProp.ullVal; //uint64_t InstallDate;

		(void)pclsObj->Get(L"Name", 0, &vtProp, &cimtype, 0);     
		hdinfo->Name = vtProp.bstrVal; //string   Name;

		(void)pclsObj->Get(L"Status", 0, &vtProp, 0, 0);     
		hdinfo->Status = vtProp.bstrVal; //string   Status;

		(void)pclsObj->Get(L"CreationClassName", 0, &vtProp, 0, 0);     
		hdinfo->CreationClassName = vtProp.bstrVal; //string   CreationClassName;

		(void)pclsObj->Get(L"Manufacturer", 0, &vtProp, 0, 0);     
		hdinfo->Manufacturer = vtProp.bstrVal; //string   Manufacturer;

		(void)pclsObj->Get(L"Model", 0, &vtProp, 0, 0);     
		hdinfo->Model = vtProp.bstrVal; //string   Model;

		(void)pclsObj->Get(L"SKU", 0, &vtProp, 0, 0);     
		hdinfo->SKU = vtProp.bstrVal; //string   SKU;

		(void)pclsObj->Get(L"SerialNumber", 0, &vtProp, 0, 0);     
		hdinfo->SerialNumber = vtProp.bstrVal; //string   SerialNumber = NULL;

		(void)pclsObj->Get(L"Tag", 0, &vtProp, 0, 0);     
		hdinfo->Tag = vtProp.bstrVal; //string   Tag = NULL;

		(void)pclsObj->Get(L"Version", 0, &vtProp, 0, 0);     
		hdinfo->Version = vtProp.bstrVal; //string   Version;

		(void)pclsObj->Get(L"PartNumber", 0, &vtProp, 0, 0);     
		hdinfo->PartNumber = vtProp.bstrVal; //string   PartNumber;

		(void)pclsObj->Get(L"OtherIdentifyingInfo", 0, &vtProp, 0, 0);     
		hdinfo->OtherIdentifyingInfo = vtProp.bstrVal; //string   OtherIdentifyingInfo;

		(void)pclsObj->Get(L"PoweredOn", 0, &vtProp, 0, 0);     
		hdinfo->PoweredOn = ( 0 != vtProp.boolVal ); //boolean  PoweredOn;

		(void)pclsObj->Get(L"Removable", 0, &vtProp, 0, 0);     
		hdinfo->Removable = ( 0 != vtProp.boolVal ); //boolean  Removable;

		(void)pclsObj->Get(L"Replaceable", 0, &vtProp, 0, 0);     
		hdinfo->Replaceable = ( 0 != vtProp.boolVal ); //boolean  Replaceable;

		(void)pclsObj->Get(L"HotSwappable", 0, &vtProp, 0, 0);     
		hdinfo->HotSwappable = ( 0 != vtProp.boolVal ); //boolean  HotSwappable;

		(void)pclsObj->Get(L"Capacity", 0, &vtProp, 0, 0);     
		hdinfo->Capacity = vtProp.ullVal; //uint64_t Capacity;

		(void)pclsObj->Get(L"MediaType", 0, &vtProp, 0, 0);     
		hdinfo->MediaType = vtProp.uiVal; //uint16_t MediaType;

		(void)pclsObj->Get(L"MediaDescription", 0, &vtProp, 0, 0);     
		hdinfo->MediaDescription = vtProp.bstrVal; //string   MediaDescription;

		(void)pclsObj->Get(L"WriteProtectOn", 0, &vtProp, 0, 0);     
		hdinfo->WriteProtectOn = ( 0 != vtProp.boolVal ); //boolean  WriteProtectOn;

		(void)pclsObj->Get(L"CleanerMedia", 0, &vtProp, 0, 0);     
		hdinfo->CleanerMedia = ( 0 != vtProp.boolVal ); //boolean  CleanerMedia;

		hdlist.push_back(hdinfo);

		VariantClear(&vtProp);    
	}    

	strHDInfo.HDNumber = hdlist.size();
	if (strHDInfo.HDNumber > 0)
	{
		if (strHDInfo.HDInfo)
		{
			delete strHDInfo.HDInfo;
			strHDInfo.HDInfo = NULL;
		}

		strHDInfo.HDInfo = new STR_HD_INFO[strHDInfo.HDNumber];
		if ( strHDInfo.HDInfo )
		{
			for (uint32_t i=0; i <strHDInfo.HDNumber; i++)
			{
				strHDInfo.HDInfo[i] = *hdlist[i];
			}
		}
	}

	for (uint32_t i=0; i<strHDInfo.HDNumber; i++)
	{
		delete hdlist[i];
		hdlist[i] = NULL;
	}
	hdlist.clear();
	// Cleanup    
	// ========        
	pSvc->Release();    
	pLoc->Release();    
	pEnumerator->Release();    
	pclsObj->Release();    
	CoUninitialize();
	return 0;
}

void GetDisk()
{
	DISK_GEOMETRY pdg = { 0 }; // disk drive geometry structure
	BOOL bResult = FALSE;      // generic results flag
	ULONGLONG DiskSize = 0;    // size of the drive, in bytes

	bResult = GetDriveGeometry (wszDrive, &pdg);

	if (bResult) 
	{
		wprintf(L"Drive path      = %ws\n",   wszDrive);
		wprintf(L"Cylinders       = %I64d\n", pdg.Cylinders);
		wprintf(L"Tracks/cylinder = %ld\n",   (ULONG) pdg.TracksPerCylinder);
		wprintf(L"Sectors/track   = %ld\n",   (ULONG) pdg.SectorsPerTrack);
		wprintf(L"Bytes/sector    = %ld\n",   (ULONG) pdg.BytesPerSector);

		DiskSize = pdg.Cylinders.QuadPart * (ULONG)pdg.TracksPerCylinder *
			(ULONG)pdg.SectorsPerTrack * (ULONG)pdg.BytesPerSector;
		wprintf(L"Disk size       = %I64d (Bytes)\n"
			L"                = %.2f (Gb)\n", 
			DiskSize, (double) DiskSize / (1024 * 1024 * 1024));
	} 
	else 
	{
		wprintf (L"GetDriveGeometry failed. Error %ld.\n", GetLastError ());
	}

}