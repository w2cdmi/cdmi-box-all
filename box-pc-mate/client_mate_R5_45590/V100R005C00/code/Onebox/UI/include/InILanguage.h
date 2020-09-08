#ifndef _LANGUAGE_H_
	#define  _LANGUAGE_H_
#include<Windows.h>
#include <sstream>
#include "Utility.h"
#include "InIHelper.h"
#include "CommonDefine.h"
#include "UserConfigure.h"
#include "UICommonDefine.h"

#ifndef PATH_DELIMITER
	#define PATH_DELIMITER L"\\"
#endif

#ifndef PATH_DELIMITER_CON
	#define PATH_DELIMITER_CON L"/"
#endif

#ifndef MAX_MSG_LENGTH
	#define  MAX_MSG_LENGTH 1024
#endif

#ifndef INVALID_LANG_ID
#define INVALID_LANG_ID (0)
#endif

#define  LANGUAGE_DEFAULT_TEXT_KEY (L"_text")
#define  LANGUAGE_DEFAULT_TOOLTIP_KEY (L"_tooltip")

#define DEFAULT_CNCONFIG_NAME (L"Language\\Chinese.ini")
#define DEFAULT_ENCONFIG_NAME (L"Language\\English.ini")
#define DEFAULT_MSGCNCONFIG_NAME (L"Language\\ChineseMsg.ini")
#define DEFAULT_MSGENCONFIG_NAME (L"Language\\EnglishMsg.ini")
#define DEFAULT_ERRORCNCONFIG_NAME (L"Language\\ChineseError.ini")
#define DEFAULT_ERRORENCONFIG_NAME (L"Language\\EnglishError.ini")

//MianFrame
#define  LANGUAGE_FRAMELANGUAGEINFO_SECTION (L"FRAMELANGUAGEINFO")
#define  LANGUAGE_SHEELLEXTENT_SECTION (L"SHEELLEXTENT")
#define  LANGUAGE_SHEELLEXTENT_UPLOADTOONEBOX_KEY (L"uploadtoonebox")
#define  LANGUAGE_SHEELLEXTENT_CREATEBACKUP_KEY (L"createbackup")
#define  LANGUAGE_SHEELLEXTENT_ENCRYPTUPLOAD_KEY (L"encryptupload")

#define  LANGUAGE_RIGHTMENU_SECTION (L"RIGHTMENU")
#define MENU_CANAELSYNC_KEY (L"menu_canelsync")
#define MENU_SETTINGSYNC_KEY (L"menu_settingsync")
#define MENU_TOTEAMSPACE_KEY (L"menu_toteamspace")
#define MENU_COPYORMOVE_KEY (L"menu_copyormove")
#define MENU_DELETE_KEY (L"menu_delete")
#define MENU_RENAME_KEY (L"menu_rename")
#define MENU_VIEWVERSION_KEY (L"menu_viewversion")
#define MENU_OPEN_KEY (L"menu_open")
#define MENU_SHARE_KEY (L"menu_share")
#define MENU_LINK_KEY (L"menu_link")
#define MENU_DOWNLOAD_KEY (L"menu_download")
#define MENU_NEWFOLDER_KEY (L"menu_newfolder")
#define MENU_CANCELSHARE_KEY (L"menu_cancelshare")
#define MENU_SAVEMYFILE_KEY (L"menu_savemyfile")
#define MENU_EXITSHARE_KEY (L"menu_exitshare")
#define MENU_FOLDEREMPOWER_KEY (L"menu_folderempower")
#define MENU_SUSPEND_KEY (L"menu_suspend")
#define MENU_RECOVER_KEY (L"menu_recover")
#define MENU_OPENCLIENT_KEY (L"menu_openclient")
#define MENU_OPENWEB_KEY (L"menu_web")
#define MENU_COLLENTLOG_KEY (L"menu_collectlog")
#define MENU_HELP_KEY (L"menu_help")
#define MENU_ABOUT_KEY (L"menu_about")
#define MENU_EXIT_KEY (L"menu_exit")
#define MENU_EDIT_KEY (L"menu_edit")
#define MENU_SHAREANDLINK_KEY (L"menu_share_link")
#define MENU_CANCELSHAREADNCANCLELINK_KEY (L"menu_cancelshare_cancellink")

#define MENU_LOOKTEAM_KEY (L"menu_lookteam")
#define MENU_DISBANDTEAM_KEY (L"menu_disbandteam")
#define MENU_EXITTEAM_KEY (L"menu_exitteam")
#define MENU_MODIFYADMIN_KEY (L"menu_modifyadmin")
#define MENU_MANAGEMEBMBER_KEY (L"menu_managemember")
#define MENU_VIEWMEMBER_KEY (L"menu_viewmember")
#define MENU_OPENTEAM_KEY (L"menu_openteamspace")

//NoticeMsg
#define  MSG_TITLE_SECTION (L"MSGTITLE")
#define  MSG_DEFAULT_TITLE_KEY (L"msg_default_tile")
#define	MSG_LOGIN_TITLE_KEY (L"msg_login_tile")
#define	MSG_DELCONFIRM_TITLE_KEY (L"msg_delConfirm_tile")
#define	MSG_MOVECONFIRM_TITLE_KEY (L"msg_moveConfirm_tile")
#define	MSG_SYNCSTTINGS_TITLE_KEY (L"msg_syncsettings_tile")
#define	MSG_SETTING_EXITTITLE_KEY (L"msg_setting_exittile")
#define	MSG_SETTING_SAVETITLE_KEY (L"msg_setting_savetile")
#define	MSG_SETTING_SHARETILE_KEY (L"msg_setting_sharetil")
#define	MSG_SETTING_SYNCDIR_TITLE (L"msg_setting_syncdir_title")
#define	MSG_SETTING_CONFIRM_KEY (L"msg_setting_confirm")
#define	MSG_WARNING_KEY (L"msg_warning_title")
#define	MSG_FAILURE_KEY (L"msg_failure_title")
#define	MSG_SUCESS_KEY (L"msg_success_title")
#define	MSG_MESSAGE_KEY (L"msg_message_title")
#define	MSG_SETTING_CONFIRM_CONTEXT (L"msg_setting_confirm_context")
#define	MSG_BACKUP_TIP_TITLE_KEY (L"msg_backup_tiptitle")
#define	MSG_BACKUP_TITLE_KEY (L"msg_backup_title")
#define MSG_NETWORK_TITLE (L"network_tile")
#define MSG_NETWORK_WIRELESS_TITLE (L"msg_network_wireless_tile")
#define MSG_POWER_TITLE (L"msg_power_tile")
#define MSG_SHARE_SYSTEM_UPDATE_TITLE (L"msg_share_system_update_title")

