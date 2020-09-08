
Var MSG
Var Dialog
Var BGImage
Var MiddleImage
Var ImageHandle
Var Btn_Welcome_Close
Var Btn_Cancel
Var Bool_License
Var Btn_Complete_Close
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
;!define MUI_ABORTWARNING
;安装图标的路径名字
!define MUI_ICON "images\fav.ico"
;卸载图标的路径名字
!define MUI_UNICON "images\uninstall.ico"
;产品协议书的路径名字
;!define MUI_PAGE_LICENSE_RTY "license\license.rtf"
;!define EM_OUTFILE_NAME "Onebox_Installer.exe"

!define PRODUCT_NAME "Onebox Mate"
!define PRODUCT_EXECUTE_NAME  "Onebox Mate.exe"
!define PRODUCT_VERSION 1.0.0.0
!define PRODUCT_PUBLISHER "Huawei company, Inc."
!define PRODUCT_WEB_SITFE "http://nshelp.huawei.com"
!define PRODUCT_DIR_REGKEY "Software\Microsoft\Windows\CurrentVersion\App Paths\${PRODUCT_EXECUTE_NAME}" ;请更改
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}Cloud"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"

BrandingText "Onebox, A desktop managment software"
; Language Selection Dialog Settings
!define MUI_LANGDLL_REGISTRY_ROOT "${PRODUCT_UNINST_ROOT_KEY}"
!define MUI_LANGDLL_REGISTRY_KEY "${PRODUCT_UNINST_KEY}"
!define MUI_LANGDLL_REGISTRY_VALUENAME "NSIS:Language"
!define PRODUCT_Onebox_KEY  "SOFTWARE\Huawei\OneboxApp\Onebox"
!define /math PBM_SETRANGE32 ${WM_USER} + 6

;Config Files and Resource Files
!define CONF_ONEBOX_CONFIGFILE "Config.ini"
!define RES_LANGUAGE_DIR "Language"
!define RES_RES_DIR "Res"
!define RES_SKIN_DIR "Skin"
!define ONEBOX_CRT "onebox.crt"
!define CONF_INSTALL_CONFIGFILE "Install.ini"
!define CONF_INSTALLINFO_SECTION "INSTALLINFO"
!define CONF_PATH_KEY "path"
!define CONF_CREATESHORTCUT_KEY "CreateShortCut"
!define CONF_AUTOSTART_KEY "AutoStart"
!define CONF_SELECTPATH_KEY "SelectPath"

;Version Information
VIProductVersion ${PRODUCT_VERSION}
VIAddVersionKey /LANG=1033 "ProductName" "Onebox"
VIAddVersionKey /LANG=1033 "Comments" "Huawei desktop management application"
VIAddVersionKey /LANG=1033 "CompanyName" "Huawei Technologies"
VIAddVersionKey /LANG=1033 "LegalTrademarks" "Huawei Technologies"
VIAddVersionKey /LANG=1033 "LegalCopyright" "Huawei Technologies Co., Ltd. 2014-2047. All rights reserved"
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
;Page custom LoadingPage
;Page custom CompletePage

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
;RequestExecutionLevel admin
;------------------------------------------------------MUI 现代界面定义以及函数结束------------------------
;应用程序显示名字
;Name "Onebox"
;应用程序输出路径
;OutFile "${EM_OUTFILE_NAME}"
;InstallDir "$PROGRAMFILES\Onebox"
Name "${PRODUCT_NAME}_V${PRODUCT_VERSION}"
OutFile "Onebox_Mate_V${PRODUCT_VERSION}_Setup.exe"  ;请更改输出安装包文件名，可以指定输出到某目录
InstallDirRegKey HKCU "${PRODUCT_DIR_REGKEY}" ""
InstallDir  "$PROGRAMFILES\Huawei\OneboxApp\Onebox\${PRODUCT_VERSION}"
ShowInstDetails show
ShowUninstDetails show
;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "$INSTDIR"

Function .onInit
    System::Call 'kernel32::CreateMutexA(i 0,i 0,t "SMAPLE_MUTEX") i .r1 ?e'
      Pop $R0
      StrCmp $R0 0 +2
        Abort
    ;IfFileExists "$INSTDIR\Install.log" 0 +3
      ;${GetTime} "" "L" $0 $1 $2 $3 $4 $5 $6
      ;Rename "$INSTDIR\Install.log" "$INSTDIR\Install$5.log"
      InitPluginsDir
      Call CheckOSVersion
      ;Call CheckIsCloudMachine
      
      var /GLOBAL PREVIOUSINSTDIR
      var /GLOBAL PREVIOUSVERSION
      var /GLOBAL INSTALLDIRDEFAULT
      Var /GLOBAL InstallSuccess

      StrCpy  $InstallSuccess "0"
      
      ClearErrors
      ReadRegStr $PREVIOUSVERSION  HKLM "${PRODUCT_Onebox_KEY}\Setting" "MainVersion"
      IfErrors 0 +2
      ReadRegStr $PREVIOUSVERSION  HKCU "${PRODUCT_Onebox_KEY}\Setting" "MainVersion"
      ClearErrors
      ReadRegStr  $PREVIOUSINSTDIR  HKLM   "${PRODUCT_Onebox_KEY}\Setting"  "AppPath"
      IfErrors 0 +2
      ReadRegStr  $PREVIOUSINSTDIR  HKCU   "${PRODUCT_Onebox_KEY}\Setting"  "AppPath"
      
      IfFileExists "$EXEDIR\${CONF_INSTALL_CONFIGFILE}"  0 +8
      ReadINIStr  $R5 $EXEDIR\${CONF_INSTALL_CONFIGFILE} ${CONF_INSTALLINFO_SECTION} ${CONF_PATH_KEY}
      ${If} $R5 == ""
        StrCpy $INSTALLDIRDEFAULT  "$PROGRAMFILES\Huawei\OneboxApp\Onebox\${PRODUCT_VERSION}"
        Goto GoOn
      ${Else}
        StrCpy $INSTALLDIRDEFAULT  $R5\${PRODUCT_VERSION}
        Goto GoOn
      ${Endif}
      StrCpy $INSTALLDIRDEFAULT  "$PROGRAMFILES\Huawei\OneboxApp\Onebox\${PRODUCT_VERSION}"
      
      GoOn:
      ;Call GetSpesPath
      ${If} $PREVIOUSINSTDIR == ""
        StrCpy $INSTDIR  $INSTALLDIRDEFAULT
      ${Else}
        ;IfFileExists $Global_SpesPath 0 +3
        ;StrCpy $INSTDIR $INSTALLDIRTEMP
        ;Goto Next
        StrCpy $R1 $PREVIOUSINSTDIR
        Loop:
        StrCpy $R2 $R1 "" -1
        ${If} $R2 != "\"
         StrLen $R3 $R1
         Intop $R4 $R3 - 1
         StrCpy $R1 $R1 $R4
         Goto Loop
        ${Else}
         Goto Next
        ${Endif}
        Next:
        StrCpy $INSTDIR  $R1${PRODUCT_VERSION}
      ${Endif}
      
      ;MessageBox MB_OK    "--------------$INSTALLDIRTEMP--------------"
      LogSet on
      StrCpy $Bool_ShortCut 1


       ;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "$PROGRAMFILES"
      ;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "$INSTDIR"
     ; MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "$PLUGINSDIR"
           ;File `/ONAME=$PLUGINSDIR\TerminateProcess.exe` `..\TerminateProcess.exe`
          ; File `/ONAME=$PLUGINSDIR\Config.ini` `..\Config.ini`
           
     ${If} $LANGUAGE == 2052
           File `/oname=$PLUGINSDIR\index.html` `images\index.html`
           CreateDirectory  $PLUGINSDIR\js
           File `/oname=$PLUGINSDIR\js\jquery-1.10.2.min.js` `images\js\jquery-1.10.2.min.js`
           CreateDirectory  $PLUGINSDIR\img
           File `/ONAME=$PLUGINSDIR\img\quit.bmp` `images\img\quit.bmp`
           File `/oname=$PLUGINSDIR\img\btn_cancel.bmp` `images\img\btn_cancel.bmp`
           File `/oname=$PLUGINSDIR\img\btn_ok.bmp` `images\img\btn_ok.bmp`
           File `/oname=$PLUGINSDIR\img\btn_close_welcome.bmp` `images\img\btn_close_welcome.bmp`
           File `/oname=$PLUGINSDIR\img\btn_close_complete.bmp` `images\img\btn_close_complete.bmp`
           File `/oname=$PLUGINSDIR\img\btn_install.bmp` `images\img\btn_install.bmp`
           File `/oname=$PLUGINSDIR\img\btn_complete.bmp` `images\img\btn_complete.bmp`
           File `/oname=$PLUGINSDIR\img\fish-water.png` `images\img\fish-water.png`
           File `/oname=$PLUGINSDIR\img\image.png` `images\img\image.png`
           File `/oname=$PLUGINSDIR\img\water01.png` `images\img\water01.png`
           File `/oname=$PLUGINSDIR\img\water02.png` `images\img\water02.png`
           File `/oname=$PLUGINSDIR\img\water03.png` `images\img\water03.png`
           File `/oname=$PLUGINSDIR\img\welcomebg.bmp` `images\img\welcomebg.bmp`
           File `/oname=$PLUGINSDIR\img\welcomebg_path.bmp` `images\img\welcomebg_path.bmp`
           SkinBtn::Init "$PLUGINSDIR\img\btn_ok.bmp"
           SkinBtn::Init "$PLUGINSDIR\img\btn_cancel.bmp"
           SkinBtn::Init "$PLUGINSDIR\img\btn_install.bmp"
	         SkinBtn::Init "$PLUGINSDIR\img\btn_complete.bmp"
      ${Else}
           File `/oname=$PLUGINSDIR\index.html` `images\index.html`
           CreateDirectory  $PLUGINSDIR\js
           File `/oname=$PLUGINSDIR\js\jquery-1.10.2.min.js` `images\js\jquery-1.10.2.min.js`
           CreateDirectory  $PLUGINSDIR\img
           File `/ONAME=$PLUGINSDIR\img\quit.bmp` `images\img\quit_en.bmp`
           File `/oname=$PLUGINSDIR\img\btn_cancel.bmp` `images\img\btn_cancel_en.bmp`
           File `/oname=$PLUGINSDIR\img\btn_ok.bmp` `images\img\btn_ok_en.bmp`
           File `/oname=$PLUGINSDIR\img\btn_close_welcome.bmp` `images\img\btn_close_welcome.bmp`
           File `/oname=$PLUGINSDIR\img\btn_close_complete.bmp` `images\img\btn_close_complete.bmp`
           File `/oname=$PLUGINSDIR\img\btn_install.bmp` `images\img\btn_install_en.bmp`
           File `/oname=$PLUGINSDIR\img\btn_complete.bmp` `images\img\btn_complete_en.bmp`
           File `/oname=$PLUGINSDIR\img\fish-water.png` `images\img\fish-water.png`
           File `/oname=$PLUGINSDIR\img\image.png` `images\img\image_en.png`
           File `/oname=$PLUGINSDIR\img\water01.png` `images\img\water01.png`
           File `/oname=$PLUGINSDIR\img\water02.png` `images\img\water02.png`
           File `/oname=$PLUGINSDIR\img\water03.png` `images\img\water03.png`
           File `/oname=$PLUGINSDIR\img\welcomebg.bmp` `images\img\welcomebg.bmp`
           File `/oname=$PLUGINSDIR\img\welcomebg_path.bmp` `images\img\welcomebg_path.bmp`
           SkinBtn::Init "$PLUGINSDIR\img\btn_ok.bmp"
           SkinBtn::Init "$PLUGINSDIR\img\btn_cancel.bmp"
           SkinBtn::Init "$PLUGINSDIR\img\btn_install.bmp"
	         SkinBtn::Init "$PLUGINSDIR\img\btn_complete.bmp"
      ${Endif}
   	  File `/oname=$PLUGINSDIR\img\process_color.bmp` `images\img\process_color.bmp`
      File `/oname=$PLUGINSDIR\img\process_bg.bmp` `images\img\process_bg.bmp`
      File `/oname=$PLUGINSDIR\img\ic_install_input.bmp` `images\img\ic_install_input.bmp`
      File `/oname=$PLUGINSDIR\img\ic_install_input_btn.bmp` `images\img\ic_install_input_btn.bmp`
		  SkinBtn::Init "$PLUGINSDIR\img\btn_close_welcome.bmp"
      SkinBtn::Init "$PLUGINSDIR\img\btn_close_complete.bmp"
		;LangString DirISEmpt ${LANG_SIMPCHINESE} "安装目录不为空，是否继续安装！"
           ; LangString DirISEmpt ${LANG_ENGLISH} "The diretory is not empty , Is Contiune?"
    ;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "$R0"
