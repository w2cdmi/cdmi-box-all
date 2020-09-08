
Var MSG
Var Dialog
Var BGImage
Var MiddleImage
Var ImageHandle
Var Btn_Next
Var Btn_Cancel
Var Bool_License
Var Btn_Close
Var WarningForm

Var Ck_ShortCut
Var Bool_ShortCut
Var Lbl_ShortCut

Var Ck_AutoRun
Var Bool_AutoRun
;Var Lbl_AutoRun

Var Ck_RunProgram
Var Bool_RunProgram
;Var Lbl_RunProgram

;Var Ck_FinishPage
Var Bool_FinishPage
;Var Lbl_FinishPage

Var Btn_Install

Var Txt_Browser
Var Btn_Browser

;Var Btn_DetailsPrint
Var UDetailsPrint   ;详细输入文字
Var Txt_DetailsPrint  ;详细文字框控件
Var Bool_DetailsPrint ;记录是否显示详细信息文本框

Var Lbl_InstallPlace
Var Lbl_FreeSpace
Var Lb1_SpaceSize

Var Global_SpesPath   ;Spes 安装路径
Var Global_RecordNetVerseion ;记录.netVar版本

Var Lbl_Installing
Var PB_ProgressBar

Var WebImg ;网页控件

;Var Ck_Weibo
Var Bool_Weibo
;Var Lbl_Weibo
Var Btn_Complete

Var Ischeckemtdir



;---------------------------全局编译脚本预定义的常量-----------------------------------------------------
!define  EM_BrandingText "Onebox"
; MUI 预定义常量
!define MUI_ABORTWARNING
;安装图标的路径名字
!define MUI_ICON "images\fav.ico"
;卸载图标的路径名字
!define MUI_UNICON "images\uninstall.ico"
;产品协议书的路径名字
;!define MUI_PAGE_LICENSE_RTY "license\license.rtf"
;!define EM_OUTFILE_NAME "Onebox_Installer.exe"

!define PRODUCT_NAME "Onebox"
!define PRODUCT_EXECUTE_NAME  "Onebox.exe"
!define PRODUCT_VERSION 1.0.0.0
!define PRODUCT_PUBLISHER "chinasoft company, Inc."
!define PRODUCT_WEB_SITFE "http://nshelp.huawei.com"
!define PRODUCT_DIR_REGKEY "Software\Microsoft\Windows\CurrentVersion\App Paths\${PRODUCT_EXECUTE_NAME}" ;请更改
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"

BrandingText "Onebox, A desktop managment software"
; Language Selection Dialog Settings
!define MUI_LANGDLL_REGISTRY_ROOT "${PRODUCT_UNINST_ROOT_KEY}"
!define MUI_LANGDLL_REGISTRY_KEY "${PRODUCT_UNINST_KEY}"
!define MUI_LANGDLL_REGISTRY_VALUENAME "NSIS:Language"
!define PRODUCT_Onebox_KEY  "SOFTWARE\chinasoft\Onebox"
!define /math PBM_SETRANGE32 ${WM_USER} + 6

;Config Files and Resource Files
!define CONF_ONEBOX_CONFIGFILE "Config.ini"

;Version Information
VIProductVersion ${PRODUCT_VERSION}
VIAddVersionKey /LANG=1033 "ProductName" "Onebox"
VIAddVersionKey /LANG=1033 "Comments" "Huawei desktop management application"
VIAddVersionKey /LANG=1033 "CompanyName" "chinasoft Technologies"
VIAddVersionKey /LANG=1033 "LegalTrademarks" "Huawei Technologies"
VIAddVersionKey /LANG=1033 "LegalCopyright" "chinasoft Technologies Co., Ltd. 2014-2047. All rights reserved"
VIAddVersionKey /LANG=1033 "FileDescription" "Onebox client"
VIAddVersionKey /LANG=1033 "OriginalFilename" "Onebox"
VIAddVersionKey /LANG=1033 "InternalName" "Onebox"
VIAddVersionKey /LANG=1033 "FileVersion" ${PRODUCT_VERSION}


;---------------------------设置软件压缩类型（也可以通过外面编译脚本控制）------------------------------------
SetCompressor lzma
BrandingText "${EM_BrandingText}"
SetCompress force
XPStyle on
; ------ MUI 现代界面定义 (1.67 版本以上兼容) ------
!include "MUI2.nsh"
!include "WinCore.nsh"
;引用文件函数头文件
!include "FileFunc.nsh"
!include "nsWindows.nsh"
!include "LoadRTF.nsh"
!include "WinMessages.nsh"
!include "LogicLib.nsh"
!include "WordFunc.nsh"
!include "StrFunc.nsh"
!include "x64.nsh"

${StrRep}
${UnStrRep}
;!include "ProcFunc.nsh"
!define MUI_CUSTOMFUNCTION_GUIINIT onGUIInit
;!define MUI_LANGDLL_REGISTRY_VALUENAME "NSIS:Language"
;自定义页面
Page custom WelcomePage
;Page custom InstallationPage
;PageEx InstallationPage
  ;DirVerify leave
 ; PageCallbacks "" "" dirLeave
;PageExEnd
Page custom LoadingPage
Page custom CompletePage

; 许可协议页面
;!define MUI_LICENSEPAGE_CHECKBOX

; 安装目录选择页面
;!insertmacro MUI_PAGE_DIRECTORY
; 安装过程页面
;!insertmacro MUI_PAGE_INSTFILES
; 安装完成页面
;!insertmacro MUI_PAGE_FINISH
; 安装卸载过程页面
!insertmacro MUI_UNPAGE_INSTFILES
; 安装界面包含的语言设置
!insertmacro MUI_LANGUAGE "SimpChinese"
!insertmacro MUI_LANGUAGE "English"
;!define MUI_LANGDLL_REGISTRY_VALUENAME "NSIS:Language"
;!insertmacro un.WordFind
;!insertmacro VersionCompare
;!insertmacro GetTime
; MUI Settings
;!define MUI_ABORTWARNING
RequestExecutionLevel admin
;------------------------------------------------------MUI 现代界面定义以及函数结束------------------------
;应用程序显示名字
;Name "Onebox"
;应用程序输出路径
;OutFile "${EM_OUTFILE_NAME}"
;InstallDir "$PROGRAMFILES\Onebox"
Name "${PRODUCT_NAME} V${PRODUCT_VERSION}"
OutFile "Onebox_V${PRODUCT_VERSION}_Setup.exe"  ;请更改输出安装包文件名，可以指定输出到某目录
InstallDirRegKey HKLM "${PRODUCT_DIR_REGKEY}" ""
InstallDir  "$PROGRAMFILES\\chinasoft\\Onebox\\${PRODUCT_VERSION}"
ShowInstDetails show
ShowUninstDetails show
;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "$INSTDIR"

Function .onInit
    System::Call 'kernel32::CreateMutexA(i 0,i 0,t "SMAPLE_MUTEX") i .r1 ?e'
      Pop $R0
      StrCmp $R0 0 +2
        Abort

    InitPluginsDir
      Call checkOSVersion
      Call checkIsCloudMachine
      
      var /GLOBAL  PREVIOUSINSTDIR
      var /GLOBAL  PREVIOUSVERSION
      
      ReadRegStr $PREVIOUSVERSION  HKLM "${PRODUCT_Onebox_KEY}\Setting" "MainVersion"
      ReadRegStr  $PREVIOUSINSTDIR  HKLM   "SOFTWARE\chinasoft\Onebox\Setting"  "AppPath"
      ${If} $PREVIOUSINSTDIR == ""
        ReadRegStr  $PREVIOUSINSTDIR  HKLM   "SOFTWARE\chinasoft\CloudDrive\Setting"  "AppPath"
      ${Endif}
      
      Call GetSpesPath
      ${If} $PREVIOUSINSTDIR == ""
        StrCpy $INSTDIR "$PROGRAMFILES\chinasoft\Onebox\${PRODUCT_VERSION}"
      ${Else}
        
        IfFileExists $Global_SpesPath 0 +3
        StrCpy $INSTDIR "$PROGRAMFILES\chinasoft\Onebox\${PRODUCT_VERSION}"
        Goto Next
        StrLen $R1 ${PRODUCT_VERSION}
        StrLen $R2 $PREVIOUSINSTDIR
        Intop $R3 $R2 - $R1
        StrCpy $R4 $PREVIOUSINSTDIR $R3
        StrCpy $INSTDIR  $R4${PRODUCT_VERSION}
        ;Messagebox MB_OK $INSTDIR
      ${Endif}
      
      Next:
      ;MessageBox MB_OK    "--------------$INSTDIR--------------"
      SetShellVarContext all
      SetOutPath "$APPDATA\Onebox\Tools"
        File /oname=TerminateProcess.exe ..\TerminateProcess.exe
      
      LogSet on
      StrCpy $Bool_ShortCut 1


       ;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "$PROGRAMFILES"
      ;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "$INSTDIR"
     ; MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "$PLUGINSDIR"
           ;File `/ONAME=$PLUGINSDIR\TerminateProcess.exe` `..\TerminateProcess.exe`
      IfFileExists  "$EXEDIR\${CONF_ONEBOX_CONFIGFILE}" 0 +3
      CopyFiles /SILENT   "$EXEDIR\${CONF_ONEBOX_CONFIGFILE}"  "$PLUGINSDIR\${CONF_ONEBOX_CONFIGFILE}"
      Goto Next1
      File `/ONAME=$PLUGINSDIR\Config.ini` `..\Config.ini`
      Next1:
     ${If} $LANGUAGE == 2052
           File `/ONAME=$PLUGINSDIR\bg.bmp` `images\bg.bmp`
           File `/ONAME=$PLUGINSDIR\quit.bmp` `images\quit.bmp`
           File `/ONAME=$PLUGINSDIR\btn_next.bmp` `images\btn_next.bmp`
           File `/oname=$PLUGINSDIR\btn_cancel.bmp` `images\btn_cancel.bmp`
           File `/oname=$PLUGINSDIR\btn_quit.bmp` `images\btn_quit.bmp`
           File `/oname=$PLUGINSDIR\btn_install.bmp` `images\btn_install.bmp`
           File `/oname=$PLUGINSDIR\btn_change.bmp` `images\btn_change.bmp`
           File `/oname=$PLUGINSDIR\installation.bmp` `images\installation.bmp`
           File `/oname=$PLUGINSDIR\success.bmp` `images\success.bmp`
           File `/oname=$PLUGINSDIR\btn_complete.bmp` `images\btn_complete.bmp`
           File `/oname=$PLUGINSDIR\loading_pic1.bmp` `images\loading_pic1.bmp`
           File `/oname=$PLUGINSDIR\loading_pic2.bmp` `images\loading_pic2.bmp`
           File `/oname=$PLUGINSDIR\loading_pic3.bmp` `images\loading_pic3.bmp`
           File `/oname=$PLUGINSDIR\index.htm` `images\index.htm`
           SkinBtn::Init "$PLUGINSDIR\btn_quit.bmp"
           SkinBtn::Init "$PLUGINSDIR\btn_next.bmp"
           SkinBtn::Init "$PLUGINSDIR\btn_cancel.bmp"
           SkinBtn::Init "$PLUGINSDIR\btn_install.bmp"
	     SkinBtn::Init "$PLUGINSDIR\btn_change.bmp"
	     SkinBtn::Init "$PLUGINSDIR\btn_complete.bmp"
      ${Else}
           File `/ONAME=$PLUGINSDIR\bg.bmp` `images\bg_en.bmp`
           File `/ONAME=$PLUGINSDIR\quit.bmp` `images\quit_en.bmp`
           File `/ONAME=$PLUGINSDIR\btn_next.bmp` `images\btn_next_en.bmp`
           File `/oname=$PLUGINSDIR\btn_cancel.bmp` `images\btn_cancel_en.bmp`
           File `/oname=$PLUGINSDIR\btn_quit.bmp` `images\btn_quit_en.bmp`
           File `/oname=$PLUGINSDIR\btn_install.bmp` `images\btn_install_en.bmp`
           File `/oname=$PLUGINSDIR\btn_change.bmp` `images\btn_change_en.bmp`
           File `/oname=$PLUGINSDIR\installation.bmp` `images\installation_en.bmp`
           File `/oname=$PLUGINSDIR\success.bmp` `images\success_en.bmp`
           File `/oname=$PLUGINSDIR\btn_complete.bmp` `images\btn_complete_en.bmp`
           File `/oname=$PLUGINSDIR\loading_pic1_en.bmp` `images\loading_pic1_en.bmp`
           File `/oname=$PLUGINSDIR\loading_pic2_en.bmp` `images\loading_pic2_en.bmp`
           File `/oname=$PLUGINSDIR\loading_pic3_en.bmp` `images\loading_pic3_en.bmp`
           File `/oname=$PLUGINSDIR\index.htm` `images\index_en.htm`
           SkinBtn::Init "$PLUGINSDIR\btn_quit_en.bmp"
           SkinBtn::Init "$PLUGINSDIR\btn_next_en.bmp"
           SkinBtn::Init "$PLUGINSDIR\btn_cancel_en.bmp"
           SkinBtn::Init "$PLUGINSDIR\btn_install_en.bmp"
	     SkinBtn::Init "$PLUGINSDIR\btn_change_en.bmp"
	     SkinBtn::Init "$PLUGINSDIR\btn_complete_en.bmp"
      ${Endif}

    
    ;File `/ONAME=$PLUGINSDIR\select.bmp` `images\select.bmp`
   ;File `/ONAME=$PLUGINSDIR\welcome.bmp` `images\welcome.bmp`
		
