#include "VersionTable.h"

class VersionTableImpl : public VersionTable
{
public:
	VersionTableImpl(const std::wstring& parent)
	{
	}

	virtual ~VersionTableImpl(void)
	{
	}

private:

};

std::auto_ptr<VersionTable>  VersionTable::create(const std::wstring& parent)
{
	return std::auto_ptr<VersionTable>(new VersionTableImpl(parent));
}