;检测安装程序
        ;Pop $R0
        ifsilent isSilent  noSilent
        isSilent:
        StrCpy $1 $INSTDIR 2
        StrCpy $R0 "$1\"      ;Drive letter
	      StrCpy $R1 "invalid"
        ${GetDrives} "HDD" "FindHDD"
        ;MessageBox MB_OK "Type of drive $R0 is $R1"
        ${If} $R1 == "invalid"
           Abort
        ${Endif}

        noSilent:
        ${GetParameters} $R0
        ${If} $R0 == "/U"
          Call UpdateNormalProc
          Quit
        ${Else}
          ${If} $R0 == "/F"
            Call UpdateForceProc
            Quit
          ${Else}
            IfFileExists "$PREVIOUSINSTDIR\Onebox.exe" 0 +3
              FindProcDLL::FindProc  "Onebox.exe"
              StrCmp $R0 1 Next1 0
            FindProcDLL::FindProc  "Onebox Mate.exe"
            StrCmp $R0 1 Next1 0
            FindProcDLL::FindProc  "Onebox_Mate.exe"
            Next1:
            ifsilent silentInit   UnSilentInit
            UnSilentInit:
              StrCmp $R0 1 0 no_run
              ${If} $LANGUAGE == 2052
                MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "安装程序检测到 Onebox Mate 正在运行，是否结束应用程序继续安装！" IDYES Continue  IDNO No_Continue
              ${Else}
      		      MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "The installation program detects that Onebox Mate is running. Terminate the program and continue the installation?" IDYES Continue  IDNO No_Continue
              ${Endif}
              No_Continue:
                Quit
              Continue:
                Call StopOneboxProcess
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
Function SkinBtn_Welcome_Close
  SkinBtn::Set /IMGID=$PLUGINSDIR\img\btn_close_welcome.bmp $1
FunctionEnd

Function SkinBtn_Complete_Close
  SkinBtn::Set /IMGID=$PLUGINSDIR\img\btn_close_complete.bmp $1
FunctionEnd

Function SkinBtn_Cancel
  SkinBtn::Set /IMGID=$PLUGINSDIR\img\btn_cancel.bmp $1
FunctionEnd

Function SkinBtn_Quit
  SkinBtn::Set /IMGID=$PLUGINSDIR\img\btn_ok.bmp $1
FunctionEnd

Function SkinBtn_Install
  SkinBtn::Set /IMGID=$PLUGINSDIR\img\btn_install.bmp $1
FunctionEnd

Function SkinBtn_Complete
	SkinBtn::Set /IMGID=$PLUGINSDIR\img\btn_complete.bmp $1
FunctionEnd

Function SkinBtn_Browser
  SkinBtn::Set /IMGID=$PLUGINSDIR\img\ic_install_input_btn.bmp $1
FunctionEnd

Function OnClickQuitOK                                                   
	Call onClickClose
FunctionEnd

;点击右上角关闭按钮
Function onClickClose
    Sleep 200
    ${If} $InstallSuccess == "1"
      IfFileExists "$EXEDIR\${CONF_INSTALL_CONFIGFILE}"  0 +4
      ReadINIStr $R0 $EXEDIR\${CONF_INSTALL_CONFIGFILE} ${CONF_INSTALLINFO_SECTION} ${CONF_CREATESHORTCUT_KEY}
      ${If} $R0 == "0"
        Goto Next
      ${Endif}
      Call IsUserAdmin
      Pop $R0
      ${If} $R0 == "true"
        SetShellVarContext all
        RMdir /r "$SMPROGRAMS\OneboxAPP"
        CreateDirectory "$SMPROGRAMS\OneboxAPP"
        CreateDirectory "$SMPROGRAMS\OneboxAPP\Onebox"
        CreateShortCut  "$SMPROGRAMS\OneboxAPP\Onebox\Onebox Mate.lnk" "$INSTDIR\Onebox Mate.exe"
        CreateShortCut  "$SMPROGRAMS\OneboxAPP\Onebox\Uninstall.lnk" "$INSTDIR\uninst.exe"
      ${Endif}
      CreateShortCut "$QUICKLAUNCH\${PRODUCT_NAME}.lnk" "$INSTDIR\${PRODUCT_EXECUTE_NAME}"
      CreateShortCut  "$DESKTOP\Onebox Mate.lnk" "$INSTDIR\Onebox Mate.exe"
      Sleep 500
    ${Endif}

    Next:
    SendMessage $HWNDPARENT ${WM_CLOSE} 0 0
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
Function onClickInstall
  call Check_Disk_Free_Space
  WebCtrl::ShowWebInCtrl $WebImg "$PLUGINSDIR/index.html"
  GetFunctionAddress $0 NSD_TimerFun1
  nsDialogs::CreateTimer $0 1000
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

	${NSW_SetWindowSize} $WarningForm 400 200
	EnableWindow $hwndparent 0
	System::Call `user32::SetWindowLong(i$WarningForm,i${GWL_STYLE},0x9480084C)i.R0`
	${NSW_CreateButton} 220 150 75 30 ''
	Pop $R0
	StrCpy $1 $R0
	Call SkinBtn_Quit
	${NSW_OnClick} $R0 OnClickQuitOK

	${NSW_CreateButton} 305 150 75 30 ''
	Pop $R0
	StrCpy $1 $R0
	Call SkinBtn_Cancel
	${NSW_OnClick} $R0 OnClickQuitCancel

	${NSW_CreateBitmap} 0 0 100% 100% ""
  	Pop $BGImage
  ${NSW_SetImage} $BGImage $PLUGINSDIR\img\quit.bmp $ImageHandle
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
FunctionEnd

