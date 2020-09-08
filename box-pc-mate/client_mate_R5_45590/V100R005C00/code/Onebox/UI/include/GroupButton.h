#pragma once

#define CONT_DEFAULT_WIDTH 400		//���мĬ�Ͽ��
#define CONT_OFFSET_WIDTH 35		//���м�����˵�+���׿��
#define TEXT_DEFAULT_WIDTH 250		//�ı�Ĭ�Ͽ��

class CGroupButtonUI : public CHorizontalLayoutUI
{
public:
	CGroupButtonUI();

public:
	void showPath(const std::wstring& moduleName, std::list<PathNode>& pathNodes);

	void showMenu(const std::wstring& moduleName, TNotifyUI& msg);

	PathNode getPathNodeById(int id);

private:
	int getNameWidth(const std::wstring& str_fileName);

	int getNameWidth(const std::wstring& str_fileName, std::wstringstream& showText);

	void addShowNode(const std::wstring& moduleName, const std::wstring& fileName, int64_t& fileId,
		const std::wstring& showName, int width);

	void addMenuNode(const std::wstring& moduleName);

private:
	std::list<PathNode> menuNodes_;
};