#define MSG_DESC_SECTION (L"MSGDESC")
#define MSG_SYSTEM_UPDATING (L"msg_system_updating")
#define MSG_LOGIN_NOTICEINPUT_KEY (L"msg_login_noticeInput")
#define MSG_LOGIN_FAILED_KEY (L"msg_login_failed")
#define MSG_LOGIN_LOCKED_KEY (L"msg_login_locked")
#define MSG_LOGIN_CHANGEPWD_KEY (L"msg_login_changepwd")
#define MSG_LOGIN_COULDNTCONNECT__KEY (L"msg_login_couldntconnect")
#define	MSG_DELCONFIRM_NOTICE_KEY (L"msg_delConfirm_notice")
#define	MSG_MOVECONFIRM_NOTICE_KEY (L"msg_moveConfirm_notice")
#define	MSG_SYNCSETTINGS_CANCEL_KEY (L"msg_syncsettings_cancel")
#define	MSG_SYNCSETTINGS_SETTING_KEY (L"msg_syncsettings_setting")
#define	MSG_CANCLSHARECONFIRM_NOTICE_KEY (L"msg_canclShareConfirm_Notice")
#define	MSG_CANCLSHARE2MECONFIRM_NOTICE_KEY (L"msg_canclShare2meConfirm_Notice")
#define MSG_COPY_FINISHED_SUCCESSFUL_KEY (L"msg_copy_finished_successful")
#define MSG_COPY_FINISHED_FAILED_KEY (L"msg_copy_finished_failed")
#define MSG_COPY_FINISHED_KEY (L"msg_copy_finished")
#define MSG_MOVE_FINISHED_SUCCESSFUL_KEY (L"msg_move_finished_successful")
#define MSG_MOVE_FINISHED_FAILED_KEY (L"msg_move_finished_failed")
#define MSG_MOVE_FINISHED_KEY (L"msg_move_finished")
#define MSG_SAVEMYFILE_FINISHED_SUCCESSFUL_KEY (L"msg_savemyfile_finished_successful")
#define MSG_SAVEMYFILE_FINISHED_FAILED_KEY (L"msg_savemyfile_finished_failed")
#define MSG_SAVEMYFILE_FINISHED_KEY (L"msg_savemyfile_finished")

#define MSG_RESTTASK_START_KEY (L"msg_resttask_start")
#define MSG_RESTTASK_START_DOING (L"msg_resttask_doing")
#define MSG_RESTTASK_COMPLETE_KEY (L"msg_resttask_complete")
#define MSG_RESTTASK_ERROR_KEY (L"msg_resttask_error")

#define	MSG_COLLECTLOGS_KEY (L"msg_collect_logs")
#define	MSG_COLLECTLOGS_SUCCESSFUL_KEY (L"msg_collect_logs_successful")
#define	MSG_COLLECTLOGS_FAILED_KEY (L"msg_collect_logs_failed")
#define MSG_TRANSTEAMSPACE_FINISHED_SUCCESSFUL_KEY (L"msg_transteamspace_finished_successful")
#define MSG_TRANSTEAMSPACE_FINISHED_FAILED_KEY (L"msg_transteamspace_finished_failed")
#define MSG_TRANSTEAMSPACE_FINISHED_KEY (L"msg_transteamspace_finished")
#define	MSG_EMPTYDIR_KEY (L"msg_emptydir")
#define	MSG_UPLOAD_SUCCESSFUL_KEY (L"msg_upload_successful")
#define	MSG_UPLOAD_FAILED_KEY (L"msg_upload_failed")
#define MSG_UPLOAD_UPLOAD_SELECTDIR_FAILED (L"msg_upload_selectdir_failed")
#define	MSG_DOWNLOAD_SUCCESSFUL_KEY (L"msg_download_successful")
#define	MSG_DOWNLOAD_FAILED_KEY (L"msg_download_failed")
#define	MSG_CREATEDIR_SUCCESSFUL_KEY (L"msg_createdir_successful")
#define	MSG_CREATEDIR_FAILED_KEY (L"msg_createdir_failed")
#define	MSG_CREATEDIR_FAILED_EX_KEY (L"msg_createdir_failed_ex")
#define	MSG_RENAME_SUCCESSFUL_KEY (L"msg_rename_successful")
#define	MSG_RENAME_FAILED_KEY (L"msg_rename_failed")
#define	MSG_RENAME_FAILED_EX_KEY (L"msg_rename_failed_ex")
#define	MSG_CANCELSYNC_SUCCESSFUL_KEY (L"msg_cancelsync_successful")
#define	MSG_CANCELSYNC_FAILED_KEY (L"msg_cancelsync_failed")
#define	MSG_SETSYNC_SUCCESSFUL_KEY (L"msg_setsync_successful")
#define	MSG_SETSYNC_FAILED_KEY (L"msg_setsync_failed")
#define	MSG_SELECTOBJECT_KEY (L"msg_selectobject")
#define	MSG_NOTDEST_TEAMSPACE_KEY (L"msg_notdest_teamspace")
#define	MSG_SELECTMULTI_KEY (L"msg_selectmulti")
#define	MSG_DRAPFILE_SETTING_KEY (L"msg_dropFile_setting")
#define	MSG_NO_PERMISSIONS_KEY (L"msg_no_permissions")
#define	MSG_TEAMSPACE_VIEWFAILED_KEY (L"msg_teamSpace_viewfailed")
#define	MSG_TEAMSPACE_CANNOTVIEW_KEY (L"msg_teamSpace_cannotview")
#define	MSG_TEAMSPACE_NAME_SETTING_KEY (L"msg_teamSpace_name_setting")
#define	MSG_TEAMSPACE_DESCRIPTION_SETTING_KEY (L"msg_teamSpace_description_setting")
#define	MSG_TEAMSPACE_MANAGER_SETTING_KEY (L"msg_teamSpace_manager_setting")
#define MSG_TEAMSPACE_MANAGER_ADDUSER_SETTING_KEY (L"msg_teamSpace_manager_adduser_setting")
#define MSG_TEAMSPACE_MANAGER_SUCCESS_SETTING_KEY (L"msg_teamSpace_manager_success_setting")
#define MSG_TEAMSPACE_MANAGER_FAIL_SETTING_KEY (L"msg_teamSpace_manager_fail_setting")
#define MSG_TEAMSPACE_DISBAND_SETTING_KEY (L"msg_teamSpace_disband_setting")
#define MSG_TEAMSPACE_MODIFY_SETTING_KEY (L"msg_teamSpace_modify_setting")
#define	MSG_TEAMSPACE_BASENAME_KEY (L"teamSpace_base_name")
#define	MSG_SHARE2ME_SEARCHDEFAULT_TEXT_KEY (L"share2Me_searchDefault_text")
#define	MSG_SHARE2ME_BASE_NAME_KEY (L"share2Me_base_name")
#define	MSG_SHARE2ME_SHOWBTN_START_TEXT_KEY (L"share2Me_showbtn_start_text")
#define	MSG_SHARE2ME_SHOWBTN_END_TEXT_KEY (L"share2Me_showbtn_end_text")
#define	MSG_SHARE2ME_SHOWBTN_START_KEYWORD_KEY (L"share2Me_showbtn_start_keyword")
#define	MSG_SHARE2ME_SHOWBTN_END_KEYWORD_KEY (L"share2Me_showbtn_end_keyword")
#define	MSG_SHARE2ME_SHOWBTNNAME_START_TEXT_KEY (L"share2Me_showbtnname_start_text")
#define	MSG_SHARE2ME_SHOWBTNNAME_END_TEXT_KEY (L"share2Me_showbtnname_end_text")
#define	MSG_MYSHARE_SEARCHDEFAULT_TEXT_KEY (L"myShare_searchDefault_text")
#define	MSG_MYSHARE_BASE_NAME_KEY (L"myShare_base_name")
#define	MSG_MYSHARE_SHARETYPE_SHARE_KEY (L"myShare_sharetype_share")
#define	MSG_MYSHARE_SHARETYPE_LINK_KEY (L"myShare_sharetype_link")