Function OnClick_BrowseButton
  Pop $0
  Pop $1
  Pop $2
  Pop $3
  Pop $4
  Pop $5
  
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
  
  Call CheckSpecialChar
  Pop $R0
  ${If} $R0 == "true"
	   ${If} $LANGUAGE == 2052
        MessageBox MB_OK|MB_ICONEXCLAMATION|MB_TOPMOST "所选安装路径包含中文字符或者特殊字符，请重新选择。"  IDOK Next
      ${Else}
      	MessageBox MB_OK|MB_ICONEXCLAMATION|MB_TOPMOST "The selected path contain chinese or special char. Please select a path again."  IDOK Next
      ${Endif}
  ${Else}
    Goto Continue
  ${Endif}
  Next:
    Return
  Continue:
  ${If} $0 != ""
    ;MessageBox MB_OK  $INSTDIR
    StrCpy $1 $0 2
    ${StrFilter} "$1" "+" "" "" $2
    ${DriveSpace} $2 "/D=F /S=K" $3
    ${If} $3 < 1024
      StrCpy $FreeSpace1 "$2KB"
    ${EndIf}
    ${If} $3 > 1024
      ${DriveSpace} $2 "/D=F /S=M" $3
      StrCpy $FreeSpace1 "$2MB"
    ${EndIf}
    ;${If} $3 > 1024
      ;${DriveSpace} $2 "/D=F /S=G" $3
      ;StrCpy $FreeSpace1 "$2G"
    ;${EndIf}
    
    StrCpy $R0 "$1\"      ;Drive letter
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
	
	StrLen $1 $0
    ${If} $1 > 150
      ${If} $LANGUAGE == 2052
        MessageBox MB_OK "安装路径长度不能超过150个字符，请重新选择。"
        return
      ${Else}
        MessageBox MB_OK "The installation path length cannot exceed 150 characters."
        return
      ${EndIf}
    ${EndIf}
    
    StrCpy $LastChar $0 "" -1
    ${If} $LastChar == "\"
      StrCpy $INSTDIR "$0OneboxApp\${PRODUCT_VERSION}"
    ${Else}
      StrCpy $INSTDIR "$0\OneboxApp\${PRODUCT_VERSION}"
    ${EndIf}
    StrCpy $4 $2 1
    ;StrCpy $4 "$3盘剩余空间:$FreeSpace1(所需空间:50M)"
     ${If} $LANGUAGE == 2052
      StrCpy $5 "$4盘剩余空间: $FreeSpace1 (所需空间: 50MB)"
    ${Else}
      StrCpy $5 "Space available on drive $4: $FreeSpace1 (Space needed: 50MB)"
    ${Endif}
    ;MessageBox MB_OK  $DiskDescribe1
    ;${NSD_SetText} $Lbl_FreeSpace $4
    system::Call `user32::SetWindowText(i $Txt_Browser, t "$INSTDIR")`
    system::Call `user32::SetWindowText(i $Lbl_FreeSpace, t "$5")`
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

Function Check_Disk_Free_Space
	StrCpy $0 $INSTDIR 3
	${DriveSpace} $0 "/D=F /S=M" $R0
	${If} $R0 < 200
    IfSilent doAbort showTips
  	showTips:
  		${If} $LANGUAGE == 2052
  			MessageBox MB_OK|MB_ICONSTOP "检测到您所安装的磁盘剩余空间小于200MB，请重新选择安装目录。"
  		${Else}
  			MessageBox MB_OK|MB_ICONSTOP "The remaining space on the disk that you have installed is smaller than 200 MB. Please select a path again."
  		${Endif}
  		Abort
    doAbort:
      Abort
	${Endif}
	
FunctionEnd

Function Check_Disk_Free_Space_For_Update
	StrCpy $0 $INSTDIR 3
	${DriveSpace} $0 "/D=F /S=M" $R0
	${If} $R0 < 200
    IfSilent doAbort showTips
  	showTips:
  		${If} $LANGUAGE == 2052
  			MessageBox MB_OK|MB_ICONSTOP "安装目录所在磁盘剩余空间小于200MB，升级程序将退出。"
  		${Else}
  			MessageBox MB_OK|MB_ICONSTOP "The remaining space on the disk where the installation directory resides is smaller than 200 MB. The upgrade program will exit."
  		${Endif}
  		Abort
    doAbort:
      Abort
	${Endif}
	
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

Function CheckSpecialChar
  Pop $0
  Push $R0
  StrLen $1 $0
  StrCpy $2 ''

  ; 每个中文会给strlen增加2，所以copy 1个字符时，会遇到不可显示字符，会被NSIS自动改成?
  ; 正好?本身是非法路径，所以可以用这个来判断路径是否非法
  ${Do}
    IntOp $1 $1 - 1
    ${IfThen} $1 < 0 ${|}${ExitDo}${|}
    StrCpy $2 $0 1 $1
    ${IfThen} $2 == '?' ${|}${ExitDo}${|}
  ${Loop}

  ${If} $2  == '?'
    StrCpy $R0 "true"
  ${Else}
    StrCpy $R0 "false"
  ${EndIf}
   Exch $R0
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
    ShowWindow $PB_ProgressBar ${SW_SHOW}
       ;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "Herekkk"
    !if 1   ;是否在后台运行,1有效
        GetFunctionAddress $0 InstallationMainFun
        BgWorker::CallAndWait
    !else
        Call InstallationMainFun
    !endif
FunctionEnd

Function NSD_TimerFun1
    ShowWindow $Lbl_InstallPlace ${SW_HIDE}
    ShowWindow $Txt_Browser ${SW_HIDE}
    ShowWindow $Btn_Browser ${SW_HIDE}
    ShowWindow $Btn_Install ${SW_HIDE}
    ShowWindow $Btn_Welcome_Close ${SW_HIDE}
    GetFunctionAddress $0 NSD_TimerFun1
    nsDialogs::KillTimer $0
    ShowWindow $WebImg ${SW_SHOW}
    GetFunctionAddress $0 NSD_TimerFun
    nsDialogs::CreateTimer $0 4000
FunctionEnd

