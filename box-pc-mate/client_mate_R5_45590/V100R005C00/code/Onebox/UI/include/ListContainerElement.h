#pragma once

//#include "Common.h"
#include "RenameRichEdit.h"
#include "BackupAllCommon.h"
namespace Onebox
{
	class BaseContainerElement : public CListContainerElementUI
	{
	public:
		BaseContainerElement();
		void SetPos(RECT rc);
		void DoEvent(TEventUI& event);
		bool Select(bool bSelect = true,bool bCallback = true);
		bool SelectRange(int index, bool bCallBack = false);
		bool SelectRange(POINT posstart, POINT posend);
		bool IsExpanded() const;

	public:
		void MouseEnter(TEventUI& event);
		void MouseDown(TEventUI& event);
		void MouseUp(TEventUI& event);
		void MouseRDown(TEventUI& event);
		void MouseRUp(TEventUI& event);
		void MouseMove(TEventUI& event);
		void MouseDBClick(TEventUI& event);
		void MouseLeave(TEventUI& event);

		void KeyDown(TEventUI& event);

		void SetParentMouseMoveType(int type);
		int GetParentMouseMoveType();
		void SetParentMouseDownPoint(POINT pt);
		POINT GetParentMouseDownPoint();
		void SetParentMouseUpPoint(POINT pt);
		//POINT GetParentMouseUpPoint();
		void SetParentCurEnter(int cursel);
		int  GetParentCurEnter();
		int  GetParentSelectSize();
		void ShowSelectWnd( TEventUI& event );
		bool IsEncloseSelect();
	public:
		void renameItem(POINT& controlPoint);
		void setRenameFlag(bool bFlag=true);
	public:
		UIFileNode	m_uNodeData;

	protected:
		bool		m_bButtonDown;
		POINT		m_pButtonDownPoint;

		int			m_iRenameReady;
		bool		m_bReadOnly;
	};

	class CShadeListContainerElement : public BaseContainerElement
	{
	public:
		CShadeListContainerElement();
		~CShadeListContainerElement();

	public:
		void SetAttribute(LPCTSTR pstrName, LPCTSTR pstrValue);
		void DoPaint(HDC hDC, const RECT& rcPaint);
		void DrawItemBk(HDC hDC, const RECT& rcItem);

		void SetStartColor(DWORD startcolor);
		void SetEndColor(DWORD endcolor);
		void SetDualColor(DWORD evencolor);
		void SetSingularColor(DWORD singularrowcolor);

		void SetValue(UINT8 value);
	private:
		DWORD m_shadecolor_;
		DWORD m_shadecolor2_;

		DWORD m_singularrowcolor_;
		DWORD m_dualrowcolor_;

		UINT8 m_value_;
	};


	/*class CShadeListContainerElement : public CShadeListContainerElement 
	{
	public:
		CShadeListContainerElement();

	public:

	protected:

	};*/


	struct TransTaskListContainerElement : public CShadeListContainerElement
	{
		UITransTaskRootNode nodeData;
	};

	struct ShareFrameListContainerElement : public BaseContainerElement
	{
		UIShareNode nodeData;
	};

	struct ShareFrameTileLayoutListContainerElement : public BaseContainerElement
	{
		UIShareNode nodeData;
	};

	struct FileVersionListContainerElement : public BaseContainerElement
	{
		UIFileVersionNode nodeData;
	};
	
	struct TeamSpaceManageListContainerElement : public BaseContainerElement
	{
		UITeamSpaceManageNode nodeData;
	};

	struct TeamSpaceManageListUserContainerElement : public BaseContainerElement
	{
		UITeamSpaceManageUserNode nodeData;
	};

	
	struct TeamSpaceManageTileLayoutListContainerElement : public BaseContainerElement
	{
		UITeamSpaceManageUserNode nodeData;
	};

	struct TransErrorListContainerElement : public CShadeListContainerElement
	{
		UITransTaskDetailNode nodeData;
	};	

	struct UploadFrameTreeNode : public CTreeNodeUI
	{
		UploadFrameTreeNode();
		void DoEvent(TEventUI& event);
		void setRenameFlag(bool bFlag=true);
		void SetExpanded(bool bFlag=true);
		bool IsExpanded() const;
		void RemoveAll();
		bool IsEnableMultiSelect() const;
		void SetEnableMultiSelect(bool bEnableMltiSelect = true);
		FileBaseInfo nodeData;
		bool m_rename;
		bool m_bExpand;
		bool m_isEnableMltiSelect;
 	};

	struct FullBackUpErrorListContainerElement : public CShadeListContainerElement
	{
		BATaskLocalNode nodeData;
	};
}