#define MSG_DOWNLOAD_CHANGE_TEXT_KEY (L"msg_download_change_text_key")

#define	MSG_MYFILE_TOTAL_START (L"myFile_base_total_start")
#define	MSG_MYFILE_TOTAL_END (L"myFile_base_total_end")
#define	MSG_MYFILE_VERSION_START (L"myFile_version_start_text")
#define	MSG_MYFILE_VERSION_END (L"myFile_version_start_end")

#define	MSG_MYSHARE_SHOWBTN_START_TEXT_KEY (L"myShare_showbtn_start_text")
#define	MSG_MYSHARE_SHOWBTN_END_TEXT_KEY (L"myShare_showbtn_end_text")

#define	MSG_MYFILE_BASENAME_KEY (L"myFile_base_name")
#define MSG_SHARE_NOTFOUND_KEY (L"msg_share_not_found")
#define MSG_SHARE_BATCH_ADD_KEY (L"msg_share_batch_add")
#define MSG_SHARE_NOTSHARETOMYSELF_KEY (L"msg_share_not_share_myself")
#define MSG_SHARE_SHAREFAILED_KEY (L"msg_share_share_failed")
#define MSG_SHARE_HASSHARED_KEY (L"msg_share_has_shared")
#define MSG_SHARE_GETTIMEZONEFAILED_KEY (L"msg_share_get_timezone_failed")
#define MSG_SHARE_SETTIMEERROR_A_KEY (L"msg_share_set_time_error_a")
#define MSG_SHARE_SETTIMEERROR_B_KEY (L"msg_share_set_time_error_b")
#define MSG_SHARE_SETTIMEERROR_C_KEY (L"msg_share_set_time_error_c")
#define MSG_SHARE_DELETEFAILED_KEY (L"msg_share_delete_failed")
#define MSG_SHARE_COPYSUCCESS_KEY (L"msg_share_copy_success")
#define MSG_SHARE_ALLCANCELASK_KEY (L"msg_share_all_cancel_ask")
#define MSG_SHARE_CANCELASK_KEY (L"msg_share_cancel_ask")
#define MSG_SHARE_DELETE_ALLSHARER_TITLE_KEY (L"msg_share_delete_allsharer_title")
#define MSG_SHARE_DELETE_ALLSHARER_KEY (L"msg_share_delete_allsharer")
#define MSG_SHARE_SHARETOOMUCH_KEY (L"msg_share_share_too_much")
#define MSG_SHARE_NOTSETVALIDUSER_KEY (L"msg_share_not_set_valid_user")
#define MSG_SHARE_SETERRORTEXT_KEY (L"msg_share_set_error_text")
#define MSG_SHARE_SELECTAUTHORITYSHARELINK_KEY (L"msg_share_select_authority_of_sharelink_user")
#define MSG_SHARE_CODENOTNULL_KEY (L"msg_share_code_not_null")
#define MSG_SHARE_MAILISINVALID_KEY (L"msg_share_mail_is_invalid")
#define MSG_SHARE_MAILONLYONEEMAILADDRESS_KEY (L"msg_share_mail_only_one_email_address")
#define MSG_SHARE_DATEISINVALID_KEY (L"msg_share_date_is_invalid")
#define MSG_SHARE_ADDFULLBACKUPSUCCESSED_KEY (L"msg_add_full_backup_successed")
#define MSG_SHARE_ADDFULLBACKUPFAILED_KEY (L"msg_add_full_backup_failed")

#define	MSG_TEAMSPACE_EMPTY_NAME_SETTING_KEY (L"msg_teamSpace_empty_name_setting")
#define	MSG_TEAMSPACE_NAME_FAIL_KEY (L"msg_teamSpace_name_fail_setting")
#define	MSG_TEAMSPACE_ADD_MANAGER_FAIL_SETTING_KEY (L"msg_teamSpace_add_manager_all_fail_setting")
#define	MSG_TEAMSPACE_ADD_MANAGER_PART_FAIL_SETTING_KEY (L"msg_teamSpace_add_manager_part_fail_setting")

