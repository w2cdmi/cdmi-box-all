#include "stdafxOnebox.h"
#include "CustomListUI.h"
#include "ListContainerElement.h"
#include "SkinConfMgr.h"
#include "Utility.h"
#include "SyncFileSystemMgr.h"
#include "UserContext.h"
#include "PathMgr.h"
#include "ItemCheckBox.h"
#include "UICommonDefine.h"
using namespace SD::Utility;

namespace Onebox
{
	CCustomListUI::CCustomListUI()
		:m_iDelayDeltaY(0)
		, m_iDelayNumber(0)
		, m_iDelayLeft(0)
		, m_pDragDialog(NULL)
		, m_isDragable_(true)
	{
		clearDropFile();		
	}

	PVOID CCustomListUI::GetInterface(LPCTSTR pstrName)
	{
		if( _tcscmp(pstrName, _T("CCustomListUI")) == 0 ) return this;
		if( _tcscmp(pstrName, _T("IListOwner")) == 0 ) return static_cast<IListOwnerUI*>(this);
		return CListUI::GetInterface(pstrName);
	}
	/*
	void CCustomListUI::resumeLastRename()
	{		
		if(-1==m_iCurSel)
		{
			return;
		}
		CControlUI* pControl = GetItemAt(m_iCurSel);
		if( pControl == NULL) return;
		CContainerUI* pListItem = static_cast<CContainerUI*>(pControl->GetInterface(DUI_CTR_CONTAINER));
		if( pListItem == NULL ) return;
		CRichEditUI* creui = static_cast<CRichEditUI*>(pListItem->FindSubControlsByClass(DUI_CTR_RICHEDITUI));
		if (NULL == creui) return;
		creui->SetBorderSize(0);
		creui->SetMouseEnabled(false);
		pListItem->SetMouseChildEnabled(false);
		m_pManager->SendNotify(pControl,DUI_MSGTYPE_RETURN);
	}*/

	void CCustomListUI::RemoveAll()
	{
		m_iCurSel = -1;
		m_selects.Empty();
		CListUI::RemoveAll();
		selectChanged();
	}
	
	double CCustomListUI::CalculateDelay(double state)
	{
		return pow(state, 2);
	}