;		File `/oname=$PLUGINSDIR\btn_agreement1.bmp` `images\btn_agreement1.bmp`
;    File `/oname=$PLUGINSDIR\btn_agreement2.bmp` `images\btn_agreement2.bmp`
    ;File `/oname=$PLUGINSDIR\license.rtf` `license\license.rtf`
    File `/oname=$PLUGINSDIR\btn_close.png` `images\btn_close.bmp`
    
    File `/oname=$PLUGINSDIR\checkbox1.bmp` `images\checkbox1.bmp`
    File `/oname=$PLUGINSDIR\checkbox2.bmp` `images\checkbox2.bmp`
    
   	
   	
   	
   	
    ;File `/oname=$PLUGINSDIR\loading_pic4.bmp` `images\loading_pic4.bmp`
   	
   	
   	File `/oname=$PLUGINSDIR\loading1.bmp` `images\loading1.bmp`
    File `/oname=$PLUGINSDIR\loading2.bmp` `images\loading2.bmp`
    

    
		
	;	SkinBtn::Init "$PLUGINSDIR\btn_agreement1.bmp"
	;	SkinBtn::Init "$PLUGINSDIR\btn_agreement2.bmp"
		SkinBtn::Init "$PLUGINSDIR\btn_close.bmp"
		
		SkinBtn::Init "$PLUGINSDIR\checkbox1.bmp"
		SkinBtn::Init "$PLUGINSDIR\checkbox2.bmp"
		

		
		
		;LangString DirISEmpt ${LANG_SIMPCHINESE} "安装目录不为空，是否继续安装！"
           ; LangString DirISEmpt ${LANG_ENGLISH} "The diretory is not empty , Is Contiune?"
    ;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "$R0"
;检测安装程序
        ;Pop $R0
        ${GetParameters} $R0
        ${If} $R0 == "/U"
          Call UpdateProc
          Quit
        ${Else}
          ${If} $R0 == "/F"
            Call ForceUpdateProc
            Quit
          ${Else}
            FindProcDLL::FindProc  "Onebox.exe"
            ifsilent silentInit   UnSilentInit
            UnSilentInit:
              StrCmp $R0 1 0 no_run
              ${If} $LANGUAGE == 2052
                MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "安装程序检测到 Onebox 正在运行，是否结束应用程序继续安装！" IDYES Continue  IDNO No_Continue
              ${Else}
      		      MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "The installation program detects that Onebox is running. Terminate the program and continue the installation?" IDYES Continue  IDNO No_Continue
              ${Endif}
              No_Continue:
                Quit
              Continue:
                Call StopRunningProcess
                Goto Done
              no_run:
                Pop $R0
                Goto Done
            silentInit:
              Call  InstallationMainFun
            ;Quit
                Done:
          ${Endif}
        ${Endif}
FunctionEnd

Function onGUIInit
    ;消除边框
    System::Call `user32::SetWindowLong(i$HWNDPARENT,i${GWL_STYLE},0x9480084C)i.R0`
    ;隐藏一些既有控件
    GetDlgItem $0 $HWNDPARENT 1034
    ShowWindow $0 ${SW_HIDE}
    GetDlgItem $0 $HWNDPARENT 1035
    ShowWindow $0 ${SW_HIDE}
    GetDlgItem $0 $HWNDPARENT 1036
    ShowWindow $0 ${SW_HIDE}
    GetDlgItem $0 $HWNDPARENT 1037
    ShowWindow $0 ${SW_HIDE}
    GetDlgItem $0 $HWNDPARENT 1038
    ShowWindow $0 ${SW_HIDE}
    GetDlgItem $0 $HWNDPARENT 1039
    ShowWindow $0 ${SW_HIDE}
    GetDlgItem $0 $HWNDPARENT 1256
    ShowWindow $0 ${SW_HIDE}
    GetDlgItem $0 $HWNDPARENT 1028
    ShowWindow $0 ${SW_HIDE}
FunctionEnd


;解决空目录不能安装问题。
Function dirLeave
    ;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "Here4"
    StrCpy $Ischeckemtdir 1
	${GetSize} "$INSTDIR" "/S=0M"  $0  $1  $2
  IntCmp $1 0 NEXT 0 RESel
  RESel:
      IfSilent NEXT
        ${If} $LANGUAGE == 2052
          MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "安装目录不为空，是否继续安装！" IDYES Continue  IDNO No_Continue
        ${Else}
      	  MessageBox	MB_YESNO|MB_OK|MB_ICONQUESTION|MB_TOPMOST "The diretory is not empty , Is Contiune?"  IDYES Continue  IDNO No_Continue
        ${Endif}
      ;MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "$DirISEmpt" IDYES Continue  IDNO No_Continue
      Continue:
        ;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "Here4"
        Goto NEXT
      No_Continue:
        ;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "Here5"
        Call onClickClose
  NEXT:
      Call checkifinstall
FunctionEnd


;处理无边框移动
Function onGUICallback
  ${If} $MSG = ${WM_LBUTTONDOWN}
    SendMessage $HWNDPARENT ${WM_NCLBUTTONDOWN} ${HTCAPTION} $0
  ${EndIf}
FunctionEnd

Function onWarningGUICallback
  ${If} $MSG = ${WM_LBUTTONDOWN}
    SendMessage $WarningForm ${WM_NCLBUTTONDOWN} ${HTCAPTION} $0
  ${EndIf}
FunctionEnd


;-----------------------------------------皮肤贴图方法----------------------------------------------------
Function SkinBtn_Next
  SkinBtn::Set /IMGID=$PLUGINSDIR\btn_next.bmp $1
FunctionEnd

Function SkinBtn_Agreement1
  SkinBtn::Set /IMGID=$PLUGINSDIR\btn_agreement1.bmp $1
FunctionEnd

Function SkinBtn_Agreement2
  SkinBtn::Set /IMGID=$PLUGINSDIR\btn_agreement2.bmp $1
FunctionEnd

Function SkinBtn_Close
  SkinBtn::Set /IMGID=$PLUGINSDIR\btn_close.png $1
FunctionEnd

Function SkinBtn_Cancel
  SkinBtn::Set /IMGID=$PLUGINSDIR\btn_cancel.bmp $1
FunctionEnd

Function SkinBtn_Checked
  SkinBtn::Set /IMGID=$PLUGINSDIR\checkbox2.bmp $1
FunctionEnd

Function SkinBtn_UnChecked
  SkinBtn::Set /IMGID=$PLUGINSDIR\checkbox1.bmp $1
FunctionEnd

Function SkinBtn_Quit
  SkinBtn::Set /IMGID=$PLUGINSDIR\btn_quit.bmp $1
FunctionEnd

Function SkinBtn_Install
  SkinBtn::Set /IMGID=$PLUGINSDIR\btn_install.bmp $1
FunctionEnd

Function SkinBtn_Browser
  SkinBtn::Set /IMGID=$PLUGINSDIR\btn_change.bmp $1
FunctionEnd

;Function SkinBtn_DetailsPrint1
;  SkinBtn::Set /IMGID=$PLUGINSDIR\btn_installlist1.bmp $1
;FunctionEnd

;Function SkinBtn_DetailsPrint2
;  SkinBtn::Set /IMGID=$PLUGINSDIR\btn_installlist2.bmp $1
;FunctionEnd

Function SkinBtn_Complete
	SkinBtn::Set /IMGID=$PLUGINSDIR\btn_complete.bmp $1
FunctionEnd

Function OnClickQuitOK
	Call onClickClose
FunctionEnd

Function OnClick_CheckShortCut
  ${IF} $Bool_ShortCut == 1
		IntOp $Bool_ShortCut $Bool_ShortCut - 1
		StrCpy $1 $Ck_ShortCut
		;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "UnChecked"
		Call SkinBtn_UnChecked
	${ELSE}
		IntOp $Bool_ShortCut $Bool_ShortCut + 1
		StrCpy $1 $Ck_ShortCut
		;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "Checked"
		Call SkinBtn_Checked
	${EndIf}
FunctionEnd

;Function OnClick_CheckAutoRun
  ;${IF} $Bool_AutoRun == 1
	;	IntOp $Bool_AutoRun $Bool_AutoRun - 1
	;	StrCpy $1 $Ck_AutoRun
	;	Call SkinBtn_UnChecked
	;${ELSE}
	;	IntOp $Bool_AutoRun $Bool_AutoRun + 1
	;	StrCpy $1 $Ck_AutoRun
	;	Call SkinBtn_Checked
	;${EndIf}
;FunctionEnd

;Function OnClick_CheckRunProgram
  ;${IF} $Bool_RunProgram == 1
	;	IntOp $Bool_RunProgram $Bool_RunProgram - 1
		;StrCpy $1 $Ck_RunProgram
		;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "UnChecked"
		;Call SkinBtn_UnChecked
	;${ELSE}
	;	IntOp $Bool_RunProgram $Bool_RunProgram + 1
	;	StrCpy $1 $Ck_RunProgram
		;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "Checked"
	;	Call SkinBtn_Checked
	;${EndIf}
;FunctionEnd



;点击右上角关闭按钮
Function onClickClose
    Sleep 200
    Var /GLOBAL WaitCountexit
     Var /GLOBAL ToolsPath

    SetShellVarContext all
    IfFileExists $INSTDIR\Tools\TerminateProcess.exe 0 +3
      StrCpy  $ToolsPath $INSTDIR\Tools\TerminateProcess.exe
      Goto Retry
    IfFileExists $APPDATA\Onebox\Tools\TerminateProcess.exe 0 +2
      StrCpy  $ToolsPath $APPDATA\Onebox\Tools\TerminateProcess.exe
      Goto Retry
    Retry:
    ;MessageBox MB_OK  $ToolsPath
    ;中止正在运行的程序
 		;FindProcDLL::KillProc "Onebox_V${PRODUCT_VERSION}_Setup.exe"
    ;Exec '"taskkill" /f /im  Onebox_V${PRODUCT_VERSION}_Setup.exe'
    ExecWait '$ToolsPath "/title"  $\"${PRODUCT_NAME} V${PRODUCT_VERSION}*$\"'
 		Sleep 500
               FindWindow $0 "#32770" "" $HWNDPARENT
               StrCmp $0 0 killed notKilled
               ;检查StorageService.exe是否已经杀掉，如果没杀掉重复20次（杀掉了就可以往下进行）
               Killed:
                        Quit
               notKilled:
    	                   IntOp $WaitCountexit $WaitCountexit + 1
    	                   IntCmp $WaitCountexit 20 is20 lessthan20 morethan20
    	                   is20:
                                Sleep 1000
                                Goto Retry
    	                   lessthan20:
                                 Sleep 1000
                                 Goto Retry
                         morethan20:
                                 Quit
FunctionEnd

;处理页面跳转的命令
Function RelGotoPage
  IntCmp $R9 0 0 Move Move
    StrCmp $R9 "X" 0 Move
      StrCpy $R9 "120"
  Move:
  SendMessage $HWNDPARENT "0x408" "1" ""
FunctionEnd

;下一步按钮事件
Function onClickNext
      ;${IF} $Ischeckemtdir == 0
         ;Call  dirLeave
	;${EndIf}
	;Call CheckFreeSpace
  StrCpy $R9 1 ;Goto the next page
  Call RelGotoPage
  Abort
FunctionEnd

;点击安装按钮
Function OnClick_Install
	;StrCpy $R9 1 ;Goto the next page
    ;Call RelGotoPage
   ;MessageBox MB_OK "11111111111111"
   ;EnableWindow $Btn_Install 0
   
   ShowWindow $Lbl_InstallPlace ${SW_HIDE}
   ShowWindow $Txt_Browser ${SW_HIDE}
   ShowWindow $Btn_Browser ${SW_HIDE}
   ShowWindow $Lbl_FreeSpace ${SW_HIDE}
   ShowWindow $Lb1_SpaceSize ${SW_HIDE}
   ;ShowWindow $Lb1_PackageSize ${SW_HIDE}
   ShowWindow $Lbl_Installing ${SW_SHOW}
   ShowWindow $PB_ProgressBar ${SW_SHOW}

   ;ShowWindow $Btn_Install ${SW_HIDE}
   ;ShowWindow $Btn_Cancel ${SW_SHOW}
   EnableWindow $Btn_Install 0
   EnableWindow $Btn_Cancel 0
  
  GetFunctionAddress $0 NSD_TimerFun
  nsDialogs::CreateTimer $0 1
FunctionEnd

Function OnClickQuitCancel
  ${NSW_DestroyWindow} $WarningForm
  EnableWindow $hwndparent 1
  BringToFront
FunctionEnd

Function onCancel
	IsWindow $WarningForm Create_End
	!define Style ${WS_VISIBLE}|${WS_OVERLAPPEDWINDOW}
	${NSW_CreateWindowEx} $WarningForm $hwndparent ${ExStyle} ${Style} "" 1018

	${NSW_SetWindowSize} $WarningForm 349 184
	EnableWindow $hwndparent 0
	System::Call `user32::SetWindowLong(i$WarningForm,i${GWL_STYLE},0x9480084C)i.R0`
	${NSW_CreateButton} 148 122 88 25 ''
	Pop $R0
	StrCpy $1 $R0
	Call SkinBtn_Quit
	${NSW_OnClick} $R0 OnClickQuitOK

	${NSW_CreateButton} 248 122 88 25 ''
	Pop $R0
	StrCpy $1 $R0
	Call SkinBtn_Cancel
	${NSW_OnClick} $R0 OnClickQuitCancel

	${NSW_CreateBitmap} 0 0 100% 100% ""
  	Pop $BGImage
  ${NSW_SetImage} $BGImage $PLUGINSDIR\quit.bmp $ImageHandle
	GetFunctionAddress $0 onWarningGUICallback
	WndProc::onCallback $BGImage $0 ;处理无边框窗体移动
  ${NSW_CenterWindow} $WarningForm $hwndparent
	${NSW_Show}
	Create_End:
  ShowWindow $WarningForm ${SW_SHOW}
FunctionEnd

;更改目录事件
Function OnChange_DirRequest
	Pop $0
	;System::Call "user32::GetWindowText($Txt_Browser,t.r0,i${NSIS_MAX_STRLEN})"
	${NSD_GetText}  $Txt_Browser $0
	StrCpy $INSTDIR $0
	;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "$INSTDIR"
FunctionEnd