;点击最后完成
Function onClickComplete
      ;搬迁
    Exec  "$INSTDIR\Onebox Mate.exe"
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
    Call Check_Disk_Free_Space
    SendMessage $PB_ProgressBar ${PBM_SETRANGE32} 0 100  ;总步长为顶部定义值
		;MessageBox MB_OK "GO detect"
    Call Checkifinstall
	  Call StopOneboxProcess
	  
    ;MessageBox MB_OK "start Go on to install"
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 10 0
		SetOverwrite on
		  Call IsUserAdmin
      Pop $R0
      ${If} $R0 == "true"
        SetShellVarContext all
      ${Endif}
    ;ReadRegStr  $R1  HKLM   "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"  "UninstallString"
    ;${if} $R1 != ""
      ;Call DeleteNeedCoverFile
      ;Sleep 2000
    ;${Endif}

   ; ReadRegStr  $R1  HKLM   "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"  "UninstallString"
   ; ${If} $R1 != ""
     ; Call SaveConfigureAndData
    ;${Endif}

    SetOutPath "$INSTDIR\skin\"
      File /r ..\output\skin\*
      Sleep 1500
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 20 0
    
     SetOutPath "$INSTDIR\Res"
      File /r ..\output\Res\*
      Sleep 1000
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 30 0

     SetOutPath "$INSTDIR\Language"
      File /r ..\output\Language\*
      Sleep 1000
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 50 0

    SetOutPath "$INSTDIR\"
      ${GetTime} "" "L" $0 $1 $2 $3 $4 $5 $6
      Rename "$INSTDIR\ShellExtent.dll" "$INSTDIR\ShellExtentbk$5.dll"
      Rename "$INSTDIR\OutlookAddin.dll" "$INSTDIR\OutlookAddinbk$5.dll"
      Rename "$INSTDIR\OfficeAddin.dll" "$INSTDIR\OfficeAddinbk$5.dll"
      Rename "$INSTDIR\DuiLib.dll" "$INSTDIR\DuiLibbk$5.dll"
      Rename "$INSTDIR\msvcp110.dll" "$INSTDIR\msvcp110bk$5.dll"
      Rename "$INSTDIR\msvcr110.dll" "$INSTDIR\msvcr110bk$5.dll"
      Rename "$INSTDIR\HuaweiSecureC.dll" "$INSTDIR\HuaweiSecureCbk$5.dll"
      Rename "$INSTDIR\HuaweiSecureC_x64.dll" "$INSTDIR\HuaweiSecureC_x64bk$5.dll"
      File  /oname=Common.dll   ..\output\Common.dll
	  File  /oname=SystemInfo.dll   ..\output\SystemInfo.dll
      File  /oname=NetSDK.dll   ..\output\NetSDK.dll
      ;File  /oname=SyncService.dll   ..\output\SyncService.dll
      File /oname=DuiLib.dll    ..\output\DuiLib.dll
      File  /oname=libcurl.dll  ..\output\libcurl.dll
      File /oname=libeay32.dll   ..\output\libeay32.dll
      File /oname=log4cpp.conf  ..\output\log4cpp.conf
      File /oname=Label32.dll  ..\output\Label32.dll
      File /oname=msvcp110.dll  ..\output\msvcp110.dll
      File /oname=msvcr110.dll  ..\output\msvcr110.dll
      File /oname=msvcr100.dll  ..\output\msvcr100.dll
      File  /oname=ssleay32.dll   ..\output\ssleay32.dll
      File  /oname=NscaMiniLib.dll   ..\output\NscaMiniLib.dll
      File  /oname=DataCache.dll   ..\output\DataCache.dll
      File  /oname=MessageProxy.dll   ..\output\MessageProxy.dll
      File  /oname=OfficeAddin.dll   ..\output\OfficeAddin.dll
      File  /oname=OutlookAddin.dll   ..\output\OutlookAddin.dll
      File  /oname=BackupAll.dll   ..\output\BackupAll.dll
      File /oname=zlib1.dll  ..\output\zlib1.dll
      File /oname=KmcCbb.dll  ..\output\KmcCbb.dll
      File /oname=HuaweiSecureC.dll  ..\output\HuaweiSecureC.dll
      File "/oname=Onebox Mate.exe" "..\output\Onebox Mate.exe"
      File /oname=Config.ini  ..\output\Config.ini
      File  /oname=OneboxStart.exe   ..\output\OneboxStart.exe
      ;File  /oname=OneboxCMBAdapter.exe   ..\output\OneboxCMBAdapter.exe
      File  /oname=setting.xml   ..\output\setting.xml
      File  /oname=UpdateDB.dll   ..\output\UpdateDB.dll
      File  /oname=updateInfo.db   ..\output\updateInfo.db
      File  /nonfatal /oname=cmb.config.ini ..\output\cmb.config.ini
      File  /oname=DataTransfer.dll   ..\output\DataTransfer.dll
      ${If} ${RunningX64}
        File  /oname=ShellExtent.dll   ..\output\ShellExtent_x64.dll
        File /oname=HuaweiSecureC_x64.dll  ..\output\HuaweiSecureC_x64.dll
      ${Else}
        File  /oname=ShellExtent.dll   ..\output\ShellExtent.dll
      ${Endif}
      Sleep 1500
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 70 0
      IfFileExists  "$EXEDIR\${CONF_ONEBOX_CONFIGFILE}" 0 +2
      CopyFiles /SILENT   "$EXEDIR\${CONF_ONEBOX_CONFIGFILE}"  "$INSTDIR\${CONF_ONEBOX_CONFIGFILE}"
      IfFileExists  "$EXEDIR\${RES_LANGUAGE_DIR}\*" 0 +2
      CopyFiles /SILENT   "$EXEDIR\${RES_LANGUAGE_DIR}\*"  "$INSTDIR\${RES_LANGUAGE_DIR}"
      IfFileExists  "$EXEDIR\${RES_RES_DIR}\*" 0 +2
      CopyFiles /SILENT   "$EXEDIR\${RES_RES_DIR}\*"  "$INSTDIR\${RES_RES_DIR}"
      IfFileExists  "$EXEDIR\${RES_SKIN_DIR}\*" 0 +2
      CopyFiles /SILENT   "$EXEDIR\${RES_SKIN_DIR}\*"  "$INSTDIR\${RES_SKIN_DIR}"
      IfFileExists "$EXEDIR\${ONEBOX_CRT}" 0 +2
      CopyFiles /SILENT   "$EXEDIR\${ONEBOX_CRT}"  "$INSTDIR\${ONEBOX_CRT}"
      Sleep 1000
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 75 0
        Call SaveUserData
        IfErrors 0 +3
        RMDir /r $NSTDIR
        SendMessage $HWNDPARENT ${WM_CLOSE} 0 0
      Sleep 1000
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 80 0
      ;IfFileExists $PREVIOUSINSTDIR\OutlookAddin.dll 0 +3
      ;Execwait 'Regsvr32 /s /u "$PREVIOUSINSTDIR\OutlookAddin.dll"'
      ;Sleep 1000
      ;Execwait 'Regsvr32 /s "$INSTDIR\OutlookAddin.dll"'
      ;Sleep 1000
      ;IfFileExists $PREVIOUSINSTDIR\OfficeAddin.dll 0 +3
      ;Execwait 'Regsvr32 /s /u "$PREVIOUSINSTDIR\OfficeAddin.dll"'
      ;Sleep 1000
      ;Execwait 'Regsvr32 /s "$INSTDIR\OfficeAddin.dll"'
      ;Sleep 1000
      IfFileExists $PREVIOUSINSTDIR\ShellExtent.dll 0 +3
      Execwait 'Regsvr32 /s /u "$PREVIOUSINSTDIR\ShellExtent.dll"'
      Sleep 1000
	    Execwait 'Regsvr32 /s "$INSTDIR\ShellExtent.dll"'
	    Sleep 1000
      ;Execwait '"$INSTDIR\OneboxCMBAdapter.exe" protocol add'
      ;FindProcDLL::KillProc "explorer.exe"
      ;Sleep 500
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 85 0
      ${If} $PREVIOUSINSTDIR != ""
        Call DeletePrevVersionFile
      ${Endif}
      Sleep 1000
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 90 0
      IfSilent RegSilent  RegNoSilent
      RegSilent:
      WriteRegDWORD HKCU "${PRODUCT_Onebox_KEY}\Setting" "InstallType" 0x01
      WriteRegDWORD HKLM "${PRODUCT_Onebox_KEY}\Setting" "InstallType" 0x01
      goto  WriteReg
      RegNoSilent:
      WriteRegDWORD HKCU "${PRODUCT_Onebox_KEY}\Setting" "InstallType" 0x00
      WriteRegDWORD HKLM "${PRODUCT_Onebox_KEY}\Setting" "InstallType" 0x00

      WriteReg:
      ;DeleteRegValue HKCU "Software\Classes\Local Settings\Software\Microsoft\Windows\CurrentVersion\TrayNotify"  "IconStreams"
      ;DeleteRegValue HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Run"  "${PRODUCT_NAME}BackUp"
      WriteUninstaller "$INSTDIR\uninst.exe"
      WriteRegStr HKCU "${PRODUCT_DIR_REGKEY}" "" "$INSTDIR"
      WriteRegStr HKCU "${PRODUCT_Onebox_KEY}\Setting" "AppPath" "$INSTDIR"
      WriteRegStr HKCU "${PRODUCT_Onebox_KEY}\Setting" "MainVersion"  "${PRODUCT_VERSION}"
      
      WriteRegStr HKLM "${PRODUCT_DIR_REGKEY}" "" "$INSTDIR"
      WriteRegStr HKLM "${PRODUCT_Onebox_KEY}\Setting" "AppPath" "$INSTDIR"
      WriteRegStr HKLM "${PRODUCT_Onebox_KEY}\Setting" "MainVersion"  "${PRODUCT_VERSION}"
      
      Call IsUserAdmin
      Pop $R0
      ${If} $R0 == "true"
        WriteRegStr HKLM "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
        WriteRegStr HKLM "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
        WriteRegStr HKLM "${PRODUCT_UNINST_KEY}" "DisplayIcon" "$INSTDIR\Onebox Mate.exe"
        WriteRegStr HKLM "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
        WriteRegStr HKLM "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
      ${Else}
        WriteRegStr HKCU "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
        WriteRegStr HKCU "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
        WriteRegStr HKCU "${PRODUCT_UNINST_KEY}" "DisplayIcon" "$INSTDIR\Onebox Mate.exe"
        WriteRegStr HKCU "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
        WriteRegStr HKCU "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
      ${Endif}
      IfFileExists "$EXEDIR\${CONF_INSTALL_CONFIGFILE}"  0 +4
      ReadINIStr $R0 $EXEDIR\${CONF_INSTALL_CONFIGFILE} ${CONF_INSTALLINFO_SECTION} ${CONF_AUTOSTART_KEY}
      ${If} $R0 == "0"
        Goto Next
      ${Endif}
      ExecWait '$INSTDIR\OneboxStart.exe "/createTask"'
      Next:
      ;Execwait '"$INSTDIR\OneboxCMBAdapter.exe" protocol add'
      Sleep 1000
    SendMessage $PB_ProgressBar ${PBM_SETPOS} 100 0
      Sleep 500
      StrCpy  $InstallSuccess "1"
 ;ShowWindow $Btn_Next ${SW_SHOW}
    ;  ShowWindow $Btn_Install ${SW_HIDE}
      
	EnableWindow $Btn_Cancel 0
	 Sleep 300
	 ifsilent SilentQuit  NoSilentQuit
       SilentQuit:
       Call onClickComplete
       NoSilentQuit:
        ;Call onClickNext
        ShowWindow $PB_ProgressBar ${SW_HIDE}
        Sleep 3000
        ShowWindow $Btn_Complete_Close ${SW_SHOW}
        ShowWindow $Btn_Complete ${SW_SHOW}
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

  ${NSW_SetWindowSize} $HWNDPARENT 600 338 ;改变窗体大小
  ${NSW_SetWindowSize} $0 600 338 ;改变Page大小

  ${If} $LANGUAGE == 2052
    ${NSD_CreateLabel} 68 303 150 25 "安装位置"
    Pop $Lbl_InstallPlace
    SetCtlColors $Lbl_InstallPlace ""  transparent ;背景设成透明
    FindWindow $5 "#32770" "" $HWNDPARENT
    GetDlgItem $R5 $5 1200
    CreateFont $R2 "微软雅黑" 10 0
    SendMessage $R5 ${WM_SETFONT} $R2 0
  ${Else}
    ${NSD_CreateLabel} 68 303 150 25 "Directory"
    Pop $Lbl_InstallPlace
    FindWindow $5 "#32770" "" $HWNDPARENT
    GetDlgItem $R5 $5 1200
    CreateFont $R2 "微软雅黑" 10 0
    SetCtlColors $Lbl_InstallPlace ""  transparent ;背景设成透明
  ${Endif}

	${NSD_CreateDirRequest} 130 298 300 30 "$INSTDIR"
 	Pop	$Txt_Browser
 	FindWindow $5 "#32770" "" $HWNDPARENT
  GetDlgItem $R5 $5 1201
  CreateFont $R2 "微软雅黑" 10 0
  SendMessage $R5 ${WM_SETFONT} $R2 0
  ${NSD_OnChange} $Txt_Browser OnChange_DirRequest
  EnableWindow $Txt_Browser 0

  ${If} $LANGUAGE == 2052
 	${NSD_CreateBrowseButton} 430 298 80 30 "选择"
 	Pop	$Btn_Browser
 	FindWindow $5 "#32770" "" $HWNDPARENT
  GetDlgItem $R5 $5 1202
  CreateFont $R2 "微软雅黑" 10 0
  SendMessage $R5 ${WM_SETFONT} $R2 0
 	StrCpy $1 $Btn_Browser
	Call SkinBtn_Browser
	GetFunctionAddress $3 OnClick_BrowseButton
	SkinBtn::onClick $1 $3
	${Else}
	${NSD_CreateBrowseButton} 430 298 80 30 "Select"
 	Pop	$Btn_Browser
 	FindWindow $5 "#32770" "" $HWNDPARENT
  GetDlgItem $R5 $5 1202
  CreateFont $R2 "微软雅黑" 10 0
  SendMessage $R5 ${WM_SETFONT} $R2 0
 	StrCpy $1 $Btn_Browser
	Call SkinBtn_Browser
	GetFunctionAddress $3 OnClick_BrowseButton
	SkinBtn::onClick $1 $3
	${Endif}
	  
  ${NSD_CreateProgressBar} 60 322 474 5 ""
  Pop $PB_ProgressBar
  SkinProgress::Set $PB_ProgressBar "$PLUGINSDIR\img\process_color.bmp" "$PLUGINSDIR\img\process_bg.bmp"
  ShowWindow $PB_ProgressBar ${SW_HIDE}
    
  ;关闭按钮
  ${NSD_CreateButton} 570 10 20 20 ""
	Pop $Btn_Welcome_Close
	StrCpy $1 $Btn_Welcome_Close
	Call SkinBtn_Welcome_Close
  GetFunctionAddress $3 onCancel
  SkinBtn::onClick $1 $3
    
  ${NSD_CreateButton} 570 10 20 20 ""
	Pop $Btn_Complete_Close
	StrCpy $1 $Btn_Complete_Close
	Call SkinBtn_Complete_Close
  GetFunctionAddress $3 onCancel
  SkinBtn::onClick $1 $3
  ShowWindow $Btn_Complete_Close ${SW_HIDE}
    
  ${NSD_CreateButton} 255 161 90 36 ""
	Pop $Btn_Complete
	StrCpy $1 $Btn_Complete
	Call SkinBtn_Complete
  GetFunctionAddress $3 onClickComplete
  SkinBtn::onClick $1 $3
  ShowWindow $Btn_Complete ${SW_HIDE}
    
  ${NSD_CreateButton} 240 197 120 40 ""
	Pop $Btn_Install
	StrCpy $1 $Btn_Install
	Call SkinBtn_Install
	GetFunctionAddress $3 onClickInstall
  SkinBtn::onClick $1 $3

  System::Call `*(i,i,i,i)i(0,0,650,338).R0`
  System::Call `user32::MapDialogRect(i$HWNDPARENT,iR0)`
  System::Call `*$R0(i.s,i.s,i.s,i.s)`
  System::Free $R0
  FindWindow $R0 "#32770" "" $HWNDPARENT
  System::Call `user32::CreateWindowEx(i,t"STATIC",in,i${DEFAULT_STYLES}|${SS_BLACKRECT},i0,i0,i650,i338,iR0,i1100,in,in)i.R0`
  StrCpy $WebImg $R0
  ShowWindow $R0 ${SW_HIDE}

  ;用户协议
	;	${NSD_CreateButton} 181 273 95 15 ""
	;	Pop $Btn_Agreement
	;	StrCpy $1 $Btn_Agreement
	;	Call SkinBtn_Agreement1
	; GetFunctionAddress $3 onClickAgreement

  ;贴小图
  ; ${NSD_CreateBitmap} 0 0 520 302 ""
  ; Pop $MiddleImage
  ; ${NSD_SetImage} $MiddleImage $PLUGINSDIR\welcome.bmp $ImageHandle
  ;  ShowWindow $MiddleImage ${SW_HIDE}
    
  ;贴背景大图
  IfFileExists "$EXEDIR\${CONF_INSTALL_CONFIGFILE}"  0 +3
  ReadINIStr $R0 $EXEDIR\${CONF_INSTALL_CONFIGFILE} ${CONF_INSTALLINFO_SECTION} ${CONF_SELECTPATH_KEY}
  ${If} $R0 == "0"
    ShowWindow $Lbl_InstallPlace ${SW_HIDE}
    ShowWindow $Txt_Browser ${SW_HIDE}
    ShowWindow $Btn_Browser ${SW_HIDE}
    ${NSD_CreateBitmap} 0 0 100% 100% ""
    Pop $BGImage
    ${NSD_SetImage} $BGImage $PLUGINSDIR\img\welcomebg.bmp $ImageHandle
    Goto Next
  ${Endif}
  
  ${NSD_CreateBitmap} 0 0 100% 100% ""
  Pop $BGImage
  ${NSD_SetImage} $BGImage $PLUGINSDIR\img\welcomebg_path.bmp $ImageHandle
  Next:
	GetFunctionAddress $0 onGUICallback
	WndProc::onCallback $BGImage $0 ;处理无边框窗体移动
	nsDialogs::Show
	${NSD_FreeImage} $ImageHandle
