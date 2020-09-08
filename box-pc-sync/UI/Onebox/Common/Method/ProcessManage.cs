using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Collections.ObjectModel;
using System.Runtime.InteropServices;
using System.Windows;
using System.Threading;

namespace Onebox.ProcessManager
{
    struct ProcessName
    {
        public const string SyncProcess = "OneboxSyncService.exe";
        public const string ShExtCmdProcess = "OneboxShExtCmd.exe";
        public const string AutoStartProcess = "OneboxStart.exe";
        public const string TerminateProcess = "TerminateProcess.exe";
    }

    public class ProcessManage
    {
        private const Int32 PROCESS_TERMINATE = 0x0001;
        private Dictionary<string,Process> ProcessCollection = null;
        private Common common = new Common();
        
        [DllImport("kernel32.dll", SetLastError = true)]

        [return: MarshalAs(UnmanagedType.Bool)]

        static extern bool TerminateProcess(IntPtr hProcess, uint uExitCode);

        [DllImport("kernel32.dll", SetLastError = true)]
        static extern IntPtr OpenProcess(int dwDesiredAccess, bool bInheritHandle, int dwProcessId);   


        public ProcessManage()
        {
            ProcessCollection = new  Dictionary<string,Process>();
        }

        public bool Start(string strWorkingDirectory, string strEXEFileName,string strArgument)
        {
            try
            {
                ProcessStartInfo StartInfo;
                StartInfo = new System.Diagnostics.ProcessStartInfo();
                StartInfo.WorkingDirectory = strWorkingDirectory;
                StartInfo.FileName = strEXEFileName;
                StartInfo.Arguments = strArgument;
                Process Pcs = System.Diagnostics.Process.Start(StartInfo);

                if (!CheckProcessExist(Pcs))
                {
                    return false;
                }

                for (int i = 0; i < ProcessCollection.Count; i++)
                {
                    if (ProcessCollection.ElementAt(i).Key == strEXEFileName)
                    {
                        ProcessCollection.Remove(ProcessCollection.ElementAt(i).Key);
                        break;
                    }
                }

               ProcessCollection.Add(strEXEFileName, Pcs);
            }
            catch(Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }

            return true;
        }

        public bool Stop(string strEXEFileName)
        {
            bool iRet = true;
            try
            {
                if (CheckProcessExist(strEXEFileName))
                {
                    ProcessStartInfo StartInfo;
                    DateTime ElapsedTime = DateTime.Now.AddSeconds(40);
                    StartInfo = new System.Diagnostics.ProcessStartInfo();
                    StartInfo.WorkingDirectory = System.Environment.CurrentDirectory;
                    StartInfo.FileName = strEXEFileName;
                    StartInfo.Arguments = "stop";
                    System.Diagnostics.Process.Start(StartInfo);
                    App.Log.Info("Stop process " + strEXEFileName);

                    while (true)
                    {
                        bool IsProcessExist = false;
                        int iElapsed = DateTime.Compare(ElapsedTime, DateTime.Now);
                        
                        if (CheckProcessExist(strEXEFileName))
                        {
                            IsProcessExist = true;
                            if (-1 == iElapsed)
                            {
                                ForceProcess(strEXEFileName);
                            }
                        }

                        if (!IsProcessExist)
                        {
                            break;
                        }

                        Thread.Sleep(1000);
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                iRet = false;
            }

            ProcessCollection.Remove(strEXEFileName);
            return iRet;
        }

        public bool StopAll()
        {
            try
            {
                int iCount = ProcessCollection.Count;

                if (iCount > 0)
                {
                    for (int i = 0; i < iCount; i++)
                    {
                        Stop(ProcessCollection.ElementAt(0).Key);
                    }
                }
                else
                {
                    if (CheckProcessExist(ProcessName.SyncProcess))
                    {
                        Stop(ProcessName.SyncProcess);
                    }
                }

                return true;
            }
            catch(Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                return false;
            }
        }

        public Process GetProcess(string strEXEFileName)
        {
            Process RetProcess = null;

            for (int i = 0; i < ProcessCollection.Count; i++)
            {
                if (ProcessCollection.ElementAt(i).Key == strEXEFileName)
                {
                    RetProcess = ProcessCollection.ElementAt(i).Value;
                    break;
                }
            }

            return RetProcess;
        }

        /// <summary>
        /// Used to check whether a process exists 
        /// </summary>
        /// <param name="inProcess">The input parameters, representation a process</param>
        /// <returns></returns>
        private  bool CheckProcessExist(System.Diagnostics.Process inProcess)
        {
            try
            {
                System.Diagnostics.Process temp = System.Diagnostics.Process.GetProcessById(inProcess.Id);
                return true;
            }
            catch
            { }

            return false;
        }

        /// <summary>
        /// Used to check whether a process exists  
        /// </summary>
        /// <param name="strProcessName">The input parameters, the executable file name</param>
        /// <returns></returns>
        public bool CheckProcessExist(string strEXEFileName)
        {
            try
            {
                string strProcessName = strEXEFileName.Substring(0, strEXEFileName.LastIndexOf('.'));
                System.Diagnostics.Process[] p = System.Diagnostics.Process.GetProcesses();

                foreach (System.Diagnostics.Process p1 in p)
                {
                    if (p1.ProcessName == strProcessName)
                    {
                        return true;
                    }
                }

                return false;
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
                return false;
            }
        }

        private void ForceProcess(string strEXEFileName)
        {
            try
            {
                string strProcessName = strEXEFileName.Substring(0, strEXEFileName.LastIndexOf('.'));
                System.Diagnostics.Process[] p = System.Diagnostics.Process.GetProcesses();

                foreach (System.Diagnostics.Process p1 in p)
                {
                    if (p1.ProcessName == strProcessName)
                    {
                        IntPtr Handle = OpenProcess(PROCESS_TERMINATE, false, p1.Id);
                        TerminateProcess(Handle, 1);
                        App.Log.Info("Force terminate Process " + strEXEFileName);
                    }
                }
            }
            catch (System.Exception ex)
            {
                App.Log.Error(ex.TargetSite + "  " + ex.Source + "  " + ex.InnerException + " " + ex.Message);
            }
        }
    }
}