Function OnClick_BrowseButton
  Pop $0
  Pop $1
  Pop $2
  Pop $3
  Pop $4
  
  Var /GLOBAL DiskDescribe1
  Var /GLOBAL FreeSpace1
  Var /GLOBAL LastChar
  
  Push $INSTDIR ; input string "C:\Program Files\ProgramName"
  Call GetParent
  Pop $R0 ; first part "C:\Program Files"
  
  Push $INSTDIR ; input string "C:\Program Files\ProgramName"
  Push "\" ; input chop char
  Call GetLastPart
  Pop $R1 ; last part "ProgramName"

  nsDialogs::SelectFolderDialog "Please Select $R0 the derectory:" "$R0"
  Pop $0
  ${If} $0 == "error" # returns 'error' if 'cancel' was pressed?
    Return
  ${EndIf}
  ${If} $0 != ""
    StrCpy $LastChar $0 "" -1
    ${If} $LastChar == "\"
      StrCpy $INSTDIR "$0${PRODUCT_VERSION}"
    ${Else}
     StrCpy $INSTDIR "$0\${PRODUCT_VERSION}"
    ${EndIf}
    ;MessageBox MB_OK  $INSTDIR
    StrCpy $0 $INSTDIR 2
    ${StrFilter} "$0" "+" "" "" $1
    ${DriveSpace} $1 "/D=F /S=K" $2
    ${If} $2 < 1024
      StrCpy $FreeSpace1 "$2KB"
    ${EndIf}
    ${If} $2 > 1024
      ${DriveSpace} $1 "/D=F /S=M" $2
      StrCpy $FreeSpace1 "$2MB"
    ${EndIf}
    ;${If} $2 > 1024
      ;${DriveSpace} $1 "/D=F /S=G" $2
      ;StrCpy $FreeSpace1 "$2G"
    ;${EndIf}
    
    StrCpy $R0 "$0\"      ;Drive letter
	  StrCpy $R1 "invalid"
    ${GetDrives} "HDD" "FindHDD"
    ;MessageBox MB_OK "Type of drive $R0 is $R1"
    ${If} $R1 == "invalid"
      ${If} $LANGUAGE == 2052
        MessageBox MB_OK "所选路径不支持安装Onebox,请重新选择。"
        return
      ${Else}
        MessageBox MB_OK "Onebox cannot be installed under the selected path. Please select a path again."
        return
      ${EndIf}
    ${EndIf}
    
    StrCpy $3 $1 1
    ;StrCpy $4 "$3盘剩余空间:$FreeSpace1(所需空间:50M)"
     ${If} $LANGUAGE == 2052
      StrCpy $4 "$3盘剩余空间: $FreeSpace1 (所需空间: 50MB)"
    ${Else}
      StrCpy $4 "Space available on drive $3: $FreeSpace1 (Space needed: 50MB)"
    ${Endif}
    ;MessageBox MB_OK  $DiskDescribe1
    ;${NSD_SetText} $Lbl_FreeSpace $4
    system::Call `user32::SetWindowText(i $Txt_Browser, t "$INSTDIR")`
    system::Call `user32::SetWindowText(i $Lbl_FreeSpace, t "$4")`
    ShowWindow $Lbl_FreeSpace ${SW_HIDE}
    ShowWindow $Lbl_FreeSpace ${SW_SHOW}
  ${EndIf}
  
FunctionEnd

Function "FindHDD"

 ;获取查找到的驱动器盘符($9)可用空间(/D=F)单位兆(/S=M)
  StrCmp $9 $R0 0 +3
	StrCpy $R1 $8
	StrCpy $0 StopGetDrives

	Push $0

FunctionEnd

; Usage:
; Push "C:\Program Files\Directory\Whatever"
; Call GetParent
; Pop $R0 ; $R0 equal "C:\Program Files\Directory"
;得到选中目录用于拼接安装程序名称
Function GetParent
  Exch $R0 ; input string
  Push $R1
  Push $R2
  Push $R3
  StrCpy $R1 0
  StrLen $R2 $R0
  loop:
    IntOp $R1 $R1 + 1
    IntCmp $R1 $R2 get 0 get
    StrCpy $R3 $R0 1 -$R1
    StrCmp $R3 "\" get
    Goto loop
  get:
    StrCpy $R0 $R0 -$R1
    Pop $R3
    Pop $R2
    Pop $R1
    Exch $R0 ; output string
FunctionEnd

; Usage:
; Push $INSTDIR ; input string "C:\Program Files\ProgramName"
; Push "\" ; input chop char
; Call GetLastPart
; Pop $R1 ; last part "ProgramName"
;截取选中目录
Function GetLastPart
  Exch $0 ; chop char
  Exch
  Exch $1 ; input string
  Push $2
  Push $3
  StrCpy $2 0
  loop:
    IntOp $2 $2 - 1
    StrCpy $3 $1 1 $2
    StrCmp $3 "" 0 +3
      StrCpy $0 ""
      Goto exit2
    StrCmp $3 $0 exit1
    Goto loop
  exit1:
    IntOp $2 $2 + 1
    StrCpy $0 $1 "" $2
  exit2:
    Pop $3
    Pop $2
    Pop $1
    Exch $0 ; output string
FunctionEnd

;详细安装事件
;Function onClickDetailsPrint
   ; ${IF} $Bool_DetailsPrint == 1
  ;      ShowWindow $WebImg ${SW_SHOW}
	;	ShowWindow $Txt_DetailsPrint ${SW_HIDE}
	;	IntOp $Bool_DetailsPrint $Bool_DetailsPrint - 1
	;	StrCpy $1 $Btn_DetailsPrint
	;	Call SkinBtn_DetailsPrint1
	;${ELSE}
  ;      ShowWindow $WebImg ${SW_HIDE}
  ;      ShowWindow $Txt_DetailsPrint ${SW_SHOW}
	;	IntOp $Bool_DetailsPrint $Bool_DetailsPrint + 1
	;	StrCpy $1 $Btn_DetailsPrint
	;	Call SkinBtn_DetailsPrint2
	;${EndIf}
;FunctionEnd

Function NSD_TimerFun
    GetFunctionAddress $0 NSD_TimerFun
     
    nsDialogs::KillTimer $0
       ;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "Herekkk"
    !if 1   ;是否在后台运行,1有效
        GetFunctionAddress $0 InstallationMainFun
        BgWorker::CallAndWait
    !else
        Call InstallationMainFun
    !endif
FunctionEnd

;点击最后完成
Function onClickComplete
      ;搬迁
    SetShellVarContext all
      ;判断是否添加桌面快捷方式
    RMdir /r "$SMPROGRAMS\Onebox"
    CreateDirectory "$SMPROGRAMS\Onebox"
    CreateShortCut  "$SMPROGRAMS\Onebox\Onebox.lnk" "$INSTDIR\Onebox.exe"
    CreateShortCut  "$SMPROGRAMS\Onebox\Uninstall.lnk" "$INSTDIR\uninst.exe"
    CreateShortCut "$QUICKLAUNCH\${PRODUCT_NAME}.lnk" "$INSTDIR\${PRODUCT_EXECUTE_NAME}"
    Exec  "$INSTDIR\Onebox.exe"
    ifSilent Silentfinish NoSilentfinish
      Silentfinish:
        CreateShortCut  "$DESKTOP\Onebox.lnk" "$INSTDIR\Onebox.exe"
      NoSilentfinish:
        ${If} $Bool_ShortCut == 1
          CreateShortCut  "$DESKTOP\Onebox.lnk" "$INSTDIR\Onebox.exe"
        ${EndIf}
    Call onClickClose
FunctionEnd

;Function .onInstSuccess
 ;在安装完成触发，Do something here
 ;Exec $INSTDIR\Onebox.exe
  ;CreateShortCut  "$DESKTOP\Onebox.lnk" "$INSTDIR\${PRODUCT_NAME}.exe"
  	;此处会在安装完成触发，do something here
  ;WriteUninstaller "$INSTDIR\uninst.exe"
  ;WriteRegStr HKLM "${PRODUCT_DIR_REGKEY}" "" "$INSTDIR\Onebox"
 ; WriteRegStr HKLM "${PRODUCT_Onebox_KEY}\Setting" "AppPath" "$INSTDIR"
  ;IfSilent RegSilent  RegNoSilent
   ;   RegSilent:
   ;   WriteRegDWORD HKLM "${PRODUCT_Onebox_KEY}\Setting" "InstallType" 0x01
   ;   RegNoSilent:
   ;   WriteRegDWORD HKLM "${PRODUCT_Onebox_KEY}\Setting" "InstallType" 0x00
  ;WriteRegStr HKLM "${PRODUCT_Onebox_KEY}\Setting" "MainVersion"  "${PRODUCT_VERSION}"
  ;WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  ;WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
  ;WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayIcon" "$INSTDIR\Onebox.exe"
  ;WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  ;WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
  ;${GetTime} "" "L" $0 $1 $2 $3 $4 $5 $6

  ;RMdir /r "$SMPROGRAMS\Onebox"
  ;CreateDirectory "$SMPROGRAMS\Onebox"
  ;CreateShortCut  "$SMPROGRAMS\Onebox\Onebox.lnk" "$INSTDIR\${PRODUCT_NAME}.exe"
  ;CreateShortCut  "$SMPROGRAMS\Onebox\Uninstall.lnk" "$INSTDIR\uninst.exe"
  ;Sleep 800
  ;ExecShell "Regsvr32" "$INSTDIR\ShellExtent.dll"
  ; MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "$INSTDIR"
  ;Sleep 2000
 	;KillProcDLL::KillProc "explorer.exe"
;Functionend

Function InstallationMainFun
    SendMessage $PB_ProgressBar ${PBM_SETRANGE32} 0 100  ;总步长为顶部定义值
         ;MessageBox MB_OK "GO detect"
        Call checkifinstall
	      Call StopRunningProcess

    ;MessageBox MB_OK "start Go on to install"
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 10 0
		SetOverwrite on
    SetShellVarContext all
    ;ReadRegStr  $R1  HKLM   "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"  "UninstallString"
    ;${if} $R1 != ""
      ;Call DeleteNeedCoverFile
      ;Sleep 2000
    ;${Endif}

    ReadRegStr  $R1  HKLM   "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"  "UninstallString"
    ${If} $R1 != ""
      Call SaveConfigureAndData
    ${Endif}

    SetOutPath "$INSTDIR\Language"
      File /oname=Chinese.ini  ..\Language\Chinese.ini
      File /oname=English.ini  ..\Language\English.ini
      File /oname=Error_Chinese.xml  ..\Language\Error_Chinese.xml
      File /oname=Error_English.xml  ..\Language\Error_English.xml
      Sleep 500
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 20 0
    
    SetOutPath "$INSTDIR\Res"
      File /oname=logo.ico   ..\Res\logo.ico
      File  /oname=outchain.bmp  ..\Res\outchain.bmp
      File /oname=share.bmp   ..\Res\share.bmp
      File  /oname=SyncFailed.ico ..\Res\SyncFailed.ico
      File  /oname=SyncIng.ico  ..\Res\SyncIng.ico
      File  /oname=SyncOk.ico  ..\Res\SyncOk.ico
      File  /oname=SyncNoAction.ico  ..\Res\SyncNoAction.ico
      File /oname=ShareLink.bmp  ..\Res\ShareLink.bmp
      File  /oname=cloudlogo.bmp  ..\Res\cloudlogo.bmp
      File  /oname=docment.png  ..\Res\docment.png
      Sleep 800
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 30 0

    SetOutPath "$INSTDIR\Tools"
      File /oname=TerminateProcess.exe ..\TerminateProcess.exe
      File /oname=logReader_v2.xlsm   ..\Tools\logReader_v2.xlsm
    ;SetOutPath "$INSTDIR\Update"
      ;File /oname=sqlite3.exe ..\Update\sqlite3.exe
      ;File /oname=UpdateDatabase.vbs ..\Update\UpdateDatabase.vbs
    SetOutPath "$INSTDIR\Update\UpdateDataBase"
     ; File /oname=1.2.3.24.sql ..\Update\UpdateDataBase\1.2.3.24.sql
      ;Sleep 500
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 35 0

    SetOutPath "$INSTDIR\"
      File  /oname=OneboxSDK.dll   ..\OneboxSDK.dll
      File /oname=OneboxSyncHelper.dll    ..\OneboxSyncHelper.dll
      File  /oname=libcurl.dll  ..\libcurl.dll
      File /oname=libeay32.dll   ..\libeay32.dll
      File /oname=log4cpp.conf  ..\log4cpp.conf  
      File /oname=Label32.dll  ..\Label32.dll
      File /oname=msvcp110.dll  ..\msvcp110.dll
      File /oname=msvcr110.dll  ..\msvcr110.dll 
      File /oname=msvcr100.dll  ..\msvcr100.dll
      File  /oname=ssleay32.dll   ..\ssleay32.dll
      File  /oname=OneboxSyncService.exe   ..\OneboxSyncService.exe
      File  /oname=SyncRules.xml  ..\SyncRules.xml
      File /oname=zlib1.dll  ..\zlib1.dll
      File /oname=setting.xml  ..\setting.xml
      Sleep 800
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 45 0
      ${GetTime} "" "L" $0 $1 $2 $3 $4 $5 $6
        Rename "$INSTDIR\ShellExtent.dll" "$INSTDIR\ShellExtentbk$5.dll"
        Rename "$INSTDIR\OutlookAddin.dll" "$INSTDIR\OutlookAddinbk$5.dll"
        Sleep 1000
        Delete "$INSTDIR\ShellExtentbk*.dll"
        Delete "$INSTDIR\OutlookAddinbk*.dll"
      ${If} ${RunningX64}
        File /oname=ShellExtent.dll  ..\..\x64\ShellExtent_x64.dll
        File /oname=OutlookAddin.dll  ..\..\x64\OutlookAddin_x64.dll
        File  /oname=OneboxShExtCmd.exe ..\..\x64\OneboxShExtCmd_x64.exe
        File  /oname=OneboxStart.exe   ..\..\x64\OneboxStart_x64.exe
      ${Else}
        File /oname=ShellExtent.dll  ..\ShellExtent.dll
        File /oname=OutlookAddin.dll  ..\OutlookAddin.dll
        File  /oname=OneboxShExtCmd.exe ..\OneboxShExtCmd.exe
        File  /oname=OneboxStart.exe   ..\OneboxStart.exe
      ${Endif}
      File /oname=NscaMiniLib.dll ..\NscaMiniLib.dll
      File /oname=onebox.crt      ..\onebox.crt
      File "/oname=Onebox 快速入门.pdf"  "..\Onebox 快速入门.pdf"
      File "/oname=Onebox Quick Start.pdf"  "..\Onebox Quick Start.pdf"
      Sleep 800
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 55 0

      Call CheckInstallDoNet
      ${If} $Global_RecordNetVerseion == "4.5"
        File  /oname=Microsoft.WindowsAPICodePack.dll  ..\v4.5\Microsoft.WindowsAPICodePack.dll
        File /oname=Microsoft.WindowsAPICodePack.Shell.dll   ..\v4.5\Microsoft.WindowsAPICodePack.Shell.dll
        File /oname=ICSharpCode.SharpZipLib.dll  ..\v4.5\ICSharpCode.SharpZipLib.dll
        File /oname=log4net.dll ..\v4.5\log4net.dll
        File /oname=log4net.config ..\v4.5\log4net.config
        File /oname=vhCalendar.dll  ..\v4.5\vhCalendar.dll
        File  /oname=Thrift.dll  ..\v4.5\Thrift.dll
        File "/oname=Onebox.exe" "..\v4.5\Onebox.exe"
     ${ElseIf} $Global_RecordNetVerseion == "4.0"
        File  /oname=Microsoft.WindowsAPICodePack.dll  ..\v4.0\Microsoft.WindowsAPICodePack.dll
        File /oname=Microsoft.WindowsAPICodePack.Shell.dll   ..\v4.0\Microsoft.WindowsAPICodePack.Shell.dll
        File /oname=ICSharpCode.SharpZipLib.dll  ..\v4.0\ICSharpCode.SharpZipLib.dll
        File /oname=log4net.dll ..\v4.0\log4net.dll
        File /oname=log4net.config ..\v4.0\log4net.config
        File /oname=vhCalendar.dll  ..\v4.0\vhCalendar.dll
        File  /oname=Thrift.dll  ..\v4.0\Thrift.dll
        File "/oname=Onebox.exe" "..\v4.0\Onebox.exe"
      ${ElseIf}   $Global_RecordNetVerseion == "3.5"
        File  /oname=Microsoft.WindowsAPICodePack.dll  ..\v3.5\Microsoft.WindowsAPICodePack.dll
        File /oname=Microsoft.WindowsAPICodePack.Shell.dll   ..\v3.5\Microsoft.WindowsAPICodePack.Shell.dll
        File /oname=ICSharpCode.SharpZipLib.dll  ..\v3.5\ICSharpCode.SharpZipLib.dll
        File /oname=log4net.dll ..\v3.5\log4net.dll
        File /oname=log4net.config ..\v3.5\log4net.config
        File /oname=vhCalendar.dll  ..\v3.5\vhCalendar.dll
        File  /oname=Thrift.dll  ..\v3.5\Thrift.dll
        File "/oname=Onebox.exe" "..\v3.5\Onebox.exe"
      ${Endif}
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 70 0

      Call UpdateINIFileAndData
      Sleep 500
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 80 0
    
      Sleep 1000
      IfSilent RegSilent  RegNoSilent
      RegSilent:
      WriteRegDWORD HKLM "${PRODUCT_Onebox_KEY}\Setting" "InstallType" 0x01
      goto  WriteReg
      RegNoSilent:
      WriteRegDWORD HKLM "${PRODUCT_Onebox_KEY}\Setting" "InstallType" 0x00

      WriteReg:
      ;DeleteRegValue HKCU "Software\Classes\Local Settings\Software\Microsoft\Windows\CurrentVersion\TrayNotify"  "IconStreams"
      DeleteRegValue HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Run"  "${PRODUCT_NAME}"
      WriteUninstaller "$INSTDIR\uninst.exe"
      WriteRegStr HKLM "${PRODUCT_DIR_REGKEY}" "" "$INSTDIR"
      WriteRegStr HKLM "${PRODUCT_Onebox_KEY}\Setting" "AppPath" "$INSTDIR"
      WriteRegStr HKLM "${PRODUCT_Onebox_KEY}\Setting" "MainVersion"  "${PRODUCT_VERSION}"
      WriteRegStr HKLM "${PRODUCT_Onebox_KEY}\Setting" "DoNetVersion" "$Global_RecordNetVerseion"
      WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Run" "${PRODUCT_NAME}"  "$\"$INSTDIR\Onebox.exe$\" autorun"
      WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
      WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
      WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayIcon" "$INSTDIR\Onebox.exe"
      WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
      WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
      ExecWait '$INSTDIR\Tools\TerminateProcess.exe "/createTask"'
      Sleep 500
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 90 0
      IfFileExists $PREVIOUSINSTDIR\OutlookAddin.dll 0 +3
      Execwait 'Regsvr32 /s /u "$PREVIOUSINSTDIR\OutlookAddin.dll"'
      Sleep 500
      Execwait 'Regsvr32 /s "$INSTDIR\OutlookAddin.dll"'
      Sleep 500
      IfFileExists $PREVIOUSINSTDIR\ShellExtent.dll 0 +3
      Execwait 'Regsvr32 /s /u "$PREVIOUSINSTDIR\ShellExtent.dll"'
      Sleep 500
      Execwait 'Regsvr32 /s "$INSTDIR\ShellExtent.dll"'
      Sleep 500
      FindProcDLL::KillProc "explorer.exe"
      Sleep 500
      ${If} $PREVIOUSINSTDIR != ""
        Call DeleteNextVersionFile
        ;Call CheckAndUnistallCloudDrive
      ${Endif}
      Sleep 1000
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 100 0
      Sleep 400

 ;ShowWindow $Btn_Next ${SW_SHOW}
    ;  ShowWindow $Btn_Install ${SW_HIDE}
      
	EnableWindow $Btn_Cancel 0
	 Sleep 300
	 ifsilent SilentQuit  NoSilentQuit
       SilentQuit:
       Call onClickComplete
       NoSilentQuit:
        Call onClickNext