#define MSG_TRANSTASK_ABNORMALINFO_KEY (L"msg_transTask_abnormal_info")
#define MSG_TRANSTASK_ADDTASKFAILED_KEY (L"msg_transTask_addtask_failed")
#define MSG_TRANSTASK_HASSAMEFOLDER_KEY (L"msg_transTask_has_same_folder")
#define MSG_SETTING_EXIT_KEY (L"msg_setting_exit")
#define MSG_SETTING_SAVE_KEY (L"msg_setting_save")
#define MSG_SETTING_CHANGE_PAGE_ISSAVE_KEY (L"msg_setting_change_page_issave")
#define MSG_DRAGFILE_TITLE_KEY (L"msg_dragFile_setting_title")
#define MSG_DRAGFILE_TEXT_KEY (L"msg_dragFile_setting_text")


#define MSG_FOLDERFILE_NEXIT_KEY (L"NoSuchSource")
#define MSG_TRANSERROR_NOEXIT_KEY (L"msg_tanserror_noexit")

#define MSG_DESTFOLDER_NOEXIST_KEY (L"NoSuchDest")

#define	MSG_BACKUP_SETTING_KEY (L"msg_backup_setting")
#define	MSG_BACKUP_SKIP_KEY (L"msg_backup_skip_failed")
#define	MSG_BACKUP_COUNT_KEY (L"msg_backup_create_failed")
#define	MSG_BACKUP_LOCALPATH_INVALID (L"msg_backup_localpath_invalid")
#define	MSG_BACKUP_REMOTEPATH_INVALID (L"msg_backup_remotepath_invalid")
#define	MSG_BACKUP_DES_KEY (L"msg_backup_des")
#define	MSG_BACKUP_SYNPATH_DES_KEY	(L"msg_synpath_des")
#define	MSG_BACKUP_DELCONFIRM_NOTICE_KEY (L"msg_backup_delConfirm_notice")

#define	MSG_VERSIONFILE_DELCONFIRM_NOTICE_KEY (L"msg_vesionfile_delConfirm_notice")
#define	MSG_SAVEFILE_COPYMOVE_TITLE_KEY (L"msg_savefile_copymove_title_key")
#define	MSG_SAVEFILE_COPYMOVE_TEXT_KEY (L"msg_savefile_copymove_text_key")
#define	MSG_SAVEFILE_COPYMOVE_FAIL_KEY (L"msg_savefile_copymove_fail_key")
#define MSG_DRAGFILE_TEXT_TIP_KEY (L"msg_dragFile_tip_setting_text")
#define MSG_DELMSGCONFIRM_NOTICE_KEY (L"msg_delMsgConfirm_notice")
#define MSG_SHARE2ME_OPERATION_FAILED_KEY (L"msg_share2me_operation_failed")

#define MSG_SEARCH_LIMIT_INFO_KEY (L"msg_search_limit_info")
#define MSG_OFFICE_SAVEFILE_KEY (L"msg_office_savefile")
#define MSG_ERROR_KEY (L"msg_error")
#define MSG_ERROR_NO_ADVICE_KEY (L"msg_error_no_advice")

#define MSG_SETSYNCDIR_MORETHAN100_KEY (L"msg_setsyncdir_morethan100")
#define MSG_SETSYNCDIR_INVALIDPATH_KEY (L"msg_setsyncdir_invalidpath")
#define MSG_SETSYNCDIR_INVALIDCHAR_KEY (L"msg_setsyncdir_invalidchar")
#define MSG_SETSYNCDIR_CONTAINSPECCHAR_KEY (L"msg_setsyncdir_containspecchar")
#define MSG_SETSYNCDIR_LESSTHAN500M_KEY (L"msg_setsyncdir_lessthan500M")
#define MSG_SETSYNCDIR_ISBACKUPDIR_KEY (L"msg_setsyncdir_isbackupdir")
#define MSG_SETSYNCDIR_NOTNTFS_KEY (L"msg_setsyncdir_notntfs")
#define MSG_SETSYNCDIR_ISMOUNTDIR_KEY (L"msg_setsyncdir_ismountdir")
#define MSG_NOTCDIR_IS_CREATE (L"msg_notcdir_is_create")
#define MSG_DOWLOADECDIR_CREATE_FAILED (L"msg_dowloadecdir_create_failed")

#define MSG_FULLBACKUP_CLOSE_TIPS (L"msg_fullbackup_closetips")
#define MSG_FULLBACKUP_TREENODEXPAND_TIPS (L"msg_fullbackup_treenodeexpand")

#define LANGUAGE_COMMON_SECTION (L"COMMON")
#define	BASEINFO_VERSION_KEY (L"baseInfo_version")

#define MSGFRAME_SEARCHCONTENT_KEY				L"msgFrame_searchcontent"
#define MSGFRAME_SHARE_KEY						L"msgFrame_share"
#define MSGFRAME_TEAM_KEY						L"msgFrame_team"
#define MSGFRAME_GROUP_KEY						L"msgFrame_group"
#define	MSGFRAME_SYSTEM_MSG_KEY				    L"msgframe_system_msg"

#define BACKUPERROR_CREATE_KEY					L"backUpError_Create"
#define BACKUPERROR_UPLOAD_KEY					L"backUpError_Upload"
#define BACKUPERROR_RENAME_KEY					L"backUpError_Rename"
#define BACKUPERROR_MOVE_KEY					L"backUpError_Move"
#define BACKUPERROR_CAUSE_KEY					L"backUpError_Cause"
#define BACKUPERROR_PROPOSE_KEY					L"backUpError_Propose"
#define BACKUPERROR_VALUE_KEY		            L"backUpError_Value"
#define BACKUPERROR_TEXT_KEY					L"backUpError_Text"