FunctionEnd

Function IsUserAdmin
  Push $R0
  Push $R1
  Push $R2
  ClearErrors
  UserInfo::GetName
  IfErrors Win9x
  Pop $R1
  UserInfo::GetAccountType
  Pop $R2
  StrCmp $R2 "Admin" 0 Continue
  ; Observation: I get here when running Win98SE. (Lilla)
  ; The functions UserInfo.dll looks for are there on Win98 too,
  ; but just don't work. So UserInfo.dll, knowing that admin isn't required
  ; on Win98, returns admin anyway. (per kichik)
  ; MessageBox MB_OK 'User "$R1" is in the Administrators group'
  StrCpy $R0 "true"
  Goto Done
  Continue:
  ; You should still check for an empty string because the functions
  ; UserInfo.dll looks for may not be present on Windows 95. (per kichik)
  StrCmp $R2 "" Win9x
  StrCpy $R0 "false"
  ;MessageBox MB_OK 'User "$R1" is in the "$R2" group'
  Goto Done
  Win9x:
  ; comment/message below is by UserInfo.nsi author:
  ; This one means you don't need to care about admin or
  ; not admin because Windows 9x doesn't either
  ;MessageBox MB_OK "Error! This DLL can't run under Windows 9x!"
  StrCpy $R0 "true"
  Done:
  ;MessageBox MB_OK 'User= "$R1" AccountType= "$R2" IsUserAdmin= "$R0"'
  Pop $R2
  Pop $R1
  Exch $R0
FunctionEnd

;检查是否已经安装
Function   Checkifinstall

    Push $R0
    Push $R1
    Push $R2

    ReadRegStr  $R1  HKLM   "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}Cloud"  "UninstallString"
    strcmp $R1 "" YES NO
        NO:
          ReadRegStr  $0 HKLM "${PRODUCT_Onebox_KEY}\Setting" "MainVersion"
          ${VersionCompare}   $0  ${PRODUCT_VERSION}  $R0
            ${If} $R0 == 1
              Ifsilent okFlag
          		${If} $LANGUAGE == 2052
		            	MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "程序已经安装."  IDOK okFlag
	           	${Else}
			            MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST "Onebox already exists." IDOK okFlag
              ${Endif}
            ${Else}
              ;RMDir /r  "$INSTDIR\UserData"
              goto YES
            ${Endif}
          ;${Endif}
          
          okFlag:
            SendMessage $HWNDPARENT ${WM_CLOSE} 0 0
            Quit
        YES:
FunctionEnd
  
Function CheckOSVersion

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
      ${AndIf} $R1 != "8"
        IfSilent quitInstall
          ${If} $LANGUAGE == 2052
            MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "当前操作系统可能无法运行该程序（仅完全支持Windows 7和Windows 8中英文版），是否继续安装？" \
                              IDYES BeginToInstall IDNO quitInstall
          ${Else}
             MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "The current operating system cannot run this program. (This program can run on Windows 7 and Windows 8 only.) Continue?" IDYES   BeginToInstall IDNO quitInstall
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
 
Function CheckIsCloudMachine
   Push $R0
   Push $R1
   
   ReadRegStr $R0  HKCR "CLSID\{6655833E-D350-4017-8C31-3F89CA360FDF}" ""
   ;MessageBox MB_OK|MB_ICONQUESTION|MB_TOPMOST $R0 IDOK quitInstall
   ${If} $R0 == "1"
     goto  BeginToInstall
   ${Endif}
   
   ${If} ${RunningX64}
      ReadRegDWORD $R1 HKLM \
        "SOFTWARE\WOW6432Node\SOFTWARE\Microsoft\windows\CurrentVersion" "isCloudMachine"
   ${Else}
       ReadRegDWORD $R1 HKLM \
        "SOFTWARE\Microsoft\windows\CurrentVersion" "isCloudMachine"
   ${Endif}
    
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

Function GetSpesPath
 Push $R1
 Var /GLOBAL SpesPath