FunctionEnd



Function WelcomePage
    GetDlgItem $0 $HWNDPARENT 1
    ShowWindow $0 ${SW_HIDE}
    GetDlgItem $0 $HWNDPARENT 2
    ShowWindow $0 ${SW_HIDE}
    GetDlgItem $0 $HWNDPARENT 3
    ShowWindow $0 ${SW_HIDE}

    StrCpy $Ischeckemtdir 0
    nsDialogs::Create 1044
    Pop $0
    ${If} $0 == error
        Abort
    ${EndIf}
    SetCtlColors $0 ""  transparent ;背景设成透明

    ${NSW_SetWindowSize} $HWNDPARENT 520 350 ;改变窗体大小
    ${NSW_SetWindowSize} $0 520 350 ;改变Page大小
    
    ;读取RTF的文本框
;		nsDialogs::CreateControl "RichEdit20A" \
    ;${ES_READONLY}|${WS_VISIBLE}|${WS_CHILD}|${WS_TABSTOP}|${WS_VSCROLL}|${ES_MULTILINE}|${ES_WANTRETURN} \
		;${WS_EX_STATICEDGE}  5 35 501 216 ''
  ;  Pop $Txt_License
;		${LoadRTF} '$PLUGINSDIR\license.rtf' $Txt_License
  ;  ShowWindow $Txt_License ${SW_HIDE}

    
    ;下一步
    ${NSD_CreateButton} 320 315 88 25 ""
		Pop $Btn_Next
		StrCpy $1 $Btn_Next
		Call SkinBtn_Next
		GetFunctionAddress $3 onClickNext
    SkinBtn::onClick $1 $3
    
    	;取消
	${NSD_CreateButton} 417 315 88 25 ""
	Pop $Btn_Cancel
	StrCpy $1 $Btn_Cancel
	Call SkinBtn_Cancel
	GetFunctionAddress $3 onCancel
  SkinBtn::onClick $1 $3
  
    ;关闭按钮
  ${NSD_CreateButton} 490 8 15 15 ""
	Pop $Btn_Close
	StrCpy $1 $Btn_Close
	Call SkinBtn_Close
  GetFunctionAddress $3 onCancel
  SkinBtn::onClick $1 $3
    
    ;用户协议
	;	${NSD_CreateButton} 181 273 95 15 ""
	;	Pop $Btn_Agreement
	;	StrCpy $1 $Btn_Agreement
	;	Call SkinBtn_Agreement1
	 ; GetFunctionAddress $3 onClickAgreement
	 
	;创建dir目录框
	;${NSD_CreateDirRequest} 12 256 358 25 "$INSTDIR"
 	;Pop	$Txt_Browser
  ;${NSD_OnChange} $Txt_Browser OnChange_DirRequest
      ;禁止
      ;禁用输入编辑的目录
      ;EnableWindow $Txt_Browser 0

 	;${NSD_CreateBrowseButton} 400 256 88 25 ""
 	;Pop	$Btn_Browser
 	;StrCpy $1 $Btn_Browser
	;Call SkinBtn_Browser
	;GetFunctionAddress $3 OnClick_BrowseButton
	;SkinBtn::onClick $1 $3
 	;StrCpy $Bool_License 0 ;初始化值为0

    ;贴小图
   ; ${NSD_CreateBitmap} 0 0 520 302 ""
   ; Pop $MiddleImage
   ; ${NSD_SetImage} $MiddleImage $PLUGINSDIR\welcome.bmp $ImageHandle
    ;  ShowWindow $MiddleImage ${SW_HIDE}
    
    ;贴背景大图
    ${NSD_CreateBitmap} 0 0 100% 100% ""
    Pop $BGImage
    ${NSD_SetImage} $BGImage $PLUGINSDIR\bg.bmp $ImageHandle

	GetFunctionAddress $0 onGUICallback
	WndProc::onCallback $BGImage $0 ;处理无边框窗体移动
	nsDialogs::Show
	${NSD_FreeImage} $ImageHandle
FunctionEnd

;检查是否已经安装
Function   checkifinstall

    Push $R0
    Push $R1
    Push $R2

    ReadRegStr  $R1  HKLM   "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"  "UninstallString"
    strcmp $R1 "" YES2 NO2
        NO2:
          ReadRegStr  $0 HKLM "${PRODUCT_Onebox_KEY}\Setting" "MainVersion"
          ${VersionCompare}   $0  ${PRODUCT_VERSION}  $R0
            ${If} $R0 == 1
              Ifsilent noFlag
          		${If} $LANGUAGE == 2052
		            	MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "程序已经安装，是否覆盖安装?"  IDYES yesFlag IDNO noFlag
	           	${Else}
			            MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "Onebox already exists. Overwrite it?" IDYES yesFlag IDNO noFlag
              ${Endif}
            ${Else}
              ;RMDir /r  "$INSTDIR\UserData"
              goto YES2
            ${Endif}
          ;${Endif}
          
          noFlag:
            Call onClickClose
            Quit
          yesFlag:
            RMDir /r  "$INSTDIR\UserData"
            Sleep 1000
            Pop $R2
            Pop $R1
            Pop $R0
            goto YES2

        YES2:
FunctionEnd
  
Function CheckInstallDoNet
  Push $0
  
  StrCpy $Global_RecordNetVerseion "4.5"
  ReadRegDWORD $0 HKLM "SOFTWARE\Microsoft\NET Framework Setup\NDP\v4.5\Client" "Install"
  IfErrors 0 +3
    ClearErrors
    Goto CheckDoNetFull4.5
    StrCpy $Global_RecordNetVerseion "4.5"
    Goto ExitCheck
  CheckDoNetFull4.5:
  ReadRegDWORD $0 HKLM "SOFTWARE\Microsoft\NET Framework Setup\NDP\v4.5\Full" "Install"
  IfErrors 0 +3
    ClearErrors
    Goto CheckDoNetClient4.0
    StrCpy $Global_RecordNetVerseion "4.5"
    Goto ExitCheck
  CheckDoNetClient4.0:
  ReadRegDWORD $0 HKLM "SOFTWARE\Microsoft\NET Framework Setup\NDP\v4\Client" "Install"
  IfErrors 0 +3
    ClearErrors
    Goto CheckDoNetFull4.0
    StrCpy $Global_RecordNetVerseion "4.0"
    Goto ExitCheck
  CheckDoNetFull4.0:
  ReadRegDWORD $0 HKLM "SOFTWARE\Microsoft\NET Framework Setup\NDP\v4\Full" "Install"
   IfErrors 0 +3
    ClearErrors
    Goto CheckDoNet3.5
    StrCpy $Global_RecordNetVerseion "4.0"
    Goto ExitCheck
  CheckDoNet3.5:
   ReadRegDWORD $0 HKLM "SOFTWARE\Microsoft\NET Framework Setup\NDP\v3.5" "Install"
   IfErrors 0 +3
    ClearErrors
    Goto ExitCheck
    StrCpy $Global_RecordNetVerseion "3.5"
  
  ExitCheck:
  ;Messagebox MB_OK $Global_RecordNetVerseion
  Pop $0
FunctionEnd
  
  
Function CheckAndUnistallCloudDrive
    ReadRegStr  $R0  HKLM   "SOFTWARE\Huawei\CloudDrive\Setting"  "AppPath"
    ${If} $R0 != ""
      ;SetOutPath "$INSTDIR\Update\"
      ;CreateDirectory $PLUGINSDIR\OneboxUserData
      ;CopyFiles  $R0\Config.ini   $PLUGINSDIR\OneboxUserData\Config.ini
        ;File /oname=OneboxUpgrade.exe    ..\Update\OneboxUpgrade.exe

      ;CreateDirectory $PLUGINSDIR\OneboxUserData\UserData
      ;CopyFiles $R0\UserData\*.ini $PLUGINSDIR\OneboxUserData\UserData
      ;CopyFiles $R0\UserData\*.db $PLUGINSDIR\OneboxUserData\UserData
      ;ExecWait '"$R0\uninst.exe" /S'
      Call  UninstallCloudDriveProduct
      ;ExecWait "$INSTDIR\Update\OneboxUpgrade.exe"
    ${Endif}
FunctionEnd

Function checkOSVersion

   Push $R0
   Push $R1
   push $R2

   ClearErrors


   ReadRegStr $R0 HKLM \
   "SOFTWARE\Microsoft\Windows NT\CurrentVersion" ProductName

   ${WordFind} $R0 " " +1 $R1
   ${StrFilter} $R1 "-" "" "" $R2
   ;MessageBox MB_OK  $R2
   StrCmp $R2 "windows"  lbl_windows

   lbl_windows:
     ${WordFind} $R0 " " +2 $R1
      ${If} $R1 != "7" 
      ${AndIf} $R1 != "8.1"
      ${AndIf} $R1 != "10"
        IfSilent quitInstall
          ${If} $LANGUAGE == 2052
            MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "当前操作系统可能无法运行该程序（仅完全支持Windows 7和Windows 8和Windows 10中英文版），是否继续安装？" \
                              IDYES   BeginToInstall IDNO quitInstall
          ${Else}
             MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "The current operating system cannot run this program. (This program can run on Windows 7 and Windows 8 and Windows 10 only.) Continue?" IDYES   BeginToInstall IDNO quitInstall
          ${Endif}
      ${Else}
        Goto BeginToInstall
      ${Endif}
        quitInstall:
             Quit
        BeginToInstall:
             Pop $R2
             Pop $R1
             Pop $R0
            ;Exch $R0