#define BACKUP_BUTTON_STATE_WAIT_TEXT			L"backup_button_state_wait_text"
#define BACKUP_BUTTON_STATE_WAIT_TOOLTIP		L"backup_button_state_wait_tooltip"
#define BACKUP_BUTTON_STATE_QUEUE_TEXT			L"backup_button_state_queue_text"
#define BACKUP_BUTTON_STATE_QUEUE_TOOLTIP		L"backup_button_state_queue_tooltip"
#define BACKUP_BUTTON_STATE_BACKUP_TEXT			L"backup_button_state_backup_text"
#define BACKUP_BUTTON_STATE_BACKUP_TOOLTIP		L"backup_button_state_backup_tooltip"
#define BACKUP_BUTTON_STATE_PAUSE_TEXT			L"backup_button_state_pause_text"
#define BACKUP_BUTTON_STATE_PAUSE_TOOLTIP		L"backup_button_state_pause_tooltip"
#define BACKUP_BUTTON_STATE_COMPLETE_TEXT		L"backup_button_state_complete_text"
#define BACKUP_BUTTON_STATE_COMPLETE_TOOLTIP	L"backup_button_state_complete_tooltip"
#define BACKUP_BUTTON_STATE_FAILED_TEXT			L"backup_button_state_failed_text"
#define BACKUP_BUTTON_STATE_FAILED_TOOLTIP		L"backup_button_state_failed_tooltip"
#define BACKUP_BUTTON_STATE_STOP_TEXT			L"backup_button_state_stop_text"
#define BACKUP_BUTTON_STATE_WITHOUT_TEXT		L"backup_button_state_without_text"
#define BACKUP_FAILED_PATH_INVALIDATION			L"backup_failed_path_invalidation"
#define BACKUP_FAILED_MORETHAN_NINETYNINE		L"backup_failed_morethan_ninetynine"
#define BACKUP_FAILED_COUNT_START_TEXT			L"backup_failed_count_start_text"
#define BACKUP_FAILED_COUNT_END_TEXT			L"backup_failed_count_end_text"
#define BACKUP_STATE_PAUSE_DESCRIPTION			L"backup_state_pause_description"
#define BACKUP_STATE_PAUSE_DESCRIPTION_TOOLTIP	L"backup_state_pause_description_tooltip"
#define BACKUP_STATE_DESCRIPTION_WAIT			L"backup_state_description_wait"
#define BACKUP_STATE_DESCRIPTION_QUEUE			L"backup_state_description_wait"
#define BACKUP_STATE_DESCRIPTION_QUEUE_TOOLTIP	L"backup_state_description_queue_tooltip"
#define BACKUP_PAUSE_DESCRIPTION				L"backup_pause_description"
#define BACKUP_FAILED_COUNT						L"backup_failed_count"
#define BACKUP_CREATETASK_SELECT_LOCALPATH		L"backup_createtask_select_localpath"
#define BACKUP_CREATETASK_SELECT_CLOUDPATH		L"backup_createtask_select_cloudpath"

#define FULLBACKUP_COUNT_DESCRIPTION_FIRST		L"fullbackup_count_description_first"
#define FULLBACKUP_COUNT_DESCRIPTION_SECOND		L"fullbackup_count_description_second"
#define FULLBACKUP_COUNT_DESCRIPTION_THIRD		L"fullbackup_count_description_third"
#define FULLBACKUP_COUNT_DESCRIPTION_FOURTH		L"fullbackup_count_description_fourth"
#define FULLBACKUP_TIME_DESCRIPTION				L"fullbackup_time_description"
#define FULLBACKUP_BACKUPING_DESCRIPTION		L"fullbackup_backuping_description"
#define FULLBACKUP_BACKUPED_DESCRIPTION			L"fullbackup_backuped_description"
#define FULLBACKUP_TIMED_DESCRIPTION			L"fullbackup_timed_description"
#define FULLBACKUP_TIMED_DESCRIPTION_SECOND		L"fullbackup_timed_description_second"
#define FULLBACKUP_FAILED_DESCRIPTION_START		L"fullbackup_failed_description_start"
#define FULLBACKUP_FAILED_DESCRIPTION_END		L"fullbackup_failed_description_end"
#define FULLBACKUP_FAILED_COUNT_DESCRIPTION		L"fullbackup_failed_count_description"
#define FULLBACKUP_OFFLINE_DESCRIPTION			L"fullbackup_offline_description"
#define FULLBACKUP_TIME_DESCRIPTION_ONESECOND	L"fullbackup_time_description_onesecond"
#define FULLBACKUP_TIME_DESCRIPTION_MINUTE		L"fullbackup_time_description_minute"
#define FULLBACKUP_TIME_DESCRIPTION_SECOND		L"fullbackup_time_description_second"
#define FULLBACKUP_TIME_DESCRIPTION_HOUR		L"fullbackup_time_description_hour"
#define FULLBACKUP_TIME_DESCRIPTION_DAY			L"fullbackup_time_description_day"
#define FULLBACKUP_TIME_DESCRIPTION_MOREDAY		L"fullbackup_time_decription_moreday"
#define FULLBACKUP_FOLDER_NAME_DESCRIPTION		L"common_file_dialog_default_myfile_name"
#define FULLBACKUP_DIALOG_SETTASK_OK			L"fullbackup_dialog_settask_ok"
#define FULLBACKUP_DIALOG_SETTASK_FINISH		L"fullbackup_dialog_settask_finish"
#define FULLBACKUP_DIALOG_SETTASK_SELECT_LOCAL	L"fullbackup_dialog_settask_select_local"
#define FULLBACKUP_DIALOG_SETTASK_SELECT_CLOUDY	L"fullbackup_dialog_settask_select_cloudy"

#define MYSPACE_BACKUP_FOLDER_NAME				L"myspace_backup_folder_name"

#define TRANSERROR_UPLOAD_KEY					L"transError_Upload"
#define TRANSERROR_DOWNLOAD_KEY					L"transError_Download"
#define TRANSERROR_NOT_EXIT					L"transError_no_Exit"
#define TRANSERROR_TEXT_KEY					L"transError_Text"

