import sys,os,shutil,zipfile,ConfigParser,codecs,random,uuid

def AddFileToZip(zipSrc, zipDes):
    myZipFile = zipfile.ZipFile(zipDes, 'a')
    if os.path.isdir(zipSrc):
        for root, dirs, files in os.walk(zipSrc):
            if len(files) == 0:
                myZipFile.write(root + '/')
            else:
                for fileName in files:
                    filePath = os.path.join(root, fileName)
                    print 'add', zipDes, ':', filePath 
                    myZipFile.write(filePath)
    else:
        print 'add', zipDes, ':', zipSrc
        myZipFile.write(zipSrc)
    myZipFile.close()

def ModifyLocalVersion(path, version):	
    tpath=path + '.tmp'
    keyversion = 'Version='
       
    fr = codecs.open(path,'r','utf-16')
    fw = codecs.open(tpath, 'a+','utf-16')
    for line in fr:
        bDeal = False
        tmp = line
        if tmp[:len(keyversion)] == keyversion:
            tmp = tmp[:len(keyversion)] + version + '\r\n'
	   
        fw.writelines(tmp)
    fr.close()
    fw.close()
    os.remove(path)
    os.rename(tpath, path)

def ModifyUiAppVersion(path, version):	
    tpath=path + '.tmp'
    keyversion = '[assembly: AssemblyVersion'
    keyvFileersion = '[assembly: AssemblyFileVersion'
       
    fr = open(path)
    fw = open(tpath, 'a+')
    for line in fr:
        bDeal = False
        tmp = line
        if tmp[:len(keyversion)] == keyversion:
            tmp = tmp[:len(keyversion)] + '(\"' + version + '\")' + ']' + '\n'
	if tmp[:len(keyvFileersion)] == keyvFileersion:
            tmp = tmp[:len(keyvFileersion)] + '(\"' + version + '\")' + ']' + '\n'

        fw.writelines(tmp)
    fr.close()
    fw.close()
    os.remove(path)
    os.rename(tpath, path)
 

def ModifyNSISVersion(path, version):
    tpath=path + '.tmp'
    keyversion = '!define PRODUCT_VERSION'
       
    fr = open(path)
    fw = open(tpath, 'a+')
    for line in fr:
        bDeal = False
        tmp = line
        if tmp[:len(keyversion)] == keyversion:
            tmp = tmp[:len(keyversion)] + ' ' + version + '\n'
	
        fw.writelines(tmp)
    fr.close()
    fw.close()
    os.remove(path)
    os.rename(tpath, path)

def ModifyShellExtentidl(path,GUID):
     tpath=path + '.tmp'
     fr = open(path)
     fw = open(tpath, 'a+')
     bDeal = False
     for line in fr:
         tmp = line
         if 'library ShellExtentLib' in tmp:
	     bDeal=True
	 if "uuid" in tmp and bDeal:
	     tmp = '        uuid(' + GUID + ')' + '\n'
	
	 fw.writelines(tmp)
     fr.close()
     fw.close()
     os.remove(path)
     os.rename(tpath, path)

def ModifyContextMenuExtrgs(path,GUID):
     tpath=path + '.tmp'
     fr = open(path)
     fw = open(tpath, 'a+')
     for line in fr:
         tmp = line
         if 'ForceRemove {' in  tmp:
             tmp = '        ForceRemove {' + GUID + '} = s \'ContextMenuExt Class\'' +'\n'
	 if "ForceRemove OneboxMate" in tmp:
	     tmp = '                ForceRemove OneboxMate = s {' + GUID + '}' + '\n'
	
	 fw.writelines(tmp)
     fr.close()
     fw.close()
     os.remove(path)
     os.rename(tpath, path)

def CreateUpdateVersionFile(path,version,updatetype):
    fw = open(path,'w')
    Section = "[VERSION]"
    VersionKey = "Version=" + version
    UpdateTypeKey = "UpdateType=" + updatetype
    line = Section + "\r\n" + VersionKey + "\r\n" + UpdateTypeKey
    fw.writelines(line)
    fw.close()