FunctionEnd
 
Function checkIsCloudMachine
   Push $R0
   Push $R1
   
   ${If} ${RunningX64}
    ReadRegStr $R0  HKCR "WOW6432Node\CLSID\{6655833E-D350-4017-8C31-3F89CA360FDF}" ""
   ${Else}
    ReadRegStr $R0  HKCR "CLSID\{6655833E-D350-4017-8C31-3F89CA360FDF}" ""
   ${Endif}
    ;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "R0:$R0"
   ${If} $R0 == "1"
     goto  BeginToInstall
   ${Endif}
   
   ${If} ${RunningX64}
      ReadRegDWORD $R1 HKLM \
        "SOFTWARE\WOW6432Node\Microsoft\windows\CurrentVersion" "isCloudMachine"
   ${Else}
       ReadRegDWORD $R1 HKLM \
        "SOFTWARE\Microsoft\windows\CurrentVersion" "isCloudMachine"
   ${Endif}
      ;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "R1:$R1"
    ${If}  $R1 == 1
      ${If} $LANGUAGE == 2052
        MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "Onebox暂缓在云桌面上推广。" IDOK quitInstall
      ${Else}
        MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "Onebox is not promoted on cloud desktops temporarily." IDOK quitInstall
      ${Endif}
    ${Else}
      goto BeginToInstall
    ${Endif}
    quitInstall:
      Quit
    BeginToInstall:
      Pop $R0
      Pop $R1
FunctionEnd
 
 
 ;检查安装目录所在盘剩余空间
Function CheckFreeSpace
;获取查找到的驱动器盘符($9)可用空间(/D=F)单位兆(/S=M)
${DriveSpace} C: "/D=F /S=M" $R0
    ${If} $R0 < 200
        IfSilent BeginToInstall
        ${If} $LANGUAGE == 2052
            MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "C盘剩余空间已不足200MB，请清理后继续安装。" IDOK FlagOk
        ${Else}
            MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "The remaining space on drive C is less than 200 MB. Continue the installation after clearing space." IDOK FlagOk
        ${Endif}
    ${Else}
        goto  BeginToInstall
    ${Endif}
    
    FlagOk:
      Abort
    BeginToInstall:
      Pop $R0
FunctionEnd

Function UpdateProc
  Call StopRunningProcess
  Sleep 1000
  Call SaveConfigureAndData
  Sleep 1000
  Call UpdateFileProc
  Sleep 1000
  Call UpdateINIFileAndData
  Sleep 1000
  ${If} $PREVIOUSINSTDIR != ""
    Call DeleteNextVersionFile
    ;Call CheckAndUnistallCloudDrive
  ${Endif}
  Exec  "$INSTDIR\${PRODUCT_EXECUTE_NAME}"
  
FunctionEnd

Function ForceUpdateProc
    ;ReadRegStr  $R1  HKLM   "SOFTWARE\chinasoft\Onebox\Setting"  "AppPath"
    Call StopRunningProcess
    Sleep 1000
    ExecWait '$INSTDIR\OneboxShExtCmd.exe  "delete-virtual-folder"'
    Sleep 1000
    ;Call SaveConfigureAndData
    ;Sleep 1000
    ;ExecWait '"$INSTDIR\uninst.exe" /S'
    ;Sleep 1000
    Call UpdateFileProc
    Sleep 1000
    ;Call UpdateINIFileAndData
    ;Sleep 1000
    ${If} $PREVIOUSINSTDIR != ""
      Call DeleteNextVersionFile
    ${Endif}
    
    ${If} ${RunningX64}
      SetRegView 64
      DeleteRegKey HKCR `CLSID\{1D84B808-6135-47E7-9D12-C391C99AD8A0}`
    ${Else}
      DeleteRegKey HKCR `CLSID\{1D84B808-6135-47E7-9D12-C391C99AD8A0}`
    ${Endif}
    Exec  "$INSTDIR\OneboxStart.exe"
    
FunctionEnd

Function UpdateFileProc
	Var /GLOBAL varWaitCount
    Call StopRunningProcess
    SetOverwrite on
    SetShellVarContext all
      
    SetOutPath "$INSTDIR\Language"
      File /oname=Chinese.ini  ..\Language\Chinese.ini
      File /oname=English.ini  ..\Language\English.ini
      File /oname=Error_Chinese.xml  ..\Language\Error_Chinese.xml
      File /oname=Error_English.xml  ..\Language\Error_English.xml
      Sleep 1000
      
    SetOutPath "$INSTDIR\Res"
     File /oname=logo.ico   ..\Res\logo.ico
     File  /oname=outchain.bmp  ..\Res\outchain.bmp
     File /oname=share.bmp   ..\Res\share.bmp
     File  /oname=SyncFailed.ico ..\Res\SyncFailed.ico
     File  /oname=SyncIng.ico  ..\Res\SyncIng.ico                    
     File  /oname=SyncOk.ico  ..\Res\SyncOk.ico
     File  /oname=SyncNoAction.ico  ..\Res\SyncNoAction.ico
     File /oname=ShareLink.bmp  ..\Res\ShareLink.bmp
     File  /oname=cloudlogo.bmp  ..\Res\cloudlogo.bmp
     File  /oname=docment.png  ..\Res\docment.png
     Sleep 2000

    ;SetOutPath "$INSTDIR\Update"
      ;File /oname=sqlite3.exe ..\Update\sqlite3.exe
      ;File /oname=UpdateDatabase.vbs ..\Update\UpdateDatabase.vbs
    SetOutPath "$INSTDIR\Update\UpdateDataBase"
      ;File /oname=1.2.3.24.sql ..\Update\UpdateDataBase\1.2.3.24.sql
      ;Sleep 500
      
    SetOutPath "$INSTDIR\Tools"
     File /oname=logReader_v2.xlsm   ..\Tools\logReader_v2.xlsm
     File /oname=TerminateProcess.exe ..\TerminateProcess.exe

    SetOutPath "$INSTDIR\"
      File  /oname=OneboxSDK.dll   ..\OneboxSDK.dll
      File /oname=OneboxSyncHelper.dll    ..\OneboxSyncHelper.dll
      File  /oname=libcurl.dll  ..\libcurl.dll
      File /oname=libeay32.dll   ..\libeay32.dll
      File /oname=log4cpp.conf  ..\log4cpp.conf
      File /oname=Label32.dll  ..\Label32.dll
      File /oname=msvcp110.dll  ..\msvcp110.dll
      File /oname=msvcr110.dll  ..\msvcr110.dll
      File /oname=msvcr100.dll  ..\msvcr100.dll
      File  /oname=ssleay32.dll   ..\ssleay32.dll
      File  /oname=OneboxSyncService.exe   ..\OneboxSyncService.exe
      File  /oname=SyncRules.xml  ..\SyncRules.xml
      File /oname=zlib1.dll  ..\zlib1.dll
      File /oname=setting.xml  ..\setting.xml
      ${GetTime} "" "L" $0 $1 $2 $3 $4 $5 $6
      Rename "$INSTDIR\ShellExtent.dll" "$INSTDIR\ShellExtentbk$5.dll"
      Rename "$INSTDIR\OutlookAddin.dll" "$INSTDIR\OutlookAddinbk$5.dll"
      Sleep 1000
      Delete "$INSTDIR\ShellExtentbk*.dll"
      Delete "$INSTDIR\OutlookAddinbk*.dll"
      ${If} ${RunningX64}
        File /oname=ShellExtent.dll  ..\..\x64\ShellExtent_x64.dll
        File /oname=OutlookAddin.dll  ..\..\x64\OutlookAddin_x64.dll
        File /oname=OneboxShExtCmd.exe ..\..\x64\OneboxShExtCmd_x64.exe
        File  /oname=OneboxStart.exe   ..\..\x64\OneboxStart_x64.exe
      ${Else}
        File /oname=ShellExtent.dll  ..\ShellExtent.dll
        File /oname=OutlookAddin.dll  ..\OutlookAddin.dll
        File /oname=OneboxShExtCmd.exe ..\OneboxShExtCmd.exe
        File  /oname=OneboxStart.exe   ..\OneboxStart.exe
      ${Endif}
      File /oname=NscaMiniLib.dll ..\NscaMiniLib.dll
      File /oname=onebox.crt      ..\onebox.crt
      File "/oname=Onebox 快速入门.pdf"  "..\Onebox 快速入门.pdf"
      File "/oname=Onebox Quick Start.pdf"  "..\Onebox Quick Start.pdf"
      Sleep 2000
      Call CheckInstallDoNet
      ${If} $Global_RecordNetVerseion == "4.5"
        File /oname=Microsoft.WindowsAPICodePack.dll  ..\v4.5\Microsoft.WindowsAPICodePack.dll
        File /oname=Microsoft.WindowsAPICodePack.Shell.dll   ..\v4.5\Microsoft.WindowsAPICodePack.Shell.dll
        File /oname=ICSharpCode.SharpZipLib.dll  ..\v4.5\ICSharpCode.SharpZipLib.dll
        File /oname=log4net.dll ..\v4.5\log4net.dll
        File /oname=log4net.config ..\v4.5\log4net.config
        File /oname=vhCalendar.dll  ..\v4.5\vhCalendar.dll
        File  /oname=Thrift.dll  ..\v4.5\Thrift.dll
        File "/oname=Onebox.exe" "..\v4.5\Onebox.exe"
     ${ElseIf} $Global_RecordNetVerseion == "4.0"
        File  /oname=Microsoft.WindowsAPICodePack.dll  ..\v4.0\Microsoft.WindowsAPICodePack.dll
        File /oname=Microsoft.WindowsAPICodePack.Shell.dll   ..\v4.0\Microsoft.WindowsAPICodePack.Shell.dll
        File /oname=ICSharpCode.SharpZipLib.dll  ..\v4.0\ICSharpCode.SharpZipLib.dll
        File /oname=log4net.dll ..\v4.0\log4net.dll
        File /oname=log4net.config ..\v4.0\log4net.config
        File /oname=vhCalendar.dll  ..\v4.0\vhCalendar.dll
        File  /oname=Thrift.dll  ..\v4.0\Thrift.dll
        File "/oname=Onebox.exe" "..\v4.0\Onebox.exe"
      ${ElseIf}   $Global_RecordNetVerseion == "3.5"
        File  /oname=Microsoft.WindowsAPICodePack.dll  ..\v3.5\Microsoft.WindowsAPICodePack.dll
        File /oname=Microsoft.WindowsAPICodePack.Shell.dll   ..\v3.5\Microsoft.WindowsAPICodePack.Shell.dll
        File /oname=ICSharpCode.SharpZipLib.dll  ..\v3.5\ICSharpCode.SharpZipLib.dll
        File /oname=log4net.dll ..\v3.5\log4net.dll
        File /oname=log4net.config ..\v3.5\log4net.config
        File /oname=vhCalendar.dll  ..\v3.5\vhCalendar.dll
        File  /oname=Thrift.dll  ..\v3.5\Thrift.dll
        File "/oname=Onebox.exe" "..\v3.5\Onebox.exe"
      ${Endif}
      Sleep 1000
      Call UpdateINIFileAndData
      Sleep 1000

      ;DeleteRegValue HKCU "Software\Classes\Local Settings\Software\Microsoft\Windows\CurrentVersion\TrayNotify"  "IconStreams"
      DeleteRegValue HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Run"  "${PRODUCT_NAME}"
      WriteUninstaller "$INSTDIR\uninst.exe"
      WriteRegStr HKLM "${PRODUCT_DIR_REGKEY}" "" "$INSTDIR"
      WriteRegStr HKLM "${PRODUCT_Onebox_KEY}\Setting" "AppPath" "$INSTDIR"
      WriteRegDWORD HKLM "${PRODUCT_Onebox_KEY}\Setting" "InstallType" 0x00
      WriteRegStr HKLM "${PRODUCT_Onebox_KEY}\Setting" "MainVersion"  "${PRODUCT_VERSION}"
      WriteRegDWORD HKLM "${PRODUCT_Onebox_KEY}\Setting" "IsUpgradeSuccess" 0x01
      WriteRegStr HKLM "${PRODUCT_Onebox_KEY}\Setting" "DoNetVersion" "$Global_RecordNetVerseion"
      WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Run" "${PRODUCT_NAME}"  "$\"$INSTDIR\Onebox.exe$\" autorun"
      WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
      WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
      WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayIcon" "$INSTDIR\Onebox.exe"
      WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
      WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
      ExecWait '$INSTDIR\Tools\TerminateProcess.exe "/createTask"'
      Sleep 500
 	    
      RMdir /r "$SMPROGRAMS\Onebox"
      CreateDirectory "$SMPROGRAMS\Onebox"
      CreateShortCut  "$SMPROGRAMS\Onebox\Onebox.lnk" "$INSTDIR\Onebox.exe"
      CreateShortCut  "$SMPROGRAMS\Onebox\Uninstall.lnk" "$INSTDIR\uninst.exe"
      CreateShortCut "$QUICKLAUNCH\${PRODUCT_NAME}.lnk" "$INSTDIR\${PRODUCT_EXECUTE_NAME}"
      CreateShortCut  "$DESKTOP\Onebox.lnk" "$INSTDIR\Onebox.exe"
      Sleep 500

      IfFileExists $PREVIOUSINSTDIR\OutlookAddin.dll 0 +3
      Execwait 'Regsvr32 /s /u "$PREVIOUSINSTDIR\OutlookAddin.dll"'
      Sleep 500
      Execwait 'Regsvr32 /s "$INSTDIR\OutlookAddin.dll"'
      Sleep 500
      IfFileExists $PREVIOUSINSTDIR\ShellExtent.dll 0 +3
      Execwait 'Regsvr32 /s /u "$PREVIOUSINSTDIR\ShellExtent.dll"'
      Sleep 500
      Execwait 'Regsvr32 /s "$INSTDIR\ShellExtent.dll"'
      Sleep 500
	    ;ExecWait '$INSTDIR\Tools\TerminateProcess.exe  "/name" "explorer.exe"'
      FindProcDLL::KillProc "explorer.exe"
      ;noFlag:
      Sleep 500
      
 FunctionEnd

 Function SaveConfigureAndData

  Push $R0
  Push $R1
  Push $R2
  
  Var /GLOBAL varMonitorPath
  Var /GLOBAL varCachePath
  Var /GLOBAL varSyncModel
  Var /GLOBAL varServerUrl
  Var /GLOBAL varUseSSL
  Var /GLOBAL varUserName
  Var /GLOBAL varPassWord
  Var /GLOBAL varLoginType
  Var /GLOBAL varBootStartRun
  Var /GLOBAL varAutoLogin
  Var /GLOBAL varRemPassword
  Var /GLOBAL varCurrentUseVersion

  ReadRegStr  $R0  HKLM   "SOFTWARE\chinasoft\Onebox\Setting"  "AppPath"
  ${If} $R0 == ""
    ReadRegStr  $R1  HKLM   "SOFTWARE\chinasoft\CloudDrive\Setting"  "AppPath"
    ${If} $R1 != ""
      StrCpy $R0 $R1
    ${Endif}
  ${Endif}
  IfFileExists $R0\Config.ini 0 +17
  ReadINIStr  $varMonitorPath $R0\Config.ini CONFIGURE MonitorRootPath
  ReadINIStr  $varCachePath $R0\Config.ini   CONFIGURE CachePath
  ReadINIStr  $varSyncModel $R0\Config.ini   CONFIGURE SyncModel
  ;ReadINIStr  $varServerUrl $R0\Config.ini   NETWORK StorageServerURL
  ;ReadINIStr  $varUseSSL $R0\Config.ini   NETWORK UseSSL
  ReadINIStr  $varUserName $R0\Config.ini   USERINFO UserName
  ReadINIStr  $varPassWord $R0\Config.ini   USERINFO PassWord
  ReadINIStr  $varLoginType $R0\Config.ini   USERINFO LoginType
  ReadINIStr  $varBootStartRun $R0\Config.ini   USERINFO BootStartRun
  ReadINIStr  $varAutoLogin $R0\Config.ini   USERINFO AutoLogin
  ReadINIStr  $varRemPassword $R0\Config.ini   USERINFO RemPassword
  

  WriteINIStr $PLUGINSDIR\Config.ini CONFIGURE MonitorRootPath $varMonitorPath
  WriteINIStr $PLUGINSDIR\Config.ini CONFIGURE CachePath $varCachePath
  WriteINIStr $PLUGINSDIR\Config.ini CONFIGURE SyncModel $varSyncModel
  ;WriteINIStr $PLUGINSDIR\Config.ini NETWORK StorageServerURL $varServerUrl
  ;WriteINIStr $PLUGINSDIR\Config.ini NETWORK UseSSL $varUseSSL
  WriteINIStr $PLUGINSDIR\Config.ini USERINFO UserName $varUserName
  WriteINIStr $PLUGINSDIR\Config.ini USERINFO PassWord $varPassWord
  WriteINIStr $PLUGINSDIR\Config.ini USERINFO LoginType $varLoginType
  WriteINIStr $PLUGINSDIR\Config.ini USERINFO BootStartRun $varBootStartRun
  WriteINIStr $PLUGINSDIR\Config.ini USERINFO AutoLogin $varAutoLogin
  WriteINIStr $PLUGINSDIR\Config.ini USERINFO RemPassword $varRemPassword
  
  ReadRegStr $varCurrentUseVersion  HKLM "${PRODUCT_Onebox_KEY}\Setting" "MainVersion"

  ${VersionCompare} $varCurrentUseVersion "1.2.3.2900" $R2
  ${If} $R2 == 2
    WriteINIStr $PLUGINSDIR\Config.ini CONFIGURE SyncModel "0"
  ${Endif}
    
    
  ${VersionCompare} ${PRODUCT_VERSION} $varCurrentUseVersion $R2
  ${If} $R2 == 0
    Goto Done
  ${Endif}
  
  ${VersionCompare} "1.2.3.2900" $varCurrentUseVersion $R2
  ${If} $R2 == 2
  IfFileExists  $R0\UserData\"*" 0 +7
  IfFileExists $INSTDIR\"*" 0 +3
    RMDir /r $INSTDIR
    Sleep 1000
    CreateDirectory $INSTDIR\UserData
    CopyFiles $R0\UserData\*.ini $INSTDIR\UserData
    CopyFiles $R0\UserData\*.db $INSTDIR\UserData
  ${Endif}
  Done:
  
  Pop $R2
  Pop $R1
  Pop $R0