#define	BACKUP_SETTASK_WEEK_KEY				L"backup_setTask_taskCycleMid_week_filed"
#define	BACKUP_SETTASK_MONDAY_KEY			L"backup_setTask_monday"
#define	BACKUP_SETTASK_TUESDAY_KEY			L"backup_setTask_tuesday"
#define	BACKUP_SETTASK_WEDNESDAY_KEY		L"backup_setTask_wednesday"
#define	BACKUP_SETTASK_THURSDAY_KEY			L"backup_setTask_thursday"
#define	BACKUP_SETTASK_FRIDAY_KEY			L"backup_setTask_friday"
#define	BACKUP_SETTASK_SATURDAY_KEY			L"backup_setTask_saturday"
#define	BACKUP_SETTASK_SUNDAY_KEY			L"backup_setTask_sunday"

#define	MSG_TEAMSPACE_MEMBER_MANAGER_KEY	(L"teamSpace_member_manager_name")
#define	MSG_TEAMSPACE_VIEW_MEMBER_KEY		(L"teamSpace_view_member_name")
#define	BACKUPCHECK_DESC_START_KEY			L"BackUpCheck_desc_start"
#define	BACKUPCHECK_DESC_END_KEY			L"BackUpCheck_desc_end"
#define	BACKUPCHECK_SETTASK_WEEK_KEY		L"backup_setTask_taskCycleMid_week"
#define	BACKUPCHECK_SETTASK_MONTH_KEY		L"backup_setTask_taskCycleMid_month"
#define	BACKUPCHECK_SETTASK_DAY_KEY			L"backup_setTask_taskCycleMid_day"
#define	BACKUPCHECK_SETTASK_MON_KEY			L"backup_setTask_taskCycleMid_mon"
#define	BACKUPCHECK_SETTASK_TUE_KEY			L"backup_setTask_taskCycleMid_tue"
#define	BACKUPCHECK_SETTASK_WED_KEY			L"backup_setTask_taskCycleMid_wed"
#define	BACKUPCHECK_SETTASK_THU_KEY			L"backup_setTask_taskCycleMid_thu"
#define	BACKUPCHECK_SETTASK_FRI_KEY			L"backup_setTask_taskCycleMid_fri"
#define	BACKUPCHECK_SETTASK_SAT_KEY			L"backup_setTask_taskCycleMid_sat"
#define	BACKUPCHECK_SETTASK_SUN_KEY			L"backup_setTask_taskCycleMid_sun"
#define	BACKUPCHECK_SETTASK_LOCALFILE_KEY	  L"backup_setTask_localfile"
#define	BACKUPCHECK_FILEMOVEORDROP_DESC_STRAT_KEY			L"FileMoveOrDrop_desc_start"
#define	BACKUPCHECK_FILEMOVEORDROP_DESC_MID_KEY				L"FileMoveOrDrop_desc_mid"
#define BACKUPCHECK_FILEMOVEORDROP_DESC_END_KEY				L"FileMoveOrDrop_desc_start_end"
#define	COMMENT_WAITFOR_KEY			L"comment_waitfor"
#define	COMMENT_UPLOADING_KEY		L"comment_uploading"
#define	COMMENT_PAUSE_KEY			L"comment_pause"
#define	COMMENT_COMPLED_KEY			L"comment_compled"
#define	COMMENT_COPYTO_KEY			L"comment_copyto"
#define	COMMENT_MOVETO_KEY			L"comment_moveto"
#define COMMENT_COPYTOCUR_KEY			L"comment_copytocur"
#define	COMMENT_CREATENEWDIR_KEY			L"comment_createnewdir"
#define	COMMENT_CANCELSYN_KEY			L"comment_cancelsyn"
#define	COMMENT_SETSYN_KEY				L"comment_setsyn"
#define	COMMENT_SELECTOBJ_KEY			L"comment_selectobj"
#define	COMMENT_INPUTCODE_KEY			L"comment_inputcode"
#define	COMMENT_INPUTEMAIL_KEY			L"comment_inputemail"
#define	TEAMSPACE_MEMNUM_KEY			L"teamSpace_memNum"
#define	NAMEDESCRIPTION_KEY				L"nameDescription"
#define	DESCRIPTIONDESCRIPTIONA_KEY			L"descriptionDescription_a"
#define	DESCRIPTIONDESCRIPTIONB_KEY			L"descriptionDescription_b"
#define	DISTANDTEAMSPACE_DESC_START_KEY			L"distandTeamSpace_desc_start" 
#define	DESTANDTEAMSPACE_DESC_END_KEY			L"distandTeamSpace_desc_end"
#define	LOOKTEAMSPACE_NUMDESCRIPTION_KEY			L"lookTeamSpace_numDescription"
#define	TEAMSPACE_AUTHER_KEY			L"teamSpace_auther"
#define	TEAMSPACE_MANAGER_KEY			L"teamSpace_manager"
#define	TEAMSPACE_EDITOR_KEY			L"teamSpace_editor"
#define	TEAMSPACE_VIEWER_KEY			L"teamSpace_viewer"
#define	TEAMSPACE_LISTER_KEY			L"teamSpace_lister"
#define	TEAMSPACE_PREVIEWER_KEY			L"teamSpace_previewer"
#define	TEAMSPACE_UPLOADER_KEY			L"teamSpace_uploader"
#define	TEAMSPACE_UPLOADERANDVIEWER_KEY			L"teamSpace_uploaderandviewer"
#define	COMMENT_ADDMEMBER_START_KEY			L"comment_addmember_start"
#define	COMMENT_ADDMEMBER_END_KEY			L"comment_addmember_end"
#define	COMMENT_POWER_AUTHER_KEY			L"comment_power_auther"
#define	COMMENT_POWER_EDITOR_KEY			L"comment_power_editor"
#define	COMMENT_POWER_VIEWER_KEY			L"comment_power_viewer"
#define	COMMENT_STARTUPLOAD_KEY				L"comment_startupload"
#define	COMMENT_STARTDOWNLOAD_KEY			L"comment_startdownload"
#define	COMMENT_PAUSEUPLOAD_KEY				L"comment_pauseupload"
#define	COMMENT_PAUSEDOWNLOAD_KEY			L"comment_pausedownload"
#define	COMMENT_UPLOAD_KEY					L"comment_upload"
#define	COMMENT_DOWNLOAD_KEY				L"comment_download"
#define	COMMENT_GETMORE_KEY				    L"comment_getmore"
#define	COMMENT_COPYMOVE_KEY				L"comment_copymove"
#define	COMMENT_SAVETOONEBOX_KEY			L"comment_savetoonebox"
#define	COMMENT_SAVE_KEY					L"comment_save"
#define	COMMENT_SAVETOTEAMSPACE_KEY			L"comment_savetoteamspace"
#define	COMMENT_SELECTCURDIR_KEY			L"comment_selectcurdir"
#define	COMMENT_OK_KEY						L"comment_ok"
#define	COMMENT_SELECTCLOUD_KEY				L"comment_selectcloud"
#define	COMMENT_UPLOADTOONEBOX_KEY			L"comment_uploadtoonebox"
#define	COMMENT_MYCOMPUTER_KEY				L"comment_mycomputer"
#define	COMMENT_DESKTOP_KEY					L"comment_desktop"
#define	COMMENT_INPUTUSERID_KEY				L"comment_inputuserid"
#define	COMMENT_INPUTUSERID_TEAMSPACE_KEY	L"comment_inputuserid_teamspace"
#define	COMMENT_ADDMEMBER_TEAMSPACE_KEY		L"comment_addmember_teamspace"
#define  LANGUAGE_COMMON_UPLOADTO_KEY (L"uploadto")
#define	OFFICEADDIN_ITEM_SUCCESS_TXT		(L"OfficeAddin_item_success_text")
#define	OFFICEADDIN_ITEM_FILE_TXT			(L"OfficeAddin_item_fail_text")
#define	COMMENT_CANCEL_KEY			(L"btn_cancel_text")
#define	COMMENT_COMMONUSER_KEY			(L"comment_commonuser")
#define	COMMOM_UPLOADTOONEBOX_KEY			(L"common_uploadtoonebox")
#define	COMMOM_TOOLTIP_MAXCHAR_KEY				(L"common_tooltip_maxchar")
#define	COMMOM_TOOLTIP_CHARACTER_KEY			(L"common_tooltip_character")
#define	COMMOM_LOCAL_SYNCDIR_KEY	 (L"common_local_syncdir")
#define	COMMOM_SYNPATH_DES_KEY			(L"common_synpath_des")