	void CCustomListUI::DoEvent(TEventUI& event)
	{
		if( !IsMouseEnabled() && event.Type > UIEVENT__MOUSEBEGIN && event.Type < UIEVENT__MOUSEEND ) {
			if( m_pParent != NULL ) m_pParent->DoEvent(event);
			else CListUI::DoEvent(event);
			return;
		}

		switch( event.Type ) {
		case  UIEVENT_TIMER: 
			{
				if (event.wParam == UI_TIMERID::SCROLL_TIMERID)
				{
					if (m_iDelayLeft > 0)
					{
						--m_iDelayLeft;
						SIZE sz = GetScrollPos();
						LONG lDeltaY =  (LONG)(CalculateDelay((double)m_iDelayLeft / m_iDelayNumber) * m_iDelayDeltaY);
						if ((lDeltaY > 0 && sz.cy != 0)  || (lDeltaY < 0 && sz.cy != GetScrollRange().cy ))
						{
							sz.cy -= lDeltaY;
							SetScrollPos(sz);
							return;
						}
					}
					m_iDelayDeltaY = 0;
					m_iDelayNumber = 0;
					m_iDelayLeft = 0;
					m_pManager->KillTimer(this, UI_TIMERID::SCROLL_TIMERID);
					return;
				}
			}			
			break;
		case  UIEVENT_SCROLLWHEEL:
			{
				LONG lDeltaY = 0;
				if (m_iDelayNumber > 0)
					lDeltaY =  (LONG)(CalculateDelay((double)m_iDelayLeft / m_iDelayNumber) * m_iDelayDeltaY);
				switch (LOWORD(event.wParam))
				{
				case SB_LINEUP:
					if (m_iDelayDeltaY >= 0)
						m_iDelayDeltaY = lDeltaY + 8;
					else
						m_iDelayDeltaY = lDeltaY + 12;
					break;
				case SB_LINEDOWN:
					if (m_iDelayDeltaY <= 0)
						m_iDelayDeltaY = lDeltaY - 8;
					else
						m_iDelayDeltaY = lDeltaY - 12;
					break;
				}
				if (m_iDelayDeltaY > 100) 
					m_iDelayDeltaY = 100;
				else if
					(m_iDelayDeltaY < -100) m_iDelayDeltaY = -100;

				m_iDelayNumber = (DWORD)sqrt((double)abs(m_iDelayDeltaY)) * 5;
				m_iDelayLeft = m_iDelayNumber;
				m_pManager->SetTimer(this, UI_TIMERID::SCROLL_TIMERID, 50U);
				return;
			}
			break;
		case UIEVENT_BUTTONDOWN:
			{
				//click blank:unselect all
				SelectItem(-1);
			}
			break;	
		case UIEVENT_MOUSEMOVE:	
			{
				if ( MOUSE_MOVE_TYPE_SELECT == GetMouseMoveType() )
				{
					//SelectItem(-1);
				}
				else if( MOUSE_MOVE_TYPE_DRAG == GetMouseMoveType() )
				{
					if(!IsDropEnabled()) return;
					if(!m_isDragable_) return;

					std::wstring listItemName = GetName().GetData();
					if(event.wParam == Event_ChildSend && m_pDragDialog == NULL)
					{
						std::vector<ItemInfo> info;
						for (int i = 0; i < m_selects.GetSize(); ++i)
						{
							CShadeListContainerElement* pm = static_cast<CShadeListContainerElement*>(GetItemAt(*(int*)m_selects[i]));
							if (pm == NULL) continue;
							ItemInfo tmp;
							tmp.fileName = pm->m_uNodeData.basic.name;
							tmp.fileIcon = SkinConfMgr::getInstance()->getIconPath(pm->m_uNodeData.basic.type, pm->m_uNodeData.basic.name);
							info.push_back(tmp);
						}

						m_pDragDialog = new DragDialog;
						m_pDragDialog->Init(L"DragDialog.xml",m_pManager->GetPaintWindow(),event.ptMouse,info);
						m_pDragDialog->SetTransparent(50);
						m_pDragDialog->SetWindowShow(false);
						m_pDragDialog->Add(info);
					}

					if(m_pDragDialog != NULL)
					{
						bool isSelect = false;
						POINT pt=event.ptMouse;
						RECT re = this->GetPos();
						if(pt.x < re.left)    pt.x = re.left;
						if(pt.x > re.right)   pt.x = re.right;
						if(pt.y < re.top)     pt.y = re.top;
						if(pt.y > re.bottom)  pt.y = re.bottom;
						CStdValArray* selects = GetSelects();
						int iSelectIndex = -1;
						CControlUI* cSelectCtl = NULL;
						for (int i = 0; i < selects->GetSize(); ++i)
						{
							if(NULL==selects->GetAt(i)) continue;
							iSelectIndex = *(int*)selects->GetAt(i);
							cSelectCtl = GetItemAt(iSelectIndex);
							if (NULL == cSelectCtl) continue;
							CShadeListContainerElement* pm = static_cast<CShadeListContainerElement*>(cSelectCtl);

							RECT rc = pm->GetPos();
							if(PtInRect(&rc, event.ptMouse))
							{
								isSelect = true;
								break;
							}
						}
						m_pDragDialog->SetDialogPos(pt);
						if (selects->GetSize() != 0)
						{
							if (!isSelect)
							{
								m_pDragDialog->SetWindowShow(true);
							}
							else
							{
								m_pDragDialog->SetWindowShow(false);
							}
						}					
					}
				}
			}
			break;
		case UIEVENT_BUTTONUP:
			{	
				if(NULL != m_pDragDialog && MOUSE_MOVE_TYPE_DRAG == GetMouseMoveType() )
				{
					m_pDragDialog->SetWindowShow(false);
					m_pDragDialog->Close();
					m_pDragDialog = NULL;
					BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000);
					if(bCtrl)
					{
						m_pManager->SendNotify(this, DUI_MSGTYPE_ITEMDRAGCOPY);
					}
					else if(-1!=m_iCurSel)
					{						
						m_pManager->SendNotify(this, DUI_MSGTYPE_ITEMDRAGMOVE);
					}
				}
			}
			break;
		case UIEVENT_KEYDOWN:
			{
				if( event.chKey  == VK_DELETE)
				{
					m_pManager->SendNotify(this, DUI_MSGTYPE_DELETE);
					return;
				}
				if (event.chKey == 0x41)
				{
					BOOL bCtrl = (GetKeyState(VK_CONTROL) & 0x8000);
					if (bCtrl)
					{
						m_pManager->SendNotify(this, DUI_MSGTYPE_LISTSELECTALL);
					}
				}
				if (event.chKey == VK_BACK)
				{
					m_pManager->SendNotify(this, DUI_MSGTYPE_BACK);
				}
			}
			break;
		}
		CListUI::DoEvent(event);
	}

	void  CCustomListUI::OnDragEnter( IDataObject *pDataObj, DWORD grfKeyState, POINT pt,  DWORD *pdwEffect)
	{	
		if (NULL == pDataObj) return;
		if (m_pDragDialog == NULL)
		{
			m_dropFiles.clear();
			HGLOBAL hgloba = NULL;
			HDROP hDrop = NULL;
			//WORD wNumFilesDropped =0;  
			WORD wPathnameSize = 0;  
			WCHAR *pFilePathName = NULL;  
			IEnumFORMATETC *etc;
			FORMATETC etc2;
			STGMEDIUM stg;
			pDataObj->EnumFormatEtc(DATADIR_GET,&etc);
			ULONG count;
			int res = etc->Reset();
			while (res == S_OK)
			{
				res = etc->Next(1,&etc2,&count);
				if(res == S_OK)
				{
					if (etc2.cfFormat == CF_HDROP)
					{
						pDataObj->GetData(&etc2,&stg);
						hgloba = stg.hGlobal;
					}
				}
			}	

			if (hgloba != NULL)
			{
				hDrop = (HDROP)hgloba;
				(void)DragQueryFile(hDrop, -1, NULL, 0);  

				/*for (WORD i=0;i<wNumFilesDropped;i++)
				{  
				wPathnameSize = DragQueryFile(hDrop, i, NULL, 0);  
				wPathnameSize++;  
				pFilePathName = new WCHAR[wPathnameSize];  
				if (NULL == pFilePathName)  
				{  
				_ASSERT(0);  
				DragFinish(hDrop);  
				return ;  
				}  

				::ZeroMemory(pFilePathName, wPathnameSize); 

				DragQueryFile(hDrop, i, pFilePathName, wPathnameSize);
				m_dropFiles.push_back(pFilePathName);

				delete[] pFilePathName;
				pFilePathName = NULL;
				}  		
				DragFinish(hDrop);*/
			}
			//if (m_dropFiles.empty())   return;

			std::vector<ItemInfo> info;
			ItemInfo tmp;
			info.push_back(tmp);
			/*for(std::list<std::wstring>::iterator it = m_dropFiles.begin(); it != m_dropFiles.end(); ++it)
			{							
			ItemInfo tmp;
			tmp.fileName = *it;
			tmp.fileIcon = SkinConfMgr::getInstance()->getIconPath(FILE_TYPE_DIR, *it);
			info.push_back(tmp);
			break;
			}*/
			
			m_pDragDialog = new DragDialog;
			m_pDragDialog->Init(L"DragDialog.xml",m_pManager->GetPaintWindow(),pt,info);
			m_pDragDialog->SetTransparent(255);
			m_pDragDialog->Add(info);
			m_pDragDialog->SetWindowShow(false);
		}	

		::SendMessage(m_pManager->GetPaintWindow(),WM_MOUSEMOVE,(WPARAM)-1,(LPARAM)MAKELPARAM(pt.x,pt.y));
		CControlUI::OnDragEnter(pDataObj,grfKeyState,pt,pdwEffect);	
	}

	void CCustomListUI::OnDragOver(DWORD grfKeyState, POINT pt,DWORD *pdwEffect)
	{
		::SendMessage(m_pManager->GetPaintWindow(),WM_MOUSEMOVE,(WPARAM)-1,(LPARAM)MAKELPARAM(pt.x,pt.y));
		CControlUI::OnDragOver(grfKeyState,pt,pdwEffect);
	}

	void CCustomListUI::OnDragLeave()
	{
		if (m_pDragDialog != NULL)
		{
			m_pDragDialog->Close();
			m_pDragDialog = NULL;
		}
		CControlUI::OnDragLeave();
	}

	void CCustomListUI::OnDrop(IDataObject *pDataObj, DWORD grfKeyState, POINT pt, DWORD *pdwEffect)
	{
		if (m_pDragDialog != NULL)
		{
			m_pDragDialog->Close();
			m_pDragDialog = NULL;
		}

		m_dropFiles.clear();
		HGLOBAL hgloba = NULL;
		HDROP hDrop = NULL;
		WORD wNumFilesDropped =0;  
		WORD wPathnameSize = 0;  
		WCHAR *pFilePathName = NULL;  
		IEnumFORMATETC *etc;
		FORMATETC etc2;
		STGMEDIUM stg;
		pDataObj->EnumFormatEtc(DATADIR_GET,&etc);
		ULONG count;
		int res = etc->Reset();
		while (res == S_OK)
		{
			res = etc->Next(1,&etc2,&count);
			if(res == S_OK)
			{
				if (etc2.cfFormat == CF_HDROP)
				{
					pDataObj->GetData(&etc2,&stg);
					hgloba = stg.hGlobal;
				}
			}
		}	

		if (hgloba != NULL)
		{
			hDrop = (HDROP)hgloba;
			wNumFilesDropped = DragQueryFile(hDrop, -1, NULL, 0);  

			for (WORD i=0;i<wNumFilesDropped;i++)
			{  
				wPathnameSize = DragQueryFile(hDrop, i, NULL, 0);  
				wPathnameSize++;  
				pFilePathName = new WCHAR[wPathnameSize];  
				if (NULL == pFilePathName)  
				{  
					_ASSERT(0);  
					DragFinish(hDrop);  
					return ;  
				}  

				::ZeroMemory(pFilePathName, wPathnameSize); 

				DragQueryFile(hDrop, i, pFilePathName, wPathnameSize);
				m_dropFiles.push_back(pFilePathName);

				delete[] pFilePathName;
				pFilePathName = NULL;
			}  		
			DragFinish(hDrop);
		}
		if (m_dropFiles.empty())   return;

		m_pManager->SendNotify(this, DUI_MSGTYPE_ITEMDRAGFILE);
	}

	std::list<std::wstring> CCustomListUI::getDropFile()
	{
		return m_dropFiles;
	}

	void CCustomListUI::clearDropFile()
	{
		m_dropFiles.clear();
	}

	void CCustomListUI::selectChanged()
	{
		m_pManager->SendNotify(this, DUI_MSGTYPE_SELECTITEMCHANGED);
		CSelectallCheckBoxUI* copui = static_cast<CSelectallCheckBoxUI*>(this->FindSubControl(_T("selectall")));
		if (NULL != copui) copui->Selected(this->GetCount()!=0&&this->GetCount()==m_selects.GetSize());
	}

	void CCustomListUI::clearOtherSelected()
	{
		int index = -1;
		for (int i = 0; i < m_selects.GetSize(); ++i)
		{
			index = *(int*)m_selects[i];
			if(index==m_iCurSel)
			{
				continue;
			}
			CControlUI* pControl = GetItemAt(index);
			if( pControl == NULL) continue;
			IListItemUI* pListItem = static_cast<IListItemUI*>(pControl->GetInterface(DUI_CTR_LISTITEM));
			if( pListItem == NULL ) continue;
			pListItem->Select(false,false);
		}
		m_selects.Empty();
		m_selects.Add(&m_iCurSel);
		m_pManager->SendNotify(this, DUI_MSGTYPE_SELECTALL, false);
		selectChanged();
	}

	void CCustomListUI::SelectAllItem(bool bCheck)
	{
		//if (bCheck) clearOtherSelected();
		m_selects.Empty();
		for (int i = 0; i < this->GetCount(); i++)
		{
			if (bCheck) m_selects.Add(&i);
			CControlUI* pControl = GetItemAt(i);
			if( NULL == pControl)return;
			IListItemUI* pListItem = static_cast<IListItemUI*>(pControl->GetInterface(DUI_CTR_LISTITEM));
			if( pListItem != NULL ) pListItem->Select(bCheck,false);
		}
		m_iCurSel = (m_selects.GetSize()==1)?(*(int*)m_selects[0]):-1;
		selectChanged();
	}

	CStdValArray* CCustomListUI::GetSelects()
	{
		return &m_selects;
	}

	int CCustomListUI::GetCurSel() const
	{
		return m_iCurSel;
	}

	void CCustomListUI::clearDialog()
	{
		if (m_pDragDialog == NULL)  return;
		m_pDragDialog->Close();
		m_pDragDialog = NULL;
	}

	void CCustomListUI::setDragable(bool dragable)
	{
		m_isDragable_ = dragable;
	}

	bool CCustomListUI::isDragFileList()
	{
		if (m_pDragDialog == NULL)
		{
			return false;
		}
		return true;
	}
}
