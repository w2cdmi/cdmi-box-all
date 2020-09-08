#include "stdafxOnebox.h"
#include "Layout/UIVerticalLayout.h"
#include "Layout/UIHorizontalLayout.h"
#include "ShareNode.h"

#define SHOWAREA_DEFAULT_WIDTH 560		//չʾ��Ĭ�Ͽ��
#define SHOWAREA_OFFSET_WIDTH 10		//���׿��

class CUserListControlUI : public CVerticalLayoutUI
{
public:
	CUserListControlUI();

	~CUserListControlUI();

	void showList(std::list<ShareNode>& nameNodes,int32_t& _height);

private:
	int getNameWidth(const std::wstring& str_fileName);

	int getNameWidth(const std::wstring& str_fileName, std::wstringstream& showText);

	void addItem(int32_t& _index, const std::wstring& userName,const std::wstring& showName, int width,CHorizontalLayoutUI* pHor);
};