#define LANGUAGE_WINDOWNAME_SECTION (L"WINDOWNAME")
#define WINDOWNAME_LOGIN_KEY (L"windowname_login_key")
#define NOTICEMSGNAME_LOGIN_KEY (L"windowname_noticemsg_key")
#define WINDOWNAME_UPLOAD_KEY (L"windowname_upload_key")
#define WINDOWNAME_SHARE_KEY (L"windowname_share_key")
#define WINDOWNAME_UPGRADE_KEY (L"windowname_upgrade_key")

#define SHAREFRAME_SHARELINKTEXTONE_TEXT	L"shareFrame_shareLinkTextOne_text"
#define SHAREFRAME_SHARELINKTEXTTWO_TEXT	L"shareFrame_shareLinkTextTwo_text"
#define SHAREFRAME_SHARELINKTEXTTHREE_TEXT	L"shareFrame_shareLinkTextThree_text"
#define SHAREFRAME_SHARELINKTEXTDATETIP_TEXT	L"shareFrame_shareLinkTextDateTip_text"
#define SHAREFRAME_SHARELINKTEXTDATETIP2_TEXT	L"shareFrame_shareLinkTextDateTip2_text"
#define SHAREFRAME_SHARELINKTEXTACCESSUPLOAD_TEXT	L"shareFrame_shareLinkTextAccessUpload_text"
#define SHAREFRAME_SHARELINKTEXTACCESSDOWNLOAD_TEXT	L"shareFrame_shareLinkTextAccessDownload_text"
#define SHAREFRAME_SHARELINKTEXTACCESSPREVIEW_TEXT	L"shareFrame_shareLinkTextAccessPreview_text"
#define SHAREFRAME_SHARELINKTEXTDYMATICCODE_TEXT L"shareFrame_shareLinkTextDymaticCode_text"
#define SHAREFRAME_LINKS L"shareFrame_links"

#define TRANSTASK_STATUS_UPLOADING L"transtask_status_uploading"
#define TRANSTASK_STATUS_UPLOAD_PAUSED L"transtask_status_upload_paused"
#define TRANSTASK_STATUS_UPLOAD_WAITING L"transtask_status_upload_waiting"
#define TRANSTASK_STATUS_UPLOADED L"transtask_status_uploaded"

#define TRANSTASK_STATUS_DOWNLOADING L"transtask_status_downloading"
#define TRANSTASK_STATUS_DOWNLOAD_PAUSED L"transtask_status_download_paused"
#define TRANSTASK_STATUS_DOWNLOAD_WAITING L"transtask_status_download_waiting"
#define TRANSTASK_STATUS_DOWNLOADED L"transtask_status_downloaded"

#define TRANSTASK_STATUS_FAILED L"transtask_status_failed"

#define USER_MSG_SHARE_FOLDER L"user_msg_share_folder"
#define USER_MSG_SHARE_FILE L"user_msg_share_file"
#define USER_MSG_CANCEL_SHARE_FOLDER L"user_msg_cancel_share_folder"
#define USER_MSG_CANCEL_SHARE_FILE L"user_msg_cancel_share_file"
#define USER_MSG_JOIN_TEAMSPACE L"user_msg_join_teamspace"
#define USER_MSG_QUIT_TEAMSPACE L"user_msg_quit_teamspace"
#define USER_MSG_REMOVE_FROM_TEAMSPACE L"user_msg_remove_from_teamspace"
#define USER_MSG_TEAMSPACE_UPLOAD L"user_msg_teamspace_upload"
#define USER_MSG_TEAMSPACE_ROLE_UPDATE L"user_msg_teamspace_role_update"
#define USER_MSG_JOIN_GROUP L"user_msg_join_group"
#define USER_MSG_QUIT_GROUP L"user_msg_quit_group"
#define USER_MSG_REMOVE_FROM_GROUP L"user_msg_remove_from_group"
#define USER_MSG_GROUP_ROLE_UPDATE L"user_msg_group_role_update"
#define USER_MSG_SYSTEM_ANNOUNCEMENT L"user_msg_system_announcement"