Var /GLOBAL LastChar1
 ${If} ${RunningX64}
	  ReadRegStr  $SpesPath  HKLM   "SOFTWARE\Wow6432Node\Huawei\SPES5.0\Composites\spes"  "InstallPath"
	${Else}
	  ReadRegStr  $SpesPath  HKLM   "SOFTWARE\Huawei\SPES5.0\Composites\spes"  "InstallPath"
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

Function UpdateNormalProc
     Call Check_Disk_Free_Space_For_Update
     Call UpdateFileProc
     Call DeletePrevVersionFile
     Exec  "$INSTDIR\Onebox Mate.exe"
FunctionEnd

Function UpdateForceProc
     Call Check_Disk_Free_Space_For_Update
     Call UpdateFileProc
     Call DeletePrevVersionFile
     Exec  "$INSTDIR\Onebox Mate.exe"
FunctionEnd

Function SaveUserData
    ClearErrors
    ${VersionCompare}   "1.3.10.2038"  $PREVIOUSVERSION  $R0
    ${If} $R0 != 2
      IfFileExists $PREVIOUSINSTDIR\UserData\* 0 +3
      CreateDirectory $INSTDIR\UserData
      CopyFiles /SILENT "$PREVIOUSINSTDIR\UserData\*"  "$INSTDIR\UserData"
    ${EndIf}
FunctionEnd

Function UpdateFileProc
		SetOverwrite on
    SetShellVarContext all
    Call stopOneboxProcess
     
    SetOutPath "$INSTDIR\skin"
      File /r ..\output\skin\*
      Sleep 500

    SetOutPath "$INSTDIR\Res"
      File /r ..\output\Res\*
      Sleep 500

     SetOutPath "$INSTDIR\Language"
      File /r ..\output\Language\*
      Sleep 500

    SetOutPath "$INSTDIR\"
      ${GetTime} "" "L" $0 $1 $2 $3 $4 $5 $6
      Rename "$INSTDIR\ShellExtent.dll" "$INSTDIR\ShellExtentbk$5.dll"
      Rename "$INSTDIR\OutlookAddin.dll" "$INSTDIR\OutlookAddinbk$5.dll"
      Rename "$INSTDIR\OfficeAddin.dll" "$INSTDIR\OfficeAddinbk$5.dll"
      Rename "$INSTDIR\DuiLib.dll" "$INSTDIR\DuiLibbk$5.dll"
      Rename "$INSTDIR\msvcp110.dll" "$INSTDIR\msvcp110bk$5.dll"
      Rename "$INSTDIR\msvcr110.dll" "$INSTDIR\msvcr110bk$5.dll"
      Rename "$INSTDIR\HuaweiSecureC.dll" "$INSTDIR\HuaweiSecureCbk$5.dll"
      Rename "$INSTDIR\HuaweiSecureC_x64.dll" "$INSTDIR\HuaweiSecureC_x64bk$5.dll"
      File  /oname=Common.dll   ..\output\Common.dll
	  File  /oname=SystemInfo.dll   ..\output\SystemInfo.dll
      File  /oname=NetSDK.dll   ..\output\NetSDK.dll
      ;File  /oname=SyncService.dll   ..\output\SyncService.dll
      File /oname=DuiLib.dll    ..\output\DuiLib.dll
      File  /oname=libcurl.dll  ..\output\libcurl.dll
      File /oname=libeay32.dll   ..\output\libeay32.dll
      File /oname=log4cpp.conf  ..\output\log4cpp.conf
      File /oname=Label32.dll  ..\output\Label32.dll
      File /oname=msvcp110.dll  ..\output\msvcp110.dll
      File /oname=msvcr110.dll  ..\output\msvcr110.dll
      File /oname=msvcr100.dll  ..\output\msvcr100.dll
      File  /oname=ssleay32.dll   ..\output\ssleay32.dll
      File  /oname=NscaMiniLib.dll   ..\output\NscaMiniLib.dll
      File  /oname=DataCache.dll   ..\output\DataCache.dll
      File  /oname=MessageProxy.dll   ..\output\MessageProxy.dll
      File  /oname=OfficeAddin.dll   ..\output\OfficeAddin.dll
      File  /oname=OutlookAddin.dll   ..\output\OutlookAddin.dll
	  File  /oname=BackupAll.dll   ..\output\BackupAll.dll
      File /oname=zlib1.dll  ..\output\zlib1.dll
      File /oname=KmcCbb.dll  ..\output\KmcCbb.dll
      File /oname=HuaweiSecureC.dll  ..\output\HuaweiSecureC.dll
      File "/oname=Onebox Mate.exe"  "..\output\Onebox Mate.exe"
      File /oname=Config.ini  ..\output\Config.ini
      File  /oname=OneboxStart.exe   ..\output\OneboxStart.exe
      ;File  /oname=OneboxCMBAdapter.exe   ..\output\OneboxCMBAdapter.exe
      File  /oname=setting.xml   ..\output\setting.xml
      File  /nonfatal /oname=cmb.config.ini ..\output\cmb.config.ini
      File  /oname=UpdateDB.dll   ..\output\UpdateDB.dll
      File  /oname=updateInfo.db   ..\output\updateInfo.db
      File  /oname=DataTransfer.dll   ..\output\DataTransfer.dll
      ${If} ${RunningX64}
        File  /oname=ShellExtent.dll   ..\output\ShellExtent_x64.dll
        File /oname=HuaweiSecureC_x64.dll  ..\output\HuaweiSecureC_x64.dll
      ${Else}
        File  /oname=ShellExtent.dll   ..\output\ShellExtent.dll
      ${Endif}
      IfFileExists  "$EXEDIR\${CONF_ONEBOX_CONFIGFILE}" 0 +2
      CopyFiles /SILENT   "$EXEDIR\${CONF_ONEBOX_CONFIGFILE}"  "$INSTDIR\${CONF_ONEBOX_CONFIGFILE}"
      IfFileExists  "$EXEDIR\${RES_LANGUAGE_DIR}\*" 0 +2
      CopyFiles /SILENT   "$EXEDIR\${RES_LANGUAGE_DIR}\*"  "$INSTDIR\${RES_LANGUAGE_DIR}"
      IfFileExists  "$EXEDIR\${RES_RES_DIR}\*" 0 +2
      CopyFiles /SILENT   "$EXEDIR\${RES_RES_DIR}\*"  "$INSTDIR\${RES_RES_DIR}"
      IfFileExists  "$EXEDIR\${RES_SKIN_DIR}\*" 0 +2
      CopyFiles /SILENT   "$EXEDIR\${RES_SKIN_DIR}\*"  "$INSTDIR\${RES_SKIN_DIR}"
      IfFileExists "$EXEDIR\${ONEBOX_CRT}" 0 +2
      CopyFiles /SILENT   "$EXEDIR\${ONEBOX_CRT}"  "$INSTDIR\${ONEBOX_CRT}"
      Sleep 1000
      Call SaveUserData
      IfErrors 0 +3
      RMDir /r $NSTDIR
      SendMessage $HWNDPARENT ${WM_CLOSE} 0 0
      Sleep 1000
      ;IfFileExists $PREVIOUSINSTDIR\OutlookAddin.dll 0 +3
      ;Execwait 'Regsvr32 /s /u "$PREVIOUSINSTDIR\OutlookAddin.dll"'
      ;Sleep 1000
      ;Execwait 'Regsvr32 /s "$INSTDIR\OutlookAddin.dll"'
      ;Sleep 1000
      ;IfFileExists $PREVIOUSINSTDIR\OfficeAddin.dll 0 +3
      ;Execwait 'Regsvr32 /s /u "$PREVIOUSINSTDIR\OfficeAddin.dll"'
      ;Sleep 1000
      ;Execwait 'Regsvr32 /s "$INSTDIR\OfficeAddin.dll"'
      ;Sleep 1000
      IfFileExists $PREVIOUSINSTDIR\ShellExtent.dll 0 +3
      Execwait 'Regsvr32 /s /u "$PREVIOUSINSTDIR\ShellExtent.dll"'
      Sleep 1000
	    Execwait 'Regsvr32 /s "$INSTDIR\ShellExtent.dll"'
	    Sleep 1000
      ;Execwait '"$INSTDIR\OneboxCMBAdapter.exe" protocol add'
      ;FindProcDLL::KillProc "explorer.exe"
      ;Sleep 1000
      IfSilent RegSilent  RegNoSilent
      RegSilent:
      WriteRegDWORD HKCU "${PRODUCT_Onebox_KEY}\Setting" "InstallType" 0x01
      WriteRegDWORD HKLM "${PRODUCT_Onebox_KEY}\Setting" "InstallType" 0x01
      goto  WriteReg
      RegNoSilent:
      WriteRegDWORD HKCU "${PRODUCT_Onebox_KEY}\Setting" "InstallType" 0x00
      WriteRegDWORD HKLM "${PRODUCT_Onebox_KEY}\Setting" "InstallType" 0x00

      WriteReg:
      ;DeleteRegValue HKCU "Software\Classes\Local Settings\Software\Microsoft\Windows\CurrentVersion\TrayNotify"  "IconStreams"
      WriteUninstaller "$INSTDIR\uninst.exe"
      WriteRegStr HKLM "${PRODUCT_DIR_REGKEY}" "" "$INSTDIR"
      WriteRegStr HKLM "${PRODUCT_Onebox_KEY}\Setting" "AppPath" "$INSTDIR"
      WriteRegStr HKLM "${PRODUCT_Onebox_KEY}\Setting" "MainVersion"  "${PRODUCT_VERSION}"
      WriteRegStr HKCU "${PRODUCT_DIR_REGKEY}" "" "$INSTDIR"
      WriteRegStr HKCU "${PRODUCT_Onebox_KEY}\Setting" "AppPath" "$INSTDIR"
      WriteRegStr HKCU "${PRODUCT_Onebox_KEY}\Setting" "MainVersion"  "${PRODUCT_VERSION}"
      Call IsUserAdmin
      Pop $R0
      ${If} $R0 == "true"
        WriteRegStr HKLM "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
        WriteRegStr HKLM "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
        WriteRegStr HKLM "${PRODUCT_UNINST_KEY}" "DisplayIcon" "$INSTDIR\Onebox Mate.exe"
        WriteRegStr HKLM "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
        WriteRegStr HKLM "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
      ${Else}
        WriteRegStr HKCU "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
        WriteRegStr HKCU "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
        WriteRegStr HKCU "${PRODUCT_UNINST_KEY}" "DisplayIcon" "$INSTDIR\Onebox Mate.exe"
        WriteRegStr HKCU "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
        WriteRegStr HKCU "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
      ${Endif}
      IfFileExists "$EXEDIR\${CONF_INSTALL_CONFIGFILE}"  0 +4
      ReadINIStr $R0 $EXEDIR\${CONF_INSTALL_CONFIGFILE} ${CONF_INSTALLINFO_SECTION} ${CONF_AUTOSTART_KEY}
      ${If} $R0 == "0"
        Goto Next
      ${Endif}
      ExecWait '$INSTDIR\OneboxStart.exe "/createTask"'
      Sleep 500
      Next:
      IfFileExists "$EXEDIR\${CONF_INSTALL_CONFIGFILE}"  0 +4
      ReadINIStr $R0 $EXEDIR\${CONF_INSTALL_CONFIGFILE} ${CONF_INSTALLINFO_SECTION} ${CONF_CREATESHORTCUT_KEY}
      ${If} $R0 == "0"
        Goto Next2
      ${Endif}
      Call IsUserAdmin
      Pop $R0
      ${If} $R0 == "true"
        SetShellVarContext all
        RMdir /r "$SMPROGRAMS\OneboxAPP"
        CreateDirectory "$SMPROGRAMS\OneboxAPP"
        CreateDirectory "$SMPROGRAMS\OneboxAPP\Onebox"
        CreateShortCut  "$SMPROGRAMS\OneboxAPP\Onebox\Onebox Mate.lnk" "$INSTDIR\Onebox Mate.exe"
        CreateShortCut  "$SMPROGRAMS\OneboxAPP\Onebox\Uninstall.lnk" "$INSTDIR\uninst.exe"
        
        CreateShortCut "$QUICKLAUNCH\${PRODUCT_NAME}.lnk" "$INSTDIR\${PRODUCT_EXECUTE_NAME}"
        CreateShortCut  "$DESKTOP\Onebox Mate.lnk" "$INSTDIR\Onebox Mate.exe"
      ${Else}
        SetShellVarContext current
        ;RMdir /r "$SMPROGRAMS\OneboxAPP"
        ;CreateDirectory "$SMPROGRAMS\OneboxAPP"
        ;CreateDirectory "$SMPROGRAMS\OneboxAPP\Onebox"
        ;CreateShortCut  "$SMPROGRAMS\OneboxAPP\Onebox\Onebox Mate.lnk" "$INSTDIR\Onebox Mate.exe"
        ;CreateShortCut  "$SMPROGRAMS\OneboxAPP\Onebox\Uninstall.lnk" "$INSTDIR\uninst.exe"
        CreateShortCut "$QUICKLAUNCH\${PRODUCT_NAME}.lnk" "$INSTDIR\${PRODUCT_EXECUTE_NAME}"
        CreateShortCut  "$DESKTOP\Onebox Mate.lnk" "$INSTDIR\Onebox Mate.exe"
      ${Endif}
      Sleep 500
      Next2:
      Exec  "$INSTDIR\Onebox Mate.exe"
