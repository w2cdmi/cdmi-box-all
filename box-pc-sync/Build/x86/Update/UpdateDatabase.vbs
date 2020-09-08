set objws=wscript.createobject("wscript.shell")
objws.run "cmd /c @sqlite3.exe remote_table.db < ../Update/UpdateDataBase/1.2.3.24.sql",0,True