FunctionEnd

Function UpdateINIFileAndData
  IfFileExists  $INSTDIR\Config.ini 0 +2
  Delete  "$INSTDIR\Config.ini"
  CopyFiles /SILENT "$PLUGINSDIR\Config.ini"   "$INSTDIR\Config.ini"
  ;${VersionCompare} "1.2.3.2816" $0 $R2
 ; ${If} $R2 == 1
    ;IfFileExists $INSTDIR\Update\UpdateDataBase\* 0 +2
   ; Call UpdateBase
  ;${Endif}

FunctionEnd

Function UpdateBase

  IfFileExists  $INSTDIR\UserData\* 0
  CopyFiles /SILENT $INSTDIR\Update\sqlite3.exe  $INSTDIR\UserData\sqlite3.exe
  CopyFiles /SILENT $INSTDIR\Update\UpdateDatabase.vbs  $INSTDIR\UserData\UpdateDatabase.vbs
  Execwait $INSTDIR\UserData\UpdateDatabase.vbs
  Sleep 2000
  ;FindFirst $0 $1 $INSTDIR\Update\UpdateDataBase\*
  ;loop:
  ;StrCmp $1 "" Done
  ;StrCmp $1  "." Next
  ;StrCmp $1  ".." Next
  ;${If} $1 != ""
     ;Call RunSqlFile
    ;Goto Next
  ;${Endif}
  ;Next:
  ;FindNext $0 $1
  ;Goto loop
  ;Done:
FunctionEnd

Function RunSqlFile

  Var /GLOBAL varVersionTemp
  Var /GLOBAL varOverFlag
  Var /GLOBAL waitDeleteCount
  
  IntOp $waitDeleteCount $waitDeleteCount & 0x0

  FindFirst $0 $1 $INSTDIR\Update\UpdateDataBase\*

  loop:
  StrCmp $1 "" Done
  StrCmp $1  "." Next
  StrCmp $1  ".." Next

  ${StrRep} $varVersionTemp $1 ".sql" ""
  ${StrRep} $varOverFlag $1 ".sql" ""
  Goto Done
  Next:
  FindNext $0 $1
  Goto loop
  Done:

  FindFirst $0 $1 $INSTDIR\Update\UpdateDataBase\*
  loop2:
  StrCmp $1 "" Over
  StrCmp $1  "." Next2
  StrCmp $1  ".." Next2

  ${StrRep} $2 $1 ".sql" ""
  ${VersionCompare} $2 $varVersionTemp $R0
  ${If} $R0 == 2
    StrCpy $varVersionTemp $2
  ${Else}
    Goto Next2
  ${Endif}

  Next2:
  FindNext $0 $1
  Goto loop2
  Over:
    IfFileExists   $INSTDIR\Userdata\remote_table.db 0  +13
    ;ExecWait  "$INSTDIR\Update\sqlite3.exe $INSTDIR\UserData\remote_table.db < $INSTDIR\Update\UpdateDataBase\$varVersionTemp.sql"
    Sleep 1000
    ;Delete "$INSTDIR\Update\UpdateDataBase\$varVersionTemp.sql"
    IfFileExists "$INSTDIR\Update\UpdateDataBase\$varVersionTemp.sql" 0 +8
    IntOp $waitDeleteCount $waitDeleteCount + 1
    IntCmp $waitDeleteCount 20 is20 lessthan20 morethan20
    is20:
      RMDir /r  "$INSTDIR\UserData"
      Goto Over2
    lessthan20:
      Goto Over
    morethan20:
      RMDir /r  "$INSTDIR\UserData"
      Goto Over2
    ${If} $varOverFlag != ""
      Call UpdateBase
    ${Endif}

    Over2:
FunctionEnd

;卸载
Function  UninstallCloudDriveProduct

  SetShellVarContext all
  Call StopRunningProcess
  
  ReadRegStr  $R1  HKLM   "SOFTWARE\chinasoft\CloudDrive\Setting"  "AppPath"
  ${If} $R1 != ""
    ExecWait '$R1\ShExtCmdHelper.exe  "delete-virtual-folder"'
    ReadINIStr $R0 $R1\Config.ini CONFIGURE MonitorRootPath
    ${StrRep} $0 $R0 "/" "\"
    ;MessageBox MB_OK $0 IDOK
    RMDir /r "$0\.CloudDriveCache"
    RMDir /r "$R1\UserData"
    RMDir /r "$R1\ImageResource"
    RMDir /r "$R1\Res"
    RMDir /r "$R1\Tools"
    RMDir /r "$R1"

    DeleteRegKey HKLM "SOFTWARE\chinasoft\CloudDrive\Setting"
    DeleteRegKey HKLM "SOFTWARE\chinasoft\CloudDrive"
    DeleteRegValue HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Run" "CloudDrive"
    DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "Software\Microsoft\Windows\CurrentVersion\Uninstall\chinasoft CloudDrive"
    ;DeleteRegKey HKCR "CLSID\{590B71C9-A21E-4DDE-8B8B-6E09B896295A}"
    ;DeleteRegKey HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Explorer\MyComputer\NameSpace\{590B71C9-A21E-4DDE-8B8B-6E09B896295A}"
    ;DeleteRegKey HKCR "CLSID\{1D84B808-6135-47E7-9D12-C391C99AD8A0}"

    ;删除桌面快捷方式等，可去除
    Delete "$DESKTOP\chinasoft CloudDrive.lnk"
    Delete  "$SMPROGRAMS\chinasoft CloudDrive\chinasoft CloudDrive.lnk"
    Delete  "$SMPROGRAMS\chinasoft CloudDrive\uninst.lnk"
    RMdir /r "$SMPROGRAMS\chinasoft CloudDrive"
    RMDir /r /REBOOTOK "$PROGRAMFILES\chinasoft\CloudDrive"
  ${EndIf}
  ;SetAutoClose true
Functionend

Function DeleteNextVersionFile
  Push $R1
  Push $R2
  Push $R3
  Push $R4

  ${VersionCompare} ${PRODUCT_VERSION} $PREVIOUSVERSION $R1
  ${If} $R1 == 0
   Goto Done
  ${EndIf}
  
  ${If} $PREVIOUSINSTDIR == "$PROGRAMFILES\chinasoft\CloudDrive"
    Call UninstallCloudDriveProduct
    Goto Done
  ${Endif}
  
  Delete "$PREVIOUSINSTDIR\uninst.exe"
  Delete "$PREVIOUSINSTDIR\Language\Chinese.ini"
  Delete "$PREVIOUSINSTDIR\Language\English.ini"
  Delete "$PREVIOUSINSTDIR\Res\logo.ico"
  Delete "$PREVIOUSINSTDIR\Res\outchain.bmp"
  Delete "$PREVIOUSINSTDIR\Res\share.bmp"
  Delete "$PREVIOUSINSTDIR\Res\SyncFailed.ico"
  Delete "$PREVIOUSINSTDIR\Res\SyncIng.ico"
  Delete "$PREVIOUSINSTDIR\Res\SyncOk.ico"
  Delete "$PREVIOUSINSTDIR\Res\SyncNoAction.ico"
  Delete "$PREVIOUSINSTDIR\Res\ShareLink.bmp"
  Delete "$PREVIOUSINSTDIR\Res\cloudlogo.bmp"
  Delete "$PREVIOUSINSTDIR\Res\docment.png"
  Delete "$PREVIOUSINSTDIR\Tools\logReader_v2.xlsm"
  ${GetTime} "" "L" $0 $1 $2 $3 $4 $5 $6
  Rename "$PREVIOUSINSTDIR\ShellExtent.dll" "$PREVIOUSINSTDIR\ShellExtentbk$5.dll"
  Rename "$PREVIOUSINSTDIR\OutlookAddin.dll" "$PREVIOUSINSTDIR\OutlookAddinbk$5.dll"
  Sleep 1000
  Delete "$PREVIOUSINSTDIR\OneboxSDK.dll"
  Delete "$PREVIOUSINSTDIR\libcurl.dll"
  Delete "$PREVIOUSINSTDIR\libeay32.dll"
  Delete "$PREVIOUSINSTDIR\log4cpp.conf"
  Delete "$PREVIOUSINSTDIR\Microsoft.WindowsAPICodePack.dll"
  Delete "$PREVIOUSINSTDIR\Microsoft.WindowsAPICodePack.Shell.dll"
  Delete "$PREVIOUSINSTDIR\msvcp110.dll"
  Delete "$PREVIOUSINSTDIR\msvcr110.dll"
  Delete "$PREVIOUSINSTDIR\msvcr100.dll"
  Delete "$PREVIOUSINSTDIR\ssleay32.dll"
  Delete "$PREVIOUSINSTDIR\Thrift.dll"
  Delete "$PREVIOUSINSTDIR\SyncRules.xml"
  Delete "$PREVIOUSINSTDIR\vhCalendar.dll"
  Delete "$PREVIOUSINSTDIR\zlib1.dll"
  Delete "$PREVIOUSINSTDIR\OneboxShExtCmd.exe"
  Delete "$PREVIOUSINSTDIR\WPFToolkit.dll"
  Delete "$PREVIOUSINSTDIR\NscaMiniLib.dll"
  Delete "$PREVIOUSINSTDIR\log4net.dll"
  Delete "$PREVIOUSINSTDIR\log4net.config"
  Delete "$PREVIOUSINSTDIR\ICSharpCode.SharpZipLib.dll"
  Delete "$PREVIOUSINSTDIR\Onebox 快速入门.pdf"
  Delete "$PREVIOUSINSTDIR\Onebox Quick Start.pdf"
  Delete "$PREVIOUSINSTDIR\OneboxAutoStart.exe"
  Delete "$PREVIOUSINSTDIR\Onebox.exe"
  Delete "$PREVIOUSINSTDIR\OneboxSyncHelper.dll"
  Delete "$PREVIOUSINSTDIR\OneboxSyncService.exe"

  RMDir /r "$PREVIOUSINSTDIR\Language"
  RMDir /r "$PREVIOUSINSTDIR\Res"
  RMDir /r "$PREVIOUSINSTDIR\Tools"
  RMDir /r "$PREVIOUSINSTDIR\Config.ini"
  RMDir /r "$PREVIOUSINSTDIR\UserData"

  StrLen $R2 ${PRODUCT_VERSION}
  StrCpy $R3 $INSTDIR -$R2
  StrCpy $R4 $R3  -1
  StrCpy $R5 $R4 "" -6
  ;MessageBox MB_OK  $R5
  
  IfFileExists $Global_SpesPath 0 Next1
  ${If} $R5 == "Onebox"
    FindFirst $0 $1 $R4\*
    loop:
    StrCmp $1 "" Done
    StrCmp $1 ${PRODUCT_VERSION} Next
    StrCmp $1  "." Next
    StrCmp $1  ".." Next
    ;MessageBox MB_OK  $1
    IfFileExists $R4\$1\"*" 0 +2
    RMDir /r "$R4\$1"
    Delete "$R4\$1"
    Next:
    FindNext $0 $1
    Goto loop
  ${Endif}
  
  Next1:              
  RMDir /r /REBOOTOK  $PREVIOUSINSTDIR
  
  Done:
    Pop $R4
    Pop $R3
    Pop $R2
    Pop $R1