def del_file_dir(filepath):
    if os.path.exists(filepath):
        if os.path.isdir(filepath):
            os.rmdir(filepath)
        else:
            os.remove(filepath)

def copy_file_dir(src, des):
    command = "COPY /Y " + "\"" + src.replace('/', '\\') + "\"" + " \"" + des.replace('/', '\\') + "\""
    os.system(command)
    print command

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print 'You need input version information, see useage:'
        print 'Useage :', sys.argv[0][sys.argv[0].rfind('\\')+1:], '<version> <MainVersion[option]> <UpdateType[option]> <BuildParam[option]> <BuildType[option]>'
        os.system('PAUSE')
    else:
        try:
	    version = ""
	    MainVersion = ""
            UpdateType = ""
            BuildParam = ""
            BuildType = ""
	  
            version = sys.argv[1]
	    if len(sys.argv) >= 3: 
	    	MainVersion = sys.argv[2]
	    if len(sys.argv) >= 4:
	    	UpdateType = sys.argv[3]
	    if len(sys.argv) >= 5:
	    	BuildParam = sys.argv[4]
	    if len(sys.argv) >= 6:
	    	BuildType = sys.argv[5]
	   
	    if MainVersion=="":
	    	MainVersion='V100R003C10B010'
	    if UpdateType=="":
	    	UpdateType='0'
	    if BuildParam=="":
		BuildParam='Rebuild'
	    if BuildType=="":
	    	BuildType='Release'

	    UPDATE_VERSION_NAME = 'updateVersion.ini'
            PACKAGE_NAME = 'Onebox_Mate_V'+version + '_Setup.exe'
	    PACKAGE_ZIP_NAME = 'Onebox_Mate_V'+ version + '_Setup.zip' 
	    RELEASE_ZIP_NAME = 'Onebox Solution V100R005C00_Onebox_Mate_Setup.zip'

            curpath = os.getcwd();
	    print curpath		                    
            
	    #delete oldFile
	    try:
		del_file_dir('../bin/' + PACKAGE_ZIP_NAME)
	    except Exception, e:
		print e
	    
            #modify shellext GUID
	    os.chdir(curpath)
	    GUID = str(uuid.uuid1()).upper()
            ModifyShellExtentidl('../../Onebox/Extensions/ShellExtension/ShellExtent/ShellExtent.idl', GUID)
	    os.chdir(curpath)
	    ModifyContextMenuExtrgs('../../Onebox/Extensions/ShellExtension/ShellExtent/ContextMenuExt.rgs', GUID)

            #modify version information
	    os.chdir(curpath)
            ModifyLocalVersion('../output/Config.ini', version)
            ModifyNSISVersion('OneboxSetup.nsi', version)
	    
	    #Build Project
	    os.system("build.bat %s %s" %(BuildParam,BuildType))
            os.chdir(curpath)

	    #make setup file
	    os.system('makensis.exe  OneboxSetup.nsi')
            
	    if os.path.exists('../bin')==False:
	    	os.mkdir('../bin')
	    CreateUpdateVersionFile(UPDATE_VERSION_NAME,version,UpdateType)
	    AddFileToZip(PACKAGE_NAME,PACKAGE_ZIP_NAME)
	    AddFileToZip(PACKAGE_ZIP_NAME,RELEASE_ZIP_NAME)
	    AddFileToZip(UPDATE_VERSION_NAME,RELEASE_ZIP_NAME)
	    copy_file_dir(RELEASE_ZIP_NAME,'../bin')
	    del_file_dir(PACKAGE_NAME)
	    del_file_dir(PACKAGE_ZIP_NAME)
	    del_file_dir(UPDATE_VERSION_NAME)
	    del_file_dir(RELEASE_ZIP_NAME)
             
            os.chdir(curpath)
            
        except Exception, e:
            print e
            