FunctionEnd

Function DeletePrevVersionFile
  Push $R1
  Push $R2
  Push $R3
  Push $R4
  Push $R5

  ;MessageBox MB_OK  $PREVIOUSVERSION
  ${VersionCompare} ${PRODUCT_VERSION} $PREVIOUSVERSION $R1
  ${If} $R1 == 0
   Goto Done
  ${EndIf}
  
  ;MessageBox MB_OK  $PREVIOUSINSTDIR
  Delete "$PREVIOUSINSTDIR\Common.dll"
  Delete "$PREVIOUSINSTDIR\SystemInfo.dll"
  Delete "$PREVIOUSINSTDIR\NetSDK.dll"
  Delete "$PREVIOUSINSTDIR\DuiLib.dll"
  Delete "$PREVIOUSINSTDIR\libcurl.dll"
  Delete "$PREVIOUSINSTDIR\libeay32.dll"
  Delete "$PREVIOUSINSTDIR\log4cpp.conf"
  Delete "$PREVIOUSINSTDIR\Label32.dll"
  Delete "$PREVIOUSINSTDIR\msvcp110.dll"
  Delete "$PREVIOUSINSTDIR\msvcr110.dll"
  Delete "$PREVIOUSINSTDIR\msvcr100.dll"
  Delete "$PREVIOUSINSTDIR\ssleay32.dll"
  Delete "$PREVIOUSINSTDIR\NscaMiniLib.dll"
  Delete "$PREVIOUSINSTDIR\DataCache.dll"
  Delete "$PREVIOUSINSTDIR\MessageProxy.dll"
  Delete "$PREVIOUSINSTDIR\BackupTask.dll"
  Delete "$PREVIOUSINSTDIR\BackupAll.dll"
  Delete "$PREVIOUSINSTDIR\zlib1.dll"
  Delete "$PREVIOUSINSTDIR\DataTransfer.dll"
  Delete "$PREVIOUSINSTDIR\Onebox Mate.exe"
  Delete "$PREVIOUSINSTDIR\Onebox_Mate.exe"
  ;Delete "$PREVIOUSINSTDIR\OneboxCMBAdapter.exe"
  Sleep 1000
  Delete "$PREVIOUSINSTDIR\Config.ini"
  ${GetTime} "" "L" $0 $1 $2 $3 $4 $5 $6
  Rename  "$PREVIOUSINSTDIR\ShellExtent.dll" "$PREVIOUSINSTDIR\ShellExtentbk$5.dll"
  Rename  "$PREVIOUSINSTDIR\OutlookAddin.dll" "$PREVIOUSINSTDIR\OutlookAddinbk$5.dll"
  Rename  "$PREVIOUSINSTDIR\OfficeAddin.dll" "$PREVIOUSINSTDIR\OfficeAddinbk$5.dll"
  Rename  "$PREVIOUSINSTDIR\msvcp110.dll" "$PREVIOUSINSTDIR\msvcp110bk$5.dll"
  Rename  "$PREVIOUSINSTDIR\msvcr110.dll" "$PREVIOUSINSTDIR\msvcr110bk$5.dll"
  Delete "$PREVIOUSINSTDIR\OneboxStart.exe"
  Delete "$PREVIOUSINSTDIR\setting.xml"
  
  RMDir /r "$PREVIOUSINSTDIR\skin"
  RMDir /r "$PREVIOUSINSTDIR\Res"
  RMDir /r "$PREVIOUSINSTDIR\Language"
  RMDir /r "$PREVIOUSINSTDIR\UserData"

  ;MessageBox MB_OK  $R5
  RMDir /r $PREVIOUSINSTDIR
  Call ClearFolder
  Delete "$DESKTOP\Onebox_Mate.lnk"
  Delete  "$SMPROGRAMS\OneboxAPP\Onebox\Onebox_Mate.lnk"
  Done:
    Pop $R5
    Pop $R4
    Pop $R3
    Pop $R2
    Pop $R1
FunctionEnd

Function ClearFolder
  Push $R1
  Push $R2
  Push $R3
  Push $R4
  
  StrLen $R1 ${PRODUCT_VERSION}
  StrCpy $R2 $INSTDIR -$R1
  StrCpy $R3 $R2  -1
  StrCpy $R4 $R3 "" -6
  IfFileExists $Global_SpesPath 0 Done
  ${If} $R4 == "Onebox"
    FindFirst $0 $1 $R3\*
    loop:
    StrCmp $1 "" Done
    StrCmp $1 ${PRODUCT_VERSION} Next
    StrCmp $1  "." Next
    StrCmp $1  ".." Next
    ;MessageBox MB_OK  $R3\$1
    IfFileExists $R3\$1\"*" 0 +2
    RMDir /r "$R3\$1"
    Delete "$R3\$1"
    Next:
    FindNext $0 $1
    Goto loop
  ${Endif}
  Done:
    Pop $R4
    Pop $R3
    Pop $R2
    Pop $R1
FunctionEnd