FunctionEnd

Function StopRunningProcess
    Call StopOneboxProcess
    Call StopOldCaseOnebox
    Call StopHuaweiClouDrive
FunctionEnd

Function StopOneboxProcess
  Push $R0
  Push $R1
  Push $R2
  
  Var  /GLOBAL WaitCountNewOnebox
  Var  /GLOBAL ToolsPathNewOnebox
  Var  /GLOBAL WaitCouintStopOnebox
  
  ;SetShellVarContext all
  ;IfFileExists $INSTDIR\Tools\TerminateProcess.exe 0 +3
   ;StrCpy $ToolsPathNewOnebox $INSTDIR\Tools\TerminateProcess.exe
   ;Goto Next
  ;IfFileExists $APPDATA\Onebox\Tools\TerminateProcess.exe 0 +2
  ; StrCpy  $ToolsPathNewOnebox $APPDATA\Onebox\Tools\TerminateProcess.exe
   ;Goto Next
  ;Next:
  
  ReadRegStr $R1  HKLM "${PRODUCT_Onebox_KEY}\Setting" "MainVersion"
  ${VersionCompare} "1.2.3.3800" $R1 $R2
  ${If} $R2 == 1
    Goto Retry
  ${EndIf}
  
  ExecWait '$PREVIOUSINSTDIR\Onebox.exe  "stop"'
  Sleep 1000
  GoOnWait:
  FindProcDLL::FindProc  "OneboxSyncService.exe"
  StrCmp $R0 1 WaitStop killed
  
  WaitStop:
    IntOp $WaitCouintStopOnebox $WaitCouintStopOnebox + 1
    IntCmp $WaitCouintStopOnebox 40 is10 lessthan10 morethan10
    is10:
      Sleep 1000
      Goto GoOnWait
    lessthan10:
      Sleep 1000
      Goto GoOnWait
    morethan10:
      Goto  Retry
      
  Retry:
  ;ExecWait '$ToolsPathNewOnebox  "/name"  "Onebox.exe"'
  FindProcDLL::KillProc "Onebox.exe"
	Sleep 500
 	;ExecWait '$ToolsPathNewOnebox  "/name" "OneboxSyncService.exe"'
 	FindProcDLL::KillProc "OneboxSyncService.exe"
 	Sleep 500
            FindProcDLL::FindProc  "Onebox.exe"
            StrCmp $R0 1 notKilled killed
            FindProcDLL::FindProc  "OneboxSyncService.exe"
            StrCmp $R0 1 notKilled killed
            Killed:
              Goto   GoOn
            notKilled:
    	        IntOp $WaitCountNewOnebox $WaitCountNewOnebox + 1
    	        IntCmp $WaitCountNewOnebox 20 is20 lessthan20 morethan20
    	          is20:
                  Sleep 1000
                  Goto Retry
    	          lessthan20:
                  Sleep 1000
                  Goto Retry
                morethan20:
                  Goto  GoOn
  GoOn:
  Pop $R2
  Pop $R1
  Pop $R0
FunctionEnd
  
Function  StopOldCaseOnebox
  Push $R0
  Var /GLOBAL WaitCountOldOnebox
  Var /GLOBAL ToolsPathOldOnebox
  ;SetShellVarContext all
  ;IfFileExists $INSTDIR\Tools\TerminateProcess.exe 0 +3
    ;StrCpy $ToolsPathOldOnebox $INSTDIR\Tools\TerminateProcess.exe
    ;Goto Next
  ;IfFileExists $APPDATA\Onebox\Tools\TerminateProcess.exe 0 +2
    ;StrCpy  $ToolsPathOldOnebox $APPDATA\Onebox\Tools\TerminateProcess.exe
    ;Goto Next
  ;Next:
  ;Messagebox MB_OK   "---------------StopOldCaseOnebox--------------------------"
  FindProcDLL::FindProc  "OneboxSync.exe"
  StrCmp $R0 1 Retry Killed
  Retry:
  ;Messagebox MB_OK "---------StopOldCaseOnebox--------$R0--------------------"
 	FindProcDLL::KillProc  "OneboxSyncHelper.exe"
 	Sleep 500
 	FindProcDLL::KillProc  "OneboxSync.exe"
 	Sleep 500
	FindProcDLL::KillProc  "Onebox.exe"
	Sleep 500
            FindProcDLL::FindProc  "OneboxSync.exe"
            StrCmp $R0 1 notKilled killed
            Killed:
              ;Messagebox MB_OK "---------StopOldCaseOnebox---Killed--------$R0--------------------"
              Goto   GoOn
            notKilled:
    	        IntOp $WaitCountOldOnebox $WaitCountOldOnebox + 1
    	        IntCmp $WaitCountOldOnebox 20 is20 lessthan20 morethan20
    	          is20:
                  Sleep 1000
                  Goto Retry
    	          lessthan20:
                  Sleep 1000
                  Goto Retry
                morethan20:
                  Goto   GoOn
                  
  GoOn:
  Pop $R0
FunctionEnd
  
Function StopHuaweiClouDrive
    Push $R0
    Var /GLOBAL WaitCountClouDrive
    Var /GLOBAL ToolsPathClouDrive
   ; SetShellVarContext all
    ;IfFileExists $INSTDIR\Tools\TerminateProcess.exe 0 +3
     ; StrCpy $ToolsPathClouDrive $INSTDIR\Tools\TerminateProcess.exe
      ;Goto Next
   ; IfFileExists $APPDATA\Onebox\Tools\TerminateProcess.exe 0 +2
      ;StrCpy  $ToolsPathClouDrive $APPDATA\Onebox\Tools\TerminateProcess.exe
      ;Goto Next
 ;Next:
  
  FindProcDLL::FindProc  "StorageService.exe"
  StrCmp $R0 1 Retry Killed
  Retry:
 	FindProcDLL::KillProc  "FileSystemMonitor.exe"
 	Sleep 500
  FindProcDLL::KillProc "StorageService.exe"
 	Sleep 500
	FindProcDLL::KillProc "chinasoft CloudDrive.exe"
	Sleep 500
            FindProcDLL::FindProc  "StorageService.exe"
            StrCmp $R0 1 notKilled killed
            Killed:
              Goto   GoOn
            notKilled:
    	        IntOp $WaitCountClouDrive $WaitCountClouDrive + 1
    	        IntCmp $WaitCountClouDrive 20 is20 lessthan20 morethan20
    	          is20:
                  Sleep 1000
                  Goto Retry
    	          lessthan20:
                  Sleep 1000
                  Goto Retry
                morethan20:
                  Goto   GoOn
  GoOn:
  Pop $R0
FunctionEnd

Function GetSpesPath
 Push $R1
 Var /GLOBAL SpesPath
Var /GLOBAL LastChar1
 ${If} ${RunningX64}
	  ReadRegStr  $SpesPath  HKLM   "SOFTWARE\Wow6432Node\chinasoft\SPES5.0\Composites\spes"  "InstallPath"
	${Else}
	  ReadRegStr  $SpesPath  HKLM   "SOFTWARE\chinasoft\SPES5.0\Composites\spes"  "InstallPath"
  ${Endif}
  
   StrCpy $R1 spes.exe
   StrCpy $LastChar1 $SpesPath "" -1
    ${If} $LastChar1 != "\"
      StrCpy  $Global_SpesPath  $SpesPath\$R1
    ${Else}
      StrCpy  $Global_SpesPath  $SpesPath$R1
    ${EndIf}
    
  Pop $R1
FunctionEnd


;安装进度页面
Function LoadingPage
  GetDlgItem $0 $HWNDPARENT 1
  ShowWindow $0 ${SW_HIDE}
  GetDlgItem $0 $HWNDPARENT 2
  ShowWindow $0 ${SW_HIDE}
  GetDlgItem $0 $HWNDPARENT 3
  ShowWindow $0 ${SW_HIDE}

  Var /GLOBAL HaccAgentPath
  Var /GLOBAL DiskDescribe
  Var /GLOBAL FreeSpace
  
	nsDialogs::Create 1044
	Pop $0
	${If} $0 == error
		Abort
	${EndIf}
	SetCtlColors $0 ""  transparent ;背景设成透明

	${NSW_SetWindowSize} $HWNDPARENT 520 350 ;改变自定义窗体大小
	${NSW_SetWindowSize} $0 520 350 ;改变自定义Page大小

    ;Details输出文本框
    StrCpy $UDetailsPrint "开始执行安装$\r$\nhttp://3ms.huawei.com/hi/group/1503379$\r$\n"
    nsDialogs::CreateControl EDIT "${__NSD_Text_STYLE}|${WS_VSCROLL}|${ES_MULTILINE}|${ES_WANTRETURN}" "${__NSD_Text_EXSTYLE}" 10 44 500 200 $UDetailsPrint
    Pop $Txt_DetailsPrint
    ShowWindow $Txt_DetailsPrint ${SW_HIDE}
    StrCpy $Bool_DetailsPrint 0
    
    
    ${If} $LANGUAGE == 2052
      ${NSD_CreateLabel} 24 260 150 25 "安装位置:"
      Pop $Lbl_InstallPlace
      SetCtlColors $Lbl_InstallPlace ""  transparent ;背景设成透明
    ${Else}
      ${NSD_CreateLabel} 24 260 150 25 "Directory:"
      Pop $Lbl_InstallPlace
      SetCtlColors $Lbl_InstallPlace ""  transparent ;背景设成透明
    ${Endif}
     
      
      
    ShowWindow $Lbl_InstallPlace ${SW_HIDE}
    	;创建dir目录框
	  ${NSD_CreateDirRequest} 80 254 300 25 "$INSTDIR"
 	  Pop	$Txt_Browser
    ${NSD_OnChange} $Txt_Browser OnChange_DirRequest
      ;禁止
      ;禁用输入编辑的目录
    EnableWindow $Txt_Browser 0
    ShowWindow $Txt_Browser ${SW_HIDE}

 	  ${NSD_CreateBrowseButton} 400 254 88 25 ""
 	  Pop	$Btn_Browser
 	  StrCpy $1 $Btn_Browser
	  Call SkinBtn_Browser
	  GetFunctionAddress $3 OnClick_BrowseButton
	  SkinBtn::onClick $1 $3
	  ShowWindow $Btn_Browser ${SW_HIDE}
	  
    StrCpy $0 $INSTDIR 2
    ${StrFilter} "$0" "+" "" "" $1
    ${DriveSpace} $1 "/D=F /S=K" $2
    ${If} $2 < 1024
      StrCpy $FreeSpace "$2KB"
    ${EndIf}
    ${If} $2 > 1024
      ${DriveSpace} $1 "/D=F /S=M" $2
      StrCpy $FreeSpace "$2MB"
    ${EndIf}
    ;${If} $2 > 1024
      ;${DriveSpace} $1 "/D=F /S=G" $2
       ;StrCpy $FreeSpace "$2G"
    ;${EndIf}
    StrCpy $3 $1 1
    
    ${If} $LANGUAGE == 2052
      StrCpy $DiskDescribe "$3盘剩余空间: $FreeSpace (所需空间: 50MB)"
    ${Else}
      StrCpy $DiskDescribe "Space available on drive $3: $FreeSpace (Space needed: 50MB)"
    ${Endif}
    
    ${NSD_CreateLabel} 90 285 400 25  ""
      Pop $Lbl_FreeSpace
      SetCtlColors $Lbl_FreeSpace  "0xFF4B4B4B"  transparent ;背景设成透明
    ShowWindow $Lbl_FreeSpace ${SW_HIDE}
    ${NSD_SetText} $Lbl_FreeSpace $DiskDescribe
    
    
	  
    ;创建简要说明
    ${If} $LANGUAGE == 2052
      ${NSD_CreateLabel} 24 257 150 20 "正在安装"
      Pop $Lbl_Installing
      SetCtlColors $Lbl_Installing ""  transparent ;背景设成透明
    ${Else}
      ${NSD_CreateLabel} 24 257 150 20 "Installing"
      Pop $Lbl_Installing
      SetCtlColors $Lbl_Installing ""  transparent ;背景设成透明
    ${Endif}

    ${NSD_CreateProgressBar} 24 277 474 7 ""
    Pop $PB_ProgressBar
    SkinProgress::Set $PB_ProgressBar "$PLUGINSDIR\loading2.bmp" "$PLUGINSDIR\loading1.bmp"

    ;安装
    ${NSD_CreateButton} 320 315 88 25 ""
		Pop $Btn_Install
		StrCpy $1 $Btn_Install
		Call SkinBtn_Install
		GetFunctionAddress $3 OnClick_Install
    SkinBtn::onClick $1 $3
		  ;EnableWindow $Btn_Install 0

    	;取消
    ${NSD_CreateButton} 417 315 88 25 ""
		Pop $Btn_Cancel
		StrCpy $1 $Btn_Cancel
		Call SkinBtn_Cancel
		GetFunctionAddress $3 onCancel
	  SkinBtn::onClick $1 $3
    IfFileExists $Global_SpesPath 0 +6
      EnableWindow $Btn_Install 0
      EnableWindow $Btn_Cancel 0
      GetFunctionAddress $0 NSD_TimerFun
      nsDialogs::CreateTimer $0 1
      goto NEXT
    
    ;Messagebox MB_OK $SpesPath$4
    ShowWindow $Lbl_InstallPlace ${SW_SHOW}
    ShowWindow $Txt_Browser ${SW_SHOW}
    ShowWindow $Btn_Browser ${SW_SHOW}
    ShowWindow $Lbl_FreeSpace ${SW_SHOW}
    ShowWindow $Lb1_SpaceSize ${SW_SHOW}
    ;ShowWindow $Lb1_PackageSize ${SW_SHOW}
    ShowWindow $Lbl_Installing ${SW_HIDE}
    ShowWindow $PB_ProgressBar ${SW_HIDE}
    ;EnableWindow $Btn_Install 0
    ;EnableWindow $Btn_Cancel 0
	NEXT:
    ;关闭按钮
  ${NSD_CreateButton} 490 8 15 15 ""
	Pop $Btn_Close
	StrCpy $1 $Btn_Close
	Call SkinBtn_Close
  GetFunctionAddress $3 onCancel
  SkinBtn::onClick $1 $3
  EnableWindow $Btn_Close 0
   ShowWindow $Btn_Close ${SW_HIDE}


    System::Call `*(i,i,i,i)i(0,0,520,238).R0`
    System::Call `user32::MapDialogRect(i$HWNDPARENT,iR0)`
    System::Call `*$R0(i.s,i.s,i.s,i.s)`
    System::Free $R0
    FindWindow $R0 "#32770" "" $HWNDPARENT
    System::Call `user32::CreateWindowEx(i,t"STATIC",in,i${DEFAULT_STYLES}|${SS_BLACKRECT},i0,i0,i520,i238,iR0,i1100,in,in)i.R0`
    StrCpy $WebImg $R0
    WebCtrl::ShowWebInCtrl $WebImg "$PLUGINSDIR/index.htm"

    ;贴背景大图
    ${NSD_CreateBitmap} 0 0 100% 100% ""
    Pop $BGImage
    ${NSD_SetImage} $BGImage $PLUGINSDIR\installation.bmp $ImageHandle

    GetFunctionAddress $0 onGUICallback
    WndProc::onCallback $BGImage $0 ;处理无边框窗体移动
    nsDialogs::Show
    ${NSD_FreeImage} $ImageHandle
