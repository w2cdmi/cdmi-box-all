#ifndef _SMART_HANDLE_H_
#define _SMART_HANDLE_H_

#include <windows.h>

template<typename HandleType, 
	template<class> class CloseFunction, 
	HandleType NULL_VALUE = NULL>
class CSmartHandle : public CloseFunction<HandleType>
{
public:
	CSmartHandle()
	{
		m_Handle = NULL_VALUE;
	}

	CSmartHandle(HandleType h)
	{
		m_Handle = h;
	}

	HandleType operator=(HandleType h)
	{
		if (m_Handle != h)
		{
			CleanUp();
			m_Handle = h;
		}

		return(*this);
	}

	bool CloseHandle()
	{
		return CleanUp();
	}

	void Detach()
	{
		m_Handle = NULL_VALUE;
	}

	operator HandleType()
	{
		return m_Handle;
	}

	HandleType* GetPointer()
	{
		return &m_Handle;
	}

	operator bool()
	{
		return IsValid();
	}

	bool IsValid()
	{
		return m_Handle != NULL_VALUE;
	}

	~CSmartHandle()
	{
		CleanUp();
	}

protected:
	bool CleanUp()
	{
		if ( m_Handle != NULL_VALUE )
		{
			bool b = Close(m_Handle);
			m_Handle = NULL_VALUE;
			return b;
		}
		return false;
	}

protected:
	HandleType m_Handle;
};

template<typename T>
struct CCloseHandle
{
	bool Close(T handle)
	{
		return !!::CloseHandle(handle);
	}
};

template<typename T>
struct CCloseWinHttpHandle
{
	bool Close(T handle)
	{
		return !!::WinHttpCloseHandle(handle);
	}
};

template<typename T>
struct CCloseFile
{
	bool Close(T file)
	{
		if (NULL != file)
		{
			return !!fclose(file);
		}
		return true;
	}
};

typedef CSmartHandle<HANDLE, CCloseHandle> SmartHandle;
typedef CSmartHandle<HANDLE, CCloseWinHttpHandle> SmartWinHttpHandle;
typedef CSmartHandle<FILE*, CCloseFile, NULL> SmartFile;

#endif
