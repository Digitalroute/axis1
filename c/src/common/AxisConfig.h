#if !defined(__AXIS_AXISCONFIG_H_INCLUDED__)
#define __AXIS_AXISCONFIG_H_INCLUDED__

#define CONFBUFFSIZE 200

/**
    The purpose of this class is to provide a way so that
    the server takes the configuration path where the
    axiscpp.conf lies from the environment variable AXIS_HOME.
    User has to set this variable pointing the path to axiscpp.conf file.
    This axiscpp.conf file contains all the configuration paths
    like wsdd configuration file path, axis log path etc.
*/
class AxisConfig
{
    public:
        AxisConfig();
        ~AxisConfig();
        char* GetWsddFilePath();
        char* GetAxisLogPath();
        
    private:		
        int ReadConfFile();
        char m_WsddFilePath[CONFBUFFSIZE];
        char m_AxisLogPath[CONFBUFFSIZE];
        char m_sWsddFilePath[CONFBUFFSIZE];
        char m_sAxisLogPath[CONFBUFFSIZE];
};
#endif