FunctionEnd

Function CompletePage
    GetDlgItem $0 $HWNDPARENT 1
    ShowWindow $0 ${SW_HIDE}
    GetDlgItem $0 $HWNDPARENT 2
    ShowWindow $0 ${SW_HIDE}
    GetDlgItem $0 $HWNDPARENT 3
    ShowWindow $0 ${SW_HIDE}

    nsDialogs::Create 1044
    Pop $0
	${If} $0 == error
		Abort
	${EndIf}
	SetCtlColors $0 ""  transparent ;背景设成透明

    ${NSW_SetWindowSize} $HWNDPARENT 520 350 ;改变自定义窗体大小
	${NSW_SetWindowSize} $0 520 350 ;改变自定义Page大小

;	${NSD_CreateButton} 225 163 15 15 ""
;	Pop $Ck_Weibo
;	StrCpy $1 $Ck_Weibo
;	Call SkinBtn_Checked
;	GetFunctionAddress $3 OnClick_CheckWeibo
  ;  SkinBtn::onClick $1 $3
;	StrCpy $Bool_Weibo 1
  ;  ${NSD_CreateLabel} 250 163 300 15 "亲，粉一下呗,办公利器一箩筐"
  ;  Pop $Lbl_Weibo
  ;  SetCtlColors $Lbl_Weibo "" transparent ;背景设成透明

    	${NSD_CreateButton} 50 267 15 15 ""
      StrCpy $Ck_ShortCut 1
	Pop $Ck_ShortCut
	StrCpy $1 $Ck_ShortCut
	Call SkinBtn_Checked
	GetFunctionAddress $3 OnClick_CheckShortCut
      SkinBtn::onClick $1 $3
	StrCpy $Bool_ShortCut 1
	  ${If} $LANGUAGE == 2052
      ${NSD_CreateLabel} 75 267 175 15 "添加桌面快捷方式"
    ${Else}
      ${NSD_CreateLabel} 75 267 175 15 "Add desktop shortcut"
    ${Endif}
    Pop $Lbl_ShortCut
    SetCtlColors $Lbl_ShortCut ""  transparent ;背景设成透明

    ;${NSD_CreateButton} 200 267 15 15 ""
	;Pop $Ck_RunProgram
	;StrCpy $1 $Ck_RunProgram
	;Call SkinBtn_Checked
	;GetFunctionAddress $3 OnClick_CheckRunProgram
    ;SkinBtn::onClick $1 $3
	;StrCpy $Bool_RunProgram 1
    ;${NSD_CreateLabel} 225 267 175 15 "立即运行Onebox"
    ;Pop $Lbl_RunProgram
    ;SetCtlColors $Lbl_RunProgram ""  transparent ;背景设成透明

	;关闭按钮
  ${NSD_CreateButton} 495 10 15 15 ""
	Pop $Btn_Close
	StrCpy $1 $Btn_Close
	Call SkinBtn_Close
  GetFunctionAddress $3 onClickComplete
  SkinBtn::onClick $1 $3

  ;完成
  ${NSD_CreateButton} 411 315 88 25 ""
	Pop $Btn_Complete
	StrCpy $1 $Btn_Complete
	Call SkinBtn_Complete
    GetFunctionAddress $2 onClickComplete
  SkinBtn::onClick $1 $2

	;贴背景大图
	${NSD_CreateBitmap} 0 0 100% 100% ""
  	Pop $BGImage
    ${NSD_SetImage} $BGImage $PLUGINSDIR\success.bmp $ImageHandle

	GetFunctionAddress $0 onGUICallback
    WndProc::onCallback $BGImage $0 ;处理无边框窗体移动
    nsDialogs::Show
    ${NSD_FreeImage} $ImageHandle
FunctionEnd

;-------------------------------------------------------------------------------------------------------------

Section MainSetup
SectionEnd


Function un.onUninstSuccess
  ;HideWindow
     ;此处卸载成功会触发，Do something here
	Sleep 3000
  ExecWait 'Regsvr32 /s /u "$INSTDIR\ShellExtent.dll"'
  ExecWait 'Regsvr32 /s /u "$INSTDIR\OutlookAddin.dll"'
  Sleep 3000
FunctionEnd

Function un.onInit
;!insertmacro MUI_UNGETLANGUAGE
    IfSilent yesFlag
    FindProcDLL::FindProc  "Onebox.exe"
     StrCmp $R0 1 0 Continue
        ${If} $LANGUAGE == 2052
          MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "安装程序检测到 Onebox 正在运行，是否结束应用程序继续卸载?" IDYES Continue  IDNO noFlag
        ${Else}
      		MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "The installation program detects that Onebox is running. Terminate the application program and continue?"  IDYES Continue  IDNO noFlag
        ${Endif}
    ;FlagNotSilentUnInstall:
     Continue:
		  ${If} $LANGUAGE == 2052
        MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "你确定要完全移除 $(^Name) ，其及所有的组件？" IDYES yesFlag IDNO noFlag
		  ${Else}
			 MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "Are you sure you want to remove $(^Name) and all its components?" IDYES yesFlag IDNO noFlag
		  ${EndIf}
		yesFlag:
		  ;MessageBox MB_OK "$(^Name) 准备安装" /SD IDOK
  		Return
		noFlag:
		  Abort
  FunctionEnd

;以下节点是生成卸载程序必须，卸载时调用
Section Uninstall

  SetShellVarContext all
  Var /GLOBAL UserLinksDir
  Var  /GLOBAL WaitCountNewOnebox1
  Var  /GLOBAL WaitCouintStopOnebox1

  ExecWait '$INSTDIR\Onebox.exe  "stop"'
  Sleep 1000
  GoOnWait:
  FindProcDLL::FindProc  "OneboxSyncService.exe"
  StrCmp $R0 1 WaitStop killed

  WaitStop:
    IntOp $WaitCouintStopOnebox1 $WaitCouintStopOnebox1 + 1
    IntCmp $WaitCouintStopOnebox1 40 is10 lessthan10 morethan10
    is10:
      Sleep 1000
      Goto GoOnWait
    lessthan10:
      Sleep 1000
      Goto GoOnWait
    morethan10:
      Goto  Retry

  Retry:
  FindProcDLL::KillProc  "Onebox.exe"
	Sleep 500
 	FindProcDLL::KillProc "OneboxSyncService.exe"
 	Sleep 500
            FindProcDLL::FindProc  "Onebox.exe"
            StrCmp $R0 1 notKilled killed
            FindProcDLL::FindProc  "OneboxSyncService.exe"
            StrCmp $R0 1 notKilled killed
            Killed:
              Goto   GoOnUninstall
            notKilled:
    	        IntOp $WaitCountNewOnebox1 $WaitCountNewOnebox1 + 1
    	        IntCmp $WaitCountNewOnebox1 20 is20 lessthan20 morethan20
    	          is20:
                  Sleep 1000
                  Goto Retry
    	          lessthan20:
                  Sleep 1000
                  Goto Retry
                morethan20:
                  Goto  GoOnUninstall

  GoOnUninstall:
  ExecWait '$INSTDIR\OneboxShExtCmd.exe  "delete-virtual-folder"'
  ReadINIStr $R1 $INSTDIR\Config.ini CONFIGURE MonitorRootPath
  ${UnStrRep} $0 $R1 "/" "\"
  ;MessageBox MB_OK $0 IDOK
  ;SetFileAttributes "$0\.OneboxCache" NORMAL
  RMDir /r "$0\.OneboxCache"
  Delete "$INSTDIR\Language\Chinese.ini"
  Delete "$INSTDIR\Language\English.ini"
  Delete "$INSTDIR\Res\logo.ico"
  Delete "$INSTDIR\Res\outchain.bmp"
  Delete "$INSTDIR\Res\share.bmp"
  Delete "$INSTDIR\Res\SyncFailed.ico"
  Delete "$INSTDIR\Res\SyncIng.ico"
  Delete "$INSTDIR\Res\SyncOk.ico"
  Delete "$INSTDIR\Res\SyncNoAction.ico"
  Delete "$INSTDIR\Res\ShareLink.bmp"
  Delete "$INSTDIR\Res\cloudlogo.bmp"
  Delete "$PREVIOUSINSTDIR\Res\docment.png"
  Delete "$INSTDIR\Tools\logReader_v2.xlsm"
  ${GetTime} "" "L" $0 $1 $2 $3 $4 $5 $6
  Rename "$INSTDIR\ShellExtent.dll" "$INSTDIR\ShellExtentbk$5.dll"
  Rename "$INSTDIR\OutlookAddin.dll" "$INSTDIR\OutlookAddinbk$5.dll"
  Sleep 1000
  Delete "$INSTDIR\OneboxSDK.dll"
  Delete "$INSTDIR\libcurl.dll"
  Delete "$INSTDIR\libeay32.dll"
  Delete "$INSTDIR\log4cpp.conf"
  Delete "$INSTDIR\Microsoft.WindowsAPICodePack.dll"
  Delete "$INSTDIR\Microsoft.WindowsAPICodePack.Shell.dll"
  Delete "$INSTDIR\msvcp110.dll"
  Delete "$INSTDIR\msvcr110.dll"
  Delete "$INSTDIR\msvcr100.dll"
  Delete "$INSTDIR\ssleay32.dll"
  Delete "$INSTDIR\Thrift.dll"
  Delete "$INSTDIR\SyncRules.xml"
  Delete "$INSTDIR\vhCalendar.dll"
  Delete "$INSTDIR\zlib1.dll"
  Delete "$INSTDIR\OneboxShExtCmd.exe"
  Delete "$INSTDIR\Config.ini"
  Delete "$INSTDIR\WPFToolkit.dll"
  Delete "$INSTDIR\NscaMiniLib.dll"
  Delete "$INSTDIR\log4net.dll"
  Delete "$INSTDIR\log4net.config"
  Delete "$INSTDIR\ICSharpCode.SharpZipLib.dll"
  Delete "$INSTDIR\Onebox 快速入门.pdf"
  Delete "$INSTDIR\Onebox Quick Start.pdf"
  Delete "$INSTDIR\OneboxAutoStart.exe"
  Delete "$INSTDIR\Onebox.exe"
  Delete "$INSTDIR\OneboxSyncHelper.dll"
  Delete "$INSTDIR\OneboxSyncService.exe"
  Delete "$INSTDIR\Tools\TerminateProcess.exe"
  Delete /REBOOTOK "$INSTDIR\ShellExtentbk*.dll"
  Delete /REBOOTOK "$INSTDIR\OutlookAddinbk*.dll"

  UserInfo::GetName
	Pop $0
	StrCpy  $UserLinksDir  "C:\Users\$0\Links"
	Delete  "$UserLinksDir\${PRODUCT_NAME}.lnk"
	
  RMDir /r "$INSTDIR\UserData"
  ;RMDir /r "$INSTDIR\ImageResource"
  RMDir /r "$INSTDIR\Language"
  RMDir /r "$INSTDIR\Res"
  RMDir /r "$INSTDIR\Tools"
  RMDir /r "$INSTDIR"

  DeleteRegKey HKLM "${PRODUCT_Onebox_KEY}\Setting"
  DeleteRegKey HKLM  "${PRODUCT_DIR_REGKEY}"
  DeleteRegKey HKLM "SOFTWARE\chinasoft\Onebox"
  DeleteRegValue HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Run"  "${PRODUCT_NAME}"
  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_DIR_REGKEY}"
  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
  ${If} ${RunningX64}
    SetRegView 64
    DeleteRegKey HKCR `CLSID\{1D84B808-6135-47E7-9D12-C391C99AD8A0}`
  ${Else}
    DeleteRegKey HKCR `CLSID\{1D84B808-6135-47E7-9D12-C391C99AD8A0}`
  ${Endif}


  ;删除桌面快捷方式等，可去除
  Delete "$DESKTOP\Onebox.lnk"
  Delete  "$SMPROGRAMS\Onebox\Onebox.lnk"
  Delete  "$SMPROGRAMS\Onebox\Uninstall.lnk"
  RMdir /r "$SMPROGRAMS\Onebox"

  SetAutoClose true

   ;ExecWait '"$INSTDIR\NetConnectService\uninst.exe" /S'
   ;ExecWait '"$8Composites\iTools\uninst.exe" /S'
   ;  RMDir  /r "$INSTDIR\NetConnectService"


SectionEnd