#define ABOUT_POWERED_BY		L"label_copy_rigth_text"
#define ABOUT_PRIVACY_STATMENT	L"about_privacy_statement"
#define SENDLINK_ADD_MESSAGE	L"sendlink_add_message"
#define SENDLINK_RECIPIENT		L"sendlink_recipient"
#define LANGUAGE_MSGTITLE_SECTION (L"MSGTITLE")
#define MYSPACE_TEXT			L"myspace_text"

#define SYSTEM_INFO_NETWORK_WIRELESS_EXIST L"network_wireless_exist"
#define SYSTEM_INFO_NETWORK_WIRELESS_OFFLINE L"network_wireless_offline"
#define SYSTEM_INFO_NETWORK_WIRELESS_ONLINE L"network_wireless_online"

#define SYSTEM_INFO_NETWORK_OFFLINE L"network_offline"
#define SYSTEM_INFO_NETWORK_ONLINE L"network_online"

#define SYSTEM_INFO_POWER_OFFLINE L"power_offline"
#define SYSTEM_INFO_POWER_ONLINE L"power_online"

#define TRANSTASK_MSG_OPEN_SOURCE L"transtask_msg_open_source"

const TCHAR ShowTipOne[] = _T("file='..\\Image\\ic_top_sys_number.png' source='0,0,16,16'");
const TCHAR ShowTipTwo[] = _T("file='..\\Image\\ic_top_sys_number.png' source='0,26,23,42'");
const TCHAR ShowTipThree[] = _T("file='..\\Image\\ic_top_sys_number.png' source='0,52,30,68'");

class IniLanguageHelper
{
public:
	IniLanguageHelper(uint32_t languageID=INVALID_LANG_ID):m_fileName(L"")
	{
		if (languageID == INVALID_LANG_ID)
		{
			m_languageID = GetUserConfValue(CONF_SETTINGS_SECTION,CONF_LANGUAGE_KEY,(int32_t)UI_LANGUGE::DEFAULT);
		}
		else
		{
			m_languageID = languageID;
		}
		if (m_languageID == (uint32_t)UI_LANGUGE::DEFAULT)
		{
			m_languageID = GetSystemDefaultUILanguage();
		}
		if (m_languageID != UI_LANGUGE::CHINESE && m_languageID != UI_LANGUGE::ENGLISH)
		{
			m_languageID = UI_LANGUGE::ENGLISH;
		}
	}

	~IniLanguageHelper(){}

	uint32_t GetLanguage()
	{
		return m_languageID;
	}

	std::wstring GetLanguageFilePath()
	{
		std::wstring LanguageFilePath = GetInstallPath();
		std::wstring fileName = DEFAULT_ENCONFIG_NAME;
		if (GetLanguage()==UI_LANGUGE::CHINESE)
		{
			fileName = DEFAULT_CNCONFIG_NAME;
		}
		else
		{
			fileName = DEFAULT_ENCONFIG_NAME;
		}
		LanguageFilePath += fileName;
		return LanguageFilePath;
	}

	std::wstring GetMsgLanguageFilePath()
	{
		std::wstring msgLanguageFilePath = GetInstallPath();
		std::wstring fileName = DEFAULT_MSGENCONFIG_NAME;
		if (GetLanguage() ==UI_LANGUGE::CHINESE)
		{
			fileName = DEFAULT_MSGCNCONFIG_NAME;
		}
		else
		{
			fileName = DEFAULT_MSGENCONFIG_NAME;
		}
		msgLanguageFilePath += fileName;
		return msgLanguageFilePath;
	}

	std::wstring GetErrorLanguageFilePath( )
	{
		std::wstring fileName =  DEFAULT_ERRORENCONFIG_NAME;
		std::wstring errorLanguageFilePath = GetInstallPath();
		if (GetLanguage()==UI_LANGUGE::CHINESE)
		{
			fileName = DEFAULT_ERRORCNCONFIG_NAME;
		}
		else
		{
			fileName = DEFAULT_ERRORENCONFIG_NAME;
		}
		errorLanguageFilePath += fileName;
		return errorLanguageFilePath;
	}

	std::wstring GetCommonString(std::wstring key)
	{
		CInIHelper InIHelper(SD::Utility::FS::format_path(GetLanguageFilePath().c_str()));
		return InIHelper.GetString(LANGUAGE_COMMON_SECTION,key,L"");
	}

	std::wstring GetMsgDesc(std::wstring key, ...)
	{
		TCHAR buffer[MAX_MSG_LENGTH] = {0};
		CInIHelper InIHelper(SD::Utility::FS::format_path(GetMsgLanguageFilePath().c_str()));
		std::wstring strTmp = InIHelper.GetString(MSG_DESC_SECTION,key,L"");
		va_list args;
		va_start (args, key);
		(void)_vstprintf_s(buffer, strTmp.c_str(), args);
		va_end (args);
		return buffer;
	}

	std::wstring GetWindowName(std::wstring key )
	{
		CInIHelper InIHelper(SD::Utility::FS::format_path(GetLanguageFilePath().c_str()));
		return InIHelper.GetString(LANGUAGE_WINDOWNAME_SECTION,key,L"");
	}

	std::wstring GetFrameName(std::wstring key )
	{
		CInIHelper InIHelper(SD::Utility::FS::format_path(GetLanguageFilePath().c_str()));
		return InIHelper.GetString(LANGUAGE_FRAMELANGUAGEINFO_SECTION,key,L"");
	}

	std::wstring GetMsgTitle(std::wstring key )
	{
		CInIHelper InIHelper(SD::Utility::FS::format_path(GetMsgLanguageFilePath().c_str()));
		return InIHelper.GetString(LANGUAGE_MSGTITLE_SECTION,key,L"");
	}

	std::wstring GetSkinFolderPath()
	{
		m_fileName = L"skin\\ch\\";
		if (m_languageID != UI_LANGUGE::CHINESE)
		{
			m_fileName = L"skin\\en\\";
		}
		return m_fileName;
	}


private:
	std::wstring m_fileName;
	uint32_t m_languageID;
};
static IniLanguageHelper iniLanguageHelper;

#endif // !_LANGUAGE_H_