Function StopOneboxProcess
  Push $R0
  Push $R1
  Push $R2

  Var  /GLOBAL WaitCountNewOnebox
  Var  /GLOBAL WaitCouintStopOnebox

  IfFileExists "$PREVIOUSINSTDIR\Onebox.exe" 0 +2
    ExecWait '$PREVIOUSINSTDIR\Onebox.exe  stop'
  IfFileExists "$PREVIOUSINSTDIR\Onebox Mate.exe" 0 +2
    ExecWait '$PREVIOUSINSTDIR\Onebox Mate.exe  stop'
  IfFileExists "$PREVIOUSINSTDIR\Onebox_Mate.exe" 0 +3
    ExecWait '$PREVIOUSINSTDIR\Onebox_Mate.exe  stop'
    Goto GoOnWait
    ExecWait '$INSTDIR\Onebox.exe  stop'
    ExecWait '$INSTDIR\Onebox Mate.exe  stop'
    ExecWait '$INSTDIR\Onebox_Mate.exe  stop'
  Sleep 1000
  GoOnWait:
  FindProcDLL::FindProc  "Onebox.exe"
  StrCmp $R0 1 WaitStop 0
  FindProcDLL::FindProc  "Onebox Mate.exe"
  StrCmp $R0 1 WaitStop 0
  FindProcDLL::FindProc  "Onebox_Mate.exe"
  StrCmp $R0 1 WaitStop killed

  WaitStop:
    IntOp $WaitCouintStopOnebox $WaitCouintStopOnebox + 1
    IntCmp $WaitCouintStopOnebox 10 is10 lessthan10 morethan10
    is10:
      Sleep 1000
      Goto GoOnWait
    lessthan10:
      Sleep 1000
      Goto GoOnWait
    morethan10:
      Goto  Retry

  Retry:
  IfFileExists "$PREVIOUSINSTDIR\Onebox.exe" 0 +2
  FindProcDLL::KillProc "Onebox.exe"
  FindProcDLL::KillProc "Onebox Mate.exe"
  FindProcDLL::KillProc "Onebox_Mate.exe"
	Sleep 500
	          IfFileExists "$PREVIOUSINSTDIR\Onebox.exe" 0 +3
            FindProcDLL::FindProc  "Onebox.exe"
            StrCmp $R0 1 notKilled 0
            FindProcDLL::FindProc  "Onebox Mate.exe"
            StrCmp $R0 1 notKilled 0
            FindProcDLL::FindProc  "Onebox_Mate.exe"
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

;-------------------------------------------------------------------------------------------------------------

Section MainSetup
SectionEnd


Function un.onUninstSuccess
  ;HideWindow
     ;此处卸载成功会触发，Do something here
  ;Messagebox MB_OK "un.onUninstSuccess"
FunctionEnd

Function un.onInit
;!insertmacro MUI_UNGETLANGUAGE
    Var /GLOBAL TestInstallPath
    Push $R0
    Push $R1
    Push $R2
    ClearErrors
    UserInfo::GetName
    IfErrors Win9x
    Pop $R1
    UserInfo::GetAccountType
    Pop $R2
    StrCmp $R2 "Admin" 0 Continue1
    StrCpy $R0 "true"
    Goto Done
    Continue1:
    StrCmp $R2 "" Win9x
    StrCpy $R0 "false"
    Goto Done
    Win9x:
    StrCpy $R0 "true"
    Done:
    Pop $R2
    Pop $R1
    Exch $R0
    Pop $R0
    ${If} $R0 == "false"
      ClearErrors
      ReadRegStr  $TestInstallPath  HKLM   "${PRODUCT_Onebox_KEY}\Setting"  "AppPath"
      IfErrors +2 0
      Abort
    ${Endif}
    IfSilent yesFlag
    FindProcDLL::FindProc  "Onebox Mate.exe"
    Next:
     StrCmp $R0 1 0 Continue
        ${If} $LANGUAGE == 2052
          MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "安装程序检测到 Onebox Mate 正在运行，是否结束应用程序继续卸载?" IDYES Continue  IDNO noFlag
        ${Else}
      		MessageBox MB_YESNO|MB_ICONQUESTION|MB_TOPMOST "The installation program detects that Onebox Mate is running. Terminate the application program and continue?"  IDYES Continue  IDNO noFlag
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

  Var /GLOBAL UserLinksDir
  Var  /GLOBAL WaitCountNewOnebox1
  Var  /GLOBAL WaitCouintStopOnebox1
  
  Push $R0
  Push $R1
  Push $R2
  ClearErrors
  UserInfo::GetName
  IfErrors Win9x
  Pop $R1
  UserInfo::GetAccountType
  Pop $R2
  StrCmp $R2 "Admin" 0 Continue
  StrCpy $R0 "true"
  Goto Done
  Continue:
  StrCmp $R2 "" Win9x
  StrCpy $R0 "false"
  Goto Done
  Win9x:
  StrCpy $R0 "true"
  Done:
  Pop $R2
  Pop $R1
  Exch $R0
  Pop $R0
  ${If} $R0 == "true"
    SetShellVarContext all
  ${Endif}

  ExecWait '$INSTDIR\Onebox Mate.exe  stop'
  Sleep 1000
  GoOnWait:
  FindProcDLL::FindProc  "Onebox Mate.exe"
  StrCmp $R0 1 WaitStop killed
  WaitStop:
    IntOp $WaitCouintStopOnebox1 $WaitCouintStopOnebox1 + 1
    IntCmp $WaitCouintStopOnebox1 10 is10 lessthan10 morethan10
    is10:
      Sleep 1000
      Goto GoOnWait
    lessthan10:
      Sleep 1000
      Goto GoOnWait
    morethan10:
      Goto  Retry
      
  Retry:
  FindProcDLL::KillProc  "Onebox Mate.exe"
	Sleep 500
    FindProcDLL::FindProc  "Onebox Mate.exe"
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
  Delete "$INSTDIR\Common.dll"
  Delete "$INSTDIR\SystemInfo.dll"
  Delete "$INSTDIR\NetSDK.dll"
  Delete "$INSTDIR\DuiLib.dll"
  Delete "$INSTDIR\libcurl.dll"
  Delete "$INSTDIR\libeay32.dll"
  Delete "$INSTDIR\log4cpp.conf"
  Delete "$INSTDIR\Label32.dll"
  Delete "$INSTDIR\msvcp110.dll"
  Delete "$INSTDIR\msvcr110.dll"
  Delete "$INSTDIR\msvcr100.dll"
  Delete "$INSTDIR\ssleay32.dll"
  Delete "$INSTDIR\NscaMiniLib.dll"
  Delete "$INSTDIR\DataCache.dll"
  Delete "$INSTDIR\MessageProxy.dll"
  Delete "$INSTDIR\BackupTask.dll"
  Delete "$INSTDIR\BackupAll.dll"
  Delete "$INSTDIR\zlib1.dll"
  Delete "$INSTDIR\DataTransfer.dll"
  Delete "$INSTDIR\Onebox Mate.exe"
  Delete "$INSTDIR\Onebox_Mate.exe"
  Sleep 1000
  Delete "$INSTDIR\Config.ini"
  ExecWait 'Regsvr32 /s /u "$INSTDIR\ShellExtent.dll"'
  Sleep 1000
  ExecWait 'Regsvr32 /s /u "$INSTDIR\OutlookAddin.dll"'
  Sleep 1000
  ExecWait 'Regsvr32 /s /u "$INSTDIR\OfficeAddin.dll"'
  Sleep 1000
  Delete "$INSTDIR\ShellExtent.dll" 
  Delete "$INSTDIR\OutlookAddin.dll"
  Delete "$INSTDIR\OfficeAddin.dll" 
  ExecWait '$INSTDIR\OneboxStart.exe "/cancelTask"'
  Delete "$INSTDIR\OneboxStart.exe"
  ;Execwait '"$INSTDIR\OneboxCMBAdapter.exe" protocol delete'
  ;Delete "$INSTDIR\OneboxCMBAdapter.exe"
  Delete "$INSTDIR\setting.xml"

  UserInfo::GetName
	Pop $0
	StrCpy  $UserLinksDir  "C:\Users\$0\Links"
	Delete  "$UserLinksDir\${PRODUCT_NAME}.lnk"
	
  RMDir /r "$INSTDIR\skin"
  RMDir /r "$INSTDIR\Res"
  RMDir /r "$INSTDIR\Language"
  ;SetShellVarContext current
  ;RMDir /r "$LOCALAPPDATA\${PRODUCT_NAME}"

  ;MessageBox MB_OK  $R5
  RMDir /r $INSTDIR

  DeleteRegKey HKCU "${PRODUCT_Onebox_KEY}\Setting"
  DeleteRegKey HKCU  "${PRODUCT_DIR_REGKEY}"
  DeleteRegKey HKCU "SOFTWARE\Huawei\OneboxApp"
  
  DeleteRegKey HKLM "${PRODUCT_Onebox_KEY}\Setting"
  DeleteRegKey HKLM  "${PRODUCT_DIR_REGKEY}"
  DeleteRegKey HKLM "SOFTWARE\Huawei\OneboxApp"
  
  DeleteRegKey HKLM "${PRODUCT_DIR_REGKEY}"
  DeleteRegKey HKLM "${PRODUCT_UNINST_KEY}"
  
  DeleteRegKey HKCU "${PRODUCT_DIR_REGKEY}"
  DeleteRegKey HKCU "${PRODUCT_UNINST_KEY}"


  ;删除桌面快捷方式等，可去除
  Delete "$DESKTOP\Onebox Mate.lnk"
  Delete  "$SMPROGRAMS\OneboxAPP\Onebox\Onebox Mate.lnk"
  Delete  "$SMPROGRAMS\OneboxApp\Onebox\Uninstall.lnk"
  RMdir /r "$SMPROGRAMS\OneboxApp"

  SetAutoClose true

   ;ExecWait '"$INSTDIR\NetConnectService\uninst.exe" /S'
   ;ExecWait '"$8Composites\iTools\uninst.exe" /S'
   ;  RMDir  /r "$INSTDIR\NetConnectService"


SectionEnd
